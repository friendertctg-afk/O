package com.example.audio

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.MainActivity
import com.example.R
import com.example.data.local.AppDatabase
import com.example.data.local.DownloadedAudioEntity
import com.example.data.remote.NetworkModule
import com.example.data.seed.QariCatalog
import com.example.data.seed.QariInfo
import com.example.data.seed.QuranMetadata
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.Request
import java.io.File

data class AudioPlaybackState(
    val isPlaying: Boolean = false,
    val isBuffering: Boolean = false,
    val qari: QariInfo = QariCatalog.qaris.first(),
    val surahNumber: Int = 1,
    val ayahNumber: Int = 1,
    val totalAyahsInSurah: Int = 7,
    val playbackSpeed: Float = 1.0f,
    val ayahRepeatTarget: Int = 1, // 1, 3, 5, 0 = infinite
    val currentAyahRepeatCount: Int = 1,
    val repeatSurah: Boolean = false,
    val autoNextSurah: Boolean = true,
    val hifzLoopStart: Int? = null,
    val hifzLoopEnd: Int? = null,
    val sleepTimerMinutes: Int? = null,
    val sleepTimerRemainingSec: Int = 0,
    val errorMessage: String? = null,
    val downloadProgressText: String? = null,
)

class QuranAudioService : Service() {

    inner class LocalBinder : Binder() {
        fun getService(): QuranAudioService = this@QuranAudioService
    }

    private val binder = LocalBinder()
    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    private var mediaPlayer: MediaPlayer? = null
    private var sleepTimerJob: Job? = null

    override fun onBind(intent: Intent?): IBinder = binder

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_STOP -> stopPlayback()
            ACTION_TOGGLE -> AudioPlayerManager.togglePlayPause(this)
            ACTION_NEXT -> AudioPlayerManager.skipNextAyah(this)
            ACTION_PREV -> AudioPlayerManager.skipPreviousAyah(this)
        }
        return START_NOT_STICKY
    }

    fun startForegroundPlayback(surahNumber: Int, ayahNumber: Int, qariName: String) {
        val surahName = QuranMetadata.getSurah(surahNumber).englishName
        val openIntent = PendingIntent.getActivity(
            this,
            0,
            Intent(this, MainActivity::class.java),
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        val stopIntent = PendingIntent.getService(
            this,
            1,
            Intent(this, QuranAudioService::class.java).apply { action = ACTION_STOP },
            PendingIntent.FLAG_IMMUTABLE
        )
        val notification: Notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("HIDAYAH • $surahName ($surahNumber:$ayahNumber)")
            .setContentText("Reciter: $qariName")
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentIntent(openIntent)
            .addAction(0, "Stop", stopIntent)
            .setOngoing(true)
            .setSilent(true)
            .build()

        startForeground(NOTIFICATION_ID, notification)
    }

    fun playSource(
        urlOrPath: String,
        isLocalFile: Boolean,
        speed: Float,
        onPrepared: () -> Unit,
        onCompleted: () -> Unit,
        onError: (String) -> Unit,
    ) {
        try {
            mediaPlayer?.release()
            val mp = MediaPlayer().apply {
                setAudioAttributes(
                    AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .build()
                )
                if (isLocalFile) {
                    setDataSource(urlOrPath)
                } else {
                    setDataSource(urlOrPath)
                }
                setOnPreparedListener { player ->
                    try {
                        player.playbackParams = player.playbackParams.setSpeed(speed)
                    } catch (_: Exception) {
                    }
                    player.start()
                    onPrepared()
                }
                setOnCompletionListener {
                    onCompleted()
                }
                setOnErrorListener { _, what, extra ->
                    onError("Audio playback error ($what/$extra)")
                    true
                }
                prepareAsync()
            }
            mediaPlayer = mp
        } catch (e: Exception) {
            Log.e("QuranAudioService", "Failed to start MediaPlayer", e)
            onError(e.localizedMessage ?: "Unable to play recitation")
        }
    }

    fun pause() {
        try {
            if (mediaPlayer?.isPlaying == true) mediaPlayer?.pause()
        } catch (_: Exception) {
        }
    }

    fun resume(speed: Float) {
        try {
            mediaPlayer?.let {
                try {
                    it.playbackParams = it.playbackParams.setSpeed(speed)
                } catch (_: Exception) {
                }
                it.start()
            }
        } catch (_: Exception) {
        }
    }

    fun applySpeed(speed: Float) {
        try {
            mediaPlayer?.let {
                if (it.isPlaying) {
                    it.playbackParams = it.playbackParams.setSpeed(speed)
                }
            }
        } catch (_: Exception) {
        }
    }

    fun stopPlayback() {
        try {
            mediaPlayer?.stop()
            mediaPlayer?.release()
        } catch (_: Exception) {
        }
        mediaPlayer = null
        AudioPlayerManager.markStopped()
        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
    }

    fun scheduleSleepTimer(minutes: Int?, onExpired: () -> Unit) {
        sleepTimerJob?.cancel()
        if (minutes == null || minutes <= 0) return
        sleepTimerJob = serviceScope.launch {
            var remaining = minutes * 60
            while (remaining > 0) {
                AudioPlayerManager.updateSleepRemaining(remaining)
                delay(1000L)
                remaining--
            }
            AudioPlayerManager.updateSleepRemaining(0)
            onExpired()
            stopPlayback()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer?.release()
        mediaPlayer = null
        serviceScope.cancel()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "HIDAYAH Quran Recitation",
                NotificationManager.IMPORTANCE_LOW
            )
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }
    }

    companion object {
        private const val CHANNEL_ID = "hidayah_quran_audio"
        private const val NOTIFICATION_ID = 7861
        const val ACTION_STOP = "com.example.audio.STOP"
        const val ACTION_TOGGLE = "com.example.audio.TOGGLE"
        const val ACTION_NEXT = "com.example.audio.NEXT"
        const val ACTION_PREV = "com.example.audio.PREV"
    }
}

object AudioPlayerManager {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    private val _state = MutableStateFlow(AudioPlaybackState())
    val state: StateFlow<AudioPlaybackState> = _state.asStateFlow()

    private var boundService: QuranAudioService? = null
    private var serviceConnection: android.content.ServiceConnection? = null

    fun playAyah(
        context: Context,
        surahNumber: Int,
        ayahNumber: Int,
        qari: QariInfo = _state.value.qari,
    ) {
        val meta = QuranMetadata.getSurah(surahNumber)
        val clampedAyah = ayahNumber.coerceIn(1, meta.ayahCount)
        _state.value = _state.value.copy(
            qari = qari,
            surahNumber = surahNumber,
            ayahNumber = clampedAyah,
            totalAyahsInSurah = meta.ayahCount,
            isBuffering = true,
            isPlaying = false,
            currentAyahRepeatCount = 1,
            errorMessage = null,
        )
        ensureServiceAndPlay(context.applicationContext)
    }

    fun playSurahFromStart(context: Context, surahNumber: Int, qari: QariInfo = _state.value.qari) {
        playAyah(context, surahNumber, 1, qari)
    }

    fun togglePlayPause(context: Context) {
        val current = _state.value
        val service = boundService
        if (service == null) {
            playAyah(context, current.surahNumber, current.ayahNumber, current.qari)
            return
        }
        if (current.isPlaying) {
            service.pause()
            _state.value = current.copy(isPlaying = false)
        } else {
            service.resume(current.playbackSpeed)
            _state.value = current.copy(isPlaying = true)
        }
    }

    fun skipNextAyah(context: Context) {
        val s = _state.value
        val loopEnd = s.hifzLoopEnd
        val loopStart = s.hifzLoopStart
        if (loopStart != null && loopEnd != null && s.ayahNumber >= loopEnd) {
            playAyah(context, s.surahNumber, loopStart, s.qari)
            return
        }
        if (s.ayahNumber < s.totalAyahsInSurah) {
            playAyah(context, s.surahNumber, s.ayahNumber + 1, s.qari)
        } else if (s.repeatSurah) {
            playAyah(context, s.surahNumber, 1, s.qari)
        } else if (s.autoNextSurah && s.surahNumber < 114) {
            playAyah(context, s.surahNumber + 1, 1, s.qari)
        } else {
            boundService?.stopPlayback()
        }
    }

    fun skipPreviousAyah(context: Context) {
        val s = _state.value
        if (s.ayahNumber > 1) {
            playAyah(context, s.surahNumber, s.ayahNumber - 1, s.qari)
        }
    }

    fun setQari(qari: QariInfo) {
        _state.value = _state.value.copy(qari = qari)
    }

    fun setSpeed(speed: Float) {
        _state.value = _state.value.copy(playbackSpeed = speed)
        boundService?.applySpeed(speed)
    }

    fun setAyahRepeat(target: Int) {
        _state.value = _state.value.copy(ayahRepeatTarget = target)
    }

    fun setRepeatSurah(enabled: Boolean) {
        _state.value = _state.value.copy(repeatSurah = enabled)
    }

    fun setAutoNextSurah(enabled: Boolean) {
        _state.value = _state.value.copy(autoNextSurah = enabled)
    }

    fun setHifzLoopRange(startAyah: Int?, endAyah: Int?) {
        _state.value = _state.value.copy(hifzLoopStart = startAyah, hifzLoopEnd = endAyah)
    }

    fun setSleepTimer(minutes: Int?) {
        _state.value = _state.value.copy(
            sleepTimerMinutes = minutes,
            sleepTimerRemainingSec = (minutes ?: 0) * 60
        )
        boundService?.scheduleSleepTimer(minutes) {
            _state.value = _state.value.copy(sleepTimerMinutes = null, sleepTimerRemainingSec = 0)
        }
    }

    fun stop() {
        boundService?.stopPlayback()
    }

    internal fun markStopped() {
        _state.value = _state.value.copy(isPlaying = false, isBuffering = false)
    }

    internal fun updateSleepRemaining(sec: Int) {
        _state.value = _state.value.copy(sleepTimerRemainingSec = sec)
    }

    private fun ensureServiceAndPlay(appContext: Context) {
        val intent = Intent(appContext, QuranAudioService::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            appContext.startForegroundService(intent)
        } else {
            appContext.startService(intent)
        }

        val currentService = boundService
        if (currentService != null) {
            dispatchCurrentAyahToService(appContext, currentService)
            return
        }

        val conn = object : android.content.ServiceConnection {
            override fun onServiceConnected(name: android.content.ComponentName?, binder: IBinder?) {
                val svc = (binder as? QuranAudioService.LocalBinder)?.getService() ?: return
                boundService = svc
                dispatchCurrentAyahToService(appContext, svc)
            }

            override fun onServiceDisconnected(name: android.content.ComponentName?) {
                boundService = null
            }
        }
        serviceConnection = conn
        appContext.bindService(intent, conn, Context.BIND_AUTO_CREATE)
    }

    private fun dispatchCurrentAyahToService(appContext: Context, service: QuranAudioService) {
        val s = _state.value
        service.startForegroundPlayback(s.surahNumber, s.ayahNumber, s.qari.name)

        scope.launch {
            val dao = AppDatabase.getInstance(appContext).appDao()
            val localFile = withContext(Dispatchers.IO) {
                dao.getDownloadedAudio(s.qari.id, s.surahNumber, s.ayahNumber)?.localPath
                    ?.takeIf { File(it).exists() }
            }
            val globalNum = QuranMetadata.globalAyahNumber(s.surahNumber, s.ayahNumber)
            val source = localFile ?: s.qari.ayahAudioUrl(globalNum)

            service.playSource(
                urlOrPath = source,
                isLocalFile = localFile != null,
                speed = s.playbackSpeed,
                onPrepared = {
                    _state.value = _state.value.copy(isPlaying = true, isBuffering = false, errorMessage = null)
                },
                onCompleted = {
                    handleAyahCompleted(appContext)
                },
                onError = { err ->
                    _state.value = _state.value.copy(isPlaying = false, isBuffering = false, errorMessage = err)
                }
            )
        }
    }

    private fun handleAyahCompleted(appContext: Context) {
        val s = _state.value
        val repeatTarget = s.ayahRepeatTarget
        if (repeatTarget == 0 || s.currentAyahRepeatCount < repeatTarget) {
            _state.value = s.copy(currentAyahRepeatCount = s.currentAyahRepeatCount + 1)
            boundService?.let { dispatchCurrentAyahToService(appContext, it) }
        } else {
            skipNextAyah(appContext)
        }
    }

    suspend fun downloadSurahOffline(
        context: Context,
        surahNumber: Int,
        qari: QariInfo,
    ): Result<Int> = withContext(Dispatchers.IO) {
        val appContext = context.applicationContext
        val meta = QuranMetadata.getSurah(surahNumber)
        val dao = AppDatabase.getInstance(appContext).appDao()
        val dir = File(appContext.filesDir, "quran_audio/${qari.id}/$surahNumber").apply { mkdirs() }
        var downloadedCount = 0

        return@withContext try {
            for (ayah in 1..meta.ayahCount) {
                _state.value = _state.value.copy(
                    downloadProgressText = "Downloading ${meta.englishName}: $ayah / ${meta.ayahCount}"
                )
                val targetFile = File(dir, "ayah_$ayah.mp3")
                if (!targetFile.exists() || targetFile.length() == 0L) {
                    val globalNum = QuranMetadata.globalAyahNumber(surahNumber, ayah)
                    val request = Request.Builder().url(qari.ayahAudioUrl(globalNum)).build()
                    NetworkModule.downloadClient.newCall(request).execute().use { resp ->
                        if (!resp.isSuccessful) {
                            throw IllegalStateException("HTTP ${resp.code} downloading Ayah $ayah")
                        }
                        val bytes = resp.body?.bytes() ?: throw IllegalStateException("Empty audio body")
                        targetFile.writeBytes(bytes)
                    }
                }
                dao.insertDownloadedAudio(
                    DownloadedAudioEntity(
                        qariId = qari.id,
                        surahNumber = surahNumber,
                        ayahNumber = ayah,
                        localPath = targetFile.absolutePath,
                        sizeBytes = targetFile.length(),
                    )
                )
                downloadedCount++
            }
            _state.value = _state.value.copy(downloadProgressText = null)
            Result.success(downloadedCount)
        } catch (e: Exception) {
            _state.value = _state.value.copy(downloadProgressText = null)
            Result.failure(e)
        }
    }
}

package com.example.ui.viewmodel

import android.app.Application
import android.os.StatFs
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.audio.AudioPlayerManager
import com.example.data.local.AppDatabase
import com.example.data.local.BookmarkEntity
import com.example.data.local.DownloadedAudioEntity
import com.example.data.local.FavoriteItemEntity
import com.example.data.local.HifzProgressEntity
import com.example.data.local.LessonProgressEntity
import com.example.data.local.MushafEdition
import com.example.data.local.PageBookmarkEntity
import com.example.data.local.PreferencesRepository
import com.example.data.local.QuranLayoutStyle
import com.example.data.local.QuranScriptType
import com.example.data.local.TasbihSessionEntity
import com.example.data.local.UserPreferencesState
import com.example.data.repository.QuranRepository
import com.example.data.seed.QariInfo
import com.example.data.seed.QuranMetadata
import com.example.localization.AppLanguage
import com.example.ui.theme.ArabicFontChoice
import com.example.ui.theme.IslamicThemePreset
import com.example.ui.theme.QuranReadingMode
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MainAppViewModel(application: Application) : AndroidViewModel(application) {
    private val db = AppDatabase.getInstance(application)
    val dao = db.appDao()
    val prefsRepo = PreferencesRepository(application)
    val quranRepo = QuranRepository(dao, application)

    val prefs: StateFlow<UserPreferencesState> = prefsRepo.preferencesFlow.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = UserPreferencesState()
    )

    val bookmarks: StateFlow<List<BookmarkEntity>> = dao.observeBookmarks().stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList()
    )

    val pageBookmarks: StateFlow<List<PageBookmarkEntity>> = dao.observePageBookmarks().stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList()
    )

    val cachedAyahCount: StateFlow<Int> = dao.observeTotalCachedAyahs().stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5_000), 0
    )

    val cachedPageCount: StateFlow<Int> = dao.observeCachedPageCount().stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5_000), 0
    )

    private val _offlineBatchStatus = MutableStateFlow<String?>(null)
    val offlineBatchStatus: StateFlow<String?> = _offlineBatchStatus.asStateFlow()

    val hifzProgress: StateFlow<List<HifzProgressEntity>> = dao.observeHifzProgress().stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList()
    )

    val tasbihSessions: StateFlow<List<TasbihSessionEntity>> = dao.observeTasbihSessions().stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList()
    )

    val favoriteDuaIds: StateFlow<List<String>> = dao.observeFavoriteIds("dua").stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList()
    )

    val favoriteName99Ids: StateFlow<List<String>> = dao.observeFavoriteIds("name99").stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList()
    )

    val favoriteQariIds: StateFlow<List<String>> = dao.observeFavoriteIds("qari").stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList()
    )

    val favoriteWallpaperIds: StateFlow<List<String>> = dao.observeFavoriteIds("wallpaper").stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList()
    )

    val lessonProgress: StateFlow<List<LessonProgressEntity>> = dao.observeLessonProgress().stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList()
    )

    val downloadedAudio: StateFlow<List<DownloadedAudioEntity>> = dao.observeDownloadedAudio().stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList()
    )

    init {
        viewModelScope.launch {
            quranRepo.ensureStarterSeeded()
        }
    }

    fun availableStorageMb(): Long = try {
        val stat = StatFs(getApplication<Application>().filesDir.path)
        (stat.availableBytes / (1024L * 1024L)).coerceAtLeast(0L)
    } catch (_: Exception) {
        0L
    }

    fun completeWelcome() = viewModelScope.launch { prefsRepo.setHasCompletedWelcome(true) }
    fun setThemePreset(preset: IslamicThemePreset) = viewModelScope.launch { prefsRepo.setThemePreset(preset) }
    fun setAppLanguage(language: AppLanguage) = viewModelScope.launch { prefsRepo.setAppLanguage(language) }
    fun setArabicFont(font: ArabicFontChoice) = viewModelScope.launch { prefsRepo.setArabicFont(font) }
    fun setArabicFontSize(sizeSp: Float) = viewModelScope.launch { prefsRepo.setArabicFontSize(sizeSp) }
    fun setReadingMode(mode: QuranReadingMode) = viewModelScope.launch { prefsRepo.setReadingMode(mode) }
    fun setQuranScript(script: QuranScriptType) = viewModelScope.launch { prefsRepo.setQuranScript(script) }
    fun setQuranLayout(layout: QuranLayoutStyle) = viewModelScope.launch { prefsRepo.setQuranLayout(layout) }
    fun setMushafEdition(edition: MushafEdition) = viewModelScope.launch { prefsRepo.setMushafEdition(edition) }
    fun setTranslationEdition(edition: String) = viewModelScope.launch { prefsRepo.setTranslationEdition(edition) }
    fun setShowTranslation(show: Boolean) = viewModelScope.launch { prefsRepo.setShowTranslation(show) }
    fun setShowWordByWord(show: Boolean) = viewModelScope.launch { prefsRepo.setShowWordByWord(show) }
    fun setShowTransliteration(show: Boolean) = viewModelScope.launch { prefsRepo.setShowTransliteration(show) }
    fun setLastRead(surah: Int, ayah: Int) = viewModelScope.launch { prefsRepo.setLastRead(surah, ayah) }
    fun setLastReadPage(page: Int, surah: Int? = null, ayah: Int? = null) = viewModelScope.launch {
        prefsRepo.setLastReadPage(page, surah, ayah)
    }
    fun setTwoPageSpreadPref(pref: Int) = viewModelScope.launch { prefsRepo.setTwoPageSpreadPref(pref) }
    fun setReaderPageMarginDp(marginDp: Float) = viewModelScope.launch { prefsRepo.setReaderPageMarginDp(marginDp) }
    fun setReaderBrightness(brightness: Float) = viewModelScope.launch { prefsRepo.setReaderBrightness(brightness) }
    fun setKeepScreenAwake(awake: Boolean) = viewModelScope.launch { prefsRepo.setKeepScreenAwake(awake) }
    fun setLockPortraitOrientation(locked: Boolean) = viewModelScope.launch { prefsRepo.setLockPortraitOrientation(locked) }
    fun setDailyPageGoal(pages: Int) = viewModelScope.launch { prefsRepo.setDailyPageGoal(pages) }
    fun toggleFavoriteSurah(surahNumber: Int) = viewModelScope.launch { prefsRepo.toggleFavoriteSurah(surahNumber) }
    fun toggleCompletedJuz(juzNumber: Int) = viewModelScope.launch { prefsRepo.toggleCompletedJuz(juzNumber) }
    fun setAudioVolume(volume: Float) = viewModelScope.launch {
        prefsRepo.setAudioVolume(volume)
        AudioPlayerManager.setVolume(volume)
    }

    fun setSelectedQari(qariId: String) = viewModelScope.launch { prefsRepo.setSelectedQari(qariId) }
    fun setLocation(city: String, lat: Double, lon: Double) = viewModelScope.launch { prefsRepo.setLocation(city, lat, lon) }
    fun setPrayerCalculation(methodIdx: Int, asrHanafi: Boolean) = viewModelScope.launch {
        prefsRepo.setPrayerCalculation(methodIdx, asrHanafi)
    }

    fun setNotifications(prayer: Boolean, adhan: Boolean, jummah: Boolean) = viewModelScope.launch {
        prefsRepo.setNotifications(prayer, adhan, jummah)
    }

    fun setDashboardWidgets(
        showPrayer: Boolean,
        showContinue: Boolean,
        showDaily: Boolean,
        showQuick: Boolean,
        showProgress: Boolean,
    ) = viewModelScope.launch {
        prefsRepo.setDashboardWidgets(showPrayer, showContinue, showDaily, showQuick, showProgress)
    }

    fun toggleDailyPrayer(key: String) = viewModelScope.launch { prefsRepo.toggleDailyPrayer(key) }
    fun toggleSunnahHabit(id: String) = viewModelScope.launch { prefsRepo.toggleSunnahHabit(id) }
    fun adjustQadaPrayers(delta: Int) = viewModelScope.launch { prefsRepo.adjustQadaPrayers(delta) }
    fun incrementFastingDays(delta: Int = 1) = viewModelScope.launch { prefsRepo.incrementFastingDays(delta) }
    fun incrementKhatamPages(delta: Int = 1) = viewModelScope.launch { prefsRepo.incrementKhatamPages(delta) }
    fun incrementCharityCount() = viewModelScope.launch { prefsRepo.incrementCharityCount() }
    fun recordFeedbackSubmitted() = viewModelScope.launch { prefsRepo.recordFeedbackSubmitted() }

    fun setTasbihThemeIndex(idx: Int) = viewModelScope.launch { prefsRepo.setTasbihThemeIndex(idx) }
    fun setDailyHifzTarget(target: Int) = viewModelScope.launch { prefsRepo.setDailyHifzTarget(target) }
    fun setDailyWallpaperId(id: String) = viewModelScope.launch { prefsRepo.setDailyWallpaperId(id) }

    fun incrementTasbihCount(delta: Long = 1L) = viewModelScope.launch {
        prefsRepo.incrementTotalTasbih(delta)
    }

    fun saveTasbihSession(title: String, arabic: String, count: Int, target: Int) = viewModelScope.launch {
        if (count > 0) {
            dao.insertTasbihSession(
                TasbihSessionEntity(
                    dhikrTitle = title,
                    dhikrArabic = arabic,
                    count = count,
                    target = target,
                )
            )
        }
    }

    fun deleteTasbihSession(id: Long) = viewModelScope.launch {
        dao.deleteTasbihSession(id)
    }

    fun upsertBookmark(surahNumber: Int, ayahNumber: Int, note: String = "") = viewModelScope.launch {
        val surah = QuranMetadata.getSurah(surahNumber)
        dao.upsertBookmark(
            BookmarkEntity(
                surahNumber = surahNumber,
                ayahNumber = ayahNumber,
                surahName = surah.englishName,
                note = note.trim(),
            )
        )
    }

    fun removeBookmark(surahNumber: Int, ayahNumber: Int) = viewModelScope.launch {
        dao.deleteBookmark(surahNumber, ayahNumber)
    }

    fun upsertPageBookmark(pageNumber: Int, title: String, note: String = "") = viewModelScope.launch {
        dao.upsertPageBookmark(
            PageBookmarkEntity(
                pageNumber = pageNumber.coerceIn(1, QuranMetadata.TOTAL_MUSHAF_PAGES),
                title = title.ifBlank { "Mushaf Page $pageNumber" },
                note = note.trim(),
            )
        )
    }

    fun removePageBookmark(pageNumber: Int) = viewModelScope.launch {
        dao.deletePageBookmark(pageNumber)
    }

    fun prefetchSurahRangeOffline(surahs: List<Int>, translationEdition: String) = viewModelScope.launch {
        if (_offlineBatchStatus.value != null) return@launch
        quranRepo.prefetchSurahsForOffline(surahs, translationEdition) { done, total, currentName ->
            _offlineBatchStatus.value = if (done >= total) null else "Caching $currentName ($done/$total)"
        }
    }

    fun updateHifzProgress(
        surahNumber: Int,
        memorizedAyahs: Int,
        dailyTarget: Int,
        status: String? = null,
    ) = viewModelScope.launch {
        val total = QuranMetadata.getSurah(surahNumber).ayahCount
        val clamped = memorizedAyahs.coerceIn(0, total)
        val derivedStatus = status ?: when {
            clamped >= total -> "memorized"
            clamped > 0 -> "in_progress"
            else -> "revision"
        }
        dao.upsertHifzProgress(
            HifzProgressEntity(
                surahNumber = surahNumber,
                memorizedAyahs = clamped,
                targetAyahs = total,
                dailyTarget = dailyTarget,
                status = derivedStatus,
            )
        )
    }

    fun toggleFavorite(type: String, itemId: String) = viewModelScope.launch {
        if (dao.isFavorite(type, itemId) > 0) {
            dao.deleteFavorite(type, itemId)
        } else {
            dao.insertFavorite(FavoriteItemEntity(itemType = type, itemId = itemId))
        }
    }

    fun saveLessonProgress(moduleType: String, lessonKey: String, completed: Boolean, score: Int) =
        viewModelScope.launch {
            dao.upsertLessonProgress(
                LessonProgressEntity(
                    moduleType = moduleType,
                    lessonKey = lessonKey,
                    completed = completed,
                    quizScore = score.coerceIn(0, 100),
                )
            )
        }

    fun downloadSurahAudio(surahNumber: Int, qari: QariInfo, onResult: (Result<Int>) -> Unit) =
        viewModelScope.launch {
            val res = AudioPlayerManager.downloadSurahOffline(getApplication(), surahNumber, qari)
            onResult(res)
        }

    fun deleteDownloadedSurahAudio(qariId: String, surahNumber: Int) = viewModelScope.launch {
        dao.deleteDownloadedSurahAudio(qariId, surahNumber)
        AudioPlayerManager.deleteSurahFiles(getApplication(), qariId, surahNumber)
    }
}

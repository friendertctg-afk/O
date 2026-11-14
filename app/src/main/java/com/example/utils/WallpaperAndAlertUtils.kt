package com.example.utils

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.annotation.DrawableRes
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.example.MainActivity
import com.example.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream

object WallpaperAndAlertUtils {
    private const val PRAYER_CHANNEL_ID = "hidayah_prayer_alerts"

    suspend fun saveWallpaperToGallery(
        context: Context,
        @DrawableRes drawableRes: Int,
        title: String,
    ): Result<String> = withContext(Dispatchers.IO) {
        return@withContext try {
            val bitmap = BitmapFactory.decodeResource(context.resources, drawableRes)
                ?: return@withContext Result.failure(IllegalStateException("Unable to decode wallpaper image."))

            val safeName = title.lowercase().replace(Regex("[^a-z0-9]+"), "_").trim('_') + "_hidayah.jpg"
            val values = ContentValues().apply {
                put(MediaStore.Images.Media.DISPLAY_NAME, safeName)
                put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    put(MediaStore.Images.Media.RELATIVE_PATH, "${Environment.DIRECTORY_PICTURES}/HIDAYAH")
                    put(MediaStore.Images.Media.IS_PENDING, 1)
                }
            }

            val resolver = context.contentResolver
            val uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
                ?: return@withContext Result.failure(IllegalStateException("MediaStore unavailable."))

            resolver.openOutputStream(uri)?.use { out ->
                bitmap.compress(Bitmap.CompressFormat.JPEG, 95, out)
            } ?: return@withContext Result.failure(IllegalStateException("Unable to open gallery output stream."))

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                values.clear()
                values.put(MediaStore.Images.Media.IS_PENDING, 0)
                resolver.update(uri, values, null, null)
            }
            Result.success("Saved to Pictures/HIDAYAH/$safeName")
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun shareWallpaper(
        context: Context,
        @DrawableRes drawableRes: Int,
        title: String,
    ): Result<Unit> = withContext(Dispatchers.IO) {
        return@withContext try {
            val bitmap = BitmapFactory.decodeResource(context.resources, drawableRes)
                ?: return@withContext Result.failure(IllegalStateException("Wallpaper unavailable"))
            val cacheDir = File(context.cacheDir, "wallpapers").apply { mkdirs() }
            val file = File(cacheDir, "hidayah_share.jpg")
            FileOutputStream(file).use { out ->
                bitmap.compress(Bitmap.CompressFormat.JPEG, 92, out)
            }
            val uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                file
            )
            val intent = Intent(Intent.ACTION_SEND).apply {
                type = "image/jpeg"
                putExtra(Intent.EXTRA_STREAM, uri)
                putExtra(Intent.EXTRA_TEXT, "$title — Shared from HIDAYAH (هداية)")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            withContext(Dispatchers.Main) {
                context.startActivity(Intent.createChooser(intent, "Share Islamic Wallpaper"))
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun shareText(context: Context, subject: String, text: String) {
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_SUBJECT, subject)
            putExtra(Intent.EXTRA_TEXT, "$text\n\n— Shared via HIDAYAH (هداية)")
        }
        context.startActivity(Intent.createChooser(intent, subject))
    }

    fun openNearbyMosques(context: Context) {
        val mapIntent = Intent(Intent.ACTION_VIEW, Uri.parse("geo:0,0?q=mosque+masjid"))
        if (mapIntent.resolveActivity(context.packageManager) != null) {
            context.startActivity(mapIntent)
        } else {
            val webIntent = Intent(
                Intent.ACTION_VIEW,
                Uri.parse("https://www.google.com/maps/search/mosque+near+me")
            )
            context.startActivity(webIntent)
        }
    }

    fun openWhatsAppSupport(context: Context) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://wa.me/966553182669"))
        context.startActivity(intent)
    }

    fun sendPrayerTestNotification(
        context: Context,
        title: String,
        body: String,
        withSound: Boolean,
    ): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val granted = ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
            if (!granted) return false
        }

        val manager = context.getSystemService(NotificationManager::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                PRAYER_CHANNEL_ID,
                "HIDAYAH Prayer & Jummah Alerts",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            manager.createNotificationChannel(channel)
        }

        val openPending = PendingIntent.getActivity(
            context,
            7862,
            Intent(context, MainActivity::class.java),
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val builder = NotificationCompat.Builder(context, PRAYER_CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(title)
            .setContentText(body)
            .setStyle(NotificationCompat.BigTextStyle().bigText(body))
            .setContentIntent(openPending)
            .setAutoCancel(true)

        if (!withSound) {
            builder.setSilent(true)
        }

        manager.notify(7862, builder.build())
        return true
    }
}

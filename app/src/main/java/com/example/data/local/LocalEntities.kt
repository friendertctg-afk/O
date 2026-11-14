package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(
    tableName = "cached_ayahs",
    primaryKeys = ["surahNumber", "ayahNumber"]
)
data class CachedAyahEntity(
    val surahNumber: Int,
    val ayahNumber: Int,
    val globalNumber: Int,
    val juz: Int,
    val page: Int,
    val uthmaniText: String,
    val simpleText: String,
    val tajweedMarkup: String,
    val transliteration: String = "",
)

@Entity(
    tableName = "cached_translations",
    primaryKeys = ["surahNumber", "ayahNumber", "edition"]
)
data class CachedTranslationEntity(
    val surahNumber: Int,
    val ayahNumber: Int,
    val edition: String,
    val text: String,
)

@Entity(
    tableName = "bookmarks",
    primaryKeys = ["surahNumber", "ayahNumber"]
)
data class BookmarkEntity(
    val surahNumber: Int,
    val ayahNumber: Int,
    val surahName: String,
    val note: String = "",
    val updatedAtMs: Long = System.currentTimeMillis(),
)

@Entity(tableName = "hifz_progress")
data class HifzProgressEntity(
    @PrimaryKey val surahNumber: Int,
    val memorizedAyahs: Int,
    val targetAyahs: Int,
    val dailyTarget: Int = 5,
    val status: String = "in_progress",
    val lastRevisedMs: Long = System.currentTimeMillis(),
)

@Entity(tableName = "tasbih_sessions")
data class TasbihSessionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val dhikrTitle: String,
    val dhikrArabic: String,
    val count: Int,
    val target: Int,
    val timestampMs: Long = System.currentTimeMillis(),
)

@Entity(
    tableName = "favorite_items",
    primaryKeys = ["itemType", "itemId"]
)
data class FavoriteItemEntity(
    val itemType: String, // "dua", "name99", "qari", "wallpaper"
    val itemId: String,
    val createdAtMs: Long = System.currentTimeMillis(),
)

@Entity(
    tableName = "lesson_progress",
    primaryKeys = ["moduleType", "lessonKey"]
)
data class LessonProgressEntity(
    val moduleType: String, // "arabic" or "tajweed"
    val lessonKey: String,
    val completed: Boolean,
    val quizScore: Int,
    val updatedAtMs: Long = System.currentTimeMillis(),
)

@Entity(
    tableName = "downloaded_audio",
    primaryKeys = ["qariId", "surahNumber", "ayahNumber"]
)
data class DownloadedAudioEntity(
    val qariId: String,
    val surahNumber: Int,
    val ayahNumber: Int,
    val localPath: String,
    val sizeBytes: Long,
    val downloadedAtMs: Long = System.currentTimeMillis(),
)

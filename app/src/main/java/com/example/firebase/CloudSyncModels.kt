package com.example.firebase

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.IgnoreExtraProperties
import com.google.firebase.firestore.ServerTimestamp

@IgnoreExtraProperties
data class UserSyncProfile(
    @DocumentId val id: String = "",
    val userId: String = "",
    val displayName: String = "Reader",
    val lastSurah: Long = 1L,
    val lastAyah: Long = 1L,
    val totalTasbihCount: Long = 0L,
    val hifzAyahsCount: Long = 0L,
    val preferredTheme: String = "EMERALD_GOLD",
    val preferredLanguage: String = "en",
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null,
)

@IgnoreExtraProperties
data class CloudQuranBookmark(
    @DocumentId val id: String = "",
    val userId: String = "",
    val surahNumber: Long = 1L,
    val ayahNumber: Long = 1L,
    val surahName: String = "Al-Fatihah",
    val note: String? = null,
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null,
)

@IgnoreExtraProperties
data class CloudHifzProgress(
    @DocumentId val id: String = "",
    val userId: String = "",
    val surahNumber: Long = 1L,
    val memorizedAyahs: Long = 0L,
    val targetAyahs: Long = 7L,
    val status: String = "in_progress",
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null,
)

@IgnoreExtraProperties
data class CloudLearningProgress(
    @DocumentId val id: String = "",
    val userId: String = "",
    val moduleType: String = "arabic",
    val lessonKey: String = "",
    val completed: Boolean = false,
    val quizScore: Long = 0L,
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null,
)

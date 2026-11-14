package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface AppDao {
    // Cached Quran Ayahs & Pages
    @Query("SELECT * FROM cached_ayahs WHERE surahNumber = :surahNumber ORDER BY ayahNumber ASC")
    fun observeSurahAyahs(surahNumber: Int): Flow<List<CachedAyahEntity>>

    @Query("SELECT * FROM cached_ayahs WHERE surahNumber = :surahNumber ORDER BY ayahNumber ASC")
    suspend fun getSurahAyahs(surahNumber: Int): List<CachedAyahEntity>

    @Query("SELECT * FROM cached_ayahs WHERE page = :pageNumber ORDER BY surahNumber ASC, ayahNumber ASC")
    fun observePageAyahs(pageNumber: Int): Flow<List<CachedAyahEntity>>

    @Query("SELECT * FROM cached_ayahs WHERE page = :pageNumber ORDER BY surahNumber ASC, ayahNumber ASC")
    suspend fun getPageAyahs(pageNumber: Int): List<CachedAyahEntity>

    @Query("SELECT page FROM cached_ayahs WHERE surahNumber = :surahNumber AND ayahNumber = :ayahNumber LIMIT 1")
    suspend fun getPageForAyah(surahNumber: Int, ayahNumber: Int): Int?

    @Query("SELECT COUNT(*) FROM cached_ayahs")
    fun observeTotalCachedAyahs(): Flow<Int>

    @Query("SELECT COUNT(DISTINCT page) FROM cached_ayahs")
    fun observeCachedPageCount(): Flow<Int>

    @Query("SELECT * FROM cached_ayahs WHERE juz = :juz ORDER BY surahNumber ASC, ayahNumber ASC")
    suspend fun getJuzAyahs(juz: Int): List<CachedAyahEntity>

    @Query("SELECT COUNT(*) FROM cached_ayahs WHERE surahNumber = :surahNumber")
    suspend fun countSurahAyahs(surahNumber: Int): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAyahs(ayahs: List<CachedAyahEntity>)

    // Cached Translations
    @Query("SELECT * FROM cached_translations WHERE surahNumber = :surahNumber AND edition = :edition ORDER BY ayahNumber ASC")
    fun observeTranslations(surahNumber: Int, edition: String): Flow<List<CachedTranslationEntity>>

    @Query(
        "SELECT t.* FROM cached_translations t " +
            "INNER JOIN cached_ayahs a ON t.surahNumber = a.surahNumber AND t.ayahNumber = a.ayahNumber " +
            "WHERE a.page = :pageNumber AND t.edition = :edition " +
            "ORDER BY t.surahNumber ASC, t.ayahNumber ASC"
    )
    fun observePageTranslations(pageNumber: Int, edition: String): Flow<List<CachedTranslationEntity>>

    @Query("SELECT * FROM cached_translations WHERE surahNumber = :surahNumber AND edition = :edition ORDER BY ayahNumber ASC")
    suspend fun getTranslations(surahNumber: Int, edition: String): List<CachedTranslationEntity>

    @Query(
        "SELECT t.* FROM cached_translations t " +
            "INNER JOIN cached_ayahs a ON t.surahNumber = a.surahNumber AND t.ayahNumber = a.ayahNumber " +
            "WHERE a.page = :pageNumber AND t.edition = :edition"
    )
    suspend fun getPageTranslations(pageNumber: Int, edition: String): List<CachedTranslationEntity>

    @Query(
        "SELECT * FROM cached_translations WHERE text LIKE '%' || :query || '%' " +
            "OR CAST(surahNumber AS TEXT) || ':' || CAST(ayahNumber AS TEXT) = :query LIMIT 40"
    )
    suspend fun searchCachedTranslations(query: String): List<CachedTranslationEntity>

    @Query("SELECT * FROM cached_ayahs WHERE uthmaniText LIKE '%' || :query || '%' OR simpleText LIKE '%' || :query || '%' LIMIT 40")
    suspend fun searchCachedArabic(query: String): List<CachedAyahEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTranslations(items: List<CachedTranslationEntity>)

    // Ayah & Page Bookmarks
    @Query("SELECT * FROM bookmarks ORDER BY updatedAtMs DESC")
    fun observeBookmarks(): Flow<List<BookmarkEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertBookmark(bookmark: BookmarkEntity)

    @Query("DELETE FROM bookmarks WHERE surahNumber = :surahNumber AND ayahNumber = :ayahNumber")
    suspend fun deleteBookmark(surahNumber: Int, ayahNumber: Int)

    @Query("SELECT * FROM page_bookmarks ORDER BY updatedAtMs DESC")
    fun observePageBookmarks(): Flow<List<PageBookmarkEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertPageBookmark(bookmark: PageBookmarkEntity)

    @Query("DELETE FROM page_bookmarks WHERE pageNumber = :pageNumber")
    suspend fun deletePageBookmark(pageNumber: Int)

    // Hifz Progress
    @Query("SELECT * FROM hifz_progress ORDER BY surahNumber ASC")
    fun observeHifzProgress(): Flow<List<HifzProgressEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertHifzProgress(item: HifzProgressEntity)

    // Tasbih Sessions
    @Query("SELECT * FROM tasbih_sessions ORDER BY timestampMs DESC LIMIT 50")
    fun observeTasbihSessions(): Flow<List<TasbihSessionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTasbihSession(session: TasbihSessionEntity)

    @Query("DELETE FROM tasbih_sessions WHERE id = :id")
    suspend fun deleteTasbihSession(id: Long)

    // Favorites
    @Query("SELECT itemId FROM favorite_items WHERE itemType = :itemType")
    fun observeFavoriteIds(itemType: String): Flow<List<String>>

    @Query("SELECT COUNT(*) FROM favorite_items WHERE itemType = :itemType AND itemId = :itemId")
    suspend fun isFavorite(itemType: String, itemId: String): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavorite(item: FavoriteItemEntity)

    @Query("DELETE FROM favorite_items WHERE itemType = :itemType AND itemId = :itemId")
    suspend fun deleteFavorite(itemType: String, itemId: String)

    // Lesson Progress
    @Query("SELECT * FROM lesson_progress ORDER BY updatedAtMs DESC")
    fun observeLessonProgress(): Flow<List<LessonProgressEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertLessonProgress(item: LessonProgressEntity)

    // Downloaded Audio
    @Query("SELECT * FROM downloaded_audio ORDER BY downloadedAtMs DESC")
    fun observeDownloadedAudio(): Flow<List<DownloadedAudioEntity>>

    @Query("SELECT * FROM downloaded_audio WHERE qariId = :qariId AND surahNumber = :surahNumber AND ayahNumber = :ayahNumber LIMIT 1")
    suspend fun getDownloadedAudio(qariId: String, surahNumber: Int, ayahNumber: Int): DownloadedAudioEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDownloadedAudio(item: DownloadedAudioEntity)

    @Query("DELETE FROM downloaded_audio WHERE qariId = :qariId AND surahNumber = :surahNumber")
    suspend fun deleteDownloadedSurahAudio(qariId: String, surahNumber: Int)
}

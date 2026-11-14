package com.example.data.repository

import android.content.Context
import android.util.Log
import com.example.data.local.AppDao
import com.example.data.local.CachedAyahEntity
import com.example.data.local.CachedTranslationEntity
import com.example.data.remote.NetworkModule
import com.example.data.seed.QuranMetadata
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.withContext

data class AyahDisplayItem(
    val surahNumber: Int,
    val ayahNumber: Int,
    val globalNumber: Int,
    val juz: Int,
    val page: Int,
    val uthmaniText: String,
    val simpleText: String,
    val tajweedMarkup: String,
    val transliteration: String,
    val translationText: String,
)

class QuranRepository(
    private val dao: AppDao,
    private val context: Context,
) {
    private var seeded = false

    suspend fun ensureStarterSeeded() = withContext(Dispatchers.IO) {
        if (!seeded) {
            if (dao.countSurahAyahs(1) == 0) {
                dao.insertAyahs(QuranMetadata.bundledAyahs)
                dao.insertTranslations(QuranMetadata.bundledTranslations)
            }
            seeded = true
        }
    }

    fun observeSurahAyahs(
        surahNumber: Int,
        translationEdition: String,
    ): Flow<List<AyahDisplayItem>> {
        return combine(
            dao.observeSurahAyahs(surahNumber),
            dao.observeTranslations(surahNumber, translationEdition),
        ) { ayahs, translations ->
            val transMap = translations.associateBy { it.ayahNumber }
            ayahs.map { a ->
                AyahDisplayItem(
                    surahNumber = a.surahNumber,
                    ayahNumber = a.ayahNumber,
                    globalNumber = a.globalNumber,
                    juz = a.juz,
                    page = a.page,
                    uthmaniText = a.uthmaniText,
                    simpleText = a.simpleText,
                    tajweedMarkup = a.tajweedMarkup,
                    transliteration = a.transliteration,
                    translationText = transMap[a.ayahNumber]?.text ?: "",
                )
            }
        }
    }

    suspend fun loadSurahIfNeeded(
        surahNumber: Int,
        translationEdition: String,
    ): Result<Unit> = withContext(Dispatchers.IO) {
        ensureStarterSeeded()
        val existingAyahs = dao.getSurahAyahs(surahNumber)
        val existingTrans = dao.getTranslations(surahNumber, translationEdition)
        val expectedCount = QuranMetadata.getSurah(surahNumber).ayahCount

        val needsArabic = existingAyahs.size < expectedCount
        val needsTrans = existingTrans.size < expectedCount

        if (!needsArabic && !needsTrans) {
            return@withContext Result.success(Unit)
        }

        return@withContext try {
            val editions = buildList {
                if (needsArabic) {
                    add("quran-uthmani")
                    add("quran-simple")
                    add("quran-tajweed")
                }
                add(translationEdition)
            }.distinct().joinToString(",")

            val response = NetworkModule.quranApi.getSurahEditions(surahNumber, editions)
            val editionsList = response.data
            if (editionsList.isEmpty()) {
                return@withContext Result.failure(IllegalStateException("Empty Quran response from server"))
            }

            if (needsArabic) {
                val uthmani = editionsList.getOrNull(0)?.ayahs ?: emptyList()
                val simple = editionsList.getOrNull(1)?.ayahs ?: uthmani
                val tajweed = editionsList.getOrNull(2)?.ayahs ?: uthmani
                val cached = uthmani.mapIndexed { idx, u ->
                    CachedAyahEntity(
                        surahNumber = surahNumber,
                        ayahNumber = u.numberInSurah,
                        globalNumber = u.number,
                        juz = u.juz,
                        page = u.page,
                        uthmaniText = u.text,
                        simpleText = simple.getOrNull(idx)?.text ?: u.text,
                        tajweedMarkup = tajweed.getOrNull(idx)?.text ?: u.text,
                        transliteration = "",
                    )
                }
                dao.insertAyahs(cached)
            }

            val transDto = editionsList.lastOrNull()
            if (transDto != null) {
                val transEntities = transDto.ayahs.map { a ->
                    CachedTranslationEntity(
                        surahNumber = surahNumber,
                        ayahNumber = a.numberInSurah,
                        edition = translationEdition,
                        text = a.text,
                    )
                }
                dao.insertTranslations(transEntities)
            }

            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("QuranRepository", "Failed to load Surah $surahNumber", e)
            if (existingAyahs.isNotEmpty()) {
                Result.success(Unit)
            } else {
                Result.failure(e)
            }
        }
    }

    suspend fun searchVerses(query: String, edition: String): List<AyahDisplayItem> =
        withContext(Dispatchers.IO) {
            ensureStarterSeeded()
            val trimmed = query.trim()
            if (trimmed.isBlank()) return@withContext emptyList()

            // Support direct "Surah:Ayah" lookup e.g. "2:255"
            val colonParts = trimmed.split(":")
            if (colonParts.size == 2) {
                val s = colonParts[0].toIntOrNull()
                val a = colonParts[1].toIntOrNull()
                if (s != null && s in 1..114 && a != null && a >= 1) {
                    loadSurahIfNeeded(s, edition)
                    val ayah = dao.getSurahAyahs(s).firstOrNull { it.ayahNumber == a }
                    val trans = dao.getTranslations(s, edition).firstOrNull { it.ayahNumber == a }
                    if (ayah != null) {
                        return@withContext listOf(
                            AyahDisplayItem(
                                surahNumber = ayah.surahNumber,
                                ayahNumber = ayah.ayahNumber,
                                globalNumber = ayah.globalNumber,
                                juz = ayah.juz,
                                page = ayah.page,
                                uthmaniText = ayah.uthmaniText,
                                simpleText = ayah.simpleText,
                                tajweedMarkup = ayah.tajweedMarkup,
                                transliteration = ayah.transliteration,
                                translationText = trans?.text ?: "",
                            )
                        )
                    }
                }
            }

            // Local cached search first + remote keyword search
            val localMatches = dao.searchCachedTranslations(trimmed).map { t ->
                val a = dao.getSurahAyahs(t.surahNumber).firstOrNull { it.ayahNumber == t.ayahNumber }
                AyahDisplayItem(
                    surahNumber = t.surahNumber,
                    ayahNumber = t.ayahNumber,
                    globalNumber = a?.globalNumber ?: QuranMetadata.globalAyahNumber(t.surahNumber, t.ayahNumber),
                    juz = a?.juz ?: 1,
                    page = a?.page ?: 1,
                    uthmaniText = a?.uthmaniText ?: "",
                    simpleText = a?.simpleText ?: "",
                    tajweedMarkup = a?.tajweedMarkup ?: "",
                    transliteration = a?.transliteration ?: "",
                    translationText = t.text,
                )
            }
            if (localMatches.isNotEmpty()) return@withContext localMatches

            return@withContext try {
                val remote = NetworkModule.quranApi.searchQuran(trimmed, edition)
                remote.data?.matches?.take(25)?.map { m ->
                    AyahDisplayItem(
                        surahNumber = m.surah.number,
                        ayahNumber = m.numberInSurah,
                        globalNumber = m.number,
                        juz = QuranMetadata.getSurah(m.surah.number).startJuz,
                        page = 1,
                        uthmaniText = "",
                        simpleText = "",
                        tajweedMarkup = "",
                        transliteration = "",
                        translationText = m.text,
                    )
                } ?: emptyList()
            } catch (_: Exception) {
                emptyList()
            }
        }
}

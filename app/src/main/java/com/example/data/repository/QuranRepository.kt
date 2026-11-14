package com.example.data.repository

import android.content.Context
import android.util.Log
import com.example.data.local.AppDao
import com.example.data.local.CachedAyahEntity
import com.example.data.local.CachedTranslationEntity
import com.example.data.local.MushafEdition
import com.example.data.remote.NetworkModule
import com.example.data.seed.QuranMetadata
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
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
    val ruku: Int = 1,
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
                    ruku = a.ruku,
                )
            }
        }
    }

    fun observePageAyahs(
        pageNumber: Int,
        translationEdition: String,
        mushafEdition: MushafEdition = MushafEdition.MADANI,
    ): Flow<List<AyahDisplayItem>> {
        val isHafizi611 = QuranMetadata.isHafizi611Edition(mushafEdition)
        val total = QuranMetadata.totalPagesForEdition(mushafEdition)
        val clampedPage = pageNumber.coerceIn(1, total)
        val madaniPage = if (isHafizi611) {
            QuranMetadata.resolveHafiziPageSlice(clampedPage).madaniPage
        } else {
            clampedPage
        }
        return combine(
            dao.observePageAyahs(madaniPage),
            dao.observePageTranslations(madaniPage, translationEdition),
        ) { ayahs, translations ->
            val transMap = translations.associateBy { it.surahNumber to it.ayahNumber }
            val mapped = ayahs.map { a ->
                AyahDisplayItem(
                    surahNumber = a.surahNumber,
                    ayahNumber = a.ayahNumber,
                    globalNumber = a.globalNumber,
                    juz = a.juz,
                    page = if (isHafizi611) clampedPage else a.page,
                    uthmaniText = a.uthmaniText,
                    simpleText = a.simpleText,
                    tajweedMarkup = a.tajweedMarkup,
                    transliteration = a.transliteration,
                    translationText = transMap[a.surahNumber to a.ayahNumber]?.text ?: "",
                    ruku = a.ruku,
                )
            }
            if (isHafizi611) {
                QuranMetadata.sliceItemsForHafiziPage(clampedPage, mapped) { it.surahNumber }
            } else {
                mapped
            }
        }
    }

    suspend fun resolvePageForAyah(
        surahNumber: Int,
        ayahNumber: Int,
        mushafEdition: MushafEdition = MushafEdition.MADANI,
    ): Int = withContext(Dispatchers.IO) {
        ensureStarterSeeded()
        val madaniPage = dao.getPageForAyah(surahNumber, ayahNumber)
            ?: QuranMetadata.startPageForSurah(surahNumber)
        if (QuranMetadata.isHafizi611Edition(mushafEdition)) {
            QuranMetadata.hafiziPageForSurahAndMadani(surahNumber, ayahNumber, madaniPage)
        } else {
            madaniPage
        }
    }

    suspend fun loadPageIfNeeded(
        pageNumber: Int,
        translationEdition: String,
        mushafEdition: MushafEdition = MushafEdition.MADANI,
    ): Result<Unit> = withContext(Dispatchers.IO) {
        ensureStarterSeeded()
        val total = QuranMetadata.totalPagesForEdition(mushafEdition)
        val clamped = pageNumber.coerceIn(1, total)
        val page = if (QuranMetadata.isHafizi611Edition(mushafEdition)) {
            QuranMetadata.resolveHafiziPageSlice(clamped).madaniPage
        } else {
            clamped
        }
        val existingAyahs = dao.getPageAyahs(page)
        val existingTrans = dao.getPageTranslations(page, translationEdition)

        // Note: Starter seed only partially populates pages 601/602/604; verify full page if Surahs are incomplete
        val hasAllExistingSurahsComplete = existingAyahs.isNotEmpty() &&
            (page !in setOf(601, 602, 604) || existingAyahs.size >= 12)
        val needsArabic = !hasAllExistingSurahsComplete
        val needsTrans = needsArabic || existingTrans.size < existingAyahs.size

        if (!needsArabic && !needsTrans) {
            return@withContext Result.success(Unit)
        }

        return@withContext try {
            coroutineScope {
                val uthmaniDef = if (needsArabic) {
                    async { NetworkModule.quranApi.getPageEdition(page, "quran-uthmani") }
                } else null
                val simpleDef = if (needsArabic) {
                    async { runCatching { NetworkModule.quranApi.getPageEdition(page, "quran-simple") }.getOrNull() }
                } else null
                val tajweedDef = if (needsArabic) {
                    async { runCatching { NetworkModule.quranApi.getPageEdition(page, "quran-tajweed") }.getOrNull() }
                } else null
                val transDef = async {
                    runCatching { NetworkModule.quranApi.getPageEdition(page, translationEdition) }.getOrNull()
                }

                if (needsArabic) {
                    val uthmaniAyahs = uthmaniDef?.await()?.data?.ayahs ?: emptyList()
                    if (uthmaniAyahs.isEmpty()) {
                        return@coroutineScope Result.failure(IllegalStateException("Empty Mushaf page $page response"))
                    }
                    val simpleAyahs = simpleDef?.await()?.data?.ayahs.orEmpty().associateBy { it.number }
                    val tajweedAyahs = tajweedDef?.await()?.data?.ayahs.orEmpty().associateBy { it.number }

                    val entities = uthmaniAyahs.map { u ->
                        val sNum = u.surah?.number ?: 1
                        CachedAyahEntity(
                            surahNumber = sNum,
                            ayahNumber = u.numberInSurah,
                            globalNumber = u.number,
                            juz = u.juz,
                            page = u.page,
                            uthmaniText = u.text,
                            simpleText = simpleAyahs[u.number]?.text ?: u.text,
                            tajweedMarkup = tajweedAyahs[u.number]?.text ?: u.text,
                            transliteration = "",
                            ruku = u.ruku,
                        )
                    }
                    dao.insertAyahs(entities)
                }

                val transAyahs = transDef.await()?.data?.ayahs.orEmpty()
                if (transAyahs.isNotEmpty()) {
                    val transEntities = transAyahs.map { t ->
                        CachedTranslationEntity(
                            surahNumber = t.surah?.number ?: 1,
                            ayahNumber = t.numberInSurah,
                            edition = translationEdition,
                            text = t.text,
                        )
                    }
                    dao.insertTranslations(transEntities)
                }
                Result.success(Unit)
            }
        } catch (e: Exception) {
            Log.e("QuranRepository", "Failed to load Mushaf page $page", e)
            if (existingAyahs.isNotEmpty()) {
                Result.success(Unit)
            } else {
                Result.failure(e)
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
                        ruku = u.ruku,
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

    suspend fun prefetchSurahsForOffline(
        surahNumbers: List<Int>,
        translationEdition: String,
        onProgress: (done: Int, total: Int, currentName: String) -> Unit,
    ): Result<Int> = withContext(Dispatchers.IO) {
        ensureStarterSeeded()
        var successCount = 0
        val total = surahNumbers.size
        try {
            surahNumbers.forEachIndexed { idx, sNum ->
                val meta = QuranMetadata.getSurah(sNum)
                onProgress(idx, total, meta.englishName)
                val res = loadSurahIfNeeded(sNum, translationEdition)
                if (res.isSuccess) successCount++
            }
            onProgress(total, total, "Complete")
            Result.success(successCount)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun searchVerses(query: String, edition: String): List<AyahDisplayItem> =
        withContext(Dispatchers.IO) {
            ensureStarterSeeded()
            val trimmed = query.trim()
            if (trimmed.isBlank()) return@withContext emptyList()

            // 1. Direct "Surah:Ayah" lookup e.g. "2:255"
            val colonParts = trimmed.split(":")
            if (colonParts.size == 2) {
                val s = colonParts[0].trim().toIntOrNull()
                val a = colonParts[1].trim().toIntOrNull()
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
                                ruku = ayah.ruku,
                            )
                        )
                    }
                }
            }

            // 2. Direct Mushaf Page lookup e.g. "page 255", "p.1", "p 611"
            val pageRegex = Regex("^(?:page|p\\.?|صفحة)\\s*(\\d{1,3})$", RegexOption.IGNORE_CASE)
            pageRegex.matchEntire(trimmed)?.groupValues?.getOrNull(1)?.toIntOrNull()?.let { pNum ->
                if (pNum in 1..QuranMetadata.TOTAL_HAFIZI_PAGES) {
                    val madaniPage = if (pNum > QuranMetadata.TOTAL_MUSHAF_PAGES) {
                        QuranMetadata.resolveHafiziPageSlice(pNum).madaniPage
                    } else {
                        pNum
                    }
                    loadPageIfNeeded(madaniPage, edition)
                    val pAyahs = dao.getPageAyahs(madaniPage)
                    val pTrans = dao.getPageTranslations(madaniPage, edition).associateBy { it.surahNumber to it.ayahNumber }
                    if (pAyahs.isNotEmpty()) {
                        return@withContext pAyahs.map { ayah ->
                            AyahDisplayItem(
                                surahNumber = ayah.surahNumber,
                                ayahNumber = ayah.ayahNumber,
                                globalNumber = ayah.globalNumber,
                                juz = ayah.juz,
                                page = pNum,
                                uthmaniText = ayah.uthmaniText,
                                simpleText = ayah.simpleText,
                                tajweedMarkup = ayah.tajweedMarkup,
                                transliteration = ayah.transliteration,
                                translationText = pTrans[ayah.surahNumber to ayah.ayahNumber]?.text ?: "",
                                ruku = ayah.ruku,
                            )
                        }
                    }
                }
            }

            // 3. Curated Topic lookup e.g. "patience", "mercy", "sabr", "tawakkul"
            val matchingTopics = QuranMetadata.topics.filter {
                it.id.contains(trimmed, ignoreCase = true) ||
                    it.title.contains(trimmed, ignoreCase = true) ||
                    it.subtitle.contains(trimmed, ignoreCase = true)
            }
            if (matchingTopics.isNotEmpty()) {
                val results = mutableListOf<AyahDisplayItem>()
                for (topic in matchingTopics.take(2)) {
                    for ((sNum, aNum) in topic.references) {
                        loadSurahIfNeeded(sNum, edition)
                        val ayah = dao.getSurahAyahs(sNum).firstOrNull { it.ayahNumber == aNum }
                        val trans = dao.getTranslations(sNum, edition).firstOrNull { it.ayahNumber == aNum }
                        if (ayah != null) {
                            results += AyahDisplayItem(
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
                                ruku = ayah.ruku,
                            )
                        }
                    }
                }
                if (results.isNotEmpty()) return@withContext results
            }

            // 4. Local Arabic + cached translation search
            val localArabic = dao.searchCachedArabic(trimmed).map { a ->
                val t = dao.getTranslations(a.surahNumber, edition).firstOrNull { it.ayahNumber == a.ayahNumber }
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
                    translationText = t?.text ?: "",
                    ruku = a.ruku,
                )
            }
            if (localArabic.isNotEmpty()) return@withContext localArabic

            val localMatches = dao.searchCachedTranslations(trimmed).map { t ->
                val a = dao.getSurahAyahs(t.surahNumber).firstOrNull { it.ayahNumber == t.ayahNumber }
                AyahDisplayItem(
                    surahNumber = t.surahNumber,
                    ayahNumber = t.ayahNumber,
                    globalNumber = a?.globalNumber ?: QuranMetadata.globalAyahNumber(t.surahNumber, t.ayahNumber),
                    juz = a?.juz ?: QuranMetadata.getSurah(t.surahNumber).startJuz,
                    page = a?.page ?: QuranMetadata.startPageForSurah(t.surahNumber),
                    uthmaniText = a?.uthmaniText ?: "",
                    simpleText = a?.simpleText ?: "",
                    tajweedMarkup = a?.tajweedMarkup ?: "",
                    transliteration = a?.transliteration ?: "",
                    translationText = t.text,
                    ruku = a?.ruku ?: 1,
                )
            }
            if (localMatches.isNotEmpty()) return@withContext localMatches

            // 5. Remote AlQuran Cloud keyword search
            return@withContext try {
                val remote = NetworkModule.quranApi.searchQuran(trimmed, edition)
                remote.data?.matches?.take(25)?.map { m ->
                    AyahDisplayItem(
                        surahNumber = m.surah.number,
                        ayahNumber = m.numberInSurah,
                        globalNumber = m.number,
                        juz = QuranMetadata.getSurah(m.surah.number).startJuz,
                        page = QuranMetadata.startPageForSurah(m.surah.number),
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

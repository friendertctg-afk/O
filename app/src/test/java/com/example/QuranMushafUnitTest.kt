package com.example

import com.example.data.local.MushafEdition
import com.example.data.local.QuranLayoutStyle
import com.example.data.seed.QuranBengaliData
import com.example.data.seed.QuranMetadata
import com.example.ui.theme.ArabicFontChoice
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class QuranMushafUnitTest {

    @Test
    fun `all 114 surahs and 30 juz have valid mushaf pages`() {
        assertEquals(604, QuranMetadata.TOTAL_MUSHAF_PAGES)
        assertEquals(114, QuranMetadata.surahs.size)
        assertEquals(30, QuranMetadata.juzList.size)

        QuranMetadata.surahs.forEach { surah ->
            assertTrue(
                "Surah ${surah.number} startPage out of bounds: ${surah.startPage}",
                surah.startPage in 1..QuranMetadata.TOTAL_MUSHAF_PAGES
            )
            assertEquals(surah.startPage, QuranMetadata.startPageForSurah(surah.number))
        }

        QuranMetadata.juzList.forEach { juz ->
            assertTrue(
                "Juz ${juz.number} startPage out of bounds: ${juz.startPage}",
                juz.startPage in 1..QuranMetadata.TOTAL_MUSHAF_PAGES
            )
            assertEquals(juz.startPage, QuranMetadata.startPageForJuz(juz.number))
        }
    }

    @Test
    fun `known surah and juz start pages match standard 604 page mushaf`() {
        assertEquals(1, QuranMetadata.startPageForSurah(1))
        assertEquals(2, QuranMetadata.startPageForSurah(2))
        assertEquals(293, QuranMetadata.startPageForSurah(18))
        assertEquals(440, QuranMetadata.startPageForSurah(36))
        assertEquals(531, QuranMetadata.startPageForSurah(55))
        assertEquals(562, QuranMetadata.startPageForSurah(67))
        assertEquals(604, QuranMetadata.startPageForSurah(114))

        assertEquals(1, QuranMetadata.startPageForJuz(1))
        assertEquals(582, QuranMetadata.startPageForJuz(30))
    }

    @Test
    fun `arabic indic numerals format multi digit numbers correctly`() {
        assertEquals("١", QuranMetadata.toArabicIndic(1))
        assertEquals("٢٥٥", QuranMetadata.toArabicIndic(255))
        assertEquals("٦٠٤", QuranMetadata.toArabicIndic(604))
    }

    @Test
    fun `manzil calculation maps all seven manzils accurately`() {
        assertEquals(1, QuranMetadata.manzilForSurah(1))
        assertEquals(1, QuranMetadata.manzilForSurah(4))
        assertEquals(2, QuranMetadata.manzilForSurah(5))
        assertEquals(3, QuranMetadata.manzilForSurah(10))
        assertEquals(4, QuranMetadata.manzilForSurah(17))
        assertEquals(5, QuranMetadata.manzilForSurah(26))
        assertEquals(6, QuranMetadata.manzilForSurah(37))
        assertEquals(7, QuranMetadata.manzilForSurah(50))
        assertEquals(7, QuranMetadata.manzilForSurah(114))
    }

    @Test
    fun `basmalah prefix strips only on surahs other than fatihah and tawbah ayah 1`() {
        val sampleFatihah = "بِسْمِ ٱللَّهِ ٱلرَّحْمَٰنِ ٱلرَّحِيمِ"
        assertEquals(
            sampleFatihah,
            QuranMetadata.stripLeadingBasmalahIfPresent(sampleFatihah, 1, 1)
        )
        val sampleIkhlas = "بِسْمِ ٱللَّهِ ٱلرَّحْمَٰنِ ٱلرَّحِيمِ قُلْ هُوَ ٱللَّهُ أَحَدٌ"
        assertEquals(
            "قُلْ هُوَ ٱللَّهُ أَحَدٌ",
            QuranMetadata.stripLeadingBasmalahIfPresent(sampleIkhlas, 112, 1)
        )
    }

    @Test
    fun `mushaf editions fonts and layout defaults are consistent`() {
        assertEquals(5, MushafEdition.entries.size)
        assertEquals(MushafEdition.HAFIZI, MushafEdition.fromKey(null))
        assertEquals(MushafEdition.TAJWEED, MushafEdition.fromKey("TAJWEED"))
        assertEquals(QuranLayoutStyle.HAFIZI_PAGE, QuranLayoutStyle.fromKey(null))
        assertEquals(ArabicFontChoice.LATEEF, ArabicFontChoice.fromKey(null))
        assertEquals(ArabicFontChoice.GULZAR, ArabicFontChoice.fromKey("GULZAR"))
        assertEquals(ArabicFontChoice.NOTO_NASTALIQ, ArabicFontChoice.fromKey("NOTO_NASTALIQ"))
        assertTrue(QuranMetadata.topics.size >= 8)
    }

    @Test
    fun `bengali quran metadata covers all 114 surahs 30 juz and quick links`() {
        assertEquals("১", QuranBengaliData.toBengaliDigits(1))
        assertEquals("১১৪", QuranBengaliData.toBengaliDigits(114))
        assertEquals("৬০৪", QuranBengaliData.toBengaliDigits(604))

        for (surahNum in 1..114) {
            val info = QuranBengaliData.getSurahInfo(surahNum)
            assertEquals(surahNum, info.number)
            assertTrue("Surah $surahNum nameBn blank", info.nameBn.isNotBlank())
            assertTrue("Surah $surahNum meaningBn blank", info.meaningBn.isNotBlank())
        }

        for (juzNum in 1..30) {
            val juzInfo = QuranBengaliData.getJuzInfo(juzNum)
            assertEquals(juzNum, juzInfo.number)
            assertTrue("Juz $juzNum nameBn blank", juzInfo.nameBn.isNotBlank())
            assertTrue("Juz $juzNum arabicShort blank", juzInfo.arabicShort.isNotBlank())
        }

        assertTrue(QuranBengaliData.quickLinks.size >= 4)
        assertTrue(QuranBengaliData.quickLinks.any { it.labelBn == "আয়াতুল কুরসী" && it.surahNumber == 2 && it.ayahNumber == 255 })
        QuranMetadata.topics.forEach { topic ->
            assertTrue("Missing Bengali topic label for ${topic.id}", QuranBengaliData.topicLabelsBn.containsKey(topic.id))
        }
    }

    @Test
    fun `hafizi 611 page mapping covers all 604 madani pages monotonically and cleanly`() {
        assertEquals(611, QuranMetadata.TOTAL_HAFIZI_PAGES)
        assertEquals(604, QuranMetadata.TOTAL_MADANI_PAGES)
        assertEquals(611, QuranMetadata.totalPagesForEdition(MushafEdition.HAFIZI))
        assertEquals(611, QuranMetadata.totalPagesForEdition(MushafEdition.INDO_PAK))
        assertEquals(604, QuranMetadata.totalPagesForEdition(MushafEdition.MADANI))
        assertEquals(604, QuranMetadata.totalPagesForEdition(MushafEdition.TAJWEED))
        assertEquals(604, QuranMetadata.totalPagesForEdition(MushafEdition.UTHMANI))

        assertEquals("٦١١", QuranMetadata.toArabicIndic(611))
        assertEquals("৬১১", QuranBengaliData.toBengaliDigits(611))

        val coveredMadaniPages = mutableSetOf<Int>()
        var prevMadani = 1
        for (p in 1..QuranMetadata.TOTAL_HAFIZI_PAGES) {
            val slice = QuranMetadata.resolveHafiziPageSlice(p)
            assertTrue("Madani page out of order at Hafizi p=$p", slice.madaniPage >= prevMadani)
            assertTrue("Madani page out of bounds at p=$p", slice.madaniPage in 1..604)
            coveredMadaniPages.add(slice.madaniPage)
            prevMadani = slice.madaniPage
        }
        assertEquals((1..604).toSet(), coveredMadaniPages)
        assertEquals(1, QuranMetadata.resolveHafiziPageSlice(1).madaniPage)
        assertEquals(604, QuranMetadata.resolveHafiziPageSlice(611).madaniPage)

        assertEquals(1, QuranMetadata.startPageForSurah(1, MushafEdition.HAFIZI))
        assertEquals(582, QuranMetadata.startPageForJuz(30, MushafEdition.HAFIZI))
        assertEquals(611, QuranMetadata.startPageForSurah(112, MushafEdition.HAFIZI))
        assertEquals(611, QuranMetadata.startPageForSurah(114, MushafEdition.HAFIZI))
        assertEquals(604, QuranMetadata.startPageForSurah(114, MushafEdition.MADANI))
    }

    @Test
    fun `hafizi multi surah page slicing splits cleanly without item loss or overlap`() {
        // Test splitting on a split Juz-30 Madani page (587 has Surahs 82 & 83)
        val sampleItems = listOf(82 to 1, 82 to 2, 83 to 1, 83 to 2)
        val firstHafizi = QuranMetadata.madaniToHafiziStartPage(587)
        val secondHafizi = firstHafizi + 1

        val part0 = QuranMetadata.sliceItemsForHafiziPage(firstHafizi, sampleItems) { it.first }
        val part1 = QuranMetadata.sliceItemsForHafiziPage(secondHafizi, sampleItems) { it.first }

        assertEquals(listOf(82 to 1, 82 to 2), part0)
        assertEquals(listOf(83 to 1, 83 to 2), part1)
        assertEquals(sampleItems, part0 + part1)
    }

    @Test
    fun `mushaf selection categories cover all editions with valid bengali labels`() {
        val categories = com.example.ui.screens.defaultBanglaMushafCategories
        assertEquals(6, categories.size)
        categories.forEach { category ->
            assertTrue("Blank titleBn for ${category.bookType}", category.titleBn.isNotBlank())
            assertTrue("Blank badgeBn for ${category.bookType}", category.badgeBn.isNotBlank())
        }
        assertEquals(
            MushafEdition.entries.toSet(),
            categories.map { it.edition }.toSet()
        )
        assertTrue(categories.any { it.layout == QuranLayoutStyle.AYAH_BY_AYAH && it.showTranslation })
    }
}


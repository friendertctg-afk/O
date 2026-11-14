package com.example

import com.example.data.local.MushafEdition
import com.example.data.local.QuranLayoutStyle
import com.example.data.seed.QuranMetadata
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
    fun `mushaf editions and layout defaults are consistent`() {
        assertEquals(5, MushafEdition.entries.size)
        assertEquals(MushafEdition.HAFIZI, MushafEdition.fromKey(null))
        assertEquals(MushafEdition.TAJWEED, MushafEdition.fromKey("TAJWEED"))
        assertEquals(QuranLayoutStyle.HAFIZI_PAGE, QuranLayoutStyle.fromKey(null))
        assertTrue(QuranMetadata.topics.size >= 8)
    }
}

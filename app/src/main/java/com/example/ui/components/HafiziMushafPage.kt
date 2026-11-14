package com.example.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.audio.AudioPlaybackState
import com.example.data.local.UserPreferencesState
import com.example.data.repository.AyahDisplayItem
import com.example.data.seed.QuranMetadata

/**
 * Traditional South Asian (611-page Emdadia / Kolkata) 15-Line Hafizi Mushaf Page component.
 *
 * Layout specifications:
 * - Outer Top Header (above ornate frame):
 *   - Top-Right: Juz / Para name & Arabic numeral (e.g., الم ١)
 *   - Top-Center: Page number in Eastern Arabic numerals (e.g., ٣ .. ٦١١)
 *   - Top-Left: Surah name & number (e.g., البقرة ٢)
 * - Intricate Islamic arabesque ornate border enclosing a 15-line horizontal ruled grid
 *   with justified RTL Quranic text and circular verse-end markers.
 * - Outer Margins (outside frame): Ruku ('ع') and pause ('وقف لازم' / 'سجدة') annotations.
 * - Outer Bottom Footer (below ornate frame): Centered Manzil indicator (e.g., منزل ١)
 *   with RTL page-turn and bookmark controls.
 */
@Composable
fun HafiziMushafPage(
    pageNumber: Int,
    ayahs: List<AyahDisplayItem>,
    prefs: UserPreferencesState,
    zoomScale: Float = 1.0f,
    isCompactSpread: Boolean = false,
    isPageBookmarked: Boolean = false,
    audioState: AudioPlaybackState = AudioPlaybackState(),
    totalPages: Int = QuranMetadata.totalPagesForEdition(prefs.mushafEdition),
    onRetry: () -> Unit = {},
    onToggleBookmark: (String) -> Unit = {},
    onAyahTapped: (AyahDisplayItem) -> Unit = {},
    onPrevPage: () -> Unit = {},
    onNextPage: () -> Unit = {},
    onToggleControls: () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    HafiziKolkata15LinePageView(
        pageNumber = pageNumber,
        ayahs = ayahs,
        prefs = prefs,
        zoomScale = zoomScale,
        isCompactSpread = isCompactSpread,
        isPageBookmarked = isPageBookmarked,
        audioState = audioState,
        totalPages = totalPages,
        onRetry = onRetry,
        onToggleBookmark = onToggleBookmark,
        onAyahTapped = onAyahTapped,
        onPrevPage = onPrevPage,
        onNextPage = onNextPage,
        onToggleControls = onToggleControls,
        modifier = modifier,
    )
}

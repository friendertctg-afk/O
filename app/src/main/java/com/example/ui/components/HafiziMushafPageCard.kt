package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.calculateZoom
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withLink
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.audio.AudioPlaybackState
import com.example.data.local.MushafEdition
import com.example.data.local.PageBookmarkEntity
import com.example.data.local.QuranScriptType
import com.example.data.local.UserPreferencesState
import com.example.data.repository.AyahDisplayItem
import com.example.data.seed.QuranMetadata
import com.example.ui.theme.QuranReadingMode
import com.example.ui.theme.toFontFamily
import com.example.utils.TajweedParser
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

@Composable
fun HafiziMushafReaderArea(
    currentPage: Int,
    prefs: UserPreferencesState,
    zoomScale: Float,
    pageBookmarks: List<PageBookmarkEntity>,
    audioState: AudioPlaybackState,
    observePage: (Int) -> Flow<List<AyahDisplayItem>>,
    onEnsurePageLoaded: (Int) -> Unit,
    onPageChanged: (Int, Int?, Int?) -> Unit,
    onZoomDelta: (Float) -> Unit,
    onTogglePageBookmark: (Int, String) -> Unit,
    onAyahTapped: (AyahDisplayItem) -> Unit,
    onToggleControlsVisibility: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val coroutineScope = rememberCoroutineScope()

    BoxWithConstraints(modifier = modifier.fillMaxSize()) {
        val isCompactSpreadWidth = maxWidth < 700.dp
        val isTwoPageSpread = when (prefs.twoPageSpreadPref) {
            1 -> false
            2 -> true
            else -> !isCompactSpreadWidth
        }
        val totalMushafPages = QuranMetadata.totalPagesForEdition(prefs.mushafEdition)
        val pageCount = if (isTwoPageSpread) (totalMushafPages + 1) / 2 else totalMushafPages
        val initialIndex = if (isTwoPageSpread) {
            ((currentPage - 1) / 2).coerceIn(0, pageCount - 1)
        } else {
            (currentPage - 1).coerceIn(0, pageCount - 1)
        }

        val pagerState = rememberPagerState(
            initialPage = initialIndex,
            pageCount = { pageCount }
        )

        LaunchedEffect(currentPage, isTwoPageSpread, totalMushafPages) {
            val targetIdx = if (isTwoPageSpread) {
                ((currentPage - 1) / 2).coerceIn(0, pageCount - 1)
            } else {
                (currentPage - 1).coerceIn(0, pageCount - 1)
            }
            if (pagerState.currentPage != targetIdx) {
                pagerState.animateScrollToPage(targetIdx)
            }
        }

        LaunchedEffect(pagerState.settledPage, isTwoPageSpread, totalMushafPages) {
            val resolvedPage = if (isTwoPageSpread) pagerState.settledPage * 2 + 1 else pagerState.settledPage + 1
            onEnsurePageLoaded(resolvedPage)
            if (isTwoPageSpread && resolvedPage + 1 <= totalMushafPages) {
                onEnsurePageLoaded(resolvedPage + 1)
            }
            if (resolvedPage != currentPage) {
                onPageChanged(resolvedPage, null, null)
            }
        }

        // Multi-touch only pinch-to-zoom so 1-finger RTL horizontal paging stays unaffected
        val pinchModifier = Modifier.pointerInput(Unit) {
            awaitEachGesture {
                awaitFirstDown(requireUnconsumed = false)
                do {
                    val event = awaitPointerEvent()
                    val pressedCount = event.changes.count { it.pressed }
                    if (pressedCount >= 2) {
                        val zoomChange = event.calculateZoom()
                        if (kotlin.math.abs(zoomChange - 1f) > 0.01f) {
                            onZoomDelta(zoomChange)
                            event.changes.forEach { it.consume() }
                        }
                    }
                } while (event.changes.any { it.pressed })
            }
        }

        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
            HorizontalPager(
                state = pagerState,
                beyondViewportPageCount = 1,
                modifier = Modifier
                    .fillMaxSize()
                    .then(pinchModifier)
                    .testTag("mushaf_horizontal_pager")
            ) { pageIdx ->
                if (isTwoPageSpread) {
                    val rightPageNum = pageIdx * 2 + 1
                    val hasLeftPage = rightPageNum + 1 <= totalMushafPages
                    val leftPageNum = (rightPageNum + 1).coerceAtMost(totalMushafPages)
                    val rightAyahs by observePage(rightPageNum).collectAsStateWithLifecycle(initialValue = emptyList())
                    val leftAyahs by observePage(leftPageNum).collectAsStateWithLifecycle(initialValue = emptyList())

                    LaunchedEffect(rightPageNum) {
                        onEnsurePageLoaded(rightPageNum)
                        if (hasLeftPage) onEnsurePageLoaded(leftPageNum)
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 6.dp, vertical = 4.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        SingleMushafPageView(
                            pageNumber = rightPageNum,
                            ayahs = rightAyahs,
                            prefs = prefs,
                            zoomScale = zoomScale,
                            isCompactSpread = isCompactSpreadWidth,
                            isPageBookmarked = pageBookmarks.any { it.pageNumber == rightPageNum },
                            audioState = audioState,
                            onRetry = { onEnsurePageLoaded(rightPageNum) },
                            onToggleBookmark = { label -> onTogglePageBookmark(rightPageNum, label) },
                            onAyahTapped = onAyahTapped,
                            onPrevPage = {
                                if (pagerState.currentPage > 0) {
                                    coroutineScope.launch { pagerState.animateScrollToPage(pagerState.currentPage - 1) }
                                }
                            },
                            onNextPage = {
                                if (pagerState.currentPage < pageCount - 1) {
                                    coroutineScope.launch { pagerState.animateScrollToPage(pagerState.currentPage + 1) }
                                }
                            },
                            onToggleControls = onToggleControlsVisibility,
                            modifier = Modifier.weight(1f).fillMaxHeight()
                        )
                        if (hasLeftPage) {
                            SingleMushafPageView(
                                pageNumber = leftPageNum,
                                ayahs = leftAyahs,
                                prefs = prefs,
                                zoomScale = zoomScale,
                                isCompactSpread = isCompactSpreadWidth,
                                isPageBookmarked = pageBookmarks.any { it.pageNumber == leftPageNum },
                                audioState = audioState,
                                onRetry = { onEnsurePageLoaded(leftPageNum) },
                                onToggleBookmark = { label -> onTogglePageBookmark(leftPageNum, label) },
                                onAyahTapped = onAyahTapped,
                                onPrevPage = {
                                    if (pagerState.currentPage > 0) {
                                        coroutineScope.launch { pagerState.animateScrollToPage(pagerState.currentPage - 1) }
                                    }
                                },
                                onNextPage = {
                                    if (pagerState.currentPage < pageCount - 1) {
                                        coroutineScope.launch { pagerState.animateScrollToPage(pagerState.currentPage + 1) }
                                    }
                                },
                                onToggleControls = onToggleControlsVisibility,
                                modifier = Modifier.weight(1f).fillMaxHeight()
                            )
                        } else {
                            Spacer(modifier = Modifier.weight(1f).fillMaxHeight())
                        }
                    }
                } else {
                    val pageNum = pageIdx + 1
                    val pageAyahs by observePage(pageNum).collectAsStateWithLifecycle(initialValue = emptyList())
                    LaunchedEffect(pageNum) {
                        onEnsurePageLoaded(pageNum)
                    }

                    val effectiveSideMargin = if (
                        prefs.mushafEdition == MushafEdition.HAFIZI ||
                        prefs.mushafEdition == MushafEdition.INDO_PAK
                    ) {
                        prefs.readerPageMarginDp.coerceAtMost(8f).dp
                    } else {
                        prefs.readerPageMarginDp.dp
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = effectiveSideMargin, vertical = 4.dp),
                        contentAlignment = Alignment.TopCenter
                    ) {
                        SingleMushafPageView(
                            pageNumber = pageNum,
                            ayahs = pageAyahs,
                            prefs = prefs,
                            zoomScale = zoomScale,
                            isCompactSpread = false,
                            isPageBookmarked = pageBookmarks.any { it.pageNumber == pageNum },
                            audioState = audioState,
                            onRetry = { onEnsurePageLoaded(pageNum) },
                            onToggleBookmark = { label -> onTogglePageBookmark(pageNum, label) },
                            onAyahTapped = onAyahTapped,
                            onPrevPage = {
                                if (pageIdx > 0) {
                                    coroutineScope.launch { pagerState.animateScrollToPage(pageIdx - 1) }
                                }
                            },
                            onNextPage = {
                                if (pageIdx < pageCount - 1) {
                                    coroutineScope.launch { pagerState.animateScrollToPage(pageIdx + 1) }
                                }
                            },
                            onToggleControls = onToggleControlsVisibility,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SingleMushafPageView(
    pageNumber: Int,
    ayahs: List<AyahDisplayItem>,
    prefs: UserPreferencesState,
    zoomScale: Float,
    isCompactSpread: Boolean,
    isPageBookmarked: Boolean,
    audioState: AudioPlaybackState,
    onRetry: () -> Unit,
    onToggleBookmark: (String) -> Unit,
    onAyahTapped: (AyahDisplayItem) -> Unit,
    onPrevPage: () -> Unit,
    onNextPage: () -> Unit,
    onToggleControls: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val edition = prefs.mushafEdition
    val totalMushafPages = QuranMetadata.totalPagesForEdition(edition)
    if (edition == MushafEdition.HAFIZI || edition == MushafEdition.INDO_PAK) {
        HafiziMushafPage(
            pageNumber = pageNumber,
            ayahs = ayahs,
            prefs = prefs,
            zoomScale = zoomScale,
            isCompactSpread = isCompactSpread,
            isPageBookmarked = isPageBookmarked,
            audioState = audioState,
            totalPages = QuranMetadata.totalPagesForEdition(edition),
            onRetry = onRetry,
            onToggleBookmark = onToggleBookmark,
            onAyahTapped = onAyahTapped,
            onPrevPage = onPrevPage,
            onNextPage = onNextPage,
            onToggleControls = onToggleControls,
            modifier = modifier
        )
        return
    }

    val arabicFont = prefs.arabicFont.toFontFamily()
    val effectiveScript = when (edition) {
        MushafEdition.TAJWEED -> QuranScriptType.TAJWEED
        else -> prefs.quranScript
    }

    // Authentic paper tones calibrated per Mushaf edition & reading mode
    val (paperBg, inkColor, outerFrameColor, goldAccent, surahHeaderBg) = when (prefs.readingMode) {
        QuranReadingMode.DARK -> listOf(
            Color(0xFF0E1512),
            Color(0xFFF3EFE2),
            Color(0xFF28735A),
            Color(0xFFDAB44C),
            Color(0xFF152921),
        )
        QuranReadingMode.SEPIA -> listOf(
            Color(0xFFF7EEDB),
            Color(0xFF271E12),
            Color(0xFF185643),
            Color(0xFFB58618),
            Color(0xFFEBE0C8),
        )
        QuranReadingMode.COMFORT -> listOf(
            Color(0xFFEBF4EE),
            Color(0xFF10241B),
            Color(0xFF0E533F),
            Color(0xFFC29622),
            Color(0xFFDAEBE1),
        )
        QuranReadingMode.LIGHT -> when (edition) {
            MushafEdition.HAFIZI -> listOf(
                Color(0xFFFDF7E6), // Classic cream Hafizi paper
                Color(0xFF14110B), // Rich deep ink
                Color(0xFF0B4E3B), // Deep emerald border
                Color(0xFFC59B27), // Royal gold inner filigree
                Color(0xFFF0E5C7),
            )
            MushafEdition.MADANI -> listOf(
                Color(0xFFFCFBF7),
                Color(0xFF121815),
                Color(0xFF125A44),
                Color(0xFFC59B27),
                Color(0xFFEBF2ED),
            )
            else -> listOf(
                Color(0xFFFCF9F0),
                Color(0xFF141A16),
                Color(0xFF0D4A3A),
                Color(0xFFC89D28),
                Color(0xFFEDE8D7),
            )
        }
    }

    val firstAyah = ayahs.firstOrNull()
    val primarySurah = firstAyah?.let { QuranMetadata.getSurah(it.surahNumber) }
    val juzNum = firstAyah?.juz ?: ((pageNumber - 1) / 20 + 1).coerceIn(1, 30)
    val rukuSet = remember(ayahs) { ayahs.map { it.ruku }.distinct() }
    val rukuLabel = when {
        rukuSet.isEmpty() -> ""
        rukuSet.size == 1 -> "Ruku ${rukuSet.first()}"
        else -> "Ruku ${rukuSet.first()}-${rukuSet.last()}"
    }

    // Never make Arabic illegibly tiny in spread mode; scale thoughtfully with a 15sp floor
    val baseFontSize = if (isCompactSpread) {
        (prefs.arabicFontSizeSp * 0.72f * zoomScale).coerceIn(15f, 34f)
    } else {
        (prefs.arabicFontSizeSp * zoomScale).coerceIn(18f, 48f)
    }
    val lineHeightSp = (baseFontSize * if (edition == MushafEdition.HAFIZI) 1.92f else 1.82f)

    var showPageTranslations by remember { mutableStateOf(false) }

    Surface(
        modifier = modifier
            .fillMaxSize()
            .testTag("mushaf_page_$pageNumber"),
        color = paperBg,
        shape = RoundedCornerShape(14.dp),
        tonalElevation = 2.dp,
        shadowElevation = 2.dp,
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // Ornamental Mushaf Double Border & Corner Rosettes
            MushafOrnamentalFrameCanvas(
                outerColor = outerFrameColor,
                goldColor = goldAccent,
                modifier = Modifier.matchParentSize()
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = if (isCompactSpread) 12.dp else 18.dp, vertical = 14.dp)
            ) {
                // Top Mushaf Running Header (Surah | Edition | Juz & Ruku)
                CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onToggleControls() }
                            .padding(horizontal = 4.dp, vertical = 2.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Juz $juzNum • جزء ${QuranMetadata.toArabicIndic(juzNum)}",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = outerFrameColor,
                                maxLines = 1
                            )
                            if (rukuLabel.isNotBlank()) {
                                Text(
                                    text = rukuLabel,
                                    style = MaterialTheme.typography.labelSmall,
                                    fontSize = 10.sp,
                                    color = inkColor.copy(alpha = 0.68f),
                                    maxLines = 1
                                )
                            }
                        }

                        if (!isCompactSpread) {
                            Surface(
                                color = outerFrameColor.copy(alpha = 0.12f),
                                border = BorderStroke(0.75.dp, goldAccent.copy(alpha = 0.7f)),
                                shape = RoundedCornerShape(20.dp)
                            ) {
                                Text(
                                    text = edition.badgeText,
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.SemiBold,
                                    color = outerFrameColor,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 3.dp)
                                )
                            }
                        }

                        Column(
                            modifier = Modifier.weight(1f),
                            horizontalAlignment = Alignment.End
                        ) {
                            Text(
                                text = primarySurah?.let { "سورة ${it.arabicName}" } ?: "القرآن الكريم",
                                fontFamily = arabicFont,
                                fontSize = if (isCompactSpread) 13.sp else 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = outerFrameColor,
                                maxLines = 1
                            )
                            Text(
                                text = primarySurah?.englishName ?: "Mushaf Page $pageNumber",
                                style = MaterialTheme.typography.labelSmall,
                                fontSize = 10.sp,
                                color = inkColor.copy(alpha = 0.68f),
                                maxLines = 1
                            )
                        }
                    }
                }

                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 4.dp),
                    thickness = 1.dp,
                    color = goldAccent.copy(alpha = 0.55f)
                )

                // Main Quranic Text Body
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                ) {
                    if (ayahs.isEmpty()) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            CircularProgressIndicator(color = outerFrameColor, modifier = Modifier.size(30.dp))
                            Spacer(Modifier.height(10.dp))
                            Text(
                                "Loading Mushaf Page $pageNumber...",
                                style = MaterialTheme.typography.bodyMedium,
                                color = inkColor,
                                textAlign = TextAlign.Center
                            )
                            Spacer(Modifier.height(8.dp))
                            Button(onClick = onRetry) {
                                Text("Fetch Page")
                            }
                        }
                    } else {
                        val scrollState = rememberScrollState()
                        val groupedBySurah = remember(ayahs) { ayahs.groupBy { it.surahNumber } }

                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .verticalScroll(scrollState)
                                .padding(vertical = 4.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            groupedBySurah.forEach { (sNum, surahAyahsOnPage) ->
                                val sMeta = QuranMetadata.getSurah(sNum)
                                val startsHere = surahAyahsOnPage.any { it.ayahNumber == 1 }

                                if (startsHere) {
                                    OrnamentalSurahHeaderCartouche(
                                        surahNameAr = sMeta.arabicName,
                                        surahNameEn = "${sMeta.number}. ${sMeta.englishName}",
                                        metaLine = "${sMeta.revelationType} • ${sMeta.ayahCount} Ayahs",
                                        bg = surahHeaderBg,
                                        borderColor = goldAccent,
                                        accentColor = outerFrameColor,
                                        textColor = inkColor,
                                        arabicFont = arabicFont,
                                        compact = isCompactSpread,
                                    )
                                    if (sNum != 1 && sNum != 9) {
                                        Text(
                                            text = "بِسْمِ ٱللَّهِ ٱلرَّحْمَٰنِ ٱلرَّحِيمِ",
                                            fontFamily = arabicFont,
                                            fontSize = (baseFontSize * 0.92f).sp,
                                            color = outerFrameColor,
                                            textAlign = TextAlign.Center,
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(vertical = 2.dp)
                                        )
                                    }
                                }

                                // Authentic Justified Arabic Mushaf Flow with clickable Ayah spans + audio sync highlight
                                val highlightBg = outerFrameColor.copy(alpha = 0.18f)
                                val verseText = remember(
                                    surahAyahsOnPage,
                                    effectiveScript,
                                    inkColor,
                                    goldAccent,
                                    audioState.isPlaying,
                                    audioState.surahNumber,
                                    audioState.ayahNumber,
                                    baseFontSize
                                ) {
                                    buildAnnotatedString {
                                        surahAyahsOnPage.forEachIndexed { idx, ayah ->
                                            val isPlayingThisAyah = (audioState.isPlaying || audioState.isBuffering) &&
                                                audioState.surahNumber == ayah.surahNumber &&
                                                audioState.ayahNumber == ayah.ayahNumber

                                            val raw = when (effectiveScript) {
                                                QuranScriptType.INDO_PAK -> ayah.simpleText.ifBlank { ayah.uthmaniText }
                                                QuranScriptType.TAJWEED -> ayah.tajweedMarkup.ifBlank { ayah.uthmaniText }
                                                QuranScriptType.UTHMANI -> ayah.uthmaniText
                                            }

                                            val link = LinkAnnotation.Clickable(
                                                tag = "ayah_${ayah.surahNumber}_${ayah.ayahNumber}",
                                                styles = TextLinkStyles(
                                                    style = SpanStyle(
                                                        background = if (isPlayingThisAyah) highlightBg else Color.Transparent
                                                    )
                                                ),
                                                linkInteractionListener = { onAyahTapped(ayah) }
                                            )

                                            withLink(link) {
                                                if (effectiveScript == QuranScriptType.TAJWEED) {
                                                    val parsed = TajweedParser.parseTajweedAnnotated(raw, inkColor)
                                                    if (isPlayingThisAyah) {
                                                        withStyle(SpanStyle(background = highlightBg)) {
                                                            append(parsed)
                                                        }
                                                    } else {
                                                        append(parsed)
                                                    }
                                                } else {
                                                    withStyle(
                                                        SpanStyle(
                                                            color = inkColor,
                                                            background = if (isPlayingThisAyah) highlightBg else Color.Transparent
                                                        )
                                                    ) {
                                                        append(raw)
                                                    }
                                                }
                                                withStyle(
                                                    SpanStyle(
                                                        color = goldAccent,
                                                        fontSize = (baseFontSize * 0.82f).sp,
                                                        fontWeight = FontWeight.Bold,
                                                        background = if (isPlayingThisAyah) highlightBg else Color.Transparent
                                                    )
                                                ) {
                                                    append(" ﴿${QuranMetadata.toArabicIndic(ayah.ayahNumber)}﴾")
                                                }
                                            }
                                            if (idx < surahAyahsOnPage.lastIndex) {
                                                append(" ")
                                            }
                                        }
                                    }
                                }

                                CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                                    Text(
                                        text = verseText,
                                        fontFamily = arabicFont,
                                        fontSize = baseFontSize.sp,
                                        lineHeight = lineHeightSp.sp,
                                        textAlign = TextAlign.Justify,
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                }
                            }

                            // Optional expandable page translation beneath the authentic Mushaf text
                            if (prefs.showTranslation && !isCompactSpread) {
                                HorizontalDivider(color = goldAccent.copy(alpha = 0.3f))
                                Surface(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(10.dp))
                                        .clickable { showPageTranslations = !showPageTranslations },
                                    color = surahHeaderBg.copy(alpha = 0.7f)
                                ) {
                                    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(horizontal = 10.dp, vertical = 6.dp),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                "Page $pageNumber Translations (${ayahs.size} Ayahs)",
                                                style = MaterialTheme.typography.labelMedium,
                                                fontWeight = FontWeight.SemiBold,
                                                color = outerFrameColor
                                            )
                                            Icon(
                                                if (showPageTranslations) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore,
                                                contentDescription = "Toggle page translations",
                                                tint = outerFrameColor
                                            )
                                        }
                                    }
                                }
                                if (showPageTranslations) {
                                    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                            ayahs.forEach { a ->
                                                if (a.translationText.isNotBlank()) {
                                                    Text(
                                                        text = "(${a.surahNumber}:${a.ayahNumber}) ${a.translationText}",
                                                        style = MaterialTheme.typography.bodySmall,
                                                        color = inkColor.copy(alpha = 0.88f),
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                HorizontalDivider(
                    modifier = Modifier.padding(top = 4.dp),
                    thickness = 1.dp,
                    color = goldAccent.copy(alpha = 0.55f)
                )

                // Bottom Mushaf Footer (Prev/Next + Page Medallion + Page Bookmark)
                CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(
                                onClick = onNextPage,
                                enabled = pageNumber < totalMushafPages,
                                modifier = Modifier.size(36.dp).testTag("next_page_btn_$pageNumber")
                            ) {
                                Icon(
                                    Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                                    contentDescription = "Next Mushaf Page (RTL)",
                                    tint = outerFrameColor
                                )
                            }
                            Text(
                                text = "Next",
                                style = MaterialTheme.typography.labelSmall,
                                color = outerFrameColor.copy(alpha = 0.78f)
                            )
                        }

                        // Center Ornamental Page Medallion
                        Surface(
                            color = surahHeaderBg,
                            border = BorderStroke(1.2.dp, goldAccent),
                            shape = RoundedCornerShape(22.dp),
                        ) {
                            Text(
                                text = "صفحة ${QuranMetadata.toArabicIndic(pageNumber)} • $pageNumber / $totalMushafPages",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = outerFrameColor,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                            )
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(
                                onClick = {
                                    val label = primarySurah?.let { "${it.englishName} (Juz $juzNum)" }
                                        ?: "Mushaf Page $pageNumber"
                                    onToggleBookmark(label)
                                },
                                modifier = Modifier.size(36.dp).testTag("bookmark_page_$pageNumber")
                            ) {
                                Icon(
                                    if (isPageBookmarked) Icons.Filled.Bookmark else Icons.Filled.BookmarkBorder,
                                    contentDescription = "Bookmark Mushaf Page $pageNumber",
                                    tint = if (isPageBookmarked) goldAccent else outerFrameColor
                                )
                            }
                            IconButton(
                                onClick = onPrevPage,
                                enabled = pageNumber > 1,
                                modifier = Modifier.size(36.dp).testTag("prev_page_btn_$pageNumber")
                            ) {
                                Icon(
                                    Icons.AutoMirrored.Filled.KeyboardArrowRight,
                                    contentDescription = "Previous Mushaf Page (RTL)",
                                    tint = outerFrameColor
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun OrnamentalSurahHeaderCartouche(
    surahNameAr: String,
    surahNameEn: String,
    metaLine: String,
    bg: Color,
    borderColor: Color,
    accentColor: Color,
    textColor: Color,
    arabicFont: FontFamily,
    compact: Boolean,
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = bg,
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.25.dp, borderColor),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.horizontalGradient(
                        listOf(
                            accentColor.copy(alpha = 0.08f),
                            borderColor.copy(alpha = 0.14f),
                            accentColor.copy(alpha = 0.08f),
                        )
                    )
                )
                .padding(horizontal = 12.dp, vertical = if (compact) 5.dp else 8.dp)
        ) {
            CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = surahNameEn,
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold,
                            color = accentColor,
                            maxLines = 1
                        )
                        Text(
                            text = metaLine,
                            style = MaterialTheme.typography.labelSmall,
                            color = textColor.copy(alpha = 0.72f),
                            maxLines = 1
                        )
                    }
                    Text(
                        text = "سُورَةُ $surahNameAr",
                        fontFamily = arabicFont,
                        fontSize = if (compact) 17.sp else 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = accentColor
                    )
                }
            }
        }
    }
}

@Composable
private fun MushafOrnamentalFrameCanvas(
    outerColor: Color,
    goldColor: Color,
    modifier: Modifier = Modifier,
) {
    Canvas(modifier = modifier) {
        val pad1 = 4.dp.toPx()
        val pad2 = 8.dp.toPx()
        val rOuter = 12.dp.toPx()
        val rInner = 9.dp.toPx()

        // Outer Emerald Border
        drawRoundRect(
            color = outerColor.copy(alpha = 0.85f),
            topLeft = Offset(pad1, pad1),
            size = Size(size.width - pad1 * 2, size.height - pad1 * 2),
            cornerRadius = CornerRadius(rOuter, rOuter),
            style = Stroke(width = 2.dp.toPx())
        )

        // Inner Royal Gold Hairline
        drawRoundRect(
            color = goldColor.copy(alpha = 0.85f),
            topLeft = Offset(pad2, pad2),
            size = Size(size.width - pad2 * 2, size.height - pad2 * 2),
            cornerRadius = CornerRadius(rInner, rInner),
            style = Stroke(width = 1.1.dp.toPx())
        )

        // Four Ornamental Corner Diamond Flourishes
        val dSize = 7.dp.toPx()
        val corners = listOf(
            Offset(pad2, pad2),
            Offset(size.width - pad2, pad2),
            Offset(pad2, size.height - pad2),
            Offset(size.width - pad2, size.height - pad2),
        )
        corners.forEach { c ->
            val diamond = Path().apply {
                moveTo(c.x, c.y - dSize)
                lineTo(c.x + dSize, c.y)
                lineTo(c.x, c.y + dSize)
                lineTo(c.x - dSize, c.y)
                close()
            }
            drawPath(diamond, color = goldColor.copy(alpha = 0.85f))
            drawCircle(color = outerColor, radius = dSize * 0.35f, center = c)
        }
    }
}

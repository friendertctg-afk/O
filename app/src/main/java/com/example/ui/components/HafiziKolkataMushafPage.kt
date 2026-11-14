package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.audio.AudioPlaybackState
import com.example.data.local.QuranScriptType
import com.example.data.local.UserPreferencesState
import com.example.data.repository.AyahDisplayItem
import com.example.data.seed.QuranMetadata
import com.example.data.seed.SurahMeta
import com.example.ui.theme.ArabicFontChoice
import com.example.ui.theme.LateefFontFamily
import com.example.ui.theme.QuranReadingMode
import com.example.ui.theme.toFontFamily
import com.example.utils.TajweedParser
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

private const val HAFIZI_LINES_PER_PAGE = 15

private val WAQF_CODEPOINTS = setOf(
    '\u06D6', // Small High Ligature Sad with Lam with Alef Maksura
    '\u06D7', // Small High Ligature Qaf with Lam with Alef Maksura
    '\u06D8', // Small High Meem Initial Form (Waqf Lazim)
    '\u06D9', // Small High Lam Alef (La Waqf)
    '\u06DA', // Small High Jeem (Waqf Jaiz)
    '\u06DB', // Small High Three Dots (Mu'anaqah)
    '\u06DC', // Small High Seen (Saktah)
    '\u06DE', // Start of Rub el Hizb
    '\u06E9', // Place of Sajdah
)

private sealed interface MushafLineToken {
    val ayah: AyahDisplayItem
    val hasWaqfLazim: Boolean
    val hasSaktah: Boolean
    val hasSajdah: Boolean
    val hasRubElHizb: Boolean

    data class Word(
        val text: AnnotatedString,
        override val ayah: AyahDisplayItem,
        override val hasWaqfLazim: Boolean = false,
        override val hasSaktah: Boolean = false,
        override val hasSajdah: Boolean = false,
        override val hasRubElHizb: Boolean = false,
    ) : MushafLineToken

    data class AyahEnd(
        override val ayah: AyahDisplayItem,
        val rukuEnded: Boolean,
        override val hasWaqfLazim: Boolean = false,
        override val hasSaktah: Boolean = false,
        override val hasSajdah: Boolean = false,
        override val hasRubElHizb: Boolean = false,
    ) : MushafLineToken
}

private sealed interface HafiziGridSlot {
    data class SurahHeader(val surah: SurahMeta) : HafiziGridSlot
    data class Basmalah(val surahNumber: Int) : HafiziGridSlot
    data class VerseLine(
        val tokens: List<MushafLineToken>,
        val isShortClosingLine: Boolean,
        val marginRukuNumber: Int? = null,
        val showWaqfLazimNote: Boolean = false,
        val showSajdahNote: Boolean = false,
        val showSaktahNote: Boolean = false,
        val showRubElHizbNote: Boolean = false,
    ) : HafiziGridSlot
}

private sealed interface PreTokenizedBlock {
    data class SurahBanner(val surah: SurahMeta) : PreTokenizedBlock
    data class BasmalahLine(val surahNumber: Int) : PreTokenizedBlock
    data class VerseTokens(val tokens: List<MushafLineToken>) : PreTokenizedBlock
}

private data class PackedHafiziPage(
    val slots: List<HafiziGridSlot>,
    val fontSizeSp: Float,
)

private data class HafiziThemePalette(
    val paperBg: Color,
    val paperGrain: Color,
    val inkColor: Color,
    val borderColor: Color,
    val accentTint: Color,
    val waqfColor: Color,
    val bannerBg: Color,
    val medallionBg: Color,
    val ruleLineColor: Color,
    val activeAyahHighlight: Color,
)

@Composable
fun HafiziKolkata15LinePageView(
    pageNumber: Int,
    ayahs: List<AyahDisplayItem>,
    prefs: UserPreferencesState,
    zoomScale: Float,
    isCompactSpread: Boolean,
    isPageBookmarked: Boolean,
    audioState: AudioPlaybackState,
    totalPages: Int = QuranMetadata.totalPagesForEdition(prefs.mushafEdition),
    onRetry: () -> Unit,
    onToggleBookmark: (String) -> Unit,
    onAyahTapped: (AyahDisplayItem) -> Unit,
    onPrevPage: () -> Unit,
    onNextPage: () -> Unit,
    onToggleControls: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val palette = remember(prefs.readingMode) {
        when (prefs.readingMode) {
            QuranReadingMode.DARK -> HafiziThemePalette(
                paperBg = Color(0xFF101311),
                paperGrain = Color(0xFFFFFFFF).copy(alpha = 0.025f),
                inkColor = Color(0xFFF3EFE2),
                borderColor = Color(0xFFD5CEB8),
                accentTint = Color(0xFFD4AF4A),
                waqfColor = Color(0xFFE8A44A),
                bannerBg = Color(0xFF1A201D),
                medallionBg = Color(0xFF1E2420),
                ruleLineColor = Color(0xFFD5CEB8).copy(alpha = 0.24f),
                activeAyahHighlight = Color(0xFFD4AF4A).copy(alpha = 0.22f),
            )
            QuranReadingMode.SEPIA -> HafiziThemePalette(
                paperBg = Color(0xFFF5EBD4),
                paperGrain = Color(0xFF6B532B).copy(alpha = 0.045f),
                inkColor = Color(0xFF1E170F),
                borderColor = Color(0xFF231B12),
                accentTint = Color(0xFF7D5A18),
                waqfColor = Color(0xFF8C1D18),
                bannerBg = Color(0xFFEBE0C4),
                medallionBg = Color(0xFFFDF8EA),
                ruleLineColor = Color(0xFF231B12).copy(alpha = 0.34f),
                activeAyahHighlight = Color(0xFFB58618).copy(alpha = 0.22f),
            )
            QuranReadingMode.COMFORT -> HafiziThemePalette(
                paperBg = Color(0xFFECF3ED),
                paperGrain = Color(0xFF1A3A28).copy(alpha = 0.03f),
                inkColor = Color(0xFF101C16),
                borderColor = Color(0xFF14271E),
                accentTint = Color(0xFF265C43),
                waqfColor = Color(0xFF8C1D18),
                bannerBg = Color(0xFFDCEADF),
                medallionBg = Color(0xFFF7FBF8),
                ruleLineColor = Color(0xFF14271E).copy(alpha = 0.32f),
                activeAyahHighlight = Color(0xFF265C43).copy(alpha = 0.18f),
            )
            QuranReadingMode.LIGHT -> HafiziThemePalette(
                paperBg = Color.White, // Authentic Emdadia Library crisp white paper
                paperGrain = Color(0xFF000000).copy(alpha = 0.015f),
                inkColor = Color(0xFF0D0D0D), // Crisp deep black ink print
                borderColor = Color(0xFF0E0E0E), // Intricate black-and-white arabesque border
                accentTint = Color(0xFF2B2B2B),
                waqfColor = Color(0xFF141414), // Classic crisp black Waqf symbols
                bannerBg = Color(0xFFF3F3F3),
                medallionBg = Color.White,
                ruleLineColor = Color(0xFF1A1A1A).copy(alpha = 0.72f),
                activeAyahHighlight = Color(0xFF2B4C3F).copy(alpha = 0.16f),
            )
        }
    }

    // Prefer Indo-Pak/Nastaliq font on Hafizi Kolkata pages
    val hafiziFont: FontFamily = when (prefs.arabicFont) {
        ArabicFontChoice.LATEEF,
        ArabicFontChoice.GULZAR,
        ArabicFontChoice.NOTO_NASTALIQ -> prefs.arabicFont.toFontFamily()
        else -> LateefFontFamily
    }

    val firstAyah = ayahs.firstOrNull()
    val primarySurah = firstAyah?.let { QuranMetadata.getSurah(it.surahNumber) }
    val juzNum = firstAyah?.juz ?: ((pageNumber - 1) / 20 + 1).coerceIn(1, 30)
    val juzMeta = QuranMetadata.getJuz(juzNum)
    val manzilNum = primarySurah?.let { QuranMetadata.manzilForSurah(it.number) }
        ?: (((pageNumber - 1) / 86) + 1).coerceIn(1, 7)

    var showPageTranslations by remember { mutableStateOf(false) }

    Surface(
        modifier = modifier
            .fillMaxSize()
            .testTag("mushaf_page_$pageNumber"),
        color = palette.paperBg,
        shape = RoundedCornerShape(10.dp),
        tonalElevation = 1.dp,
        shadowElevation = 2.dp,
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // Subtle off-white paper fiber texture canvas
            HafiziPaperTextureCanvas(
                grainColor = palette.paperGrain,
                modifier = Modifier.matchParentSize()
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(
                        horizontal = if (isCompactSpread) 4.dp else 8.dp,
                        vertical = if (isCompactSpread) 4.dp else 6.dp
                    )
            ) {
                // =========================================================================
                // 4. PAGE HEADER (OUTSIDE THE ORNATE FRAME)
                // Top-Right: Juz/Para Arabic name (الم ١)
                // Top-Center: Page # in Eastern Arabic numerals (٣)
                // Top-Left: Surah name & number (البقرة ٢)
                // =========================================================================
                CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onToggleControls() }
                            .padding(
                                horizontal = if (isCompactSpread) 18.dp else 28.dp,
                                vertical = 2.dp
                            ),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Top-Left (Outside Frame): Surah name & number (e.g., البقرة ٢)
                        Text(
                            text = primarySurah?.let {
                                "${it.arabicName} ${QuranMetadata.toArabicIndic(it.number)}"
                            } ?: "القرآن الكريم",
                            fontFamily = hafiziFont,
                            fontSize = if (isCompactSpread) 14.sp else 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = palette.inkColor,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            textAlign = TextAlign.Start,
                            modifier = Modifier
                                .weight(1f)
                                .testTag("mushaf_header_surah_$pageNumber")
                        )

                        // Top-Center (Outside Frame): Eastern Arabic page number (٣ .. ٦١١)
                        Text(
                            text = QuranMetadata.toArabicIndic(pageNumber),
                            fontFamily = hafiziFont,
                            fontSize = if (isCompactSpread) 14.sp else 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = palette.inkColor,
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .padding(horizontal = 8.dp)
                                .testTag("mushaf_header_page_$pageNumber")
                        )

                        // Top-Right (Outside Frame): Juz/Para name & number (e.g., الم ١)
                        Text(
                            text = "${juzMeta.arabicName} ${QuranMetadata.toArabicIndic(juzNum)}",
                            fontFamily = hafiziFont,
                            fontSize = if (isCompactSpread) 14.sp else 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = palette.inkColor,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            textAlign = TextAlign.End,
                            modifier = Modifier
                                .weight(1f)
                                .testTag("mushaf_header_juz_$pageNumber")
                        )
                    }
                }

                // =========================================================================
                // MIDDLE AREA: OUTER MARGIN MARKERS + ORNATE FRAME + 15-LINE RULED GRID
                // =========================================================================
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                ) {
                    if (ayahs.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = if (isCompactSpread) 16.dp else 26.dp)
                        ) {
                            HafiziKolkataOrnateFrameCanvas(
                                borderColor = palette.borderColor,
                                accentColor = palette.accentTint,
                                paperColor = palette.paperBg,
                                modifier = Modifier.matchParentSize()
                            )
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(20.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                CircularProgressIndicator(
                                    color = palette.borderColor,
                                    modifier = Modifier.size(28.dp)
                                )
                                Spacer(Modifier.height(10.dp))
                                Text(
                                    "Loading 15-Line Hafizi Page $pageNumber...",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = palette.inkColor,
                                    textAlign = TextAlign.Center
                                )
                                Spacer(Modifier.height(8.dp))
                                Button(onClick = onRetry) { Text("Fetch Page") }
                            }
                        }
                    } else if (pageNumber == 1) {
                        FatihahEmdadiaFirstPageBody(
                            ayahs = ayahs,
                            prefs = prefs,
                            hafiziFont = hafiziFont,
                            palette = palette,
                            zoomScale = zoomScale,
                            isCompactSpread = isCompactSpread,
                            audioState = audioState,
                            onAyahTapped = onAyahTapped,
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        HafiziFifteenLineRuledBody(
                            pageNumber = pageNumber,
                            ayahs = ayahs,
                            prefs = prefs,
                            hafiziFont = hafiziFont,
                            palette = palette,
                            zoomScale = zoomScale,
                            isCompactSpread = isCompactSpread,
                            audioState = audioState,
                            onAyahTapped = onAyahTapped,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }

                // Optional Page Translations Bar (kept unobtrusive outside the Mushaf frame)
                if (prefs.showTranslation && !isCompactSpread && ayahs.isNotEmpty()) {
                    Column(modifier = Modifier.padding(horizontal = 24.dp, vertical = 1.dp)) {
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(6.dp))
                                .clickable { showPageTranslations = !showPageTranslations },
                            color = palette.bannerBg.copy(alpha = 0.72f)
                        ) {
                            CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 8.dp, vertical = 2.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        "Translation (${ayahs.size} Ayahs)",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = palette.inkColor.copy(alpha = 0.8f)
                                    )
                                    Icon(
                                        if (showPageTranslations) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore,
                                        contentDescription = "Toggle page translations",
                                        tint = palette.inkColor,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        }
                        if (showPageTranslations) {
                            CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(100.dp)
                                        .verticalScroll(rememberScrollState())
                                        .padding(vertical = 4.dp),
                                    verticalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    ayahs.forEach { a ->
                                        if (a.translationText.isNotBlank()) {
                                            Text(
                                                text = "(${a.surahNumber}:${a.ayahNumber}) ${a.translationText}",
                                                style = MaterialTheme.typography.bodySmall,
                                                color = palette.inkColor.copy(alpha = 0.88f)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // =========================================================================
                // 4. PAGE FOOTER (OUTSIDE THE ORNATE FRAME)
                // Bottom-Center: Manzil indicator (منزل ١) + edge navigation & bookmark
                // =========================================================================
                CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = if (isCompactSpread) 4.dp else 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = onNextPage,
                            enabled = pageNumber < totalPages,
                            modifier = Modifier
                                .size(30.dp)
                                .testTag("next_page_btn_$pageNumber")
                        ) {
                            Icon(
                                Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                                contentDescription = "Next Mushaf Page (RTL)",
                                tint = palette.inkColor.copy(alpha = 0.78f),
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        // Bottom-center outside frame: Manzil indicator (e.g. منزل ١)
                        Text(
                            text = "منزل ${QuranMetadata.toArabicIndic(manzilNum)}",
                            fontFamily = hafiziFont,
                            fontSize = if (isCompactSpread) 13.sp else 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = palette.inkColor,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.testTag("mushaf_footer_manzil_$pageNumber")
                        )

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(
                                onClick = {
                                    val label = primarySurah?.let { "${it.englishName} (Juz $juzNum)" }
                                        ?: "Mushaf Page $pageNumber"
                                    onToggleBookmark(label)
                                },
                                modifier = Modifier
                                    .size(30.dp)
                                    .testTag("bookmark_page_$pageNumber")
                            ) {
                                Icon(
                                    if (isPageBookmarked) Icons.Filled.Bookmark else Icons.Filled.BookmarkBorder,
                                    contentDescription = "Bookmark Mushaf Page $pageNumber",
                                    tint = if (isPageBookmarked) palette.waqfColor else palette.inkColor.copy(alpha = 0.75f),
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                            IconButton(
                                onClick = onPrevPage,
                                enabled = pageNumber > 1,
                                modifier = Modifier
                                    .size(30.dp)
                                    .testTag("prev_page_btn_$pageNumber")
                            ) {
                                Icon(
                                    Icons.AutoMirrored.Filled.KeyboardArrowRight,
                                    contentDescription = "Previous Mushaf Page (RTL)",
                                    tint = palette.inkColor.copy(alpha = 0.78f),
                                    modifier = Modifier.size(18.dp)
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
private fun HafiziFifteenLineRuledBody(
    pageNumber: Int,
    ayahs: List<AyahDisplayItem>,
    prefs: UserPreferencesState,
    hafiziFont: FontFamily,
    palette: HafiziThemePalette,
    zoomScale: Float,
    isCompactSpread: Boolean,
    audioState: AudioPlaybackState,
    onAyahTapped: (AyahDisplayItem) -> Unit,
    modifier: Modifier = Modifier,
) {
    val density = LocalDensity.current
    val textMeasurer = rememberTextMeasurer()

    val outerMarginWidthDp = if (isCompactSpread) 18.dp else 28.dp
    val frameBandDp = if (isCompactSpread) 12.dp else 17.dp
    val textHorizPadDp = if (isCompactSpread) 5.dp else 8.dp
    val frameVertInsetDp = if (isCompactSpread) 12.dp else 17.dp

    val effectiveScript = when (prefs.mushafEdition) {
        com.example.data.local.MushafEdition.TAJWEED -> QuranScriptType.TAJWEED
        else -> prefs.quranScript
    }

    BoxWithConstraints(modifier = modifier.fillMaxSize()) {
        val availableInnerTextWidthPx = with(density) {
            (maxWidth - outerMarginWidthDp * 2 - frameBandDp * 2 - textHorizPadDp * 2)
                .coerceAtLeast(80.dp)
                .toPx()
        }
        val baseGridHeightDp = (maxHeight - frameVertInsetDp * 2).coerceAtLeast(300.dp)
        val singleRowHeightCapSp = (baseGridHeightDp.value / HAFIZI_LINES_PER_PAGE) * 0.60f
        val desiredMaxFontSp = if (isCompactSpread) {
            (prefs.arabicFontSizeSp * 0.76f * zoomScale).coerceIn(12f, 32f)
        } else {
            val baseMax = (prefs.arabicFontSizeSp * 0.88f * zoomScale).coerceIn(13f, 44f)
            if (zoomScale <= 1.05f) baseMax.coerceAtMost(singleRowHeightCapSp.coerceAtLeast(13f)) else baseMax
        }

        // Tokenize Ayahs into words + ornate circular Ayah-end rosettes + margin metadata
        val preBlocks = remember(ayahs, effectiveScript, palette.inkColor, palette.waqfColor) {
            buildPreTokenizedBlocks(
                ayahs = ayahs,
                script = effectiveScript,
                inkColor = palette.inkColor,
                waqfColor = palette.waqfColor,
            )
        }

        // Pack lines and fit cleanly into 15 ruled lines at default scale
        val packed = remember(
            preBlocks,
            availableInnerTextWidthPx,
            desiredMaxFontSp,
            hafiziFont,
            zoomScale,
            density
        ) {
            packIntoFifteenLines(
                blocks = preBlocks,
                maxWidthPx = availableInnerTextWidthPx,
                targetFontSp = desiredMaxFontSp,
                enforceMax15Lines = zoomScale <= 1.05f,
                fontFamily = hafiziFont,
                textMeasurer = textMeasurer,
                density = density,
            )
        }

        val totalRows = if (zoomScale <= 1.05f) {
            HAFIZI_LINES_PER_PAGE
        } else {
            maxOf(HAFIZI_LINES_PER_PAGE, packed.slots.size)
        }
        val rowHeightDp: Dp = if (zoomScale <= 1.05f) {
            baseGridHeightDp / HAFIZI_LINES_PER_PAGE
        } else {
            (baseGridHeightDp / HAFIZI_LINES_PER_PAGE) * zoomScale.coerceAtMost(1.55f)
        }
        val totalGridHeightDp = rowHeightDp * totalRows
        val scrollModifier = if (zoomScale > 1.05f) {
            Modifier.verticalScroll(rememberScrollState())
        } else {
            Modifier
        }

        // Detect if page has any mid-page Ruku end marker; if none, show active Ruku in top margin
        val hasRowRukuMarker = packed.slots.any {
            it is HafiziGridSlot.VerseLine && it.marginRukuNumber != null
        }
        val activeRukuNumber = ayahs.firstOrNull()?.ruku

        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .then(scrollModifier),
                verticalAlignment = Alignment.Top
            ) {
                // LEFT OUTER MARGIN (outside the ornate border): Sajdah / Saktah / alternating Ruku markers
                Column(
                    modifier = Modifier
                        .width(outerMarginWidthDp)
                        .height(totalGridHeightDp + frameVertInsetDp * 2)
                        .padding(top = frameVertInsetDp, bottom = frameVertInsetDp)
                ) {
                    for (r in 0 until totalRows) {
                        val slot = packed.slots.getOrNull(r)
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(rowHeightDp),
                            contentAlignment = Alignment.Center
                        ) {
                            if (slot is HafiziGridSlot.VerseLine) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center
                                ) {
                                    if (pageNumber % 2 == 0 && slot.marginRukuNumber != null) {
                                        RukuMarginBadge(
                                            rukuNum = slot.marginRukuNumber,
                                            inkColor = palette.inkColor,
                                            font = hafiziFont,
                                            compact = isCompactSpread
                                        )
                                    }
                                    if (slot.showSajdahNote) {
                                        MarginNoteTag(
                                            text = "سجدة",
                                            color = palette.waqfColor,
                                            border = palette.waqfColor,
                                            font = hafiziFont
                                        )
                                    }
                                    if (slot.showSaktahNote) {
                                        MarginNoteTag(
                                            text = "سكتة",
                                            color = palette.inkColor,
                                            border = palette.inkColor.copy(alpha = 0.45f),
                                            font = hafiziFont
                                        )
                                    }
                                    if (pageNumber % 2 != 0 && slot.showWaqfLazimNote) {
                                        WaqfLazimMarginBadge(
                                            color = palette.waqfColor,
                                            font = hafiziFont
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // CENTER ORNATE BORDER FRAME + 15-LINE RULED GRID
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(totalGridHeightDp + frameVertInsetDp * 2)
                ) {
                    // Ornate dark-ink Islamic geometric & floral border around the 15-line block
                    HafiziKolkataOrnateFrameCanvas(
                        borderColor = palette.borderColor,
                        accentColor = palette.accentTint,
                        paperColor = palette.paperBg,
                        modifier = Modifier.matchParentSize()
                    )

                    // Inner ruled container with exact horizontal divider lines
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(
                                horizontal = frameBandDp,
                                vertical = frameVertInsetDp
                            )
                    ) {
                        // Draw 15 crisp horizontal ruled lines across the entire writing box
                        Canvas(modifier = Modifier.matchParentSize()) {
                            val rowH = size.height / totalRows.toFloat()
                            val strokeW = 0.8.dp.toPx()
                            for (i in 1 until totalRows) {
                                val y = rowH * i
                                drawLine(
                                    color = palette.ruleLineColor,
                                    start = Offset(0f, y),
                                    end = Offset(size.width, y),
                                    strokeWidth = strokeW
                                )
                            }
                        }

                        // Populate each of the 15 horizontal rows cleanly inside its cell
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = textHorizPadDp)
                        ) {
                            for (r in 0 until totalRows) {
                                val slot = packed.slots.getOrNull(r)
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(rowHeightDp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    when (slot) {
                                        is HafiziGridSlot.SurahHeader -> {
                                            HafiziSurahRowCartouche(
                                                surah = slot.surah,
                                                palette = palette,
                                                font = hafiziFont,
                                                compact = isCompactSpread,
                                            )
                                        }
                                        is HafiziGridSlot.Basmalah -> {
                                            CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                                                Text(
                                                    text = "بِسْمِ ٱللَّهِ ٱلرَّحْمَٰنِ ٱلرَّحِيمِ",
                                                    fontFamily = hafiziFont,
                                                    fontSize = (packed.fontSizeSp * 0.96f).sp,
                                                    color = palette.inkColor,
                                                    textAlign = TextAlign.Center,
                                                    maxLines = 1,
                                                    modifier = Modifier.fillMaxWidth()
                                                )
                                            }
                                        }
                                        is HafiziGridSlot.VerseLine -> {
                                            JustifiedHafiziVerseRow(
                                                tokens = slot.tokens,
                                                isShortClosingLine = slot.isShortClosingLine,
                                                fontSizeSp = packed.fontSizeSp,
                                                fontFamily = hafiziFont,
                                                palette = palette,
                                                audioState = audioState,
                                                onAyahTapped = onAyahTapped,
                                            )
                                        }
                                        null -> Unit
                                    }
                                }
                            }
                        }
                    }
                }

                // RIGHT OUTER MARGIN (outside the ornate border): Arabic 'ع' Ruku markers + 'وقف لازم' + Juz/Hizb ۞
                Column(
                    modifier = Modifier
                        .width(outerMarginWidthDp)
                        .height(totalGridHeightDp + frameVertInsetDp * 2)
                        .padding(top = frameVertInsetDp, bottom = frameVertInsetDp)
                ) {
                    for (r in 0 until totalRows) {
                        val slot = packed.slots.getOrNull(r)
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(rowHeightDp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                // Show active page Ruku on row 0 only when no mid-page Ruku boundary exists
                                if (r == 0 && !hasRowRukuMarker && activeRukuNumber != null) {
                                    RukuMarginBadge(
                                        rukuNum = activeRukuNumber,
                                        inkColor = palette.inkColor.copy(alpha = 0.72f),
                                        font = hafiziFont,
                                        compact = true
                                    )
                                }
                                if (slot is HafiziGridSlot.VerseLine) {
                                    if (pageNumber % 2 != 0 && slot.marginRukuNumber != null) {
                                        RukuMarginBadge(
                                            rukuNum = slot.marginRukuNumber,
                                            inkColor = palette.inkColor,
                                            font = hafiziFont,
                                            compact = isCompactSpread
                                        )
                                    }
                                    if (pageNumber % 2 == 0 && slot.showWaqfLazimNote) {
                                        WaqfLazimMarginBadge(
                                            color = palette.waqfColor,
                                            font = hafiziFont
                                        )
                                    }
                                    if (slot.showRubElHizbNote) {
                                        Text(
                                            text = "۞",
                                            fontFamily = hafiziFont,
                                            fontSize = 12.sp,
                                            color = palette.inkColor
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
}

@Composable
private fun JustifiedHafiziVerseRow(
    tokens: List<MushafLineToken>,
    isShortClosingLine: Boolean,
    fontSizeSp: Float,
    fontFamily: FontFamily,
    palette: HafiziThemePalette,
    audioState: AudioPlaybackState,
    onAyahTapped: (AyahDisplayItem) -> Unit,
) {
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = if (isShortClosingLine) {
                Arrangement.spacedBy(5.dp, Alignment.CenterHorizontally)
            } else {
                Arrangement.SpaceBetween
            },
            verticalAlignment = Alignment.CenterVertically
        ) {
            tokens.forEach { token ->
                val isPlayingThisAyah = (audioState.isPlaying || audioState.isBuffering) &&
                    audioState.surahNumber == token.ayah.surahNumber &&
                    audioState.ayahNumber == token.ayah.ayahNumber

                when (token) {
                    is MushafLineToken.Word -> {
                        Text(
                            text = token.text,
                            fontFamily = fontFamily,
                            fontSize = fontSizeSp.sp,
                            maxLines = 1,
                            softWrap = false,
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(
                                    if (isPlayingThisAyah) palette.activeAyahHighlight else Color.Transparent
                                )
                                .clickable { onAyahTapped(token.ayah) }
                        )
                    }
                    is MushafLineToken.AyahEnd -> {
                        OrnateCircularAyahMarker(
                            ayahNumber = token.ayah.ayahNumber,
                            fontSizeSp = fontSizeSp,
                            inkColor = palette.inkColor,
                            accentColor = palette.borderColor,
                            fillColor = if (isPlayingThisAyah) palette.activeAyahHighlight else palette.medallionBg,
                            fontFamily = fontFamily,
                            modifier = Modifier.clickable { onAyahTapped(token.ayah) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun OrnateCircularAyahMarker(
    ayahNumber: Int,
    fontSizeSp: Float,
    inkColor: Color,
    accentColor: Color,
    fillColor: Color,
    fontFamily: FontFamily,
    modifier: Modifier = Modifier,
) {
    val arabicNum = QuranMetadata.toArabicIndic(ayahNumber)
    val baseDiameter = (fontSizeSp * 1.18f).coerceIn(18f, 34f)
    val extraWidth = if (arabicNum.length >= 3) 4f else 0f

    Box(
        modifier = modifier
            .size(width = (baseDiameter + extraWidth).dp, height = baseDiameter.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.matchParentSize()) {
            val cx = size.width / 2f
            val cy = size.height / 2f
            val rOuter = minOf(size.width, size.height) * 0.47f
            val rInner = rOuter * 0.72f

            // Classic scalloped 8-petal floral rosette border
            val petalCount = 8
            val rosettePath = Path()
            for (i in 0 until petalCount * 2) {
                val angle = (i * PI / petalCount).toFloat()
                val r = if (i % 2 == 0) rOuter else rOuter * 0.83f
                val x = cx + r * cos(angle)
                val y = cy + r * sin(angle)
                if (i == 0) rosettePath.moveTo(x, y) else rosettePath.lineTo(x, y)
            }
            rosettePath.close()

            drawPath(rosettePath, color = fillColor)
            drawPath(rosettePath, color = accentColor, style = Stroke(width = 1.1.dp.toPx()))
            drawCircle(
                color = accentColor.copy(alpha = 0.7f),
                radius = rInner,
                center = Offset(cx, cy),
                style = Stroke(width = 0.75.dp.toPx())
            )
        }
        Text(
            text = arabicNum,
            fontFamily = fontFamily,
            fontSize = (fontSizeSp * if (arabicNum.length >= 3) 0.44f else 0.52f).coerceIn(8f, 16f).sp,
            fontWeight = FontWeight.Bold,
            color = inkColor,
            maxLines = 1,
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
private fun HafiziSurahRowCartouche(
    surah: SurahMeta,
    palette: HafiziThemePalette,
    font: FontFamily,
    compact: Boolean,
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(0.86f),
        color = palette.bannerBg,
        shape = RoundedCornerShape(4.dp),
        border = BorderStroke(1.dp, palette.borderColor),
    ) {
        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (surah.revelationType == "Meccan") "مكية" else "مدنية",
                    fontFamily = font,
                    fontSize = if (compact) 10.sp else 12.sp,
                    color = palette.inkColor.copy(alpha = 0.8f),
                    maxLines = 1
                )
                Text(
                    text = "سُورَةُ ${surah.arabicName}",
                    fontFamily = font,
                    fontSize = if (compact) 14.sp else 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = palette.inkColor,
                    maxLines = 1
                )
                Text(
                    text = "آياتها ${QuranMetadata.toArabicIndic(surah.ayahCount)}",
                    fontFamily = font,
                    fontSize = if (compact) 10.sp else 12.sp,
                    color = palette.inkColor.copy(alpha = 0.8f),
                    maxLines = 1
                )
            }
        }
    }
}

@Composable
private fun RukuMarginBadge(
    rukuNum: Int,
    inkColor: Color,
    font: FontFamily,
    compact: Boolean,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(vertical = 1.dp)
    ) {
        Text(
            text = "ع",
            fontFamily = font,
            fontSize = if (compact) 12.sp else 15.sp,
            fontWeight = FontWeight.Bold,
            color = inkColor,
            lineHeight = 14.sp,
        )
        Text(
            text = QuranMetadata.toArabicIndic(rukuNum),
            fontFamily = font,
            fontSize = if (compact) 8.sp else 10.sp,
            color = inkColor.copy(alpha = 0.88f),
            lineHeight = 10.sp,
        )
    }
}

@Composable
private fun WaqfLazimMarginBadge(
    color: Color,
    font: FontFamily,
) {
    Surface(
        color = Color.Transparent,
        border = BorderStroke(0.6.dp, color.copy(alpha = 0.65f)),
        shape = RoundedCornerShape(3.dp),
    ) {
        Text(
            text = "وقف\nلازم",
            fontFamily = font,
            fontSize = 7.5.sp,
            lineHeight = 8.sp,
            color = color,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 2.dp, vertical = 1.dp)
        )
    }
}

@Composable
private fun MarginNoteTag(
    text: String,
    color: Color,
    border: Color,
    font: FontFamily,
) {
    Surface(
        color = Color.Transparent,
        border = BorderStroke(0.6.dp, border),
        shape = RoundedCornerShape(3.dp),
    ) {
        Text(
            text = text,
            fontFamily = font,
            fontSize = 8.sp,
            lineHeight = 9.sp,
            color = color,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 2.dp, vertical = 0.5.dp)
        )
    }
}

@Composable
private fun HafiziPaperTextureCanvas(
    grainColor: Color,
    modifier: Modifier = Modifier,
) {
    Canvas(modifier = modifier) {
        // Deterministic subtle printed-paper grain flecks
        val cols = 18
        val rows = 26
        val dx = size.width / cols
        val dy = size.height / rows
        for (r in 0 until rows) {
            for (c in 0 until cols) {
                val hash = ((r * 73 + c * 37) % 19)
                val ox = (hash - 9) * 0.04f * dx
                val oy = (((r * 29 + c * 53) % 17) - 8) * 0.04f * dy
                drawCircle(
                    color = grainColor,
                    radius = if (hash % 3 == 0) 1.1.dp.toPx() else 0.7.dp.toPx(),
                    center = Offset((c + 0.5f) * dx + ox, (r + 0.5f) * dy + oy)
                )
            }
        }
    }
}

@Composable
private fun FatihahEmdadiaFirstPageBody(
    ayahs: List<AyahDisplayItem>,
    prefs: UserPreferencesState,
    hafiziFont: FontFamily,
    palette: HafiziThemePalette,
    zoomScale: Float,
    isCompactSpread: Boolean,
    audioState: AudioPlaybackState,
    onAyahTapped: (AyahDisplayItem) -> Unit,
    modifier: Modifier = Modifier,
) {
    val density = LocalDensity.current
    val textMeasurer = rememberTextMeasurer()

    val outerMarginWidthDp = if (isCompactSpread) 18.dp else 28.dp
    val frameBandDp = if (isCompactSpread) 12.dp else 17.dp
    val textHorizPadDp = if (isCompactSpread) 6.dp else 10.dp
    val frameVertInsetDp = if (isCompactSpread) 12.dp else 17.dp
    val ruledLineCount = 7

    val effectiveScript = when (prefs.mushafEdition) {
        com.example.data.local.MushafEdition.TAJWEED -> QuranScriptType.TAJWEED
        else -> prefs.quranScript
    }

    val basmalahAyah = ayahs.firstOrNull()
    val bodyAyahs = if (ayahs.size > 1) ayahs.drop(1) else ayahs

    BoxWithConstraints(modifier = modifier.fillMaxSize()) {
        val availableInnerWidthPx = with(density) {
            (maxWidth - outerMarginWidthDp * 2 - frameBandDp * 2 - textHorizPadDp * 2)
                .coerceAtLeast(80.dp)
                .toPx()
        }
        val innerContentHeightDp = (maxHeight - frameVertInsetDp * 2).coerceAtLeast(300.dp)
        val upperHeaderHeightDp = innerContentHeightDp * 0.34f
        val basmalahHeightDp = innerContentHeightDp * 0.12f
        val sevenLinesTotalHeightDp = (innerContentHeightDp - upperHeaderHeightDp - basmalahHeightDp)
            .coerceAtLeast(160.dp)
        val rowHeightDp = (sevenLinesTotalHeightDp / ruledLineCount) *
            (if (zoomScale > 1.05f) zoomScale.coerceAtMost(1.5f) else 1f)

        val rowHeightCapSp = (sevenLinesTotalHeightDp.value / ruledLineCount) * 0.56f
        val targetFontSp = if (isCompactSpread) {
            (prefs.arabicFontSizeSp * 0.78f * zoomScale).coerceIn(13f, 28f)
        } else {
            (prefs.arabicFontSizeSp * 0.90f * zoomScale).coerceIn(15f, 34f)
                .coerceAtMost(rowHeightCapSp.coerceAtLeast(15f))
        }

        val bodyTokens = remember(bodyAyahs, effectiveScript, palette.inkColor, palette.waqfColor) {
            val sMeta = QuranMetadata.getSurah(1)
            val list = mutableListOf<MushafLineToken>()
            bodyAyahs.forEachIndexed { idx, ayah ->
                val rawBase = when (effectiveScript) {
                    QuranScriptType.TAJWEED -> ayah.tajweedMarkup.ifBlank { ayah.uthmaniText }
                    QuranScriptType.INDO_PAK -> ayah.simpleText.ifBlank { ayah.uthmaniText }
                    QuranScriptType.UTHMANI -> ayah.uthmaniText
                }
                val ayahAnnotated = if (effectiveScript == QuranScriptType.TAJWEED) {
                    TajweedParser.parseTajweedAnnotated(rawBase, palette.inkColor)
                } else {
                    buildAnnotatedWithWaqfAccent(rawBase, palette.inkColor, palette.waqfColor)
                }
                val rawStr = ayahAnnotated.text
                var cursor = 0
                while (cursor < rawStr.length) {
                    while (cursor < rawStr.length && rawStr[cursor].isWhitespace()) cursor++
                    if (cursor >= rawStr.length) break
                    val start = cursor
                    while (cursor < rawStr.length && !rawStr[cursor].isWhitespace()) cursor++
                    val end = cursor
                    val wordSlice = ayahAnnotated.subSequence(start, end)
                    list += MushafLineToken.Word(
                        text = wordSlice,
                        ayah = ayah,
                        hasWaqfLazim = '\u06D8' in wordSlice.text,
                    )
                }
                list += MushafLineToken.AyahEnd(
                    ayah = ayah,
                    rukuEnded = idx == bodyAyahs.lastIndex || ayah.ayahNumber == sMeta.ayahCount
                )
            }
            listOf<PreTokenizedBlock>(PreTokenizedBlock.VerseTokens(list))
        }

        val packedFatihah = remember(
            bodyTokens,
            availableInnerWidthPx,
            targetFontSp,
            hafiziFont,
            density
        ) {
            var fontCandidate = targetFontSp
            var slots = measureAndPackOnce(bodyTokens, availableInnerWidthPx, fontCandidate, hafiziFont, textMeasurer, density)
            while (slots.size > ruledLineCount && fontCandidate > 12f) {
                fontCandidate -= 0.5f
                slots = measureAndPackOnce(bodyTokens, availableInnerWidthPx, fontCandidate, hafiziFont, textMeasurer, density)
            }
            PackedHafiziPage(slots, fontCandidate)
        }

        val basmalahPlaying = basmalahAyah != null &&
            (audioState.isPlaying || audioState.isBuffering) &&
            audioState.surahNumber == 1 &&
            audioState.ayahNumber == 1

        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .let { if (zoomScale > 1.05f) it.verticalScroll(rememberScrollState()) else it },
                verticalAlignment = Alignment.Top
            ) {
                // Left outer spacer matching symmetrical frame placement
                Spacer(Modifier.width(outerMarginWidthDp))

                // Center Thick Black-and-White Emdadia Arabesque Frame + Special Fatihah Layout
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(upperHeaderHeightDp + basmalahHeightDp + rowHeightDp * ruledLineCount + frameVertInsetDp * 2)
                ) {
                    HafiziKolkataOrnateFrameCanvas(
                        borderColor = palette.borderColor,
                        accentColor = palette.accentTint,
                        paperColor = palette.paperBg,
                        modifier = Modifier.matchParentSize()
                    )

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(
                                horizontal = frameBandDp + textHorizPadDp,
                                vertical = frameVertInsetDp
                            ),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Upper Ornamental Surah Al-Fatihah Info Block
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(upperHeaderHeightDp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(2.dp)
                            ) {
                                Text(
                                    text = "اٰيَاتُهَا ٧",
                                    fontFamily = hafiziFont,
                                    fontSize = if (isCompactSpread) 13.sp else 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = palette.inkColor
                                )
                                Text(
                                    text = "(١) سُوْرَةُ الْفَاتِحَةِ مَكِّيَّةٌ (٥)",
                                    fontFamily = hafiziFont,
                                    fontSize = if (isCompactSpread) 15.sp else 19.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = palette.inkColor,
                                    textAlign = TextAlign.Center
                                )
                                Text(
                                    text = "رُكُوْعُهَا ١",
                                    fontFamily = hafiziFont,
                                    fontSize = if (isCompactSpread) 13.sp else 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = palette.inkColor
                                )
                            }
                        }

                        // Decorative Calligraphic Basmalah above the 7 ruled lines
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(basmalahHeightDp)
                                .clip(RoundedCornerShape(6.dp))
                                .background(if (basmalahPlaying) palette.activeAyahHighlight else Color.Transparent)
                                .clickable(enabled = basmalahAyah != null) {
                                    basmalahAyah?.let { onAyahTapped(it) }
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                                Text(
                                    text = "بِسْمِ اللّٰهِ الرَّحْمٰنِ الرَّحِيْمِ",
                                    fontFamily = hafiziFont,
                                    fontSize = (packedFatihah.fontSizeSp * 1.12f).coerceIn(16f, 34f).sp,
                                    fontWeight = FontWeight.Bold,
                                    color = palette.inkColor,
                                    textAlign = TextAlign.Center,
                                    maxLines = 1
                                )
                            }
                        }

                        // Top ruled border above the 7 lines
                        HorizontalDivider(thickness = 0.9.dp, color = palette.ruleLineColor)

                        // 7 Ruled Horizontal Lines for Surah Al-Fatihah
                        Column(modifier = Modifier.fillMaxWidth()) {
                            for (r in 0 until ruledLineCount) {
                                val slot = packedFatihah.slots.getOrNull(r)
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(rowHeightDp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (slot is HafiziGridSlot.VerseLine) {
                                        JustifiedHafiziVerseRow(
                                            tokens = slot.tokens,
                                            isShortClosingLine = slot.isShortClosingLine && r == packedFatihah.slots.lastIndex,
                                            fontSizeSp = packedFatihah.fontSizeSp,
                                            fontFamily = hafiziFont,
                                            palette = palette,
                                            audioState = audioState,
                                            onAyahTapped = onAyahTapped,
                                        )
                                    }
                                }
                                HorizontalDivider(thickness = 0.85.dp, color = palette.ruleLineColor)
                            }
                        }
                    }
                }

                // Right Outer Margin matching Page 1 reference: المنزل ١ & ع ١
                Column(
                    modifier = Modifier
                        .width(outerMarginWidthDp)
                        .height(upperHeaderHeightDp + basmalahHeightDp + rowHeightDp * ruledLineCount + frameVertInsetDp * 2)
                        .padding(vertical = frameVertInsetDp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Spacer(Modifier.height(upperHeaderHeightDp * 0.7f))
                    Text(
                        text = "المنزل\n١",
                        fontFamily = hafiziFont,
                        fontSize = if (isCompactSpread) 9.sp else 11.sp,
                        lineHeight = 12.sp,
                        color = palette.inkColor,
                        textAlign = TextAlign.Center
                    )
                    RukuMarginBadge(
                        rukuNum = 1,
                        inkColor = palette.inkColor,
                        font = hafiziFont,
                        compact = isCompactSpread
                    )
                }
            }
        }
    }
}

@Composable
private fun HafiziKolkataOrnateFrameCanvas(
    borderColor: Color,
    accentColor: Color,
    paperColor: Color,
    modifier: Modifier = Modifier,
) {
    Canvas(modifier = modifier) {
        val outerStroke = 1.6.dp.toPx()
        val innerStroke = 1.3.dp.toPx()
        val hairStroke = 0.65.dp.toPx()
        val band = 15.dp.toPx().coerceAtMost(size.width * 0.068f)

        val outerLeft = 1.dp.toPx()
        val outerTop = 1.dp.toPx()
        val outerW = size.width - outerLeft * 2
        val outerH = size.height - outerTop * 2

        val innerLeft = outerLeft + band
        val innerTop = outerTop + band
        val innerW = (outerW - band * 2).coerceAtLeast(10f)
        val innerH = (outerH - band * 2).coerceAtLeast(10f)

        // Outer border double outline
        drawRect(
            color = borderColor,
            topLeft = Offset(outerLeft, outerTop),
            size = Size(outerW, outerH),
            style = Stroke(width = outerStroke)
        )
        drawRect(
            color = borderColor,
            topLeft = Offset(outerLeft + 2.2.dp.toPx(), outerTop + 2.2.dp.toPx()),
            size = Size(outerW - 4.4.dp.toPx(), outerH - 4.4.dp.toPx()),
            style = Stroke(width = hairStroke)
        )

        // Rich repeating Emdadia black-and-white arabesque leaf & scalloped floral band
        val topBandMid = outerTop + band * 0.5f
        val botBandMid = outerTop + outerH - band * 0.5f
        val leftBandMid = outerLeft + band * 0.5f
        val rightBandMid = outerLeft + outerW - band * 0.5f

        val motifStep = band * 1.15f
        val petalR = band * 0.34f

        var x = innerLeft + motifStep * 0.45f
        while (x < innerLeft + innerW - motifStep * 0.35f) {
            drawEmdadiaFloralScrollUnit(Offset(x, topBandMid), petalR, borderColor, paperColor)
            drawEmdadiaFloralScrollUnit(Offset(x, botBandMid), petalR, borderColor, paperColor)
            x += motifStep
        }

        var y = innerTop + motifStep * 0.45f
        while (y < innerTop + innerH - motifStep * 0.35f) {
            drawEmdadiaFloralScrollUnit(Offset(leftBandMid, y), petalR, borderColor, paperColor)
            drawEmdadiaFloralScrollUnit(Offset(rightBandMid, y), petalR, borderColor, paperColor)
            y += motifStep
        }

        // Inner double rule enclosing the text block cleanly
        drawRect(
            color = borderColor,
            topLeft = Offset(innerLeft - 2.2.dp.toPx(), innerTop - 2.2.dp.toPx()),
            size = Size(innerW + 4.4.dp.toPx(), innerH + 4.4.dp.toPx()),
            style = Stroke(width = hairStroke)
        )
        drawRect(
            color = borderColor,
            topLeft = Offset(innerLeft, innerTop),
            size = Size(innerW, innerH),
            style = Stroke(width = innerStroke)
        )

        // Ornate Black-and-White Corner Arabesque Rosettes
        val cornerR = band * 0.58f
        val cornerCenters = listOf(
            Offset(outerLeft + band * 0.5f, outerTop + band * 0.5f),
            Offset(outerLeft + outerW - band * 0.5f, outerTop + band * 0.5f),
            Offset(outerLeft + band * 0.5f, outerTop + outerH - band * 0.5f),
            Offset(outerLeft + outerW - band * 0.5f, outerTop + outerH - band * 0.5f),
        )
        cornerCenters.forEach { center ->
            drawCircle(color = paperColor, radius = cornerR * 0.96f, center = center)
            drawGeometricCornerRosette(center, cornerR, borderColor, accentColor)
        }
    }
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawEmdadiaFloralScrollUnit(
    center: Offset,
    radius: Float,
    inkColor: Color,
    paperColor: Color,
) {
    // High-contrast ink-filled scalloped floral leaf motif mimicking woodcut arabesque border
    val petals = 6
    val outerPath = Path()
    for (i in 0 until petals * 2) {
        val angle = (i * PI / petals).toFloat()
        val r = if (i % 2 == 0) radius else radius * 0.62f
        val px = center.x + r * cos(angle)
        val py = center.y + r * sin(angle)
        if (i == 0) outerPath.moveTo(px, py) else outerPath.lineTo(px, py)
    }
    outerPath.close()
    drawPath(outerPath, color = inkColor)
    drawCircle(color = paperColor, radius = radius * 0.44f, center = center)
    drawCircle(color = inkColor, radius = radius * 0.22f, center = center)
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawGeometricCornerRosette(
    center: Offset,
    radius: Float,
    inkColor: Color,
    accentColor: Color,
) {
    val petals = 10
    val path = Path()
    for (i in 0 until petals * 2) {
        val angle = (i * PI / petals).toFloat()
        val r = if (i % 2 == 0) radius else radius * 0.56f
        val px = center.x + r * cos(angle)
        val py = center.y + r * sin(angle)
        if (i == 0) path.moveTo(px, py) else path.lineTo(px, py)
    }
    path.close()
    drawPath(path, color = inkColor)
    drawCircle(color = accentColor.copy(alpha = 0.25f), radius = radius * 0.46f, center = center)
    drawCircle(color = inkColor, radius = radius * 0.42f, center = center, style = Stroke(width = 1.dp.toPx()))
    drawCircle(color = inkColor, radius = radius * 0.16f, center = center)
}

private fun buildPreTokenizedBlocks(
    ayahs: List<AyahDisplayItem>,
    script: QuranScriptType,
    inkColor: Color,
    waqfColor: Color,
): List<PreTokenizedBlock> {
    val result = mutableListOf<PreTokenizedBlock>()
    val grouped = ayahs.groupBy { it.surahNumber }

    grouped.forEach { (surahNum, surahAyahs) ->
        val sMeta = QuranMetadata.getSurah(surahNum)
        val startsHere = surahAyahs.any { it.ayahNumber == 1 }
        if (startsHere) {
            result += PreTokenizedBlock.SurahBanner(sMeta)
            if (surahNum != 1 && surahNum != 9) {
                result += PreTokenizedBlock.BasmalahLine(surahNum)
            }
        }

        val tokens = mutableListOf<MushafLineToken>()
        surahAyahs.forEach { ayah ->
            val rawBase = when (script) {
                QuranScriptType.TAJWEED -> ayah.tajweedMarkup.ifBlank { ayah.uthmaniText }
                QuranScriptType.INDO_PAK -> ayah.simpleText.ifBlank { ayah.uthmaniText }
                QuranScriptType.UTHMANI -> ayah.uthmaniText
            }
            val cleanedRaw = QuranMetadata.stripLeadingBasmalahIfPresent(
                text = rawBase,
                surahNumber = ayah.surahNumber,
                ayahNumber = ayah.ayahNumber
            )

            val ayahAnnotated = if (script == QuranScriptType.TAJWEED) {
                TajweedParser.parseTajweedAnnotated(cleanedRaw, inkColor)
            } else {
                buildAnnotatedWithWaqfAccent(cleanedRaw, inkColor, waqfColor)
            }

            // Split cleanly on whitespace while keeping standalone Waqf symbols glued to previous word
            val rawStr = ayahAnnotated.text
            var cursor = 0
            val wordRanges = mutableListOf<IntRange>()
            while (cursor < rawStr.length) {
                while (cursor < rawStr.length && rawStr[cursor].isWhitespace()) cursor++
                if (cursor >= rawStr.length) break
                val start = cursor
                while (cursor < rawStr.length && !rawStr[cursor].isWhitespace()) cursor++
                val end = cursor
                wordRanges += start until end
            }

            val mergedWords = mutableListOf<AnnotatedString>()
            wordRanges.forEach { range ->
                val slice = ayahAnnotated.subSequence(range.first, range.last + 1)
                val onlyWaqf = slice.text.all { it in WAQF_CODEPOINTS }
                if (onlyWaqf && mergedWords.isNotEmpty()) {
                    val prev = mergedWords.removeAt(mergedWords.lastIndex)
                    mergedWords += buildAnnotatedString {
                        append(prev)
                        append("\u2009")
                        append(slice)
                    }
                } else {
                    mergedWords += slice
                }
            }

            mergedWords.forEach { word ->
                val plain = word.text
                tokens += MushafLineToken.Word(
                    text = word,
                    ayah = ayah,
                    hasWaqfLazim = '\u06D8' in plain,
                    hasSaktah = '\u06DC' in plain,
                    hasSajdah = '\u06E9' in plain,
                    hasRubElHizb = '\u06DE' in plain,
                )
            }

            val idxInPage = ayahs.indexOf(ayah)
            val rukuEnded = (idxInPage in 0 until ayahs.lastIndex && ayahs[idxInPage + 1].ruku != ayah.ruku) ||
                (ayah.ayahNumber == sMeta.ayahCount)

            tokens += MushafLineToken.AyahEnd(
                ayah = ayah,
                rukuEnded = rukuEnded,
            )
        }

        if (tokens.isNotEmpty()) {
            result += PreTokenizedBlock.VerseTokens(tokens)
        }
    }

    return result
}

private fun buildAnnotatedWithWaqfAccent(
    text: String,
    inkColor: Color,
    waqfColor: Color,
): AnnotatedString = buildAnnotatedString {
    var i = 0
    while (i < text.length) {
        val ch = text[i]
        if (ch in WAQF_CODEPOINTS) {
            withStyle(SpanStyle(color = waqfColor, fontWeight = FontWeight.Bold)) {
                append(ch)
            }
        } else {
            withStyle(SpanStyle(color = inkColor)) {
                append(ch)
            }
        }
        i++
    }
}

private fun packIntoFifteenLines(
    blocks: List<PreTokenizedBlock>,
    maxWidthPx: Float,
    targetFontSp: Float,
    enforceMax15Lines: Boolean,
    fontFamily: FontFamily,
    textMeasurer: TextMeasurer,
    density: androidx.compose.ui.unit.Density,
): PackedHafiziPage {
    var candidateFont = targetFontSp
    val minFont = 11.5f

    while (true) {
        val slots = measureAndPackOnce(
            blocks = blocks,
            maxWidthPx = maxWidthPx,
            fontSizeSp = candidateFont,
            fontFamily = fontFamily,
            textMeasurer = textMeasurer,
            density = density,
        )
        if (!enforceMax15Lines || slots.size <= HAFIZI_LINES_PER_PAGE || candidateFont <= minFont) {
            return PackedHafiziPage(slots = slots, fontSizeSp = candidateFont)
        }
        candidateFont = (candidateFont - 0.5f).coerceAtLeast(minFont)
    }
}

private fun measureAndPackOnce(
    blocks: List<PreTokenizedBlock>,
    maxWidthPx: Float,
    fontSizeSp: Float,
    fontFamily: FontFamily,
    textMeasurer: TextMeasurer,
    density: androidx.compose.ui.unit.Density,
): List<HafiziGridSlot> {
    val slots = mutableListOf<HafiziGridSlot>()
    val style = TextStyle(
        fontFamily = fontFamily,
        fontSize = fontSizeSp.sp,
    )
    val minSpacePx = with(density) { (fontSizeSp * 0.26f).sp.toPx() }
    val medallionBaseDiamDp = (fontSizeSp * 1.18f).coerceIn(18f, 34f)

    blocks.forEach { block ->
        when (block) {
            is PreTokenizedBlock.SurahBanner -> slots += HafiziGridSlot.SurahHeader(block.surah)
            is PreTokenizedBlock.BasmalahLine -> slots += HafiziGridSlot.Basmalah(block.surahNumber)
            is PreTokenizedBlock.VerseTokens -> {
                val linesOfTokens = mutableListOf<Pair<List<MushafLineToken>, Float>>()
                var currentLine = mutableListOf<MushafLineToken>()
                var currentWidthPx = 0f

                block.tokens.forEach { token ->
                    val w = when (token) {
                        is MushafLineToken.Word ->
                            textMeasurer.measure(token.text, style = style, maxLines = 1).size.width.toFloat()
                        is MushafLineToken.AyahEnd -> {
                            val digits = QuranMetadata.toArabicIndic(token.ayah.ayahNumber).length
                            val extraDp = if (digits >= 3) 4f else 0f
                            with(density) { (medallionBaseDiamDp + extraDp).dp.toPx() }
                        }
                    }

                    val neededWithSpace = if (currentLine.isEmpty()) w else currentWidthPx + minSpacePx + w
                    if (currentLine.isNotEmpty() && neededWithSpace > maxWidthPx) {
                        linesOfTokens += currentLine.toList() to currentWidthPx
                        currentLine = mutableListOf(token)
                        currentWidthPx = w
                    } else {
                        currentLine += token
                        currentWidthPx = neededWithSpace
                    }
                }
                if (currentLine.isNotEmpty()) {
                    linesOfTokens += currentLine.toList() to currentWidthPx
                }

                linesOfTokens.forEachIndexed { lineIdx, (lineTokens, widthPx) ->
                    val isLastInBlock = lineIdx == linesOfTokens.lastIndex
                    val isShortClosing = isLastInBlock && widthPx < maxWidthPx * 0.72f
                    val rukuEndAyah = lineTokens
                        .filterIsInstance<MushafLineToken.AyahEnd>()
                        .firstOrNull { it.rukuEnded }
                        ?.ayah

                    slots += HafiziGridSlot.VerseLine(
                        tokens = lineTokens,
                        isShortClosingLine = isShortClosing,
                        marginRukuNumber = rukuEndAyah?.ruku,
                        showWaqfLazimNote = lineTokens.any { it.hasWaqfLazim },
                        showSajdahNote = lineTokens.any { it.hasSajdah },
                        showSaktahNote = lineTokens.any { it.hasSaktah },
                        showRubElHizbNote = lineTokens.any { it.hasRubElHizb },
                    )
                }
            }
        }
    }

    return slots
}

package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.audio.AudioPlaybackState
import com.example.audio.AudioPlayerManager
import com.example.data.local.BookmarkEntity
import com.example.data.local.DownloadedAudioEntity
import com.example.data.local.QuranLayoutStyle
import com.example.data.local.QuranScriptType
import com.example.data.local.UserPreferencesState
import com.example.data.repository.AyahDisplayItem
import com.example.data.seed.HidayahExtendedData
import com.example.data.seed.QariCatalog
import com.example.data.seed.QariInfo
import com.example.data.seed.QuranMetadata
import com.example.ui.theme.ArabicFontChoice
import com.example.ui.theme.QuranReadingMode
import com.example.ui.theme.TajweedGhunnah
import com.example.ui.theme.TajweedIdgham
import com.example.ui.theme.TajweedIkhfa
import com.example.ui.theme.TajweedIqlab
import com.example.ui.theme.TajweedMadd
import com.example.ui.theme.TajweedQalqalah
import com.example.ui.theme.toFontFamily
import com.example.ui.viewmodel.SurahLoadState
import com.example.utils.TajweedParser
import com.example.utils.WallpaperAndAlertUtils

private data class EditionOption(val code: String, val label: String)

private val translationEditions = listOf(
    EditionOption("en.sahih", "English (Sahih International)"),
    EditionOption("bn.bengali", "Bengali (Muhiuddin Khan)"),
    EditionOption("ur.jalandhry", "Urdu (Jalandhry)"),
    EditionOption("hi.hindi", "Hindi (Suhel Farooq)"),
    EditionOption("id.indonesian", "Indonesian (Kemenag)"),
    EditionOption("tr.diyanet", "Turkish (Diyanet)"),
    EditionOption("fr.hamidullah", "French (Hamidullah)"),
    EditionOption("ms.basmeih", "Malay (Basmeih)"),
    EditionOption("ar.muyassar", "Arabic Tafsir (Al-Muyassar)"),
)

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun QuranReaderScreen(
    surahNumber: Int,
    initialAyah: Int,
    prefs: UserPreferencesState,
    ayahs: List<AyahDisplayItem>,
    loadState: SurahLoadState,
    bookmarks: List<BookmarkEntity>,
    favoriteQariIds: List<String>,
    downloadedAudio: List<DownloadedAudioEntity>,
    audioState: AudioPlaybackState,
    onBack: () -> Unit,
    onSelectSurah: (Int) -> Unit,
    onUpdateLastRead: (Int, Int) -> Unit,
    onSetScript: (QuranScriptType) -> Unit,
    onSetLayout: (QuranLayoutStyle) -> Unit,
    onSetArabicFont: (ArabicFontChoice) -> Unit,
    onSetArabicFontSize: (Float) -> Unit,
    onSetReadingMode: (QuranReadingMode) -> Unit,
    onSetTranslationEdition: (String) -> Unit,
    onSetShowTranslation: (Boolean) -> Unit,
    onSetShowWordByWord: (Boolean) -> Unit,
    onSetShowTransliteration: (Boolean) -> Unit,
    onUpsertBookmark: (Int, Int, String) -> Unit,
    onRemoveBookmark: (Int, Int) -> Unit,
    onSelectQari: (String) -> Unit,
    onToggleFavoriteQari: (String) -> Unit,
    onDownloadSurah: (Int, QariInfo, (Result<Int>) -> Unit) -> Unit,
    onRetryLoad: () -> Unit,
    onAskAiAboutAyah: (String) -> Unit,
) {
    val context = LocalContext.current
    val clipboard = LocalClipboardManager.current
    val surahMeta = QuranMetadata.getSurah(surahNumber)
    val listState = rememberLazyListState()

    var showReaderSettings by remember { mutableStateOf(false) }
    var showAudioSheet by remember { mutableStateOf(false) }
    var bookmarkTargetAyah by remember { mutableStateOf<AyahDisplayItem?>(null) }
    var tafsirTargetAyah by remember { mutableStateOf<AyahDisplayItem?>(null) }
    var statusBanner by remember { mutableStateOf<String?>(null) }

    val (surfaceBg, surfaceFg, cardBg, borderAccent) = when (prefs.readingMode) {
        QuranReadingMode.LIGHT -> listOf(Color(0xFFFAF7F0), Color(0xFF141E1A), Color.White, Color(0xFFD6C9A6))
        QuranReadingMode.DARK -> listOf(Color(0xFF0C1015), Color(0xFFF0ECE1), Color(0xFF161C24), Color(0xFF423A24))
        QuranReadingMode.SEPIA -> listOf(Color(0xFFF4EAD5), Color(0xFF2D2318), Color(0xFFFCF4E3), Color(0xFFC5A879))
        QuranReadingMode.COMFORT -> listOf(Color(0xFFEAF2EE), Color(0xFF13261E), Color(0xFFF6FBF8), Color(0xFF9AC2B0))
    }

    LaunchedEffect(surahNumber, initialAyah, ayahs.size) {
        if (ayahs.isNotEmpty()) {
            onUpdateLastRead(surahNumber, initialAyah)
            val idx = ayahs.indexOfFirst { it.ayahNumber == initialAyah }.coerceAtLeast(0)
            if (idx > 0) listState.scrollToItem(idx)
        }
    }

    // Synchronized text highlight + auto-scroll while audio plays
    LaunchedEffect(audioState.surahNumber, audioState.ayahNumber, audioState.isPlaying) {
        if (audioState.isPlaying && audioState.surahNumber == surahNumber && ayahs.isNotEmpty()) {
            val idx = ayahs.indexOfFirst { it.ayahNumber == audioState.ayahNumber }
            if (idx >= 0) {
                listState.animateScrollToItem(idx)
            }
        }
    }

    val arabicFontFamily = prefs.arabicFont.toFontFamily()
    val bookmarkedAyahNums = remember(bookmarks, surahNumber) {
        bookmarks.filter { it.surahNumber == surahNumber }.associateBy { it.ayahNumber }
    }
    val downloadedCountForSurah = remember(downloadedAudio, surahNumber, audioState.qari.id) {
        downloadedAudio.count { it.surahNumber == surahNumber && it.qariId == audioState.qari.id }
    }

    Scaffold(
        containerColor = surfaceBg,
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            "${surahMeta.number}. ${surahMeta.englishName} • ${surahMeta.arabicName}",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            "${surahMeta.revelationType} • ${surahMeta.ayahCount} Ayahs • ${prefs.quranLayout.label}",
                            style = MaterialTheme.typography.labelSmall
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack, modifier = Modifier.testTag("reader_back_button")) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(
                        onClick = { showAudioSheet = true },
                        modifier = Modifier.testTag("reader_audio_settings_button")
                    ) {
                        Icon(Icons.Filled.GraphicEq, contentDescription = "Qari & Audio Controls")
                    }
                    IconButton(
                        onClick = { showReaderSettings = true },
                        modifier = Modifier.testTag("reader_display_settings_button")
                    ) {
                        Icon(Icons.Filled.Tune, contentDescription = "Script & Reading Mode")
                    }
                }
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentAlignment = Alignment.TopCenter
        ) {
            Column(
                modifier = Modifier
                    .widthIn(max = 840.dp)
                    .fillMaxSize()
            ) {
                // Quick action strip
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState())
                        .padding(horizontal = 14.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val isSurahPlaying = audioState.isPlaying && audioState.surahNumber == surahNumber
                    Button(
                        onClick = {
                            if (isSurahPlaying) {
                                AudioPlayerManager.togglePlayPause(context)
                            } else {
                                AudioPlayerManager.playSurahFromStart(
                                    context,
                                    surahNumber,
                                    QariCatalog.findById(prefs.selectedQariId)
                                )
                            }
                        },
                        modifier = Modifier.testTag("reader_play_surah_button")
                    ) {
                        Icon(
                            if (isSurahPlaying) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                            contentDescription = null
                        )
                        Spacer(Modifier.width(4.dp))
                        Text(if (isSurahPlaying) "Pause" else "Play Surah")
                    }

                    FilterChip(
                        selected = prefs.quranScript == QuranScriptType.TAJWEED,
                        onClick = {
                            onSetScript(
                                if (prefs.quranScript == QuranScriptType.TAJWEED) {
                                    QuranScriptType.UTHMANI
                                } else {
                                    QuranScriptType.TAJWEED
                                }
                            )
                        },
                        label = { Text("Tajweed Colors") }
                    )

                    FilterChip(
                        selected = prefs.showWordByWord,
                        onClick = { onSetShowWordByWord(!prefs.showWordByWord) },
                        label = { Text("Word-by-Word") }
                    )

                    FilterChip(
                        selected = downloadedCountForSurah >= surahMeta.ayahCount,
                        onClick = {
                            val qari = QariCatalog.findById(prefs.selectedQariId)
                            statusBanner = "Downloading ${surahMeta.englishName} (${qari.name})..."
                            onDownloadSurah(surahNumber, qari) { res ->
                                statusBanner = res.fold(
                                    onSuccess = { "Downloaded $it Ayahs for offline listening!" },
                                    onFailure = { "Download error: ${it.localizedMessage}" }
                                )
                            }
                        },
                        leadingIcon = { Icon(Icons.Filled.Download, contentDescription = null, modifier = Modifier.size(16.dp)) },
                        label = {
                            Text(
                                if (downloadedCountForSurah >= surahMeta.ayahCount) "Offline Ready" else "Download MP3"
                            )
                        }
                    )
                }

                if (prefs.quranScript == QuranScriptType.TAJWEED) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState())
                            .padding(horizontal = 16.dp, vertical = 4.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        TajweedLegendChip("Ghunnah", TajweedGhunnah)
                        TajweedLegendChip("Qalqalah", TajweedQalqalah)
                        TajweedLegendChip("Madd", TajweedMadd)
                        TajweedLegendChip("Ikhfa", TajweedIkhfa)
                        TajweedLegendChip("Idgham", TajweedIdgham)
                        TajweedLegendChip("Iqlab", TajweedIqlab)
                    }
                }

                (statusBanner ?: audioState.downloadProgressText)?.let { banner ->
                    Text(
                        text = banner,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                    )
                }

                if (ayahs.isEmpty() && loadState is SurahLoadState.Loading) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            CircularProgressIndicator()
                            Spacer(Modifier.height(10.dp))
                            Text("Loading Surah ${surahMeta.englishName}...", color = surfaceFg)
                        }
                    }
                } else if (ayahs.isEmpty() && loadState is SurahLoadState.Error) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.padding(24.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Text(loadState.message, color = surfaceFg, textAlign = TextAlign.Center)
                            Button(onClick = onRetryLoad) { Text("Retry") }
                        }
                    }
                } else if (prefs.quranLayout == QuranLayoutStyle.MADANI_MUSHAF ||
                    prefs.quranLayout == QuranLayoutStyle.HAFIZI_PAGE
                ) {
                    val isHafizi = prefs.quranLayout == QuranLayoutStyle.HAFIZI_PAGE
                    val pages = remember(ayahs) { ayahs.groupBy { it.page }.toSortedMap() }

                    LazyColumn(
                        state = listState,
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        if (surahNumber != 1 && surahNumber != 9) {
                            item {
                                Text(
                                    text = "بِسْمِ ٱللَّهِ ٱلرَّحْمَٰنِ ٱلرَّحِيمِ",
                                    fontFamily = arabicFontFamily,
                                    fontSize = (prefs.arabicFontSizeSp + 2).sp,
                                    color = surfaceFg,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        }
                        pages.forEach { (pageNum, pageAyahs) ->
                            item(key = "page_$pageNum") {
                                Surface(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .border(
                                            width = if (isHafizi) 2.dp else 1.5.dp,
                                            color = borderAccent,
                                            shape = RoundedCornerShape(18.dp)
                                        ),
                                    color = if (isHafizi) Color(0xFFFBF3DF) else cardBg,
                                    shape = RoundedCornerShape(18.dp)
                                ) {
                                    Column(
                                        modifier = Modifier.padding(18.dp),
                                        verticalArrangement = Arrangement.spacedBy(12.dp)
                                    ) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Text(
                                                if (isHafizi) "Hafizi 15-Line Layout" else "Madani Mushaf",
                                                style = MaterialTheme.typography.labelSmall,
                                                color = MaterialTheme.colorScheme.secondary
                                            )
                                            Text(
                                                "Page $pageNum • Juz ${pageAyahs.first().juz}",
                                                style = MaterialTheme.typography.labelSmall,
                                                color = MaterialTheme.colorScheme.secondary
                                            )
                                        }
                                        val pageFg = if (isHafizi) Color(0xFF1F180D) else surfaceFg
                                        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                                            val combinedText = remember(pageAyahs, prefs.quranScript) {
                                                pageAyahs.joinToString(" ") { a ->
                                                    val raw = when (prefs.quranScript) {
                                                        QuranScriptType.UTHMANI -> a.uthmaniText
                                                        QuranScriptType.INDO_PAK -> a.simpleText
                                                        QuranScriptType.TAJWEED -> a.tajweedMarkup
                                                    }
                                                    val clean = if (prefs.quranScript == QuranScriptType.TAJWEED) {
                                                        TajweedParser.parseTajweedAnnotated(raw, pageFg).text
                                                    } else {
                                                        raw
                                                    }
                                                    "$clean ﴿${a.ayahNumber}﴾"
                                                }
                                            }
                                            Text(
                                                text = combinedText,
                                                fontFamily = arabicFontFamily,
                                                fontSize = prefs.arabicFontSizeSp.sp,
                                                lineHeight = (prefs.arabicFontSizeSp * 1.85f).sp,
                                                color = pageFg,
                                                textAlign = TextAlign.Justify,
                                                modifier = Modifier.fillMaxWidth()
                                            )
                                        }
                                    }
                                }
                            }
                        }
                        item { SurahPrevNextRow(surahNumber, onSelectSurah) }
                    }
                } else {
                    LazyColumn(
                        state = listState,
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        if (surahNumber != 1 && surahNumber != 9) {
                            item {
                                Surface(
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(16.dp),
                                    color = cardBg,
                                    border = BorderStroke(1.dp, borderAccent.copy(alpha = 0.5f))
                                ) {
                                    Text(
                                        text = "بِسْمِ ٱللَّهِ ٱلرَّحْمَٰنِ ٱلرَّحِيمِ",
                                        fontFamily = arabicFontFamily,
                                        fontSize = (prefs.arabicFontSizeSp + 2).sp,
                                        color = surfaceFg,
                                        textAlign = TextAlign.Center,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(14.dp)
                                    )
                                }
                            }
                        }

                        items(ayahs, key = { it.ayahNumber }) { ayah ->
                            val isCurrentlyPlaying = audioState.isPlaying &&
                                audioState.surahNumber == surahNumber &&
                                audioState.ayahNumber == ayah.ayahNumber
                            val bookmark = bookmarkedAyahNums[ayah.ayahNumber]

                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("ayah_card_${ayah.ayahNumber}"),
                                shape = RoundedCornerShape(18.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = if (isCurrentlyPlaying) {
                                        MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.55f)
                                    } else {
                                        cardBg
                                    }
                                ),
                                border = if (isCurrentlyPlaying) {
                                    BorderStroke(1.5.dp, MaterialTheme.colorScheme.primary)
                                } else null
                            ) {
                                Column(
                                    modifier = Modifier.padding(16.dp),
                                    verticalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Surface(
                                            color = MaterialTheme.colorScheme.primaryContainer,
                                            shape = RoundedCornerShape(10.dp)
                                        ) {
                                            Text(
                                                "${surahNumber}:${ayah.ayahNumber}",
                                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                                style = MaterialTheme.typography.labelLarge,
                                                fontWeight = FontWeight.Bold,
                                                color = MaterialTheme.colorScheme.primary
                                            )
                                        }
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            IconButton(
                                                onClick = {
                                                    onUpdateLastRead(surahNumber, ayah.ayahNumber)
                                                    AudioPlayerManager.playAyah(
                                                        context,
                                                        surahNumber,
                                                        ayah.ayahNumber,
                                                        QariCatalog.findById(prefs.selectedQariId)
                                                    )
                                                },
                                                modifier = Modifier.testTag("play_ayah_${ayah.ayahNumber}")
                                            ) {
                                                Icon(
                                                    if (isCurrentlyPlaying) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                                                    contentDescription = "Play Ayah ${ayah.ayahNumber}",
                                                    tint = MaterialTheme.colorScheme.primary
                                                )
                                            }
                                            IconButton(onClick = { bookmarkTargetAyah = ayah }) {
                                                Icon(
                                                    if (bookmark != null) Icons.Filled.Bookmark else Icons.Filled.BookmarkBorder,
                                                    contentDescription = "Bookmark Ayah",
                                                    tint = if (bookmark != null) MaterialTheme.colorScheme.secondary else surfaceFg
                                                )
                                            }
                                            IconButton(onClick = { tafsirTargetAyah = ayah }) {
                                                Icon(
                                                    Icons.AutoMirrored.Filled.MenuBook,
                                                    contentDescription = "View Tafsir",
                                                    tint = surfaceFg
                                                )
                                            }
                                            IconButton(onClick = {
                                                val txt = "${ayah.uthmaniText}\n\n${ayah.translationText}\n(${surahMeta.englishName} ${surahNumber}:${ayah.ayahNumber})"
                                                clipboard.setText(AnnotatedString(txt))
                                                statusBanner = "Copied ${surahNumber}:${ayah.ayahNumber} to clipboard"
                                            }) {
                                                Icon(Icons.Filled.ContentCopy, contentDescription = "Copy Ayah", tint = surfaceFg)
                                            }
                                            IconButton(onClick = {
                                                val txt = "${ayah.uthmaniText}\n\n${ayah.translationText}\n(${surahMeta.englishName} ${surahNumber}:${ayah.ayahNumber})"
                                                WallpaperAndAlertUtils.shareText(context, "${surahMeta.englishName} ${surahNumber}:${ayah.ayahNumber}", txt)
                                            }) {
                                                Icon(Icons.Filled.Share, contentDescription = "Share Ayah", tint = surfaceFg)
                                            }
                                            IconButton(onClick = {
                                                onAskAiAboutAyah("Please explain ${surahMeta.englishName} ($surahNumber:${ayah.ayahNumber}) and its practical lesson.")
                                            }) {
                                                Icon(Icons.Filled.Psychology, contentDescription = "Ask AI", tint = MaterialTheme.colorScheme.secondary)
                                            }
                                        }
                                    }

                                    // Arabic Verse
                                    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                                        when (prefs.quranScript) {
                                            QuranScriptType.TAJWEED -> {
                                                val annotated = remember(ayah.tajweedMarkup, surfaceFg) {
                                                    TajweedParser.parseTajweedAnnotated(ayah.tajweedMarkup, surfaceFg)
                                                }
                                                Text(
                                                    text = annotated,
                                                    fontFamily = arabicFontFamily,
                                                    fontSize = prefs.arabicFontSizeSp.sp,
                                                    lineHeight = (prefs.arabicFontSizeSp * 1.8f).sp,
                                                    modifier = Modifier.fillMaxWidth()
                                                )
                                            }
                                            QuranScriptType.INDO_PAK -> {
                                                Text(
                                                    text = ayah.simpleText,
                                                    fontFamily = arabicFontFamily,
                                                    fontSize = prefs.arabicFontSizeSp.sp,
                                                    lineHeight = (prefs.arabicFontSizeSp * 1.8f).sp,
                                                    color = surfaceFg,
                                                    modifier = Modifier.fillMaxWidth()
                                                )
                                            }
                                            QuranScriptType.UTHMANI -> {
                                                Text(
                                                    text = ayah.uthmaniText,
                                                    fontFamily = arabicFontFamily,
                                                    fontSize = prefs.arabicFontSizeSp.sp,
                                                    lineHeight = (prefs.arabicFontSizeSp * 1.8f).sp,
                                                    color = surfaceFg,
                                                    modifier = Modifier.fillMaxWidth()
                                                )
                                            }
                                        }
                                    }

                                    // Word-by-word breakdown
                                    if (prefs.showWordByWord) {
                                        val words = remember(ayah.simpleText) {
                                            ayah.simpleText.split(Regex("\\s+")).filter { it.isNotBlank() }
                                        }
                                        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                                            FlowRow(
                                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                                verticalArrangement = Arrangement.spacedBy(6.dp),
                                                modifier = Modifier.fillMaxWidth()
                                            ) {
                                                words.forEachIndexed { wIdx, word ->
                                                    Surface(
                                                        shape = RoundedCornerShape(8.dp),
                                                        color = surfaceBg.copy(alpha = 0.85f),
                                                        border = BorderStroke(1.dp, borderAccent.copy(alpha = 0.45f))
                                                    ) {
                                                        Column(
                                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                                            horizontalAlignment = Alignment.CenterHorizontally
                                                        ) {
                                                            Text(
                                                                word,
                                                                fontFamily = arabicFontFamily,
                                                                fontSize = 16.sp,
                                                                color = surfaceFg
                                                            )
                                                            Text(
                                                                "#${wIdx + 1}",
                                                                style = MaterialTheme.typography.labelSmall,
                                                                color = surfaceFg.copy(alpha = 0.6f)
                                                            )
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }

                                    if (prefs.showTransliteration && ayah.transliteration.isNotBlank() &&
                                        prefs.quranLayout != QuranLayoutStyle.SIMPLE_READING
                                    ) {
                                        Text(
                                            text = ayah.transliteration,
                                            style = MaterialTheme.typography.bodySmall,
                                            color = surfaceFg.copy(alpha = 0.72f)
                                        )
                                    }

                                    if (prefs.showTranslation && ayah.translationText.isNotBlank() &&
                                        prefs.quranLayout != QuranLayoutStyle.SIMPLE_READING
                                    ) {
                                        Text(
                                            text = ayah.translationText,
                                            style = MaterialTheme.typography.bodyLarge,
                                            color = surfaceFg.copy(alpha = 0.92f)
                                        )
                                    }

                                    if (bookmark?.note?.isNotBlank() == true) {
                                        Text(
                                            text = "Personal Note: ${bookmark.note}",
                                            style = MaterialTheme.typography.labelMedium,
                                            color = MaterialTheme.colorScheme.secondary
                                        )
                                    }
                                }
                            }
                        }

                        item { SurahPrevNextRow(surahNumber, onSelectSurah) }
                    }
                }
            }
        }
    }

    // Reader Display Settings Sheet
    if (showReaderSettings) {
        ModalBottomSheet(onDismissRequest = { showReaderSettings = false }) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 18.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text("Quran Display & Reading Settings", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)

                Text("Script Style", style = MaterialTheme.typography.labelLarge)
                Row(modifier = Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    QuranScriptType.entries.forEach { s ->
                        FilterChip(selected = prefs.quranScript == s, onClick = { onSetScript(s) }, label = { Text(s.label) })
                    }
                }

                Text("Layout Mode", style = MaterialTheme.typography.labelLarge)
                Row(modifier = Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    QuranLayoutStyle.entries.forEach { l ->
                        FilterChip(selected = prefs.quranLayout == l, onClick = { onSetLayout(l) }, label = { Text(l.label) })
                    }
                }

                Text("Reading Theme", style = MaterialTheme.typography.labelLarge)
                Row(modifier = Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    QuranReadingMode.entries.forEach { m ->
                        FilterChip(selected = prefs.readingMode == m, onClick = { onSetReadingMode(m) }, label = { Text(m.displayName) })
                    }
                }

                Text("Arabic Font & Size (${prefs.arabicFontSizeSp.toInt()} sp)", style = MaterialTheme.typography.labelLarge)
                Row(modifier = Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    ArabicFontChoice.entries.forEach { f ->
                        FilterChip(selected = prefs.arabicFont == f, onClick = { onSetArabicFont(f) }, label = { Text(f.displayName) })
                    }
                }
                Slider(
                    value = prefs.arabicFontSizeSp,
                    onValueChange = onSetArabicFontSize,
                    valueRange = 18f..44f
                )

                Text("Translation Edition", style = MaterialTheme.typography.labelLarge)
                Row(modifier = Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    translationEditions.forEach { ed ->
                        FilterChip(
                            selected = prefs.translationEdition == ed.code,
                            onClick = { onSetTranslationEdition(ed.code) },
                            label = { Text(ed.label) }
                        )
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Show Translation")
                    Switch(checked = prefs.showTranslation, onCheckedChange = onSetShowTranslation)
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Show Transliteration")
                    Switch(checked = prefs.showTransliteration, onCheckedChange = onSetShowTransliteration)
                }
                Spacer(Modifier.height(16.dp))
            }
        }
    }

    // Qari & Audio Player Controls Sheet (20 Qaris + Sleep Timer + Speed + Hifz Loop)
    if (showAudioSheet) {
        var qariQuery by remember { mutableStateOf("") }
        val filteredQaris = remember(qariQuery, favoriteQariIds) {
            QariCatalog.qaris.filter {
                qariQuery.isBlank() ||
                    it.name.contains(qariQuery, ignoreCase = true) ||
                    it.country.contains(qariQuery, ignoreCase = true)
            }.sortedByDescending { it.id in favoriteQariIds }
        }
        var loopStart by remember { mutableStateOf((audioState.hifzLoopStart ?: 1).toString()) }
        var loopEnd by remember { mutableStateOf((audioState.hifzLoopEnd ?: surahMeta.ayahCount.coerceAtMost(5)).toString()) }

        ModalBottomSheet(onDismissRequest = { showAudioSheet = false }) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 18.dp, vertical = 10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text("Quran Audio & Qari Library (20 Reciters)", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)

                Text("Playback Speed (${audioState.playbackSpeed}x)", style = MaterialTheme.typography.labelLarge)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf(0.75f, 1.0f, 1.25f, 1.5f, 2.0f).forEach { spd ->
                        FilterChip(
                            selected = audioState.playbackSpeed == spd,
                            onClick = { AudioPlayerManager.setSpeed(spd) },
                            label = { Text("${spd}x") }
                        )
                    }
                }

                Text("Ayah Repeat & Sleep Timer", style = MaterialTheme.typography.labelLarge)
                Row(modifier = Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf(1 to "1x", 3 to "3x", 5 to "5x", 0 to "∞ Loop").forEach { (count, label) ->
                        FilterChip(
                            selected = audioState.ayahRepeatTarget == count,
                            onClick = { AudioPlayerManager.setAyahRepeat(count) },
                            label = { Text("Ayah $label") }
                        )
                    }
                    FilterChip(
                        selected = audioState.repeatSurah,
                        onClick = { AudioPlayerManager.setRepeatSurah(!audioState.repeatSurah) },
                        label = { Text("Repeat Surah") }
                    )
                    FilterChip(
                        selected = audioState.autoNextSurah,
                        onClick = { AudioPlayerManager.setAutoNextSurah(!audioState.autoNextSurah) },
                        label = { Text("Auto-Next") }
                    )
                }

                Row(modifier = Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf(null to "Timer Off", 10 to "10m", 20 to "20m", 30 to "30m", 60 to "60m").forEach { (min, lbl) ->
                        FilterChip(
                            selected = audioState.sleepTimerMinutes == min,
                            onClick = { AudioPlayerManager.setSleepTimer(min) },
                            label = { Text(lbl) }
                        )
                    }
                }

                // Hifz Loop Range
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = loopStart,
                        onValueChange = { loopStart = it },
                        label = { Text("From Ayah") },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = loopEnd,
                        onValueChange = { loopEnd = it },
                        label = { Text("To Ayah") },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                    Button(onClick = {
                        val s = loopStart.toIntOrNull()?.coerceIn(1, surahMeta.ayahCount) ?: 1
                        val e = loopEnd.toIntOrNull()?.coerceIn(s, surahMeta.ayahCount) ?: s
                        AudioPlayerManager.setHifzLoopRange(s, e)
                        AudioPlayerManager.playAyah(context, surahNumber, s, QariCatalog.findById(prefs.selectedQariId))
                    }) { Text("Hifz Loop") }
                }

                OutlinedTextField(
                    value = qariQuery,
                    onValueChange = { qariQuery = it },
                    label = { Text("Search 20 Qaris") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                LazyColumn(modifier = Modifier.height(220.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    items(filteredQaris, key = { it.id }) { qari ->
                        val isFav = qari.id in favoriteQariIds
                        val isSelected = prefs.selectedQariId == qari.id
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .clickable {
                                    onSelectQari(qari.id)
                                    AudioPlayerManager.setQari(qari)
                                },
                            color = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(qari.name, fontWeight = FontWeight.Bold)
                                    Text("${qari.arabicName} • ${qari.style} • ${qari.country}", style = MaterialTheme.typography.bodySmall)
                                }
                                IconButton(onClick = { onToggleFavoriteQari(qari.id) }) {
                                    Icon(
                                        if (isFav) Icons.Filled.Star else Icons.Filled.StarBorder,
                                        contentDescription = "Favorite Qari",
                                        tint = MaterialTheme.colorScheme.secondary
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Bookmark + Personal Note Dialog
    bookmarkTargetAyah?.let { target ->
        val existing = bookmarkedAyahNums[target.ayahNumber]
        var noteText by remember(target) { mutableStateOf(existing?.note ?: "") }
        AlertDialog(
            onDismissRequest = { bookmarkTargetAyah = null },
            title = { Text("Bookmark ${surahMeta.englishName} ${surahNumber}:${target.ayahNumber}") },
            text = {
                OutlinedTextField(
                    value = noteText,
                    onValueChange = { noteText = it },
                    label = { Text("Personal reflection / note (optional)") },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 4
                )
            },
            confirmButton = {
                Button(onClick = {
                    onUpsertBookmark(surahNumber, target.ayahNumber, noteText)
                    bookmarkTargetAyah = null
                }) { Text("Save Bookmark") }
            },
            dismissButton = {
                Row {
                    if (existing != null) {
                        TextButton(onClick = {
                            onRemoveBookmark(surahNumber, target.ayahNumber)
                            bookmarkTargetAyah = null
                        }) { Text("Remove") }
                    }
                    TextButton(onClick = { bookmarkTargetAyah = null }) { Text("Cancel") }
                }
            }
        )
    }

    // Tafsir Insight Dialog
    tafsirTargetAyah?.let { target ->
        val matchingTafsir = HidayahExtendedData.tafsirs.firstOrNull {
            it.surahNumber == surahNumber && it.ayahNumber == target.ayahNumber
        }
        AlertDialog(
            onDismissRequest = { tafsirTargetAyah = null },
            title = { Text("Tafsir • ${surahMeta.englishName} ${surahNumber}:${target.ayahNumber}") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(target.uthmaniText, fontFamily = arabicFontFamily, fontSize = 20.sp)
                    if (matchingTafsir != null) {
                        Text(matchingTafsir.source, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                        Text(matchingTafsir.summaryEn, style = MaterialTheme.typography.bodyMedium)
                        Text(matchingTafsir.summaryBn, style = MaterialTheme.typography.bodySmall)
                    } else {
                        Text(target.translationText, style = MaterialTheme.typography.bodyMedium)
                        Text(
                            "Tap 'Ask AI Tafsir' below for an interactive classical commentary summary based on Ibn Kathir, Al-Jalalayn, and Al-Sa'di.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            },
            confirmButton = {
                Button(onClick = {
                    tafsirTargetAyah = null
                    onAskAiAboutAyah("Provide a concise Tafsir reflection on Surah ${surahMeta.englishName} (${surahNumber}:${target.ayahNumber}) referencing classical scholars like Ibn Kathir.")
                }) { Text("Ask AI Tafsir") }
            },
            dismissButton = {
                TextButton(onClick = { tafsirTargetAyah = null }) { Text("Close") }
            }
        )
    }
}

@Composable
private fun TajweedLegendChip(label: String, color: Color) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Box(modifier = Modifier.size(10.dp).clip(RoundedCornerShape(5.dp)).background(color))
        Text(label, style = MaterialTheme.typography.labelSmall)
    }
}

@Composable
private fun SurahPrevNextRow(surahNumber: Int, onSelectSurah: (Int) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        if (surahNumber > 1) {
            TextButton(onClick = { onSelectSurah(surahNumber - 1) }) {
                Text("<- Previous Surah (${surahNumber - 1})")
            }
        } else Spacer(Modifier.width(1.dp))

        if (surahNumber < 114) {
            TextButton(onClick = { onSelectSurah(surahNumber + 1) }) {
                Text("Next Surah (${surahNumber + 1}) ->")
            }
        }
    }
}

package com.example.ui.screens

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.pm.ActivityInfo
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.Fullscreen
import androidx.compose.material.icons.filled.FullscreenExit
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Splitscreen
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.ZoomIn
import androidx.compose.material.icons.filled.ZoomOut
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
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
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.audio.AudioPlaybackState
import com.example.audio.AudioPlayerManager
import com.example.data.local.BookmarkEntity
import com.example.data.local.DownloadedAudioEntity
import com.example.data.local.MushafEdition
import com.example.data.local.PageBookmarkEntity
import com.example.data.local.QuranLayoutStyle
import com.example.data.local.QuranScriptType
import com.example.data.local.UserPreferencesState
import com.example.data.repository.AyahDisplayItem
import com.example.data.seed.HidayahExtendedData
import com.example.data.seed.QariCatalog
import com.example.data.seed.QariInfo
import com.example.data.seed.QuranMetadata
import com.example.ui.components.AyahQuickActionSheet
import com.example.ui.components.HafiziMushafReaderArea
import com.example.ui.components.InlineQuranAudioBar
import com.example.ui.components.JumpToMushafDialog
import com.example.ui.components.QariAndAudioSheet
import com.example.ui.components.ReaderDisplaySettingsSheet
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
import kotlinx.coroutines.flow.Flow

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun QuranReaderScreen(
    surahNumber: Int,
    initialAyah: Int,
    currentPage: Int,
    prefs: UserPreferencesState,
    ayahs: List<AyahDisplayItem>,
    loadState: SurahLoadState,
    bookmarks: List<BookmarkEntity>,
    pageBookmarks: List<PageBookmarkEntity>,
    favoriteQariIds: List<String>,
    downloadedAudio: List<DownloadedAudioEntity>,
    audioState: AudioPlaybackState,
    observePage: (Int) -> Flow<List<AyahDisplayItem>>,
    onEnsurePageLoaded: (Int) -> Unit,
    onJumpToPage: (Int) -> Unit,
    onBack: () -> Unit,
    onSelectSurah: (Int) -> Unit,
    onUpdateLastRead: (Int, Int) -> Unit,
    onUpdateLastReadPage: (Int, Int?, Int?) -> Unit,
    onSetMushafEdition: (MushafEdition) -> Unit,
    onSetScript: (QuranScriptType) -> Unit,
    onSetLayout: (QuranLayoutStyle) -> Unit,
    onSetTwoPageSpread: (Int) -> Unit,
    onSetArabicFont: (ArabicFontChoice) -> Unit,
    onSetArabicFontSize: (Float) -> Unit,
    onSetPageMarginDp: (Float) -> Unit,
    onSetReadingMode: (QuranReadingMode) -> Unit,
    onSetBrightness: (Float) -> Unit,
    onSetKeepScreenAwake: (Boolean) -> Unit,
    onSetLockPortrait: (Boolean) -> Unit,
    onSetAudioVolume: (Float) -> Unit,
    onSetTranslationEdition: (String) -> Unit,
    onSetShowTranslation: (Boolean) -> Unit,
    onSetShowWordByWord: (Boolean) -> Unit,
    onSetShowTransliteration: (Boolean) -> Unit,
    onUpsertBookmark: (Int, Int, String) -> Unit,
    onRemoveBookmark: (Int, Int) -> Unit,
    onTogglePageBookmark: (Int, String) -> Unit,
    onSelectQari: (String) -> Unit,
    onToggleFavoriteQari: (String) -> Unit,
    onDownloadSurah: (Int, QariInfo, (Result<Int>) -> Unit) -> Unit,
    onRetryLoad: () -> Unit,
    onAskAiAboutAyah: (String) -> Unit,
) {
    val context = LocalContext.current
    val view = LocalView.current
    val clipboard = LocalClipboardManager.current
    val surahMeta = QuranMetadata.getSurah(surahNumber)
    val listState = rememberLazyListState()

    var showReaderSettings by remember { mutableStateOf(false) }
    var showAudioSheet by remember { mutableStateOf(false) }
    var showJumpDialog by remember { mutableStateOf(false) }
    var isFullScreen by remember { mutableStateOf(false) }
    var zoomScale by remember { mutableFloatStateOf(1.0f) }

    var tappedMushafAyah by remember { mutableStateOf<AyahDisplayItem?>(null) }
    var bookmarkTargetAyah by remember { mutableStateOf<AyahDisplayItem?>(null) }
    var tafsirTargetAyah by remember { mutableStateOf<AyahDisplayItem?>(null) }
    var statusBanner by remember { mutableStateOf<String?>(null) }

    val isMushafPageMode = prefs.quranLayout == QuranLayoutStyle.HAFIZI_PAGE ||
        prefs.quranLayout == QuranLayoutStyle.MADANI_MUSHAF

    // Keep Screen Awake, Reader Brightness & Optional Portrait Lock
    DisposableEffect(prefs.keepScreenAwake) {
        view.keepScreenOn = prefs.keepScreenAwake
        onDispose { view.keepScreenOn = false }
    }

    DisposableEffect(prefs.readerBrightness) {
        val activity = context.findActivity()
        val window = activity?.window
        val previous = window?.attributes?.screenBrightness ?: -1f
        if (window != null) {
            val lp = window.attributes
            lp.screenBrightness = prefs.readerBrightness
            window.attributes = lp
        }
        onDispose {
            if (window != null) {
                val lp = window.attributes
                lp.screenBrightness = previous
                window.attributes = lp
            }
        }
    }

    DisposableEffect(prefs.lockPortraitOrientation) {
        val activity = context.findActivity()
        val previous = activity?.requestedOrientation ?: ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
        if (activity != null) {
            activity.requestedOrientation = if (prefs.lockPortraitOrientation) {
                ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
            } else {
                ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
            }
        }
        onDispose {
            if (activity != null) {
                activity.requestedOrientation = previous
            }
        }
    }

    val isBanglaEmdadiaTheme = prefs.readingMode == QuranReadingMode.LIGHT && isMushafPageMode
    val (surfaceBg, surfaceFg, cardBg, borderAccent) = when (prefs.readingMode) {
        QuranReadingMode.LIGHT -> if (isBanglaEmdadiaTheme) {
            listOf(Color(0xFFF6F7F6), Color(0xFF141E1A), Color.White, Color(0xFF2B4C3F))
        } else {
            listOf(Color(0xFFF6F2E7), Color(0xFF141E1A), Color.White, Color(0xFFD6C9A6))
        }
        QuranReadingMode.DARK -> listOf(Color(0xFF0A0E0C), Color(0xFFF0ECE1), Color(0xFF141B17), Color(0xFF2D5E49))
        QuranReadingMode.SEPIA -> listOf(Color(0xFFF2E6CE), Color(0xFF2D2318), Color(0xFFFCF4E3), Color(0xFFC5A879))
        QuranReadingMode.COMFORT -> listOf(Color(0xFFE4EFE9), Color(0xFF13261E), Color(0xFFF6FBF8), Color(0xFF9AC2B0))
    }

    LaunchedEffect(surahNumber, initialAyah, ayahs.size) {
        if (ayahs.isNotEmpty()) {
            onUpdateLastRead(surahNumber, initialAyah)
            val idx = ayahs.indexOfFirst { it.ayahNumber == initialAyah }.coerceAtLeast(0)
            if (idx > 0 && !isMushafPageMode) listState.scrollToItem(idx)
        }
    }

    // Synchronized scrolling in Ayah-by-Ayah mode when audio advances
    LaunchedEffect(audioState.surahNumber, audioState.ayahNumber, audioState.isPlaying, isMushafPageMode) {
        if (audioState.isPlaying && audioState.surahNumber == surahNumber && ayahs.isNotEmpty() && !isMushafPageMode) {
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

    // Determine active Surah displayed by page header in Mushaf mode
    val activeAudioOrPageSurah = if (audioState.isPlaying) audioState.surahNumber else surahNumber
    val totalMushafPages = QuranMetadata.totalPagesForEdition(prefs.mushafEdition)
    val topBarBg = if (isBanglaEmdadiaTheme) Color(0xFF2B4C3F) else surfaceBg
    val topBarFg = if (isBanglaEmdadiaTheme) Color.White else surfaceFg

    Scaffold(
        containerColor = surfaceBg,
        topBar = {
            if (!isFullScreen) {
                TopAppBar(
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = topBarBg,
                        titleContentColor = topBarFg,
                        navigationIconContentColor = topBarFg,
                        actionIconContentColor = topBarFg,
                    ),
                    title = {
                        Column(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .clickable { showJumpDialog = true }
                        ) {
                            Text(
                                text = if (isMushafPageMode) {
                                    "Mushaf Page $currentPage / $totalMushafPages • ${prefs.mushafEdition.label}"
                                } else {
                                    "${surahMeta.number}. ${surahMeta.englishName} • ${surahMeta.arabicName}"
                                },
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = if (isMushafPageMode) {
                                    "Tap here to jump • Juz ${((currentPage - 1) / 20 + 1).coerceIn(1, 30)} / 30"
                                } else {
                                    "${surahMeta.revelationType} • ${surahMeta.ayahCount} Ayahs"
                                },
                                style = MaterialTheme.typography.labelSmall,
                                color = topBarFg.copy(alpha = 0.78f),
                                maxLines = 1
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
                            onClick = { showJumpDialog = true },
                            modifier = Modifier.testTag("reader_jump_button")
                        ) {
                            Icon(Icons.Filled.Search, contentDescription = "Jump to Page, Surah or Juz")
                        }
                        IconButton(
                            onClick = { isFullScreen = true },
                            modifier = Modifier.testTag("reader_fullscreen_button")
                        ) {
                            Icon(Icons.Filled.Fullscreen, contentDescription = "Enter full-screen reading")
                        }
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
                            Icon(Icons.Filled.Tune, contentDescription = "Mushaf & Display Settings")
                        }
                    }
                )
            }
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
                    .widthIn(max = 1120.dp)
                    .fillMaxSize()
            ) {
                // Quick Mode & Ergonomics Toolbar (hidden in Fullscreen)
                AnimatedVisibility(visible = !isFullScreen) {
                    Column {
                        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .horizontalScroll(rememberScrollState())
                                    .padding(horizontal = 12.dp, vertical = 4.dp),
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                FilterChip(
                                    selected = isMushafPageMode,
                                    onClick = { onSetLayout(QuranLayoutStyle.HAFIZI_PAGE) },
                                    label = { Text("Hafizi Mushaf") }
                                )
                                FilterChip(
                                    selected = prefs.quranLayout == QuranLayoutStyle.AYAH_BY_AYAH,
                                    onClick = { onSetLayout(QuranLayoutStyle.AYAH_BY_AYAH) },
                                    label = { Text("Ayah + Trans") }
                                )
                                FilterChip(
                                    selected = prefs.quranLayout == QuranLayoutStyle.SIMPLE_READING,
                                    onClick = { onSetLayout(QuranLayoutStyle.SIMPLE_READING) },
                                    label = { Text("Arabic Cards") }
                                )

                                if (isMushafPageMode) {
                                    FilterChip(
                                        selected = prefs.twoPageSpreadPref == 2,
                                        onClick = {
                                            val next = if (prefs.twoPageSpreadPref == 2) 1 else 2
                                            onSetTwoPageSpread(next)
                                        },
                                        leadingIcon = {
                                            Icon(Icons.Filled.Splitscreen, contentDescription = null, modifier = Modifier.size(15.dp))
                                        },
                                        label = {
                                            Text(
                                                when (prefs.twoPageSpreadPref) {
                                                    2 -> "2-Page Spread"
                                                    1 -> "Single Page"
                                                    else -> "Auto Spread"
                                                }
                                            )
                                        }
                                    )
                                    // Zoom Controls
                                    Surface(
                                        shape = RoundedCornerShape(10.dp),
                                        color = cardBg,
                                        border = BorderStroke(1.dp, borderAccent.copy(alpha = 0.5f))
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            IconButton(
                                                onClick = { zoomScale = (zoomScale - 0.1f).coerceIn(0.8f, 1.8f) },
                                                modifier = Modifier.size(32.dp)
                                            ) {
                                                Icon(Icons.Filled.ZoomOut, contentDescription = "Zoom out", modifier = Modifier.size(16.dp))
                                            }
                                            Text(
                                                "${(zoomScale * 100).toInt()}%",
                                                style = MaterialTheme.typography.labelSmall,
                                                fontWeight = FontWeight.Bold
                                            )
                                            IconButton(
                                                onClick = { zoomScale = (zoomScale + 0.1f).coerceIn(0.8f, 1.8f) },
                                                modifier = Modifier.size(32.dp)
                                            ) {
                                                Icon(Icons.Filled.ZoomIn, contentDescription = "Zoom in", modifier = Modifier.size(16.dp))
                                            }
                                        }
                                    }
                                } else {
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
                                        label = { Text("Tajweed") }
                                    )
                                    FilterChip(
                                        selected = prefs.showWordByWord,
                                        onClick = { onSetShowWordByWord(!prefs.showWordByWord) },
                                        label = { Text("Word-by-Word") }
                                    )
                                }
                            }
                        }

                        if (prefs.quranScript == QuranScriptType.TAJWEED ||
                            prefs.mushafEdition == MushafEdition.TAJWEED
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .horizontalScroll(rememberScrollState())
                                    .padding(horizontal = 16.dp, vertical = 2.dp),
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

                        statusBanner?.let { banner ->
                            Text(
                                text = banner,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 2.dp)
                            )
                        }
                    }
                }

                // MAIN READING AREA (takes all flexible vertical height above Inline Audio Player)
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                ) {
                    if (isMushafPageMode) {
                        HafiziMushafReaderArea(
                            currentPage = currentPage,
                            prefs = prefs,
                            zoomScale = zoomScale,
                            pageBookmarks = pageBookmarks,
                            audioState = audioState,
                            observePage = observePage,
                            onEnsurePageLoaded = onEnsurePageLoaded,
                            onPageChanged = { newPage, sNum, aNum ->
                                onUpdateLastReadPage(newPage, sNum, aNum)
                            },
                            onZoomDelta = { factor ->
                                zoomScale = (zoomScale * factor).coerceIn(0.8f, 1.8f)
                            },
                            onTogglePageBookmark = onTogglePageBookmark,
                            onAyahTapped = { ayah -> tappedMushafAyah = ayah },
                            onToggleControlsVisibility = { isFullScreen = !isFullScreen },
                            modifier = Modifier.fillMaxSize()
                        )
                    } else if (ayahs.isEmpty() && loadState is SurahLoadState.Loading) {
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
                                                    "${surahNumber}:${ayah.ayahNumber} • p.${ayah.page}",
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

                                        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                                            when (prefs.quranScript) {
                                                QuranScriptType.TAJWEED -> {
                                                    val annotated = remember(ayah.tajweedMarkup, surfaceFg) {
                                                        TajweedParser.parseTajweedAnnotated(ayah.tajweedMarkup, surfaceFg)
                                                    }
                                                    Text(
                                                        text = annotated,
                                                        fontFamily = arabicFontFamily,
                                                        fontSize = (prefs.arabicFontSizeSp * zoomScale).sp,
                                                        lineHeight = (prefs.arabicFontSizeSp * zoomScale * 1.8f).sp,
                                                        modifier = Modifier.fillMaxWidth()
                                                    )
                                                }
                                                QuranScriptType.INDO_PAK -> {
                                                    Text(
                                                        text = ayah.simpleText,
                                                        fontFamily = arabicFontFamily,
                                                        fontSize = (prefs.arabicFontSizeSp * zoomScale).sp,
                                                        lineHeight = (prefs.arabicFontSizeSp * zoomScale * 1.8f).sp,
                                                        color = surfaceFg,
                                                        modifier = Modifier.fillMaxWidth()
                                                    )
                                                }
                                                QuranScriptType.UTHMANI -> {
                                                    Text(
                                                        text = ayah.uthmaniText,
                                                        fontFamily = arabicFontFamily,
                                                        fontSize = (prefs.arabicFontSizeSp * zoomScale).sp,
                                                        lineHeight = (prefs.arabicFontSizeSp * zoomScale * 1.8f).sp,
                                                        color = surfaceFg,
                                                        modifier = Modifier.fillMaxWidth()
                                                    )
                                                }
                                            }
                                        }

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

                    // Exit Fullscreen Floating Chip
                    if (isFullScreen) {
                        FloatingActionButton(
                            onClick = { isFullScreen = false },
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .padding(14.dp)
                                .size(44.dp)
                                .testTag("reader_exit_fullscreen_button"),
                            containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.9f),
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        ) {
                            Icon(Icons.Filled.FullscreenExit, contentDescription = "Show reader controls")
                        }
                    }
                }

                // DOCKED INLINE QURAN AUDIO PLAYER DIRECTLY BELOW READING AREA
                AnimatedVisibility(visible = !isFullScreen) {
                    InlineQuranAudioBar(
                        surahNumber = activeAudioOrPageSurah,
                        selectedQariId = prefs.selectedQariId,
                        audioState = audioState,
                        audioVolume = prefs.audioVolume,
                        downloadedAudio = downloadedAudio,
                        onOpenQariSheet = { showAudioSheet = true },
                        onVolumeChange = onSetAudioVolume,
                        onDownloadSurah = { sNum, qari ->
                            val sMeta = QuranMetadata.getSurah(sNum)
                            statusBanner = "Downloading ${sMeta.englishName} (${qari.name})..."
                            onDownloadSurah(sNum, qari) { res ->
                                statusBanner = res.fold(
                                    onSuccess = { "Downloaded $it Ayahs for offline listening!" },
                                    onFailure = { "Download error: ${it.localizedMessage}" }
                                )
                            }
                        }
                    )
                }
            }
        }
    }

    // Ayah Quick Action Sheet (when tapping any verse in Mushaf Page mode)
    tappedMushafAyah?.let { tapped ->
        val isBm = bookmarks.any { it.surahNumber == tapped.surahNumber && it.ayahNumber == tapped.ayahNumber }
        val sMeta = QuranMetadata.getSurah(tapped.surahNumber)
        AyahQuickActionSheet(
            ayah = tapped,
            isBookmarked = isBm,
            prefs = prefs,
            onDismiss = { tappedMushafAyah = null },
            onPlayAyah = {
                onUpdateLastReadPage(tapped.page, tapped.surahNumber, tapped.ayahNumber)
                AudioPlayerManager.playAyah(
                    context,
                    tapped.surahNumber,
                    tapped.ayahNumber,
                    QariCatalog.findById(prefs.selectedQariId)
                )
            },
            onBookmark = { bookmarkTargetAyah = tapped },
            onTafsir = { tafsirTargetAyah = tapped },
            onCopy = {
                val txt = "${tapped.uthmaniText}\n\n${tapped.translationText}\n(${sMeta.englishName} ${tapped.surahNumber}:${tapped.ayahNumber})"
                clipboard.setText(AnnotatedString(txt))
                statusBanner = "Copied ${tapped.surahNumber}:${tapped.ayahNumber} to clipboard"
            },
            onShare = {
                val txt = "${tapped.uthmaniText}\n\n${tapped.translationText}\n(${sMeta.englishName} ${tapped.surahNumber}:${tapped.ayahNumber})"
                WallpaperAndAlertUtils.shareText(context, "${sMeta.englishName} ${tapped.surahNumber}:${tapped.ayahNumber}", txt)
            },
            onAskAi = {
                onAskAiAboutAyah("Please explain ${sMeta.englishName} (${tapped.surahNumber}:${tapped.ayahNumber}) and its practical lesson.")
            }
        )
    }

    if (showReaderSettings) {
        ReaderDisplaySettingsSheet(
            prefs = prefs,
            onDismiss = { showReaderSettings = false },
            onSetMushafEdition = onSetMushafEdition,
            onSetScript = onSetScript,
            onSetLayout = onSetLayout,
            onSetTwoPageSpread = onSetTwoPageSpread,
            onSetArabicFont = onSetArabicFont,
            onSetArabicFontSize = onSetArabicFontSize,
            onSetPageMarginDp = onSetPageMarginDp,
            onSetReadingMode = onSetReadingMode,
            onSetBrightness = onSetBrightness,
            onSetKeepScreenAwake = onSetKeepScreenAwake,
            onSetLockPortrait = onSetLockPortrait,
            onSetTranslationEdition = onSetTranslationEdition,
            onSetShowTranslation = onSetShowTranslation,
            onSetShowTransliteration = onSetShowTransliteration,
        )
    }

    if (showAudioSheet) {
        QariAndAudioSheet(
            surahNumber = activeAudioOrPageSurah,
            prefs = prefs,
            audioState = audioState,
            favoriteQariIds = favoriteQariIds,
            onDismiss = { showAudioSheet = false },
            onSelectQari = onSelectQari,
            onToggleFavoriteQari = onToggleFavoriteQari,
        )
    }

    if (showJumpDialog) {
        JumpToMushafDialog(
            currentPage = currentPage,
            currentSurah = surahNumber,
            totalPages = totalMushafPages,
            onDismiss = { showJumpDialog = false },
            onJumpToPage = { p ->
                onJumpToPage(p)
                onUpdateLastReadPage(p, null, null)
            },
            onJumpToSurahAyah = { s, a ->
                onSelectSurah(s)
                onUpdateLastRead(s, a)
                onJumpToPage(QuranMetadata.startPageForSurah(s, prefs.mushafEdition))
            },
            onJumpToJuz = { j ->
                val juzMeta = QuranMetadata.getJuz(j)
                onSelectSurah(juzMeta.startSurah)
                onJumpToPage(QuranMetadata.startPageForJuz(j, prefs.mushafEdition))
            }
        )
    }

    bookmarkTargetAyah?.let { target ->
        val existing = bookmarks.firstOrNull {
            it.surahNumber == target.surahNumber && it.ayahNumber == target.ayahNumber
        }
        val targetSurahMeta = QuranMetadata.getSurah(target.surahNumber)
        var noteText by remember(target) { mutableStateOf(existing?.note ?: "") }
        AlertDialog(
            onDismissRequest = { bookmarkTargetAyah = null },
            title = { Text("Bookmark ${targetSurahMeta.englishName} ${target.surahNumber}:${target.ayahNumber}") },
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
                    onUpsertBookmark(target.surahNumber, target.ayahNumber, noteText)
                    bookmarkTargetAyah = null
                }) { Text("Save Bookmark") }
            },
            dismissButton = {
                Row {
                    if (existing != null) {
                        TextButton(onClick = {
                            onRemoveBookmark(target.surahNumber, target.ayahNumber)
                            bookmarkTargetAyah = null
                        }) { Text("Remove") }
                    }
                    TextButton(onClick = { bookmarkTargetAyah = null }) { Text("Cancel") }
                }
            }
        )
    }

    tafsirTargetAyah?.let { target ->
        val targetSurahMeta = QuranMetadata.getSurah(target.surahNumber)
        val matchingTafsir = HidayahExtendedData.tafsirs.firstOrNull {
            it.surahNumber == target.surahNumber && it.ayahNumber == target.ayahNumber
        }
        AlertDialog(
            onDismissRequest = { tafsirTargetAyah = null },
            title = { Text("Tafsir • ${targetSurahMeta.englishName} ${target.surahNumber}:${target.ayahNumber}") },
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
                    onAskAiAboutAyah("Provide a concise Tafsir reflection on Surah ${targetSurahMeta.englishName} (${target.surahNumber}:${target.ayahNumber}) referencing classical scholars like Ibn Kathir.")
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

private tailrec fun Context.findActivity(): Activity? = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.findActivity()
    else -> null
}

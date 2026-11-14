package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.audio.AudioPlaybackState
import com.example.data.local.BookmarkEntity
import com.example.data.local.DownloadedAudioEntity
import com.example.data.local.MushafEdition
import com.example.data.local.PageBookmarkEntity
import com.example.data.local.QuranLayoutStyle
import com.example.data.local.UserPreferencesState
import com.example.data.repository.AyahDisplayItem
import com.example.data.seed.QariCatalog
import com.example.data.seed.QariInfo
import com.example.data.seed.QuranBengaliData
import com.example.data.seed.QuranMetadata
import com.example.ui.components.QuranBookmarksModalSheet
import com.example.ui.components.QuranOfflineManagerSheet
import com.example.ui.components.SurahOriginIcon
import com.example.ui.components.SurahStarBadge
import com.example.ui.theme.HindSiliguriFontFamily
import com.example.ui.theme.LateefFontFamily

private val ForestGreen = Color(0xFF2B4C3F)
private val SegmentedTrackGreen = Color(0xFF476A5C)
private val OffWhiteBg = Color(0xFFF6F7F6)
private val TextCharcoal = Color(0xFF1F2723)
private val MutedGray = Color(0xFF6A746E)

@Composable
fun QuranHubScreen(
    prefs: UserPreferencesState,
    bookmarks: List<BookmarkEntity>,
    pageBookmarks: List<PageBookmarkEntity>,
    cachedPageCount: Int,
    cachedAyahCount: Int,
    downloadedAudio: List<DownloadedAudioEntity>,
    availableStorageMb: Long,
    offlineBatchStatus: String?,
    audioState: AudioPlaybackState,
    searchQuery: String,
    searchResults: List<AyahDisplayItem>,
    isSearching: Boolean,
    onSearchQueryChange: (String) -> Unit,
    onExecuteSearch: () -> Unit,
    onOpenSurah: (surahNumber: Int, ayahNumber: Int, pageNumber: Int?) -> Unit,
    onSelectMushafEdition: (MushafEdition) -> Unit,
    onSetLayout: (QuranLayoutStyle) -> Unit,
    onSetShowTranslation: (Boolean) -> Unit = {},
    onToggleFavoriteSurah: (Int) -> Unit,
    onToggleCompletedJuz: (Int) -> Unit,
    onIncrementKhatamPage: () -> Unit,
    onDeleteBookmark: (surahNumber: Int, ayahNumber: Int) -> Unit,
    onDeletePageBookmark: (Int) -> Unit,
    onPrefetchSurahsOffline: (List<Int>) -> Unit,
    onDownloadSurahAudio: (Int, QariInfo) -> Unit,
    onDeleteSurahAudio: (String, Int) -> Unit,
    onBackToHome: () -> Unit = {},
) {
    var showSurahParaList by rememberSaveable { mutableStateOf(false) }
    var selectedListTab by rememberSaveable { mutableIntStateOf(0) } // 0=সূরা, 1=পারা, 2=বিষয়ভিত্তিক
    var showBookmarksModal by remember { mutableStateOf(false) }
    var showOfflineModal by remember { mutableStateOf(false) }

    BackHandler(enabled = showSurahParaList) {
        showSurahParaList = false
    }

    val selectedQari = QariCatalog.findById(prefs.selectedQariId)

    if (!showSurahParaList) {
        // =====================================================================
        // VIEW 1: 2-COLUMN MUSHAF SELECTION SCREEN (QURAN HOME)
        // =====================================================================
        MushafSelectionScreen(
            selectedEdition = prefs.mushafEdition,
            selectedLayout = prefs.quranLayout,
            lastReadSurah = prefs.lastReadSurah,
            lastReadAyah = prefs.lastReadAyah,
            lastReadPage = prefs.lastReadPage,
            khatamPagesRead = prefs.khatamPagesRead,
            onSelectEdition = { option ->
                onSelectMushafEdition(option.edition)
                onSetLayout(option.layout)
                onSetShowTranslation(option.showTranslation)
                showSurahParaList = true
            },
            onResumeLastRead = { pageNum ->
                onOpenSurah(prefs.lastReadSurah, prefs.lastReadAyah, pageNum)
            },
            onIncrementKhatamPage = onIncrementKhatamPage,
            onBackClick = onBackToHome,
            onOpenSurahList = { showSurahParaList = true },
            onOpenBookmarks = { showBookmarksModal = true },
            onOpenOfflineManager = { showOfflineModal = true },
            totalBookmarkCount = pageBookmarks.size + bookmarks.size,
        )
    } else {
        // =====================================================================
        // VIEW 2: SURAH, PARA & TOPIC LIST VIEW (BENGALI UI)
        // =====================================================================
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(OffWhiteBg),
            contentAlignment = Alignment.TopCenter
        ) {
            Column(
                modifier = Modifier
                    .widthIn(max = 900.dp)
                    .fillMaxSize()
            ) {
                MushafSelectionTopHeader(
                    title = "কুরআন",
                    showListActions = true,
                    onBackClick = { showSurahParaList = false },
                    onBookmarkClick = { showBookmarksModal = true },
                    onSearchClick = {
                        selectedListTab = 2
                        if (searchQuery.isNotBlank()) onExecuteSearch()
                    },
                )
                BanglaSurahParaTopicView(
                    prefs = prefs,
                    selectedTab = selectedListTab,
                    onSelectTab = { selectedListTab = it },
                    searchQuery = searchQuery,
                    searchResults = searchResults,
                    isSearching = isSearching,
                    onSearchQueryChange = onSearchQueryChange,
                    onExecuteSearch = {
                        selectedListTab = 2
                        onExecuteSearch()
                    },
                    onOpenSurah = onOpenSurah,
                    onToggleCompletedJuz = onToggleCompletedJuz,
                    onToggleFavoriteSurah = onToggleFavoriteSurah,
                )
            }
        }
    }

    if (showBookmarksModal) {
        QuranBookmarksModalSheet(
            bookmarks = bookmarks,
            pageBookmarks = pageBookmarks,
            onDismiss = { showBookmarksModal = false },
            onOpenSurah = onOpenSurah,
            onDeleteBookmark = onDeleteBookmark,
            onDeletePageBookmark = onDeletePageBookmark,
        )
    }

    if (showOfflineModal) {
        QuranOfflineManagerSheet(
            cachedPageCount = cachedPageCount,
            cachedAyahCount = cachedAyahCount,
            downloadedAudio = downloadedAudio,
            availableStorageMb = availableStorageMb,
            offlineBatchStatus = offlineBatchStatus,
            audioState = audioState,
            selectedQari = selectedQari,
            onDismiss = { showOfflineModal = false },
            onPrefetchSurahsOffline = onPrefetchSurahsOffline,
            onDownloadSurahAudio = onDownloadSurahAudio,
            onDeleteSurahAudio = onDeleteSurahAudio,
        )
    }
}


@Composable
private fun BanglaSurahParaTopicView(
    prefs: UserPreferencesState,
    selectedTab: Int,
    onSelectTab: (Int) -> Unit,
    searchQuery: String,
    searchResults: List<AyahDisplayItem>,
    isSearching: Boolean,
    onSearchQueryChange: (String) -> Unit,
    onExecuteSearch: () -> Unit,
    onOpenSurah: (surahNumber: Int, ayahNumber: Int, pageNumber: Int?) -> Unit,
    onToggleCompletedJuz: (Int) -> Unit,
    onToggleFavoriteSurah: (Int) -> Unit,
) {
    val normalizedQueryNumber = remember(searchQuery) {
        normalizeToAsciiDigits(searchQuery.trim()).toIntOrNull()
    }

    val filteredSurahs = remember(searchQuery, normalizedQueryNumber) {
        val q = searchQuery.trim()
        QuranMetadata.surahs.filter { surah ->
            val bnInfo = QuranBengaliData.getSurahInfo(surah.number)
            q.isEmpty() ||
                normalizedQueryNumber == surah.number ||
                bnInfo.nameBn.contains(q, ignoreCase = true) ||
                bnInfo.meaningBn.contains(q, ignoreCase = true) ||
                surah.englishName.contains(q, ignoreCase = true) ||
                surah.englishMeaning.contains(q, ignoreCase = true) ||
                surah.bengaliName.contains(q, ignoreCase = true) ||
                surah.arabicName.contains(q)
        }
    }

    val filteredJuz = remember(searchQuery, normalizedQueryNumber) {
        val q = searchQuery.trim()
        if (q.isEmpty()) {
            QuranMetadata.juzList
        } else {
            QuranMetadata.juzList.filter { juz ->
                val bnJuz = QuranBengaliData.getJuzInfo(juz.number)
                normalizedQueryNumber == juz.number ||
                    bnJuz.nameBn.contains(q, ignoreCase = true) ||
                    juz.englishName.contains(q, ignoreCase = true) ||
                    juz.arabicName.contains(q)
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(OffWhiteBg)
    ) {
        // Segmented Pill Tab Bar: সূরা | পারা | বিষয়ভিত্তিক
        val segmentTabs = listOf("সূরা", "পারা", "বিষয়ভিত্তিক")
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 10.dp),
            shape = RoundedCornerShape(10.dp),
            color = SegmentedTrackGreen,
        ) {
            Row(modifier = Modifier.fillMaxWidth().padding(2.dp)) {
                segmentTabs.forEachIndexed { idx, label ->
                    val isSelected = selectedTab == idx
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isSelected) ForestGreen else Color.Transparent)
                            .clickable { onSelectTab(idx) }
                            .padding(vertical = 8.dp)
                            .testTag("quran_tab_$idx"),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = label,
                            fontFamily = HindSiliguriFontFamily,
                            fontSize = 15.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = Color.White,
                            maxLines = 1
                        )
                    }
                }
            }
        }

        // White Outlined Search Input ("সূরার নাম/অর্থ/নম্বর দিয়ে অনুসন্ধান")
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .height(48.dp)
                .testTag("quran_search_input"),
            shape = RoundedCornerShape(10.dp),
            color = Color.White,
            border = BorderStroke(1.dp, Color(0xFF9EA7A2))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Filled.Search,
                    contentDescription = "অনুসন্ধান",
                    tint = Color(0xFF4F5953),
                    modifier = Modifier.size(21.dp)
                )
                Spacer(Modifier.width(8.dp))
                Box(
                    modifier = Modifier.weight(1f),
                    contentAlignment = Alignment.CenterStart
                ) {
                    if (searchQuery.isEmpty()) {
                        Text(
                            text = "সূরার নাম/অর্থ/নম্বর দিয়ে অনুসন্ধান",
                            fontFamily = HindSiliguriFontFamily,
                            fontSize = 14.5.sp,
                            color = Color(0xFF6B756F),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                    BasicTextField(
                        value = searchQuery,
                        onValueChange = onSearchQueryChange,
                        singleLine = true,
                        textStyle = TextStyle(
                            fontFamily = HindSiliguriFontFamily,
                            fontSize = 14.5.sp,
                            color = TextCharcoal
                        ),
                        cursorBrush = SolidColor(ForestGreen),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                if (searchQuery.isNotBlank()) {
                    if (selectedTab == 2 || searchQuery.contains(":")) {
                        Surface(
                            color = ForestGreen,
                            shape = RoundedCornerShape(6.dp),
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .clickable { onExecuteSearch() }
                                .testTag("execute_verse_search_button")
                        ) {
                            Text(
                                text = "খুঁজুন",
                                fontFamily = HindSiliguriFontFamily,
                                fontSize = 12.sp,
                                color = Color.White,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                        Spacer(Modifier.width(4.dp))
                    }
                    IconButton(
                        onClick = { onSearchQueryChange("") },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(Icons.Filled.Close, contentDescription = "পরিষ্কার করুন", tint = MutedGray, modifier = Modifier.size(18.dp))
                    }
                }
            }
        }

        // Quick Links Section ("কুইক লিংক")
        Column(modifier = Modifier.padding(top = 10.dp, bottom = 6.dp)) {
            Text(
                text = "কুইক লিংক",
                fontFamily = HindSiliguriFontFamily,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = ForestGreen,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 2.dp)
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                QuranBengaliData.quickLinks.forEach { link ->
                    Surface(
                        color = ForestGreen,
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .clickable {
                                onOpenSurah(link.surahNumber, link.ayahNumber, link.pageNumber)
                            }
                            .testTag("quick_link_${link.id}")
                    ) {
                        Text(
                            text = link.labelBn,
                            fontFamily = HindSiliguriFontFamily,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color.White,
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                        )
                    }
                }
            }
        }

        // TAB CONTENT
        when (selectedTab) {
            // Tab 0: সূরা
            0 -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(filteredSurahs, key = { it.number }) { surah ->
                        val bnInfo = QuranBengaliData.getSurahInfo(surah.number)
                        val isMeccan = surah.revelationType.equals("Meccan", ignoreCase = true)
                        val surahStartPage = QuranMetadata.startPageForSurah(surah.number, prefs.mushafEdition)
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("surah_item_${surah.number}")
                                .clickable { onOpenSurah(surah.number, 1, surahStartPage) },
                            shape = RoundedCornerShape(10.dp),
                            color = Color.White,
                            shadowElevation = 2.dp,
                            border = BorderStroke(0.5.dp, Color(0xFFE3E7E4))
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 14.dp, vertical = 12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                val isFav = surah.number in prefs.favoriteSurahs
                                val isLastRead = surah.number == prefs.lastReadSurah
                                SurahStarBadge(
                                    numberText = QuranBengaliData.toBengaliDigits(surah.number),
                                    ringColor = when {
                                        isFav -> Color(0xFF1A66C2)
                                        isLastRead -> ForestGreen
                                        else -> Color(0xFF5A6560)
                                    },
                                    textColor = if (isFav) Color(0xFF1A66C2) else ForestGreen,
                                    modifier = Modifier.clickable { onToggleFavoriteSurah(surah.number) }
                                )
                                Spacer(Modifier.width(14.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = bnInfo.nameBn,
                                        fontFamily = HindSiliguriFontFamily,
                                        fontSize = 16.5.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = TextCharcoal,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Text(
                                        text = "${bnInfo.meaningBn} (${QuranBengaliData.toBengaliDigits(surah.ayahCount)})",
                                        fontFamily = HindSiliguriFontFamily,
                                        fontSize = 13.5.sp,
                                        color = MutedGray,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    SurahOriginIcon(isMeccan = isMeccan)
                                    Text(
                                        text = surah.arabicName,
                                        fontFamily = LateefFontFamily,
                                        fontSize = 23.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = TextCharcoal
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Tab 1: পারা
            1 -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(filteredJuz, key = { it.number }) { juz ->
                        val bnJuz = QuranBengaliData.getJuzInfo(juz.number)
                        val startSurahBn = QuranBengaliData.getSurahInfo(juz.startSurah)
                        val endSurahBn = QuranBengaliData.getSurahInfo(juz.endSurah)
                        val isDone = juz.number in prefs.completedJuz
                        val juzStartPage = QuranMetadata.startPageForJuz(juz.number, prefs.mushafEdition)
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("juz_item_${juz.number}")
                                .clickable { onOpenSurah(juz.startSurah, juz.startAyah, juzStartPage) },
                            shape = RoundedCornerShape(10.dp),
                            color = Color.White,
                            shadowElevation = 2.dp,
                            border = BorderStroke(0.5.dp, Color(0xFFE3E7E4))
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 14.dp, vertical = 12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                SurahStarBadge(
                                    numberText = QuranBengaliData.toBengaliDigits(juz.number),
                                    ringColor = if (isDone) ForestGreen else Color(0xFF5A6560)
                                )
                                Spacer(Modifier.width(12.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "পারা ${QuranBengaliData.toBengaliDigits(juz.number)} • ${bnJuz.nameBn}",
                                        fontFamily = HindSiliguriFontFamily,
                                        fontSize = 15.5.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = TextCharcoal
                                    )
                                    Text(
                                        text = "${startSurahBn.nameBn} (${QuranBengaliData.toBengaliDigits(juz.startAyah)}) → ${endSurahBn.nameBn} (${QuranBengaliData.toBengaliDigits(juz.endAyah)}) • পৃ. ${QuranBengaliData.toBengaliDigits(juzStartPage)}",
                                        fontFamily = HindSiliguriFontFamily,
                                        fontSize = 12.5.sp,
                                        color = MutedGray,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                                Text(
                                    text = juz.arabicName,
                                    fontFamily = LateefFontFamily,
                                    fontSize = 21.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = ForestGreen
                                )
                                IconButton(onClick = { onToggleCompletedJuz(juz.number) }) {
                                    Icon(
                                        Icons.Filled.CheckCircle,
                                        contentDescription = "পারা সম্পন্ন চিহ্নিত করুন",
                                        tint = if (isDone) ForestGreen else Color(0xFFBDC5C0)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Tab 2: বিষয়ভিত্তিক (Topics + Ayah Search)
            else -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    item {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            QuranMetadata.topics.forEach { topic ->
                                val bnTopic = QuranBengaliData.topicLabelsBn[topic.id]
                                val isSel = searchQuery.equals(topic.id, ignoreCase = true)
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = if (isSel) ForestGreen else Color.White,
                                    border = BorderStroke(1.dp, ForestGreen.copy(alpha = 0.45f)),
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .clickable {
                                            onSearchQueryChange(topic.id)
                                            onExecuteSearch()
                                        }
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(5.dp)
                                    ) {
                                        Icon(
                                            Icons.Filled.AutoAwesome,
                                            contentDescription = null,
                                            modifier = Modifier.size(14.dp),
                                            tint = if (isSel) Color.White else ForestGreen
                                        )
                                        Text(
                                            text = bnTopic?.titleBn ?: topic.title,
                                            fontFamily = HindSiliguriFontFamily,
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Medium,
                                            color = if (isSel) Color.White else ForestGreen
                                        )
                                    }
                                }
                            }
                        }
                    }

                    if (isSearching) {
                        item {
                            Box(
                                modifier = Modifier.fillMaxWidth().padding(24.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator(color = ForestGreen)
                            }
                        }
                    } else if (searchResults.isNotEmpty()) {
                        items(searchResults, key = { "sr_${it.surahNumber}:${it.ayahNumber}" }) { item ->
                            val bnSurah = QuranBengaliData.getSurahInfo(item.surahNumber)
                            val displayPage = if (QuranMetadata.isHafizi611Edition(prefs.mushafEdition)) {
                                QuranMetadata.hafiziPageForSurahAndMadani(item.surahNumber, item.ayahNumber, item.page)
                            } else {
                                item.page
                            }
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { onOpenSurah(item.surahNumber, item.ayahNumber, displayPage) },
                                shape = RoundedCornerShape(10.dp),
                                colors = CardDefaults.cardColors(containerColor = Color.White),
                                border = BorderStroke(0.5.dp, Color(0xFFE3E7E4))
                            ) {
                                Column(
                                    modifier = Modifier.padding(14.dp),
                                    verticalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Text(
                                        text = "${bnSurah.nameBn} (${QuranBengaliData.toBengaliDigits(item.surahNumber)}:${QuranBengaliData.toBengaliDigits(item.ayahNumber)}) • পৃ. ${QuranBengaliData.toBengaliDigits(displayPage)}",
                                        fontFamily = HindSiliguriFontFamily,
                                        fontWeight = FontWeight.Bold,
                                        color = ForestGreen
                                    )
                                    if (item.uthmaniText.isNotBlank()) {
                                        Text(
                                            text = item.uthmaniText,
                                            fontFamily = LateefFontFamily,
                                            fontSize = 22.sp,
                                            color = TextCharcoal,
                                            modifier = Modifier.fillMaxWidth()
                                        )
                                    }
                                    Text(
                                        text = item.translationText,
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = TextCharcoal
                                    )
                                }
                            }
                        }
                    } else {
                        items(QuranMetadata.topics, key = { "topic_card_${it.id}" }) { topic ->
                            val bnTopic = QuranBengaliData.topicLabelsBn[topic.id]
                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        onSearchQueryChange(topic.id)
                                        onExecuteSearch()
                                    },
                                shape = RoundedCornerShape(10.dp),
                                color = Color.White,
                                shadowElevation = 1.dp,
                                border = BorderStroke(0.5.dp, Color(0xFFE3E7E4))
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(14.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(Icons.Filled.AutoAwesome, contentDescription = null, tint = ForestGreen)
                                    Spacer(Modifier.width(12.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = bnTopic?.titleBn ?: topic.title,
                                            fontFamily = HindSiliguriFontFamily,
                                            fontSize = 15.5.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = TextCharcoal
                                        )
                                        Text(
                                            text = bnTopic?.subtitleBn ?: topic.subtitle,
                                            fontFamily = HindSiliguriFontFamily,
                                            fontSize = 12.5.sp,
                                            color = MutedGray
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

private fun normalizeToAsciiDigits(input: String): String =
    input.map { ch ->
        when (ch) {
            in '০'..'৯' -> ('0' + (ch - '০'))
            in '٠'..'٩' -> ('0' + (ch - '٠'))
            else -> ch
        }
    }.joinToString("")

package com.example.ui.screens

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.PrimaryScrollableTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.audio.AudioPlaybackState
import com.example.audio.AudioPlayerManager
import com.example.data.local.BookmarkEntity
import com.example.data.local.DownloadedAudioEntity
import com.example.data.local.MushafEdition
import com.example.data.local.PageBookmarkEntity
import com.example.data.local.QuranLayoutStyle
import com.example.data.local.UserPreferencesState
import com.example.data.repository.AyahDisplayItem
import com.example.data.seed.QariCatalog
import com.example.data.seed.QariInfo
import com.example.data.seed.QuranMetadata
import com.example.localization.AppLanguage
import com.example.localization.stringsFor
import com.example.ui.theme.AmiriFontFamily

@OptIn(ExperimentalMaterial3Api::class)
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
    onToggleFavoriteSurah: (Int) -> Unit,
    onToggleCompletedJuz: (Int) -> Unit,
    onIncrementKhatamPage: () -> Unit,
    onDeleteBookmark: (surahNumber: Int, ayahNumber: Int) -> Unit,
    onDeletePageBookmark: (Int) -> Unit,
    onPrefetchSurahsOffline: (List<Int>) -> Unit,
    onDownloadSurahAudio: (Int, QariInfo) -> Unit,
    onDeleteSurahAudio: (String, Int) -> Unit,
) {
    val context = LocalContext.current
    val strings = stringsFor(prefs.appLanguage)
    var selectedTab by remember { mutableIntStateOf(0) }
    var onlyFavoriteSurahs by remember { mutableStateOf(false) }
    val tabs = listOf(strings.surahs, strings.juz, strings.bookmarks, "Search", "Offline")

    val filteredSurahs = remember(searchQuery, onlyFavoriteSurahs, prefs.favoriteSurahs) {
        val q = searchQuery.trim()
        QuranMetadata.surahs.filter { surah ->
            val favMatch = !onlyFavoriteSurahs || surah.number in prefs.favoriteSurahs
            val textMatch = q.isEmpty() ||
                q.toIntOrNull() == surah.number ||
                surah.englishName.contains(q, ignoreCase = true) ||
                surah.englishMeaning.contains(q, ignoreCase = true) ||
                surah.bengaliName.contains(q, ignoreCase = true) ||
                surah.arabicName.contains(q)
            favMatch && textMatch
        }
    }

    val filteredJuz = remember(searchQuery) {
        val num = searchQuery.trim().toIntOrNull()
        if (num != null && num in 1..30) {
            QuranMetadata.juzList.filter { it.number == num }
        } else {
            QuranMetadata.juzList
        }
    }

    val lastSurah = QuranMetadata.getSurah(prefs.lastReadSurah)
    val selectedQari = QariCatalog.findById(prefs.selectedQariId)
    val downloadedAudioMb = remember(downloadedAudio) {
        val bytes = downloadedAudio.sumOf { it.sizeBytes }
        String.format("%.1f MB", bytes / (1024f * 1024f))
    }

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.TopCenter) {
        Column(
            modifier = Modifier
                .widthIn(max = 900.dp)
                .fillMaxSize()
        ) {
            // Premium Continue Reading + Hafizi Mushaf Hero Banner
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .clip(RoundedCornerShape(22.dp))
                    .testTag("quran_continue_hero_card"),
                shape = RoundedCornerShape(22.dp),
                color = MaterialTheme.colorScheme.primary
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.linearGradient(
                                listOf(
                                    Color(0xFF0A3D2F),
                                    Color(0xFF105B46),
                                    Color(0xFF0A2E24),
                                )
                            )
                        )
                        .padding(16.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Hafizi & Madani Mushaf • ${prefs.mushafEdition.label}",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = Color(0xFFE9C767)
                                )
                                Text(
                                    text = "${lastSurah.number}. ${lastSurah.englishName} (${lastSurah.arabicName})",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    text = "Page ${prefs.lastReadPage} / 604 • Ayah ${prefs.lastReadAyah} • Qari: ${selectedQari.name}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color.White.copy(alpha = 0.85f),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                            Text(
                                text = "القرآن",
                                fontFamily = AmiriFontFamily,
                                fontSize = 28.sp,
                                color = Color(0xFFE9C767)
                            )
                        }

                        // Action Row: Open Hafizi Mushaf | Ayah View | Listen Audio
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = {
                                    onSetLayout(QuranLayoutStyle.HAFIZI_PAGE)
                                    onOpenSurah(prefs.lastReadSurah, prefs.lastReadAyah, prefs.lastReadPage)
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFFE7C563),
                                    contentColor = Color(0xFF0B2B22)
                                ),
                                modifier = Modifier.testTag("open_hafizi_mushaf_button")
                            ) {
                                Icon(Icons.AutoMirrored.Filled.MenuBook, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(Modifier.width(6.dp))
                                Text("Open Mushaf p.${prefs.lastReadPage}", fontWeight = FontWeight.Bold)
                            }
                            OutlinedButton(
                                onClick = {
                                    onSetLayout(QuranLayoutStyle.AYAH_BY_AYAH)
                                    onOpenSurah(prefs.lastReadSurah, prefs.lastReadAyah, prefs.lastReadPage)
                                },
                                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.5f)),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White)
                            ) {
                                Text("Ayah + Translation")
                            }
                            OutlinedButton(
                                onClick = {
                                    AudioPlayerManager.playAyah(
                                        context,
                                        prefs.lastReadSurah,
                                        prefs.lastReadAyah,
                                        selectedQari
                                    )
                                },
                                border = BorderStroke(1.dp, Color(0xFFE7C563).copy(alpha = 0.65f)),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFE7C563))
                            ) {
                                Icon(Icons.Filled.GraphicEq, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(Modifier.width(4.dp))
                                Text("Listen")
                            }
                        }

                        // Reading Khatam Progress Bar
                        val khatamFrac = (prefs.khatamPagesRead / 604f).coerceIn(0f, 1f)
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    "Khatam Progress: ${prefs.khatamPagesRead} / 604 pages • ${prefs.completedJuz.size}/30 Juz",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Color.White.copy(alpha = 0.9f)
                                )
                                Text(
                                    "+1 Page",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFE7C563),
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .clickable { onIncrementKhatamPage() }
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                            LinearProgressIndicator(
                                progress = { khatamFrac },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(6.dp)
                                    .clip(RoundedCornerShape(3.dp)),
                                color = Color(0xFFE7C563),
                                trackColor = Color.White.copy(alpha = 0.22f)
                            )
                        }
                    }
                }
            }

            // Mushaf Edition Switcher Pills
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 2.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                MushafEdition.entries.forEach { edition ->
                    FilterChip(
                        selected = prefs.mushafEdition == edition,
                        onClick = { onSelectMushafEdition(edition) },
                        label = { Text(edition.label, style = MaterialTheme.typography.labelMedium) }
                    )
                }
            }

            // Smart Search Bar (Surah, Juz, Page, 2:255, Topics, Text)
            OutlinedTextField(
                value = searchQuery,
                onValueChange = onSearchQueryChange,
                placeholder = {
                    Text(
                        "Surah, Juz, 'page 255', '2:255', patience, Arabic...",
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null) },
                trailingIcon = {
                    if (searchQuery.isNotBlank()) {
                        Button(
                            onClick = {
                                selectedTab = 3
                                onExecuteSearch()
                            },
                            modifier = Modifier.padding(end = 4.dp)
                        ) { Text("Find") }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp)
                    .testTag("quran_search_input"),
            )

            PrimaryScrollableTabRow(selectedTabIndex = selectedTab) {
                tabs.forEachIndexed { idx, label ->
                    Tab(
                        selected = selectedTab == idx,
                        onClick = { selectedTab = idx },
                        text = { Text(label, maxLines = 1, overflow = TextOverflow.Ellipsis) }
                    )
                }
            }

            when (selectedTab) {
                // Tab 0: Surahs (114) with Favorites Filter & Start Page badge
                0 -> {
                    Column(modifier = Modifier.fillMaxSize()) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 6.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            FilterChip(
                                selected = !onlyFavoriteSurahs,
                                onClick = { onlyFavoriteSurahs = false },
                                label = { Text("All 114 Surahs") }
                            )
                            FilterChip(
                                selected = onlyFavoriteSurahs,
                                onClick = { onlyFavoriteSurahs = true },
                                leadingIcon = { Icon(Icons.Filled.Star, contentDescription = null, modifier = Modifier.size(16.dp)) },
                                label = { Text("Favorites (${prefs.favoriteSurahs.size})") }
                            )
                        }
                        LazyColumn(
                            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(filteredSurahs, key = { it.number }) { surah ->
                                val isFav = surah.number in prefs.favoriteSurahs
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("surah_item_${surah.number}")
                                        .clickable { onOpenSurah(surah.number, 1, surah.startPage) },
                                    shape = RoundedCornerShape(16.dp),
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(14.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(40.dp)
                                                .clip(CircleShape)
                                                .background(MaterialTheme.colorScheme.primaryContainer),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                "${surah.number}",
                                                fontWeight = FontWeight.Bold,
                                                color = MaterialTheme.colorScheme.primary
                                            )
                                        }
                                        Spacer(Modifier.width(12.dp))
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(
                                                text = if (prefs.appLanguage == AppLanguage.BENGALI) {
                                                    "${surah.englishName} (${surah.bengaliName})"
                                                } else {
                                                    surah.englishName
                                                },
                                                style = MaterialTheme.typography.titleMedium,
                                                fontWeight = FontWeight.Bold
                                            )
                                            Text(
                                                text = "${surah.englishMeaning} • ${surah.ayahCount} Ayahs • p.${surah.startPage}",
                                                style = MaterialTheme.typography.bodySmall,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                        Text(
                                            text = surah.arabicName,
                                            fontFamily = AmiriFontFamily,
                                            fontSize = 22.sp,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                        IconButton(onClick = { onToggleFavoriteSurah(surah.number) }) {
                                            Icon(
                                                if (isFav) Icons.Filled.Star else Icons.Filled.StarBorder,
                                                contentDescription = "Favorite Surah ${surah.englishName}",
                                                tint = if (isFav) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // Tab 1: Juz / Para (30) with Completed Juz Tracking
                1 -> {
                    LazyColumn(
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(filteredJuz, key = { it.number }) { juz ->
                            val startSurah = QuranMetadata.getSurah(juz.startSurah)
                            val endSurah = QuranMetadata.getSurah(juz.endSurah)
                            val isDone = juz.number in prefs.completedJuz
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { onOpenSurah(juz.startSurah, juz.startAyah, juz.startPage) },
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(14.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(40.dp)
                                            .clip(CircleShape)
                                            .background(
                                                if (isDone) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondaryContainer
                                            ),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            "${juz.number}",
                                            fontWeight = FontWeight.Bold,
                                            color = if (isDone) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSecondaryContainer
                                        )
                                    }
                                    Spacer(Modifier.width(12.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            "Juz ${juz.number} • ${juz.englishName} (p.${juz.startPage})",
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(
                                            "${startSurah.englishName} ${juz.startAyah} -> ${endSurah.englishName} ${juz.endAyah}",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                    Text(
                                        juz.arabicName,
                                        fontFamily = AmiriFontFamily,
                                        fontSize = 18.sp,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                    IconButton(onClick = { onToggleCompletedJuz(juz.number) }) {
                                        Icon(
                                            Icons.Filled.CheckCircle,
                                            contentDescription = "Mark Juz ${juz.number} completed",
                                            tint = if (isDone) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // Tab 2: Bookmarks (Pages & Ayahs)
                2 -> {
                    if (pageBookmarks.isEmpty() && bookmarks.isEmpty()) {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.padding(24.dp)
                            ) {
                                Icon(
                                    Icons.Filled.Bookmark,
                                    contentDescription = null,
                                    modifier = Modifier.size(48.dp),
                                    tint = MaterialTheme.colorScheme.primary
                                )
                                Spacer(Modifier.height(8.dp))
                                Text("No page or Ayah bookmarks yet", style = MaterialTheme.typography.titleMedium)
                                Text(
                                    "Bookmark any Mushaf page or Ayah while reading to jump back instantly.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    } else {
                        LazyColumn(
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            if (pageBookmarks.isNotEmpty()) {
                                item {
                                    Text(
                                        "Saved Mushaf Pages (${pageBookmarks.size})",
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                                items(pageBookmarks, key = { "page_${it.pageNumber}" }) { pBm ->
                                    Card(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable { onOpenSurah(1, 1, pBm.pageNumber) },
                                        shape = RoundedCornerShape(14.dp),
                                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                                    ) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth().padding(14.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Icon(Icons.Filled.Bookmark, contentDescription = null, tint = MaterialTheme.colorScheme.secondary)
                                            Spacer(Modifier.width(10.dp))
                                            Column(modifier = Modifier.weight(1f)) {
                                                Text("Mushaf Page ${pBm.pageNumber}", fontWeight = FontWeight.Bold)
                                                Text(pBm.title, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                            }
                                            IconButton(onClick = { onDeletePageBookmark(pBm.pageNumber) }) {
                                                Icon(Icons.Filled.Delete, contentDescription = "Remove page bookmark")
                                            }
                                        }
                                    }
                                }
                            }

                            if (bookmarks.isNotEmpty()) {
                                item {
                                    Text(
                                        "Saved Ayah Bookmarks (${bookmarks.size})",
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.padding(top = 6.dp)
                                    )
                                }
                                items(bookmarks, key = { "${it.surahNumber}:${it.ayahNumber}" }) { bm ->
                                    Card(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable { onOpenSurah(bm.surahNumber, bm.ayahNumber, null) },
                                        shape = RoundedCornerShape(14.dp),
                                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                                    ) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth().padding(14.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Column(modifier = Modifier.weight(1f)) {
                                                Text("${bm.surahName} (${bm.surahNumber}:${bm.ayahNumber})", fontWeight = FontWeight.Bold)
                                                if (bm.note.isNotBlank()) {
                                                    Text(
                                                        "Note: ${bm.note}",
                                                        style = MaterialTheme.typography.bodySmall,
                                                        color = MaterialTheme.colorScheme.primary
                                                    )
                                                }
                                            }
                                            IconButton(onClick = { onDeleteBookmark(bm.surahNumber, bm.ayahNumber) }) {
                                                Icon(Icons.Filled.Delete, contentDescription = "Delete bookmark")
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // Tab 3: Smart Quran Search (Topics + Page + Surah:Ayah + Arabic + Translation)
                3 -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Button(
                                onClick = onExecuteSearch,
                                modifier = Modifier.testTag("execute_verse_search_button")
                            ) { Text("Search Quran") }
                            Text(
                                "Try: 2:255, page 582, mercy, patience",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Text("Explore by Topic", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            QuranMetadata.topics.forEach { topic ->
                                FilterChip(
                                    selected = searchQuery.equals(topic.id, ignoreCase = true),
                                    onClick = {
                                        onSearchQueryChange(topic.id)
                                        onExecuteSearch()
                                    },
                                    leadingIcon = { Icon(Icons.Filled.AutoAwesome, contentDescription = null, modifier = Modifier.size(14.dp)) },
                                    label = { Text(topic.title) }
                                )
                            }
                        }

                        if (isSearching) {
                            Box(modifier = Modifier.fillMaxWidth().padding(24.dp), contentAlignment = Alignment.Center) {
                                CircularProgressIndicator()
                            }
                        } else {
                            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                items(searchResults, key = { "${it.surahNumber}:${it.ayahNumber}" }) { item ->
                                    val sMeta = QuranMetadata.getSurah(item.surahNumber)
                                    Card(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable { onOpenSurah(item.surahNumber, item.ayahNumber, item.page) },
                                        shape = RoundedCornerShape(14.dp),
                                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                                    ) {
                                        Column(
                                            modifier = Modifier.padding(14.dp),
                                            verticalArrangement = Arrangement.spacedBy(6.dp)
                                        ) {
                                            Text(
                                                "${sMeta.englishName} (${item.surahNumber}:${item.ayahNumber}) • Mushaf p.${item.page}",
                                                fontWeight = FontWeight.Bold,
                                                color = MaterialTheme.colorScheme.primary
                                            )
                                            if (item.uthmaniText.isNotBlank()) {
                                                Text(
                                                    item.uthmaniText,
                                                    fontFamily = AmiriFontFamily,
                                                    fontSize = 20.sp,
                                                    modifier = Modifier.fillMaxWidth()
                                                )
                                            }
                                            Text(item.translationText, style = MaterialTheme.typography.bodyMedium)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // Tab 4: Offline Quran Text & Audio Download Manager
                else -> {
                    val downloadedBySurah = remember(downloadedAudio, selectedQari.id) {
                        downloadedAudio
                            .filter { it.qariId == selectedQari.id }
                            .groupBy { it.surahNumber }
                    }
                    LazyColumn(
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        item {
                            Card(
                                shape = RoundedCornerShape(18.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                            ) {
                                Column(
                                    modifier = Modifier.padding(16.dp),
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Icon(Icons.Filled.Storage, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                                        Text("Offline Quran & Audio Storage", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                                    }
                                    Text(
                                        "Cached Text: $cachedPageCount / 604 Mushaf pages ($cachedAyahCount Ayahs)\n" +
                                            "Audio Downloaded: $downloadedAudioMb (${selectedQari.name})\n" +
                                            "Available Device Storage: $availableStorageMb MB",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    if (offlineBatchStatus != null) {
                                        Text(offlineBatchStatus, color = MaterialTheme.colorScheme.primary, style = MaterialTheme.typography.labelLarge)
                                        LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                                    }
                                    audioState.downloadProgressText?.let { prog ->
                                        Text(prog, color = MaterialTheme.colorScheme.secondary, style = MaterialTheme.typography.labelMedium)
                                    }
                                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                        Button(
                                            onClick = { onPrefetchSurahsOffline((78..114).toList()) },
                                            enabled = offlineBatchStatus == null
                                        ) {
                                            Icon(Icons.Filled.CloudDone, contentDescription = null, modifier = Modifier.size(16.dp))
                                            Spacer(Modifier.width(4.dp))
                                            Text("Cache Juz Amma (78-114)")
                                        }
                                        OutlinedButton(
                                            onClick = { onPrefetchSurahsOffline(listOf(1, 18, 36, 55, 67, 78)) },
                                            enabled = offlineBatchStatus == null
                                        ) {
                                            Text("Essential Surahs")
                                        }
                                    }
                                }
                            }
                        }

                        item {
                            Text(
                                "Audio Downloads (${selectedQari.name})",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        items(
                            items = QuranMetadata.surahs.filter {
                                it.number in listOf(1, 18, 36, 55, 56, 67) || it.number >= 78 || it.number in downloadedBySurah
                            },
                            key = { "dl_${it.number}" }
                        ) { surah ->
                            val count = downloadedBySurah[surah.number]?.size ?: 0
                            val complete = count >= surah.ayahCount
                            Card(
                                shape = RoundedCornerShape(14.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth().padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text("${surah.number}. ${surah.englishName}", fontWeight = FontWeight.Bold)
                                        Text(
                                            if (complete) "Offline Ready (${surah.ayahCount} Ayahs)" else "$count / ${surah.ayahCount} Ayahs cached",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = if (complete) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                    if (count > 0) {
                                        IconButton(onClick = { onDeleteSurahAudio(selectedQari.id, surah.number) }) {
                                            Icon(Icons.Filled.Delete, contentDescription = "Delete downloaded audio")
                                        }
                                    }
                                    IconButton(onClick = { onDownloadSurahAudio(surah.number, selectedQari) }) {
                                        Icon(
                                            if (complete) Icons.Filled.CheckCircle else Icons.Filled.Download,
                                            contentDescription = "Download ${surah.englishName} audio",
                                            tint = MaterialTheme.colorScheme.primary
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

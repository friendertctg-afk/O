package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.BookmarkEntity
import com.example.data.local.UserPreferencesState
import com.example.data.repository.AyahDisplayItem
import com.example.data.seed.QuranMetadata
import com.example.localization.AppLanguage
import com.example.localization.stringsFor
import com.example.ui.theme.AmiriFontFamily

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuranHubScreen(
    prefs: UserPreferencesState,
    bookmarks: List<BookmarkEntity>,
    searchQuery: String,
    searchResults: List<AyahDisplayItem>,
    isSearching: Boolean,
    onSearchQueryChange: (String) -> Unit,
    onExecuteSearch: () -> Unit,
    onOpenSurah: (surahNumber: Int, ayahNumber: Int) -> Unit,
    onDeleteBookmark: (surahNumber: Int, ayahNumber: Int) -> Unit,
) {
    val strings = stringsFor(prefs.appLanguage)
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf(strings.surahs, strings.juz, strings.bookmarks, "Search")

    val filteredSurahs = remember(searchQuery) {
        val q = searchQuery.trim()
        if (q.isEmpty()) {
            QuranMetadata.surahs
        } else {
            val num = q.toIntOrNull()
            QuranMetadata.surahs.filter {
                (num != null && it.number == num) ||
                    it.englishName.contains(q, ignoreCase = true) ||
                    it.englishMeaning.contains(q, ignoreCase = true) ||
                    it.bengaliName.contains(q, ignoreCase = true) ||
                    it.arabicName.contains(q)
            }
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

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.TopCenter) {
        Column(
            modifier = Modifier
                .widthIn(max = 840.dp)
                .fillMaxSize()
        ) {
            // Last read banner
            val lastSurah = QuranMetadata.getSurah(prefs.lastReadSurah)
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .clip(RoundedCornerShape(18.dp))
                    .clickable { onOpenSurah(prefs.lastReadSurah, prefs.lastReadAyah) },
                color = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Icon(Icons.AutoMirrored.Filled.MenuBook, contentDescription = null)
                        Column {
                            Text(strings.continueReading, style = MaterialTheme.typography.labelMedium)
                            Text(
                                "${lastSurah.number}. ${lastSurah.englishName} • Ayah ${prefs.lastReadAyah}",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                    Icon(Icons.Filled.PlayArrow, contentDescription = "Resume Surah")
                }
            }

            OutlinedTextField(
                value = searchQuery,
                onValueChange = onSearchQueryChange,
                placeholder = { Text(strings.searchHint, maxLines = 1, overflow = TextOverflow.Ellipsis) },
                leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null) },
                trailingIcon = {
                    if (selectedTab == 3 || searchQuery.contains(":")) {
                        Button(
                            onClick = {
                                selectedTab = 3
                                onExecuteSearch()
                            },
                            modifier = Modifier.padding(end = 4.dp)
                        ) { Text("Go") }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp)
                    .testTag("quran_search_input"),
            )

            PrimaryTabRow(selectedTabIndex = selectedTab) {
                tabs.forEachIndexed { idx, label ->
                    Tab(
                        selected = selectedTab == idx,
                        onClick = { selectedTab = idx },
                        text = { Text(label, maxLines = 1, overflow = TextOverflow.Ellipsis) }
                    )
                }
            }

            when (selectedTab) {
                0 -> {
                    LazyColumn(
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(filteredSurahs, key = { it.number }) { surah ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("surah_item_${surah.number}")
                                    .clickable { onOpenSurah(surah.number, 1) },
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
                                            text = "${surah.englishMeaning} • ${surah.revelationType} • ${surah.ayahCount} Ayahs • Juz ${surah.startJuz}",
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
                                }
                            }
                        }
                    }
                }
                1 -> {
                    LazyColumn(
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(filteredJuz, key = { it.number }) { juz ->
                            val startSurah = QuranMetadata.getSurah(juz.startSurah)
                            val endSurah = QuranMetadata.getSurah(juz.endSurah)
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { onOpenSurah(juz.startSurah, juz.startAyah) },
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
                                            .background(MaterialTheme.colorScheme.secondaryContainer),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text("${juz.number}", fontWeight = FontWeight.Bold)
                                    }
                                    Spacer(Modifier.width(12.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text("Juz ${juz.number} • ${juz.englishName}", fontWeight = FontWeight.Bold)
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
                                }
                            }
                        }
                    }
                }
                2 -> {
                    if (bookmarks.isEmpty()) {
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
                                Text("No bookmarks yet", style = MaterialTheme.typography.titleMedium)
                                Text(
                                    "Tap the bookmark icon on any Ayah while reading to save verses and personal notes.",
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
                            items(bookmarks, key = { "${it.surahNumber}:${it.ayahNumber}" }) { bm ->
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { onOpenSurah(bm.surahNumber, bm.ayahNumber) },
                                    shape = RoundedCornerShape(16.dp),
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(14.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(
                                                "${bm.surahName} (${bm.surahNumber}:${bm.ayahNumber})",
                                                fontWeight = FontWeight.Bold
                                            )
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
                else -> {
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
                            ) { Text("Search Verses") }
                            Text(
                                "Try: 2:255, mercy, patience, guidance",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        if (isSearching) {
                            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                                CircularProgressIndicator()
                            }
                        } else {
                            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                items(searchResults, key = { "${it.surahNumber}:${it.ayahNumber}" }) { item ->
                                    val sMeta = QuranMetadata.getSurah(item.surahNumber)
                                    Card(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable { onOpenSurah(item.surahNumber, item.ayahNumber) },
                                        shape = RoundedCornerShape(14.dp),
                                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                                    ) {
                                        Column(
                                            modifier = Modifier.padding(14.dp),
                                            verticalArrangement = Arrangement.spacedBy(6.dp)
                                        ) {
                                            Text(
                                                "${sMeta.englishName} (${item.surahNumber}:${item.ayahNumber})",
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
            }
        }
    }
}

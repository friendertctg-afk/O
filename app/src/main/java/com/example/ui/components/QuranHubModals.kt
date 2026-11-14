package com.example.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.audio.AudioPlaybackState
import com.example.data.local.BookmarkEntity
import com.example.data.local.DownloadedAudioEntity
import com.example.data.local.PageBookmarkEntity
import com.example.data.seed.QariInfo
import com.example.data.seed.QuranBengaliData
import com.example.data.seed.QuranMetadata
import com.example.ui.theme.HindSiliguriFontFamily

private val ForestGreen = Color(0xFF2B4C3F)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuranBookmarksModalSheet(
    bookmarks: List<BookmarkEntity>,
    pageBookmarks: List<PageBookmarkEntity>,
    onDismiss: () -> Unit,
    onOpenSurah: (surahNumber: Int, ayahNumber: Int, pageNumber: Int?) -> Unit,
    onDeleteBookmark: (surahNumber: Int, ayahNumber: Int) -> Unit,
    onDeletePageBookmark: (Int) -> Unit,
) {
    ModalBottomSheet(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 18.dp, vertical = 10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                text = "বুকমার্ক ও সংরক্ষিত আয়াত",
                fontFamily = HindSiliguriFontFamily,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = ForestGreen
            )
            if (pageBookmarks.isEmpty() && bookmarks.isEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        Icons.Filled.Bookmark,
                        contentDescription = null,
                        tint = ForestGreen,
                        modifier = Modifier.size(44.dp)
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        "এখনও কোনো পৃষ্ঠা বা আয়াত বুকমার্ক করা হয়নি",
                        fontFamily = HindSiliguriFontFamily,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        "পড়ার সময় যেকোনো পৃষ্ঠা বা আয়াতে বুকমার্ক আইকনে ট্যাপ করুন।",
                        fontFamily = HindSiliguriFontFamily,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.height(340.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (pageBookmarks.isNotEmpty()) {
                        item {
                            Text(
                                "সংরক্ষিত মুসহাফ পৃষ্ঠা (${QuranBengaliData.toBengaliDigits(pageBookmarks.size)})",
                                fontFamily = HindSiliguriFontFamily,
                                style = MaterialTheme.typography.labelLarge,
                                color = ForestGreen,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        items(pageBookmarks, key = { "bm_p_${it.pageNumber}" }) { pBm ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        onOpenSurah(1, 1, pBm.pageNumber)
                                        onDismiss()
                                    },
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = Color(0xFFF6F7F6))
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(Icons.Filled.Bookmark, contentDescription = null, tint = ForestGreen)
                                    Spacer(Modifier.width(10.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            "পৃষ্ঠা ${QuranBengaliData.toBengaliDigits(pBm.pageNumber)}",
                                            fontFamily = HindSiliguriFontFamily,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(pBm.title, style = MaterialTheme.typography.bodySmall)
                                    }
                                    IconButton(onClick = { onDeletePageBookmark(pBm.pageNumber) }) {
                                        Icon(Icons.Filled.Delete, contentDescription = "Delete page bookmark")
                                    }
                                }
                            }
                        }
                    }

                    if (bookmarks.isNotEmpty()) {
                        item {
                            Text(
                                "সংরক্ষিত আয়াতসমূহ (${QuranBengaliData.toBengaliDigits(bookmarks.size)})",
                                fontFamily = HindSiliguriFontFamily,
                                style = MaterialTheme.typography.labelLarge,
                                color = ForestGreen,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        items(bookmarks, key = { "bm_a_${it.surahNumber}:${it.ayahNumber}" }) { bm ->
                            val bnInfo = QuranBengaliData.getSurahInfo(bm.surahNumber)
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        onOpenSurah(bm.surahNumber, bm.ayahNumber, null)
                                        onDismiss()
                                    },
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = Color(0xFFF6F7F6))
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            "${bnInfo.nameBn} (${QuranBengaliData.toBengaliDigits(bm.surahNumber)}:${QuranBengaliData.toBengaliDigits(bm.ayahNumber)})",
                                            fontFamily = HindSiliguriFontFamily,
                                            fontWeight = FontWeight.Bold
                                        )
                                        if (bm.note.isNotBlank()) {
                                            Text(
                                                "নোট: ${bm.note}",
                                                style = MaterialTheme.typography.bodySmall,
                                                color = ForestGreen
                                            )
                                        }
                                    }
                                    IconButton(onClick = { onDeleteBookmark(bm.surahNumber, bm.ayahNumber) }) {
                                        Icon(Icons.Filled.Delete, contentDescription = "Delete ayah bookmark")
                                    }
                                }
                            }
                        }
                    }
                }
            }
            Spacer(Modifier.height(16.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuranOfflineManagerSheet(
    cachedPageCount: Int,
    cachedAyahCount: Int,
    downloadedAudio: List<DownloadedAudioEntity>,
    availableStorageMb: Long,
    offlineBatchStatus: String?,
    audioState: AudioPlaybackState,
    selectedQari: QariInfo,
    onDismiss: () -> Unit,
    onPrefetchSurahsOffline: (List<Int>) -> Unit,
    onDownloadSurahAudio: (Int, QariInfo) -> Unit,
    onDeleteSurahAudio: (String, Int) -> Unit,
) {
    val downloadedBySurah = remember(downloadedAudio, selectedQari.id) {
        downloadedAudio.filter { it.qariId == selectedQari.id }.groupBy { it.surahNumber }
    }
    val downloadedAudioMb = remember(downloadedAudio) {
        val bytes = downloadedAudio.sumOf { it.sizeBytes }
        String.format("%.1f MB", bytes / (1024f * 1024f))
    }

    ModalBottomSheet(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 18.dp, vertical = 10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(Icons.Filled.Storage, contentDescription = null, tint = ForestGreen)
                Text(
                    "অফলাইন কুরআন ও অডিও ডাউনলোড",
                    fontFamily = HindSiliguriFontFamily,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = ForestGreen
                )
            }

            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF3F6F4))
            ) {
                Column(
                    modifier = Modifier.padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        "সংরক্ষিত পৃষ্ঠা: $cachedPageCount / 604 ($cachedAyahCount আয়াত)\n" +
                            "অডিও (${selectedQari.name}): $downloadedAudioMb\n" +
                            "খালি স্টোরেজ: $availableStorageMb MB",
                        fontFamily = HindSiliguriFontFamily,
                        style = MaterialTheme.typography.bodySmall
                    )
                    if (offlineBatchStatus != null) {
                        Text(offlineBatchStatus, color = ForestGreen, style = MaterialTheme.typography.labelMedium)
                        LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                    }
                    audioState.downloadProgressText?.let {
                        Text(it, color = ForestGreen, style = MaterialTheme.typography.labelSmall)
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(
                            onClick = { onPrefetchSurahsOffline((78..114).toList()) },
                            enabled = offlineBatchStatus == null,
                            colors = ButtonDefaults.buttonColors(containerColor = ForestGreen)
                        ) {
                            Icon(Icons.Filled.CloudDone, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(4.dp))
                            Text("আমপারা (৭৮-১১৪)", fontFamily = HindSiliguriFontFamily)
                        }
                        OutlinedButton(
                            onClick = { onPrefetchSurahsOffline(listOf(1, 18, 36, 55, 67, 78)) },
                            enabled = offlineBatchStatus == null
                        ) {
                            Text("প্রয়োজনীয় সূরা", fontFamily = HindSiliguriFontFamily)
                        }
                    }
                }
            }

            LazyColumn(
                modifier = Modifier.height(230.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                items(
                    items = QuranMetadata.surahs.filter {
                        it.number in listOf(1, 18, 36, 55, 56, 67) || it.number >= 78 || it.number in downloadedBySurah
                    },
                    key = { "dl_modal_${it.number}" }
                ) { surah ->
                    val bnInfo = QuranBengaliData.getSurahInfo(surah.number)
                    val count = downloadedBySurah[surah.number]?.size ?: 0
                    val complete = count >= surah.ayahCount
                    Card(
                        shape = RoundedCornerShape(10.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    "${QuranBengaliData.toBengaliDigits(surah.number)}. ${bnInfo.nameBn}",
                                    fontFamily = HindSiliguriFontFamily,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    "$count / ${surah.ayahCount} আয়াত ডাউনলোড",
                                    fontFamily = HindSiliguriFontFamily,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            if (complete) {
                                IconButton(onClick = { onDeleteSurahAudio(selectedQari.id, surah.number) }) {
                                    Icon(Icons.Filled.Delete, contentDescription = "Delete offline audio")
                                }
                            } else {
                                OutlinedButton(onClick = { onDownloadSurahAudio(surah.number, selectedQari) }) {
                                    Icon(Icons.Filled.Download, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(Modifier.width(4.dp))
                                    Text("অডিও", fontFamily = HindSiliguriFontFamily)
                                }
                            }
                        }
                    }
                }
            }
            Spacer(Modifier.height(14.dp))
        }
    }
}

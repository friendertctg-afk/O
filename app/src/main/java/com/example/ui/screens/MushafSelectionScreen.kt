package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.MushafEdition
import com.example.data.local.QuranLayoutStyle
import com.example.data.seed.QuranBengaliData
import com.example.data.seed.QuranMetadata
import com.example.ui.components.BanglaMushafBookType
import com.example.ui.components.BanglaMushafCoverThumbnail
import com.example.ui.theme.HindSiliguriFontFamily

val ForestGreenHeader = Color(0xFF2B4C3F)
private val MushafSelectionOffWhite = Color(0xFFF6F7F6)
private val MutedSlateGray = Color(0xFF637069)

data class MushafEditionCategory(
    val bookType: BanglaMushafBookType,
    val titleBn: String,
    val badgeBn: String,
    val edition: MushafEdition,
    val layout: QuranLayoutStyle,
    val showTranslation: Boolean,
)

val defaultBanglaMushafCategories: List<MushafEditionCategory> = listOf(
    MushafEditionCategory(
        bookType = BanglaMushafBookType.TAFSEER,
        titleBn = "তাফসীরে কুরআন",
        badgeBn = "অনুবাদ ও তাফসীর",
        edition = MushafEdition.MADANI,
        layout = QuranLayoutStyle.AYAH_BY_AYAH,
        showTranslation = true,
    ),
    MushafEditionCategory(
        bookType = BanglaMushafBookType.HAFIZI_EMDADIA,
        titleBn = "হাফিজী কুরআন\n(এমদাদিয়া লাইব্রেরী)",
        badgeBn = "৬১১ পৃষ্ঠা • ১৫ লাইন",
        edition = MushafEdition.HAFIZI,
        layout = QuranLayoutStyle.HAFIZI_PAGE,
        showTranslation = false,
    ),
    MushafEditionCategory(
        bookType = BanglaMushafBookType.COLOR_MADINAH,
        titleBn = "রঙিন মুসহাফে মাদীনা",
        badgeBn = "৬০৪ পৃষ্ঠা • মাদানী",
        edition = MushafEdition.MADANI,
        layout = QuranLayoutStyle.HAFIZI_PAGE,
        showTranslation = false,
    ),
    MushafEditionCategory(
        bookType = BanglaMushafBookType.COLOR_TAJWEED,
        titleBn = "রঙিন তাজবীদ কুরআন",
        badgeBn = "৬০৪ পৃষ্ঠা • তাজবীদ",
        edition = MushafEdition.TAJWEED,
        layout = QuranLayoutStyle.HAFIZI_PAGE,
        showTranslation = false,
    ),
    MushafEditionCategory(
        bookType = BanglaMushafBookType.INDO_PAK_GREEN_GOLD,
        titleBn = "ইন্দো-পাক\n১৫ লাইন কুরআন",
        badgeBn = "৬১১ পৃষ্ঠা • নাস্তালিক",
        edition = MushafEdition.INDO_PAK,
        layout = QuranLayoutStyle.HAFIZI_PAGE,
        showTranslation = false,
    ),
    MushafEditionCategory(
        bookType = BanglaMushafBookType.UTHMANI_CLASSIC,
        titleBn = "ক্লাসিক উসমানী\nমুসহাফ",
        badgeBn = "৬০৪ পৃষ্ঠা • উসমানী",
        edition = MushafEdition.UTHMANI,
        layout = QuranLayoutStyle.HAFIZI_PAGE,
        showTranslation = false,
    ),
)

@Composable
fun MushafSelectionScreen(
    selectedEdition: MushafEdition,
    selectedLayout: QuranLayoutStyle,
    lastReadSurah: Int,
    lastReadAyah: Int,
    lastReadPage: Int,
    khatamPagesRead: Int,
    onSelectEdition: (MushafEditionCategory) -> Unit,
    onResumeLastRead: (pageNumber: Int) -> Unit,
    onIncrementKhatamPage: () -> Unit,
    onBackClick: () -> Unit,
    onOpenSurahList: () -> Unit = {},
    onOpenBookmarks: () -> Unit = {},
    onOpenOfflineManager: () -> Unit = {},
    totalBookmarkCount: Int = 0,
    showHeader: Boolean = true,
    showListActionsInHeader: Boolean = false,
    onSearchClick: () -> Unit = {},
    categories: List<MushafEditionCategory> = defaultBanglaMushafCategories,
    modifier: Modifier = Modifier,
) {
    var showOverflowMenu by remember { mutableStateOf(false) }

    val lastSurahBn = QuranBengaliData.getSurahInfo(lastReadSurah)
    val totalEditionPages = QuranMetadata.totalPagesForEdition(selectedEdition)
    val totalEditionPagesBn = QuranBengaliData.toBengaliDigits(totalEditionPages)
    val safeLastReadPage = lastReadPage.coerceIn(1, totalEditionPages)

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MushafSelectionOffWhite),
        contentAlignment = Alignment.TopCenter
    ) {
        Column(
            modifier = Modifier
                .widthIn(max = 900.dp)
                .fillMaxSize()
        ) {
            if (showHeader) {
                MushafSelectionTopHeader(
                    title = "কুরআন",
                    showListActions = showListActionsInHeader,
                    onBackClick = onBackClick,
                    onBookmarkClick = onOpenBookmarks,
                    onSearchClick = onSearchClick,
                    showMenu = showOverflowMenu,
                    onToggleMenu = { showOverflowMenu = it },
                    menuContent = {
                        DropdownMenuItem(
                            text = {
                                Text(
                                    "সর্বশেষ পাঠ (পৃ. ${QuranBengaliData.toBengaliDigits(safeLastReadPage)})",
                                    fontFamily = HindSiliguriFontFamily
                                )
                            },
                            onClick = {
                                showOverflowMenu = false
                                onResumeLastRead(safeLastReadPage)
                            }
                        )
                        DropdownMenuItem(
                            text = {
                                Text("সূরা ও পারা তালিকা", fontFamily = HindSiliguriFontFamily)
                            },
                            onClick = {
                                showOverflowMenu = false
                                onOpenSurahList()
                            }
                        )
                        DropdownMenuItem(
                            text = {
                                Text(
                                    "বুকমার্কসমূহ (${QuranBengaliData.toBengaliDigits(totalBookmarkCount)})",
                                    fontFamily = HindSiliguriFontFamily
                                )
                            },
                            onClick = {
                                showOverflowMenu = false
                                onOpenBookmarks()
                            }
                        )
                        DropdownMenuItem(
                            text = {
                                Text("অফলাইন ও অডিও ডাউনলোড", fontFamily = HindSiliguriFontFamily)
                            },
                            onClick = {
                                showOverflowMenu = false
                                onOpenOfflineManager()
                            }
                        )
                    }
                )
            }

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier
                    .fillMaxSize()
                    .testTag("quran_mushaf_grid"),
                contentPadding = PaddingValues(horizontal = 22.dp, vertical = 18.dp),
                horizontalArrangement = Arrangement.spacedBy(22.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp),
            ) {
                items(categories, key = { it.bookType.name }) { option ->
                    val isCurrentSelection = when (option.bookType) {
                        BanglaMushafBookType.TAFSEER ->
                            selectedLayout == QuranLayoutStyle.AYAH_BY_AYAH
                        BanglaMushafBookType.HAFIZI_EMDADIA ->
                            selectedEdition == MushafEdition.HAFIZI &&
                                selectedLayout != QuranLayoutStyle.AYAH_BY_AYAH
                        BanglaMushafBookType.COLOR_MADINAH ->
                            selectedEdition == MushafEdition.MADANI &&
                                selectedLayout != QuranLayoutStyle.AYAH_BY_AYAH
                        BanglaMushafBookType.COLOR_TAJWEED ->
                            selectedEdition == MushafEdition.TAJWEED &&
                                selectedLayout != QuranLayoutStyle.AYAH_BY_AYAH
                        BanglaMushafBookType.INDO_PAK_GREEN_GOLD ->
                            selectedEdition == MushafEdition.INDO_PAK &&
                                selectedLayout != QuranLayoutStyle.AYAH_BY_AYAH
                        BanglaMushafBookType.UTHMANI_CLASSIC ->
                            selectedEdition == MushafEdition.UTHMANI &&
                                selectedLayout != QuranLayoutStyle.AYAH_BY_AYAH
                    }

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .clickable { onSelectEdition(option) }
                            .padding(4.dp)
                            .testTag("mushaf_cover_${option.bookType.name.lowercase()}"),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(modifier = Modifier.fillMaxWidth()) {
                            BanglaMushafCoverThumbnail(
                                type = option.bookType,
                                isSelected = isCurrentSelection,
                                modifier = Modifier.fillMaxWidth()
                            )
                            if (isCurrentSelection) {
                                Surface(
                                    shape = CircleShape,
                                    color = ForestGreenHeader,
                                    shadowElevation = 3.dp,
                                    modifier = Modifier
                                        .align(Alignment.TopEnd)
                                        .padding(6.dp)
                                        .size(24.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(
                                            imageVector = Icons.Filled.Check,
                                            contentDescription = "নির্বাচিত সংস্করণ",
                                            tint = Color.White,
                                            modifier = Modifier.size(14.dp)
                                        )
                                    }
                                }
                            }
                        }
                        Spacer(Modifier.height(10.dp))
                        Text(
                            text = option.titleBn,
                            fontFamily = HindSiliguriFontFamily,
                            fontSize = 16.sp,
                            lineHeight = 22.sp,
                            fontWeight = if (isCurrentSelection) FontWeight.Bold else FontWeight.SemiBold,
                            color = ForestGreenHeader,
                            textAlign = TextAlign.Center,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                        Spacer(Modifier.height(2.dp))
                        Text(
                            text = option.badgeBn,
                            fontFamily = HindSiliguriFontFamily,
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.Medium,
                            color = MutedSlateGray,
                            textAlign = TextAlign.Center,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                // Compact Resume Reading & Khatam Strip below the 2-column Mushaf shelf
                item(span = { GridItemSpan(2) }) {
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 6.dp)
                            .testTag("quran_continue_hero_card"),
                        shape = RoundedCornerShape(14.dp),
                        color = Color.White,
                        border = BorderStroke(1.dp, ForestGreenHeader.copy(alpha = 0.2f)),
                        shadowElevation = 2.dp
                    ) {
                        Column(
                            modifier = Modifier.padding(14.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "সর্বশেষ পাঠ: ${lastSurahBn.nameBn}",
                                        fontFamily = HindSiliguriFontFamily,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 15.sp,
                                        color = ForestGreenHeader
                                    )
                                    Text(
                                        text = "পৃষ্ঠা ${QuranBengaliData.toBengaliDigits(safeLastReadPage)} / $totalEditionPagesBn • আয়াত ${QuranBengaliData.toBengaliDigits(lastReadAyah)}",
                                        fontFamily = HindSiliguriFontFamily,
                                        fontSize = 12.5.sp,
                                        color = MutedSlateGray
                                    )
                                }
                                Button(
                                    onClick = { onResumeLastRead(safeLastReadPage) },
                                    colors = ButtonDefaults.buttonColors(containerColor = ForestGreenHeader),
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier.testTag("open_hafizi_mushaf_button")
                                ) {
                                    Icon(
                                        Icons.AutoMirrored.Filled.MenuBook,
                                        contentDescription = null,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(Modifier.width(6.dp))
                                    Text(
                                        "পড়ুন",
                                        fontFamily = HindSiliguriFontFamily,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                            }
                            val khatamFrac = (khatamPagesRead / totalEditionPages.toFloat()).coerceIn(0f, 1f)
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "খতম অগ্রগতি: ${QuranBengaliData.toBengaliDigits(khatamPagesRead.coerceAtMost(totalEditionPages))} / $totalEditionPagesBn পৃষ্ঠা",
                                    fontFamily = HindSiliguriFontFamily,
                                    fontSize = 12.sp,
                                    color = MutedSlateGray
                                )
                                Text(
                                    text = "+১ পৃষ্ঠা",
                                    fontFamily = HindSiliguriFontFamily,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = ForestGreenHeader,
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .clickable { onIncrementKhatamPage() }
                                        .padding(horizontal = 8.dp, vertical = 2.dp)
                                )
                            }
                            LinearProgressIndicator(
                                progress = { khatamFrac },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(5.dp)
                                    .clip(RoundedCornerShape(3.dp)),
                                color = ForestGreenHeader,
                                trackColor = ForestGreenHeader.copy(alpha = 0.14f)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MushafSelectionTopHeader(
    title: String = "কুরআন",
    showListActions: Boolean = false,
    onBackClick: () -> Unit,
    onBookmarkClick: () -> Unit = {},
    onSearchClick: () -> Unit = {},
    showMenu: Boolean = false,
    onToggleMenu: (Boolean) -> Unit = {},
    menuContent: @Composable () -> Unit = {},
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = ForestGreenHeader,
        shadowElevation = 2.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp)
                .padding(horizontal = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .clickable(onClick = onBackClick)
                    .testTag("quran_header_back_btn"),
                contentAlignment = Alignment.Center
            ) {
                Surface(
                    shape = CircleShape,
                    color = Color.Transparent,
                    border = BorderStroke(1.3.dp, Color.White.copy(alpha = 0.85f)),
                    modifier = Modifier.size(32.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                            contentDescription = "Back",
                            tint = Color.White,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }
            }

            Text(
                text = title,
                fontFamily = HindSiliguriFontFamily,
                fontSize = 21.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.White,
                textAlign = TextAlign.Center,
                modifier = Modifier.weight(1f)
            )

            if (showListActions) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .clickable(onClick = onBookmarkClick)
                            .testTag("quran_header_bookmark_btn"),
                        contentAlignment = Alignment.Center
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = Color.Transparent,
                            border = BorderStroke(1.3.dp, Color.White.copy(alpha = 0.85f)),
                            modifier = Modifier.size(32.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Filled.Bookmark,
                                    contentDescription = "বুকমার্কসমূহ",
                                    tint = Color.White,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }
                    IconButton(
                        onClick = onSearchClick,
                        modifier = Modifier
                            .size(48.dp)
                            .testTag("quran_header_search_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Search,
                            contentDescription = "অনুসন্ধান",
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            } else {
                Box {
                    IconButton(
                        onClick = { onToggleMenu(true) },
                        modifier = Modifier
                            .size(48.dp)
                            .testTag("quran_header_menu_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Filled.MoreVert,
                            contentDescription = "অপশন মেনু",
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    DropdownMenu(
                        expanded = showMenu,
                        onDismissRequest = { onToggleMenu(false) }
                    ) {
                        menuContent()
                    }
                }
            }
        }
    }
}

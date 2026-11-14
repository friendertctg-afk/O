package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.LibraryBooks
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Checklist
import androidx.compose.material.icons.filled.ChildCare
import androidx.compose.material.icons.filled.CompassCalibration
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FlightTakeoff
import androidx.compose.material.icons.filled.FormatListNumbered
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Headphones
import androidx.compose.material.icons.filled.HistoryEdu
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Mosque
import androidx.compose.material.icons.filled.NightsStay
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material.icons.filled.Wallpaper
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.audio.AudioPlaybackState
import com.example.data.local.UserPreferencesState
import com.example.ui.components.QariAndAudioSheet
import com.example.ui.navigation.HidayahRoutes
import com.example.utils.WallpaperAndAlertUtils

private enum class ExploreCategoryGroup(val label: String) {
    ALL("All (24)"),
    QURAN("Quran & Study"),
    WORSHIP("Worship"),
    LEARNING("Learning"),
    TOOLS("Lifestyle & Tools"),
}

private data class ExploreFolderItem(
    val id: String,
    val title: String,
    val description: String,
    val badge: String,
    val icon: ImageVector,
    val accentColor: Color,
    val group: ExploreCategoryGroup,
    val route: String?,
)

@Composable
fun ExploreHubScreen(
    prefs: UserPreferencesState,
    audioState: AudioPlaybackState,
    favoriteQariIds: List<String>,
    onSelectQari: (String) -> Unit,
    onToggleFavoriteQari: (String) -> Unit,
    onNavigate: (String) -> Unit,
) {
    val context = LocalContext.current
    var query by remember { mutableStateOf("") }
    var selectedGroup by remember { mutableStateOf(ExploreCategoryGroup.ALL) }
    var showQariAudioSheet by remember { mutableStateOf(false) }

    val folders = remember {
        listOf(
            ExploreFolderItem(
                "quran_mushaf", "Quran & Mushaf", "604-page Hafizi & Madani Mushaf reader",
                "114 Surahs", Icons.AutoMirrored.Filled.MenuBook, Color(0xFF0E6B4F),
                ExploreCategoryGroup.QURAN, HidayahRoutes.QURAN
            ),
            ExploreFolderItem(
                "quran_audio", "Quran Audio", "20 global Qaris, repeat, speed & offline MP3",
                "20 Qaris", Icons.Filled.Headphones, Color(0xFF1565C0),
                ExploreCategoryGroup.QURAN, null
            ),
            ExploreFolderItem(
                "tafsir", "Tafsir & Insight", "Ibn Kathir, Jalalayn & verse commentary",
                "Tafsir", Icons.Filled.AutoStories, Color(0xFF6A1B9A),
                ExploreCategoryGroup.QURAN, "${HidayahRoutes.HADITH_LIBRARY}?tab=1"
            ),
            ExploreFolderItem(
                "hadith", "Hadith Library", "9 authentic Hadith collections with narration",
                "9 Books", Icons.Filled.HistoryEdu, Color(0xFFAD6800),
                ExploreCategoryGroup.QURAN, "${HidayahRoutes.HADITH_LIBRARY}?tab=0"
            ),
            ExploreFolderItem(
                "duas", "Dua & Azkar", "16 daily supplication categories & حصن المسلم",
                "16 Topics", Icons.Filled.Favorite, Color(0xFFC2185B),
                ExploreCategoryGroup.WORSHIP, HidayahRoutes.DUAS
            ),
            ExploreFolderItem(
                "prayer_times", "Prayer Times", "Adhan alerts, next Salah countdown & timetable",
                "Salah", Icons.Filled.AccessTime, Color(0xFF00796B),
                ExploreCategoryGroup.WORSHIP, "${HidayahRoutes.PRAYERS}?tab=0"
            ),
            ExploreFolderItem(
                "qibla", "Qibla Compass", "Real-time Kaaba direction & distance bearing",
                "Kaaba", Icons.Filled.CompassCalibration, Color(0xFF2E7D32),
                ExploreCategoryGroup.WORSHIP, HidayahRoutes.QIBLA
            ),
            ExploreFolderItem(
                "tasbih", "Digital Tasbih", "Tactile Dhikr counter, targets & history",
                "Dhikr", Icons.Filled.FormatListNumbered, Color(0xFF00838F),
                ExploreCategoryGroup.WORSHIP, HidayahRoutes.TASBIH
            ),
            ExploreFolderItem(
                "asma_99", "99 Names of Allah", "Asma-ul-Husna meanings & audio reflection",
                "99 Names", Icons.Filled.AutoAwesome, Color(0xFFB8860B),
                ExploreCategoryGroup.QURAN, HidayahRoutes.ASMA_99
            ),
            ExploreFolderItem(
                "hifz", "Hifz Academy", "Memorization planner, hide-Ayah & revision",
                "Hifz", Icons.Filled.Bookmark, Color(0xFF1B5E20),
                ExploreCategoryGroup.LEARNING, HidayahRoutes.HIFZ
            ),
            ExploreFolderItem(
                "tajweed", "Tajweed Academy", "Makharij, Noon/Meem Sakin, Madd & quizzes",
                "Rules", Icons.Filled.GraphicEq, Color(0xFF4527A0),
                ExploreCategoryGroup.LEARNING, HidayahRoutes.TAJWEED
            ),
            ExploreFolderItem(
                "arabic", "Arabic Learning", "28 letters, Harakat, grammar & conversation",
                "Alphabet", Icons.Filled.Translate, Color(0xFF0277BD),
                ExploreCategoryGroup.LEARNING, HidayahRoutes.ARABIC
            ),
            ExploreFolderItem(
                "stories", "Islamic Stories", "Inspiring Prophet & Sahabah biographies",
                "Stories", Icons.AutoMirrored.Filled.MenuBook, Color(0xFF8D4E00),
                ExploreCategoryGroup.LEARNING, "${HidayahRoutes.HADITH_LIBRARY}?tab=2"
            ),
            ExploreFolderItem(
                "books", "Islamic Books", "Curated classical & Seerah digital library",
                "Library", Icons.AutoMirrored.Filled.LibraryBooks, Color(0xFF5D4037),
                ExploreCategoryGroup.LEARNING, "${HidayahRoutes.HADITH_LIBRARY}?tab=4"
            ),
            ExploreFolderItem(
                "wallpapers", "Islamic Wallpapers", "4K Kaaba, Madinah & calligraphy gallery",
                "4K Art", Icons.Filled.Wallpaper, Color(0xFF3949AB),
                ExploreCategoryGroup.TOOLS, HidayahRoutes.WALLPAPERS
            ),
            ExploreFolderItem(
                "ramadan", "Ramadan Center", "Suhoor, Iftar, Laylatul Qadr & fasting guide",
                "Fasting", Icons.Filled.NightsStay, Color(0xFF283593),
                ExploreCategoryGroup.WORSHIP, "${HidayahRoutes.HADITH_LIBRARY}?tab=3"
            ),
            ExploreFolderItem(
                "hajj_umrah", "Hajj & Umrah Guide", "Step-by-step Ihram, Tawaf & Sa'i rituals",
                "Pilgrimage", Icons.Filled.FlightTakeoff, Color(0xFF00695C),
                ExploreCategoryGroup.WORSHIP, "${HidayahRoutes.HADITH_LIBRARY}?tab=3"
            ),
            ExploreFolderItem(
                "zakat", "Zakat Calculator", "Gold, silver, savings & 2.5% Nisab engine",
                "Nisab", Icons.Filled.Calculate, Color(0xFF558B2F),
                ExploreCategoryGroup.TOOLS, HidayahRoutes.ZAKAT
            ),
            ExploreFolderItem(
                "kids", "Kids Academy", "Noorani Qaida, short Surahs & manners",
                "For Kids", Icons.Filled.ChildCare, Color(0xFFD84315),
                ExploreCategoryGroup.LEARNING, HidayahRoutes.KIDS
            ),
            ExploreFolderItem(
                "ai_assistant", "AI Islamic Assistant", "Ask Quran, Hadith, Fiqh & Dua questions",
                "AI Guide", Icons.Filled.Psychology, Color(0xFF6A1B9A),
                ExploreCategoryGroup.TOOLS, HidayahRoutes.AI_ASSISTANT
            ),
            ExploreFolderItem(
                "life_tracker", "Muslim Life Tracker", "5 daily Salah, Qada, Fasting & Khatam log",
                "Tracker", Icons.Filled.Checklist, Color(0xFF0E6B4F),
                ExploreCategoryGroup.TOOLS, HidayahRoutes.LIFE_TRACKER
            ),
            ExploreFolderItem(
                "nearby_mosques", "Nearby Mosques", "Locate Masjids around you on Maps",
                "Maps GPS", Icons.Filled.LocationOn, Color(0xFFBF360C),
                ExploreCategoryGroup.TOOLS, null
            ),
            ExploreFolderItem(
                "islamic_calendar", "Islamic Calendar", "30-day Hijri & Gregorian Salah schedule",
                "Hijri", Icons.Filled.CalendarMonth, Color(0xFF00796B),
                ExploreCategoryGroup.WORSHIP, "${HidayahRoutes.PRAYERS}?tab=1"
            ),
            ExploreFolderItem(
                "quiz_center", "Islamic Quiz Center", "Knowledge challenges & glossary trainer",
                "Quizzes", Icons.Filled.Quiz, Color(0xFF9C27B0),
                ExploreCategoryGroup.LEARNING, "${HidayahRoutes.HADITH_LIBRARY}?tab=5"
            ),
        )
    }

    val visibleFolders = remember(folders, query, selectedGroup) {
        val q = query.trim()
        folders.filter { item ->
            val groupMatch = selectedGroup == ExploreCategoryGroup.ALL || item.group == selectedGroup
            val textMatch = q.isEmpty() ||
                item.title.contains(q, ignoreCase = true) ||
                item.description.contains(q, ignoreCase = true)
            groupMatch && textMatch
        }
    }

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.TopCenter) {
        LazyVerticalGrid(
            columns = GridCells.Adaptive(minSize = 156.dp),
            modifier = Modifier
                .widthIn(max = 1040.dp)
                .fillMaxSize()
                .testTag("explore_folder_grid"),
            contentPadding = PaddingValues(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item(span = { GridItemSpan(maxLineSpan) }) {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(MaterialTheme.colorScheme.primaryContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Filled.Mosque, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        }
                        Column {
                            Text(
                                "Explore HIDAYAH Library",
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                "24 Islamic modules organized as quick-access folders",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    OutlinedTextField(
                        value = query,
                        onValueChange = { query = it },
                        placeholder = { Text("Search 24 modules (Tafsir, Zakat, Hajj, Qibla...)") },
                        leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null) },
                        singleLine = true,
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("explore_search_input")
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        ExploreCategoryGroup.entries.forEach { grp ->
                            FilterChip(
                                selected = selectedGroup == grp,
                                onClick = { selectedGroup = grp },
                                label = { Text(grp.label) }
                            )
                        }
                    }
                }
            }

            items(visibleFolders, key = { it.id }) { folder ->
                IslamicFolderCard(
                    folder = folder,
                    onClick = {
                        when {
                            folder.id == "quran_audio" -> showQariAudioSheet = true
                            folder.id == "nearby_mosques" -> WallpaperAndAlertUtils.openNearbyMosques(context)
                            folder.route != null -> onNavigate(folder.route)
                        }
                    }
                )
            }
        }
    }

    if (showQariAudioSheet) {
        QariAndAudioSheet(
            surahNumber = prefs.lastReadSurah,
            prefs = prefs,
            audioState = audioState,
            favoriteQariIds = favoriteQariIds,
            onDismiss = { showQariAudioSheet = false },
            onSelectQari = onSelectQari,
            onToggleFavoriteQari = onToggleFavoriteQari,
        )
    }
}

@Composable
private fun IslamicFolderCard(
    folder: ExploreFolderItem,
    onClick: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("explore_folder_${folder.id}")
            .clip(RoundedCornerShape(18.dp))
            .clickable(onClick = onClick)
    ) {
        // Folder Tab Silhouette
        Row(verticalAlignment = Alignment.Bottom) {
            Surface(
                color = folder.accentColor.copy(alpha = 0.20f),
                shape = RoundedCornerShape(topStart = 12.dp, topEnd = 14.dp)
            ) {
                Text(
                    text = folder.badge,
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = folder.accentColor,
                    maxLines = 1,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                )
            }
        }

        // Main Folder Body Card
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .height(142.dp),
            shape = RoundedCornerShape(
                topStart = 0.dp,
                topEnd = 18.dp,
                bottomStart = 18.dp,
                bottomEnd = 18.dp
            ),
            color = MaterialTheme.colorScheme.surface,
            border = BorderStroke(1.2.dp, folder.accentColor.copy(alpha = 0.28f)),
            tonalElevation = 2.dp,
            shadowElevation = 2.dp,
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            listOf(
                                folder.accentColor.copy(alpha = 0.08f),
                                Color.Transparent
                            )
                        )
                    )
            ) {
                // Subtle corner Islamic 8-point geometry accent
                Canvas(
                    modifier = Modifier
                        .size(64.dp)
                        .align(Alignment.BottomEnd)
                ) {
                    val r = size.minDimension * 0.45f
                    val center = Offset(size.width * 0.78f, size.height * 0.78f)
                    drawCircle(
                        color = folder.accentColor.copy(alpha = 0.08f),
                        radius = r,
                        center = center
                    )
                    val path = Path().apply {
                        moveTo(center.x, center.y - r * 0.65f)
                        lineTo(center.x + r * 0.65f, center.y)
                        lineTo(center.x, center.y + r * 0.65f)
                        lineTo(center.x - r * 0.65f, center.y)
                        close()
                    }
                    drawPath(path, color = folder.accentColor.copy(alpha = 0.12f))
                }

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(14.dp),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(RoundedCornerShape(13.dp))
                            .background(folder.accentColor.copy(alpha = 0.16f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = folder.icon,
                            contentDescription = folder.title,
                            tint = folder.accentColor,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                        Text(
                            text = folder.title,
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = folder.description,
                            style = MaterialTheme.typography.bodySmall,
                            fontSize = 11.sp,
                            lineHeight = 14.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
        }
    }
}

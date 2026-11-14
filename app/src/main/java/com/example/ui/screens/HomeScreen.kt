package com.example.ui.screens

import androidx.compose.foundation.Image
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.Checklist
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FormatListNumbered
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.HistoryEdu
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Wallpaper
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.audio.AudioPlaybackState
import com.example.data.local.HifzProgressEntity
import com.example.data.local.UserPreferencesState
import com.example.data.seed.DuaSeedData
import com.example.data.seed.HidayahExtendedData
import com.example.data.seed.QuranMetadata
import com.example.localization.AppLanguage
import com.example.localization.stringsFor
import com.example.ui.components.HidayahLogoMark
import com.example.ui.navigation.HidayahRoutes
import com.example.ui.theme.AmiriFontFamily
import com.example.ui.viewmodel.PrayerUiState
import com.example.utils.WallpaperAndAlertUtils
import java.util.Calendar

private data class QuickModuleTile(
    val title: String,
    val subtitle: String,
    val icon: ImageVector,
    val route: String,
)

@Composable
fun HomeScreen(
    prefs: UserPreferencesState,
    prayerState: PrayerUiState,
    audioState: AudioPlaybackState,
    hifzList: List<HifzProgressEntity>,
    onNavigate: (String) -> Unit,
    onOpenSurah: (Int, Int) -> Unit,
    onPlayContinueAudio: () -> Unit,
    onSaveDashboardConfig: (Boolean, Boolean, Boolean, Boolean, Boolean) -> Unit,
) {
    val strings = stringsFor(prefs.appLanguage)
    val context = LocalContext.current
    var showCustomizeDialog by remember { mutableStateOf(false) }
    var notifToast by remember { mutableStateOf<String?>(null) }

    val dayIndex = remember { Calendar.getInstance().get(Calendar.DAY_OF_YEAR) }
    val dailyHadith = remember(dayIndex) {
        HidayahExtendedData.allHadiths[dayIndex % HidayahExtendedData.allHadiths.size]
    }
    val dailyDua = remember(dayIndex) {
        DuaSeedData.duas[dayIndex % DuaSeedData.duas.size]
    }
    val lastSurahMeta = QuranMetadata.getSurah(prefs.lastReadSurah)
    val totalHifzAyahs = hifzList.sumOf { it.memorizedAyahs }

    val quickModules = remember(strings) {
        listOf(
            QuickModuleTile(strings.qiblaCompass, "Kaaba Bearing", Icons.Filled.Explore, HidayahRoutes.QIBLA),
            QuickModuleTile(strings.tasbihCounter, "Dhikr & Goals", Icons.Filled.FormatListNumbered, HidayahRoutes.TASBIH),
            QuickModuleTile(strings.duasAzkar, "16 Categories", Icons.Filled.Favorite, HidayahRoutes.DUAS),
            QuickModuleTile(strings.hifzQuran, "Memorize", Icons.Filled.Bookmark, HidayahRoutes.HIFZ),
            QuickModuleTile(strings.tajweedAcademy, "Color Rules", Icons.Filled.GraphicEq, HidayahRoutes.TAJWEED),
            QuickModuleTile(strings.learnArabic, "28 Letters", Icons.Filled.Translate, HidayahRoutes.ARABIC),
            QuickModuleTile("Daily Tracker", "5 Prayers", Icons.Filled.Checklist, HidayahRoutes.LIFE_TRACKER),
            QuickModuleTile(strings.asmaUlHusna, "99 Names", Icons.Filled.AutoAwesome, HidayahRoutes.ASMA_99),
            QuickModuleTile(strings.zakatCalculator, "Nisab 2.5%", Icons.Filled.Calculate, HidayahRoutes.ZAKAT),
            QuickModuleTile(strings.islamicLibrary, "9 Collections", Icons.Filled.HistoryEdu, HidayahRoutes.HADITH_LIBRARY),
            QuickModuleTile("Kids Academy", "Qaida & Duas", Icons.Filled.School, HidayahRoutes.KIDS),
            QuickModuleTile(strings.wallpapers, "4K Gallery", Icons.Filled.Wallpaper, HidayahRoutes.WALLPAPERS),
        )
    }

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.TopCenter) {
        LazyColumn(
            modifier = Modifier
                .widthIn(max = 840.dp)
                .fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            // 1. Hero Header Card
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("home_hero_card"),
                    shape = RoundedCornerShape(24.dp),
                ) {
                    Box(modifier = Modifier.fillMaxWidth()) {
                        Image(
                            painter = painterResource(R.drawable.img_hero_emerald_arch),
                            contentDescription = "HIDAYAH Islamic Dashboard Banner",
                            modifier = Modifier.matchParentSize(),
                            contentScale = ContentScale.Crop,
                        )
                        Box(
                            modifier = Modifier
                                .matchParentSize()
                                .background(
                                    Brush.verticalGradient(
                                        listOf(Color(0xCC06281E), Color(0xEC08221A))
                                    )
                                )
                        )
                        Column(
                            modifier = Modifier.padding(18.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    HidayahLogoMark(size = 44.dp, darkSurfaceVariant = true)
                                    Column {
                                        Text(
                                            text = "${strings.greeting}!",
                                            style = MaterialTheme.typography.titleMedium,
                                            color = Color(0xFFF6E7BD),
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(
                                            text = "${strings.appTitle} (${strings.appArabicTitle})",
                                            style = MaterialTheme.typography.labelMedium,
                                            color = Color.White.copy(alpha = 0.85f)
                                        )
                                    }
                                }
                                Row {
                                    IconButton(
                                        onClick = {
                                            val sent = WallpaperAndAlertUtils.sendPrayerTestNotification(
                                                context,
                                                "HIDAYAH • ${prayerState.nextPrayerName} Reminder",
                                                "Next prayer ${prayerState.nextPrayerName} at ${prayerState.nextPrayerTime} (${prayerState.schedule.cityName}).",
                                                withSound = prefs.adhanNotificationEnabled
                                            )
                                            notifToast = if (sent) {
                                                "Prayer alert notification sent!"
                                            } else {
                                                "Enable Notification permission in Prayer Alerts to receive reminders."
                                            }
                                        },
                                        modifier = Modifier.testTag("home_notification_button")
                                    ) {
                                        Icon(
                                            Icons.Filled.NotificationsActive,
                                            contentDescription = "Test Prayer Notification",
                                            tint = Color(0xFFE7C563)
                                        )
                                    }
                                    IconButton(
                                        onClick = { showCustomizeDialog = true },
                                        modifier = Modifier.testTag("customize_dashboard_button")
                                    ) {
                                        Icon(
                                            Icons.Filled.Tune,
                                            contentDescription = "Customize Home Dashboard",
                                            tint = Color.White
                                        )
                                    }
                                }
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = prayerState.schedule.hijriDate,
                                        style = MaterialTheme.typography.titleSmall,
                                        color = Color(0xFFE7C563),
                                        fontWeight = FontWeight.SemiBold,
                                    )
                                    Text(
                                        text = prayerState.schedule.gregorianDate,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = Color.White.copy(alpha = 0.78f),
                                    )
                                }
                                Surface(
                                    color = Color.White.copy(alpha = 0.14f),
                                    shape = RoundedCornerShape(20.dp),
                                    modifier = Modifier.clickable { onNavigate(HidayahRoutes.PRAYERS) }
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        Icon(
                                            Icons.Filled.LocationOn,
                                            contentDescription = null,
                                            tint = Color(0xFFE7C563),
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Text(
                                            text = prayerState.schedule.cityName,
                                            style = MaterialTheme.typography.labelLarge,
                                            color = Color.White
                                        )
                                    }
                                }
                            }

                            notifToast?.let { msg ->
                                Text(
                                    text = msg,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color(0xFFE7C563)
                                )
                            }
                        }
                    }
                }
            }

            // 2. Next Prayer & Timetable Strip
            if (prefs.showDashboardPrayerCard) {
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("home_prayer_card")
                            .clickable { onNavigate(HidayahRoutes.PRAYERS) },
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Icon(
                                        Icons.Filled.AccessTime,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                    Column {
                                        Text(
                                            text = "${strings.nextPrayer}: ${prayerState.nextPrayerName}",
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(
                                            text = "Adhan at ${prayerState.nextPrayerTime}",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                                Surface(
                                    color = MaterialTheme.colorScheme.primaryContainer,
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Text(
                                        text = prayerState.countdownText,
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onPrimaryContainer
                                    )
                                }
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                PrayerSlotPill(strings.fajr, prayerState.schedule.fajr, prayerState.nextPrayerName == "Fajr")
                                PrayerSlotPill(strings.dhuhr, prayerState.schedule.dhuhr, prayerState.nextPrayerName == "Dhuhr")
                                PrayerSlotPill(strings.asr, prayerState.schedule.asr, prayerState.nextPrayerName == "Asr")
                                PrayerSlotPill(strings.maghrib, prayerState.schedule.maghrib, prayerState.nextPrayerName == "Maghrib")
                                PrayerSlotPill(strings.isha, prayerState.schedule.isha, prayerState.nextPrayerName == "Isha")
                            }
                        }
                    }
                }
            }

            // 3. Continue Reading Quran + Recent Audio + AI Assistant Shortcut
            if (prefs.showDashboardContinueReading) {
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("home_continue_reading_card"),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
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
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = strings.continueReading,
                                        style = MaterialTheme.typography.labelLarge,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                    Text(
                                        text = "${lastSurahMeta.number}. ${lastSurahMeta.englishName} • Ayah ${prefs.lastReadAyah}",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onPrimaryContainer
                                    )
                                    Text(
                                        text = "Reciter: ${audioState.qari.name}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f),
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                                Text(
                                    text = lastSurahMeta.arabicName,
                                    fontFamily = AmiriFontFamily,
                                    fontSize = 24.sp,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Button(
                                    onClick = { onOpenSurah(prefs.lastReadSurah, prefs.lastReadAyah) },
                                    modifier = Modifier
                                        .weight(1f)
                                        .testTag("home_resume_quran_button")
                                ) {
                                    Icon(Icons.AutoMirrored.Filled.MenuBook, contentDescription = null)
                                    Spacer(Modifier.width(6.dp))
                                    Text("Read Now")
                                }
                                FilledTonalButton(
                                    onClick = onPlayContinueAudio,
                                    modifier = Modifier
                                        .weight(1f)
                                        .testTag("home_play_recent_audio_button")
                                ) {
                                    Icon(Icons.Filled.PlayCircle, contentDescription = null)
                                    Spacer(Modifier.width(6.dp))
                                    Text("Listen")
                                }
                            }
                        }
                    }
                }
            }

            // AI Islamic Assistant Spotlight Banner
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("home_ai_shortcut_card")
                        .clickable { onNavigate(HidayahRoutes.AI_ASSISTANT) },
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer),
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(46.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.secondary),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Filled.Psychology,
                                contentDescription = "AI Islamic Assistant",
                                tint = MaterialTheme.colorScheme.onSecondary
                            )
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "HIDAYAH AI Islamic Assistant",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = strings.askAiHint,
                                style = MaterialTheme.typography.bodySmall,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
            }

            // 4. Quick Access Islamic Modules
            if (prefs.showDashboardQuickActions) {
                item {
                    Text(
                        text = strings.quickActions,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
                item {
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        items(quickModules) { tile ->
                            Card(
                                modifier = Modifier
                                    .width(126.dp)
                                    .testTag("quick_tile_${tile.route}")
                                    .clickable { onNavigate(tile.route) },
                                shape = RoundedCornerShape(18.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                            ) {
                                Column(
                                    modifier = Modifier.padding(12.dp),
                                    verticalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(38.dp)
                                            .clip(RoundedCornerShape(12.dp))
                                            .background(MaterialTheme.colorScheme.primaryContainer),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            tile.icon,
                                            contentDescription = tile.title,
                                            tint = MaterialTheme.colorScheme.primary
                                        )
                                    }
                                    Text(
                                        tile.title,
                                        style = MaterialTheme.typography.labelLarge,
                                        fontWeight = FontWeight.Bold,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Text(
                                        tile.subtitle,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        maxLines = 1
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // 5. Personal Worship Progress Summary
            if (prefs.showDashboardWorshipProgress) {
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onNavigate(HidayahRoutes.LIFE_TRACKER) },
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
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
                                Text(
                                    "Personal Worship Progress",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    "Open Tracker",
                                    style = MaterialTheme.typography.labelLarge,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                WorshipStatMini("Daily Prayers", "${prefs.completedDailyPrayers.size}/5")
                                WorshipStatMini("Tasbih Total", "${prefs.totalTasbihCount}")
                                WorshipStatMini("Hifz Ayahs", "$totalHifzAyahs")
                                WorshipStatMini("Khatam", "${prefs.khatamPagesRead}/604")
                            }
                            LinearProgressIndicator(
                                progress = { (prefs.completedDailyPrayers.size / 5f).coerceIn(0f, 1f) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(7.dp)
                                    .clip(RoundedCornerShape(4.dp))
                            )
                        }
                    }
                }
            }

            // 6. Daily Ayah, Daily Hadith & Daily Dua
            if (prefs.showDashboardDailyInspirations) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                strings.verseOfTheDay,
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.secondary,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "فَإِنَّ مَعَ ٱلْعُسْرِ يُسْرًا",
                                fontFamily = AmiriFontFamily,
                                fontSize = 24.sp,
                                textAlign = TextAlign.End,
                                modifier = Modifier.fillMaxWidth()
                            )
                            Text(
                                text = if (prefs.appLanguage == AppLanguage.BENGALI) {
                                    "নিশ্চয়ই কষ্টের সাথেই রয়েছে স্বস্তি। (সূরা আশ-শারহ ৯৪:৫)"
                                } else {
                                    "For indeed, with hardship comes ease. (Surah Ash-Sharh 94:5)"
                                },
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }

                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onNavigate(HidayahRoutes.HADITH_LIBRARY) },
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                "${strings.dailyHadith} • ${dailyHadith.collection}",
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = if (prefs.appLanguage == AppLanguage.BENGALI) dailyHadith.bengaliText else dailyHadith.englishText,
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Text(
                                text = "${dailyHadith.narrator} • ${dailyHadith.number} (${dailyHadith.grade})",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onNavigate(HidayahRoutes.DUAS) },
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                "${strings.dailyDua} • ${if (prefs.appLanguage == AppLanguage.BENGALI) dailyDua.titleBn else dailyDua.titleEn}",
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.secondary,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = dailyDua.arabic,
                                fontFamily = AmiriFontFamily,
                                fontSize = 20.sp,
                                textAlign = TextAlign.End,
                                modifier = Modifier.fillMaxWidth()
                            )
                            Text(
                                text = if (prefs.appLanguage == AppLanguage.BENGALI) dailyDua.bengaliTranslation else dailyDua.englishTranslation,
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Text(
                                text = dailyDua.reference,
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            item { Spacer(Modifier.height(24.dp)) }
        }
    }

    if (showCustomizeDialog) {
        var p by remember { mutableStateOf(prefs.showDashboardPrayerCard) }
        var c by remember { mutableStateOf(prefs.showDashboardContinueReading) }
        var q by remember { mutableStateOf(prefs.showDashboardQuickActions) }
        var w by remember { mutableStateOf(prefs.showDashboardWorshipProgress) }
        var d by remember { mutableStateOf(prefs.showDashboardDailyInspirations) }

        AlertDialog(
            onDismissRequest = { showCustomizeDialog = false },
            title = { Text("Customize Home Dashboard") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    DashboardSwitchRow("Prayer Timetable & Countdown", p) { p = it }
                    DashboardSwitchRow("Continue Reading & Recent Audio", c) { c = it }
                    DashboardSwitchRow("Quick Modules Strip", q) { q = it }
                    DashboardSwitchRow("Personal Worship Progress", w) { w = it }
                    DashboardSwitchRow("Daily Ayah, Hadith & Dua", d) { d = it }
                }
            },
            confirmButton = {
                Button(onClick = {
                    onSaveDashboardConfig(p, c, d, q, w)
                    showCustomizeDialog = false
                }) { Text("Save") }
            },
            dismissButton = {
                TextButton(onClick = { showCustomizeDialog = false }) { Text("Cancel") }
            }
        )
    }
}

@Composable
private fun PrayerSlotPill(label: String, time: String, isNext: Boolean) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = if (isNext) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
        contentColor = if (isNext) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(label, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.SemiBold)
            Text(time.substringBefore(" "), style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun WorshipStatMini(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
        Text(label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
private fun DashboardSwitchRow(label: String, checked: Boolean, onChange: (Boolean) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.weight(1f))
        Switch(checked = checked, onCheckedChange = onChange)
    }
}

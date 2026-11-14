package com.example.ui.screens

import android.speech.tts.TextToSpeech
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.ChildCare
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.audio.AudioPlayerManager
import com.example.data.local.HifzProgressEntity
import com.example.data.local.LessonProgressEntity
import com.example.data.local.UserPreferencesState
import com.example.data.seed.HidayahExtendedData
import com.example.data.seed.QariCatalog
import com.example.data.seed.QuranMetadata
import com.example.localization.AppLanguage
import com.example.ui.navigation.HidayahRoutes
import com.example.ui.theme.AmiriFontFamily
import java.util.Locale

@Composable
fun LearnHubScreen(
    prefs: UserPreferencesState,
    hifzList: List<HifzProgressEntity>,
    lessons: List<LessonProgressEntity>,
    onNavigate: (String) -> Unit,
) {
    val totalMemorized = hifzList.sumOf { it.memorizedAyahs }
    val completedLessons = lessons.count { it.completed }
    val isBengali = prefs.appLanguage == AppLanguage.BENGALI

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.TopCenter) {
        LazyColumn(
            modifier = Modifier
                .widthIn(max = 840.dp)
                .fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Text(
                    if (isBengali) "ইসলামিক শিক্ষা একাডেমি" else "HIDAYAH Learning Academy",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    "Hifz memorization, Tajweed mastery, Arabic from Alif to Nahw/Sarf, and Kids Islamic Academy.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            item {
                LearnModuleCard(
                    title = if (isBengali) "হিফজুল কুরআন (Hifz Memorization)" else "Hifzul Quran Memorization",
                    subtitle = "$totalMemorized / 6,236 Ayahs memorized • Daily goal: ${prefs.dailyHifzTarget} Ayahs",
                    icon = Icons.Filled.Bookmark,
                    testTag = "learn_card_hifz",
                    onClick = { onNavigate(HidayahRoutes.HIFZ) }
                )
            }
            item {
                LearnModuleCard(
                    title = if (isBengali) "তাজউইদ একাডেমি (Tajweed)" else "Tajweed Mastery & Quizzes",
                    subtitle = "Makharij, Sifat, Noon/Meem Sakinah, Madd, Qalqalah + Audio & Quiz",
                    icon = Icons.Filled.GraphicEq,
                    testTag = "learn_card_tajweed",
                    onClick = { onNavigate(HidayahRoutes.TAJWEED) }
                )
            }
            item {
                LearnModuleCard(
                    title = if (isBengali) "আরবি শিক্ষা একাডেমি (Arabic)" else "Arabic Learning Academy",
                    subtitle = "28 Alphabet Letters, Harakat, Conversations, Nahw & Sarf, Quranic Words ($completedLessons completed)",
                    icon = Icons.Filled.Translate,
                    testTag = "learn_card_arabic",
                    onClick = { onNavigate(HidayahRoutes.ARABIC) }
                )
            }
            item {
                LearnModuleCard(
                    title = if (isBengali) "শিশুদের ইসলামিক একাডেমি" else "Kids Islamic Academy",
                    subtitle = "Noorani Qaida, Short Surahs, Basic Duas & Islamic Manners with voice",
                    icon = Icons.Filled.ChildCare,
                    testTag = "learn_card_kids",
                    onClick = { onNavigate(HidayahRoutes.KIDS) }
                )
            }
            item {
                LearnModuleCard(
                    title = "Ask HIDAYAH AI Tutor",
                    subtitle = "Interactive Tajweed, Arabic Grammar & Hifz revision coaching",
                    icon = Icons.Filled.Psychology,
                    testTag = "learn_card_ai",
                    onClick = { onNavigate(HidayahRoutes.AI_ASSISTANT) }
                )
            }
        }
    }
}

@Composable
private fun LearnModuleCard(
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    testTag: String,
    onClick: () -> Unit,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag(testTag)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Surface(
                color = MaterialTheme.colorScheme.primaryContainer,
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.size(48.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(icon, contentDescription = title, tint = MaterialTheme.colorScheme.primary)
                }
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text(subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HifzScreen(
    prefs: UserPreferencesState,
    hifzList: List<HifzProgressEntity>,
    onSetDailyTarget: (Int) -> Unit,
    onUpdateHifz: (surahNumber: Int, memorizedAyahs: Int, dailyTarget: Int) -> Unit,
    onOpenReader: (Int, Int) -> Unit,
    onBack: () -> Unit,
) {
    val context = LocalContext.current
    val progressMap = remember(hifzList) { hifzList.associateBy { it.surahNumber } }
    val totalMemorized = hifzList.sumOf { it.memorizedAyahs }
    val memorizedSurahs = hifzList.count { it.status == "memorized" }

    // Interactive Hide/Reveal drill for starter memorization surahs
    var drillIndex by remember { mutableIntStateOf(0) }
    var hideArabic by remember { mutableStateOf(false) }
    val drillAyahs = remember { QuranMetadata.bundledAyahs }
    val currentDrill = drillAyahs[drillIndex % drillAyahs.size]
    val drillMeta = QuranMetadata.getSurah(currentDrill.surahNumber)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Hifzul Quran Memorization") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
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
            LazyColumn(
                modifier = Modifier
                    .widthIn(max = 840.dp)
                    .fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                        shape = RoundedCornerShape(20.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text("Personal Hifz Dashboard", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                            Text("Total Memorized: $totalMemorized / 6,236 Ayahs • $memorizedSurahs / 114 Surahs Complete")
                            LinearProgressIndicator(
                                progress = { (totalMemorized / 6236f).coerceIn(0f, 1f) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(8.dp)
                                    .clip(RoundedCornerShape(4.dp))
                            )
                            Text("Daily Memorization Target: ${prefs.dailyHifzTarget} Ayahs/day", style = MaterialTheme.typography.labelLarge)
                            Slider(
                                value = prefs.dailyHifzTarget.toFloat(),
                                onValueChange = { onSetDailyTarget(it.toInt()) },
                                valueRange = 1f..30f
                            )
                        }
                    }
                }

                // Hide & Reveal Memorization Drill
                item {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        shape = RoundedCornerShape(20.dp)
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
                                    "Hide & Reveal Drill • ${drillMeta.englishName} (${currentDrill.surahNumber}:${currentDrill.ayahNumber})",
                                    fontWeight = FontWeight.Bold
                                )
                                IconButton(
                                    onClick = { hideArabic = !hideArabic },
                                    modifier = Modifier.testTag("hifz_toggle_hide_button")
                                ) {
                                    Icon(
                                        if (hideArabic) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                                        contentDescription = "Hide or reveal Ayah"
                                    )
                                }
                            }
                            Surface(
                                modifier = Modifier.fillMaxWidth(),
                                color = MaterialTheme.colorScheme.surfaceVariant,
                                shape = RoundedCornerShape(14.dp)
                            ) {
                                Text(
                                    text = if (hideArabic) {
                                        "Recite from memory, then tap the eye icon to check..."
                                    } else {
                                        currentDrill.uthmaniText
                                    },
                                    fontFamily = if (hideArabic) MaterialTheme.typography.bodyLarge.fontFamily else AmiriFontFamily,
                                    fontSize = if (hideArabic) 15.sp else 24.sp,
                                    textAlign = if (hideArabic) TextAlign.Center else TextAlign.End,
                                    modifier = Modifier.padding(16.dp)
                                )
                            }
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Button(onClick = {
                                    AudioPlayerManager.setAyahRepeat(3)
                                    AudioPlayerManager.playAyah(
                                        context,
                                        currentDrill.surahNumber,
                                        currentDrill.ayahNumber,
                                        QariCatalog.findById(prefs.selectedQariId)
                                    )
                                }) { Text("Listen (3x)") }
                                OutlinedButton(onClick = {
                                    drillIndex = (drillIndex + 1) % drillAyahs.size
                                }) { Text("Next Verse") }
                            }
                        }
                    }
                }

                // Juz & Surah Tracker (showing Juz 30 & all Surahs compactly)
                items(QuranMetadata.surahs, key = { it.number }) { surah ->
                    val existing = progressMap[surah.number]
                    val memorized = existing?.memorizedAyahs ?: 0
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(14.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text("${surah.number}. ${surah.englishName} (${surah.arabicName})", fontWeight = FontWeight.Bold)
                                    Text("$memorized / ${surah.ayahCount} Ayahs • ${existing?.status ?: "not started"}", style = MaterialTheme.typography.bodySmall)
                                }
                                TextButton(onClick = { onOpenReader(surah.number, 1) }) { Text("Read") }
                            }
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                OutlinedButton(
                                    onClick = { onUpdateHifz(surah.number, (memorized + 1).coerceAtMost(surah.ayahCount), prefs.dailyHifzTarget) }
                                ) { Text("+1 Ayah") }
                                OutlinedButton(
                                    onClick = { onUpdateHifz(surah.number, surah.ayahCount, prefs.dailyHifzTarget) }
                                ) { Text("Mark Complete") }
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KidsAcademyScreen(
    prefs: UserPreferencesState,
    onOpenShortSurah: (Int) -> Unit,
    onBack: () -> Unit,
) {
    val context = LocalContext.current
    val isBn = prefs.appLanguage == AppLanguage.BENGALI
    var tts by remember { mutableStateOf<TextToSpeech?>(null) }

    DisposableEffect(Unit) {
        var instance: TextToSpeech? = null
        instance = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                instance?.language = Locale("ar")
            }
        }
        tts = instance
        onDispose { instance.stop(); instance.shutdown() }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (isBn) "শিশুদের ইসলামিক একাডেমি" else "Kids Islamic Academy") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
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
            LazyColumn(
                modifier = Modifier
                    .widthIn(max = 840.dp)
                    .fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer),
                        shape = RoundedCornerShape(18.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Icon(Icons.Filled.School, contentDescription = null)
                                Text("Short Surahs for Kids", fontWeight = FontWeight.Bold)
                            }
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                listOf(1 to "Al-Fatihah", 112 to "Al-Ikhlas", 113 to "Al-Falaq", 114 to "An-Nas").forEach { (num, name) ->
                                    FilterChip(selected = false, onClick = { onOpenShortSurah(num) }, label = { Text(name) })
                                }
                            }
                        }
                    }
                }

                items(HidayahExtendedData.kidsLessons, key = { it.id }) { lesson ->
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        shape = RoundedCornerShape(18.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(lesson.category, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.secondary)
                                    Text(if (isBn) lesson.titleBn else lesson.titleEn, fontWeight = FontWeight.Bold)
                                }
                                IconButton(onClick = {
                                    tts?.speak(lesson.arabicSample, TextToSpeech.QUEUE_FLUSH, null, lesson.id)
                                }) {
                                    Icon(Icons.Filled.VolumeUp, contentDescription = "Listen to Arabic")
                                }
                            }
                            Text(
                                lesson.arabicSample,
                                fontFamily = AmiriFontFamily,
                                fontSize = 26.sp,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.fillMaxWidth()
                            )
                            Text(lesson.transliteration, style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)
                            Text(if (isBn) lesson.lessonTextBn else lesson.lessonTextEn, style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                }
            }
        }
    }
}

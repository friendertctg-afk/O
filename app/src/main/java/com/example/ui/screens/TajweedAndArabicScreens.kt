package com.example.ui.screens

import android.speech.tts.TextToSpeech
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.audio.AudioPlayerManager
import com.example.data.local.LessonProgressEntity
import com.example.data.local.UserPreferencesState
import com.example.data.seed.ArabicAlphabetLetter
import com.example.data.seed.QariCatalog
import com.example.data.seed.TajweedAndArabicData
import com.example.localization.AppLanguage
import com.example.ui.theme.AmiriFontFamily
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TajweedScreen(
    prefs: UserPreferencesState,
    lessonProgress: List<LessonProgressEntity>,
    onCompleteLesson: (lessonKey: String, score: Int) -> Unit,
    onBack: () -> Unit,
) {
    val context = LocalContext.current
    val isBn = prefs.appLanguage == AppLanguage.BENGALI
    var selectedTab by remember { mutableIntStateOf(0) }
    var selectedLevel by remember { mutableStateOf("All") }
    val completedKeys = remember(lessonProgress) {
        lessonProgress.filter { it.moduleType == "tajweed" && it.completed }.map { it.lessonKey }.toSet()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (isBn) "তাজউইদ শিক্ষা একাডেমি" else "Tajweed Mastery") },
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
            Column(
                modifier = Modifier
                    .widthIn(max = 840.dp)
                    .fillMaxSize()
            ) {
                PrimaryTabRow(selectedTabIndex = selectedTab) {
                    Tab(selected = selectedTab == 0, onClick = { selectedTab = 0 }, text = { Text("Lessons") })
                    Tab(selected = selectedTab == 1, onClick = { selectedTab = 1 }, text = { Text("Practice Quiz") })
                }

                if (selectedTab == 0) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState())
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf("All", "Beginner", "Intermediate", "Advanced").forEach { lvl ->
                            FilterChip(selected = selectedLevel == lvl, onClick = { selectedLevel = lvl }, label = { Text(lvl) })
                        }
                    }

                    val lessons = remember(selectedLevel) {
                        TajweedAndArabicData.tajweedLessons.filter { selectedLevel == "All" || it.level == selectedLevel }
                    }

                    LazyColumn(
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(lessons, key = { it.id }) { lesson ->
                            val isDone = lesson.id in completedKeys
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
                                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                            Box(
                                                modifier = Modifier
                                                    .size(12.dp)
                                                    .clip(RoundedCornerShape(6.dp))
                                                    .background(Color(lesson.colorHex))
                                            )
                                            Text(lesson.level, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.secondary)
                                        }
                                        if (isDone) {
                                            Icon(Icons.Filled.CheckCircle, contentDescription = "Completed", tint = MaterialTheme.colorScheme.primary)
                                        }
                                    }

                                    Text(if (isBn) lesson.titleBn else lesson.titleEn, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                                    Text(lesson.arabicTitle, fontFamily = AmiriFontFamily, fontSize = 20.sp, color = MaterialTheme.colorScheme.primary)
                                    Text(if (isBn) lesson.summaryBn else lesson.summaryEn, style = MaterialTheme.typography.bodyMedium)

                                    (if (isBn) lesson.ruleDetailsBn else lesson.ruleDetailsEn).forEach { bullet ->
                                        Text("• $bullet", style = MaterialTheme.typography.bodySmall)
                                    }

                                    Surface(
                                        color = MaterialTheme.colorScheme.surfaceVariant,
                                        shape = RoundedCornerShape(12.dp),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(12.dp),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Column(modifier = Modifier.weight(1f)) {
                                                Text(
                                                    lesson.exampleArabic,
                                                    fontFamily = AmiriFontFamily,
                                                    fontSize = 22.sp,
                                                    textAlign = TextAlign.End,
                                                    modifier = Modifier.fillMaxWidth()
                                                )
                                                Text(lesson.exampleReference, style = MaterialTheme.typography.labelSmall)
                                            }
                                            IconButton(onClick = {
                                                val qari = QariCatalog.findById(prefs.selectedQariId)
                                                val surahGuess = if (lesson.exampleAudioGlobalAyah <= 7) 1 else 112
                                                val ayahGuess = if (surahGuess == 1) lesson.exampleAudioGlobalAyah else 1
                                                AudioPlayerManager.playAyah(context, surahGuess, ayahGuess, qari)
                                            }) {
                                                Icon(Icons.Filled.PlayArrow, contentDescription = "Play Tajweed Example")
                                            }
                                        }
                                    }

                                    OutlinedButton(
                                        onClick = { onCompleteLesson(lesson.id, 100) },
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Text(if (isDone) "Reviewed ✓" else "Mark Lesson Completed")
                                    }
                                }
                            }
                        }
                    }
                } else {
                    TajweedQuizContent(
                        isBengali = isBn,
                        onSaveScore = { score -> onCompleteLesson("tajweed_quiz", score) }
                    )
                }
            }
        }
    }
}

@Composable
private fun TajweedQuizContent(
    isBengali: Boolean,
    onSaveScore: (Int) -> Unit,
) {
    var qIdx by remember { mutableIntStateOf(0) }
    var selectedOpt by remember { mutableIntStateOf(-1) }
    var score by remember { mutableIntStateOf(0) }
    var finished by remember { mutableStateOf(false) }

    val questions = TajweedAndArabicData.tajweedQuiz
    if (finished) {
        val pct = (score * 100) / questions.size
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text("Quiz Complete!", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(8.dp))
            Text("Score: $score / ${questions.size} ($pct%)", style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.primary)
            Spacer(Modifier.height(16.dp))
            Button(onClick = {
                qIdx = 0
                selectedOpt = -1
                score = 0
                finished = false
            }) { Text("Retake Quiz") }
        }
        return
    }

    val q = questions[qIdx]
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Question ${qIdx + 1} of ${questions.size}", style = MaterialTheme.typography.labelLarge)
        Text(if (isBengali) q.questionBn else q.questionEn, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        Surface(
            color = MaterialTheme.colorScheme.primaryContainer,
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                q.arabicSample,
                fontFamily = AmiriFontFamily,
                fontSize = 28.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(14.dp)
            )
        }
        q.options.forEachIndexed { idx, opt ->
            val picked = selectedOpt == idx
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(enabled = selectedOpt == -1) {
                        selectedOpt = idx
                        if (idx == q.correctIndex) score++
                    },
                colors = CardDefaults.cardColors(
                    containerColor = when {
                        selectedOpt == -1 -> MaterialTheme.colorScheme.surface
                        idx == q.correctIndex -> MaterialTheme.colorScheme.primaryContainer
                        picked -> MaterialTheme.colorScheme.errorContainer
                        else -> MaterialTheme.colorScheme.surface
                    }
                )
            ) {
                Text(opt, modifier = Modifier.padding(16.dp), fontWeight = FontWeight.Medium)
            }
        }
        if (selectedOpt != -1) {
            Text(q.explanationEn, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.primary)
            Button(
                onClick = {
                    if (qIdx + 1 < questions.size) {
                        qIdx++
                        selectedOpt = -1
                    } else {
                        finished = true
                        onSaveScore((score * 100) / questions.size)
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) { Text(if (qIdx + 1 < questions.size) "Next Question" else "Finish Quiz") }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ArabicAcademyScreen(
    prefs: UserPreferencesState,
    lessonProgress: List<LessonProgressEntity>,
    onCompleteLesson: (lessonKey: String, score: Int) -> Unit,
    onBack: () -> Unit,
) {
    val context = LocalContext.current
    val isBn = prefs.appLanguage == AppLanguage.BENGALI
    var selectedTab by remember { mutableIntStateOf(0) }
    var selectedLetter by remember { mutableStateOf<ArabicAlphabetLetter?>(null) }
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

    val completedKeys = remember(lessonProgress) {
        lessonProgress.filter { it.moduleType == "arabic" && it.completed }.map { it.lessonKey }.toSet()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (isBn) "আরবি শিক্ষা একাডেমি" else "Arabic Learning Academy") },
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
            Column(
                modifier = Modifier
                    .widthIn(max = 840.dp)
                    .fillMaxSize()
            ) {
                PrimaryTabRow(selectedTabIndex = selectedTab) {
                    Tab(selected = selectedTab == 0, onClick = { selectedTab = 0 }, text = { Text("28 Letters") })
                    Tab(selected = selectedTab == 1, onClick = { selectedTab = 1 }, text = { Text("Grammar & Lessons") })
                }

                if (selectedTab == 0) {
                    LazyColumn(
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        selectedLetter?.let { letter ->
                            item {
                                Card(
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                                    shape = RoundedCornerShape(18.dp)
                                ) {
                                    Column(
                                        modifier = Modifier.padding(16.dp),
                                        verticalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                "${letter.order}. ${letter.nameEn} (${letter.nameBn})",
                                                style = MaterialTheme.typography.titleMedium,
                                                fontWeight = FontWeight.Bold
                                            )
                                            IconButton(onClick = {
                                                tts?.speak(letter.exampleWord, TextToSpeech.QUEUE_FLUSH, null, letter.nameEn)
                                            }) {
                                                Icon(Icons.Filled.VolumeUp, contentDescription = "Pronounce Arabic")
                                            }
                                        }
                                        Text("Forms: Isolated ${letter.isolated} | Initial ${letter.initial} | Medial ${letter.medial} | Final ${letter.finalForm}", fontFamily = AmiriFontFamily, fontSize = 22.sp)
                                        Text("Pronunciation: ${letter.pronunciation} • Makhraj: ${letter.makhraj}")
                                        Text("Quranic Word: ${letter.exampleWord} (${letter.exampleMeaning})", fontWeight = FontWeight.SemiBold)
                                    }
                                }
                            }
                        }

                        items(TajweedAndArabicData.arabicAlphabet, key = { it.order }) { letter ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        selectedLetter = letter
                                        tts?.speak(letter.isolated, TextToSpeech.QUEUE_FLUSH, null, letter.nameEn)
                                    },
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                shape = RoundedCornerShape(14.dp)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(14.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        letter.isolated,
                                        fontFamily = AmiriFontFamily,
                                        fontSize = 32.sp,
                                        color = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.width(56.dp),
                                        textAlign = TextAlign.Center
                                    )
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text("${letter.nameEn} • ${letter.nameBn}", fontWeight = FontWeight.Bold)
                                        Text("${letter.pronunciation} • ${letter.makhraj}", style = MaterialTheme.typography.bodySmall)
                                    }
                                    Text(letter.exampleWord, fontFamily = AmiriFontFamily, fontSize = 20.sp)
                                }
                            }
                        }
                    }
                } else {
                    LazyColumn(
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(TajweedAndArabicData.arabicLessons, key = { it.id }) { lesson ->
                            val isDone = lesson.id in completedKeys
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
                                        Text(lesson.level, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.secondary)
                                        if (isDone) Icon(Icons.Filled.CheckCircle, contentDescription = "Completed", tint = MaterialTheme.colorScheme.primary)
                                    }
                                    Text(if (isBn) lesson.titleBn else lesson.titleEn, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                                    Text(if (isBn) lesson.descriptionBn else lesson.descriptionEn, style = MaterialTheme.typography.bodySmall)
                                    lesson.items.forEach { item ->
                                        Surface(
                                            color = MaterialTheme.colorScheme.surfaceVariant,
                                            shape = RoundedCornerShape(10.dp),
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .clickable {
                                                    tts?.speak(item.arabic, TextToSpeech.QUEUE_FLUSH, null, item.transliteration)
                                                }
                                        ) {
                                            Row(
                                                modifier = Modifier.padding(10.dp),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Column(modifier = Modifier.weight(1f)) {
                                                    Text(item.arabic, fontFamily = AmiriFontFamily, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                                                    Text("${item.transliteration} — ${if (isBn) item.meaningBn else item.meaningEn}", style = MaterialTheme.typography.bodySmall)
                                                }
                                                Icon(Icons.Filled.VolumeUp, contentDescription = "Speak Arabic", modifier = Modifier.size(20.dp))
                                            }
                                        }
                                    }
                                    OutlinedButton(
                                        onClick = { onCompleteLesson(lesson.id, 100) },
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Text(if (isDone) "Lesson Completed ✓" else "Mark Completed")
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

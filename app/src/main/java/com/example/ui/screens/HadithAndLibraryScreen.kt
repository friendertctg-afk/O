package com.example.ui.screens

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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryScrollableTabRow
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.UserPreferencesState
import com.example.data.seed.HidayahExtendedData
import com.example.data.seed.IslamicLibraryData
import com.example.localization.AppLanguage
import com.example.ui.theme.AmiriFontFamily
import com.example.utils.WallpaperAndAlertUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HadithAndLibraryScreen(
    prefs: UserPreferencesState,
    onBack: () -> Unit,
) {
    val context = LocalContext.current
    val isBn = prefs.appLanguage == AppLanguage.BENGALI
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("Hadith (9)", "Tafsir", "Stories", "Ramadan & Hajj", "Digital Library", "Quiz & Terms")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Hadith, Tafsir & Islamic Library") },
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
                PrimaryScrollableTabRow(selectedTabIndex = selectedTab) {
                    tabs.forEachIndexed { idx, title ->
                        Tab(selected = selectedTab == idx, onClick = { selectedTab = idx }, text = { Text(title) })
                    }
                }

                when (selectedTab) {
                    // Tab 0: 9 Hadith Collections
                    0 -> {
                        var selectedCol by remember { mutableStateOf("All") }
                        val list = remember(selectedCol) {
                            HidayahExtendedData.allHadiths.filter { selectedCol == "All" || it.collection == selectedCol }
                        }
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState())
                                .padding(horizontal = 16.dp, vertical = 8.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            (listOf("All") + HidayahExtendedData.allHadithCollections).forEach { col ->
                                FilterChip(selected = selectedCol == col, onClick = { selectedCol = col }, label = { Text(col) })
                            }
                        }
                        LazyColumn(
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(list, key = { it.id }) { h ->
                                Card(
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                    shape = RoundedCornerShape(18.dp)
                                ) {
                                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                            Text("${h.collection} • ${h.number}", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)
                                            IconButton(onClick = {
                                                WallpaperAndAlertUtils.shareText(context, h.collection, "${h.arabicText}\n\n${h.englishText}\n(${h.collection}, ${h.number})")
                                            }) { Icon(Icons.Filled.Share, contentDescription = "Share Hadith") }
                                        }
                                        Text(h.arabicText, fontFamily = AmiriFontFamily, fontSize = 21.sp, textAlign = TextAlign.End, modifier = Modifier.fillMaxWidth())
                                        Text(h.englishText, style = MaterialTheme.typography.bodyMedium)
                                        Text("বাংলা: ${h.bengaliText}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary)
                                        Text("اردو: ${h.urduText}", style = MaterialTheme.typography.bodySmall)
                                        Text("Narrator: ${h.narrator} • Grade: ${h.grade}", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.secondary)
                                    }
                                }
                            }
                        }
                    }

                    // Tab 1: Tafsir
                    1 -> {
                        var selectedSource by remember { mutableStateOf("All") }
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState())
                                .padding(horizontal = 16.dp, vertical = 8.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            (listOf("All") + HidayahExtendedData.tafsirSources).forEach { src ->
                                FilterChip(selected = selectedSource == src, onClick = { selectedSource = src }, label = { Text(src) })
                            }
                        }
                        LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            items(HidayahExtendedData.tafsirs.filter { selectedSource == "All" || it.source == selectedSource }, key = { it.id }) { t ->
                                Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface), shape = RoundedCornerShape(18.dp)) {
                                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                        Text(t.source, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.secondary)
                                        Text(t.titleEn, fontWeight = FontWeight.Bold)
                                        Text(t.arabicAyah, fontFamily = AmiriFontFamily, fontSize = 22.sp, textAlign = TextAlign.End, modifier = Modifier.fillMaxWidth())
                                        Text(if (isBn) t.summaryBn else t.summaryEn, style = MaterialTheme.typography.bodyMedium)
                                    }
                                }
                            }
                        }
                    }

                    // Tab 2: Stories
                    2 -> {
                        var selectedCat by remember { mutableStateOf("All") }
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState())
                                .padding(horizontal = 16.dp, vertical = 8.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            (listOf("All") + IslamicLibraryData.storyCategories).forEach { cat ->
                                FilterChip(selected = selectedCat == cat, onClick = { selectedCat = cat }, label = { Text(cat) })
                            }
                        }
                        LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            items(IslamicLibraryData.stories.filter { selectedCat == "All" || it.category == selectedCat }, key = { it.id }) { s ->
                                val title = when (prefs.appLanguage) {
                                    AppLanguage.BENGALI -> s.titleBn
                                    AppLanguage.ARABIC -> s.titleAr
                                    AppLanguage.URDU -> s.titleUr
                                    else -> s.titleEn
                                }
                                val body = when (prefs.appLanguage) {
                                    AppLanguage.BENGALI -> s.summaryBn
                                    AppLanguage.ARABIC -> s.summaryAr
                                    AppLanguage.URDU -> s.summaryUr
                                    else -> s.summaryEn
                                }
                                Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface), shape = RoundedCornerShape(18.dp)) {
                                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                        Text(s.category, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.secondary)
                                        Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                                        Text(body, style = MaterialTheme.typography.bodyMedium)
                                        Text("Source: ${s.sourceRef}", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary)
                                    }
                                }
                            }
                        }
                    }

                    // Tab 3: Ramadan & Hajj/Umrah
                    3 -> {
                        LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            item {
                                Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer), shape = RoundedCornerShape(18.dp)) {
                                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                        Text("Ramadan Worship Essentials", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                                        Text("• Obligatory (Fard): Dawn-to-sunset fasting with sincere Niyyah (Surah Al-Baqarah 2:183-185) & Zakat al-Fitr before Eid prayer.")
                                        Text("• Recommended (Sunnah): Suhoor pre-dawn meal, hastening Iftar with dates/water, Taraweeh Qiyam, I'tikaf & seeking Laylatul Qadr in the last 10 odd nights.")
                                        Text("• Dua at Iftar: ذَهَبَ الظَّمَأُ وَابْتَلَّتِ الْعُرُوقُ وَثَبَتَ الأَجْرُ إِنْ شَاءَ اللَّهُ", fontFamily = AmiriFontFamily, fontSize = 18.sp)
                                    }
                                }
                            }
                            item {
                                Text("Hajj & Umrah Step-by-Step Guide", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                            }
                            items(IslamicLibraryData.hajjSteps, key = { it.stepNumber }) { step ->
                                Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface), shape = RoundedCornerShape(16.dp)) {
                                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                        Text("Step ${step.stepNumber}: ${if (isBn) step.titleBn else step.titleEn}", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                                        Text(step.arabicDua, fontFamily = AmiriFontFamily, fontSize = 20.sp, textAlign = TextAlign.End, modifier = Modifier.fillMaxWidth())
                                        Text(if (isBn) step.descriptionBn else step.descriptionEn, style = MaterialTheme.typography.bodyMedium)
                                    }
                                }
                            }
                        }
                    }

                    // Tab 4: Digital Library
                    4 -> {
                        LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            items(HidayahExtendedData.digitalLibraryItems, key = { it.id }) { doc ->
                                Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface), shape = RoundedCornerShape(18.dp)) {
                                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                        Text("${doc.category} • ${doc.pagesOrDuration}", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.secondary)
                                        Text(doc.titleEn, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                                        Text("By ${doc.author} (${doc.language})", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary)
                                        Text(doc.summaryEn, style = MaterialTheme.typography.bodyMedium)
                                        doc.keyTakeaways.forEach { t ->
                                            Text("• $t", style = MaterialTheme.typography.bodySmall)
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // Tab 5: Quiz & Glossary
                    else -> {
                        var quizIdx by remember { mutableIntStateOf(0) }
                        var pickedOpt by remember { mutableIntStateOf(-1) }
                        val quiz = IslamicLibraryData.islamicQuiz
                        val currentQ = quiz[quizIdx % quiz.size]

                        LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            item {
                                Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer), shape = RoundedCornerShape(18.dp)) {
                                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                        Text("Islamic Knowledge Quiz (${(quizIdx % quiz.size) + 1}/${quiz.size})", fontWeight = FontWeight.Bold)
                                        Text(currentQ.question, style = MaterialTheme.typography.titleMedium)
                                        currentQ.options.forEachIndexed { idx, opt ->
                                            Surface(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .clickable(enabled = pickedOpt == -1) { pickedOpt = idx },
                                                shape = RoundedCornerShape(10.dp),
                                                color = when {
                                                    pickedOpt == -1 -> MaterialTheme.colorScheme.surface
                                                    idx == currentQ.correctIndex -> Color(0xFFC8E6C9)
                                                    pickedOpt == idx -> Color(0xFFFFCDD2)
                                                    else -> MaterialTheme.colorScheme.surface
                                                }
                                            ) {
                                                Text(opt, modifier = Modifier.padding(12.dp))
                                            }
                                        }
                                        if (pickedOpt != -1) {
                                            Text("Reference: ${currentQ.reference}", style = MaterialTheme.typography.bodySmall)
                                            Button(onClick = {
                                                quizIdx++
                                                pickedOpt = -1
                                            }) { Text("Next Question") }
                                        }
                                    }
                                }
                            }
                            item { Text("Islamic Glossary", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold) }
                            items(IslamicLibraryData.glossaryTerms, key = { it.term }) { g ->
                                Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface), shape = RoundedCornerShape(14.dp)) {
                                    Column(modifier = Modifier.padding(14.dp)) {
                                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                            Text(g.term, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                                            Text(g.arabic, fontFamily = AmiriFontFamily, fontSize = 20.sp)
                                        }
                                        Text(if (isBn) g.definitionBn else g.definitionEn, style = MaterialTheme.typography.bodySmall)
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

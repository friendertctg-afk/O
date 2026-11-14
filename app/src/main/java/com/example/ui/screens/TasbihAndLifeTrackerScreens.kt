package com.example.ui.screens

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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
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
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.TasbihSessionEntity
import com.example.data.local.UserPreferencesState
import com.example.data.seed.HidayahExtendedData
import com.example.localization.AppLanguage
import com.example.ui.theme.AmiriFontFamily

private data class DhikrPreset(val title: String, val arabic: String, val defaultTarget: Int)

private val defaultDhikrPresets = listOf(
    DhikrPreset("SubhanAllah", "سُبْحَانَ اللَّهِ", 33),
    DhikrPreset("Alhamdulillah", "الْحَمْدُ لِلَّهِ", 33),
    DhikrPreset("Allahu Akbar", "اللَّهُ أَكْبَرُ", 34),
    DhikrPreset("Astaghfirullah", "أَسْتَغْفِرُ اللَّهَ", 100),
    DhikrPreset("La ilaha illallah", "لَا إِلَهَ إِلَّا اللَّهُ", 100),
    DhikrPreset("Salawat", "اللَّهُمَّ صَلِّ عَلَى مُحَمَّدٍ", 100),
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TasbihScreen(
    prefs: UserPreferencesState,
    sessions: List<TasbihSessionEntity>,
    onIncrementTotal: (Long) -> Unit,
    onSaveSession: (String, String, Int, Int) -> Unit,
    onDeleteSession: (Long) -> Unit,
    onSetTasbihTheme: (Int) -> Unit,
    onBack: () -> Unit,
) {
    val haptics = LocalHapticFeedback.current
    var presets by remember { mutableStateOf(defaultDhikrPresets) }
    var selectedPreset by remember { mutableStateOf(defaultDhikrPresets.first()) }
    var count by remember { mutableIntStateOf(0) }
    var target by remember { mutableIntStateOf(33) }
    var showCustomDialog by remember { mutableStateOf(false) }

    val beadGradients = listOf(
        listOf(Color(0xFF0E4D3B), Color(0xFF06231B)),
        listOf(Color(0xFF23252C), Color(0xFF0D0E12)),
        listOf(Color(0xFF4D2473), Color(0xFF250F3B)),
        listOf(Color(0xFF104B7A), Color(0xFF06243D)),
    )
    val themeNames = listOf("Emerald", "Royal Black", "Amethyst", "Ocean")
    val activeGrad = beadGradients[prefs.tasbihThemeIndex.coerceIn(0, beadGradients.lastIndex)]

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Digital Tasbih") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { showCustomDialog = true }) {
                        Icon(Icons.Filled.Add, contentDescription = "Custom Dhikr")
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
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        presets.forEach { preset ->
                            FilterChip(
                                selected = selectedPreset == preset,
                                onClick = {
                                    selectedPreset = preset
                                    target = preset.defaultTarget
                                    count = 0
                                },
                                label = { Text(preset.title) }
                            )
                        }
                    }
                }

                item {
                    Card(
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(20.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Text(
                                selectedPreset.arabic,
                                fontFamily = AmiriFontFamily,
                                fontSize = 30.sp,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(selectedPreset.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                            Text(
                                "Lifetime Dhikr: ${prefs.totalTasbihCount}",
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.secondary
                            )

                            Box(
                                modifier = Modifier
                                    .size(196.dp)
                                    .clip(CircleShape)
                                    .background(Brush.radialGradient(activeGrad))
                                    .testTag("tasbih_tap_button")
                                    .clickable {
                                        count++
                                        onIncrementTotal(1L)
                                        if (count % target == 0) {
                                            haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                                        } else {
                                            haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                        }
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        "$count",
                                        fontSize = 48.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFFE7C563)
                                    )
                                    Text(
                                        "/ $target TAP",
                                        style = MaterialTheme.typography.labelLarge,
                                        color = Color.White.copy(alpha = 0.85f)
                                    )
                                }
                            }

                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                listOf(33, 99, 100, 1000).forEach { t ->
                                    FilterChip(
                                        selected = target == t,
                                        onClick = { target = t },
                                        label = { Text("$t") }
                                    )
                                }
                            }

                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                themeNames.forEachIndexed { idx, name ->
                                    FilterChip(
                                        selected = prefs.tasbihThemeIndex == idx,
                                        onClick = { onSetTasbihTheme(idx) },
                                        label = { Text(name) }
                                    )
                                }
                            }

                            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                OutlinedButton(onClick = { count = 0 }) {
                                    Icon(Icons.Filled.Refresh, contentDescription = null)
                                    Spacer(Modifier.width(6.dp))
                                    Text("Reset")
                                }
                                Button(
                                    onClick = {
                                        onSaveSession(selectedPreset.title, selectedPreset.arabic, count, target)
                                        count = 0
                                    },
                                    enabled = count > 0
                                ) {
                                    Icon(Icons.Filled.Save, contentDescription = null)
                                    Spacer(Modifier.width(6.dp))
                                    Text("Save Session")
                                }
                            }
                        }
                    }
                }

                if (sessions.isNotEmpty()) {
                    item {
                        Text("Recent Tasbih Sessions", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    }
                    items(sessions, key = { it.id }) { s ->
                        Card(
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            shape = RoundedCornerShape(14.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text("${s.dhikrTitle} (${s.dhikrArabic})", fontWeight = FontWeight.Bold)
                                    Text("Count: ${s.count} / Target ${s.target}", style = MaterialTheme.typography.bodySmall)
                                }
                                IconButton(onClick = { onDeleteSession(s.id) }) {
                                    Icon(Icons.Filled.Delete, contentDescription = "Delete session")
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (showCustomDialog) {
        var title by remember { mutableStateOf("") }
        var arabic by remember { mutableStateOf("") }
        AlertDialog(
            onDismissRequest = { showCustomDialog = false },
            title = { Text("Add Custom Dhikr") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Title (e.g. SubhanAllahi wa bihamdihi)") })
                    OutlinedTextField(value = arabic, onValueChange = { arabic = it }, label = { Text("Arabic (optional)") })
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (title.isNotBlank()) {
                            val item = DhikrPreset(title.trim(), arabic.trim().ifBlank { title.trim() }, 100)
                            presets = presets + item
                            selectedPreset = item
                            count = 0
                            target = 100
                            showCustomDialog = false
                        }
                    }
                ) { Text("Add") }
            },
            dismissButton = { TextButton(onClick = { showCustomDialog = false }) { Text("Cancel") } }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DailyLifeTrackerScreen(
    prefs: UserPreferencesState,
    onTogglePrayer: (String) -> Unit,
    onToggleHabit: (String) -> Unit,
    onAdjustQada: (Int) -> Unit,
    onIncrementFasting: (Int) -> Unit,
    onIncrementKhatam: (Int) -> Unit,
    onIncrementCharity: () -> Unit,
    onBack: () -> Unit,
) {
    val isBn = prefs.appLanguage == AppLanguage.BENGALI
    val prayers = listOf("fajr" to "Fajr", "dhuhr" to "Dhuhr", "asr" to "Asr", "maghrib" to "Maghrib", "isha" to "Isha")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Muslim Daily Life Tracker") },
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
                // 5 Daily Prayers
                item {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        shape = RoundedCornerShape(18.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                "Today's 5 Obligatory Prayers (${prefs.completedDailyPrayers.size}/5)",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            LinearProgressIndicator(
                                progress = { (prefs.completedDailyPrayers.size / 5f).coerceIn(0f, 1f) },
                                modifier = Modifier.fillMaxWidth().height(7.dp).clip(RoundedCornerShape(4.dp))
                            )
                            prayers.forEach { (key, label) ->
                                val checked = key in prefs.completedDailyPrayers
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { onTogglePrayer(key) },
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Checkbox(checked = checked, onCheckedChange = { onTogglePrayer(key) })
                                    Text(label, fontWeight = if (checked) FontWeight.Bold else FontWeight.Normal)
                                }
                            }
                        }
                    }
                }

                // Qada, Fasting, Khatam & Charity counters
                item {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        shape = RoundedCornerShape(18.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Text("Worship Milestones", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)

                            TrackerCounterRow(
                                title = "Qada (Missed) Prayers Remaining",
                                valueText = "${prefs.qadaPrayersRemaining} prayers",
                                onMinus = { onAdjustQada(-1) },
                                onPlus = { onAdjustQada(1) }
                            )
                            TrackerCounterRow(
                                title = "Fasting Days Completed",
                                valueText = "${prefs.fastingDaysCompleted} days",
                                onMinus = { onIncrementFasting(-1) },
                                onPlus = { onIncrementFasting(1) }
                            )
                            TrackerCounterRow(
                                title = "Quran Khatam Progress (604 Pages)",
                                valueText = "${prefs.khatamPagesRead} / 604 pages",
                                onMinus = { onIncrementKhatam(-1) },
                                onPlus = { onIncrementKhatam(2) }
                            )

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text("Charity / Sadaqah Logged", fontWeight = FontWeight.SemiBold)
                                    Text("${prefs.charityTotalCount} acts recorded", style = MaterialTheme.typography.bodySmall)
                                }
                                Button(onClick = onIncrementCharity) { Text("+1 Sadaqah") }
                            }
                        }
                    }
                }

                // Sunnah Habits
                item {
                    Text("Daily Sunnah Habits", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                }
                items(HidayahExtendedData.sunnahHabits, key = { it.id }) { habit ->
                    val done = habit.id in prefs.completedSunnahHabits
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onToggleHabit(habit.id) },
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Checkbox(checked = done, onCheckedChange = { onToggleHabit(habit.id) })
                            Column(modifier = Modifier.weight(1f)) {
                                Text(if (isBn) habit.titleBn else habit.titleEn, fontWeight = FontWeight.SemiBold)
                            }
                            Text(habit.arabicTag, fontFamily = AmiriFontFamily, fontSize = 18.sp, color = MaterialTheme.colorScheme.primary)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun TrackerCounterRow(
    title: String,
    valueText: String,
    onMinus: () -> Unit,
    onPlus: () -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(title, fontWeight = FontWeight.SemiBold)
            Text(valueText, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary)
        }
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            OutlinedButton(onClick = onMinus) { Text("-1") }
            Button(onClick = onPlus) { Text("+1") }
        }
    }
}

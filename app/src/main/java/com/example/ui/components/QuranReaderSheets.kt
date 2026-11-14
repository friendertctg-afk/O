package com.example.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.audio.AudioPlaybackState
import com.example.audio.AudioPlayerManager
import com.example.data.local.MushafEdition
import com.example.data.local.QuranLayoutStyle
import com.example.data.local.QuranScriptType
import com.example.data.local.UserPreferencesState
import com.example.data.repository.AyahDisplayItem
import com.example.data.seed.QariCatalog
import com.example.data.seed.QuranMetadata
import com.example.ui.theme.ArabicFontChoice
import com.example.ui.theme.QuranReadingMode
import com.example.ui.theme.toFontFamily

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReaderDisplaySettingsSheet(
    prefs: UserPreferencesState,
    onDismiss: () -> Unit,
    onSetMushafEdition: (MushafEdition) -> Unit,
    onSetScript: (QuranScriptType) -> Unit,
    onSetLayout: (QuranLayoutStyle) -> Unit,
    onSetTwoPageSpread: (Int) -> Unit,
    onSetArabicFont: (ArabicFontChoice) -> Unit,
    onSetArabicFontSize: (Float) -> Unit,
    onSetPageMarginDp: (Float) -> Unit,
    onSetReadingMode: (QuranReadingMode) -> Unit,
    onSetBrightness: (Float) -> Unit,
    onSetKeepScreenAwake: (Boolean) -> Unit,
    onSetLockPortrait: (Boolean) -> Unit,
    onSetTranslationEdition: (String) -> Unit,
    onSetShowTranslation: (Boolean) -> Unit,
    onSetShowTransliteration: (Boolean) -> Unit,
) {
    ModalBottomSheet(onDismissRequest = onDismiss) {
        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 18.dp, vertical = 10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    "Mushaf & Reading Controls",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Text("Mushaf Edition", style = MaterialTheme.typography.labelLarge)
                Row(
                    modifier = Modifier.horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    MushafEdition.entries.forEach { ed ->
                        FilterChip(
                            selected = prefs.mushafEdition == ed,
                            onClick = { onSetMushafEdition(ed) },
                            label = { Text(ed.label) }
                        )
                    }
                }

                Text("Reading Layout", style = MaterialTheme.typography.labelLarge)
                Row(
                    modifier = Modifier.horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    QuranLayoutStyle.entries.forEach { l ->
                        FilterChip(
                            selected = prefs.quranLayout == l,
                            onClick = { onSetLayout(l) },
                            label = { Text(l.label) }
                        )
                    }
                }

                Text("Two-Page Spread (Mushaf Mode)", style = MaterialTheme.typography.labelLarge)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf(0 to "Auto (Tablet)", 1 to "Single Page", 2 to "Two-Page").forEach { (code, lbl) ->
                        FilterChip(
                            selected = prefs.twoPageSpreadPref == code,
                            onClick = { onSetTwoPageSpread(code) },
                            label = { Text(lbl) }
                        )
                    }
                }

                Text("Arabic Script & Reading Theme", style = MaterialTheme.typography.labelLarge)
                Row(
                    modifier = Modifier.horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    QuranScriptType.entries.forEach { s ->
                        FilterChip(selected = prefs.quranScript == s, onClick = { onSetScript(s) }, label = { Text(s.label) })
                    }
                }
                Row(
                    modifier = Modifier.horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    QuranReadingMode.entries.forEach { m ->
                        FilterChip(selected = prefs.readingMode == m, onClick = { onSetReadingMode(m) }, label = { Text(m.displayName) })
                    }
                }

                Text("Arabic Font & Size (${prefs.arabicFontSizeSp.toInt()} sp)", style = MaterialTheme.typography.labelLarge)
                Row(
                    modifier = Modifier.horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    ArabicFontChoice.entries.forEach { f ->
                        FilterChip(selected = prefs.arabicFont == f, onClick = { onSetArabicFont(f) }, label = { Text(f.displayName) })
                    }
                }
                Slider(
                    value = prefs.arabicFontSizeSp,
                    onValueChange = onSetArabicFontSize,
                    valueRange = 18f..44f
                )

                Text("Page Margins (${prefs.readerPageMarginDp.toInt()} dp)", style = MaterialTheme.typography.labelLarge)
                Slider(
                    value = prefs.readerPageMarginDp,
                    onValueChange = onSetPageMarginDp,
                    valueRange = 4f..32f
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        if (prefs.readerBrightness < 0f) "Brightness: System Auto" else "Brightness: ${(prefs.readerBrightness * 100).toInt()}%",
                        style = MaterialTheme.typography.labelLarge
                    )
                    if (prefs.readerBrightness >= 0f) {
                        TextButton(onClick = { onSetBrightness(-1f) }) { Text("Reset Auto") }
                    }
                }
                Slider(
                    value = if (prefs.readerBrightness < 0f) 0.65f else prefs.readerBrightness,
                    onValueChange = onSetBrightness,
                    valueRange = 0.08f..1.0f
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Keep Screen Awake While Reading")
                    Switch(checked = prefs.keepScreenAwake, onCheckedChange = onSetKeepScreenAwake)
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Lock Portrait Orientation")
                    Switch(checked = prefs.lockPortraitOrientation, onCheckedChange = onSetLockPortrait)
                }

                Text("Translation Language", style = MaterialTheme.typography.labelLarge)
                Row(
                    modifier = Modifier.horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    QuranMetadata.translationEditions.forEach { (code, lbl) ->
                        FilterChip(
                            selected = prefs.translationEdition == code,
                            onClick = { onSetTranslationEdition(code) },
                            label = { Text(lbl) }
                        )
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Show Translation")
                    Switch(checked = prefs.showTranslation, onCheckedChange = onSetShowTranslation)
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Show Transliteration")
                    Switch(checked = prefs.showTransliteration, onCheckedChange = onSetShowTransliteration)
                }
                Spacer(Modifier.height(18.dp))
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QariAndAudioSheet(
    surahNumber: Int,
    prefs: UserPreferencesState,
    audioState: AudioPlaybackState,
    favoriteQariIds: List<String>,
    onDismiss: () -> Unit,
    onSelectQari: (String) -> Unit,
    onToggleFavoriteQari: (String) -> Unit,
) {
    val context = LocalContext.current
    val surahMeta = QuranMetadata.getSurah(surahNumber)
    var qariQuery by remember { mutableStateOf("") }
    val filteredQaris = remember(qariQuery, favoriteQariIds) {
        QariCatalog.qaris.filter {
            qariQuery.isBlank() ||
                it.name.contains(qariQuery, ignoreCase = true) ||
                it.country.contains(qariQuery, ignoreCase = true) ||
                it.arabicName.contains(qariQuery)
        }.sortedByDescending { it.id in favoriteQariIds }
    }
    var loopStart by remember { mutableStateOf((audioState.hifzLoopStart ?: 1).toString()) }
    var loopEnd by remember { mutableStateOf((audioState.hifzLoopEnd ?: surahMeta.ayahCount.coerceAtMost(5)).toString()) }

    ModalBottomSheet(onDismissRequest = onDismiss) {
        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 18.dp, vertical = 10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    "Quran Audio & Qari Library (20 Verified Reciters)",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Text("Playback Speed (${audioState.playbackSpeed}x)", style = MaterialTheme.typography.labelLarge)
                Row(
                    modifier = Modifier.horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf(0.75f, 1.0f, 1.25f, 1.5f, 2.0f).forEach { spd ->
                        FilterChip(
                            selected = audioState.playbackSpeed == spd,
                            onClick = { AudioPlayerManager.setSpeed(spd) },
                            label = { Text("${spd}x") }
                        )
                    }
                }

                Text("Ayah Repeat & Sleep Timer", style = MaterialTheme.typography.labelLarge)
                Row(
                    modifier = Modifier.horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf(1 to "1x", 3 to "3x", 5 to "5x", 0 to "∞ Loop").forEach { (count, label) ->
                        FilterChip(
                            selected = audioState.ayahRepeatTarget == count,
                            onClick = { AudioPlayerManager.setAyahRepeat(count) },
                            label = { Text("Ayah $label") }
                        )
                    }
                    FilterChip(
                        selected = audioState.repeatSurah,
                        onClick = { AudioPlayerManager.setRepeatSurah(!audioState.repeatSurah) },
                        label = { Text("Repeat Surah") }
                    )
                    FilterChip(
                        selected = audioState.autoNextSurah,
                        onClick = { AudioPlayerManager.setAutoNextSurah(!audioState.autoNextSurah) },
                        label = { Text("Auto-Next") }
                    )
                }

                Row(
                    modifier = Modifier.horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf(null to "Timer Off", 10 to "10m", 20 to "20m", 30 to "30m", 60 to "60m").forEach { (min, lbl) ->
                        FilterChip(
                            selected = audioState.sleepTimerMinutes == min,
                            onClick = { AudioPlayerManager.setSleepTimer(min) },
                            label = { Text(lbl) }
                        )
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = loopStart,
                        onValueChange = { loopStart = it },
                        label = { Text("From Ayah") },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = loopEnd,
                        onValueChange = { loopEnd = it },
                        label = { Text("To Ayah") },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                    Button(onClick = {
                        val s = loopStart.toIntOrNull()?.coerceIn(1, surahMeta.ayahCount) ?: 1
                        val e = loopEnd.toIntOrNull()?.coerceIn(s, surahMeta.ayahCount) ?: s
                        AudioPlayerManager.setHifzLoopRange(s, e)
                        AudioPlayerManager.playAyah(context, surahNumber, s, QariCatalog.findById(prefs.selectedQariId))
                        onDismiss()
                    }) { Text("Hifz Loop") }
                }

                OutlinedTextField(
                    value = qariQuery,
                    onValueChange = { qariQuery = it },
                    label = { Text("Search 20 Qaris") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                LazyColumn(
                    modifier = Modifier.height(220.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    items(filteredQaris, key = { it.id }) { qari ->
                        val isFav = qari.id in favoriteQariIds
                        val isSelected = prefs.selectedQariId == qari.id
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .clickable {
                                    onSelectQari(qari.id)
                                    AudioPlayerManager.setQari(qari)
                                },
                            color = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(qari.name, fontWeight = FontWeight.Bold)
                                    Text("${qari.arabicName} • ${qari.style} • ${qari.country}", style = MaterialTheme.typography.bodySmall)
                                }
                                IconButton(onClick = { onToggleFavoriteQari(qari.id) }) {
                                    Icon(
                                        if (isFav) Icons.Filled.Star else Icons.Filled.StarBorder,
                                        contentDescription = "Favorite Qari",
                                        tint = MaterialTheme.colorScheme.secondary
                                    )
                                }
                            }
                        }
                    }
                }
                Spacer(Modifier.height(12.dp))
            }
        }
    }
}

@Composable
fun JumpToMushafDialog(
    currentPage: Int,
    currentSurah: Int,
    totalPages: Int = QuranMetadata.TOTAL_MUSHAF_PAGES,
    onDismiss: () -> Unit,
    onJumpToPage: (Int) -> Unit,
    onJumpToSurahAyah: (Int, Int) -> Unit,
    onJumpToJuz: (Int) -> Unit,
) {
    var mode by remember { mutableIntStateOf(0) } // 0 = Page, 1 = Surah:Ayah, 2 = Juz
    var pageInput by remember { mutableStateOf(currentPage.toString()) }
    var surahInput by remember { mutableStateOf(currentSurah.toString()) }
    var ayahInput by remember { mutableStateOf("1") }
    var juzInput by remember { mutableStateOf(((currentPage - 1) / 20 + 1).coerceIn(1, 30).toString()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Jump to Quran Location", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(
                    modifier = Modifier.horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    FilterChip(selected = mode == 0, onClick = { mode = 0 }, label = { Text("Page (1-$totalPages)") })
                    FilterChip(selected = mode == 1, onClick = { mode = 1 }, label = { Text("Surah : Ayah") })
                    FilterChip(selected = mode == 2, onClick = { mode = 2 }, label = { Text("Juz (1-30)") })
                }

                when (mode) {
                    0 -> OutlinedTextField(
                        value = pageInput,
                        onValueChange = { pageInput = it },
                        label = { Text("Mushaf Page Number (1..$totalPages)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    1 -> Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = surahInput,
                            onValueChange = { surahInput = it },
                            label = { Text("Surah (1..114)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true,
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = ayahInput,
                            onValueChange = { ayahInput = it },
                            label = { Text("Ayah") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true,
                            modifier = Modifier.weight(1f)
                        )
                    }
                    else -> OutlinedTextField(
                        value = juzInput,
                        onValueChange = { juzInput = it },
                        label = { Text("Juz / Para (1..30)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        },
        confirmButton = {
            Button(onClick = {
                when (mode) {
                    0 -> {
                        val p = (pageInput.toIntOrNull() ?: currentPage).coerceIn(1, totalPages)
                        onJumpToPage(p)
                    }
                    1 -> {
                        val s = (surahInput.toIntOrNull() ?: currentSurah).coerceIn(1, 114)
                        val maxA = QuranMetadata.getSurah(s).ayahCount
                        val a = (ayahInput.toIntOrNull() ?: 1).coerceIn(1, maxA)
                        onJumpToSurahAyah(s, a)
                    }
                    else -> {
                        val j = (juzInput.toIntOrNull() ?: 1).coerceIn(1, 30)
                        onJumpToJuz(j)
                    }
                }
                onDismiss()
            }) { Text("Go") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AyahQuickActionSheet(
    ayah: AyahDisplayItem,
    isBookmarked: Boolean,
    prefs: UserPreferencesState,
    onDismiss: () -> Unit,
    onPlayAyah: () -> Unit,
    onBookmark: () -> Unit,
    onTafsir: () -> Unit,
    onCopy: () -> Unit,
    onShare: () -> Unit,
    onAskAi: () -> Unit,
) {
    val sMeta = QuranMetadata.getSurah(ayah.surahNumber)
    ModalBottomSheet(onDismissRequest = onDismiss) {
        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 18.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    "${sMeta.englishName} (${ayah.surahNumber}:${ayah.ayahNumber}) • Page ${ayah.page} • Juz ${ayah.juz}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = ayah.uthmaniText,
                    fontFamily = prefs.arabicFont.toFontFamily(),
                    fontSize = 22.sp,
                    textAlign = TextAlign.End,
                    modifier = Modifier.fillMaxWidth()
                )
                if (ayah.translationText.isNotBlank()) {
                    Text(ayah.translationText, style = MaterialTheme.typography.bodyMedium)
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(onClick = { onPlayAyah(); onDismiss() }) {
                        Icon(Icons.Filled.PlayArrow, contentDescription = null)
                        Text(" Play")
                    }
                    OutlinedButton(onClick = { onBookmark(); onDismiss() }) {
                        Icon(Icons.Filled.Bookmark, contentDescription = null)
                        Text(if (isBookmarked) " Edit Note" else " Bookmark")
                    }
                    OutlinedButton(onClick = { onTafsir(); onDismiss() }) {
                        Icon(Icons.AutoMirrored.Filled.MenuBook, contentDescription = null)
                        Text(" Tafsir")
                    }
                    OutlinedButton(onClick = { onCopy(); onDismiss() }) {
                        Icon(Icons.Filled.ContentCopy, contentDescription = null)
                        Text(" Copy")
                    }
                    OutlinedButton(onClick = { onShare(); onDismiss() }) {
                        Icon(Icons.Filled.Share, contentDescription = null)
                        Text(" Share")
                    }
                    OutlinedButton(onClick = { onAskAi(); onDismiss() }) {
                        Icon(Icons.Filled.Psychology, contentDescription = null)
                        Text(" Ask AI")
                    }
                }
                Spacer(Modifier.height(14.dp))
            }
        }
    }
}

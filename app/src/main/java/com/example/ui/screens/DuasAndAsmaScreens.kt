package com.example.ui.screens

import android.speech.tts.TextToSpeech
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Share
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
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.UserPreferencesState
import com.example.data.seed.AsmaUlHusnaData
import com.example.data.seed.DuaSeedData
import com.example.localization.AppLanguage
import com.example.ui.theme.AmiriFontFamily
import com.example.utils.WallpaperAndAlertUtils
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DuasScreen(
    prefs: UserPreferencesState,
    favoriteIds: List<String>,
    onToggleFavorite: (String) -> Unit,
    onBack: () -> Unit,
) {
    val context = LocalContext.current
    val isBn = prefs.appLanguage == AppLanguage.BENGALI
    var selectedCategory by remember { mutableStateOf("All") }
    var search by remember { mutableStateOf("") }
    val repeatMap = remember { mutableStateMapOf<String, Int>() }
    var tts by remember { mutableStateOf<TextToSpeech?>(null) }

    DisposableEffect(Unit) {
        var inst: TextToSpeech? = null
        inst = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                inst?.language = Locale("ar")
            }
        }
        tts = inst
        onDispose { inst.stop(); inst.shutdown() }
    }

    val filtered = remember(selectedCategory, search, favoriteIds) {
        DuaSeedData.duas.filter { dua ->
            val catOk = selectedCategory == "All" ||
                (selectedCategory == "Favorites" && dua.id in favoriteIds) ||
                dua.category == selectedCategory
            val qOk = search.isBlank() ||
                dua.titleEn.contains(search, ignoreCase = true) ||
                dua.titleBn.contains(search, ignoreCase = true) ||
                dua.englishTranslation.contains(search, ignoreCase = true)
            catOk && qOk
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (isBn) "দোয়া ও আযকার" else "Duas & Azkar (16 Categories)") },
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
                OutlinedTextField(
                    value = search,
                    onValueChange = { search = it },
                    label = { Text("Search Duas & Azkar...") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp)
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState())
                        .padding(horizontal = 16.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    (listOf("All", "Favorites") + DuaSeedData.categories).forEach { cat ->
                        FilterChip(
                            selected = selectedCategory == cat,
                            onClick = { selectedCategory = cat },
                            label = { Text(cat) }
                        )
                    }
                }

                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(filtered, key = { it.id }) { dua ->
                        val isFav = dua.id in favoriteIds
                        val count = repeatMap[dua.id] ?: 0
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
                                        Text(dua.category, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.secondary)
                                        Text(if (isBn) dua.titleBn else dua.titleEn, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                                    }
                                    Row {
                                        IconButton(onClick = { tts?.speak(dua.arabic, TextToSpeech.QUEUE_FLUSH, null, dua.id) }) {
                                            Icon(Icons.Filled.VolumeUp, contentDescription = "Listen Dua")
                                        }
                                        IconButton(onClick = { onToggleFavorite(dua.id) }) {
                                            Icon(
                                                if (isFav) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                                                contentDescription = "Favorite Dua",
                                                tint = MaterialTheme.colorScheme.secondary
                                            )
                                        }
                                        IconButton(onClick = {
                                            WallpaperAndAlertUtils.shareText(
                                                context,
                                                dua.titleEn,
                                                "${dua.arabic}\n\n${dua.englishTranslation}\n(${dua.reference})"
                                            )
                                        }) {
                                            Icon(Icons.Filled.Share, contentDescription = "Share Dua")
                                        }
                                    }
                                }

                                Text(
                                    dua.arabic,
                                    fontFamily = AmiriFontFamily,
                                    fontSize = 23.sp,
                                    lineHeight = 38.sp,
                                    textAlign = TextAlign.End,
                                    modifier = Modifier.fillMaxWidth()
                                )
                                Text("Pronunciation (Bn): ${dua.bengaliPronunciation}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary)
                                Text("Transliteration: ${dua.englishTransliteration}", style = MaterialTheme.typography.bodySmall)
                                Text(if (isBn) dua.bengaliTranslation else dua.englishTranslation, style = MaterialTheme.typography.bodyMedium)

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(dua.reference, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    Button(onClick = { repeatMap[dua.id] = count + 1 }) {
                                        Text("Recite ($count / ${dua.recommendedCount})")
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AsmaUlHusnaScreen(
    prefs: UserPreferencesState,
    favoriteIds: List<String>,
    onToggleFavorite: (String) -> Unit,
    onBack: () -> Unit,
) {
    val context = LocalContext.current
    val isBn = prefs.appLanguage == AppLanguage.BENGALI
    var query by remember { mutableStateOf("") }
    var tts by remember { mutableStateOf<TextToSpeech?>(null) }

    DisposableEffect(Unit) {
        var inst: TextToSpeech? = null
        inst = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) inst?.language = Locale("ar")
        }
        tts = inst
        onDispose { inst.stop(); inst.shutdown() }
    }

    val filtered = remember(query) {
        AsmaUlHusnaData.names.filter {
            query.isBlank() ||
                it.transliteration.contains(query, ignoreCase = true) ||
                it.meaningEn.contains(query, ignoreCase = true) ||
                it.meaningBn.contains(query, ignoreCase = true) ||
                it.number.toString() == query.trim()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("99 Names of Allah (Asma-ul-Husna)") },
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
                OutlinedTextField(
                    value = query,
                    onValueChange = { query = it },
                    label = { Text("Search 99 Names by number or meaning...") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                )

                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(filtered, key = { it.number }) { name ->
                        val isFav = name.number.toString() in favoriteIds
                        Card(
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Surface(
                                    color = MaterialTheme.colorScheme.primaryContainer,
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Text(
                                        "${name.number}",
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                Spacer(Modifier.width(12.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        "${name.transliteration} • ${if (isBn) name.meaningBn else name.meaningEn}",
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(name.bengaliPronunciation, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary)
                                    Text(name.explanation, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                                Column(horizontalAlignment = Alignment.End) {
                                    Text(name.arabic, fontFamily = AmiriFontFamily, fontSize = 24.sp, color = MaterialTheme.colorScheme.primary)
                                    Row {
                                        IconButton(onClick = { tts?.speak(name.arabic, TextToSpeech.QUEUE_FLUSH, null, name.transliteration) }) {
                                            Icon(Icons.Filled.VolumeUp, contentDescription = "Listen")
                                        }
                                        IconButton(onClick = { onToggleFavorite(name.number.toString()) }) {
                                            Icon(
                                                if (isFav) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                                                contentDescription = "Favorite",
                                                tint = MaterialTheme.colorScheme.secondary
                                            )
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

package com.example.ui.screens

import android.app.Activity
import android.content.Intent
import android.speech.RecognizerIntent
import android.speech.tts.TextToSpeech
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.VolumeOff
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.local.UserPreferencesState
import com.example.data.repository.AiLearningMode
import com.example.localization.stringsFor
import com.example.ui.viewmodel.AiAssistantUiState
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AiAssistantScreen(
    prefs: UserPreferencesState,
    uiState: AiAssistantUiState,
    initialPrompt: String?,
    onSelectMode: (AiLearningMode) -> Unit,
    onSendQuestion: (String) -> Unit,
    onClearChat: () -> Unit,
    onConsumeInitialPrompt: () -> Unit,
    onBack: () -> Unit,
) {
    val context = LocalContext.current
    val strings = stringsFor(prefs.appLanguage)
    var input by remember { mutableStateOf("") }
    var ttsEnabled by remember { mutableStateOf(false) }
    var tts by remember { mutableStateOf<TextToSpeech?>(null) }
    val listState = rememberLazyListState()

    DisposableEffect(Unit) {
        var inst: TextToSpeech? = null
        inst = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                inst?.language = Locale(prefs.appLanguage.code)
            }
        }
        tts = inst
        onDispose { inst.stop(); inst.shutdown() }
    }

    val voiceLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val spoken = result.data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)?.firstOrNull()
            if (!spoken.isNullOrBlank()) {
                input = spoken
                onSendQuestion(spoken)
                input = ""
            }
        }
    }

    LaunchedEffect(initialPrompt) {
        if (!initialPrompt.isNullOrBlank()) {
            onSendQuestion(initialPrompt)
            onConsumeInitialPrompt()
        }
    }

    LaunchedEffect(uiState.messages.size) {
        if (uiState.messages.isNotEmpty()) {
            listState.animateScrollToItem(uiState.messages.lastIndex)
        }
    }

    LaunchedEffect(uiState.isStreaming, ttsEnabled) {
        if (!uiState.isStreaming && ttsEnabled) {
            val last = uiState.messages.lastOrNull()
            if (last != null && !last.isUser && last.text.isNotBlank()) {
                tts?.speak(last.text.take(600), TextToSpeech.QUEUE_FLUSH, null, last.id)
            }
        }
    }

    val starterPrompts = listOf(
        "Explain Surah Al-Fatihah simply",
        "How do I calculate Zakat on gold?",
        "নামাজ ও ওযুর সঠিক নিয়ম বলুন",
        "What is Qalqalah in Tajweed?",
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("HIDAYAH AI Assistant", fontWeight = FontWeight.Bold)
                        Text(uiState.selectedMode.label, style = MaterialTheme.typography.labelSmall)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = {
                        ttsEnabled = !ttsEnabled
                        if (!ttsEnabled) tts?.stop()
                    }) {
                        Icon(
                            if (ttsEnabled) Icons.Filled.VolumeUp else Icons.Filled.VolumeOff,
                            contentDescription = "Toggle Voice Response"
                        )
                    }
                    IconButton(onClick = onClearChat) {
                        Icon(Icons.Filled.DeleteSweep, contentDescription = "Clear Chat")
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
                // 8 Mode Chips
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState())
                        .padding(horizontal = 14.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    AiLearningMode.entries.forEach { mode ->
                        FilterChip(
                            selected = uiState.selectedMode == mode,
                            onClick = { onSelectMode(mode) },
                            label = { Text(mode.label) }
                        )
                    }
                }

                // Messages list
                LazyColumn(
                    state = listState,
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(14.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(uiState.messages, key = { it.id }) { msg ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = if (msg.isUser) Arrangement.End else Arrangement.Start
                        ) {
                            Card(
                                modifier = Modifier.widthIn(max = 560.dp),
                                shape = RoundedCornerShape(18.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = if (msg.isUser) {
                                        MaterialTheme.colorScheme.primary
                                    } else {
                                        MaterialTheme.colorScheme.surface
                                    }
                                )
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    if (!msg.isUser && msg.modeLabel.isNotBlank()) {
                                        Text(
                                            msg.modeLabel,
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.secondary,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                    if (msg.text.isEmpty() && uiState.isStreaming) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp)
                                            Spacer(Modifier.width(8.dp))
                                            Text("Thinking...", style = MaterialTheme.typography.bodySmall)
                                        }
                                    } else {
                                        Text(
                                            text = msg.text,
                                            color = if (msg.isUser) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
                                        )
                                    }
                                }
                            }
                        }
                    }

                    if (uiState.messages.size <= 1) {
                        item {
                            Row(
                                modifier = Modifier.horizontalScroll(rememberScrollState()),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                starterPrompts.forEach { p ->
                                    AssistChip(
                                        onClick = { onSendQuestion(p) },
                                        label = { Text(p) }
                                    )
                                }
                            }
                        }
                    }
                }

                // Composer Bar
                Surface(tonalElevation = 3.dp, modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        IconButton(
                            onClick = {
                                val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                                    putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                                    putExtra(RecognizerIntent.EXTRA_LANGUAGE, prefs.appLanguage.code)
                                    putExtra(RecognizerIntent.EXTRA_PROMPT, "Ask HIDAYAH AI...")
                                }
                                try {
                                    voiceLauncher.launch(intent)
                                } catch (_: Exception) {
                                }
                            }
                        ) {
                            Icon(Icons.Filled.Mic, contentDescription = "Voice Input")
                        }
                        OutlinedTextField(
                            value = input,
                            onValueChange = { input = it },
                            placeholder = { Text(strings.askAiHint, maxLines = 1) },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("ai_question_input"),
                            maxLines = 3,
                            shape = RoundedCornerShape(16.dp)
                        )
                        IconButton(
                            onClick = {
                                val q = input.trim()
                                if (q.isNotEmpty() && !uiState.isStreaming) {
                                    input = ""
                                    onSendQuestion(q)
                                }
                            },
                            enabled = input.isNotBlank() && !uiState.isStreaming,
                            modifier = Modifier.testTag("ai_send_button")
                        ) {
                            Icon(Icons.AutoMirrored.Filled.Send, contentDescription = "Send Question")
                        }
                    }
                }
            }
        }
    }
}

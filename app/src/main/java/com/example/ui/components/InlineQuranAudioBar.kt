package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material.icons.filled.RepeatOne
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.audio.AudioPlaybackState
import com.example.audio.AudioPlayerManager
import com.example.data.local.DownloadedAudioEntity
import com.example.data.seed.QariCatalog
import com.example.data.seed.QariInfo
import com.example.data.seed.QuranMetadata

@Composable
fun InlineQuranAudioBar(
    surahNumber: Int,
    selectedQariId: String,
    audioState: AudioPlaybackState,
    audioVolume: Float,
    downloadedAudio: List<DownloadedAudioEntity>,
    onOpenQariSheet: () -> Unit,
    onVolumeChange: (Float) -> Unit,
    onDownloadSurah: (Int, QariInfo) -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val activeQari = if (audioState.isPlaying || audioState.isBuffering) {
        audioState.qari
    } else {
        QariCatalog.findById(selectedQariId)
    }
    val displaySurahNum = if (audioState.isPlaying || audioState.isBuffering) audioState.surahNumber else surahNumber
    val displaySurah = QuranMetadata.getSurah(displaySurahNum)
    val displayAyah = if (audioState.isPlaying || audioState.isBuffering) audioState.ayahNumber else 1

    val downloadedCount = remember(downloadedAudio, displaySurahNum, activeQari.id) {
        downloadedAudio.count { it.surahNumber == displaySurahNum && it.qariId == activeQari.id }
    }
    val isOfflineReady = downloadedCount >= displaySurah.ayahCount

    var showAdvancedControls by remember { mutableStateOf(false) }

    val durationMs = audioState.durationMs.coerceAtLeast(1L)
    val positionMs = audioState.currentPositionMs.coerceIn(0L, durationMs)
    val sliderFraction = if (audioState.durationMs > 0L) {
        (positionMs.toFloat() / durationMs.toFloat()).coerceIn(0f, 1f)
    } else {
        0f
    }

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
        Surface(
            modifier = modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp, vertical = 6.dp)
                .testTag("inline_quran_audio_bar"),
            shape = RoundedCornerShape(20.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp,
            shadowElevation = 4.dp,
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.35f)),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                // Row 1: Qari Info + Surah:Ayah + Offline Download + Qari Picker
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .clickable { onOpenQariSheet() }
                            .padding(vertical = 2.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primary),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = activeQari.name.take(2).uppercase(),
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "${displaySurah.englishName} (${displaySurah.number}:$displayAyah/${displaySurah.ayahCount})",
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = "${activeQari.name} • Tap to change Qari",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(
                            onClick = { onDownloadSurah(displaySurahNum, activeQari) },
                            modifier = Modifier.size(36.dp).testTag("inline_audio_download_btn")
                        ) {
                            Icon(
                                imageVector = if (isOfflineReady) Icons.Filled.CheckCircle else Icons.Filled.Download,
                                contentDescription = if (isOfflineReady) "Offline Surah downloaded" else "Download Surah audio offline",
                                tint = if (isOfflineReady) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        IconButton(
                            onClick = { showAdvancedControls = !showAdvancedControls },
                            modifier = Modifier.size(36.dp).testTag("inline_audio_expand_btn")
                        ) {
                            Icon(
                                imageVector = if (showAdvancedControls) Icons.Filled.ExpandMore else Icons.Filled.ExpandLess,
                                contentDescription = "Toggle audio speed and volume controls"
                            )
                        }
                    }
                }

                // Live Progress Slider & Time Stamps
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = formatMillis(audioState.currentPositionMs),
                        style = MaterialTheme.typography.labelSmall,
                        fontSize = 10.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Slider(
                        value = sliderFraction,
                        onValueChange = { frac ->
                            if (audioState.durationMs > 0L) {
                                AudioPlayerManager.seekTo((frac * audioState.durationMs).toLong())
                            }
                        },
                        enabled = audioState.durationMs > 0L,
                        colors = SliderDefaults.colors(
                            thumbColor = MaterialTheme.colorScheme.primary,
                            activeTrackColor = MaterialTheme.colorScheme.primary,
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .height(24.dp)
                    )
                    Text(
                        text = formatMillis(audioState.durationMs),
                        style = MaterialTheme.typography.labelSmall,
                        fontSize = 10.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // Main Transport Row (Repeat Ayah | Prev | Play/Pause | Next | Repeat Surah | Speed)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Ayah repeat cycle (1 -> 3 -> 5 -> ∞)
                    Surface(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .clickable {
                                val next = when (audioState.ayahRepeatTarget) {
                                    1 -> 3
                                    3 -> 5
                                    5 -> 0
                                    else -> 1
                                }
                                AudioPlayerManager.setAyahRepeat(next)
                            }
                            .testTag("inline_repeat_ayah_btn"),
                        color = if (audioState.ayahRepeatTarget != 1) {
                            MaterialTheme.colorScheme.secondaryContainer
                        } else {
                            MaterialTheme.colorScheme.surfaceVariant
                        }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(3.dp)
                        ) {
                            Icon(Icons.Filled.RepeatOne, contentDescription = "Repeat Ayah", modifier = Modifier.size(16.dp))
                            Text(
                                text = if (audioState.ayahRepeatTarget == 0) "∞" else "${audioState.ayahRepeatTarget}x",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        IconButton(
                            onClick = { AudioPlayerManager.skipPreviousAyah(context) },
                            modifier = Modifier.size(40.dp).testTag("inline_prev_ayah_btn")
                        ) {
                            Icon(Icons.Filled.SkipPrevious, contentDescription = "Previous Ayah")
                        }

                        Surface(
                            modifier = Modifier
                                .size(46.dp)
                                .clip(CircleShape)
                                .clickable {
                                    if (audioState.isPlaying || audioState.isBuffering || audioState.durationMs > 0L) {
                                        AudioPlayerManager.togglePlayPause(context)
                                    } else {
                                        AudioPlayerManager.playSurahFromStart(context, surahNumber, activeQari)
                                    }
                                }
                                .testTag("inline_play_pause_btn"),
                            color = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary,
                            shape = CircleShape,
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                if (audioState.isBuffering) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(22.dp),
                                        color = MaterialTheme.colorScheme.onPrimary,
                                        strokeWidth = 2.5.dp
                                    )
                                } else {
                                    Icon(
                                        imageVector = if (audioState.isPlaying) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                                        contentDescription = if (audioState.isPlaying) "Pause recitation" else "Play recitation"
                                    )
                                }
                            }
                        }

                        IconButton(
                            onClick = { AudioPlayerManager.skipNextAyah(context) },
                            modifier = Modifier.size(40.dp).testTag("inline_next_ayah_btn")
                        ) {
                            Icon(Icons.Filled.SkipNext, contentDescription = "Next Ayah")
                        }
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        IconButton(
                            onClick = { AudioPlayerManager.setRepeatSurah(!audioState.repeatSurah) },
                            modifier = Modifier.size(36.dp).testTag("inline_repeat_surah_btn")
                        ) {
                            Icon(
                                Icons.Filled.Repeat,
                                contentDescription = "Repeat Surah",
                                tint = if (audioState.repeatSurah) {
                                    MaterialTheme.colorScheme.primary
                                } else {
                                    MaterialTheme.colorScheme.onSurfaceVariant
                                }
                            )
                        }

                        Surface(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .clickable {
                                    val nextSpeed = when (audioState.playbackSpeed) {
                                        0.75f -> 1.0f
                                        1.0f -> 1.25f
                                        1.25f -> 1.5f
                                        1.5f -> 2.0f
                                        else -> 0.75f
                                    }
                                    AudioPlayerManager.setSpeed(nextSpeed)
                                }
                                .testTag("inline_speed_btn"),
                            color = MaterialTheme.colorScheme.surfaceVariant
                        ) {
                            Text(
                                text = "${audioState.playbackSpeed}x",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp)
                            )
                        }

                        if (audioState.isPlaying || audioState.isBuffering) {
                            IconButton(
                                onClick = { AudioPlayerManager.stop() },
                                modifier = Modifier.size(34.dp)
                            ) {
                                Icon(Icons.Filled.Close, contentDescription = "Stop recitation", modifier = Modifier.size(18.dp))
                            }
                        }
                    }
                }

                // Expandable Volume & Quick Qari/Hifz Drawer
                AnimatedVisibility(visible = showAdvancedControls) {
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                Icons.AutoMirrored.Filled.VolumeUp,
                                contentDescription = "Volume",
                                modifier = Modifier.size(18.dp),
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Slider(
                                value = audioVolume,
                                onValueChange = onVolumeChange,
                                valueRange = 0f..1f,
                                modifier = Modifier.weight(1f).height(26.dp)
                            )
                            Text(
                                "${(audioVolume * 100).toInt()}%",
                                style = MaterialTheme.typography.labelSmall
                            )
                        }
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            FilterChip(
                                selected = false,
                                onClick = onOpenQariSheet,
                                leadingIcon = { Icon(Icons.Filled.Tune, contentDescription = null, modifier = Modifier.size(14.dp)) },
                                label = { Text("All 20 Qaris & Hifz Loop") }
                            )
                            audioState.downloadProgressText?.let { prog ->
                                Text(prog, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary)
                            }
                            audioState.errorMessage?.let { err ->
                                Text(err, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.error)
                            }
                        }
                    }
                }
            }
        }
    }
}

private fun formatMillis(ms: Long): String {
    if (ms <= 0L) return "0:00"
    val totalSec = (ms / 1000L).toInt()
    val mins = totalSec / 60
    val secs = totalSec % 60
    return "%d:%02d".format(mins, secs)
}

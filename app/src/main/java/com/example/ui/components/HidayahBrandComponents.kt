package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.audio.AudioPlaybackState
import com.example.data.seed.QuranMetadata
import com.example.ui.theme.AmiriFontFamily

/**
 * Official HIDAYAH (هداية) Geometric Emblem:
 * Combines an Islamic pointed Mihrab arch, a radiant guidance light beam,
 * an open Quran silhouette, and a subtle modern 'H' monogram.
 */
@Composable
fun HidayahLogoMark(
    modifier: Modifier = Modifier,
    size: Dp = 56.dp,
    darkSurfaceVariant: Boolean = true,
) {
    val bgTop = if (darkSurfaceVariant) Color(0xFF0E4435) else Color(0xFFFAF7F0)
    val bgBottom = if (darkSurfaceVariant) Color(0xFF07231B) else Color(0xFFEBE4D4)
    val goldColor = Color(0xFFD9AE3B)
    val accentStroke = if (darkSurfaceVariant) Color(0xFFF6E7BD) else Color(0xFF0B3B2E)

    Box(
        modifier = modifier
            .size(size)
            .clip(RoundedCornerShape(size * 0.28f))
            .background(Brush.verticalGradient(listOf(bgTop, bgBottom)))
            .border(1.5.dp, goldColor.copy(alpha = 0.7f), RoundedCornerShape(size * 0.28f)),
        contentAlignment = Alignment.Center,
    ) {
        Canvas(modifier = Modifier.size(size * 0.78f)) {
            val w = this.size.width
            val h = this.size.height

            // Subtle guidance light beam from top apex
            drawPath(
                path = Path().apply {
                    moveTo(w * 0.50f, h * 0.08f)
                    lineTo(w * 0.22f, h * 0.76f)
                    lineTo(w * 0.78f, h * 0.76f)
                    close()
                },
                brush = Brush.verticalGradient(
                    listOf(goldColor.copy(alpha = 0.34f), Color.Transparent)
                )
            )

            // Pointed Islamic Arch
            val archPath = Path().apply {
                moveTo(w * 0.14f, h * 0.88f)
                lineTo(w * 0.14f, h * 0.44f)
                quadraticTo(w * 0.14f, h * 0.18f, w * 0.50f, h * 0.08f)
                quadraticTo(w * 0.86f, h * 0.18f, w * 0.86f, h * 0.44f)
                lineTo(w * 0.86f, h * 0.88f)
            }
            drawPath(
                path = archPath,
                color = goldColor,
                style = Stroke(width = w * 0.065f, cap = StrokeCap.Round, join = StrokeJoin.Round)
            )

            // Modern 'H' Monogram pillars inside arch
            drawLine(
                color = accentStroke,
                start = Offset(w * 0.34f, h * 0.34f),
                end = Offset(w * 0.34f, h * 0.66f),
                strokeWidth = w * 0.06f,
                cap = StrokeCap.Round
            )
            drawLine(
                color = accentStroke,
                start = Offset(w * 0.66f, h * 0.34f),
                end = Offset(w * 0.66f, h * 0.66f),
                strokeWidth = w * 0.06f,
                cap = StrokeCap.Round
            )
            drawLine(
                color = goldColor,
                start = Offset(w * 0.34f, h * 0.50f),
                end = Offset(w * 0.66f, h * 0.50f),
                strokeWidth = w * 0.055f,
                cap = StrokeCap.Round
            )

            // Open Quran silhouette at base
            val bookPath = Path().apply {
                moveTo(w * 0.18f, h * 0.80f)
                quadraticTo(w * 0.34f, h * 0.72f, w * 0.50f, h * 0.84f)
                quadraticTo(w * 0.66f, h * 0.72f, w * 0.82f, h * 0.80f)
            }
            drawPath(
                path = bookPath,
                color = accentStroke,
                style = Stroke(width = w * 0.06f, cap = StrokeCap.Round, join = StrokeJoin.Round)
            )
        }
    }
}

@Composable
fun BrandGuidelinesDialog(
    onDismiss: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Column {
                Text("HIDAYAH Brand Guidelines", fontWeight = FontWeight.Bold)
                Text(
                    "هداية • Guidance & The Right Path",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.secondary,
                    fontFamily = AmiriFontFamily,
                )
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    "Logo Variations (Dark & Light)",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.SemiBold
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        HidayahLogoMark(size = 64.dp, darkSurfaceVariant = true)
                        Spacer(Modifier.height(4.dp))
                        Text("Emerald Dark", style = MaterialTheme.typography.labelSmall)
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        HidayahLogoMark(size = 64.dp, darkSurfaceVariant = false)
                        Spacer(Modifier.height(4.dp))
                        Text("Cream Light", style = MaterialTheme.typography.labelSmall)
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            "هداية",
                            fontFamily = AmiriFontFamily,
                            fontSize = 26.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            "Arch • Light Beam • Open Quran • H Monogram",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Text("Brand Color Palette", style = MaterialTheme.typography.labelLarge)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    BrandSwatch(Color(0xFF0B3B2E), "Emerald")
                    BrandSwatch(Color(0xFFC59B27), "Gold")
                    BrandSwatch(Color(0xFFFAF7F0), "Cream")
                    BrandSwatch(Color(0xFF0B0C0F), "Midnight")
                }

                Text(
                    "Official Attribution: Founded and Owned by Mohammed Touhidul Islam, Son of Sirajul Islam.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) { Text("Close") }
        }
    )
}

@Composable
private fun BrandSwatch(color: Color, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(28.dp)
                .clip(CircleShape)
                .background(color)
                .border(1.dp, MaterialTheme.colorScheme.outline, CircleShape)
        )
        Spacer(Modifier.height(2.dp))
        Text(label, style = MaterialTheme.typography.labelSmall)
    }
}

@Composable
fun PersistentMiniAudioPlayer(
    audioState: AudioPlaybackState,
    onTogglePlayPause: () -> Unit,
    onNextAyah: () -> Unit,
    onStop: () -> Unit,
    onOpenReader: (surahNumber: Int, ayahNumber: Int) -> Unit,
) {
    val visible = audioState.isPlaying || audioState.isBuffering || audioState.errorMessage != null
    AnimatedVisibility(visible = visible) {
        val surah = QuranMetadata.getSurah(audioState.surahNumber)
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 4.dp)
                .testTag("mini_audio_player")
                .clickable { onOpenReader(audioState.surahNumber, audioState.ayahNumber) },
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary,
            tonalElevation = 6.dp,
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                if (audioState.isBuffering) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.secondary,
                        strokeWidth = 2.5.dp
                    )
                } else {
                    IconButton(
                        onClick = onTogglePlayPause,
                        modifier = Modifier
                            .size(40.dp)
                            .testTag("mini_player_toggle")
                    ) {
                        Icon(
                            imageVector = if (audioState.isPlaying) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                            contentDescription = "Play or pause Quran recitation"
                        )
                    }
                }
                Spacer(Modifier.width(8.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "${surah.englishName} • Ayah ${audioState.ayahNumber}/${surah.ayahCount}",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    Text(
                        text = audioState.errorMessage ?: "${audioState.qari.name} (${audioState.playbackSpeed}x)",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.85f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
                IconButton(onClick = onNextAyah) {
                    Icon(Icons.Filled.SkipNext, contentDescription = "Next Ayah")
                }
                IconButton(onClick = onStop) {
                    Icon(Icons.Filled.Close, contentDescription = "Stop recitation")
                }
            }
        }
    }
}

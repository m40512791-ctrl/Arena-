package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.engine.AudioTtsManager
import com.example.ui.theme.*

@Composable
fun AudioPlayerBar(
    ttsManager: AudioTtsManager,
    chapterTitle: String,
    onPrevious: () -> Unit,
    onNext: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showSpeedDialog by remember { mutableStateOf(false) }

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .border(1.dp, ArenaBorder, RoundedCornerShape(16.dp)),
        color = ArenaSurfaceElevated,
        tonalElevation = 6.dp
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .background(if (ttsManager.isPlaying) CrimsonNeon.copy(alpha = 0.2f) else ArenaBorder, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.VolumeUp,
                            contentDescription = "Audio Icon",
                            tint = if (ttsManager.isPlaying) CrimsonNeon else TextSecondary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "AI Voice Narration",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = CyanNeon
                        )
                        Text(
                            text = chapterTitle,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                // Speed chip
                OutlinedButton(
                    onClick = { showSpeedDialog = true },
                    modifier = Modifier
                        .height(32.dp)
                        .testTag("speed_button"),
                    contentPadding = PaddingValues(horizontal = 8.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = TextPrimary),
                    border = ButtonDefaults.outlinedButtonBorder.copy(brush = androidx.compose.ui.graphics.SolidColor(ArenaBorder))
                ) {
                    Text("${ttsManager.playbackSpeed}x", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Player controls row
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                IconButton(
                    onClick = onPrevious,
                    modifier = Modifier.testTag("audio_prev_button")
                ) {
                    Icon(Icons.Default.SkipPrevious, contentDescription = "Previous Chapter", tint = TextPrimary)
                }

                Spacer(modifier = Modifier.width(8.dp))

                FloatingActionButton(
                    onClick = {
                        if (ttsManager.isPlaying) {
                            ttsManager.pauseOrStop()
                        } else {
                            // caller triggers play
                        }
                    },
                    modifier = Modifier
                        .size(44.dp)
                        .testTag("audio_play_pause_button"),
                    containerColor = CrimsonNeon,
                    contentColor = ArenaBgDark,
                    shape = CircleShape
                ) {
                    Icon(
                        imageVector = if (ttsManager.isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = if (ttsManager.isPlaying) "Pause Narration" else "Play Narration",
                        modifier = Modifier.size(26.dp)
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                IconButton(
                    onClick = onNext,
                    modifier = Modifier.testTag("audio_next_button")
                ) {
                    Icon(Icons.Default.SkipNext, contentDescription = "Next Chapter", tint = TextPrimary)
                }
            }

            // Status message
            Text(
                text = ttsManager.statusMessage,
                fontSize = 11.sp,
                color = TextMuted,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        }
    }

    if (showSpeedDialog) {
        AlertDialog(
            onDismissRequest = { showSpeedDialog = false },
            title = { Text("Playback Speed", color = TextPrimary) },
            text = {
                Column {
                    listOf(0.75f, 1.0f, 1.25f, 1.5f, 2.0f).forEach { speed ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = (ttsManager.playbackSpeed == speed),
                                onClick = {
                                    ttsManager.setSpeed(speed)
                                    showSpeedDialog = false
                                }
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("${speed}x Speed", color = TextPrimary, fontSize = 14.sp)
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showSpeedDialog = false }) {
                    Text("Close", color = CyanNeon)
                }
            },
            containerColor = ArenaSurfaceDark
        )
    }
}

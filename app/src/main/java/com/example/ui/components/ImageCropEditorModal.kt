package com.example.ui.components

import android.graphics.Bitmap
import android.graphics.Matrix
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTransformGestures
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.model.CropAspectRatio
import com.example.ui.theme.*
import kotlin.math.roundToInt

@Composable
fun ImageCropEditorModal(
    rawBitmap: Bitmap,
    initialAspectRatio: CropAspectRatio = CropAspectRatio.SQUARE_1_1,
    onConfirm: (Bitmap) -> Unit,
    onCancel: () -> Unit
) {
    var selectedAspectRatio by remember { mutableStateOf(initialAspectRatio) }
    var zoomScale by remember { mutableFloatStateOf(1f) }
    var rotationAngle by remember { mutableFloatStateOf(0f) }
    var panOffset by remember { mutableStateOf(Offset.Zero) }

    Dialog(
        onDismissRequest = onCancel,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .background(ArenaBgDark),
            color = ArenaBgDark
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .systemBarsPadding()
                    .padding(16.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(
                            onClick = onCancel,
                            modifier = Modifier.testTag("crop_cancel_btn")
                        ) {
                            Icon(Icons.Default.Close, contentDescription = "Cancel", tint = TextPrimary)
                        }
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Crop & Adjust Image",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                    }

                    // Reset button
                    TextButton(
                        onClick = {
                            zoomScale = 1f
                            rotationAngle = 0f
                            panOffset = Offset.Zero
                        },
                        modifier = Modifier.testTag("crop_reset_btn")
                    ) {
                        Icon(Icons.Default.Refresh, contentDescription = null, tint = CyanNeon, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Reset", color = CyanNeon, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Aspect Ratio Selector Chips
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    CropAspectRatio.values().forEach { ratio ->
                        val isSelected = ratio == selectedAspectRatio
                        FilterChip(
                            selected = isSelected,
                            onClick = { selectedAspectRatio = ratio },
                            label = { Text(ratio.label, fontSize = 11.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = CrimsonNeon,
                                selectedLabelColor = ArenaBgDark,
                                containerColor = ArenaSurfaceElevated,
                                labelColor = TextSecondary
                            ),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.testTag("aspect_ratio_${ratio.name}")
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Interactive Crop Viewport Area
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color.Black)
                        .border(1.dp, ArenaBorder, RoundedCornerShape(16.dp))
                        .pointerInput(Unit) {
                            detectTransformGestures { _, pan, zoom, _ ->
                                zoomScale = (zoomScale * zoom).coerceIn(0.6f, 3.5f)
                                panOffset += pan
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    val imageBitmap = remember(rawBitmap, rotationAngle) {
                        if (rotationAngle != 0f) {
                            val matrix = Matrix().apply { postRotate(rotationAngle) }
                            Bitmap.createBitmap(rawBitmap, 0, 0, rawBitmap.width, rawBitmap.height, matrix, true).asImageBitmap()
                        } else {
                            rawBitmap.asImageBitmap()
                        }
                    }

                    // Render Transformable Image Canvas
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val canvasWidth = size.width
                        val canvasHeight = size.height

                        // Calculate crop viewport dimensions based on aspect ratio
                        val ratioW = selectedAspectRatio.widthRatio
                        val ratioH = selectedAspectRatio.heightRatio
                        val targetRatio = ratioW / ratioH

                        val cropW: Float
                        val cropH: Float
                        val padding = 32.dp.toPx()
                        val availableW = canvasWidth - (padding * 2)
                        val availableH = canvasHeight - (padding * 2)

                        if (availableW / availableH > targetRatio) {
                            cropH = availableH
                            cropW = cropH * targetRatio
                        } else {
                            cropW = availableW
                            cropH = cropW / targetRatio
                        }

                        val cropRect = Rect(
                            offset = Offset((canvasWidth - cropW) / 2f, (canvasHeight - cropH) / 2f),
                            size = Size(cropW, cropH)
                        )

                        // Draw Image centered with user transforms
                        val imgW = imageBitmap.width * zoomScale
                        val imgH = imageBitmap.height * zoomScale
                        val drawX = (canvasWidth - imgW) / 2f + panOffset.x
                        val drawY = (canvasHeight - imgH) / 2f + panOffset.y

                        drawImage(
                            image = imageBitmap,
                            dstOffset = IntOffset(drawX.roundToInt(), drawY.roundToInt()),
                            dstSize = IntSize(imgW.roundToInt(), imgH.roundToInt())
                        )

                        // Draw Dim Overlay outside cropRect
                        val scrimPath = Path().apply {
                            fillType = PathFillType.EvenOdd
                            addRect(Rect(0f, 0f, canvasWidth, canvasHeight))
                            addRect(cropRect)
                        }
                        drawPath(path = scrimPath, color = Color.Black.copy(alpha = 0.65f))

                        // Draw Viewport Border
                        drawRect(
                            color = CyanNeon,
                            topLeft = cropRect.topLeft,
                            size = cropRect.size,
                            style = Stroke(width = 2.dp.toPx())
                        )

                        // Draw Rule of Thirds Grid Lines
                        val thirdW = cropRect.width / 3f
                        val thirdH = cropRect.height / 3f

                        drawLine(
                            color = Color.White.copy(alpha = 0.35f),
                            start = Offset(cropRect.left + thirdW, cropRect.top),
                            end = Offset(cropRect.left + thirdW, cropRect.bottom),
                            strokeWidth = 1.dp.toPx()
                        )
                        drawLine(
                            color = Color.White.copy(alpha = 0.35f),
                            start = Offset(cropRect.left + thirdW * 2, cropRect.top),
                            end = Offset(cropRect.left + thirdW * 2, cropRect.bottom),
                            strokeWidth = 1.dp.toPx()
                        )
                        drawLine(
                            color = Color.White.copy(alpha = 0.35f),
                            start = Offset(cropRect.left, cropRect.top + thirdH),
                            end = Offset(cropRect.right, cropRect.top + thirdH),
                            strokeWidth = 1.dp.toPx()
                        )
                        drawLine(
                            color = Color.White.copy(alpha = 0.35f),
                            start = Offset(cropRect.left, cropRect.top + thirdH * 2),
                            end = Offset(cropRect.right, cropRect.top + thirdH * 2),
                            strokeWidth = 1.dp.toPx()
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Bottom Tool Controls
                Surface(
                    color = ArenaSurfaceDark,
                    shape = RoundedCornerShape(14.dp),
                    border = ButtonDefaults.outlinedButtonBorder.copy(brush = androidx.compose.ui.graphics.SolidColor(ArenaBorder)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        // Zoom Slider
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.ZoomIn, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Slider(
                                value = zoomScale,
                                onValueChange = { zoomScale = it },
                                valueRange = 0.6f..3.5f,
                                modifier = Modifier.weight(1f).testTag("crop_zoom_slider"),
                                colors = SliderDefaults.colors(
                                    thumbColor = CyanNeon,
                                    activeTrackColor = CyanNeon,
                                    inactiveTrackColor = ArenaSurfaceElevated
                                )
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("${(zoomScale * 100).toInt()}%", fontSize = 11.sp, color = TextMuted)
                        }

                        // Rotate & Reposition actions
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            OutlinedButton(
                                onClick = {
                                    rotationAngle = (rotationAngle + 90f) % 360f
                                },
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = TextPrimary),
                                border = ButtonDefaults.outlinedButtonBorder.copy(brush = androidx.compose.ui.graphics.SolidColor(ArenaBorder)),
                                modifier = Modifier.testTag("crop_rotate_btn")
                            ) {
                                Icon(Icons.Default.RotateRight, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Rotate 90°", fontSize = 12.sp)
                            }

                            // Confirm & Apply Button
                            Button(
                                onClick = {
                                    val cropped = createCroppedBitmap(
                                        source = rawBitmap,
                                        aspectRatio = selectedAspectRatio,
                                        rotation = rotationAngle,
                                        zoom = zoomScale,
                                        pan = panOffset
                                    )
                                    onConfirm(cropped)
                                },
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = CrimsonNeon),
                                modifier = Modifier.testTag("crop_confirm_btn")
                            ) {
                                Icon(Icons.Default.Check, contentDescription = null, tint = ArenaBgDark)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Confirm Crop", color = ArenaBgDark, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * Transforms and crops bitmap according to parameters.
 */
private fun createCroppedBitmap(
    source: Bitmap,
    aspectRatio: CropAspectRatio,
    rotation: Float,
    zoom: Float,
    pan: Offset
): Bitmap {
    val matrix = Matrix()
    if (rotation != 0f) {
        matrix.postRotate(rotation)
    }

    val rotated = if (rotation != 0f) {
        Bitmap.createBitmap(source, 0, 0, source.width, source.height, matrix, true)
    } else {
        source
    }

    val targetRatio = aspectRatio.widthRatio / aspectRatio.heightRatio

    val srcW = rotated.width.toFloat()
    val srcH = rotated.height.toFloat()

    // Base crop rect according to aspect ratio
    var cropW: Float
    var cropH: Float

    if (srcW / srcH > targetRatio) {
        cropH = srcH
        cropW = cropH * targetRatio
    } else {
        cropW = srcW
        cropH = cropW / targetRatio
    }

    // Apply zoom & pan adjustments
    cropW /= zoom.coerceAtLeast(0.5f)
    cropH /= zoom.coerceAtLeast(0.5f)

    cropW = cropW.coerceAtMost(srcW)
    cropH = cropH.coerceAtMost(srcH)

    var startX = (srcW - cropW) / 2f - (pan.x / zoom).coerceIn(-srcW / 2f, srcW / 2f)
    var startY = (srcH - cropH) / 2f - (pan.y / zoom).coerceIn(-srcH / 2f, srcH / 2f)

    startX = startX.coerceIn(0f, srcW - cropW)
    startY = startY.coerceIn(0f, srcH - cropH)

    return Bitmap.createBitmap(
        rotated,
        startX.toInt(),
        startY.toInt(),
        cropW.toInt(),
        cropH.toInt()
    )
}

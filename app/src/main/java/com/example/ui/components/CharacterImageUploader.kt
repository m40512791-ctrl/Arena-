package com.example.ui.components

import android.graphics.Bitmap
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.model.CharacterImageState
import com.example.model.CropAspectRatio
import com.example.model.SourceType
import com.example.model.UploadStatus
import com.example.storage.ImageStorageService
import com.example.storage.ImageValidationResult
import com.example.ui.theme.*
import kotlinx.coroutines.launch

@Composable
fun CharacterImageUploader(
    characterName: String,
    sourceType: SourceType,
    accentColor: Color,
    imageState: CharacterImageState,
    storageService: ImageStorageService,
    onImageStateChange: (CharacterImageState) -> Unit,
    modifier: Modifier = Modifier,
    tagPrefix: String = "char_a"
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    var showActionMenu by remember { mutableStateOf(false) }
    var pendingBitmapForCrop by remember { mutableStateOf<Bitmap?>(null) }
    var showCropDialog by remember { mutableStateOf(false) }

    // Android Activity Result Launchers
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            coroutineScope.launch {
                onImageStateChange(imageState.copy(uploadStatus = UploadStatus.SELECTING))
                when (val result = storageService.validateAndDecode(uri)) {
                    is ImageValidationResult.Success -> {
                        pendingBitmapForCrop = result.bitmap
                        showCropDialog = true
                        onImageStateChange(
                            imageState.copy(
                                originalFileName = result.fileName,
                                sizeBytes = result.originalSize,
                                uploadStatus = UploadStatus.CROPPING
                            )
                        )
                    }
                    is ImageValidationResult.Error -> {
                        onImageStateChange(
                            imageState.copy(
                                uploadStatus = UploadStatus.ERROR,
                                errorMessage = result.message
                            )
                        )
                        Toast.makeText(context, result.message, Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap: Bitmap? ->
        if (bitmap != null) {
            pendingBitmapForCrop = bitmap
            showCropDialog = true
            onImageStateChange(
                imageState.copy(
                    originalFileName = "camera_capture.jpg",
                    uploadStatus = UploadStatus.CROPPING
                )
            )
        }
    }

    Column(modifier = modifier.fillMaxWidth()) {
        if (imageState.imageUri != null) {
            // Display Uploaded Character Image with Controls
            Card(
                colors = CardDefaults.cardColors(containerColor = ArenaSurfaceElevated),
                shape = RoundedCornerShape(14.dp),
                border = ButtonDefaults.outlinedButtonBorder.copy(
                    brush = androidx.compose.ui.graphics.SolidColor(accentColor.copy(alpha = 0.8f))
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("${tagPrefix}_image_preview_card")
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color.Black),
                        contentAlignment = Alignment.Center
                    ) {
                        AsyncImage(
                            model = ImageRequest.Builder(context)
                                .data(imageState.imageUri)
                                .crossfade(true)
                                .build(),
                            contentDescription = "$characterName portrait",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )

                        // Top Badges
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .align(Alignment.TopStart)
                                .padding(8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                color = ArenaBgDark.copy(alpha = 0.75f),
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                                ) {
                                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = HealthGreen, modifier = Modifier.size(12.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("CUSTOM ART", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = HealthGreen)
                                }
                            }

                            SourceTypeBadge(sourceType)
                        }

                        // Bottom Character label
                        Surface(
                            color = ArenaBgDark.copy(alpha = 0.8f),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .align(Alignment.BottomStart)
                                .padding(8.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = characterName,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    modifier = Modifier.weight(1f)
                                )
                                if (imageState.sizeBytes > 0) {
                                    Text(
                                        text = "%.1f KB".format(imageState.sizeBytes / 1024.0),
                                        fontSize = 10.sp,
                                        color = TextMuted
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Action Controls: Replace, Crop Again, Remove
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = { showActionMenu = true },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("${tagPrefix}_replace_btn"),
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = TextPrimary),
                            border = ButtonDefaults.outlinedButtonBorder.copy(
                                brush = androidx.compose.ui.graphics.SolidColor(ArenaBorder)
                            ),
                            contentPadding = PaddingValues(horizontal = 6.dp, vertical = 6.dp)
                        ) {
                            Icon(Icons.Default.Image, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Replace", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }

                        OutlinedButton(
                            onClick = {
                                // Re-crop current image if bitmap exists
                                coroutineScope.launch {
                                    val uri = Uri.parse(imageState.imageUri)
                                    when (val res = storageService.validateAndDecode(uri)) {
                                        is ImageValidationResult.Success -> {
                                            pendingBitmapForCrop = res.bitmap
                                            showCropDialog = true
                                        }
                                        else -> Toast.makeText(context, "Unable to load image for re-cropping", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("${tagPrefix}_crop_btn"),
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = CyanNeon),
                            border = ButtonDefaults.outlinedButtonBorder.copy(
                                brush = androidx.compose.ui.graphics.SolidColor(CyanNeon.copy(alpha = 0.5f))
                            ),
                            contentPadding = PaddingValues(horizontal = 6.dp, vertical = 6.dp)
                        ) {
                            Icon(Icons.Default.Crop, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Crop", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }

                        OutlinedButton(
                            onClick = {
                                imageState.imageUri?.let { path ->
                                    storageService.deleteImage(path.removePrefix("file://"))
                                }
                                onImageStateChange(CharacterImageState())
                                Toast.makeText(context, "Image removed", Toast.LENGTH_SHORT).show()
                            },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("${tagPrefix}_remove_btn"),
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = DangerRed),
                            border = ButtonDefaults.outlinedButtonBorder.copy(
                                brush = androidx.compose.ui.graphics.SolidColor(DangerRed.copy(alpha = 0.4f))
                            ),
                            contentPadding = PaddingValues(horizontal = 6.dp, vertical = 6.dp)
                        ) {
                            Icon(Icons.Default.DeleteOutline, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Remove", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        } else {
            // Empty Placeholder State
            Surface(
                color = ArenaSurfaceElevated,
                shape = RoundedCornerShape(14.dp),
                border = ButtonDefaults.outlinedButtonBorder.copy(
                    brush = androidx.compose.ui.graphics.SolidColor(
                        if (imageState.uploadStatus == UploadStatus.ERROR) DangerRed else ArenaBorder
                    )
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showActionMenu = true }
                    .testTag("${tagPrefix}_upload_placeholder")
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(52.dp)
                            .background(accentColor.copy(alpha = 0.15f), CircleShape)
                            .border(1.dp, accentColor.copy(alpha = 0.6f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.AddPhotoAlternate,
                            contentDescription = "Upload Character Image",
                            tint = accentColor,
                            modifier = Modifier.size(26.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "Upload Character Image",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "Gallery • Camera • JPG, PNG, WEBP (Max 10 MB)",
                        fontSize = 11.sp,
                        color = TextMuted,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        onClick = { showActionMenu = true },
                        colors = ButtonDefaults.buttonColors(containerColor = accentColor),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
                        modifier = Modifier.testTag("${tagPrefix}_select_image_btn")
                    ) {
                        Icon(Icons.Default.Upload, contentDescription = null, tint = ArenaBgDark, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Select Image", color = ArenaBgDark, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }
            }
        }

        // Upload Status & Progress Feedback
        AnimatedVisibility(visible = imageState.uploadStatus == UploadStatus.UPLOADING) {
            Column(modifier = Modifier.padding(top = 8.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Compressing & Saving Image...", fontSize = 11.sp, color = CyanNeon)
                    Text("${(imageState.progress * 100).toInt()}%", fontSize = 11.sp, color = CyanNeon, fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.height(4.dp))
                LinearProgressIndicator(
                    progress = { imageState.progress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(4.dp)
                        .clip(RoundedCornerShape(2.dp)),
                    color = CyanNeon,
                    trackColor = ArenaSurfaceElevated
                )
            }
        }

        // Error message banner
        if (imageState.uploadStatus == UploadStatus.ERROR && imageState.errorMessage != null) {
            Surface(
                color = DangerRed.copy(alpha = 0.15f),
                shape = RoundedCornerShape(8.dp),
                border = ButtonDefaults.outlinedButtonBorder.copy(brush = androidx.compose.ui.graphics.SolidColor(DangerRed)),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
            ) {
                Row(
                    modifier = Modifier.padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.ErrorOutline, contentDescription = null, tint = DangerRed, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = imageState.errorMessage,
                        fontSize = 11.sp,
                        color = TextPrimary,
                        modifier = Modifier.weight(1f)
                    )
                    IconButton(
                        onClick = { onImageStateChange(imageState.copy(uploadStatus = UploadStatus.IDLE, errorMessage = null)) },
                        modifier = Modifier.size(20.dp)
                    ) {
                        Icon(Icons.Default.Close, contentDescription = "Dismiss", tint = TextMuted, modifier = Modifier.size(14.dp))
                    }
                }
            }
        }
    }

    // Modern Upload Interface Modal (Gallery / Browse Files / Take Photo / Cancel)
    if (showActionMenu) {
        AlertDialog(
            onDismissRequest = { showActionMenu = false },
            title = {
                Text(
                    text = "Upload $characterName Image",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
            },
            text = {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "Choose an image source. Supports JPG, PNG, WEBP up to 10 MB.",
                        fontSize = 12.sp,
                        color = TextSecondary
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // 1. Choose from Gallery
                    UploadOptionRow(
                        icon = Icons.Default.PhotoLibrary,
                        title = "Choose from Gallery",
                        subtitle = "Select photo from local device gallery",
                        color = CrimsonNeon,
                        testTag = "${tagPrefix}_opt_gallery",
                        onClick = {
                            showActionMenu = false
                            galleryLauncher.launch("image/*")
                        }
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // 2. Browse Files
                    UploadOptionRow(
                        icon = Icons.Default.FolderOpen,
                        title = "Browse Files",
                        subtitle = "Select image file from storage / downloads",
                        color = CyanNeon,
                        testTag = "${tagPrefix}_opt_browse",
                        onClick = {
                            showActionMenu = false
                            galleryLauncher.launch("image/*")
                        }
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // 3. Take Photo (Camera)
                    UploadOptionRow(
                        icon = Icons.Default.CameraAlt,
                        title = "Take Photo",
                        subtitle = "Capture action figure or artwork with camera",
                        color = GoldNeon,
                        testTag = "${tagPrefix}_opt_camera",
                        onClick = {
                            showActionMenu = false
                            cameraLauncher.launch(null)
                        }
                    )
                }
            },
            confirmButton = {},
            dismissButton = {
                TextButton(
                    onClick = { showActionMenu = false },
                    modifier = Modifier.testTag("${tagPrefix}_opt_cancel")
                ) {
                    Text("Cancel", color = TextSecondary)
                }
            },
            containerColor = ArenaSurfaceDark,
            shape = RoundedCornerShape(16.dp)
        )
    }

    // Crop Editor Modal
    if (showCropDialog && pendingBitmapForCrop != null) {
        ImageCropEditorModal(
            rawBitmap = pendingBitmapForCrop!!,
            initialAspectRatio = CropAspectRatio.SQUARE_1_1,
            onConfirm = { croppedBitmap ->
                showCropDialog = false
                pendingBitmapForCrop = null

                // Save to storage with progress feedback
                coroutineScope.launch {
                    onImageStateChange(imageState.copy(uploadStatus = UploadStatus.UPLOADING, progress = 0.1f))
                    storageService.saveImageWithProgress(croppedBitmap, categoryTag = tagPrefix).collect { (prog, metadata) ->
                        if (metadata != null) {
                            onImageStateChange(
                                imageState.copy(
                                    imageUri = metadata.fileUriString,
                                    uploadStatus = UploadStatus.SUCCESS,
                                    progress = 1.0f,
                                    sizeBytes = metadata.sizeBytes,
                                    isCropped = true,
                                    errorMessage = null
                                )
                            )
                            Toast.makeText(context, "Image uploaded successfully!", Toast.LENGTH_SHORT).show()
                        } else {
                            onImageStateChange(imageState.copy(uploadStatus = UploadStatus.UPLOADING, progress = prog))
                        }
                    }
                }
            },
            onCancel = {
                showCropDialog = false
                pendingBitmapForCrop = null
                onImageStateChange(imageState.copy(uploadStatus = UploadStatus.IDLE))
            }
        )
    }
}

@Composable
private fun UploadOptionRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
    color: Color,
    testTag: String,
    onClick: () -> Unit
) {
    Surface(
        color = ArenaSurfaceElevated,
        shape = RoundedCornerShape(10.dp),
        border = ButtonDefaults.outlinedButtonBorder.copy(
            brush = androidx.compose.ui.graphics.SolidColor(ArenaBorder)
        ),
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .testTag(testTag)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .background(color.copy(alpha = 0.15f), CircleShape)
                    .border(1.dp, color.copy(alpha = 0.5f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = icon, contentDescription = null, tint = color, modifier = Modifier.size(20.dp))
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(text = title, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                Text(text = subtitle, fontSize = 10.sp, color = TextMuted)
            }
        }
    }
}

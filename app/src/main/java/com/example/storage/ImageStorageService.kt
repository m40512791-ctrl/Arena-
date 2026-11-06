package com.example.storage

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.util.UUID

data class StoredImageMetadata(
    val id: String,
    val filePath: String,
    val fileUriString: String,
    val fileName: String,
    val mimeType: String,
    val sizeBytes: Long,
    val width: Int,
    val height: Int,
    val createdAt: Long = System.currentTimeMillis()
)

sealed class ImageValidationResult {
    data class Success(
        val bitmap: Bitmap,
        val mimeType: String,
        val originalSize: Long,
        val fileName: String
    ) : ImageValidationResult()

    data class Error(val message: String) : ImageValidationResult()
}

class ImageStorageService(private val context: Context) {

    private val imagesDir: File by lazy {
        File(context.filesDir, "arena_uploaded_images").apply {
            if (!exists()) mkdirs()
        }
    }

    companion object {
        const val DEFAULT_MAX_SIZE_BYTES = 10 * 1024 * 1024L // 10 MB
        val SUPPORTED_MIME_TYPES = setOf(
            "image/jpeg",
            "image/jpg",
            "image/png",
            "image/webp"
        )
    }

    /**
     * Validates file size, format and decodes the image bitmap safely.
     */
    suspend fun validateAndDecode(
        uri: Uri,
        maxSizeBytes: Long = DEFAULT_MAX_SIZE_BYTES
    ): ImageValidationResult = withContext(Dispatchers.IO) {
        try {
            val contentResolver = context.contentResolver
            val mimeType = contentResolver.getType(uri)?.lowercase() ?: run {
                // Infer from path if null
                val path = uri.path?.lowercase().orEmpty()
                when {
                    path.endsWith(".jpg") || path.endsWith(".jpeg") -> "image/jpeg"
                    path.endsWith(".png") -> "image/png"
                    path.endsWith(".webp") -> "image/webp"
                    else -> "unknown"
                }
            }

            if (mimeType !in SUPPORTED_MIME_TYPES && mimeType != "unknown") {
                return@withContext ImageValidationResult.Error(
                    "Unsupported image format: '$mimeType'. Please upload JPG, PNG, or WEBP."
                )
            }

            // Check file size
            var fileSize = 0L
            contentResolver.openFileDescriptor(uri, "r")?.use { pfd ->
                fileSize = pfd.statSize
            }
            if (fileSize <= 0) {
                // Fallback stream length measurement
                contentResolver.openInputStream(uri)?.use { stream ->
                    fileSize = stream.available().toLong()
                }
            }

            if (fileSize > maxSizeBytes) {
                val sizeMb = fileSize / (1024 * 1024.0)
                return@withContext ImageValidationResult.Error(
                    "File size (%.1f MB) exceeds the 10 MB limit. Please select a smaller image.".format(sizeMb)
                )
            }

            // Decode image and verify integrity
            val options = BitmapFactory.Options().apply { inJustDecodeBounds = true }
            contentResolver.openInputStream(uri)?.use { stream ->
                BitmapFactory.decodeStream(stream, null, options)
            }

            if (options.outWidth <= 0 || options.outHeight <= 0) {
                return@withContext ImageValidationResult.Error(
                    "Corrupted or invalid image file. Unable to decode image data."
                )
            }

            // Downsample if huge to prevent OutOfMemory
            var sampleSize = 1
            val maxDimension = 2560
            while ((options.outWidth / sampleSize) > maxDimension || (options.outHeight / sampleSize) > maxDimension) {
                sampleSize *= 2
            }

            val decodeOptions = BitmapFactory.Options().apply {
                inSampleSize = sampleSize
                inPreferredConfig = Bitmap.Config.ARGB_8888
            }

            val bitmap = contentResolver.openInputStream(uri)?.use { stream ->
                BitmapFactory.decodeStream(stream, null, decodeOptions)
            } ?: return@withContext ImageValidationResult.Error("Could not decode image stream.")

            val fileName = uri.lastPathSegment ?: "uploaded_image"

            ImageValidationResult.Success(
                bitmap = bitmap,
                mimeType = if (mimeType == "unknown") "image/jpeg" else mimeType,
                originalSize = fileSize,
                fileName = fileName
            )
        } catch (e: Exception) {
            ImageValidationResult.Error("Failed to process image: ${e.localizedMessage ?: "Unknown error"}")
        }
    }

    /**
     * Saves a bitmap to persistent storage with unique name, compression, and returns its metadata.
     */
    suspend fun saveImage(
        bitmap: Bitmap,
        categoryTag: String = "char"
    ): StoredImageMetadata = withContext(Dispatchers.IO) {
        val uniqueId = UUID.randomUUID().toString()
        val fileName = "img_${categoryTag}_$uniqueId.webp"
        val destFile = File(imagesDir, fileName)

        // Compress to WebP / JPEG
        val outputStream = ByteArrayOutputStream()
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
            bitmap.compress(Bitmap.CompressFormat.WEBP_LOSSY, 88, outputStream)
        } else {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, outputStream)
        }
        val bytes = outputStream.toByteArray()

        FileOutputStream(destFile).use { fos ->
            fos.write(bytes)
            fos.flush()
        }

        StoredImageMetadata(
            id = uniqueId,
            filePath = destFile.absolutePath,
            fileUriString = "file://${destFile.absolutePath}",
            fileName = fileName,
            mimeType = "image/webp",
            sizeBytes = bytes.size.toLong(),
            width = bitmap.width,
            height = bitmap.height
        )
    }

    /**
     * Simulates progress and uploads bitmap with progress feedback.
     */
    fun saveImageWithProgress(
        bitmap: Bitmap,
        categoryTag: String = "char"
    ): Flow<Pair<Float, StoredImageMetadata?>> = flow {
        emit(0.15f to null)
        delay(120)
        emit(0.45f to null)
        delay(150)
        emit(0.80f to null)
        val metadata = saveImage(bitmap, categoryTag)
        delay(100)
        emit(1.0f to metadata)
    }

    /**
     * Deletes a previously stored image file.
     */
    fun deleteImage(filePath: String): Boolean {
        return try {
            val file = File(filePath)
            if (file.exists() && file.parentFile?.canonicalPath == imagesDir.canonicalPath) {
                file.delete()
            } else {
                false
            }
        } catch (e: Exception) {
            false
        }
    }
}

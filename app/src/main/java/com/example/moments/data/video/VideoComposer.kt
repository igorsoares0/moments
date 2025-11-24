package com.example.moments.data.video

import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.media3.common.MediaItem
import androidx.media3.common.util.UnstableApi
import androidx.media3.transformer.Composition
import androidx.media3.transformer.EditedMediaItem
import androidx.media3.transformer.EditedMediaItemSequence
import androidx.media3.transformer.ExportException
import androidx.media3.transformer.ExportResult
import androidx.media3.transformer.ProgressHolder
import androidx.media3.transformer.Transformer
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@UnstableApi
class VideoComposer(private val context: Context) {

    sealed class CompositionState {
        data object Idle : CompositionState()
        data class Progress(val percentage: Int) : CompositionState()
        data class Success(val uri: Uri) : CompositionState()
        data class Error(val message: String) : CompositionState()
    }

    companion object {
        /**
         * Salva um vídeo da URI (arquivo em cache) para a galeria do dispositivo
         */
        fun saveVideoToGallery(context: Context, videoUri: Uri): Uri? {
            try {
                // Obtém o arquivo do URI
                val videoFile = if (videoUri.scheme == "file") {
                    File(videoUri.path!!)
                } else {
                    return null
                }

                if (!videoFile.exists()) {
                    return null
                }

                val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
                val fileName = "MOMENTS_$timestamp.mp4"

                val contentValues = ContentValues().apply {
                    put(MediaStore.Video.Media.DISPLAY_NAME, fileName)
                    put(MediaStore.Video.Media.MIME_TYPE, "video/mp4")

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        put(MediaStore.Video.Media.RELATIVE_PATH,
                            Environment.DIRECTORY_MOVIES + "/Moments")
                        put(MediaStore.Video.Media.IS_PENDING, 1)
                    }
                }

                val resolver = context.contentResolver
                val uri = resolver.insert(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, contentValues)

                uri?.let {
                    resolver.openOutputStream(it)?.use { output ->
                        videoFile.inputStream().use { input ->
                            input.copyTo(output)
                        }
                    }

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        contentValues.clear()
                        contentValues.put(MediaStore.Video.Media.IS_PENDING, 0)
                        resolver.update(it, contentValues, null, null)
                    }

                    return it
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

            return null
        }
    }

    fun composeVideo(
        mediaItems: List<com.example.moments.data.models.MediaItem>,
        durations: List<Float>
    ): Flow<CompositionState> = flow {
        emit(CompositionState.Progress(0))

        try {
            val outputFile = createOutputFile()
            var completed = false
            var errorOccurred = false
            var resultUri: Uri? = null

            val transformer = Transformer.Builder(context)
                .setVideoMimeType("video/avc") // H.264 codec
                .addListener(object : Transformer.Listener {
                    override fun onCompleted(composition: Composition, exportResult: ExportResult) {
                        completed = true
                        // Retorna URI do arquivo em cache (não salva na galeria ainda)
                        resultUri = Uri.fromFile(outputFile)
                    }

                    override fun onError(
                        composition: Composition,
                        exportResult: ExportResult,
                        exportException: ExportException
                    ) {
                        errorOccurred = true
                        exportException.printStackTrace()
                    }
                })
                .build()

            // Criar EditedMediaItems
            val editedItems = mediaItems.mapIndexed { index, item ->
                val durationMs = (durations.getOrNull(index) ?: 3.0f) * 1000

                val mediaItem = if (item.isVideo) {
                    // Para vídeos
                    MediaItem.Builder()
                        .setUri(item.uri)
                        .setClippingConfiguration(
                            MediaItem.ClippingConfiguration.Builder()
                                .setStartPositionMs(0)
                                .setEndPositionMs(durationMs.toLong())
                                .build()
                        )
                        .build()
                } else {
                    // Para imagens
                    MediaItem.Builder()
                        .setUri(item.uri)
                        .setImageDurationMs(durationMs.toLong())
                        .build()
                }

                if (item.isVideo) {
                    EditedMediaItem.Builder(mediaItem).build()
                } else {
                    // CRÍTICO: setFrameRate é obrigatório para imagens!
                    EditedMediaItem.Builder(mediaItem)
                        .setFrameRate(30)
                        .setRemoveAudio(true)
                        .build()
                }
            }

            // Criar composição
            val sequence = EditedMediaItemSequence(editedItems)
            val composition = Composition.Builder(listOf(sequence))
                .setTransmuxVideo(false)
                .setTransmuxAudio(false)
                .experimentalSetForceAudioTrack(true) // CRÍTICO para misturar imagens e vídeos
                .build()

            // Iniciar transformação
            transformer.start(composition, outputFile.absolutePath)

            // Monitorar progresso na Main thread
            val progressHolder = ProgressHolder()
            while (!completed && !errorOccurred) {
                val state = transformer.getProgress(progressHolder)
                when (state) {
                    Transformer.PROGRESS_STATE_AVAILABLE -> {
                        val progress = (progressHolder.progress * 100).toInt()
                        emit(CompositionState.Progress(progress))
                    }
                }
                delay(100)
            }

            if (errorOccurred) {
                emit(CompositionState.Error("Erro ao criar vídeo"))
            } else if (resultUri != null) {
                emit(CompositionState.Success(resultUri!!))
            }

        } catch (e: Exception) {
            e.printStackTrace()
            emit(CompositionState.Error(e.message ?: "Erro desconhecido"))
        }
    }

    private fun createOutputFile(): File {
        val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val fileName = "MOMENTS_$timestamp.mp4"
        return File(context.cacheDir, fileName)
    }
}

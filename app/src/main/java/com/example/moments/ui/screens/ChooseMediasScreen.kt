package com.example.moments.ui.screens

import android.Manifest
import android.content.ContentUris
import android.content.Context
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.moments.data.models.MediaItem
import com.example.moments.data.models.Template
import com.example.moments.ui.components.MediaGridItem
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.media3.common.util.UnstableApi
import com.example.moments.viewmodel.VideoCompositionViewModel
import com.example.moments.data.video.VideoComposer
import androidx.compose.runtime.collectAsState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.TextButton

@OptIn(ExperimentalPermissionsApi::class, UnstableApi::class)
@Composable
fun ChooseMediasScreen(
    template: Template,
    onClose: () -> Unit = {},
    onContinue: (List<MediaItem>) -> Unit = {},
    onVideoCreated: (Uri) -> Unit = {}
) {
    val context = LocalContext.current
    val mediaItems = remember { mutableStateListOf<MediaItem>() }
    var selectedCount by remember { mutableStateOf(0) }
    val selectedMediasInOrder = remember { mutableStateListOf<MediaItem>() }

    val viewModel: VideoCompositionViewModel = viewModel()
    val compositionState by viewModel.compositionState.collectAsState()

    val permissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        listOf(
            Manifest.permission.READ_MEDIA_IMAGES,
            Manifest.permission.READ_MEDIA_VIDEO
        )
    } else {
        listOf(Manifest.permission.READ_EXTERNAL_STORAGE)
    }

    val permissionsState = rememberMultiplePermissionsState(permissions)

    LaunchedEffect(permissionsState.allPermissionsGranted) {
        if (permissionsState.allPermissionsGranted) {
            mediaItems.clear()
            mediaItems.addAll(loadMediaFromGallery(context))
        } else {
            permissionsState.launchMultiplePermissionRequest()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0F0F0F))
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Top bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .windowInsetsPadding(WindowInsets.statusBars)
                    .padding(16.dp)
            ) {
                IconButton(
                    onClick = onClose,
                    modifier = Modifier.align(Alignment.CenterStart)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Text(
                    text = "Choose in your gallery",
                    color = Color.White,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Normal,
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            // Media Grid
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentPadding = PaddingValues(bottom = 140.dp),
                horizontalArrangement = Arrangement.spacedBy(1.dp),
                verticalArrangement = Arrangement.spacedBy(1.dp)
            ) {
                items(mediaItems) { item ->
                    MediaGridItem(
                        mediaItem = item,
                        onClick = {
                            val index = mediaItems.indexOf(item)
                            if (index != -1) {
                                if (item.isSelected) {
                                    // Deselect
                                    mediaItems[index] = item.copy(isSelected = false)
                                    selectedMediasInOrder.remove(item)
                                } else {
                                    // Select only if we haven't reached the limit
                                    if (selectedMediasInOrder.size < template.momentsCount) {
                                        mediaItems[index] = item.copy(isSelected = true)
                                        selectedMediasInOrder.add(item)
                                    }
                                }
                                selectedCount = selectedMediasInOrder.size
                            }
                        }
                    )
                }
            }

            // Bottom bar with duration indicators and continue button
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF0F0F0F))
                    .windowInsetsPadding(WindowInsets.navigationBars)
                    .padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 20.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // Counter text and button row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "${selectedCount}/${template.momentsCount} moments",
                            color = Color.White.copy(alpha = 0.5f),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Normal
                        )

                        // Continue button
                        Button(
                            onClick = {
                                if (selectedCount == template.momentsCount) {
                                    viewModel.createVideo(
                                        context = context,
                                        mediaItems = selectedMediasInOrder.toList(),
                                        durations = template.momentDurations
                                    )
                                }
                            },
                            enabled = selectedCount == template.momentsCount,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF8B5CF6),
                                disabledContainerColor = Color(0xFF8B5CF6).copy(alpha = 0.3f)
                            ),
                            shape = CircleShape,
                            modifier = Modifier.size(50.dp),
                            contentPadding = PaddingValues(0.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.ArrowForward,
                                contentDescription = "Continue",
                                tint = Color.White,
                                modifier = Modifier.size(26.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Duration boxes row with horizontal scroll
                    if (template.momentDurations.isNotEmpty()) {
                        Row(
                            modifier = Modifier.horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            template.momentDurations.forEachIndexed { index, duration ->
                                Box(
                                    modifier = Modifier
                                        .size(width = 64.dp, height = 100.dp)
                                        .background(
                                            color = Color(0xFF1C1C1C),
                                            shape = RoundedCornerShape(8.dp)
                                        )
                                        .clip(RoundedCornerShape(8.dp))
                                ) {
                                    // Display selected media if available
                                    if (index < selectedMediasInOrder.size) {
                                        AsyncImage(
                                            model = selectedMediasInOrder[index].uri,
                                            contentDescription = null,
                                            modifier = Modifier.fillMaxSize(),
                                            contentScale = ContentScale.Crop
                                        )
                                    }

                                    // Duration text overlay
                                    Box(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .background(
                                                color = if (index < selectedMediasInOrder.size)
                                                    Color.Black.copy(alpha = 0.4f)
                                                else Color.Transparent
                                            ),
                                        contentAlignment = Alignment.BottomCenter
                                    ) {
                                        Text(
                                            text = "${duration}s",
                                            color = Color.White,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Normal,
                                            modifier = Modifier.padding(bottom = 12.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Progress/Success/Error Dialogs
        when (val state = compositionState) {
            is VideoComposer.CompositionState.Progress -> {
                AlertDialog(
                    onDismissRequest = { },
                    title = { Text("Creating video", color = Color.White) },
                    text = {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            CircularProgressIndicator(
                                color = Color(0xFF8B5CF6),
                                modifier = Modifier.padding(16.dp)
                            )
                            Text(
                                text = "${state.percentage}%",
                                color = Color.White,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    },
                    confirmButton = { },
                    containerColor = Color(0xFF1A1A1A)
                )
            }
            is VideoComposer.CompositionState.Success -> {
                LaunchedEffect(Unit) {
                    viewModel.resetState()
                    onVideoCreated(state.uri)
                }
            }
            is VideoComposer.CompositionState.Error -> {
                AlertDialog(
                    onDismissRequest = { viewModel.resetState() },
                    title = { Text("Error", color = Color.White) },
                    text = { Text(state.message, color = Color.White) },
                    confirmButton = {
                        TextButton(onClick = { viewModel.resetState() }) {
                            Text("OK", color = Color(0xFF8B5CF6))
                        }
                    },
                    containerColor = Color(0xFF1A1A1A)
                )
            }
            else -> { }
        }
    }
}

fun loadMediaFromGallery(context: Context): List<MediaItem> {
    val mediaList = mutableListOf<MediaItem>()

    val projection = arrayOf(
        MediaStore.Files.FileColumns._ID,
        MediaStore.Files.FileColumns.MEDIA_TYPE,
        MediaStore.Files.FileColumns.DURATION
    )

    val selection = "${MediaStore.Files.FileColumns.MEDIA_TYPE}=? OR ${MediaStore.Files.FileColumns.MEDIA_TYPE}=?"
    val selectionArgs = arrayOf(
        MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE.toString(),
        MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO.toString()
    )

    val sortOrder = "${MediaStore.Files.FileColumns.DATE_ADDED} DESC"

    context.contentResolver.query(
        MediaStore.Files.getContentUri("external"),
        projection,
        selection,
        selectionArgs,
        sortOrder
    )?.use { cursor ->
        val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns._ID)
        val mediaTypeColumn = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.MEDIA_TYPE)
        val durationColumn = cursor.getColumnIndex(MediaStore.Files.FileColumns.DURATION)

        while (cursor.moveToNext()) {
            val id = cursor.getLong(idColumn)
            val mediaType = cursor.getInt(mediaTypeColumn)
            val duration = if (durationColumn >= 0) cursor.getLong(durationColumn) else 0L

            val contentUri: Uri = if (mediaType == MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE) {
                ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id)
            } else {
                ContentUris.withAppendedId(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, id)
            }

            mediaList.add(
                MediaItem(
                    id = id,
                    uri = contentUri,
                    isVideo = mediaType == MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO,
                    duration = duration
                )
            )
        }
    }

    return mediaList
}

package com.example.moments.data.models

import android.net.Uri

data class MediaItem(
    val id: Long,
    val uri: Uri,
    val isVideo: Boolean,
    val duration: Long = 0,
    val isSelected: Boolean = false
)

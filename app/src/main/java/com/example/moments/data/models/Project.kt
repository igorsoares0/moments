package com.example.moments.data.models

import android.net.Uri
import java.util.Date

data class Project(
    val id: String,
    val name: String,
    val videoUri: Uri,
    val thumbnailUri: Uri,
    val template: Template,
    val mediaItems: List<MediaItem>,
    val createdAt: Date
)

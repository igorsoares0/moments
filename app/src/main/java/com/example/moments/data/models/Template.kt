package com.example.moments.data.models

data class Template(
    val id: Int,
    val title: String,
    val thumbnailResId: Int,
    val momentsCount: Int,
    val durationSeconds: Int,
    val category: TemplateCategory
)

enum class TemplateCategory {
    FEATURE,
    NEW,
    MOST_VIEWED
}

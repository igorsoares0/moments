package com.example.moments.data.models

data class MediaProject(
    val items: List<MediaItem> = emptyList(),
    val template: Template,
    val maxDurationMs: Long = 40_000 // 40 segundos máximo
) {
    fun getTotalDurationMs(): Long {
        return template.momentDurations.sum().toLong() * 1000
    }

    fun isValid(): Boolean {
        return items.size == template.momentsCount && getTotalDurationMs() <= maxDurationMs
    }
}

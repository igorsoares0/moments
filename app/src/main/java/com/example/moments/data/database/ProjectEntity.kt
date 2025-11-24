package com.example.moments.data.database

import android.net.Uri
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.example.moments.data.models.MediaItem
import com.example.moments.data.models.Project
import com.example.moments.data.models.Template
import com.example.moments.data.models.TemplateCategory
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.util.Date

@Entity(tableName = "projects")
@TypeConverters(Converters::class)
data class ProjectEntity(
    @PrimaryKey val id: String,
    val name: String,
    val videoUriString: String,
    val thumbnailUriString: String,
    val templateId: Int,
    val templateTitle: String,
    val templateThumbnailResId: Int,
    val templateMomentsCount: Int,
    val templateDurationSeconds: Int,
    val templateCategory: String,
    val templateMomentDurations: String, // JSON array
    val mediaItemsJson: String, // JSON array
    val createdAt: Long
) {
    fun toProject(): Project {
        val gson = Gson()
        val durationsType = object : TypeToken<List<Float>>() {}.type
        val mediasType = object : TypeToken<List<MediaItemData>>() {}.type

        val durations: List<Float> = gson.fromJson(templateMomentDurations, durationsType)
        val mediaItemsData: List<MediaItemData> = gson.fromJson(mediaItemsJson, mediasType)

        val template = Template(
            id = templateId,
            title = templateTitle,
            thumbnailResId = templateThumbnailResId,
            momentsCount = templateMomentsCount,
            durationSeconds = templateDurationSeconds,
            category = TemplateCategory.valueOf(templateCategory),
            momentDurations = durations
        )

        val mediaItems = mediaItemsData.map {
            MediaItem(
                id = it.id,
                uri = Uri.parse(it.uriString),
                isVideo = it.isVideo,
                duration = it.duration,
                isSelected = it.isSelected
            )
        }

        return Project(
            id = id,
            name = name,
            videoUri = Uri.parse(videoUriString),
            thumbnailUri = Uri.parse(thumbnailUriString),
            template = template,
            mediaItems = mediaItems,
            createdAt = Date(createdAt)
        )
    }

    companion object {
        fun fromProject(project: Project): ProjectEntity {
            val gson = Gson()

            val mediaItemsData = project.mediaItems.map {
                MediaItemData(
                    id = it.id,
                    uriString = it.uri.toString(),
                    isVideo = it.isVideo,
                    duration = it.duration,
                    isSelected = it.isSelected
                )
            }

            return ProjectEntity(
                id = project.id,
                name = project.name,
                videoUriString = project.videoUri.toString(),
                thumbnailUriString = project.thumbnailUri.toString(),
                templateId = project.template.id,
                templateTitle = project.template.title,
                templateThumbnailResId = project.template.thumbnailResId,
                templateMomentsCount = project.template.momentsCount,
                templateDurationSeconds = project.template.durationSeconds,
                templateCategory = project.template.category.name,
                templateMomentDurations = gson.toJson(project.template.momentDurations),
                mediaItemsJson = gson.toJson(mediaItemsData),
                createdAt = project.createdAt.time
            )
        }
    }
}

data class MediaItemData(
    val id: Long,
    val uriString: String,
    val isVideo: Boolean,
    val duration: Long,
    val isSelected: Boolean
)

class Converters {
    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return value?.let { Date(it) }
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time
    }
}

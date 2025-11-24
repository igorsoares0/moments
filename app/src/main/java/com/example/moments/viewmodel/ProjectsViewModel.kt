package com.example.moments.viewmodel

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.moments.data.database.MomentsDatabase
import com.example.moments.data.models.MediaItem
import com.example.moments.data.models.Project
import com.example.moments.data.models.Template
import com.example.moments.data.repository.ProjectRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

class ProjectsViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: ProjectRepository
    private val _projects = MutableStateFlow<List<Project>>(emptyList())
    val projects: StateFlow<List<Project>> = _projects.asStateFlow()

    init {
        val projectDao = MomentsDatabase.getDatabase(application).projectDao()
        repository = ProjectRepository(projectDao)

        viewModelScope.launch {
            repository.getAllProjects().collect { projectsList ->
                _projects.value = projectsList
            }
        }
    }

    fun saveProject(
        videoUri: Uri,
        template: Template,
        mediaItems: List<MediaItem>
    ) {
        viewModelScope.launch {
            val timestamp = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).format(Date())
            val project = Project(
                id = UUID.randomUUID().toString(),
                name = "your projects name",
                videoUri = videoUri,
                thumbnailUri = mediaItems.firstOrNull()?.uri ?: videoUri,
                template = template,
                mediaItems = mediaItems,
                createdAt = Date()
            )
            repository.insertProject(project)
        }
    }

    fun deleteProject(project: Project) {
        viewModelScope.launch {
            repository.deleteProject(project)
        }
    }

    fun updateProjectName(project: Project, newName: String) {
        viewModelScope.launch {
            val updatedProject = project.copy(name = newName)
            repository.updateProject(updatedProject)
        }
    }
}

package com.example.moments.data.repository

import com.example.moments.data.database.ProjectDao
import com.example.moments.data.database.ProjectEntity
import com.example.moments.data.models.Project
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ProjectRepository(private val projectDao: ProjectDao) {

    fun getAllProjects(): Flow<List<Project>> {
        return projectDao.getAllProjects().map { entities ->
            entities.map { it.toProject() }
        }
    }

    suspend fun getProjectById(projectId: String): Project? {
        return projectDao.getProjectById(projectId)?.toProject()
    }

    suspend fun insertProject(project: Project) {
        projectDao.insertProject(ProjectEntity.fromProject(project))
    }

    suspend fun updateProject(project: Project) {
        projectDao.updateProject(ProjectEntity.fromProject(project))
    }

    suspend fun deleteProject(project: Project) {
        projectDao.deleteProjectById(project.id)
    }
}

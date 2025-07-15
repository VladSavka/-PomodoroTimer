package org.timer.main.domain.project

import kotlinx.coroutines.flow.*

actual class FirebaseProjectsGateway : ProjectsGateway {
    actual override fun getProjects(): Flow<List<Project>> {
        TODO("Not yet implemented")
    }

    actual override suspend fun getProjectById(projectId: String): Project {
        TODO("Not yet implemented")
    }

    actual override suspend fun saveProject(project: Project) {
        TODO("Not yet implemented")
    }

    actual override suspend fun updateProject(project: Project) {
        TODO("Not yet implemented")
    }

    actual override suspend fun updateProjects(projects: List<Project>) {
        TODO("Not yet implemented")
    }

    actual override suspend fun removeProjectById(id: String) {
        TODO("Not yet implemented")
    }


}
package org.timer.main.domain.project

import com.diamondedge.logging.*
import kotlinx.coroutines.flow.*

class InMemoryProjectsGateway : ProjectsGateway {
    private val projects = mutableListOf<Project>()

    private val flow = MutableStateFlow<List<Project>>(emptyList())
    override fun getProjects(): Flow<List<Project>> = flow.asStateFlow()

    override suspend fun saveProject(project: Project) {
        if (projects.contains(project)) {
            throw IllegalArgumentException("Project already exists $project")
        }
        projects.add(project)
        logging().debug { "Saved projects: $projects" }
        flow.emit(projects.deepCopy())
    }

    override suspend fun updateProject(project: Project) {
        val index = projects.indexOfFirst { it.id == project.id }
        projects[index] = project
        flow.update { projects.deepCopy() }
    }


    override suspend fun removeProjectById(id: Long) {
        val project = projects.first { it.id == id }
        projects.remove(project)
        flow.emit(projects.deepCopy())
    }

    override fun getProjectById(projectId: Long) = projects.first { it.id == projectId }


}
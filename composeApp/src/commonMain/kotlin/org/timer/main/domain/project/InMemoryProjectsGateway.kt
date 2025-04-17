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
        projects[index]= project
        flow.update { projects.deepCopy() }
    }


    override suspend fun removeProjectById(id: Long) {
        val project = projects.first { it.id == id }
        projects.remove(project)
        flow.emit(projects.deepCopy())
    }

    override fun getProjectById(projectId: Long) = projects.first { it.id == projectId }

    private fun List<Project>.deepCopy(): List<Project> {
        val newList = mutableListOf<Project>()
        this.forEach {
            newList.add(Project(it.id, it.title, addTasks(it.tasks)))
        }
        return newList
    }

    private fun addTasks(tasks: MutableList<Task>): MutableList<Task> {
        val newList = mutableListOf<Task>()
        tasks.forEach {
            newList.add(Task(it.id, it.description, it.isDone))
        }
        return newList
    }
}
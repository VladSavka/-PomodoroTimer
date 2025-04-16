package org.timer.main.data

import kotlinx.coroutines.flow.*
import kotlinx.datetime.*
import org.timer.main.data.entity.*

class ProjectsGateway {
    private val flow = MutableStateFlow<List<Project>>(emptyList())

    fun getProjects(): Flow<List<Project>> = flow.asStateFlow()

    private fun genereateId() = Clock.System.now().toEpochMilliseconds()

    fun addProject(name: String) {
        flow.update {  projects ->
            projects.plus(Project(genereateId(), name, emptyList()))
        }
    }

    fun removeProjectById(id: Long) {
        flow.update { projects ->
            projects.filter { it.id != id }
        }
    }
}
package org.timer.main.domain.project

import org.timer.main.domain.*

class AddTaskUseCase(private val projectsGateway: ProjectsGateway) {

    suspend operator fun invoke(projectId: Long, taskDescription: String) {
        val project = projectsGateway.getProjectById(projectId)
        project.tasks.add(Task(generateID(), taskDescription, false))
        projectsGateway.updateProject(project)
    }
}
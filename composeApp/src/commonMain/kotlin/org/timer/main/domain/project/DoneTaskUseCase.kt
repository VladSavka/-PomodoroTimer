package org.timer.main.domain.project

class DoneTaskUseCase(private val projectsGateway: ProjectsGateway) {

    suspend operator fun invoke(projectId: String, taskId: Long) {
        val project = projectsGateway.getProjectById(projectId)
        project.tasks.forEach {
            if (it.id == taskId) it.isDone = true
        }
        projectsGateway.updateProject(project)
    }
}
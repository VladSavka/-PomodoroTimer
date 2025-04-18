package org.timer.main.domain.project

class UndoneTaskUseCase(private val projectsGateway: ProjectsGateway) {

    suspend operator fun invoke(projectId: Long, taskId: Long) {
        val project = projectsGateway.getProjectById(projectId)
        project.tasks.forEach {
            if (it.id == taskId) it.isDone = false
        }
        projectsGateway.updateProject(project)
    }
}
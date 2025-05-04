package org.timer.main.domain.project

class UpdateTaskDescriptionUseCase(private val projectsGateway: ProjectsGateway) {

    suspend operator fun invoke(projectId: Long, taskId: Long, name: String) {
        val project = projectsGateway.getProjectById(projectId)
        project.tasks.forEach {
            if (it.id == taskId) {
                it.description = name
            }
        }
        projectsGateway.updateProject(project)
    }
}
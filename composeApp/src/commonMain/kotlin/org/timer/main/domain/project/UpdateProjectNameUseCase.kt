package org.timer.main.domain.project

class UpdateProjectNameUseCase(private val projectsGateway: ProjectsGateway) {

    suspend operator fun invoke(id: Long, name: String) {
        val project = projectsGateway.getProjectById(id)
        project.name = name
        projectsGateway.updateProject(project)
    }
}
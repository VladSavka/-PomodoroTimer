package org.timer.main.data

class GetProjectsUseCase(private val projectsGateway: ProjectsGateway) {
    operator fun invoke() = projectsGateway.getProjects()
}
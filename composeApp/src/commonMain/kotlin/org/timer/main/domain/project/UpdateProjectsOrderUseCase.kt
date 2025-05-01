package org.timer.main.domain.project

import org.timer.main.domain.*

class UpdateProjectsOrderUseCase(private val projectsGateway: ProjectsGateway) {

    suspend operator fun invoke(projects: List<Project>) {
        projectsGateway.updateProjects(projects)
    }
}
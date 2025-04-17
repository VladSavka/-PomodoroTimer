package org.timer.main.domain.project

class RemoveProjectUseCase(private val projectsGateway: ProjectsGateway) {

    suspend operator fun invoke(id: Long) {
        projectsGateway.removeProjectById(id)
    }
}
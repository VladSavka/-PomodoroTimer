package org.timer.main.data

class RemoveProjectUseCase(private val projectsGateway: ProjectsGateway) {

     operator fun invoke(id: Long) {
         projectsGateway.removeProjectById(id)
    }
}
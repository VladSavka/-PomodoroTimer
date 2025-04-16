package org.timer.main.data

class AddProjectUseCase(private val projectsGateway: ProjectsGateway) {

     operator fun invoke(name: String) {
         projectsGateway.addProject(name)
    }
}
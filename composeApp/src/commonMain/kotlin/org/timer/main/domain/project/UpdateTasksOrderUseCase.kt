package org.timer.main.domain.project

import kotlinx.coroutines.flow.*

class UpdateTasksOrderUseCase(private val projectsGateway: ProjectsGateway) {

    suspend operator fun invoke(projectId: String, fromIndex: Int, toIndex: Int) {
        val project = projectsGateway.getProjects().first().first { it.id == projectId }
        val reorderedTasks = project.tasks.toMutableList().apply { add(toIndex, removeAt(fromIndex)) }
        val updatedProject = project.copy(tasks = reorderedTasks)
        projectsGateway.updateProject(updatedProject)
    }
}
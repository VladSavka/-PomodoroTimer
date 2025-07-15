package org.timer.main.domain.project

import kotlinx.coroutines.flow.*

class UpdateTasksOrderUseCase(private val projectsGateway: ProjectsGateway) {

    suspend operator fun invoke(projectId: String, fromIndex: Int, toIndex: Int) {
        val projects = projectsGateway.getProjects().first()
        val projectsWithReorderedTasks = projects.map {
            if (it.id == projectId) {
                it.copy(
                    tasks = it.tasks.toMutableList().apply { add(toIndex, removeAt(fromIndex)) })
            } else {
                it
            }
        }
        projectsGateway.updateProjects(projectsWithReorderedTasks)
    }
}
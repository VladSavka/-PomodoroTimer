package org.timer.main.domain.project

import com.diamondedge.logging.*

class DoneTaskUseCase(private val projectsGateway: ProjectsGateway) {

    suspend operator fun invoke(projectId: Long, taskId: Long) {
        val project = projectsGateway.getProjectById(projectId)
        project.tasks.forEach {
            if (it.id == taskId) it.isDone = true
        }
        projectsGateway.updateProject(project)
    }
}
package org.timer.main.projects

import org.timer.main.domain.project.*

data class ProjectsViewState(
    val projects: List<PresentableProject> = emptyList(),
    val projectName: String = ""
) {
    val isSubmitEnabled: Boolean
        get() = projectName.isNotBlank()
}


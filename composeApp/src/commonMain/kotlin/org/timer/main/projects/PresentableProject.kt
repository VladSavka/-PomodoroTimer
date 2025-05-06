package org.timer.main.projects

import org.timer.main.domain.project.*

data class PresentableProject(
    val id: Long,
    val name: String,
    val tasks: MutableList<Task>,
)
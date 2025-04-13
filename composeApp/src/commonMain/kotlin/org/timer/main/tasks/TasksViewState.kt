package org.timer.main.tasks

import org.timer.main.data.*

data class TasksViewState(
    val tasks: List<Task> = emptyList()
)
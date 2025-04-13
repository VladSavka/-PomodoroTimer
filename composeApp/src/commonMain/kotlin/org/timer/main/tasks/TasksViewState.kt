package org.timer.main.tasks

import org.timer.main.data.Task

data class TasksViewState(
    val tasks: List<Task> = emptyList()
)
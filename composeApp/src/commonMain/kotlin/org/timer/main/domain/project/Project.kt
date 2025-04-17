package org.timer.main.domain.project

data class Project(val id: Long, val title: String, val tasks: MutableList<Task>)

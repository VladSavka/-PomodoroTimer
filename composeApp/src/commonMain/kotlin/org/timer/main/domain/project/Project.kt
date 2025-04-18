package org.timer.main.domain.project

import kotlinx.serialization.*

@Serializable
data class Project(val id: Long, val title: String, val tasks: MutableList<Task>)

fun List<Project>.deepCopy(): List<Project> {
    val newList = mutableListOf<Project>()
    this.forEach {
        newList.add(Project(it.id, it.title, addTasks(it.tasks)))
    }
    return newList
}

private fun addTasks(tasks: MutableList<Task>): MutableList<Task> {
    val newList = mutableListOf<Task>()
    tasks.forEach {
        newList.add(Task(it.id, it.description, it.isDone))
    }
    return newList
}
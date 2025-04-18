package org.timer.main.domain.project

import kotlinx.serialization.*

@Serializable
data class Task(val id: Long, val description: String, var isDone: Boolean)

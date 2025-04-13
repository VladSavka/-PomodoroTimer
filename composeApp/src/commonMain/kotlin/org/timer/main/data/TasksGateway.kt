package org.timer.main.data

import kotlinx.coroutines.flow.*
import kotlinx.datetime.*

object TasksGateway {
    private val flow = MutableStateFlow(
        listOf(
            Task(
                id = Clock.System.now().toEpochMilliseconds(), "Task 1",
                listOf(
                    ToDo(
                        Clock.System.now().toEpochMilliseconds(),
                        "ToDo 1", false
                    ), ToDo(Clock.System.now().toEpochMilliseconds(), "ToDo 2", false)
                )
            ),
            Task(Clock.System.now().toEpochMilliseconds(), "Task 2", emptyList()),
            Task(Clock.System.now().toEpochMilliseconds(), "Task 3", emptyList()),
            Task(Clock.System.now().toEpochMilliseconds(), "Task 3", emptyList()),
            Task(Clock.System.now().toEpochMilliseconds(), "Task 3", emptyList()),
            Task(Clock.System.now().toEpochMilliseconds(), "Task 3", emptyList()),
            Task(Clock.System.now().toEpochMilliseconds(), "Task 3", emptyList()),
            Task(Clock.System.now().toEpochMilliseconds(), "Task 3", emptyList()),
            Task(Clock.System.now().toEpochMilliseconds(), "Task 3", emptyList()),
            Task(Clock.System.now().toEpochMilliseconds(), "Task 3", emptyList()),
            Task(Clock.System.now().toEpochMilliseconds(), "Task 3", emptyList()),
            Task(Clock.System.now().toEpochMilliseconds(), "Task 3", emptyList()),
            Task(Clock.System.now().toEpochMilliseconds(), "Task 3", emptyList()),
            Task(Clock.System.now().toEpochMilliseconds(), "Task 3", emptyList()),
            Task(Clock.System.now().toEpochMilliseconds(), "Task 3", emptyList()),
            Task(Clock.System.now().toEpochMilliseconds(), "Task 3", emptyList()),
            Task(Clock.System.now().toEpochMilliseconds(), "Task 3", emptyList()),
            Task(Clock.System.now().toEpochMilliseconds(), "Task 3", emptyList()),
            Task(Clock.System.now().toEpochMilliseconds(), "Task 3", emptyList()),
            Task(Clock.System.now().toEpochMilliseconds(), "Task 3", emptyList()),
            Task(Clock.System.now().toEpochMilliseconds(), "Task 3", emptyList()),
            Task(Clock.System.now().toEpochMilliseconds(), "Task 3", emptyList()),
            Task(Clock.System.now().toEpochMilliseconds(), "Task 3", emptyList()),
            Task(Clock.System.now().toEpochMilliseconds(), "Task 3", emptyList()),
            Task(Clock.System.now().toEpochMilliseconds(), "Task 3", emptyList()),
            Task(Clock.System.now().toEpochMilliseconds(), "Task 3", emptyList()),
            Task(Clock.System.now().toEpochMilliseconds(), "Task 3", emptyList()),
            Task(Clock.System.now().toEpochMilliseconds(), "Task 3", emptyList()),
            Task(Clock.System.now().toEpochMilliseconds(), "Task 3", emptyList()),
            Task(Clock.System.now().toEpochMilliseconds(), "Task 3", emptyList()),
            Task(Clock.System.now().toEpochMilliseconds(), "Task 3", emptyList()),
        )
    )

    fun getTasks(): Flow<List<Task>> = flow.asStateFlow()
}
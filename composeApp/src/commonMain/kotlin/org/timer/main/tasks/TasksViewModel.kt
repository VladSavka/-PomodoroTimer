package org.timer.main.tasks

import androidx.lifecycle.*
import kotlinx.coroutines.flow.*
import org.timer.main.data.*

class TasksViewModel : ViewModel() {
    private val _viewState = MutableStateFlow(TasksViewState())
    val viewState: StateFlow<TasksViewState> = _viewState.asStateFlow()

    init {
        TasksGateway.getTasks()
            .onEach { tasks ->
                _viewState.update { it.copy(tasks = tasks) }
            }
            .launchIn(viewModelScope)
    }
}
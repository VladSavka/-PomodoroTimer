package org.timer.main.tasks

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.diamondedge.logging.logging
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import org.timer.main.data.SettingsGateway
import org.timer.main.data.Task
import org.timer.main.data.TasksGateway
import org.timer.main.data.TimeSettings

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
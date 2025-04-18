package org.timer.main.projects

import androidx.lifecycle.*
import com.diamondedge.logging.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import org.timer.main.domain.project.*

class ProjectsViewModel(
    private val getProjectsUseCase: GetProjectsUseCase,
    private val addProjectUseCase: AddProjectUseCase,
    private val removeProjectUseCase: RemoveProjectUseCase,
    private val addTaskUseCase: AddTaskUseCase,
    private val doneTaskUseCase: DoneTaskUseCase,
    private val undoneTaskUseCase: UndoneTaskUseCase,
) : ViewModel() {
    private val _viewState = MutableStateFlow(ProjectsViewState())
    val viewState: StateFlow<ProjectsViewState> = _viewState.asStateFlow()

    init {
        getProjectsUseCase()
            .onEach { projects ->
                log.debug { "projects " + projects }
                _viewState.update { it.copy(projects = projects) }
            }
            .launchIn(viewModelScope)
    }

    fun onProjectTitleUpdate(title: String) {
        _viewState.update { it.copy(projectName = title) }
    }

    fun onAddProjectSubmitClick() = viewModelScope.launch {
        addProjectUseCase(viewState.value.projectName)
    }

    fun removeProject(id: Long) = viewModelScope.launch {
        removeProjectUseCase(id)
    }

    fun onTaskSubmit(projectID: Long, taskDescription: String) = viewModelScope.launch {
        addTaskUseCase(projectID, taskDescription)
    }

    fun onTaskDoneClick(projectId: Long, taskId: Long) = viewModelScope.launch {
        doneTaskUseCase(projectId, taskId)
    }

    fun onTaskUndoneClick(projectId: Long, taskId: Long) = viewModelScope.launch {
        undoneTaskUseCase(projectId, taskId)
    }

    companion object {
        val log = logging()
    }
}
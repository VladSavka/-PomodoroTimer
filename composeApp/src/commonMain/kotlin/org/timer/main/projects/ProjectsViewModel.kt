package org.timer.main.projects

import androidx.lifecycle.*
import kotlinx.coroutines.flow.*
import org.timer.main.data.*

class ProjectsViewModel(
    private val getProjectsUseCase: GetProjectsUseCase,
    private val addProjectUseCase: AddProjectUseCase,
    private val removeProjectUseCase: RemoveProjectUseCase

) : ViewModel() {
    private val _viewState = MutableStateFlow(ProjectsViewState())
    val viewState: StateFlow<ProjectsViewState> = _viewState.asStateFlow()

    init {
        getProjectsUseCase()
            .onEach { tasks ->
                _viewState.update { it.copy(projects = tasks) }
            }
            .launchIn(viewModelScope)
    }

    fun onProjectTitleUpdate(title: String) {
        _viewState.update { it.copy(projectName = title) }
    }

    fun onAddProjectSubmitClick() {
        addProjectUseCase(viewState.value.projectName)
    }

    fun removeProject(id: Long) {
        removeProjectUseCase(id)
    }
}
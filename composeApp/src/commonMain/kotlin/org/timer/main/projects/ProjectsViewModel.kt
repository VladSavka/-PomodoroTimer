package org.timer.main.projects

import androidx.lifecycle.*
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
    private val updateProjectsOrderUseCase: UpdateProjectsOrderUseCase,
    private val updateTasksOrderUseCase: UpdateTasksOrderUseCase,
    private val updateProjectNameUseCase: UpdateProjectNameUseCase,
    private val updateTaskDescriptionUseCase: UpdateTaskDescriptionUseCase,
    private val deleteTaskUseCase: DeleteTaskUseCase,
    private val moveTaskToTheEndOfListTaskUseCase: MoveTaskToTheEndOfListUseCase,
    private val deleteDoneTasksUseCase: DeleteAllDoneTasksUseCase
) : ViewModel() {
    private val _viewState = MutableStateFlow(ProjectsViewState())
    val viewState: StateFlow<ProjectsViewState> = _viewState.asStateFlow()

    init {
        getProjectsUseCase()
            .map { it.map { it.toPresentableProject() } }
            .onEach { projects ->
                _viewState.update { it.copy(projects = projects) }
            }
            .launchIn(viewModelScope)
    }

    private fun Project.toPresentableProject(): PresentableProject =
        PresentableProject(
            this.id,
            this.name,
            this.tasks
        )

    fun onProjectTitleUpdate(title: String) {
        _viewState.update { it.copy(projectName = title) }
    }

    fun onAddProjectSubmitClick() = viewModelScope.launch {
        addProjectUseCase(viewState.value.projectName)
    }

    fun removeProject(id: String) = viewModelScope.launch {
        removeProjectUseCase(id)
    }

    fun onTaskSubmitClick(projectID: String, taskDescription: String) = viewModelScope.launch {
        addTaskUseCase(projectID, taskDescription)
    }

    fun onTaskDoneClick(projectId: String, taskId: Long) = viewModelScope.launch {
        doneTaskUseCase(projectId, taskId)
        moveTaskToTheEndOfListTaskUseCase(projectId, taskId)
    }

    fun onTaskUndoneClick(projectId: String, taskId: Long) = viewModelScope.launch {
        undoneTaskUseCase(projectId, taskId)
    }

    fun onProjectsDrugAndDrop(fromIndex: Int, toIndex: Int) = viewModelScope.launch {
        updateProjectsOrderUseCase(fromIndex, toIndex)
    }

    fun onTasksDrugAndDrop(projectId: String, fromIndex: Int, toIndex: Int) = viewModelScope.launch {
        updateTasksOrderUseCase(projectId, fromIndex, toIndex)
    }

    fun onSubmitEditProjectName(id: String, name: String) = viewModelScope.launch {
        updateProjectNameUseCase(id, name)
    }

    fun onSubmitEditTaskDescription(projectId: String, taskId: Long, desc: String) =
        viewModelScope.launch {
            updateTaskDescriptionUseCase(projectId, taskId, desc)
        }

    fun onDeleteTaskClick(projectId: String, taskId: Long) = viewModelScope.launch {
        deleteTaskUseCase(projectId, taskId)
    }

    fun onDeleteDoneTasksClick(projectId: String) = viewModelScope.launch {
        deleteDoneTasksUseCase(projectId)
    }
}


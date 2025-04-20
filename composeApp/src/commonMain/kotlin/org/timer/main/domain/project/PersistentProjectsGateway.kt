package org.timer.main.domain.project


import com.russhwolf.settings.*
import com.russhwolf.settings.serialization.*
import kotlinx.coroutines.flow.*
import kotlinx.serialization.*


class PersistentProjectsGateway : ProjectsGateway {

    private val settings: Settings = Settings()
    private val flow = MutableStateFlow(getProjectListFromStorage())

    override fun getProjects(): Flow<List<Project>> {
        return flow.asStateFlow()
    }

    override fun getProjectById(projectId: Long): Project {
        return getProjectListFromStorage().first { it.id == projectId }
    }

    override suspend fun saveProject(project: Project) {
        val projectsList: List<Project> = getProjectListFromStorage()
        val newProjectList = projectsList + project
        saveProjectListToStorage(newProjectList)
        flow.emit(newProjectList.deepCopy())
    }


    override suspend fun updateProject(project: Project) {
        val projects = getProjectListFromStorage().toMutableList()
        val index = projects.indexOfFirst { it.id == project.id }
        projects[index] = project
        saveProjectListToStorage(projects)
        flow.update { projects.deepCopy() }
    }

    override suspend fun removeProjectById(id: Long) {
        val projects = getProjectListFromStorage().toMutableList()
        val projectToRemove = projects.first { it.id == id }
        projects.remove(projectToRemove)
        saveProjectListToStorage(projects)
        flow.emit(projects.deepCopy())
    }

    private fun saveProjectListToStorage(projects: List<Project>) {
        settings.encodeValue(ProjectListDto.serializer(), KEY, ProjectListDto(projects))
    }

    private fun getProjectListFromStorage(): List<Project> {
        return settings.decodeValue(
            ProjectListDto.serializer(),
            KEY,
            ProjectListDto(emptyList())
        ).projects
    }

    private companion object {
        private const val KEY = "ProjectListKey"

        @Serializable
        private data class ProjectListDto(val projects: List<Project>)
    }
}
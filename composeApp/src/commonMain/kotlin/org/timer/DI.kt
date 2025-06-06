package org.timer

import org.jetbrains.compose.resources.*
import org.koin.core.*
import org.koin.core.context.*
import org.koin.core.module.*
import org.koin.core.module.dsl.*
import org.koin.dsl.*
import org.timer.main.domain.project.*
import org.timer.main.projects.*
import org.timer.main.settings.*
import org.timer.main.timer.*
import org.timer.main.breakactivity.*
import org.timer.main.domain.settings.*
import org.timer.main.domain.timer.*

@ExperimentalResourceApi
fun appModule() = module {
    viewModel { SettingsViewModel(get(),get(),get()) }
    viewModel { BreakActivityViewModel() }
    viewModel { TimerViewModel(get(), get(), get(),get())}
    singleOf(::ProjectsViewModel)
    singleOf(::SettingsGateway)
    singleOf<ProjectsGateway>(::PersistentProjectsGateway)
    factoryOf(::GetProjectsUseCase)
    factoryOf(::AddProjectUseCase)
    factoryOf(::RemoveProjectUseCase)
    factoryOf(::AddTaskUseCase)
    factoryOf(::DoneTaskUseCase)
    factoryOf(::UndoneTaskUseCase)
    factoryOf(::UpdateProjectsOrderUseCase)
    factoryOf(::UpdateTasksOrderUseCase)
    factoryOf(::UpdateProjectNameUseCase)
    factoryOf(::UpdateTaskDescriptionUseCase)
    factoryOf(::DeleteTaskUseCase)
    factoryOf(::MoveTaskToTheEndOfListUseCase)
    factoryOf(::PlayAlarmUseCase)
    factoryOf(::CancelAlarmUseCase)
}

expect fun platformSpecificModule(): Module

@OptIn(ExperimentalResourceApi::class)
fun initializeKoin(
    conf: (KoinApplication.() -> Unit)? = null
) {

    startKoin {
        conf?.invoke(this)
        modules(appModule(), platformSpecificModule())
    }
}

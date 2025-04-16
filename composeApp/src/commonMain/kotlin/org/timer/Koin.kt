package org.timer

import org.jetbrains.compose.resources.*
import org.koin.core.*
import org.koin.core.context.*
import org.koin.core.module.*
import org.koin.core.module.dsl.*
import org.koin.dsl.*
import org.timer.main.data.*
import org.timer.main.settings.*
import org.timer.main.projects.*
import org.timer.main.timer.*

@ExperimentalResourceApi
fun appModule() = module {
    viewModel { ProjectsViewModel(get(),get(),get()) }
    viewModel { SettingsViewModel() }
    viewModel { TimerViewModel(get()) }
    singleOf(::ProjectsGateway)
    factoryOf(::GetProjectsUseCase)
    factoryOf(::AddProjectUseCase)
    factoryOf(::RemoveProjectUseCase)
}

expect fun  platformSpecificModule(): Module

@OptIn(ExperimentalResourceApi::class)
fun initializeKoin(
    conf: (KoinApplication.() -> Unit)?=null
){

    startKoin {
        conf?.invoke(this)
        modules(appModule(),platformSpecificModule())
    }
}

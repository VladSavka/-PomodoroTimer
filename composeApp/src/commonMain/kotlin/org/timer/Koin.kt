package org.timer

import org.jetbrains.compose.resources.*
import org.koin.core.*
import org.koin.core.context.*
import org.koin.core.module.*
import org.koin.core.module.dsl.*
import org.koin.dsl.*
import org.timer.main.settings.*
import org.timer.main.tasks.*
import org.timer.main.timer.*

@ExperimentalResourceApi
fun appModule() = module {
    viewModel { TasksViewModel() }
    viewModel { SettingsViewModel() }
    viewModel { TimerViewModel(get()) }
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

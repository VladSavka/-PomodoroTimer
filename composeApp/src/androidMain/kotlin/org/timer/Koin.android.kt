package org.timer

import org.koin.android.ext.koin.*
import org.koin.core.module.*
import org.koin.dsl.*
import org.timer.main.domain.auth.*
import org.timer.main.timer.*


actual fun platformSpecificModule(): Module = module {
    single { AlarmPlayer(context = androidContext()) }
    factory { MobileAlarm(context = androidContext()) }
    single<AuthGateway> { DefaultAuthGateway(context = androidContext()) }
}
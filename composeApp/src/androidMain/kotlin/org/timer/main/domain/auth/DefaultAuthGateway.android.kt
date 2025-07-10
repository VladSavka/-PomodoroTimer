package org.timer.main.domain.auth

import com.diamondedge.logging.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

actual class DefaultAuthGateway : AuthGateway {

    val flow = MutableStateFlow<AuthState>(AuthState.NotAuthenticated)

    init {
        val launch = GlobalScope.launch {
            delay(2000)
            flow.emit(AuthState.NotAuthenticated)
        }
    }

    actual override suspend fun login() {
        flow.emit(AuthState.Authenticated)
    }

    actual override fun isLoggedIn(): Flow<AuthState> = flow

    actual override suspend fun logout() {
        logging().d { "logout" }
        flow.emit(AuthState.NotAuthenticated)
    }

}
package org.timer.main.breakactivity.org.timer.main.domain.auth

import kotlinx.coroutines.flow.*
import org.timer.main.domain.auth.*

class FakeAuthGateway : AuthGateway {
    val flow = MutableStateFlow<AuthState>(AuthState.Loading)

    override suspend fun login() {
        flow.emit(AuthState.Authenticated)
    }

    override fun isLoggedIn(): Flow<AuthState> = flow

    override suspend fun logout() {
        flow.emit(AuthState.NotAuthenticated)
    }
}
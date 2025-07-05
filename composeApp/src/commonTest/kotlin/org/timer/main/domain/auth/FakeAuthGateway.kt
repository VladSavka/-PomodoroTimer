package org.timer.main.breakactivity.org.timer.main.domain.auth

import kotlinx.coroutines.flow.*
import org.timer.main.domain.auth.*

class FakeAuthGateway : AuthGateway {
    private val flow = MutableStateFlow(false)

    override suspend fun login() {
        flow.emit(true)
    }

    override fun isLoggedIn(): Flow<Boolean> = flow

    override suspend fun logout() {
        flow.emit(false)
    }
}
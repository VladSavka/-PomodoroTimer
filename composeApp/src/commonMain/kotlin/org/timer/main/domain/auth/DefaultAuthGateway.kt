package org.timer.main.domain.auth

import kotlinx.coroutines.flow.*

class DefaultAuthGateway : AuthGateway {
    private val flow = MutableStateFlow(false)

    override suspend fun login() {
        flow.emit(true)
    }

    override fun isLoggedIn(): Flow<Boolean> = flow
    override suspend fun logout() {
        flow.emit(false)
    }
}
package org.timer.main.domain.auth

import kotlinx.coroutines.flow.*

actual class DefaultAuthGateway : AuthGateway {

    actual override suspend fun login() {
    }

    actual override fun isLoggedIn(): Flow<AuthState> {
        TODO("Not yet implemented")
    }

    actual override suspend fun logout() {
    }

}
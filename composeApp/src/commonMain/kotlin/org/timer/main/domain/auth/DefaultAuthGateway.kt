package org.timer.main.domain.auth

import kotlinx.coroutines.flow.*

expect class DefaultAuthGateway() : AuthGateway {

    override suspend fun login()

    override fun getAuthState(): Flow<AuthState>

    override suspend fun logout()
}
package org.timer.main.domain.auth

import kotlinx.coroutines.flow.*

expect class DefaultAuthGateway constructor() : AuthGateway {

    override suspend fun login(): AuthResult

    override fun isLoggedIn(): Flow<Boolean>
    override suspend fun logout()
}
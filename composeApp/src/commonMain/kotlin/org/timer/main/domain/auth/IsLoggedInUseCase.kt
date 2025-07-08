package org.timer.main.domain.auth

import kotlinx.coroutines.flow.*

interface IsLoggedInUseCase {
    suspend operator fun invoke(): Flow<AuthState>
}

class DefaultIsLoggedInUseCase(private val authGateway: AuthGateway) : IsLoggedInUseCase {

    override suspend fun invoke(): Flow<AuthState> = authGateway.isLoggedIn()
}
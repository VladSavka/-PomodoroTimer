package org.timer.main.domain.auth

import kotlinx.coroutines.flow.*

interface IsLoggedInUseCase {
    suspend operator fun invoke(): Flow<Boolean>
}

class DefaultIsLoggedInUseCase(private val authGateway: AuthGateway) : IsLoggedInUseCase {

    override suspend fun invoke(): Flow<Boolean> = authGateway.isLoggedIn()
}
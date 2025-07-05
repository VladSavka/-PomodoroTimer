package org.timer.main.domain.auth

interface LogoutUseCase {
    suspend operator fun invoke(): Unit
}

class DefaultLogoutUseCase(private val authGateway: AuthGateway) : LogoutUseCase {

    override suspend fun invoke(): Unit {
        authGateway.logout()
    }
}
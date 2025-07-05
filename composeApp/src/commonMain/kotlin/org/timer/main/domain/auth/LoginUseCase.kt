package org.timer.main.domain.auth

interface LoginUseCase {
    suspend operator fun invoke()
}

class DefaultLoginUseCase(private val authGateway: AuthGateway) : LoginUseCase {

    override suspend fun invoke() {
        authGateway.login()
    }
}
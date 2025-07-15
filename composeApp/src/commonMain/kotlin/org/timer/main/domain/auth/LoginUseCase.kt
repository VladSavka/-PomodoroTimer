package org.timer.main.domain.auth

import com.diamondedge.logging.*
import kotlinx.coroutines.flow.*

interface LoginUseCase {
    suspend operator fun invoke()
}

class DefaultLoginUseCase(private val authGateway: AuthGateway) : LoginUseCase {

    override suspend fun invoke() {
        authGateway.login()
        val authState = authGateway.getAuthState().first()
        if (authState is AuthState.Authenticated) {
            authGateway.createUser(authState.id, authState.email)
        }
    }
}
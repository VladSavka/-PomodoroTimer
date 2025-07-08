package org.timer.main.domain.auth

import com.diamondedge.logging.*
import kotlin.math.*

interface LoginUseCase {
    suspend operator fun invoke()
}

class DefaultLoginUseCase(private val authGateway: AuthGateway) : LoginUseCase {

    override suspend fun invoke() {
        authGateway.login()
    }
}
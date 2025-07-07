package org.timer.main.domain.auth

import com.diamondedge.logging.*
import kotlin.math.*

interface LoginUseCase {
    suspend operator fun invoke()
}

class DefaultLoginUseCase(private val authGateway: AuthGateway) : LoginUseCase {

    override suspend fun invoke() {
        val login = authGateway.login()
        when (login){
            is AuthResult.Error ->  logging().e{ "Login error" + login.message }
            is AuthResult.Success ->  logging().d { "Login success user id = " + login.userId + " email "+login.email }
        }


    }
}
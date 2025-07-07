package org.timer.main.domain.auth

import kotlinx.coroutines.flow.*

sealed class AuthResult {
    data class Success(val userId: String? = null, val email: String? = null) : AuthResult()
    data class Error(val message: String) : AuthResult()
}

interface AuthGateway {
    suspend fun login(): AuthResult
    fun isLoggedIn() : Flow<Boolean>
    suspend fun logout()
}
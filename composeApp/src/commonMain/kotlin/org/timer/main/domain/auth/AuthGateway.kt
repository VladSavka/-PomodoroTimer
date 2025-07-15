package org.timer.main.domain.auth

import kotlinx.coroutines.flow.*

interface AuthGateway {
    suspend fun login()
    fun getAuthState(): Flow<AuthState>
    suspend fun logout()
    suspend fun createUser(id: String, email: String)
}

sealed class AuthState {
    data class Authenticated(val id: String, val email: String) : AuthState()
    data object Loading : AuthState()
    data object NotAuthenticated : AuthState()
}
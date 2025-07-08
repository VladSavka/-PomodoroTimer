package org.timer.main.domain.auth

import kotlinx.coroutines.flow.*

interface AuthGateway {
    suspend fun login()
    fun isLoggedIn() : Flow<AuthState>
    suspend fun logout()
}

sealed class AuthState{
    data object Authenticated: AuthState()
    data object Loading : AuthState()
    data object NotAuthenticated: AuthState()
}
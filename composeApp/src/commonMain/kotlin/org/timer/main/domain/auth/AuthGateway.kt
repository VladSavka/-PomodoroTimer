package org.timer.main.domain.auth

import kotlinx.coroutines.flow.*

interface AuthGateway {
    suspend fun login()
    fun isLoggedIn() : Flow<Boolean>
    suspend fun logout()
}
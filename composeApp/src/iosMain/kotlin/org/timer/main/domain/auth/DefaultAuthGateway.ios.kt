package org.timer.main.domain.auth

import kotlinx.coroutines.flow.*

lateinit var signInWithGoogle: () -> Unit
lateinit var signOut: () -> Unit
lateinit var observeAuthState: ((Boolean) -> Unit) -> Unit

actual class DefaultAuthGateway : AuthGateway {

    private val authStateFlow = MutableStateFlow<AuthState>(AuthState.Loading)

    actual override suspend fun login() {
        signInWithGoogle()
    }

    actual override suspend fun logout() {
        signOut()
    }

    actual override fun isLoggedIn(): Flow<AuthState> = observedAuthState

    private val observedAuthState: StateFlow<AuthState> by lazy {
        observeAuthState { isLoggedIn ->
            authStateFlow.value = if (isLoggedIn) {
                AuthState.Authenticated
            } else {
                AuthState.NotAuthenticated
            }
        }
        authStateFlow
    }
}
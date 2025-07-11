package org.timer.main.domain.auth

import android.content.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import org.timer.main.*
import kotlin.coroutines.*


actual class DefaultAuthGateway actual constructor(val context: Any?) : AuthGateway {

    private val authStateFlow = MutableStateFlow<AuthState>(AuthState.Loading)

    init {
        FirebaseAuthBridge.observeAuthState { isSignedIn ->
            val state = if (isSignedIn) AuthState.Authenticated else AuthState.NotAuthenticated
            authStateFlow.tryEmit(state)
        }
    }

    actual override suspend fun login() {
        suspendCancellableCoroutine { cont ->
            FirebaseAuthBridge.signInWithGoogle(
                context as Context,
                onSuccess = {
                    cont.resume(Unit)
                },
                onError = { error ->
                    cont.resumeWithException(error)
                }
            )
        }
    }

    actual override fun isLoggedIn(): Flow<AuthState> = authStateFlow

    actual override suspend fun logout() {
        FirebaseAuthBridge.signOut()
    }
}
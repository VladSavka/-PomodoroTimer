package org.timer.main.domain.auth

import dev.gitlive.firebase.*
import dev.gitlive.firebase.firestore.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlin.coroutines.*

lateinit var signInWithGoogle: ((Boolean, String?) -> Unit) -> Unit
lateinit var signOut: () -> Unit
lateinit var observeAuthState: ((Boolean, String, String) -> Unit) -> Unit

actual class DefaultAuthGateway actual constructor() : AuthGateway {

    private val authStateFlow = MutableStateFlow<AuthState>(AuthState.Loading)

    actual override suspend fun login() {
        suspendCancellableCoroutine { cont ->
            signInWithGoogle { isSuccess, error ->
                if (isSuccess) {
                    cont.resume(Unit)
                } else {
                    cont.resumeWithException(Exception(error))
                }
            }
        }
    }

    actual override suspend fun logout() {
        signOut()
    }

    override suspend fun createUser(id: String, email: String) {
        Firebase.firestore
            .collection("users")
            .document(id)
            .set(mapOf("email" to email))
    }

    actual override fun getAuthState(): Flow<AuthState> = observedAuthState

    private val observedAuthState: StateFlow<AuthState> by lazy {
        observeAuthState { isLoggedIn, id, email ->
            authStateFlow.value = if (isLoggedIn) {
                AuthState.Authenticated(id, email)
            } else {
                AuthState.NotAuthenticated
            }
        }
        authStateFlow
    }
}
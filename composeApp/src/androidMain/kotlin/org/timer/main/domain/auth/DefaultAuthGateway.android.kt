package org.timer.main.domain.auth

import android.content.*
import dev.gitlive.firebase.*
import dev.gitlive.firebase.firestore.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import org.timer.main.*
import kotlin.coroutines.*


actual class DefaultAuthGateway  : AuthGateway {

    private val authStateFlow = MutableStateFlow<AuthState>(AuthState.Loading)

    init {
        FirebaseAuthBridge.observeAuthState { isSignedIn, id, email ->
            val state =
                if (isSignedIn) AuthState.Authenticated(id, email) else AuthState.NotAuthenticated
            authStateFlow.tryEmit(state)
        }
    }

    actual override suspend fun login() {
        suspendCancellableCoroutine { cont ->
            FirebaseAuthBridge.signInWithGoogle(
                onSuccess = {
                    cont.resume(Unit)
                },
                onError = { error ->
                    cont.resumeWithException(error)
                }
            )
        }
    }

    actual override fun getAuthState(): Flow<AuthState> = authStateFlow

    actual override suspend fun logout() {
        FirebaseAuthBridge.signOut()
    }

    override suspend fun createUser(id: String, email: String) {
        Firebase.firestore
            .collection("users")
            .document(id)
            .set(
                mapOf(
                    "email" to email
                )
            )
    }
}
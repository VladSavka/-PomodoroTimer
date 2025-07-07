// wasmJsMain/kotlin/org/timer/main/domain/auth/DefaultAuthGateway.kt
package org.timer.main.domain.auth

import com.diamondedge.logging.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlin.js.*

// --- Внешние объявления для JS Interop (остаются без изменений) ---
private external interface JsAuthResponse {
    val success: Boolean
    val email: String?
    val uid: String?
    val error: String?
}

private external interface FirebaseJsBridge {
    fun signInWithGooglePopup(): Promise<JsAny?>
    fun signOut(): Promise<JsAny?>
    fun observeAuthState(callback: (isSignedIn: Boolean, email: String?, uid: String?) -> Unit): () -> Unit
}


@JsName("window.myAppJsFirebase")
private external val myAppJsFirebase: FirebaseJsBridge?

private external interface JsHelpers {
    fun getBooleanProperty(obj: JsAny?, propName: JsString): JsBoolean // JsBoolean может быть null
    fun getStringProperty(obj: JsAny?, propName: JsString): JsString?   // JsString может быть null
}

private fun getJsHelpersOrThrow(): JsHelpers {
    return myAppJsHelpers ?: throw IllegalStateException("JavaScript helpers (window.myAppJsHelpers) not available.")
}

@JsName("window.myAppJsHelpers")
private external val myAppJsHelpers: JsHelpers?

actual class DefaultAuthGateway : AuthGateway {

    private val _currentAuthState = MutableStateFlow(false)

    init {
        observeFirebaseAuthState()
    }

    private fun observeFirebaseAuthState() {
        try {
            val bridge = getFirebaseJsOrThrow()
            bridge.observeAuthState { isSignedIn, email, uid ->
                logging().d {"observeAuthState: isSignedIn=$isSignedIn"}
                _currentAuthState.value = isSignedIn
            }
        } catch (e: Throwable) {
            val errorMessage = e.message ?: "Unknown error setting up Firebase AuthState observer"
            logging().error{"Error setting up Firebase AuthState observer: $errorMessage"}
            _currentAuthState.value = false
        }
    }

    private fun getFirebaseJsOrThrow(): FirebaseJsBridge {
        return myAppJsFirebase
            ?: throw IllegalStateException("Firebase JS bridge (window.myAppJsFirebase) is not available.")
    }

    actual override suspend fun login(): AuthResult {
        return try {
            val responseJs: JsAny? = getFirebaseJsOrThrow().signInWithGooglePopup().await()

            if (responseJs == null) {
                AuthResult.Error("Received null response from JavaScript for login")
            } else {
                val helpers = getJsHelpersOrThrow()
                // Преобразуем Kotlin String в JsString для передачи в JS функцию
                val jsSuccess: JsBoolean = helpers.getBooleanProperty(responseJs, "success".toJsString())
                    val uid = helpers.getStringProperty(responseJs, "uid".toJsString())?.toString() // Преобразуем JsString? в Kotlin String?
                val email = helpers.getStringProperty(responseJs, "email".toJsString())?.toString()
                val error = helpers.getStringProperty(responseJs, "error".toJsString())?.toString()
                if (jsSuccess.toBoolean()) {
                    AuthResult.Success(userId = uid, email = email)
                } else {
                    AuthResult.Error(error ?: "Unknown error during Google login")
                }
            }
        } catch (e: Throwable) {
            val errorMessage = e.message ?: "JavaScript error during Google login"
            AuthResult.Error(errorMessage)
        }
    }

    actual override fun isLoggedIn(): Flow<Boolean> {
        return _currentAuthState
    }


    actual override suspend fun logout() {
           getFirebaseJsOrThrow().signOut()
    }
}


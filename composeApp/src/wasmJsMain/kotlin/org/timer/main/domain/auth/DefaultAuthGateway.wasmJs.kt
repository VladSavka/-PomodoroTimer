package org.timer.main.domain.auth

import com.diamondedge.logging.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlin.js.*

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

    private val _currentAuthState = MutableStateFlow<AuthState>(AuthState.Loading)

    init {
        observeFirebaseAuthState()
    }

    private fun observeFirebaseAuthState() {
        try {
            val bridge = getFirebaseJs()
            bridge.observeAuthState { isSignedIn, email, uid ->
                _currentAuthState.value = if (isSignedIn) AuthState.Authenticated else AuthState.NotAuthenticated
            }
        } catch (e: Throwable) {
            val errorMessage = e.message ?: "Unknown error setting up Firebase AuthState observer"
            logging().error{"Error setting up Firebase AuthState observer: $errorMessage"}
            _currentAuthState.value = AuthState.NotAuthenticated
        }
    }

    private fun getFirebaseJs(): FirebaseJsBridge {
        return myAppJsFirebase
            ?: throw IllegalStateException("Firebase JS bridge (window.myAppJsFirebase) is not available.")
    }

    actual override suspend fun login() {
        return try {
            val responseJs: JsAny? = getFirebaseJs().signInWithGooglePopup().await()

            if (responseJs == null) {
                logging().error{"Received null response from JavaScript for login"}
            } else {
                val helpers = getJsHelpersOrThrow()
                val jsSuccess: JsBoolean = helpers.getBooleanProperty(responseJs, "success".toJsString())
                    val uid = helpers.getStringProperty(responseJs, "uid".toJsString())?.toString()
                val email = helpers.getStringProperty(responseJs, "email".toJsString())?.toString()
                val error = helpers.getStringProperty(responseJs, "error".toJsString())?.toString()
                if (jsSuccess.toBoolean()) {
                  //  todo
                } else {
                    logging().error{"Error during Google login: $error"}
                }
            }
        } catch (e: Throwable) {
            val errorMessage = e.message ?: "JavaScript error during Google login"
            logging().error{errorMessage}
        }
    }

    actual override fun isLoggedIn(): Flow<AuthState> = _currentAuthState

    actual override suspend fun logout() {
           getFirebaseJs().signOut()
    }
}


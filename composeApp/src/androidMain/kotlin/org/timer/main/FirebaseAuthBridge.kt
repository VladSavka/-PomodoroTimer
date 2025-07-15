package org.timer.main

import android.app.*
import android.content.*
import androidx.activity.*
import androidx.activity.result.*
import androidx.activity.result.contract.*
import com.google.android.gms.auth.api.signin.*
import com.google.firebase.auth.*

object FirebaseAuthBridge {

    private lateinit var auth: FirebaseAuth
    private lateinit var signInLauncher: ActivityResultLauncher<Intent>
    private lateinit var signInIntent: Intent

    private var onSuccessCallback: (() -> Unit)? = null
    private var onErrorCallback: ((Throwable) -> Unit)? = null

    /**
     * Инициализация: регистрируем ActivityResultLauncher внутри FirebaseAuthBridge.
     * Вызывать нужно из Activity в onCreate.
     */
    fun init(activity: ComponentActivity) {
        auth = FirebaseAuth.getInstance()

        // Регистрируем launcher, чтобы обрабатывать результат входа
        signInLauncher =
            activity.registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->

                if (result.resultCode == Activity.RESULT_OK) {
                    val data: Intent? = result.data
                    try {
                        val task = GoogleSignIn.getSignedInAccountFromIntent(data)
                        val account = task.getResult(Exception::class.java)
                        val idToken = account?.idToken

                        if (idToken == null) {
                            onErrorCallback?.invoke(IllegalStateException("Missing Google ID Token"))
                            clearCallbacks()
                            return@registerForActivityResult
                        }

                        val credential = GoogleAuthProvider.getCredential(idToken, null)
                        auth.signInWithCredential(credential)
                            .addOnSuccessListener {
                                onSuccessCallback?.invoke()
                                clearCallbacks()
                            }
                            .addOnFailureListener {
                                onErrorCallback?.invoke(it)
                                clearCallbacks()
                            }
                    } catch (e: Exception) {
                        onErrorCallback?.invoke(e)
                        clearCallbacks()
                    }
                } else {
                    onErrorCallback?.invoke(Exception("Sign-in canceled"))
                    clearCallbacks()
                }
            }

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("653658039232-o458d8pml4eakl9la50qp9j7e5e0579t.apps.googleusercontent.com")
            .requestEmail()
            .build()

        val googleSignInClient = GoogleSignIn.getClient(activity, gso)
        signInIntent = googleSignInClient.signInIntent
    }

    /**
     * Запускаем процесс логина.
     * Вызывается из твоего DefaultAuthGateway.
     */
    fun signInWithGoogle(
        onSuccess: () -> Unit,
        onError: (Throwable) -> Unit
    ) {
        onSuccessCallback = onSuccess
        onErrorCallback = onError
        signInLauncher.launch(signInIntent)
    }

    fun signOut() {
        auth.signOut()
    }

    fun observeAuthState(listener: (Boolean, String, String) -> Unit) {
        auth.addAuthStateListener {
            listener(it.currentUser != null, it.currentUser?.uid ?: "", it.currentUser?.email ?: "")
        }
    }

    private fun clearCallbacks() {
        onSuccessCallback = null
        onErrorCallback = null
    }
}

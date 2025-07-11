package org.timer.main

import android.app.*
import android.content.*
import androidx.core.content.*
import androidx.credentials.*
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import androidx.credentials.exceptions.*
import com.google.android.libraries.identity.googleid.*
import com.google.firebase.*
import com.google.firebase.auth.*

object FirebaseAuthBridge {

    private lateinit var credentialManager: CredentialManager
    private lateinit var auth: FirebaseAuth

    fun init(context: Activity) {
        credentialManager = CredentialManager.create(context)
        auth = FirebaseAuth.getInstance()
    }

    fun signInWithGoogle(
        activity: Context,
        onSuccess: () -> Unit,
        onError: (Throwable) -> Unit
    ) {
        val request = GetCredentialRequest.Builder()
            .addCredentialOption(
                GetGoogleIdOption.Builder()
                    .setFilterByAuthorizedAccounts(true)
                    .setServerClientId("653658039232-o458d8pml4eakl9la50qp9j7e5e0579t.apps.googleusercontent.com")
                    .build()
            )
            .build()

        val executor = ContextCompat.getMainExecutor(activity)

        CredentialManager.create(activity).getCredentialAsync(
            activity,
            request,
            null,
            executor,
            object : CredentialManagerCallback<GetCredentialResponse, GetCredentialException> {
                override fun onResult(result: GetCredentialResponse) {
                    val credential = result.credential
                    val googleCredential = credential as? GoogleIdTokenCredential
                    val idToken = googleCredential?.idToken

                    if (idToken != null) {
                        val firebaseCredential = GoogleAuthProvider.getCredential(idToken, null)
                        FirebaseAuth.getInstance().signInWithCredential(firebaseCredential)
                            .addOnSuccessListener {
                                onSuccess()
                            }
                            .addOnFailureListener {
                                onError(it)
                            }
                    } else {
                        onError(IllegalStateException("Missing Google ID Token"))
                    }
                }

                override fun onError(e: GetCredentialException) {
                    onError(e)
                }
            }
        )
    }


    fun signOut() {
        auth.signOut()
    }

    fun observeAuthState(listener: (Boolean) -> Unit) {
        auth.addAuthStateListener {
            listener(it.currentUser != null)
        }
    }
}

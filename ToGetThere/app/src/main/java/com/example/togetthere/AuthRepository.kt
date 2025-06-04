package com.example.togetthere

import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.firestore
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.tasks.await

data class AuthResult(
    val isSuccess: Boolean,
    val errorMessage: String? = null,
    val user: FirebaseUser? = null
)

class AuthRepository {

    companion object {
        private const val TAG = "AuthRepository"
    }

    private val firebaseAuth = FirebaseAuth.getInstance()

    suspend fun signInWithCredentials(email: String, password: String): AuthResult {
        Log.d(TAG, "Attempting sign in for email: $email")

        return try {
            // 🔐 Autenticazione con await()
            val result = firebaseAuth.signInWithEmailAndPassword(email, password).await()

            val user = result.user
            if (user != null) {
                try {
                    // 🔁 Recupero token FCM
                    val token = FirebaseMessaging.getInstance().token.await()

                    // ☁️ Salvataggio token in Firestore
                    Firebase.firestore.collection("users").document(user.uid)
                        .set(mapOf("fcmToken" to token), SetOptions.merge()).await()

                    Log.d("FCM", "Token aggiornato con successo")
                } catch (e: Exception) {
                    Log.e("FCM", "Errore recupero o aggiornamento token", e)
                }
            }

            Log.d(TAG, "Sign in successful for user: ${user?.email}")
            AuthResult(
                isSuccess = true,
                user = user
            )

        } catch (e: Exception) {
            Log.e(TAG, "Error during authentication", e)
            val errorMessage = when (e) {
                is com.google.firebase.auth.FirebaseAuthInvalidUserException -> "Account non trovato"
                is com.google.firebase.auth.FirebaseAuthInvalidCredentialsException -> "Email o password non corretti"
                is com.google.firebase.auth.FirebaseAuthUserCollisionException -> "Account già esistente"
                is com.google.firebase.auth.FirebaseAuthWeakPasswordException -> "Password troppo debole"
                else -> "Errore di autenticazione: ${e.message}"
            }

            AuthResult(
                isSuccess = false,
                errorMessage = errorMessage
            )
        }
    }


    suspend fun signUp(email: String, password: String): AuthResult {
        Log.d(TAG, "Attempting sign up for email: $email")

        return try {
            val result = firebaseAuth.createUserWithEmailAndPassword(email, password).await()

            if (result.user != null) {
                Log.d(TAG, "Sign up successful for user: ${result.user?.email}")
                AuthResult(
                    isSuccess = true,
                    user = result.user
                )
            } else {
                Log.w(TAG, "Sign up failed: No user returned")
                AuthResult(
                    isSuccess = false,
                    errorMessage = "Errore durante la registrazione"
                )
            }

        } catch (e: Exception) {
            Log.e(TAG, "Error during sign up", e)
            val errorMessage = when (e) {
                is com.google.firebase.auth.FirebaseAuthUserCollisionException -> "Email già in uso"
                is com.google.firebase.auth.FirebaseAuthWeakPasswordException -> "Password troppo debole (minimo 6 caratteri)"
                is com.google.firebase.auth.FirebaseAuthInvalidCredentialsException -> "Email non valida"
                else -> "Errore durante la registrazione: ${e.message}"
            }

            AuthResult(
                isSuccess = false,
                errorMessage = errorMessage
            )
        }
    }

    suspend fun signOut() {
        try {
            Log.d(TAG, "Signing out user: ${getCurrentUser()?.email}")
            firebaseAuth.signOut()
        } catch (e: Exception) {
            Log.e(TAG, "Error during sign out", e)
        }
    }

    suspend fun sendPasswordResetEmail(email: String): AuthResult {
        return try {
            firebaseAuth.sendPasswordResetEmail(email).await()
            Log.d(TAG, "Password reset email sent to: $email")
            AuthResult(
                isSuccess = true
            )
        } catch (e: Exception) {
            Log.e(TAG, "Error sending password reset email", e)
            val errorMessage = when (e) {
                is com.google.firebase.auth.FirebaseAuthInvalidUserException -> "Email non trovata"
                is com.google.firebase.auth.FirebaseAuthInvalidCredentialsException -> "Email non valida"
                else -> "Errore nell'invio dell'email: ${e.message}"
            }

            AuthResult(
                isSuccess = false,
                errorMessage = errorMessage
            )
        }
    }

    fun getCurrentUser(): FirebaseUser? = firebaseAuth.currentUser

    fun isUserSignedIn(): Boolean = firebaseAuth.currentUser != null

    fun addAuthStateListener(listener: FirebaseAuth.AuthStateListener) {
        firebaseAuth.addAuthStateListener(listener)
    }

    fun removeAuthStateListener(listener: FirebaseAuth.AuthStateListener) {
        firebaseAuth.removeAuthStateListener(listener)
    }
}
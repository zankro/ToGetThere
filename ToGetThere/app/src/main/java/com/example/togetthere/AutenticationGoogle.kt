package com.example.togetthere

import android.content.Context
import android.util.Log
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import androidx.credentials.exceptions.GetCredentialException
import androidx.credentials.CustomCredential
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.tasks.await

class GoogleAuthUiClient(
    private val context: Context
) {
    private val auth = Firebase.auth
    private val credentialManager = CredentialManager.create(context)

    companion object {
        private const val TAG = "GoogleAuthUiClient"
    }

    suspend fun signIn(): SignInResult {
        Log.d(TAG, "Starting sign in process...")

        return try {
            val webClientId = context.getString(R.string.default_web_client_id)
            Log.d(TAG, "Web client ID: $webClientId") // Aggiungi questo log
            Log.d(TAG, "Web client ID configured: ${webClientId.isNotEmpty()}")

            val googleIdOption = GetGoogleIdOption.Builder()
                .setFilterByAuthorizedAccounts(false)
                .setServerClientId(webClientId)
                .setAutoSelectEnabled(false) // Cambiato a false per debugging
                .build()

            val request = GetCredentialRequest.Builder()
                .addCredentialOption(googleIdOption)
                .build()

            Log.d(TAG, "Making credential request...")

            // Aggiungi questo per verificare Google Play Services
            val context = this.context
            Log.d(TAG, "Package name: ${context.packageName}")

            val result = credentialManager.getCredential(
                request = request,
                context = context
            )

            Log.d(TAG, "Credential request successful, handling result...")
            handleSignInResult(result)

        } catch (e: GetCredentialException) {
            Log.e(TAG, "GetCredentialException occurred", e)
            e.printStackTrace()

            if (e is CancellationException) {
                Log.d(TAG, "Sign in was cancelled by user")
                throw e
            }

            SignInResult(
                data = null,
                errorMessage = "Authentication failed: ${e.message}",
                isNeededRegistration = false
            )
        } catch (e: Exception) {
            Log.e(TAG, "Unexpected exception during sign in", e)
            e.printStackTrace()

            if (e is CancellationException) throw e

            SignInResult(
                data = null,
                errorMessage = "Unexpected error: ${e.message}",
                isNeededRegistration = false
            )
        }
    }

    private suspend fun handleSignInResult(result: GetCredentialResponse): SignInResult {
        Log.d(TAG, "Handling sign in result...")

        return when (val credential = result.credential) {
            is CustomCredential -> {
                Log.d(TAG, "Received CustomCredential of type: ${credential.type}")

                if (credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                    try {
                        Log.d(TAG, "Parsing Google ID token credential...")

                        val googleIdTokenCredential = GoogleIdTokenCredential
                            .createFrom(credential.data)

                        val googleCredentials = GoogleAuthProvider.getCredential(
                            googleIdTokenCredential.idToken,
                            null
                        )

                        Log.d(TAG, "Signing in with Firebase...")
                        val firebaseUser = auth.signInWithCredential(googleCredentials).await().user

                        if (firebaseUser != null) {
                            Log.d(TAG, "Firebase sign in successful for user: ${firebaseUser.uid}")
                        } else {
                            Log.w(TAG, "Firebase sign in returned null user")
                        }

                        SignInResult(
                            data = firebaseUser?.run {
                                UserData(
                                    userId = uid,
                                    username = displayName,
                                    email = email,
                                    phoneNumber = phoneNumber,
                                    profilePictureUrl = photoUrl?.toString()
                                )
                            },
                            errorMessage = null,
                            isNeededRegistration = false
                        )

                    } catch (e: GoogleIdTokenParsingException) {
                        Log.e(TAG, "Failed to parse Google ID token", e)
                        e.printStackTrace()
                        SignInResult(
                            data = null,
                            errorMessage = "Failed to parse Google ID token: ${e.message}",
                            isNeededRegistration = false
                        )
                    } catch (e: Exception) {
                        Log.e(TAG, "Error during Firebase authentication", e)
                        e.printStackTrace()
                        if (e is CancellationException) throw e
                        SignInResult(
                            data = null,
                            errorMessage = "Authentication error: ${e.message}",
                            isNeededRegistration = false
                        )
                    }
                } else {
                    Log.w(TAG, "Unexpected credential type: ${credential.type}")
                    SignInResult(
                        data = null,
                        errorMessage = "Unexpected credential type: ${credential.type}",
                        isNeededRegistration = false
                    )
                }
            }
            else -> {
                Log.w(TAG, "Received non-CustomCredential: ${credential::class.java.simpleName}")
                SignInResult(
                    data = null,
                    errorMessage = "Unexpected credential format",
                    isNeededRegistration = false
                )
            }
        }
    }

    suspend fun signOut() {
        try {
            Log.d(TAG, "Signing out...")
            auth.signOut()
            Log.d(TAG, "Sign out successful")
        } catch (e: Exception) {
            Log.e(TAG, "Error during sign out", e)
            e.printStackTrace()
            if (e is CancellationException) throw e
        }
    }

    fun getSignedInUser(): UserData? {
        val currentUser = auth.currentUser
        Log.d(TAG, "Current user: ${currentUser?.uid ?: "null"}")

        return currentUser?.run {
            UserData(
                userId = uid,
                username = displayName,
                email = email,
                phoneNumber = phoneNumber,
                profilePictureUrl = photoUrl?.toString()
            )
        }
    }
}
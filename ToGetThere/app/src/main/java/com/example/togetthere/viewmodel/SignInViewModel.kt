package com.example.togetthere.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.togetthere.AuthRepository
import com.example.togetthere.GoogleAuthUiClient
import com.example.togetthere.SignInState
import com.example.togetthere.utils.SessionPrefs
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SignInViewModel(
    application: Application,
    private val googleAuthUiClient: GoogleAuthUiClient,
    private val authRepository: AuthRepository,
    private val userSessionViewModel: UserSessionViewModel
) : AndroidViewModel(application) {

    private val _state = MutableStateFlow(SignInState())
    val state = _state.asStateFlow()

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading.asStateFlow()

    private val _isNeededRegistration = MutableStateFlow(false)
    val isNeededRegistration: StateFlow<Boolean> = _isNeededRegistration.asStateFlow()

    companion object {
        private const val TAG = "SignInViewModel"
    }

    private val appContext = getApplication<Application>()

    // Autenticazione con Google
    fun signIn() {
        Log.d(TAG, "signIn() called")

        viewModelScope.launch {
            try {
                Log.d(TAG, "Setting loading to true")
                _loading.value = true

                Log.d(TAG, "Calling googleAuthUiClient.signIn()")
                val result = googleAuthUiClient.signIn()

                Log.d(TAG, "Sign in result received: success=${result.data != null}, error=${result.errorMessage}")

                _state.update {
                    it.copy(
                        isSignInSuccessful = result.data != null,
                        signInError = result.errorMessage,
                        isNeededRegistration = result.isNeededRegistration
                    )
                }

                _isNeededRegistration.value = result.isNeededRegistration

                if (result.data != null) {
                    Log.d(TAG, "Sign in successful for user: ${result.data.userId}")
                    // Aggiorna la sessione utente  Google
                    SessionPrefs.markLoggedOnce(appContext)
                    userSessionViewModel.login(result.data.userId)
                } else {
                    Log.w(TAG, "Sign in failed: ${result.errorMessage}")
                }

            } catch (e: Exception) {
                Log.e(TAG, "Exception in signIn()", e)
                e.printStackTrace()

                _state.update {
                    it.copy(
                        isSignInSuccessful = false,
                        signInError = "Unexpected error: ${e.message}",
                        isNeededRegistration = false
                    )
                }
            } finally {
                Log.d(TAG, "Setting loading to false")
                _loading.value = false
            }
        }
    }

    fun signInWithCredentials(email: String, password: String) {
        Log.d(TAG, "signInWithCredentials() called for email: $email")

        viewModelScope.launch {
            try {
                Log.d(TAG, "Setting loading to true")
                _loading.value = true

                // Validazione input
                if (email.isBlank() || password.isBlank()) {
                    _state.update {
                        it.copy(
                            isSignInSuccessful = false,
                            signInError = "Email e password sono obbligatori",
                            isNeededRegistration = false
                        )
                    }
                    return@launch
                }

                // Validazione formato email
                if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    _state.update {
                        it.copy(
                            isSignInSuccessful = false,
                            signInError = "Formato email non valido",
                            isNeededRegistration = false
                        )
                    }
                    return@launch
                }

                Log.d(TAG, "Calling authRepository.signInWithCredentials()")
                val result = authRepository.signInWithCredentials(email, password)

                Log.d(TAG, "Credentials sign in result: success=${result.isSuccess}, error=${result.errorMessage}")

                _state.update {
                    it.copy(
                        isSignInSuccessful = result.isSuccess,
                        signInError = result.errorMessage,
                        isNeededRegistration = false
                    )
                }

                if (result.isSuccess) {
                    Log.d(TAG, "Credentials sign in successful for user: ${result.user?.uid}")
                    // Aggiorna la sessione utente login
                    result.user?.uid?.let { userId ->
                        SessionPrefs.markLoggedOnce(appContext)
                        userSessionViewModel.login(userId)
                    }
                } else {
                    Log.w(TAG, "Credentials sign in failed: ${result.errorMessage}")
                }

            } catch (e: Exception) {
                Log.e(TAG, "Exception in signInWithCredentials()", e)
                e.printStackTrace()

                _state.update {
                    it.copy(
                        isSignInSuccessful = false,
                        signInError = "Errore imprevisto: ${e.message}",
                        isNeededRegistration = false
                    )
                }
            } finally {
                Log.d(TAG, "Setting loading to false")
                _loading.value = false
            }
        }
    }

    fun signOut() {
        Log.d(TAG, "signOut() called")

        viewModelScope.launch {
            try {
                googleAuthUiClient.signOut()
                authRepository.signOut()
                // Pulisci anche la sessione utente
                userSessionViewModel.logout()
                SessionPrefs.clear(appContext)
                resetState()
                Log.d(TAG, "Sign out completed")
            } catch (e: Exception) {
                Log.e(TAG, "Error during sign out", e)
                e.printStackTrace()
            }
        }
    }

    // Reset dello stato
    fun resetState() {
        Log.d(TAG, "resetState() called")
        _state.update { SignInState() }
        _loading.value = false
        _isNeededRegistration.value = false
    }
}
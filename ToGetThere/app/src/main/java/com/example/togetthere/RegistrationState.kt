package com.example.togetthere

sealed class RegistrationState {
    data object Idle : RegistrationState()
    data object Loading : RegistrationState()
    data class Success(val userId: String) : RegistrationState()
    data class Error(val message: String) : RegistrationState()
}

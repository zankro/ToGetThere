package com.example.togetthere

data class SignInState(
    val isSignInSuccessful: Boolean = false,
    val signInError: String? = null,
    val isNeededRegistration: Boolean = false
)
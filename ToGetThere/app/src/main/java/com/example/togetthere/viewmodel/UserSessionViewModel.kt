package com.example.togetthere.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.togetthere.data.repository.FirebaseUserProfileRepository
import com.example.togetthere.data.repository.UserProfileRepository
import com.example.togetthere.model.UserProfile
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class UserSessionViewModel(
    private val userProfileRepository: UserProfileRepository,
) : ViewModel() {

//    private val _currentUser = mutableStateOf<UserProfile?>(null)
//    val currentUser = _currentUser
//
//    fun login(userId: String) {
//        viewModelScope.launch { _currentUser.value = userProfileRepository.getUserById(userId)}
//
//        //_currentUser.value = PaoloProfile
//    }
//
//    fun logout() {
//        _currentUser.value = null
//    }

    private val _currentUser = MutableStateFlow<UserProfile?>(null)
    val currentUser: StateFlow<UserProfile?> = _currentUser

    init {
        // Appena il VM nasce, provo a rileggere la sessione Firebase
        val firebaseUser = userProfileRepository.getCurrentUser()
        if (firebaseUser != null) {
            viewModelScope.launch {
                _currentUser.value = userProfileRepository.getUserById(firebaseUser.uid)
            }
        }
    }

   /* val firebaseUser: FirebaseUser?
        get() = userProfileRepository.getCurrentUser()*/

    fun login(userId: String) {
        viewModelScope.launch {
            _currentUser.value = userProfileRepository.getUserById(userId)
        }
    }

    fun logout() {
        _currentUser.value = null
    }


    companion object {
        fun provideFactory(userProfileRepository: UserProfileRepository): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    @Suppress("UNCHECKED_CAST")
                    return UserSessionViewModel(userProfileRepository) as T
                }
            }
    }
}
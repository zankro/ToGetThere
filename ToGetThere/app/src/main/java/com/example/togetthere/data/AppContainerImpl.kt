package com.example.togetthere.data

import android.content.Context
import com.example.togetthere.data.repository.FirebaseChatRepository
import com.example.togetthere.data.repository.FirebaseNotificationRepository
import com.example.togetthere.data.repository.FirebaseReviewRepository
import com.example.togetthere.data.repository.FirebaseTripRepository
import com.example.togetthere.data.repository.FirebaseUserProfileRepository
import com.example.togetthere.model.MainRepository
import com.google.firebase.Firebase
import com.google.firebase.auth.auth

/**
 * Dependency Injection container at the application level.
 */
interface AppContainer {
    val mainRepository: MainRepository

//    val currentUser: UserProfile
//        get() = mainRepository.userProfileRepository.getUserById("60DCjfz7ouUTxAsySkatlT6xzt13")!!
    //val currentUser : UserProfile
        //get() = PaoloProfile

}

/**
 * Implementation for the Dependency Injection container at the application level.
 *
 * Variables are initialized lazily and the same instance is shared across the whole app.
 */
class AppContainerImpl(private val context: Context) : AppContainer {
    override val mainRepository: MainRepository by lazy {
        MainRepository(
            userProfileRepository = FirebaseUserProfileRepository(),
            tripRepository = FirebaseTripRepository(),
            reviewRepository = FirebaseReviewRepository(),
            notificationRepository = FirebaseNotificationRepository(Firebase.auth),
            chatRepository = FirebaseChatRepository(Firebase.auth)
        )
    }
}
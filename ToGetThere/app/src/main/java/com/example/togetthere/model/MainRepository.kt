package com.example.togetthere.model

import com.example.togetthere.data.repository.FirebaseChatRepository
import com.example.togetthere.data.repository.FirebaseNotificationRepository
import com.example.togetthere.data.repository.ReviewRepository
import com.example.togetthere.data.repository.TripRepository
import com.example.togetthere.data.repository.UserProfileRepository


//class MainRepository(
//    val userProfileRepository: UserProfileRepository = UserProfileRepository(),
//    val reviewRepository: ReviewRepository = ReviewRepository(),
//    val tripRepository: TripRepository = TripRepository(),
//    val notificationRepository: NotificationRepository = NotificationRepository()
//)

class MainRepository(
    val userProfileRepository: UserProfileRepository,
    val reviewRepository: ReviewRepository,
    val tripRepository: TripRepository,
    val notificationRepository: FirebaseNotificationRepository,
    val chatRepository: FirebaseChatRepository
)
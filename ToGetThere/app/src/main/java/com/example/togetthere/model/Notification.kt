package com.example.togetthere.model


import com.google.firebase.Timestamp

enum class NotificationType {
    LastMinuteProposal,
    NewApplication,
    TripReviewReceived,
    UserReviewReceived,
    ApplicationStatusUpdate,
    Other
}

data class Notification(
    val id: String = "",
    val receiverId: String = "",
    val senderId: String = "",
    val type: NotificationType = NotificationType.Other,
    val message: String = "",
    val timestamp: Timestamp = Timestamp.now(),
    val pending: Boolean = false,
    val tripId: Int? = null,
    val reviewId: Int? = null,
)

//class NotificationRepository {
//    val notifications = listOf(not1, not2, not3, not4, not5)
//}
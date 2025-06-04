package com.example.togetthere.data
//
//import com.example.togetthere.model.Notification
//import com.example.togetthere.model.NotificationType
//import java.time.LocalDateTime
//
//val not1 = Notification(
//        id = 1,
//        type = NotificationType.LastMinuteProposal,
//        message = "Romantic gateway in Venice coming soon!",
//        timestamp = LocalDateTime.now().minusHours(2),
//        isRead = true,
//        relatedTripId = 1
//        )
//
//val not2 = Notification(
//        id = 2,
//        type = NotificationType.NewApplication,
//        message = "Andrea wants to join \"Surf trip around France coast\"",
//        timestamp = LocalDateTime.now().minusDays(1),
//        isRead = false,
//        relatedTripId = 0,
//        )
//
//val not3 = Notification(
//        id = 3,
//        type = NotificationType.TripReviewReceived,
//        message = "\"Sahara Desert Escape\" received a review",
//        timestamp = LocalDateTime.now().minusDays(3),
//        isRead = false,
//        relatedReview = 4,
//        relatedTripId = 5
//        )
//
//val not4 = Notification(
//        id = 4,
//        type = NotificationType.ApplicationStatusUpdate,
//        message = "Your application for \"Island Adventure in Greece\" is confirmed!",
//        timestamp = LocalDateTime.now().minusHours(5),
//        isRead = false,
//        relatedTripId = 3
//        )
//
//val not5 = Notification(
//        id = 5,
//        type = NotificationType.UserReviewReceived,
//        message = "You received a new review",
//        timestamp = LocalDateTime.now().minusHours(5),
//        isRead = false,
//        relatedReview = 2,
//    )
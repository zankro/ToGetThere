package com.example.togetthere.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.Exclude

/*data class Message (
    val text: String,
    val time: String,
    val senderId: String,
    val receiverId: String,
    val senderName: String
)*/


data class Message(
    val id: String = "",
    val text: String = "",
    val senderId: String = "",
    val senderName: String = "",
    @get:Exclude val isCurrentUser: Boolean = false,
    val timestamp: Timestamp = Timestamp.now()
)


data class TripChat(
    val id: String = "",
    val tripId: String = "",
    val name: String = "",
    val image: String? = null,
    val lastMessage: Message? = null,
    val timestamp: Timestamp = Timestamp.now(),
    val lastRead: Map<String, Timestamp> = emptyMap(),
    val participants: List<String> = emptyList()
)
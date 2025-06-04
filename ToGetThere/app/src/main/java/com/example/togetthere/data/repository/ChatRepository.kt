package com.example.togetthere.data.repository

import com.example.togetthere.model.Message
import com.example.togetthere.model.TripChat
import kotlinx.coroutines.flow.Flow

interface ChatRepository {
    fun getUserChats(userId: String): Flow<List<TripChat>>
    fun getChatMessages(chatId: String): Flow<List<Message>>

    suspend fun sendMessage(chatId: String, text: String)
    suspend fun createGroupChat(
        tripId: String,
        tripTitle: String,
        tripImage: String?,
        participants: List<String>
    ): String

    suspend fun addParticipantToChat(chatId: String, userId: String)
    suspend fun markMessagesAsRead(chatId: String)
    suspend fun deleteChat(tripId: String)
}
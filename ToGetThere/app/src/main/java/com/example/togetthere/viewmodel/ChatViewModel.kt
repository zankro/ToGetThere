package com.example.togetthere.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.togetthere.model.Message
import com.example.togetthere.model.TripChat
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import android.util.Log
import androidx.lifecycle.ViewModelProvider
import com.example.togetthere.data.repository.FirebaseChatRepository
import com.example.togetthere.data.repository.UserProfileRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.sql.Timestamp


class ChatViewModel @Inject constructor(
    private val chatRepository: FirebaseChatRepository,
    private val userProfileRepository: UserProfileRepository
) : ViewModel() {
    private val _messages = MutableStateFlow<List<Message>>(emptyList())
    val messages: StateFlow<List<Message>> = _messages.asStateFlow()

    private val _userImageMap = MutableStateFlow<Map<String, String>>(emptyMap())
    val userImageMap: StateFlow<Map<String, String>> = _userImageMap

    private val _chatItems = MutableStateFlow<List<TripChat>>(emptyList())
    val chatItems: StateFlow<List<TripChat>> = _chatItems.asStateFlow()

    private val _currentChat = MutableStateFlow<TripChat?>(null)
    val currentChat: StateFlow<TripChat?> = _currentChat.asStateFlow()

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading.asStateFlow()

    private fun loadMessages(chatId: String) {
        viewModelScope.launch {
            try {
                chatRepository.getChatMessages(chatId).collect { messages ->
                    _messages.value = messages

                    val uniqueUserIds = messages.map { it.senderId }.distinct()
                    val users = userProfileRepository.getUsersByIds(uniqueUserIds)
                    val userImageMap = users.associate { it.userId to it.photo.orEmpty() }
                    _userImageMap.value = userImageMap
                }
            } catch (e: Exception) {
                Log.e("ChatViewModel", "Error loading messages", e)
            }
        }
    }

    fun loadUserChats(userId: String) {
        _loading.value = true
        viewModelScope.launch {
            try {
                chatRepository.getUserChats(userId).collect { chats ->
                    _chatItems.value = chats
                    _loading.value = false
                }
            } catch (e: Exception) {
                Log.e("ChatViewModel", "Error loading chats", e)
                _loading.value = false
            }
        }
    }

    fun selectChat(chat: TripChat?) {
        _currentChat.value = chat
        chat?.let { loadMessages(it.id) }
    }

    fun sendMessage(text: String) {
        viewModelScope.launch {
            try {
                val chatId = _currentChat.value?.id ?: return@launch
                chatRepository.sendMessage(chatId, text)
            } catch (e: Exception) {
                Log.e("ChatViewModel", "Error sending message", e)
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
    }


    fun getUnreadCount(chatId: String): Flow<Int> {
        return chatRepository.getUnreadCount(chatId)
            .map { unreadCount ->
                unreadCount
            }
    }

    fun markChatAsRead(chatId: String, userId: String) {
        viewModelScope.launch {
            try {
                chatRepository.markAsRead(chatId, userId)
            } catch (e: Exception) {
                Log.e("ChatViewModel", "Error marking chat as read", e)
            }
        }
    }

    companion object {
        fun provideFactory(
            chatRepository: FirebaseChatRepository,
            userProfileRepository: UserProfileRepository
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return ChatViewModel(chatRepository, userProfileRepository,) as T
            }
        }
    }
}
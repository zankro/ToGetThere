package com.example.togetthere.data.repository

import com.example.togetthere.model.UserProfile
import com.google.firebase.auth.FirebaseUser

interface UserProfileRepository {
    suspend fun getAllUsers(): List<UserProfile>
    suspend fun getUserById(userId: String): UserProfile?
    suspend fun getUsersByIds(userIds: List<String>): List<UserProfile>
    suspend fun addUser(user: UserProfile)
    suspend fun removeUser(userId: String)
    suspend fun updateUser(user: UserProfile)
    fun getCurrentUser(): FirebaseUser?
}
package com.example.togetthere.data.repository

import android.util.Log
import com.example.togetthere.model.UserProfile
import com.example.togetthere.model.UserReview
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class FirebaseUserProfileRepository(
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
) : UserProfileRepository {

    private val usersCollection = db.collection("users")

    override fun getCurrentUser(): FirebaseUser? {
        return FirebaseAuth.getInstance().currentUser
    }

    override suspend fun getAllUsers(): List<UserProfile> = withContext(Dispatchers.IO) {
        val snapshot = usersCollection.get().await()
        snapshot.documents.mapNotNull { it.toObject(UserProfile::class.java) }
    }

    override suspend fun getUserById(userId: String): UserProfile? = withContext(Dispatchers.IO) {
        if (userId.isBlank()) {
            Log.e("FirebaseUserRepo", "❌ getUserById: userId is blank or invalid!")
            return@withContext null
        }

        try {
            val snapshot = usersCollection.document(userId).get().await()
            snapshot.toObject(UserProfile::class.java)
        } catch (e: Exception) {
            Log.e("FirebaseUserRepo", "❌ Error fetching user with id $userId: ${e.localizedMessage}")
            null
        }
    }

    override suspend fun getUsersByIds(userIds: List<String>): List<UserProfile> = withContext(Dispatchers.IO) {
        if (userIds.isEmpty()) return@withContext emptyList()
        val chunks = userIds.chunked(10)
        val users = mutableListOf<UserProfile>()
        for (chunk in chunks) {
            val snapshot = usersCollection.whereIn("userId", chunk).get().await()
            users += snapshot.documents.mapNotNull { it.toObject(UserProfile::class.java) }
        }
        users
    }

    override suspend fun addUser(user: UserProfile) = withContext(Dispatchers.IO) {
        usersCollection.document(user.userId).set(user).await()
        Unit
    }

    override suspend fun removeUser(userId: String) = withContext(Dispatchers.IO) {
        usersCollection.document(userId).delete().await()
        Unit
    }

    override suspend fun updateUser(user: UserProfile) = withContext(Dispatchers.IO) {
        usersCollection.document(user.userId).set(user).await()
        Unit
    }

}
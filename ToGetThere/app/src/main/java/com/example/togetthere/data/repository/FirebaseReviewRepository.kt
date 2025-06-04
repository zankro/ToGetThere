package com.example.togetthere.data.repository

import com.example.togetthere.model.TripReview
import com.example.togetthere.model.UserReview
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class FirebaseReviewRepository(
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) : ReviewRepository {

    private val tripReviewsCollection = firestore.collection("trip_reviews")
    private val userReviewsCollection = firestore.collection("user_reviews")

    override suspend fun getAllTripReviews(): List<TripReview> = withContext(Dispatchers.IO) {
        val snapshot = tripReviewsCollection.get().await()
        snapshot.toObjects(TripReview::class.java)
    }

    override suspend fun getReviewsForTrip(tripId: String): List<TripReview> = withContext(Dispatchers.IO) {
        val snapshot = tripReviewsCollection.whereEqualTo("tripId", tripId).get().await()
        snapshot.toObjects(TripReview::class.java)
    }

    override suspend fun addTripReview(review: TripReview): Unit = withContext(Dispatchers.IO) {
        tripReviewsCollection.add(review).await()
    }

    override suspend fun getAllUserReviews(): List<UserReview> = withContext(Dispatchers.IO) {
        val snapshot = userReviewsCollection.get().await()
        snapshot.toObjects(UserReview::class.java)
    }

    override suspend fun getReviewsForUser(userId: String): List<UserReview> = withContext(Dispatchers.IO) {
        val snapshot = userReviewsCollection.whereEqualTo("receiverId", userId).get().await()
        snapshot.toObjects(UserReview::class.java)
    }

    override suspend fun addUserReview(review: UserReview): Unit = withContext(Dispatchers.IO) {
        userReviewsCollection.add(review).await()
    }

    override suspend fun getNextReviewId(): Int = withContext(Dispatchers.IO) {
        val snapshot = userReviewsCollection.get().await()
        val currentMaxId = snapshot.documents.mapNotNull {
            it.toObject(UserReview::class.java)?.id
        }.maxOrNull() ?: 0
        return@withContext currentMaxId + 1
    }

}
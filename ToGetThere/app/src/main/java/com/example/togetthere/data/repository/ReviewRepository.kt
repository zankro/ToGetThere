package com.example.togetthere.data.repository

import com.example.togetthere.model.TripReview
import com.example.togetthere.model.UserReview

interface ReviewRepository {
    suspend fun getAllTripReviews(): List<TripReview>
    suspend fun getReviewsForTrip(tripId: String): List<TripReview>
    suspend fun addTripReview(review: TripReview)

    suspend fun getAllUserReviews(): List<UserReview>
    suspend fun getReviewsForUser(userId: String): List<UserReview>
    suspend fun addUserReview(review: UserReview)
    suspend fun getNextReviewId(): Int
}
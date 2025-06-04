package com.example.togetthere.data.repository

import com.example.togetthere.model.Trip
import com.example.togetthere.model.TripReview
import com.example.togetthere.model.TripType
import kotlinx.coroutines.flow.Flow

interface TripRepository {
    fun getAllTrips(): Flow<List<Trip>>
    suspend fun getTripById(tripId: Int): Trip?
    fun getTripsByCreator(creatorId: String): Flow<List<Trip>>
    fun getTripsByDestination(destination: String): Flow<List<Trip>>
    fun getTripsByType(type: TripType): Flow<List<Trip>>

    suspend fun addTrip(trip: Trip)
    suspend fun removeTrip(tripId: Int)
    suspend fun updateTrip(trip: Trip)

    fun getTripsDoneByUser(userId: String): Flow<List<Trip>>
    fun getBookedTripsByUser(userId: String): Flow<List<Trip>>

    suspend fun isTripFavoriteForUser(tripId: Int, userId: String): Boolean
    suspend fun toggleFavoriteTripForUser(tripId: Int, userId: String)
    fun getFavoriteTripsForUser(userId: String): Flow<List<Trip>>

    suspend fun addReviewToTrip(tripId: Int, review: TripReview)
}
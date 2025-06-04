package com.example.togetthere.data.repository

import android.util.Log
import com.example.togetthere.R
import com.example.togetthere.model.Message
import com.example.togetthere.model.Trip
import com.example.togetthere.model.TripReview
import com.example.togetthere.model.TripType
import com.google.firebase.Firebase
import com.google.firebase.Timestamp
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class FirebaseTripRepository : TripRepository {

    private val db = Firebase.firestore
    private val tripsCollection = db.collection("trips")

    override fun getAllTrips(): Flow<List<Trip>> = callbackFlow {
        val listener = tripsCollection
            .addSnapshotListener { snapshot, error ->
                if (error != null || snapshot == null) {
                    trySend(emptyList())
                    return@addSnapshotListener
                }
                trySend(snapshot.toObjects(Trip::class.java))
            }
        awaitClose { listener.remove() }
    }

    override suspend fun getTripById(tripId: Int): Trip? {
        val snapshot = tripsCollection
            .whereEqualTo("tripId", tripId)
            .get()
            .await()

        return snapshot.documents.firstOrNull()?.toObject(Trip::class.java)
    }

    override fun getTripsByCreator(creatorId: String): Flow<List<Trip>> = callbackFlow {
        val listener = tripsCollection
            .whereEqualTo("creator", creatorId)
            .addSnapshotListener { snapshot, error ->
                if (error != null || snapshot == null) {
                    trySend(emptyList())
                    return@addSnapshotListener
                }
                trySend(snapshot.toObjects(Trip::class.java))
            }
        awaitClose { listener.remove() }
    }

    override fun getTripsByDestination(destination: String): Flow<List<Trip>> = callbackFlow {
        val listener = tripsCollection
            .whereEqualTo("destination", destination)
            .addSnapshotListener { snapshot, error ->
                if (error != null || snapshot == null) {
                    trySend(emptyList())
                    return@addSnapshotListener
                }
                trySend(snapshot.toObjects(Trip::class.java))
            }
        awaitClose { listener.remove() }
    }

    override fun getTripsByType(type: TripType): Flow<List<Trip>> = callbackFlow {
        val listener = tripsCollection
            .whereEqualTo("type", type.name)
            .addSnapshotListener { snapshot, error ->
                if (error != null || snapshot == null) {
                    trySend(emptyList())
                    return@addSnapshotListener
                }
                trySend(snapshot.toObjects(Trip::class.java))
            }
        awaitClose { listener.remove() }
    }

    override suspend fun addTrip(trip: Trip) {
        tripsCollection.add(trip).await()

        val defaultMessage = Message(
            text = "Hi! Welcome to ${trip.name}",
            senderId = trip.creator,
            senderName = "",
            timestamp =Timestamp.now()
        )

        val chatData = hashMapOf(
            "image" to trip.images.firstOrNull()?.url.orEmpty(),
            "name" to trip.name,
            "participants" to listOf(trip.creator),
            "tripId" to trip.tripId.toString(),
            "timestamp" to Timestamp.now(),
            "lastMessage" to defaultMessage,
            "lastRead" to hashMapOf<String, Any>(
                trip.creator to Timestamp.now()
            )
        )

        Firebase.firestore.collection("chats")
            .add(chatData)
            .addOnFailureListener { e ->
                Log.e("Firestore", "Errore nella creazione chat", e)
            }


    }

    override suspend fun removeTrip(tripId: Int) {

        val snapshot = db.collection("chats")
            .whereEqualTo("tripId", tripId.toString())
            .get()
            .await()

        for (document in snapshot.documents) {
            db.collection("chats").document(document.id).delete().await()
        }
        val query = tripsCollection.whereEqualTo("tripId", tripId).get().await()
        query.documents.forEach { it.reference.delete() }

    }

    override suspend fun updateTrip(trip: Trip) {
        val query = tripsCollection.whereEqualTo("tripId", trip.tripId).get().await()
        query.documents.forEach { it.reference.set(trip) }
    }

    override fun getTripsDoneByUser(userId: String): Flow<List<Trip>> = callbackFlow {
        val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
        val today = LocalDate.now()

        val listener = tripsCollection
            .addSnapshotListener { snapshot, _ ->
                val trips = snapshot?.toObjects(Trip::class.java)?.filter { trip ->
                    val endDate = LocalDate.parse(trip.endDate, formatter)
                    endDate.isBefore(today) &&
                            (trip.creator == userId || trip.reservationsList.any { it.bookerId == userId })
                } ?: emptyList()

                trySend(trips)
            }
        awaitClose { listener.remove() }
    }

    override fun getBookedTripsByUser(userId: String): Flow<List<Trip>> = callbackFlow {
        val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
        val today = LocalDate.now()

        val listener = tripsCollection
            .addSnapshotListener { snapshot, _ ->
                val trips = snapshot?.toObjects(Trip::class.java)?.filter { trip ->
                    val isBookedByUser = trip.reservationsList.any { it.bookerId == userId }
                    val startDate = LocalDate.parse(trip.startDate, formatter)
                    val notStartedYet = !startDate.isBefore(today)

                    isBookedByUser && notStartedYet
                } ?: emptyList()

                trySend(trips)
            }

        awaitClose { listener.remove() }
    }

    override suspend fun isTripFavoriteForUser(tripId: Int, userId: String): Boolean {
        val query = tripsCollection.whereEqualTo("tripId", tripId).get().await()
        return query.documents.firstOrNull()
            ?.toObject(Trip::class.java)
            ?.favoritesUsers
            ?.contains(userId) ?: false
    }

    override suspend fun toggleFavoriteTripForUser(tripId: Int, userId: String) {
        val query = tripsCollection.whereEqualTo("tripId", tripId).get().await()
        val doc = query.documents.firstOrNull() ?: return
        val trip = doc.toObject(Trip::class.java) ?: return

        val updatedFavorites = if (trip.favoritesUsers.contains(userId)) {
            trip.favoritesUsers - userId
        } else {
            trip.favoritesUsers + userId
        }

        val updatedTrip = trip.copy(favoritesUsers = updatedFavorites)
        doc.reference.set(updatedTrip).await()
    }

    override fun getFavoriteTripsForUser(userId: String): Flow<List<Trip>> = callbackFlow {
        val listener = tripsCollection
            .addSnapshotListener { snapshot, _ ->
                val trips = snapshot?.toObjects(Trip::class.java)?.filter {
                    userId in it.favoritesUsers
                } ?: emptyList()
                trySend(trips)
            }
        awaitClose { listener.remove() }
    }

    override suspend fun addReviewToTrip(tripId: Int, review: TripReview) {
        val query = tripsCollection.whereEqualTo("tripId", tripId).get().await()
        val doc = query.documents.firstOrNull() ?: return
        val trip = doc.toObject(Trip::class.java) ?: return
        val updatedTrip = trip.copy(reviews = trip.reviews + review)
        doc.reference.set(updatedTrip).await()
    }
}
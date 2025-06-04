package com.example.togetthere.data.repository;

import android.util.Log
import com.example.togetthere.model.Notification
import com.example.togetthere.model.TripChat
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

import javax.inject.Inject;

class FirebaseNotificationRepository @Inject constructor(
        private val auth: FirebaseAuth
){
    private val db = Firebase.firestore
    private val notifCollection = db.collection("notifications")

    fun getUserNotifications(): Flow<List<Notification>> = callbackFlow {
        val listener = notifCollection
            .whereEqualTo("receiverId", auth.currentUser?.uid)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.d("DEBUG", "Errore notifiche $error")
                    trySend(emptyList())
                    return@addSnapshotListener
                }
                val notifs = snapshot?.documents?.mapNotNull { doc ->
                    doc.toObject(Notification::class.java)?.copy(id = doc.id)
                } ?: emptyList()
                notifs.forEach { notif ->
                    Log.d("DEBUG", "Notifica: $notif")
                }

                trySend(notifs.sortedByDescending { it.timestamp })
            }
        awaitClose { listener.remove() }
    }

    suspend fun removePending(notificationId: String) {
        println("Rimuovo Pending")

        val docRef = notifCollection.document(notificationId)

        val snapshot = docRef.get().await()
        if (snapshot.exists()) {
            docRef.update("pending", false).await()
            Log.d("Firestore", "Campo 'pending' aggiornato a false per notifica $notificationId")
        } else {
            Log.w("Firestore", "Nessuna notifica trovata con id: $notificationId")
        }
    }
}

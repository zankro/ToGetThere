package com.example.togetthere.data.repository

import android.util.Log
import com.example.togetthere.model.Message
import com.example.togetthere.model.TripChat
import com.google.firebase.Firebase
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.snapshots
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import java.util.Date
import javax.inject.Inject

class FirebaseChatRepository @Inject constructor(
    private val auth: FirebaseAuth
) {
    private val db = Firebase.firestore
    private val chatsCollection = db.collection("chats")

    fun getUserChats(userId: String): Flow<List<TripChat>> = callbackFlow {
        val listener = chatsCollection
            .whereArrayContains("participants", userId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(emptyList())
                    return@addSnapshotListener
                }
                val chats = snapshot?.documents?.mapNotNull { doc ->
                    doc.toObject(TripChat::class.java)?.copy(id = doc.id)
                } ?: emptyList()
                trySend(chats.sortedByDescending { it.timestamp })
            }
        awaitClose { listener.remove() }
    }

    fun getChatMessages(chatId: String): Flow<List<Message>> = callbackFlow {
        val currentUserId = auth.currentUser?.uid ?: ""

        val listener = db.collection("chats/$chatId/messages")
            .orderBy("timestamp", Query.Direction.ASCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(emptyList())
                    return@addSnapshotListener
                }

                val messages = snapshot?.documents?.mapNotNull { doc ->
                    doc.toObject(Message::class.java)?.copy(
                        id = doc.id,
                        isCurrentUser = doc.getString("senderId") == currentUserId
                    )
                } ?: emptyList()
                trySend(messages)
            }
        awaitClose { listener.remove() }
    }

    suspend fun sendMessage(chatId: String, text: String) {
        val currentUser = auth.currentUser ?: return

        val snapshot = db.collection("users").document(currentUser.uid).get().await()
        val userName = snapshot.getString("name")


        val message = hashMapOf(
            "text" to text,
            "senderId" to currentUser.uid,
            "senderName" to userName,
            "timestamp" to Timestamp.now()
        )

        // Add message to subcollection
        db.collection("chats/$chatId/messages").add(message).await()

        // Update chat metadata - CORRECTED VERSION
        val updates = mapOf(
            "lastMessage" to message,
            "timestamp" to Timestamp.now()
        )

        db.collection("chats").document(chatId).update(updates).await()
    }

//    suspend fun createGroupChat(
//        tripId: String,
//        tripTitle: String,
//        tripImage: String?,
//        participants: List<String>
//    ): String {
//        val chatData = hashMapOf(
//            "tripId" to tripId,
//            "name" to tripTitle,
//            "image" to tripImage,
//            "lastMessage" to "",
//            "timestamp" to Timestamp.now(),
//            "participants" to participants.distinct()
//        )
//
//        val result = chatsCollection.add(chatData).await()
//        return result.id
//    }

    suspend fun addParticipantToChat(chatId: String, userId: String) {
        db.collection("chats").document(chatId)
            .update(
                "participants", FieldValue.arrayUnion(userId),
                "lastRead.$userId", Timestamp(0, 0)
            )
            .await()
    }

    suspend fun markAsRead(chatId: String, userId: String) {
        db.collection("chats").document(chatId)
            .update("lastRead.$userId", FieldValue.serverTimestamp())
            .also { println("DEBUG: Updated lastRead for $userId") }
            .await()
    }

    private suspend fun getLastMessage(chatId: String): Message {
        val snapshot = db.collection("chats/$chatId/messages")
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .limit(1)
            .get()
            .await()

        return snapshot.documents.first().toObject(Message::class.java)!!
    }

    suspend fun deleteChat(tripId: String) {
        val snapshot = db.collection("chats")
            .whereEqualTo("tripId", tripId)
            .get()
            .await()

        for (document in snapshot.documents) {
            db.collection("chats").document(document.id).delete().await()
        }
    }


    fun getUnreadCount(chatId: String): Flow<Int> = callbackFlow {
        val currentUserId = auth.currentUser?.uid ?: ""

        // Prima otteniamo il lastRead timestamp per l'utente corrente
        val chatDocRef = db.collection("chats").document(chatId)

        val listener = chatDocRef.addSnapshotListener { snapshot, error ->
            if (error != null) {
                trySend(0)
                return@addSnapshotListener
            }

            val lastReadMap = snapshot?.get("lastRead") as? Map<String, Any> ?: emptyMap()
            val lastReadTimestamp = if(lastReadMap[currentUserId] != null) {lastReadMap[currentUserId] as Timestamp} else  {Timestamp.now()}
            println("DEBUG: da confrontare: ${lastReadTimestamp}")

            // DEBUG: stampa tutti i timestamp di tutti i messaggi nella chat
            db.collection("chats/$chatId/messages")
                .get()
                .addOnSuccessListener { allMessagesSnapshot ->
                    for (document in allMessagesSnapshot.documents) {
                        val timestamp = document.getTimestamp("timestamp")
                        Log.d("DEBUG", "Tutti i messaggi - timestamp: $timestamp")
                    }
                }
                .addOnFailureListener { exception ->
                    Log.e("DEBUG", "Errore nel recupero di tutti i messaggi: ${exception.message}")
                }

            // Ora cerchiamo i messaggi con timestamp successivo
            db.collection("chats/$chatId/messages")
                .whereGreaterThan("timestamp", lastReadTimestamp)
                .get()
                .addOnSuccessListener { querySnapshot ->
                    val unreadCount = querySnapshot.size()
                    trySend(unreadCount)
                }
                .addOnFailureListener {
                    trySend(0)
                }
        }

        awaitClose { listener.remove() }
    }

}
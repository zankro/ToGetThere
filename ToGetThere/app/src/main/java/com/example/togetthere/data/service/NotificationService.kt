package com.example.togetthere.data.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.NotificationManager.IMPORTANCE_DEFAULT
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_IMMUTABLE
import android.content.Context
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.togetthere.MainActivity
import com.example.togetthere.R
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.google.firebase.messaging.RemoteMessage.Notification

class NotificationService: FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        super.onNewToken(token)

        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return
        Firebase.firestore.collection("users")
            .document(uid)
            .update("fcmToken", token)
    }

    override fun onMessageReceived(message: RemoteMessage) {
        message.notification?.let { message ->
            sendNotification(message)
        }
    }

    private fun sendNotification(message: RemoteMessage.Notification) {
        val intent = Intent(this, MainActivity::class.java).apply {
            addFlags(FLAG_ACTIVITY_CLEAR_TOP)
        }

        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent, FLAG_IMMUTABLE
        )

        val channelId = this.getString(R.string.default_notification_channel_id)
        val channelName = "Notifications"

        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setContentTitle(message.title)
            .setContentText(message.body)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_HIGH)

        val manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        manager.notify((System.currentTimeMillis() % Int.MAX_VALUE).toInt(), notificationBuilder.build())
    }

}

fun startFirestoreListener(context: Context): ListenerRegistration? {
    val currentUserId = FirebaseAuth.getInstance().currentUser?.uid ?: return null
    val listenerRegistration = com.google.firebase.ktx.Firebase.firestore.collection("join_trip_requests")
        .whereEqualTo("creatorId", currentUserId)
        .whereEqualTo("isRead", false)
        .addSnapshotListener { snapshots, error ->
            if (error != null || snapshots == null) return@addSnapshotListener

            for (docChange in snapshots.documentChanges) {
                if (docChange.type == DocumentChange.Type.ADDED) {
                    val data = docChange.document.data
                    val userName = data["userName"] as? String ?: "Qualcuno"
                    val tripId = data["tripId"] as? String ?: ""
                    showNotification(
                        context = context,
                        title = "New join trip request",
                        body = "$userName wants to join your trip!"
                    )
                }
            }
        }
    return listenerRegistration
}

fun showNotification(context: Context, title: String, body: String) {
    val channelId = "trip_notifications"
    val notificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    // Creazione canale per Android O e versioni successive
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val channel = NotificationChannel(
            channelId,
            "Notifiche Viaggi",
            NotificationManager.IMPORTANCE_DEFAULT
        )
        notificationManager.createNotificationChannel(channel)
    }

    val notificationBuilder = NotificationCompat.Builder(context, channelId)
        .setSmallIcon(R.drawable.ic_launcher_foreground)
        .setContentTitle(title)
        .setContentText(body)
        .setAutoCancel(true)

    notificationManager.notify(System.currentTimeMillis().toInt(), notificationBuilder.build())
}

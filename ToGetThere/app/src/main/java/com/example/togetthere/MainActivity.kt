package com.example.togetthere

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.app.NotificationCompat
import com.example.togetthere.data.service.startFirestoreListener
import com.example.togetthere.ui.navigation.ToGetThereApp
import com.example.togetthere.ui.theme.ToGetThereTheme
import com.example.togetthere.utils.SessionPrefs
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class MainActivity : ComponentActivity() {
//    private val mainRepository = MainRepository()
//    private val tripViewModel = TripViewModel(mainRepository.tripRepository, 1)

    private var listenerRegistration: ListenerRegistration? = null

    val googleAuthUiClient by lazy {
        GoogleAuthUiClient(
            context = applicationContext

        )
    }

    override fun onStart() {
        super.onStart()

        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser != null) {
            listenerRegistration = startFirestoreListener(this)
        }

        FirebaseAuth.getInstance().addAuthStateListener { auth ->
            val user = auth.currentUser
            if (user != null) {
                listenerRegistration = startFirestoreListener(this)
            } else {
                listenerRegistration?.remove()
                listenerRegistration = null
            }
        }
    }



    override fun onStop() {
        super.onStop()
        listenerRegistration?.remove()
        listenerRegistration = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (!SessionPrefs.hasLoggedOnce(this)) {
            FirebaseAuth.getInstance().signOut()
        }
        val appContainer = (application as ToGetThereApplication).container
        enableEdgeToEdge()
        setContent {
            ToGetThereTheme {
                ToGetThereApp(appContainer, googleAuthUiClient = googleAuthUiClient)
            }
        }

    }
}

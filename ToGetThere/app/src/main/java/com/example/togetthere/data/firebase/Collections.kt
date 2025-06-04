package com.example.togetthere.data.firebase

import com.google.firebase.Firebase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.google.firebase.firestore.firestore

object Collections {
    private const val C_TRIPS = "trips"
    private const val C_USERS = "users"
    private const val C_REVIEWS = "reviews"

    private val db: FirebaseFirestore
        get() = Firebase.firestore

    init {
        db.firestoreSettings = FirebaseFirestoreSettings.Builder()
            .setPersistenceEnabled(true) // per offline caching
            .build()
    }

    val trips = db.collection(C_TRIPS)
    val users = db.collection(C_USERS)
    val reviews = db.collection(C_REVIEWS)
}
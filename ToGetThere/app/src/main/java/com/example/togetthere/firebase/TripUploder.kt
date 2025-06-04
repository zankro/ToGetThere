package com.example.togetthere.firebase

import android.net.Uri
import android.util.Log
import com.example.togetthere.model.Message
import com.example.togetthere.model.Trip
import com.example.togetthere.model.TripPhoto
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.Timestamp


fun Trip.toFirestoreTrip(): Map<String, Any?> {
    return mapOf(
        "tripId" to tripId,
        "name" to name,
        "type" to type,
        "destination" to destination,
        "creator" to creator,
        "numParticipants" to numParticipants,
        "maxParticipants" to maxParticipants,
        "reservationsList" to reservationsList,
        "startDate" to startDate,
        "endDate" to endDate,
        "images" to images.map {
            mapOf(
                "url" to it.url,
            )
        },
        "tags" to tags,
        "description" to description,
        "stops" to stops,
        "priceEstimation" to priceEstimation,
        "suggestedActivities" to suggestedActivities,
        "filters" to filters,
        "ageRange" to ageRange,
        "reviews" to reviews,
        "favoritesUsers" to favoritesUsers
    )
}

fun uploadTripImage(
    tripId: Int,
    imageUri: Uri,
    imageIndex: Int,
    onSuccess: (String) -> Unit,
    onFailure: (Exception) -> Unit
) {
    val userId = FirebaseAuth.getInstance().currentUser?.uid
    if (userId == null) {
        onFailure(Exception("User not authenticated"))
        return
    }

    val timestamp = System.currentTimeMillis()
    Log.d("Firebase", "Starting trip image upload for trip: $tripId, image index: $imageIndex")

    val storageRef = FirebaseStorage.getInstance().reference
    val imageRef = storageRef.child("trip_images/$userId/trip_${tripId}/image_${imageIndex}_${timestamp}.jpg")

    imageRef.putFile(imageUri)
        .addOnSuccessListener { taskSnapshot ->
            Log.d("Firebase", "Trip image upload successful, getting download URL...")
            imageRef.downloadUrl
                .addOnSuccessListener { uri ->
                    Log.d("Firebase", "Trip image download URL obtained: $uri")
                    onSuccess(uri.toString())
                }
                .addOnFailureListener { e ->
                    Log.e("Firebase", "Failed to get trip image download URL", e)
                    onFailure(e)
                }
        }
        .addOnFailureListener { e ->
            Log.e("Firebase", "Trip image upload failed", e)
            onFailure(e)
        }


}

fun uploadTripImages(
    tripId: Int,
    imageUris: List<Uri>,
    onSuccess: (List<TripPhoto>) -> Unit,
    onFailure: (Exception) -> Unit
) {
    Log.d("Firebase", "uploadTripImages called with ${imageUris.size} images")
    Log.d("Firebase", "Image URIs: $imageUris")

    if (imageUris.isEmpty()) {
        Log.d("Firebase", "No images to upload, returning empty list")
        onSuccess(emptyList())
        return
    }

    val uploadedPhotos = mutableListOf<TripPhoto>()
    var uploadCount = 0
    var hasError = false

    imageUris.forEachIndexed { index, imageUri ->
        Log.d("Firebase", "Starting upload for image $index: $imageUri")

        uploadTripImage(
            tripId = tripId,
            imageUri = imageUri,
            imageIndex = index,
            onSuccess = { imageUrl ->
                Log.d("Firebase", "Image $index uploaded successfully: $imageUrl")

                if (!hasError) {
                    val tripPhoto = TripPhoto(

                        url = imageUrl,

                    )
                    uploadedPhotos.add(tripPhoto)
                    uploadCount++

                    Log.d("Firebase", "Upload progress: $uploadCount/${imageUris.size}")

                    if (uploadCount == imageUris.size) {
                        Log.d("Firebase", "All trip images uploaded successfully: $uploadedPhotos")
                        onSuccess(uploadedPhotos)
                    }
                }
            },
            onFailure = { e ->
                Log.e("Firebase", "Failed to upload image $index", e)

                if (!hasError) {
                    hasError = true
                    Log.e("Firebase", "Setting hasError to true, cleaning up images")

                    // Elimina le immagini già caricate in caso di errore
                    uploadedPhotos.forEach { photo ->
                        deleteTripImage(photo.url)
                    }

                    onFailure(e)
                }
            }
        )
    }
}

fun uploadTripToFirestore(
    trip: Trip,
    onSuccess: () -> Unit = {},
    onFailure: (Exception) -> Unit = {}
) {
    val db = Firebase.firestore
    val firestoreTrip = trip.toFirestoreTrip()

    db.collection("trips")
        .document(trip.tripId.toString())
        .set(firestoreTrip, SetOptions.merge())
        .addOnSuccessListener {
            Log.d("Firebase", "Trip ${trip.tripId} uploaded successfully to Firestore")

            // Dopo aver salvato il trip, crea la chat
            createTripChat(trip, onSuccess, onFailure)
        }
        .addOnFailureListener { exception ->
            Log.e("Firebase", "Error uploading trip to Firestore", exception)
            onFailure(exception)
        }
}

private fun createTripChat(
    trip: Trip,
    onSuccess: () -> Unit,
    onFailure: (Exception) -> Unit
) {
    val defaultMessage = Message(
        text = "Hi! Welcome to ${trip.name}",
        senderId = "",
        senderName = "",
        timestamp = Timestamp.now()
    )

    val chatData = hashMapOf(
        "image" to trip.images.firstOrNull()?.url.orEmpty(),
        "lastMessage" to defaultMessage,
        "name" to trip.name,
        "participants" to listOf(trip.creator),
        "tripId" to trip.tripId.toString(),
        "timestamp" to Timestamp.now(),
        "lastRead" to hashMapOf<String, Any>(
            trip.creator to Timestamp.now()
        )
    )

    Firebase.firestore.collection("chats")
        .add(chatData)
        .addOnSuccessListener {
            Log.d("Firebase", "Chat created successfully for trip ${trip.tripId}")
            onSuccess()
        }
        .addOnFailureListener { e ->
            Log.e("Firebase", "Error creating chat for trip ${trip.tripId}", e)
            onFailure(e)
        }
}

fun createTripWithImages(
    trip: Trip,
    imageUris: List<Uri>,
    onSuccess: (Trip) -> Unit,
    onFailure: (Exception) -> Unit
) {
    Log.d("Firebase", "createTripWithImages called")
    Log.d("Firebase", "Trip ID: ${trip.tripId}")
    Log.d("Firebase", "Number of images: ${imageUris.size}")
    Log.d("Firebase", "Image URIs: $imageUris")

    if (imageUris.isEmpty()) {
        Log.d("Firebase", "No images provided, creating trip without images")
        // Se non ci sono immagini, carica direttamente il viaggio
        val finalTrip = trip.copy(images = emptyList())
        uploadTripToFirestore(
            trip = finalTrip,
            onSuccess = {
                Log.d("Firebase", "Trip created successfully without images")
                onSuccess(finalTrip)
            },
            onFailure = onFailure
        )
    } else {
        Log.d("Firebase", "Starting image upload process")

        uploadTripImages(
            tripId = trip.tripId,
            imageUris = imageUris,
            onSuccess = { tripPhotos ->
                Log.d("Firebase", "Images uploaded successfully, got ${tripPhotos.size} photos")
                Log.d("Firebase", "TripPhotos: $tripPhotos")

                // Poi salva il viaggio con gli oggetti TripPhoto
                val finalTrip = trip.copy(images = tripPhotos)
                Log.d("Firebase", "Final trip with images: ${finalTrip.images}")

                uploadTripToFirestore(
                    trip = finalTrip,
                    onSuccess = {
                        Log.d("Firebase", "Trip saved to Firestore successfully with ${tripPhotos.size} images")
                        onSuccess(finalTrip)
                    },
                    onFailure = { e ->
                        Log.e("Firebase", "Failed to save trip to Firestore, cleaning up images", e)
                        // In caso di errore, elimina le immagini caricate
                        tripPhotos.forEach { photo ->
                            deleteTripImage(photo.url)
                        }
                        onFailure(e)
                    }
                )
            },
            onFailure = { e ->
                Log.e("Firebase", "Failed to upload images", e)
                onFailure(e)
            }
        )
    }
}

fun deleteTripImage(
    imageUrl: String?,
    onSuccess: () -> Unit = {},
    onFailure: (Exception) -> Unit = {}
) {
    if (imageUrl.isNullOrBlank()) {
        Log.d("Firebase", "No trip image URL to delete")
        onSuccess()
        return
    }

    try {
        val storageRef = FirebaseStorage.getInstance().getReferenceFromUrl(imageUrl)
        storageRef.delete()
            .addOnSuccessListener {
                Log.d("Firebase", "Trip image deleted successfully from Storage")
                onSuccess()
            }
            .addOnFailureListener { e ->
                Log.e("Firebase", "Failed to delete trip image from Storage", e)
                onFailure(e)
            }
    } catch (e: Exception) {
        Log.e("Firebase", "Error parsing trip image URL for deletion", e)
        onFailure(e)
    }
}
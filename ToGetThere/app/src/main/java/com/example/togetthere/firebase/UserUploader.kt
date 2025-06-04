package com.example.togetthere.firebase

import android.net.Uri
import android.util.Log
import com.example.togetthere.model.Destination
import com.example.togetthere.model.GenderType
import com.example.togetthere.model.SocialHandle
import com.example.togetthere.model.SocialPlatform
import com.example.togetthere.model.UserProfile
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage


fun UserProfile.toFirestoreUser(): Map<String, Any?> {
    return mapOf(
        "userId" to userId,
        "name" to name,
        "surname" to surname,
        "nickname" to nickname,
        "gender" to gender.name,
        "nationality" to nationality,
        "description" to description,
        "photo" to photo,
        "interests" to interests,
        "desiredDestinations" to desiredDestinations.map {
            mapOf("name" to it.name, "imageURL" to it.imageURL)
        },
        "socials" to socials.map {
            mapOf("platform" to it.platform.name, "username" to it.username)
        }
    )
}


fun saveUserCredentials(
    email: String,
    password: String,
    onSuccess: (String) -> Unit,
    onFailure: (Exception) -> Unit
) {
    val auth = FirebaseAuth.getInstance()

    auth.createUserWithEmailAndPassword(email, password)
        .addOnSuccessListener { authResult ->
            val userId = authResult.user?.uid
            if (userId != null) {
                Log.d("Firebase", "User credentials saved in Auth with ID: $userId")
                onSuccess(userId)
            } else {
                onFailure(Exception("User ID is null"))
            }
        }
        .addOnFailureListener { exception ->
            Log.e("Firebase", "Error saving user credentials in Auth", exception)
            onFailure(exception)
        }
}

fun uploadUserToFirestore(
    user: UserProfile,
    onSuccess: () -> Unit = {},
    onFailure: (Exception) -> Unit = {}
) {
    val db = Firebase.firestore
    val firestoreUser = user.toFirestoreUser()

    db.collection("users")
        .document(user.userId) // Usa l'ID specifico (stesso dell'Auth)
        .set(firestoreUser, SetOptions.merge())
        .addOnSuccessListener {
            Log.d("Firebase", "User ${user.userId} uploaded successfully to Firestore")
            onSuccess()
        }
        .addOnFailureListener { exception ->
            Log.e("Firebase", "Error uploading user to Firestore", exception)
            onFailure(exception)
        }
}

fun registerUser(
    email: String,
    password: String,
    userProfile: UserProfile,
    imageUri: Uri?,
    onSuccess: (UserProfile) -> Unit,
    onFailure: (Exception) -> Unit
) {
    saveUserCredentials(
        email = email,
        password = password,
        onSuccess = { userId ->

            fun uploadAndSaveUser(photoUrl: String? = null) {
                val finalProfile = userProfile.copy(
                    userId = userId,
                    photo = photoUrl
                )

                uploadUserToFirestore(
                    user = finalProfile,
                    onSuccess = { onSuccess(finalProfile) },
                    onFailure = { onFailure(it) }
                )
            }

            if (imageUri != null) {
                Log.d("Firebase", "Starting image upload for URI: $imageUri")
                uploadUserProfilePhoto(
                    userId = userId,
                    imageUri = imageUri,
                    onSuccess = { photoUrl ->
                        Log.d("Firebase", "Image uploaded successfully. URL: $photoUrl")
                        uploadAndSaveUser(photoUrl)
                    },
                    onFailure = { e ->
                        Log.e("Firebase", "Failed to upload image, skipping photo", e)
                        uploadAndSaveUser() // Prosegui senza foto se fallisce
                    }
                )
            } else {
                Log.d("Firebase", "No image URI provided, skipping photo upload")
                uploadAndSaveUser()
            }
        },
        onFailure = { authException ->
            onFailure(authException)
        }
    )
}


fun uploadUserProfilePhoto(
    userId: String,
    imageUri: Uri,
    onSuccess: (String) -> Unit,
    onFailure: (Exception) -> Unit
) {
    Log.d("Firebase", "Starting photo upload for user: $userId with URI: $imageUri")
    val storageRef = FirebaseStorage.getInstance().reference
    val photoRef = storageRef.child("profile_photos/$userId.jpg")

    photoRef.putFile(imageUri)
        .addOnSuccessListener { taskSnapshot ->
            Log.d("Firebase", "Photo upload successful, getting download URL...")
            photoRef.downloadUrl
                .addOnSuccessListener { uri ->
                    Log.d("Firebase", "Download URL obtained: $uri")
                    onSuccess(uri.toString())
                }
                .addOnFailureListener { e ->
                    Log.e("Firebase", "Failed to get download URL", e)
                    onFailure(e)
                }
        }
        .addOnFailureListener { e ->
            Log.e("Firebase", "Photo upload failed", e)
            onFailure(e)
        }
}

fun DocumentSnapshot.toUserProfile(): UserProfile {
    return UserProfile(
        userId = getString("userId") ?: "",
        name = getString("name") ?: "",
        surname = getString("surname") ?: "",
        nickname = getString("nickname") ?: "",
        gender = getString("gender")?.let {
            try {
                GenderType.valueOf(it)
            } catch (e: Exception) {
                GenderType.OTHER
            }
        } ?: GenderType.OTHER,
        nationality = getString("nationality") ?: "",
        description = getString("description") ?: "",
        photo = getString("photo"), // Corretto: leggi direttamente la stringa URL
        interests = get("interests") as? List<String> ?: emptyList(),
        desiredDestinations = (get("desiredDestinations") as? List<Map<String, Any>>)?.mapNotNull {
            try {
                Destination(
                    name = it["name"] as? String ?: "",
                    imageURL = it["imageURL"] as? String ?: "" // Corretto: già una stringa
                )
            } catch (e: Exception) {
                null
            }
        } ?: emptyList(),
        socials = (get("socials") as? List<Map<String, Any>>)?.mapNotNull {
            try {
                SocialHandle(
                    platform = SocialPlatform.valueOf(it["platform"] as? String ?: "FACEBOOK"),
                    username = it["username"] as? String ?: ""
                )
            } catch (e: Exception) {
                null
            }
        } ?: emptyList()
    )
}

fun deleteUserProfilePhoto(
    currentPhotoUrl: String?,
    onSuccess: () -> Unit = {},
    onFailure: (Exception) -> Unit = {}
) {
    if (currentPhotoUrl.isNullOrBlank()) {
        Log.d("Firebase", "No photo URL to delete")
        onSuccess()
        return
    }

    try {
        val storageRef = FirebaseStorage.getInstance().getReferenceFromUrl(currentPhotoUrl)
        storageRef.delete()
            .addOnSuccessListener {
                Log.d("Firebase", "Photo deleted successfully from Storage")
                onSuccess()
            }
            .addOnFailureListener { e ->
                Log.e("Firebase", "Failed to delete photo from Storage", e)
                onFailure(e)
            }
    } catch (e: Exception) {
        Log.e("Firebase", "Error parsing photo URL for deletion", e)
        onFailure(e)
    }
}


fun updateUserProfilePhoto(
    userId: String,
    currentPhotoUrl: String?,
    newImageUri: Uri?,
    onSuccess: (String?) -> Unit,
    onFailure: (Exception) -> Unit
) {
    Log.d("Firebase", "Starting profile photo update for user: $userId")

    // Se newImageUri è null, vuol dire che si vuole rimuovere la foto
    if (newImageUri == null) {
        deleteUserProfilePhoto(
            currentPhotoUrl = currentPhotoUrl,
            onSuccess = {
                Log.d("Firebase", "Photo removed successfully")
                onSuccess(null)
            },
            onFailure = onFailure
        )
        return
    }

    // Prima carica la nuova foto
    uploadUserProfilePhoto(
        userId = userId,
        imageUri = newImageUri,
        onSuccess = { newPhotoUrl ->
            Log.d("Firebase", "New photo uploaded successfully: $newPhotoUrl")

            // Poi cancella la vecchia foto (se esiste)
            if (!currentPhotoUrl.isNullOrBlank()) {
                deleteUserProfilePhoto(
                    currentPhotoUrl = currentPhotoUrl,
                    onSuccess = {
                        Log.d("Firebase", "Old photo deleted successfully")
                        onSuccess(newPhotoUrl)
                    },
                    onFailure = { e ->
                        Log.w("Firebase", "Failed to delete old photo, but new photo was uploaded", e)
                        // Anche se la cancellazione della vecchia foto fallisce,
                        // consideriamo l'operazione riuscita perché la nuova foto è stata caricata
                        onSuccess(newPhotoUrl)
                    }
                )
            } else {
                onSuccess(newPhotoUrl)
            }
        },
        onFailure = onFailure
    )
}


fun updateUserProfileWithPhoto(
    userProfile: UserProfile,
    newImageUri: Uri?,
    onSuccess: (UserProfile) -> Unit,
    onFailure: (Exception) -> Unit
) {
    updateUserProfilePhoto(
        userId = userProfile.userId,
        currentPhotoUrl = userProfile.photo,
        newImageUri = newImageUri,
        onSuccess = { newPhotoUrl ->
            val updatedProfile = userProfile.copy(photo = newPhotoUrl)

            // Aggiorna il profilo in Firestore
            uploadUserToFirestore(
                user = updatedProfile,
                onSuccess = {
                    Log.d("Firebase", "User profile updated successfully with new photo")
                    onSuccess(updatedProfile)
                },
                onFailure = onFailure
            )
        },
        onFailure = onFailure
    )
}

fun uploadDreamTripImage(
    imageUri: Uri,
    destinationName: String,
    onSuccess: (String) -> Unit,
    onFailure: (Exception) -> Unit
) {
    val userId = FirebaseAuth.getInstance().currentUser?.uid
    if (userId == null) {
        onFailure(Exception("User not authenticated"))
        return
    }

    // Crea un nome file sicuro dal nome della destinazione
    val safeFileName = destinationName.replace(Regex("[^a-zA-Z0-9]"), "_").lowercase()
    val timestamp = System.currentTimeMillis()

    Log.d("Firebase", "Starting dream trip image upload for destination: $destinationName")
    val storageRef = FirebaseStorage.getInstance().reference
    val imageRef = storageRef.child("dream_trips/$userId/${safeFileName}_${timestamp}.jpg")

    imageRef.putFile(imageUri)
        .addOnSuccessListener { taskSnapshot ->
            Log.d("Firebase", "Dream trip image upload successful, getting download URL...")
            imageRef.downloadUrl
                .addOnSuccessListener { uri ->
                    Log.d("Firebase", "Dream trip image download URL obtained: $uri")
                    onSuccess(uri.toString())
                }
                .addOnFailureListener { e ->
                    Log.e("Firebase", "Failed to get dream trip image download URL", e)
                    onFailure(e)
                }
        }
        .addOnFailureListener { e ->
            Log.e("Firebase", "Dream trip image upload failed", e)
            onFailure(e)
        }
}

fun deleteDreamTripImage(
    imageUrl: String?,
    onSuccess: () -> Unit = {},
    onFailure: (Exception) -> Unit = {}
) {
    if (imageUrl.isNullOrBlank()) {
        Log.d("Firebase", "No dream trip image URL to delete")
        onSuccess()
        return
    }

    try {
        val storageRef = FirebaseStorage.getInstance().getReferenceFromUrl(imageUrl)
        storageRef.delete()
            .addOnSuccessListener {
                Log.d("Firebase", "Dream trip image deleted successfully from Storage")
                onSuccess()
            }
            .addOnFailureListener { e ->
                Log.e("Firebase", "Failed to delete dream trip image from Storage", e)
                onFailure(e)
            }
    } catch (e: Exception) {
        Log.e("Firebase", "Error parsing dream trip image URL for deletion", e)
        onFailure(e)
    }
}

fun updateDreamTripImage(
    currentImageUrl: String?,
    newImageUri: Uri,
    destinationName: String,
    onSuccess: (String) -> Unit,
    onFailure: (Exception) -> Unit
) {
    Log.d("Firebase", "Starting dream trip image update for destination: $destinationName")

    // Prima carica la nuova immagine
    uploadDreamTripImage(
        imageUri = newImageUri,
        destinationName = destinationName,
        onSuccess = { newImageUrl ->
            Log.d("Firebase", "New dream trip image uploaded successfully: $newImageUrl")

            // Poi cancella la vecchia immagine (se esiste)
            if (!currentImageUrl.isNullOrBlank()) {
                deleteDreamTripImage(
                    imageUrl = currentImageUrl,
                    onSuccess = {
                        Log.d("Firebase", "Old dream trip image deleted successfully")
                        onSuccess(newImageUrl)
                    },
                    onFailure = { e ->
                        Log.w("Firebase", "Failed to delete old dream trip image, but new image was uploaded", e)
                        // Anche se la cancellazione della vecchia immagine fallisce,
                        // consideriamo l'operazione riuscita perché la nuova immagine è stata caricata
                        onSuccess(newImageUrl)
                    }
                )
            } else {
                onSuccess(newImageUrl)
            }
        },
        onFailure = onFailure
    )
}

fun deleteAllUserDreamTripImages(
    dreamTrips: List<Destination>,
    onSuccess: () -> Unit = {},
    onFailure: (Exception) -> Unit = {}
) {
    if (dreamTrips.isEmpty()) {
        onSuccess()
        return
    }

    var deletedCount = 0
    var errorCount = 0
    val totalImages = dreamTrips.count { !it.imageURL.isNullOrBlank() }

    if (totalImages == 0) {
        onSuccess()
        return
    }

    dreamTrips.forEach { destination ->
        if (!destination.imageURL.isNullOrBlank()) {
            deleteDreamTripImage(
                imageUrl = destination.imageURL,
                onSuccess = {
                    deletedCount++
                    if (deletedCount + errorCount == totalImages) {
                        if (errorCount == 0) {
                            onSuccess()
                        } else {
                            onFailure(Exception("Some images could not be deleted"))
                        }
                    }
                },
                onFailure = { e ->
                    errorCount++
                    Log.e("Firebase", "Failed to delete dream trip image: ${destination.imageURL}", e)
                    if (deletedCount + errorCount == totalImages) {
                        onFailure(Exception("Some images could not be deleted"))
                    }
                }
            )
        }
    }
}
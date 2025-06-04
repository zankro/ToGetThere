package com.example.togetthere.viewmodel

import android.net.Uri
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.togetthere.model.AgeRange
import com.example.togetthere.model.Filter
import com.example.togetthere.model.GenderType
import com.example.togetthere.model.MainRepository
import com.example.togetthere.model.NotificationType
import com.example.togetthere.model.PriceRange
import com.example.togetthere.model.Reservation
import com.example.togetthere.model.ReservationStatus
import com.example.togetthere.model.Stage
import com.example.togetthere.model.Trip
import com.example.togetthere.model.TripPhoto
import com.example.togetthere.model.TripReview
import com.example.togetthere.model.TripType
import com.example.togetthere.model.UserProfile
import com.example.togetthere.model.UserReview
import com.example.togetthere.utils.convertDateToMillis
import com.google.firebase.Firebase
import com.google.firebase.Timestamp
import com.google.firebase.firestore.firestore
import com.google.firebase.storage.storage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.UUID

//class TripViewModel(val model: TripRepository, val id: Int = -1) : ViewModel() {
//    val trip = model.getTripById(id)
//
//}

data class TripReviewWithAuthor(
    val id: Int,
    val score: Int,
    val title: String,
    val description: String,
    val authorId: String,
    val authorName: String,
    val authorPhoto: String?,
    val authorGender: GenderType?,
    val photos: List<TripPhoto>
)

class TripViewModel(
    private val model: MainRepository,
    private val tripId: Int
) : ViewModel() {
    private val _trip = MutableStateFlow<Trip?>(null)
    val trip: StateFlow<Trip?> = _trip

    private val _creator = MutableStateFlow<UserProfile?>(null)
    val creator: StateFlow<UserProfile?> = _creator

    private val _participants = MutableStateFlow<List<UserProfile>>(emptyList())
    val participants: StateFlow<List<UserProfile>> = _participants

    private val _uiState = MutableStateFlow<TripUiState>(TripUiState.Loading)
    val uiState: StateFlow<TripUiState> = _uiState

//    val requestsUsers: StateFlow<List<UserProfile>> = combine(
//        participants, MutableStateFlow(tripReservationsList)
//    ) { participants, reservations ->
//        reservations.mapNotNull { r -> participants.find { it.userId == r.bookerId } }
//    }.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())


    // var tripId by mutableIntStateOf(0)
    var tripName by mutableStateOf("")
    var tripType by mutableStateOf(TripType.ADVENTURE)
    var tripDestination by mutableStateOf("")
    var tripCreator by mutableStateOf("")
    var tripNumParticipants by mutableIntStateOf(0)
    var tripMaxParticipants by mutableIntStateOf(0)
    var tripReservationsList by mutableStateOf(emptyList<Reservation>())
    var tripStartDate by mutableStateOf("")
    var tripEndDate by mutableStateOf("")
    var tripImages by mutableStateOf(emptyList<TripPhoto>())
    var tripTags by mutableStateOf(emptyList<String>())
    var tripDescription by mutableStateOf("")
    var tripStops by mutableStateOf(emptyList<Stage>())
    var tripPrice by mutableStateOf(PriceRange(0, 100))
    var tripSuggestedActivities by mutableStateOf(emptyList<String>())
    var tripFilters by mutableStateOf(emptyList<Filter>())
    var tripAgeRange by mutableStateOf(AgeRange(20, 30))
    var tripReviews by mutableStateOf(emptyList<TripReview>())

    var nameError by mutableStateOf<String?>(null)
    var typeError by mutableStateOf<String?>(null)
    var destinationError by mutableStateOf<String?>(null)
    var numParticipantsError by mutableStateOf<String?>(null)
    var maxParticipantsError by mutableStateOf<String?>(null)
    var dateError by mutableStateOf<String?>(null)
    var imagesError by mutableStateOf<String?>(null)
    var tagsError by mutableStateOf<String?>(null)
    var descriptionError by mutableStateOf<String?>(null)
    var stopsError by mutableStateOf<String?>(null)
    var priceError by mutableStateOf<String?>(null)
    var suggestedActivitiesError by mutableStateOf<String?>(null)
    var filtersError by mutableStateOf<String?>(null)
    var ageRangeError by mutableStateOf<String?>(null)

    var reviewsWithAuthor by mutableStateOf(emptyList<TripReviewWithAuthor>())


    init {
        loadTrip()
        println("TripViewModel created for tripId = $tripId, hash: ${this.hashCode()}")
    }

    fun loadTrip() {
        viewModelScope.launch {
            val t = model.tripRepository.getTripById(tripId)
            if (t != null) {
                _trip.value = t
                Log.d("TripViewModel", "📌 Carico user con id: ${t.creator}")
                _creator.value = model.userProfileRepository.getUserById(t.creator)
//                _participants.value = model.userProfileRepository.getUsersByIds(
//                    t.reservationsList.filter { it.status == ReservationStatus.CONFIRMED }
//                        .map { it.bookerId }
//                )
                _participants.value = model.userProfileRepository.getUsersByIds(
                    t.reservationsList.map { it.bookerId }
                )


                _uiState.value = TripUiState.Success(t)

                // Popola i campi modificabili
                tripName = t.name
                tripType = t.type
                tripDestination = t.destination
                tripCreator = t.creator
                tripNumParticipants = t.numParticipants
                tripMaxParticipants = t.maxParticipants
                tripReservationsList = t.reservationsList
                tripStartDate = t.startDate
                tripEndDate = t.endDate
                tripImages = t.images
                tripTags = t.tags
                tripDescription = t.description
                tripStops = t.stops
                tripPrice = t.priceEstimation
                tripSuggestedActivities = t.suggestedActivities
                tripFilters = t.filters
                tripAgeRange = t.ageRange

                loadReviews()
            } else {
                _trip.value = null
                _uiState.value = TripUiState.Error("Viaggio non trovato")
            }
        }
    }

    fun getTripId(): Int {
        return tripId
    }

    fun removeTrip() {
        viewModelScope.launch {
            model.chatRepository.deleteChat(tripId.toString())
            model.tripRepository.removeTrip(tripId)
        }
    }

    // model.userProfile in TripViewModel?
//    fun getParticipants(): List<UserProfile> {
//        return model.userProfileRepository.getUsersByIds(
//            tripReservationsList
//                .filter { it.status == ReservationStatus.CONFIRMED}
//                .map { it.bookerId })
//
//    }
//
//    fun getReservationsUsers(): List<UserProfile> {
//        return model.userProfileRepository.getUsersByIds(
//            tripReservationsList.map { it.bookerId })
//    }

    fun joinTrip(userId: String) {
        viewModelScope.launch {
            model.tripRepository.updateTrip(
                trip.value!!.copy(
                    reservationsList = trip.value!!.reservationsList + Reservation(
                        bookerId = userId,
                        numAdults = 1,
                        numChildren = 0,
                        status = ReservationStatus.PENDING
                    )
                )
            )
            sendJoinRequest(trip.value!!, userId)
            loadTrip()
        }
    }

    fun acceptReservation(bookerId: String) {
        viewModelScope.launch {
            val currentTrip = trip.value ?: return@launch
            val reservation = currentTrip.reservationsList.find { it.bookerId == bookerId } ?: return@launch

            val newNumParticipants = currentTrip.numParticipants + reservation.numAdults + reservation.numChildren

            val db = Firebase.firestore

            if (newNumParticipants <= currentTrip.maxParticipants) {
                val updatedTrip = currentTrip.copy(
                    reservationsList = currentTrip.reservationsList.map {
                        if (it.bookerId == bookerId) it.copy(status = ReservationStatus.CONFIRMED) else it
                    },
                    numParticipants = newNumParticipants
                )
                model.tripRepository.updateTrip(updatedTrip)
                sendApplicationUpdate(trip.value!!, bookerId)
                loadTrip()
            }

            val querySnapshot = db.collection("chats")
                .whereEqualTo("tripId", currentTrip.tripId.toString())
                .get()
                .await()

            for (document in querySnapshot.documents) {
                val chatId = document.id
                model.chatRepository.addParticipantToChat(chatId, bookerId)
            }
        }
    }

    fun rejectReservation(bookerId: String) {
        viewModelScope.launch {
            val currentTrip = trip.value ?: return@launch

            val updatedTrip = currentTrip.copy(
                reservationsList = currentTrip.reservationsList.filter { it.bookerId != bookerId }
            )

            model.tripRepository.updateTrip(updatedTrip)
            sendApplicationUpdate(trip.value!!, bookerId)
            loadTrip()
        }
    }

    fun hasUserParticipated(userId: String): Boolean {
        if(tripCreator == userId) return true

        return trip.value!!.reservationsList
            .filter { it.status == ReservationStatus.CONFIRMED }
            .any { it.bookerId == userId }
    }

    fun hasUserApplied(userId: String): Boolean {
        println("TripViewModel.hasUserApplied: $userId")
        println("hasUserApplied, trip.value: ${trip.value}")
        return trip.value!!.reservationsList
            .filter { it.status == ReservationStatus.PENDING }
            .any { it.bookerId == userId }
    }

    fun isUserConfirmed(userId: String): Boolean {
        return trip.value?.reservationsList
            ?.any { it.bookerId == userId && it.status == ReservationStatus.CONFIRMED } ?: false
    }

    fun getCreatorId(): String {
        return tripCreator
    }

//    fun getCreator(): UserProfile {
//        viewModelScope.launch {
//            _creator.value = model.userProfileRepository.getUserById(tripCreator)
//        }
//        return model.userProfileRepository.getUserById(tripCreator)!!
//    }

    fun addGalleryImages(uris: List<Uri>) {
        viewModelScope.launch {
            val uploadedPhotos = mutableListOf<TripPhoto>()

            for (uri in uris) {
                try {
                    val storageRef = Firebase.storage.reference
                    val imageRef = storageRef.child("trip_images/${UUID.randomUUID()}.jpg")
                    val uploadTask = imageRef.putFile(uri).await()
                    val downloadUrl = imageRef.downloadUrl.await()
                    uploadedPhotos.add(TripPhoto(downloadUrl.toString()))
                } catch (e: Exception) {
                    Log.e("TripViewModel", "Image upload failed: ${e.localizedMessage}")
                }
            }

            if (uploadedPhotos.isNotEmpty()) {
                tripImages = tripImages + uploadedPhotos

                _trip.value?.let { currentTrip ->
                    val updatedTrip = currentTrip.copy(images = tripImages)
                    model.tripRepository.updateTrip(updatedTrip)
                    _trip.value = updatedTrip
                }
            }
        }
    }

    fun removeImage(image: TripPhoto) {
        tripImages = tripImages.filterNot { it == image }

        viewModelScope.launch {
            _trip.value?.let { currentTrip ->
                val updatedTrip = currentTrip.copy(images = tripImages)
                model.tripRepository.updateTrip(updatedTrip)
                _trip.value = updatedTrip
            }
        }
    }

    fun resetFields() {
        tripName = trip.value?.name ?: ""
        tripType = trip.value?.type ?: TripType.ADVENTURE
        tripDestination = trip.value?.destination ?: ""
        tripCreator = trip.value?.creator ?: ""
        tripNumParticipants = trip.value?.numParticipants ?: 0
        tripMaxParticipants = trip.value?.maxParticipants ?: 0
        tripReservationsList = trip.value?.reservationsList ?: emptyList()
        tripStartDate = trip.value?.startDate ?: ""
        tripEndDate = trip.value?.endDate ?: ""
        tripImages = trip.value?.images ?: emptyList()
        tripTags = trip.value?.tags ?: emptyList()
        tripDescription = trip.value?.description ?: ""
        tripStops = trip.value?.stops ?: emptyList()
        tripPrice = trip.value?.priceEstimation ?: PriceRange(0, 100)
        tripSuggestedActivities = trip.value?.suggestedActivities ?: emptyList()
        tripFilters = trip.value?.filters ?: emptyList()
        tripAgeRange = trip.value?.ageRange ?: AgeRange(20, 30)
        tripReviews = trip.value?.reviews ?: emptyList()
        resetErrors()
    }

    fun validate(): Boolean {
        checkName()
        checkType()
        checkWhere()
        checkMaxParticipants()
//        checkDate()
        checkImages()
        checkTags()
        checkDescription()
        checkStops()
        checkPrice()
        checkAgeRange()
        checkSuggestedActivities()

        val valid = nameError.isNullOrEmpty() &&
                typeError.isNullOrEmpty() &&
                destinationError.isNullOrEmpty() &&
                maxParticipantsError.isNullOrEmpty() &&
                dateError.isNullOrEmpty() &&
                imagesError.isNullOrEmpty() &&
                tagsError.isNullOrEmpty() &&
                descriptionError.isNullOrEmpty() &&
                stopsError.isNullOrEmpty() &&
                priceError.isNullOrEmpty() &&
                suggestedActivitiesError.isNullOrEmpty() &&
                filtersError.isNullOrEmpty() &&
                ageRangeError.isNullOrEmpty()

        return valid
    }

    fun applyValidatedTrip() {
        val sortedStops = tripStops.sortedBy {
            convertDateToMillis(it.startDate) ?: Long.MAX_VALUE
        }

        val updatedTrip = Trip(
            tripId = tripId,
            name = tripName,
            type = tripType,
            destination = tripDestination,
            creator = tripCreator,
            numParticipants = tripNumParticipants,
            maxParticipants = tripMaxParticipants,
            reservationsList = tripReservationsList,
            startDate = tripStartDate,
            endDate = tripEndDate,
            images = tripImages,
            tags = tripTags,
            description = tripDescription,
            stops = sortedStops.map { Stage(it.stageName, it.startDate, it.endDate, it.freeRoaming) },
            priceEstimation = tripPrice,
            suggestedActivities = tripSuggestedActivities,
            filters = tripFilters,
            ageRange = tripAgeRange
        )

        viewModelScope.launch {
            model.tripRepository.updateTrip(updatedTrip)
            _trip.value = updatedTrip
            resetErrors()
        }
    }

    // Validation Functions
    private fun checkName() {
        nameError = if (tripName.isBlank()) "Name cannot be empty" else null
    }

    private fun checkType() {
        typeError = if (tripType.toString().isBlank()) "Type cannot be empty" else null
    }

    private fun checkWhere() {
        destinationError = if (tripDestination.isBlank()) "Location cannot be empty" else null
    }

    private fun checkNumParticipants() {
        numParticipantsError = if (tripNumParticipants == 0) "Number of participants cannot be empty" else null
    }

    private fun checkMaxParticipants() {
        maxParticipantsError = if (tripMaxParticipants <= 1) "Max number of participants must be greater than 1" else null
    }

    private fun checkDate() {
        val today: LocalDate = LocalDate.now()
        val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
        val formattedDate = today.format(formatter)

        val startDateMillis = convertDateToMillis(tripStartDate)
        val currentDateMillis = convertDateToMillis(formattedDate)

        dateError = when {
            tripStartDate.isBlank() -> "Start date cannot be empty"
            tripEndDate.isBlank() -> "End date cannot be empty"
            startDateMillis != null && currentDateMillis != null && startDateMillis < currentDateMillis -> "Start date must be in the future"
            tripStartDate > tripEndDate -> "Start date must be before end date"
            else -> null
        }
    }

    fun isTripOver(): Boolean {
        val today: LocalDate = LocalDate.now()
        val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
        val formattedDate = today.format(formatter)

        val endDateMillis = convertDateToMillis(tripEndDate)
        val currentDateMillis = convertDateToMillis(formattedDate)

        return endDateMillis != null && currentDateMillis != null && endDateMillis < currentDateMillis

    }

    private fun checkImages() {
        imagesError = if (tripImages.isEmpty()) "Images cannot be empty" else null
    }

    private fun checkTags() {
        tagsError = when{
            tripTags.size > 6 -> "Can't have more than 6 tags"
            tripTags.any { it.length > 15 } -> "Tags can't be longer than 15 characters"
            else -> null
        }
//        tagsError = if (tripTags.isEmpty()) "Tags cannot be empty" else null
    }

    private fun checkDescription() {
        descriptionError = if (tripDescription.isBlank()) "Description cannot be empty" else null
    }

//    private fun checkStops() {
//        when{
//            tripStops.isEmpty() -> stopsError = "Stops cannot be empty"
//            tripStops.any { it.stageName.isBlank() } -> stopsError = "All stops must have a name"
//        }
//    }

    private fun checkStops() {
        val today = LocalDate.now()
        val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
        val todayStr = today.format(formatter)
        val todayMillis = convertDateToMillis(todayStr)

        stopsError = when {
            tripStops.isEmpty() -> "Stops cannot be empty"
            tripStops.any { it.stageName.isBlank() } -> "All stops must have a name"
            tripStops.any { it.startDate.isBlank() || it.endDate.isBlank() } -> "Each stop must have a date range"
            todayMillis == null -> "Date conversion error"
            tripStops.any {
                val startMillis = convertDateToMillis(it.startDate)
                val endMillis = convertDateToMillis(it.endDate)
                (startMillis != null && startMillis < todayMillis) ||
                        (endMillis != null && endMillis < todayMillis)
            } -> "Stops cannot be in the past"
            !areStopsInChronologicalOrder(tripStops) -> "Stops must be in chronological order"
            else -> null
        }
    }

    private fun areStopsInChronologicalOrder(stops: List<Stage>): Boolean {
        return stops.sortedBy { it.startDate }.zipWithNext().all {
            it.first.endDate <= it.second.startDate
        }
    }

    private fun areDatesOverlapping(stops: List<Stage>): Boolean {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val sorted = stops.sortedBy { LocalDate.parse(it.startDate, formatter) }
        for (i in 0 until sorted.lastIndex) {
            val currentEnd = LocalDate.parse(sorted[i].endDate, formatter)
            val nextStart = LocalDate.parse(sorted[i + 1].startDate, formatter)
            if (!currentEnd.isBefore(nextStart)) return true
        }
        return false
    }

    private fun checkPrice() {
        when{
            tripPrice.min == 0 -> priceError = "Price cannot be empty"
            tripPrice.min > tripPrice.max -> priceError = "Min price must be less or equal than max price"
        }
    }

    private fun checkAgeRange() {
        when{
            tripAgeRange.min == 0 -> ageRangeError = "Min age cannot be empty"
            tripAgeRange.min > tripAgeRange.max -> ageRangeError = "Min age must be less or equal than max age"
        }
    }

    private fun checkSuggestedActivities() {
        suggestedActivitiesError = if (tripSuggestedActivities.isEmpty()) "Suggested activities cannot be empty" else null
    }

    private fun resetErrors() {
        nameError = null
        typeError = null
        destinationError = null
        dateError = null
        imagesError = null
        tagsError = null
        descriptionError = null
        stopsError = null
        priceError = null
        suggestedActivitiesError = null
        filtersError = null
        ageRangeError = null
    }

    private fun loadReviews() {
        viewModelScope.launch {
            val trip = model.tripRepository.getTripById(tripId) ?: return@launch

            tripReviews = trip.reviews

            reviewsWithAuthor = trip.reviews.mapNotNull { review ->
                val author = model.userProfileRepository.getUserById(review.author)
                author?.let {
                    TripReviewWithAuthor(
                        id = review.id,
                        score = review.score,
                        title = review.title,
                        description = review.description,
                        authorId = it.userId,
                        authorName = it.name,
                        authorPhoto = it.photo,
                        authorGender = it.gender,
                        photos = review.photos
                    )
                }
            }
        }
    }

    fun addReview(
        tripId: Int,
        author: String,
        score: Int,
        title: String,
        description: String,
        photos: List<TripPhoto>,
    ) {
        val review = TripReview(
            id = 0,
            author = author,
            score = score,
            title = title,
            description = description,
            photos = photos
        )

        viewModelScope.launch {
            model.tripRepository.addReviewToTrip(tripId = tripId, review)
            loadReviews()
        }
    }

    fun addUserReview(
        reviewedUserId: String,
        reviewerUserId: String,
        description: String
    ) {
        viewModelScope.launch {
            val nextId = model.reviewRepository.getNextReviewId()

            val newReview = UserReview(
                id = nextId,
                author = reviewerUserId,
                receiverId = reviewedUserId,
                description = description,
                createdAt = System.currentTimeMillis()
            )

            model.reviewRepository.addUserReview(newReview)
        }
    }



    private fun sendJoinRequest(trip: Trip, requestUserId: String) {
        //val currentUserId = FirebaseAuth.getInstance().currentUser?.uid ?: return

        val request = mapOf(
            "type" to NotificationType.NewApplication,
            "tripId" to trip.tripId,
            "receiverId" to trip.creator,
            "senderId" to requestUserId,
            "pending" to true,
            "timestamp" to Timestamp.now()
        )

        Firebase.firestore.collection("notifications")
            .add(request)
            .addOnSuccessListener {
                Log.d("JOIN", "Richiesta inviata con successo")
            }
            .addOnFailureListener {
                Log.e("JOIN", "Errore nell'invio", it)
            }
    }

    private fun sendApplicationUpdate(trip: Trip, requestUserId: String) {
        //val currentUserId = FirebaseAuth.getInstance().currentUser?.uid ?: return

        val request = mapOf(
            "type" to NotificationType.ApplicationStatusUpdate,
            "tripId" to trip.tripId,
            "receiverId" to requestUserId,
            "senderId" to trip.creator,
            "pending" to true,
            "timestamp" to Timestamp.now()
        )

        Firebase.firestore.collection("notifications")
            .add(request)
            .addOnSuccessListener {
                Log.d("JOIN", "Richiesta inviata con successo")
            }
            .addOnFailureListener {
                Log.e("JOIN", "Errore nell'invio", it)
            }
    }

    companion object {
        fun provideFactory(
            mainRepository: MainRepository,
            tripId: Int
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return TripViewModel(mainRepository, tripId) as T
            }
        }
    }
}

sealed interface TripUiState {
    object Loading : TripUiState
    data class Success(val trip: Trip) : TripUiState
    data class Error(val message: String) : TripUiState
}

// Factory for the TripViewModel, this will create the ViewModel with the correct parameters
//class TripViewModelFactory(
//    private val tripId: Int,
//    private val tripRepository: TripRepository
//) : ViewModelProvider.Factory {
//
//    override fun <T : ViewModel> create(modelClass: Class<T>): T {
//        if (modelClass.isAssignableFrom(TripViewModel::class.java)) {
//            return TripViewModel(tripRepository, tripId) as T
//        }
//        throw IllegalArgumentException("Unknown ViewModel class")
//    }
//}

//class TripViewModelFactory(
//    private val tripRepository: TripRepository,
//    private val tripId: Int
//) : ViewModelProvider.Factory {
//
//    override fun <T : ViewModel> create(modelClass: Class<T>): T {
//        if (modelClass.isAssignableFrom(TripViewModel::class.java)) {
//            return TripViewModel(tripRepository, tripId) as T
//        }
//        throw IllegalArgumentException("Unknown ViewModel class")
//    }
//}
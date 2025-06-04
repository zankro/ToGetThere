package com.example.togetthere.viewmodel

import android.net.Uri
import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.togetthere.data.repository.TripRepository
import com.example.togetthere.firebase.createTripWithImages
import com.example.togetthere.model.AgeRange
import com.example.togetthere.model.Filter
import com.example.togetthere.model.MainRepository
import com.example.togetthere.model.PriceRange
import com.example.togetthere.model.Stage
import com.example.togetthere.model.Trip
import com.example.togetthere.model.TripPhoto
import com.example.togetthere.model.TripType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class TripsViewModel(val model: MainRepository) : ViewModel() {

    private val _allTrips = MutableStateFlow<List<Trip>>(emptyList())
    val allTrips: StateFlow<List<Trip>> = _allTrips.asStateFlow()

    init {
        viewModelScope.launch {
            model.tripRepository.getAllTrips().collect {
                _allTrips.value = it
            }
        }
    }

    companion object {
        fun provideFactory(mainRepository: MainRepository): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return TripsViewModel(mainRepository) as T
                }
            }
    }

    private val _currentTrip = MutableStateFlow<Trip?>(null)
    val currentTrip: StateFlow<Trip?> = _currentTrip.asStateFlow()

    // Editable fields
    val name = MutableStateFlow("")
    val type = MutableStateFlow(TripType.ADVENTURE)
    val destination = MutableStateFlow("")
    val creator = MutableStateFlow("")
    val numParticipants = MutableStateFlow(0)
    val maxParticipants = MutableStateFlow(1)
    val startDate = MutableStateFlow("")
    val endDate = MutableStateFlow("")
    val images = MutableStateFlow<List<TripPhoto>>(emptyList())
    val tags = MutableStateFlow<List<String>>(emptyList())
    val description = MutableStateFlow("")
    val stops = MutableStateFlow<List<Stage>>(emptyList())
    val priceEstimation = MutableStateFlow(PriceRange(0, 0))
    val suggestedActivities = MutableStateFlow<List<String>>(emptyList())
    val filters = MutableStateFlow<List<Filter>>(emptyList())
    val ageRange = MutableStateFlow(AgeRange(18, 35))

    // Validation error holders
    val nameError = MutableStateFlow<String?>(null)
    val destinationError = MutableStateFlow<String?>(null)
    val dateError = MutableStateFlow<String?>(null)
    val descriptionError = MutableStateFlow<String?>(null)

    /***************************** LOAD *********************************/
    fun loadTrip(tripId: Int) {
        viewModelScope.launch {
            val selectedTrip = model.tripRepository.getTripById(tripId)
            _currentTrip.value = selectedTrip

            selectedTrip?.let {
                name.value = it.name
                type.value = it.type
                destination.value = it.destination
                creator.value = it.creator
                numParticipants.value = it.numParticipants
                maxParticipants.value = it.maxParticipants
                startDate.value = it.startDate
                endDate.value = it.endDate
                images.value = it.images
                tags.value = it.tags
                description.value = it.description
                stops.value = it.stops
                priceEstimation.value = it.priceEstimation
                suggestedActivities.value = it.suggestedActivities
                filters.value = it.filters
                ageRange.value = it.ageRange
            }
        }
    }

    /**************************** CREATION *****************************/
    fun startCreatingNewTrip(creatorId: String) {
        _currentTrip.value = null

        name.value = ""
        type.value = TripType.ADVENTURE
        destination.value = ""
        creator.value = creatorId
        numParticipants.value = 1
        maxParticipants.value = 10
        startDate.value = ""
        endDate.value = ""
        images.value = emptyList()
        tags.value = emptyList()
        description.value = ""
        stops.value = emptyList()
        priceEstimation.value = PriceRange(0, 0)
        suggestedActivities.value = emptyList()
        filters.value = emptyList()
        ageRange.value = AgeRange(18, 35)
    }

    fun validateAndCreate(trip: Trip) {
        viewModelScope.launch {
            val newTripId = (_allTrips.value.maxOfOrNull { it.tripId } ?: 0) + 1


            val tripWithId = trip.copy(tripId = newTripId)


            // Estrai gli URI delle immagini dal Trip object
            val imageUrisToUpload = tripWithId.images.mapNotNull { tripPhoto ->
                try {
                    tripPhoto.url.toUri()
                } catch (e: Exception) {
                    println("[WARNING] Invalid URI in trip images: ${tripPhoto.url}")
                    null
                }
            }

            println("[DEBUG] Image URIs to upload: $imageUrisToUpload (${imageUrisToUpload.size} images)")

            // Crea il trip senza le immagini (verranno aggiunte dopo l'upload)
            val tripWithoutImages = tripWithId.copy(images = emptyList())

            createTripWithImages(
                trip = tripWithoutImages,
                imageUris = imageUrisToUpload,
                onSuccess = { createdTrip ->
                    println("[DEBUG] Trip created successfully: $createdTrip")
                    // Aggiorna il tuo stato locale se necessario
                    // _allTrips.value = _allTrips.value + createdTrip
                },
                onFailure = { exception ->
                    println("[ERROR] Failed to create trip: ${exception.message}")
                    // Gestisci l'errore (mostra un messaggio all'utente, ecc.)
                }
            )
        }
    }

    private val _trip = MutableStateFlow<Trip?>(null)
    val trip: StateFlow<Trip?> = _trip

    fun loadTripById(tripId: Int) {
        viewModelScope.launch {
            val trip = model.tripRepository.getTripById(tripId)
            _trip.value = trip
        }
    }

    /*
        fun validateAndCreate(): Trip? {
            if (!validateFields()) return null

            val newTripId = (allTrips.value.maxOfOrNull { it.tripId } ?: 0) + 1

            val newTrip = Trip(
                tripId = newTripId,
                name = name.value,
                type = type.value,
                destination = destination.value,
                creator = creator.value,
                numParticipants = numParticipants.value,
                maxParticipants = maxParticipants.value,
                startDate = startDate.value,
                endDate = endDate.value,
                images = images.value,
                tags = tags.value,
                description = description.value,
                stops = stops.value,
                priceEstimation = priceEstimation.value,
                suggestedActivities = suggestedActivities.value,
                filters = filters.value,
                ageRange = ageRange.value,
                reservationsList = listOf(),
                reviews = listOf()
            )

            // Aggiungi alla lista
            model.addTrip(newTrip)

            return newTrip
        }*/

    fun validateAndUpdate(): Boolean {
        if (!validateFields()) return false

        _currentTrip.value = _currentTrip.value?.copy(
            name = name.value,
            type = type.value,
            destination = destination.value,
            numParticipants = numParticipants.value,
            maxParticipants = maxParticipants.value,
            startDate = startDate.value,
            endDate = endDate.value,
            images = images.value,
            tags = tags.value,
            description = description.value,
            stops = stops.value,
            priceEstimation = priceEstimation.value,
            suggestedActivities = suggestedActivities.value,
            filters = filters.value,
            ageRange = ageRange.value
        )

        return true
    }

    /**************************** DELETE TRIP FROM THE LIST *****************************/
    fun deleteTrip(tripId: Int, userId: Int) {
        viewModelScope.launch {
            model.tripRepository.removeTrip(tripId)
        }
        //_filteredTrips.value = _filteredTrips.value.filter { it.tripId != tripId }
        //loadCreatedTrips(userId)
    }


    private fun validateFields(): Boolean {
        var isValid = true

        if (name.value.length < 3) {
            nameError.value = "Il nome deve avere almeno 3 caratteri"
            isValid = false
        } else nameError.value = null

        if (destination.value.isBlank()) {
            destinationError.value = "Destinazione obbligatoria"
            isValid = false
        } else destinationError.value = null

        if (description.value.length > 400) {
            descriptionError.value = "Massimo 400 caratteri"
            isValid = false
        } else descriptionError.value = null

        if (startDate.value.isBlank() || endDate.value.isBlank()) {
            dateError.value = "Inserisci le date"
            isValid = false
        } else dateError.value = null

        return isValid
    }



}

//object Factory : ViewModelProvider.Factory{
//    private val model: MainRepository = MainRepository()
//
//    override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
//        return when{
//            modelClass.isAssignableFrom(TripsViewModel::class.java)->
//                TripsViewModel(model.tripRepository) as T
////            modelClass.isAssignableFrom(TripViewModel::class.java)->
////                TripViewModel(model.tripRepository) as T
////            modelClass.isAssignableFrom(TheCounterViewModel::class.java)->
////                TheCounterViewModel(model) as T
//            else -> throw IllegalArgumentException("Unknown ViewModel")
//        }
//    }
//}

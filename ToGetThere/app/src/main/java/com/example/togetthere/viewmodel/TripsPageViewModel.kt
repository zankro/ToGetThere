package com.example.togetthere.viewmodel

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.togetthere.data.repository.TripRepository
import com.example.togetthere.model.Trip
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class TripsPageViewModel(private val model: TripRepository) : ViewModel() {

    companion object {
        fun provideFactory(tripRepository: TripRepository): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return TripsPageViewModel(tripRepository) as T
                }
            }
    }

    private val _createdTrips = MutableStateFlow<List<Trip>>(emptyList())
    val createdTrips: StateFlow<List<Trip>> = _createdTrips.asStateFlow()

    private val _doneTrips = MutableStateFlow<List<Trip>>(emptyList())
    val doneTrips: StateFlow<List<Trip>> = _doneTrips.asStateFlow()

    private val _bookedTrips = MutableStateFlow<List<Trip>>(emptyList())
    val bookedTrips: StateFlow<List<Trip>> = _bookedTrips.asStateFlow()

    private val _favoriteTrips = MutableStateFlow<List<Trip>>(emptyList())
    val favoriteTrips: StateFlow<List<Trip>> = _favoriteTrips.asStateFlow()

    private val _favoriteTripStates = mutableStateMapOf<Pair<Int, String>, Boolean>()
    val favoriteTripStates: SnapshotStateMap<Pair<Int, String>, Boolean> = _favoriteTripStates

    private val _trip = MutableStateFlow<Trip?>(null)
    val trip: StateFlow<Trip?> = _trip

    /********************* CREATE TRIP ***************************/
    fun validateAndCreate(trip: Trip) {
        viewModelScope.launch {
            val allTrips = model.getAllTrips().first()
            val newTripId = (allTrips.maxOfOrNull { it.tripId } ?: 0) + 1
            val tripWithId = trip.copy(tripId = newTripId)
            model.addTrip(tripWithId)
        }
    }

    /********************* LOAD TRIP BY ID ***************************/
    fun loadTripById(tripId: Int) {
        viewModelScope.launch {
            _trip.value = model.getTripById(tripId)
        }
    }

    /********************* LOAD CREATED TRIPS ***************************/
    fun loadCreatedTrips(userId: String?) {
        if (userId.isNullOrEmpty()) return
        viewModelScope.launch {
            _createdTrips.value = model.getTripsByCreator(userId).first()
        }
    }

    /********************* LOAD DONE TRIPS ***************************/
    fun loadDoneTrips(userId: String?) {
        if (userId.isNullOrEmpty()) return
        viewModelScope.launch {
            _doneTrips.value = model.getTripsDoneByUser(userId).first()
        }
    }

    /********************* LOAD BOOKED TRIPS ***************************/
    fun loadBookedTrips(userId: String?) {
        if (userId.isNullOrEmpty()) return
        viewModelScope.launch {
            model.getBookedTripsByUser(userId).collect { trips ->
                _bookedTrips.value = trips
            }
        }
    }

    /********************* LOAD FAVORITE TRIPS ***************************/
    fun loadFavoriteTrips(userId: String?) {
        if (userId.isNullOrEmpty()) return
        viewModelScope.launch {
            _favoriteTrips.value = model.getFavoriteTripsForUser(userId).first()
        }
    }

    /********************* FAVORITES ***************************/
    fun toggleFavoriteStatus(tripId: Int, userId: String?) {
        if (userId.isNullOrEmpty()) return
        viewModelScope.launch {
            val key = tripId to userId
            val currentStatus = isFavoriteTripForUser(tripId, userId)
            val newStatus = !currentStatus
            model.toggleFavoriteTripForUser(tripId, userId)
            _favoriteTripStates[key] = newStatus
            loadFavoriteTrips(userId)
        }
    }

    fun isFavoriteTripForUser(tripId: Int, userId: String?): Boolean {
        if (userId.isNullOrEmpty()) return false
        val key = tripId to userId
        return _favoriteTripStates[key] ?: false
    }

    /********************* DELETE TRIP ***************************/
    fun deleteTrip(tripId: Int, userId: String?) {
        if (userId.isNullOrEmpty()) return
        viewModelScope.launch {
            model.removeTrip(tripId)

            loadCreatedTrips(userId)
        }
    }

    @Composable
    fun isFavoriteTripForUserState(tripId: Int, userId: String?): Boolean {
        if (userId.isNullOrEmpty()) return false
        val key = tripId to userId

        // Usa derivedStateOf per creare uno State osservabile
        val isFavorite by remember(key) {
            derivedStateOf {
                _favoriteTripStates[key] ?: false
            }
        }

        // Inizializza lo stato se necessario
        LaunchedEffect(key) {
            if (key !in _favoriteTripStates) {
                val status = model.isTripFavoriteForUser(tripId, userId)
                _favoriteTripStates[key] = status
            }
        }

        return isFavorite
    }
}
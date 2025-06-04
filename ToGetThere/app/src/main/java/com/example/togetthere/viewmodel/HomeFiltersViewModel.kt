package com.example.togetthere.viewmodel

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.togetthere.data.repository.FirebaseNotificationRepository
import com.example.togetthere.data.repository.TripRepository
import com.example.togetthere.model.Filter
import com.example.togetthere.model.Notification
import com.example.togetthere.model.Trip
import com.example.togetthere.model.TripType
import com.example.togetthere.utils.continentToCountries
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class HomeFiltersViewModel(val model: TripRepository, val notModel: FirebaseNotificationRepository, userId: String?) : ViewModel() {

    companion object {
        fun provideFactory(tripRepository: TripRepository, notRepository: FirebaseNotificationRepository, userId: String?): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return HomeFiltersViewModel(tripRepository, notRepository, userId) as T
                }
            }
    }

    //Viaggi filtrati inizialmente togliendo quelli creati dall'utente loggato
    private val _baseTrips = MutableStateFlow<List<Trip>>(emptyList())

    //Viaggi filtrati applicando i filtri
    private val _filteredTrips = MutableStateFlow<List<Trip>>(emptyList())
    val filteredTrips: StateFlow<List<Trip>> = _filteredTrips.asStateFlow()

    private val _isLoading = mutableStateOf(true)
    val isLoading: State<Boolean> = _isLoading

    //Notifiche
    private val _notifications = MutableStateFlow<List<Notification>>(emptyList())
    val notifications: StateFlow<List<Notification>> = _notifications.asStateFlow()

    //Numero di notifiche non lette
    private val _unreadCount = MutableStateFlow(0)
    val unreadCount: StateFlow<Int> = _unreadCount.asStateFlow()

    // Filter states
    val selectedPlace = MutableStateFlow("")
    val selectedStartDate = MutableStateFlow("")
    val selectedEndDate = MutableStateFlow("")
    val totalGuestCount = MutableStateFlow(0)
    val girlsOnly = MutableStateFlow(false)
    val lgbtqFriendly = MutableStateFlow(false)
    val groupSize = MutableStateFlow(50f)
    val selectedTripType = MutableStateFlow<TripType?>(null)
    val minAge = MutableStateFlow(0f)
    val maxAge = MutableStateFlow(100f)
    val minPrice = MutableStateFlow(0f)
    val maxPrice = MutableStateFlow(1500f)
    val adultsCount = MutableStateFlow(0)
    val childrenCount = MutableStateFlow(0)
    val searchQuery = MutableStateFlow("")
    val isSearchActive = MutableStateFlow(false)
    val selectedContinent = MutableStateFlow<String?>(null)
    val recentSearches = MutableStateFlow(mutableListOf("France", "Norway"))

    private var tripsObserverJob: Job? = null

    /********************** NOTIFICATIONS ****************************/
    fun observeNotifications() {
        viewModelScope.launch {
            notModel.getUserNotifications().collect { notifications ->
                _notifications.value = notifications
                _unreadCount.value = notifications.count { it.pending }
            }
        }
    }

    fun removePending(notificationId: String) {
        viewModelScope.launch {
            try {
                notModel.removePending(notificationId)
            } catch (e: Exception) {
                Log.e("TripViewModel", "Errore aggiornando la notifica: ${e.message}")
            }
        }
    }


    /********************** LOAD BASE TRIPS **************************/
    fun filterTripsForUser(userId: String, allTrips: List<Trip>): List<Trip> {
        return allTrips.filter { it.creator != userId }
    }

    private val _favoriteTrips = mutableStateOf<List<Trip>>(emptyList())
    val favoriteTrips: State<List<Trip>> = _favoriteTrips

    private val _favoriteTripStates = mutableStateMapOf<Pair<Int, String>, Boolean>()
    val favoriteTripStates: SnapshotStateMap<Pair<Int, String>, Boolean> = _favoriteTripStates

    fun initializeTripsForUser(userId: String) {
        tripsObserverJob?.cancel()

        tripsObserverJob = viewModelScope.launch {

            try {
                _isLoading.value = true

                val today = LocalDate.now()
                val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")

                // Observe the trips flow continuously
                model.getAllTrips().collect { allTrips ->
                    val filteredTrips = filterTripsForUser(userId, allTrips).filter { trip ->
                        val startDate = LocalDate.parse(trip.startDate, formatter)
                        !startDate.isBefore(today)
                    }

                    _baseTrips.value = filteredTrips

                    // Only update filtered trips if no filters are applied
                    if (areFiltersEmpty()) {
                        _filteredTrips.value = filteredTrips
                    } else {
                        // Re-apply filters to the new base data
                        applyFilters()
                    }

                    kotlinx.coroutines.delay(100)
                    _isLoading.value = false
                }
            } catch (e: Exception) {
                Log.e("HomeFiltersViewModel", "Error loading trips: ${e.message}")
                _isLoading.value = false
            }
        }
    }

    fun initializeTripsForGuest() {
        tripsObserverJob?.cancel()

        tripsObserverJob = viewModelScope.launch {
            try {
                _isLoading.value = true

                val today = LocalDate.now()
                val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")

                // Observe the trips flow continuously
                model.getAllTrips().collect { allTrips ->
                    // For guests, show all trips that haven't started yet
                    val filteredTrips = allTrips.filter { trip ->
                        val startDate = LocalDate.parse(trip.startDate, formatter)
                        !startDate.isBefore(today)
                    }

                    _baseTrips.value = filteredTrips

                    // Only update filtered trips if no filters are applied
                    if (areFiltersEmpty()) {
                        _filteredTrips.value = filteredTrips
                    } else {
                        // Re-apply filters to the new base data
                        applyFilters()
                    }

                    kotlinx.coroutines.delay(100)
                    _isLoading.value = false
                }
            } catch (e: Exception) {
                Log.e("HomeFiltersViewModel", "Error loading trips: ${e.message}")
                _isLoading.value = false
            }
        }
    }

    // Helper function to check if any filters are applied
    private fun areFiltersEmpty(): Boolean {
        return selectedPlace.value.isBlank() &&
                selectedStartDate.value.isBlank() &&
                selectedEndDate.value.isBlank() &&
                totalGuestCount.value == 0 &&
                !girlsOnly.value &&
                !lgbtqFriendly.value &&
                groupSize.value == 50f &&
                selectedTripType.value == null &&
                minAge.value == 0f &&
                maxAge.value == 100f &&
                minPrice.value == 0f &&
                maxPrice.value == 1500f
    }

    // Get favorite status from the state map or repository
    suspend fun isFavoriteTripForUser(tripId: Int, userId: String): Boolean {
        val key = Pair(tripId, userId)
        return _favoriteTripStates[key] ?: run {
            val status = model.isTripFavoriteForUser(tripId, userId) // 🔄 funzione suspend
            _favoriteTripStates[key] = status
            status
        }
    }

    fun ensureFavoriteStatusLoaded(tripId: Int, userId: String?) {
        if (userId.isNullOrEmpty()) return
        val key = tripId to userId
        if (key !in _favoriteTripStates) {
            viewModelScope.launch {
                val status = model.isTripFavoriteForUser(tripId, userId)
                _favoriteTripStates[key] = status
            }
        }
    }

    // Toggle favorite status in both state map and repository
    fun toggleFavoriteStatus(tripId: Int, userId: String?) {
        if (userId.isNullOrEmpty()) return
        viewModelScope.launch {
            val key = Pair(tripId, userId)
            val currentStatus = isFavoriteTripForUser(tripId, userId)
            val newStatus = !currentStatus

            model.toggleFavoriteTripForUser(tripId, userId)

            _favoriteTripStates[key] = newStatus
        }
    }


    /**************************** APPLY FILTERS *****************************/
    fun applyFilters() {
        viewModelScope.launch {
            _filteredTrips.value = _baseTrips.value.filter { trip ->

                val matchWhere = selectedPlace.let { selected ->
                    val normalizedSelected = selected.value.trim().lowercase()

                    when {
                        normalizedSelected == "anywhere" -> {
                            println("Trip ${trip.name}: matchAnywhere=true")
                            true
                        }

                        continentToCountries.containsKey(normalizedSelected) -> {
                            val countriesInContinent =
                                continentToCountries[normalizedSelected] ?: emptyList()
                            val tripLocationLower = trip.destination.lowercase()
                            val match = countriesInContinent.any { country ->
                                tripLocationLower.contains(country.lowercase())
                            }
                            match
                        }

                        else -> {
                            val match = trip.destination.contains(selected.value, ignoreCase = true) ||
                                    trip.stops.any { stage ->
                                        stage.stageName.contains(selected.value, ignoreCase = true)
                                    }
                            match
                        }
                    }
                }

                val matchType = (selectedTripType.value?.let { it == trip.type }) ?: true

                val matchNumPeople = totalGuestCount.value.let {
                    val available =
                        trip.maxParticipants - trip.numParticipants
                    val match = available >= it
                    match
                }

                val matchInclusivity = when {
                    girlsOnly.value && lgbtqFriendly.value -> {
                        val match =
                            Filter.GIRLS_ONLY in trip.filters || Filter.LGBTQ_FRIENDLY in trip.filters
                        match
                    }

                    girlsOnly.value -> {
                        val match = Filter.GIRLS_ONLY in trip.filters
                        match
                    }

                    lgbtqFriendly.value -> {
                        val match = Filter.LGBTQ_FRIENDLY in trip.filters
                        match
                    }

                    else -> {
                        true
                    }
                }

                val matchMaxGroupSize = groupSize.value.let {
                    val match = trip.maxParticipants <= it
                    match
                }

                val matchPrice = minPrice.value.let {
                    val matchMin = trip.priceEstimation.min >= it
                    val matchMax = trip.priceEstimation.max <= maxPrice.value
                    matchMin && matchMax
                }

                val matchAge = run {
                    val match = !(maxAge.value <= trip.ageRange.min || minAge.value >= trip.ageRange.max)
                    match
                }

                val matchDates = if (selectedStartDate.value.isBlank() || selectedEndDate.value.isBlank()) {
                    true // Se le date sono vuote, il filtro non viene applicato
                } else {
                    try {
                        val dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")

                        val selectedStart = LocalDate.parse(selectedStartDate.value, dateFormatter)
                        val selectedEnd = LocalDate.parse(selectedEndDate.value, dateFormatter)
                        val tripStart = LocalDate.parse(trip.startDate, dateFormatter)
                        val tripEnd = LocalDate.parse(trip.endDate, dateFormatter)

                        !(tripEnd.isBefore(selectedStart) || tripStart.isAfter(selectedEnd))
                    } catch (e: Exception) {
                        false
                    }
                }


                val isMatching = matchWhere && matchType && matchNumPeople && matchInclusivity &&
                        matchMaxGroupSize && matchPrice && matchAge && matchDates

                println("✅ RISULTATO FINALE per ${trip.name} = $isMatching\n")

                isMatching
            }

            println("🎯 Viaggi filtrati (${filteredTrips.value.size}):")
        }
    }

    /**************************** RESET FILTERS *****************************/
    fun resetFilters() {
        selectedPlace.value = ""
        selectedStartDate.value = ""
        selectedEndDate.value = ""
        totalGuestCount.value = 0
        girlsOnly.value = false
        lgbtqFriendly.value = false
        groupSize.value = 50f
        selectedTripType.value = null
        minAge.value = 0f
        maxAge.value = 100f
        minPrice.value = 0f
        maxPrice.value = 1500f

        _filteredTrips.value = _baseTrips.value
    }

    /**************************** RECENT LIST OF SEARCH BAR *****************************/
    fun addRecentSearch(search: String) {
        if (search.isNotBlank()) {
            val currentList = recentSearches.value.toMutableList()

            currentList.remove(search)
            currentList.add(0, search)

            if (currentList.size > 5) {
                currentList.removeAt(currentList.lastIndex)
            }

            recentSearches.value = currentList
        }
    }

    /* UTILITY FUNCTION TO CHANGE IMAGES FIELD NAME FROM ResId to Url */
    private val firestore = FirebaseFirestore.getInstance()
    private var hasRun = false // questo evita ripetizioni nella stessa sessione

    fun runTripImageMigrationIfNeeded() {
        if (hasRun) return
        hasRun = true

        firestore.collection("trips")
            .get()
            .addOnSuccessListener { snapshot ->
                for (document in snapshot.documents) {
                    val images = document.get("images") as? List<Map<String, Any>> ?: continue

                    // Controlla se serve migrazione
                    val needsMigration = images.any { it.containsKey("resId") }
                    if (!needsMigration) continue

                    val updatedImages = images.mapNotNull { img ->
                        val resId = img["resId"] as? String
                        if (resId != null) mapOf("url" to resId) else null
                    }

                    document.reference.update("images", updatedImages)
                        .addOnSuccessListener {
                            Log.d("Migration", "✅ Migrated document ${document.id}")
                        }
                        .addOnFailureListener { e ->
                            Log.e("Migration", "❌ Failed to update ${document.id}: $e")
                        }
                }
            }
            .addOnFailureListener {
                Log.e("Migration", "❌ Failed to fetch trips: $it")
            }
    }


}
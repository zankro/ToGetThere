package com.example.togetthere.model

//import com.example.togetthere.data.digitalDetox
//import com.example.togetthere.data.natureTrip
//import com.example.togetthere.data.islandAdventure
//import com.example.togetthere.data.culturalTokyo
//import com.example.togetthere.data.desertEscape
//import com.example.togetthere.data.cityBreak
//import com.example.togetthere.data.surfTrip
//import com.example.togetthere.data.romanticTrip

enum class ReservationStatus {
    PENDING,
    CONFIRMED,
    REJECTED,
}

data class Reservation(
    val bookerId: String = "", //user_id
    val numAdults: Int = 1,
    val numChildren: Int = 0,
    val status: ReservationStatus = ReservationStatus.PENDING,
)

enum class TripType(val displayName: String) {
    ADVENTURE("Adventure"),
    RELAX("Relax"),
    CULTURAL("Cultural"),
    NATURE("Nature"),
    PARTY("Party"),
    ROMANTIC("Romantic")
}

enum class ParticipantActionState {
    REVIEW,
    PENDING_DECISION,
    NONE
}

enum class Filter {
    GIRLS_ONLY,
    LGBTQ_FRIENDLY,
}

data class AgeRange(
    val min: Int = 18,
    val max: Int = 25
)

data class PriceRange(
    val min: Int = 0,
    val max: Int = 100
)

data class TripPhoto(
//    data class Resource(val resId: Int) : TripPhoto()
//    data class UriPhoto(val uri: Uri) : TripPhoto()
    val url: String = ""
)

data class Stage(
    val stageName: String = "",
    val startDate: String = "",
    val endDate: String = "",
    val freeRoaming: Boolean = false,
)

data class Trip(
    val tripId: Int = 0,
    val name: String = "",
    val type: TripType = TripType.ADVENTURE,
    val destination: String = "",
    val creator: String = "",
    val numParticipants: Int = 1,
    val maxParticipants: Int = 4,
    val reservationsList: List<Reservation> = listOf(),
    val startDate: String = "",
    val endDate: String = "",
    val images: List<TripPhoto> = listOf(),
    val tags: List<String> = listOf(),
    val description: String = "",
    val stops: List<Stage> = listOf(),
    val priceEstimation: PriceRange = PriceRange(100, 500),
    val suggestedActivities: List<String> = listOf(),
    val filters: List<Filter> = listOf(),
    val ageRange: AgeRange = AgeRange(18, 25),
    val reviews: List<TripReview> = listOf(),
    val favoritesUsers: List<String> = listOf()
)

//class TripRepository {
//    private val _trips = MutableStateFlow<List<Trip>>(listOf(
//     surfTrip,
//     romanticTrip,
//     natureTrip,
//     islandAdventure,
//     culturalTokyo,
//     desertEscape,
//     cityBreak,
//     digitalDetox
//    ))
//
//    val trips: StateFlow<List<Trip>> = _trips
//
//    private val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
//
//    fun getAllTrips(): List<Trip> {
//        return _trips.value
//    }
//
//    fun getTripById(tripId: Int): Trip? {
//        return _trips.value.find { it.tripId == tripId }
//    }
//
//    fun getTripsByCreator(creatorId: String?): List<Trip> {
//        println("[DEBUG23] Trip IDs: ${_trips.value.map { it.tripId }} e poi ${_trips.value.map { it.creator }}")
//        return _trips.value.filter { it.creator == creatorId }
//    }
//
//    fun getTripsByDestination(destination: String): List<Trip> {
//        return _trips.value.filter { it.destination == destination }
//    }
//
//    fun getTripsByType(type: TripType): List<Trip> {
//        return _trips.value.filter { it.type == type }
//    }
//
//    fun addTrip(trip: Trip) {
//        println("[DEBUG] Adding trip. Before count: ${_trips.value.size}")
//        _trips.value =_trips.value + trip
//        println("[DEBUG] Trip added. After count: ${_trips.value.size}")
//        println("[DEBUG] Trip IDs: ${_trips.value.map { it.tripId }} e poi ${_trips.value.map { it.creator }}")
//    }
//
//    fun removeTrip(tripId: Int) {
//        _trips.value = _trips.value.filter { it.tripId != tripId }
//    }
//
//    fun updateTrip(trip: Trip) {
//        _trips.value = _trips.value.map { if (it.tripId == trip.tripId) trip else it }
//    }
//
//    fun getTripsDoneByUser(userId: String): List<Trip> {
//        val today = LocalDate.now()
//        return _trips.value.filter { trip ->
//            trip.reservationsList.any { it.bookerId == userId } &&
//                    LocalDate.parse(trip.endDate, formatter).isBefore(today)
//        }
//    }
//
//    fun getBookedTripsByUser(userId: String): List<Trip> {
//        val today = LocalDate.now()
//        return _trips.value.filter { trip ->
//            trip.reservationsList.any { it.bookerId == userId } &&
//                    !LocalDate.parse(trip.startDate, formatter).isBefore(today)
//        }
//    }
//
//    fun isTripFavoriteForUser(tripId: Int, userId: String?): Boolean {
//        if (userId == null) return false
//
//        return _trips.value
//            .find { it.tripId == tripId }
//            ?.favoritesUsers
//            ?.contains(userId) == true
//    }
//
//    fun toggleFavoriteTripForUser(tripId: Int, userId: String) {
//        _trips.value = _trips.value.map { trip ->
//            if (trip.tripId == tripId) {
//                val updatedSavedUsers = if (userId in trip.favoritesUsers) {
//                    trip.favoritesUsers - userId
//                } else {
//                    trip.favoritesUsers + userId
//                }
//                trip.copy(favoritesUsers = updatedSavedUsers)
//            } else trip
//        }
//    }
//
//    fun getFavoriteTripsForUser(userId: String): List<Trip> {
//        return _trips.value.filter { userId in it.favoritesUsers }
//    }
//
//    fun addReviewToTrip(
//        tripId: Int,
//        author: String,
//        score: Int,
//        title: String,
//        description: String,
//        photos: List<TripPhoto>,
//    ) {
//        _trips.update { list ->
//            list.map { trip ->
//                if (trip.tripId == tripId) {
//                    val nextId = trip.reviews.size + 1
//                    val review = TripReview(
//                        id = nextId,
//                        author = author,
//                        score = score,
//                        title = title,
//                        description = description,
//                        photos = photos
//                    )
//                    trip.copy(reviews = trip.reviews + review)
//                } else trip
//            }
//        }
//    }
//
//}

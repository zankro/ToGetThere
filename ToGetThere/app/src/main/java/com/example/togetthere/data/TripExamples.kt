package com.example.togetthere.data

//import androidx.compose.runtime.getValue
//import androidx.compose.runtime.mutableStateOf
//import androidx.compose.runtime.setValue
//import com.example.togetthere.R
//import com.example.togetthere.model.AgeRange
//import com.example.togetthere.model.Filter
//import com.example.togetthere.model.PriceRange
//import com.example.togetthere.model.Reservation
//import com.example.togetthere.model.ReservationStatus
//import com.example.togetthere.model.Stage
//import com.example.togetthere.model.Trip
//import com.example.togetthere.model.TripPhoto
//import com.example.togetthere.model.TripType
//
//var surfTrip by mutableStateOf(
//    Trip(
//        tripId =  0,
//        name = "Surf trip around France coast",
//        type = TripType.ADVENTURE,
//        destination = "France",
//        creator = PaoloProfile.userId,  // This should reference creator's unique ID
//        numParticipants = 2,
//        maxParticipants = 5,
//        reservationsList = listOf(
//            Reservation(
//                numAdults = 1,
//                numChildren = 1,
//                bookerId = AndreProfile.userId,
//                status = ReservationStatus.PENDING
//            ),
//            Reservation(
//                numAdults = 2,
//                numChildren = 0,
//                bookerId = LauraProfile.userId,
//                status = ReservationStatus.CONFIRMED
//            ),
//            Reservation(
//                numAdults = 4,
//                numChildren = 0,
//                bookerId = HugoProfile.userId,
//                status = ReservationStatus.REJECTED
//            ),
//            Reservation(
//                numAdults = 1,
//                numChildren = 0,
//                bookerId = SofiaProfile.userId,
//                status = ReservationStatus.PENDING
//            ),
//            Reservation(
//                numAdults = 3,
//                numChildren = 0,
//                bookerId = YukiProfile.userId,
//                status = ReservationStatus.PENDING
//            )
//        ),
//        startDate = "01/06/2025",
//        endDate = "15/06/2025",
//        images = listOf(
//            TripPhoto.Resource(R.drawable.hossegor1),
//            TripPhoto.Resource(R.drawable.hossegor2),
//            TripPhoto.Resource(R.drawable.hossegor3),
//        ),
//        tags = listOf(
//            "Mixed Group",
//            "Surf trip",
//            "Wild",
//            "\uD83C\uDFF3\uFE0F\u200D\uD83C\uDF08 Friendly",
//            "\uD83C\uDF82 25-30"
//        ),
//        description = "Hi, I’m Paolo, 26 y.o. from Italy!\nI’m planning a surf trip along the South-West coast in France, and looking for a few fellows to live this adventure with.\n\nIf you enjoy surfing, adventure, and van life style, you’re in the right place!",
//        stops = listOf(
//            Stage(stageName = "Soorts-Hossegor", startDate = "01/05/2025", endDate = "08/05/2025", freeRoaming = false),
//            Stage(stageName = "Biarritz", startDate = "08/05/2025", endDate = "12/05/2025", freeRoaming = false),
//            Stage(stageName = "La Dune du Pilat", startDate = "12/05/2025", endDate = "15/05/2025", freeRoaming = true)
//        ),
//        priceEstimation = PriceRange(min = 400, max = 700),
//        suggestedActivities = listOf("Surf", "Explore nature", "Scuba Diving"),
//        filters = listOf(),
//        ageRange = AgeRange(min = 5, max = 60),
//        reviews = listOf(tripReview1, tripReview9),
//        favoritesUsers = listOf("1", "2", "3")
//    )
//)
//
//
//val romanticTrip = Trip(
//    tripId = 1,
//    name = "Romantic Getaway in Venice",
//    type = TripType.ROMANTIC,
//    destination = "Italy",
//    creator = AndreProfile.userId,
//    numParticipants = 2,
//    maxParticipants = 10,
//    reservationsList = listOf(
//        Reservation(
//            numAdults = 4,
//            numChildren = 0,
//            bookerId = PaoloProfile.userId,
//            status = ReservationStatus.CONFIRMED
//        ),
//    ),
//    startDate ="10/06/2025",
//    endDate = "14/06/2025",
//    images = listOf(
//        TripPhoto.Resource(R.drawable.venice1),
//        TripPhoto.Resource(R.drawable.venice2),
//    ),
//    tags = listOf("Couple", "Romantic", "Gondola"),
//    description = "Spend a dreamy weekend in Venice! Ride gondolas, explore canals and enjoy Italian cuisine.",
//    stops = listOf(
//        Stage(stageName = "Murano", startDate = "10/06/2025", endDate = "12/06/2025", freeRoaming = false),
//        Stage(stageName = "Burano", startDate = "13/06/2025", endDate = "14/06/2025", freeRoaming = true)
//    ),
//    priceEstimation = PriceRange(min = 500, max = 900),
//    suggestedActivities = listOf("Gondola Ride", "Wine Tasting"),
//    filters = listOf(Filter.LGBTQ_FRIENDLY),
//    ageRange = AgeRange(min = 25, max = 35),
//    reviews = listOf(tripReview1, tripReview9),
//    favoritesUsers = listOf("60DCjfz7ouUTxAsySkatlT6xzt13", "3", "6")
//)
//
//val natureTrip = Trip(
//    tripId = 2,
//    name = "Norway Fjords Nature Escape",
//    type = TripType.NATURE,
//    destination = "Norway",
//    creator = LauraProfile.userId,
//    numParticipants = 3,
//    maxParticipants = 15,
//    reservationsList = listOf(
//    ),
//    startDate = "05/07/2025",
//    endDate = ("20/07/2025"),
//    images = listOf(
//        TripPhoto.Resource(R.drawable.norway1),
//        TripPhoto.Resource(R.drawable.norway2)
//    ),
//    tags = listOf("Fjords", "Hiking", "Nature"),
//    description = "Explore the majestic fjords of Norway. Perfect for nature lovers.",
//    stops = listOf(
//        Stage(stageName = "Geirangerfjord", startDate = "", endDate = "", freeRoaming = true),
//        Stage(stageName = "Trolltunga", startDate = "", endDate = "", freeRoaming = false),
//        Stage(stageName = "Oslo", startDate = "", endDate = "", freeRoaming = true)
//    ),
//    priceEstimation = PriceRange(min = 600, max = 1200),
//    suggestedActivities = listOf("Hiking", "Kayaking", "Photography"),
//    filters = listOf(Filter.GIRLS_ONLY),
//    ageRange = AgeRange(min = 18, max = 50),
//    reviews = listOf(tripReview2, tripReview10),
//    favoritesUsers = listOf("60DCjfz7ouUTxAsySkatlT6xzt13", "1", "4")
//)
//
//val islandAdventure = Trip(
//    tripId = 3,
//    name = "Island Adventure in Greece",
//    type = TripType.ADVENTURE,
//    destination = "Greece",
//    creator = AndreProfile.userId,
//    numParticipants = 4,
//    maxParticipants = 6,
//    reservationsList = listOf(
//        Reservation(
//            numAdults = 3,
//            numChildren = 0,
//            bookerId = PaoloProfile.userId,
//            status = ReservationStatus.CONFIRMED
//        ),
//    ),
//    startDate = ("10/08/2025"),
//    endDate = ("20/08/2025"),
//    images = listOf(
//        TripPhoto.Resource(R.drawable.greece1)
//    ),
//    tags = listOf("Adventure", "Island Hopping", "Sea"),
//    description = "Join us for an island-hopping adventure through the Greek isles — from Santorini sunsets to Mykonos nightlife!",
//    stops = listOf(
//        Stage(stageName = "Santorini", startDate = "", endDate = "", freeRoaming = true),
//        Stage(stageName = "Mykonos", startDate = "", endDate = "", freeRoaming = false),
//        Stage(stageName = "Naxos", startDate = "", endDate = "", freeRoaming = true)
//    ),
//    priceEstimation = PriceRange(min = 700, max = 1000),
//    suggestedActivities = listOf("Snorkeling", "Hiking", "Nightlife"),
//    filters = listOf(),
//    ageRange = AgeRange(min = 20, max = 35),
//    reviews = listOf(tripReview3),
//    favoritesUsers = listOf("60DCjfz7ouUTxAsySkatlT6xzt13", "3", "5")
//)
//
//val culturalTokyo = Trip(
//    tripId = 4,
//    name = "Cultural Discovery in Tokyo",
//    type = TripType.CULTURAL,
//    destination = "Japan",
//    creator = LauraProfile.userId,
//    numParticipants = 3,
//    maxParticipants = 8,
//    reservationsList = listOf(
//    ),
//    startDate = ("15/09/2025"),
//    endDate = ("25/09/2025"),
//    images = listOf(
//        TripPhoto.Resource(R.drawable.tokyo1),
//        TripPhoto.Resource(R.drawable.tokyo2),
//    ),
//    tags = listOf("Culture", "Japan", "Temple Tours"),
//    description = "Explore ancient temples, vibrant neighborhoods, and the culture-packed streets of Tokyo.",
//    stops = listOf(
//        Stage(stageName = "Asakusa", startDate = "", endDate = "", freeRoaming = true),
//        Stage(stageName = "Shibuya", startDate = "", endDate = "", freeRoaming = false),
//        Stage(stageName = "Ueno", startDate = "", endDate = "", freeRoaming = true)
//    ),
//    priceEstimation = PriceRange(min = 1000, max = 1500),
//    suggestedActivities = listOf("Cultural Visits", "Tea Ceremony", "Shopping"),
//    filters = listOf(Filter.LGBTQ_FRIENDLY),
//    ageRange = AgeRange(min = 18, max = 40),
//    reviews = listOf(tripReview4),
//    favoritesUsers = listOf("4", "5", "6")
//)
//
//val desertEscape = Trip(
//    tripId = 5,
//    name = "Sahara Desert Escape",
//    type = TripType.ADVENTURE,
//    destination = "Morocco",
//    creator = PaoloProfile.userId,
//    numParticipants = 2,
//    maxParticipants = 6,
//    reservationsList = listOf(
//        Reservation(
//            numAdults = 3,
//            numChildren = 0,
//            bookerId = LauraProfile.userId,
//            status = ReservationStatus.CONFIRMED
//        ),
//        Reservation(
//            numAdults = 3,
//            numChildren = 0,
//            bookerId = AndreProfile.userId,
//            status = ReservationStatus.CONFIRMED
//        ),
//    ),
//    startDate = ("01/02/2025"),
//    endDate = ("10/02/2025"),
//    images = listOf(
//        TripPhoto.Resource(R.drawable.sahara1),
//    ),
//    tags = listOf("Desert", "Camel Ride", "Adventure"),
//    description = "Experience the magic of the Sahara Desert — camel rides, desert camps, and stargazing nights.",
//    stops = listOf(
//        Stage(stageName = "Merzouga", startDate = "", endDate = "", freeRoaming = true),
//        Stage(stageName = "Erg Chebbi", startDate = "", endDate = "", freeRoaming = true)
//    ),
//    priceEstimation = PriceRange(min = 500, max = 800),
//    suggestedActivities = listOf("Camel Ride", "Camping", "Photography"),
//    filters = listOf(),
//    ageRange = AgeRange(min = 21, max = 40),
//    reviews = listOf(tripReview5),
//    favoritesUsers = listOf("7")
//)
//
//val cityBreak = Trip(
//    tripId = 6,
//    name = "City Break in New York",
//    type = TripType.PARTY,
//    destination = "USA",
//    creator = AndreProfile.userId,
//    numParticipants = 3,
//    maxParticipants = 5,
//    reservationsList = listOf(
//        Reservation(
//            numAdults = 2,
//            numChildren = 0,
//            bookerId = PaoloProfile.userId,
//            status = ReservationStatus.CONFIRMED
//        ),
//        Reservation(
//            numAdults = 1,
//            numChildren = 0,
//            bookerId = LauraProfile.userId,
//            status = ReservationStatus.CONFIRMED
//        ),
//    ),
//    startDate = ("05/04/2025"),
//    endDate = ("12/04/2025"),
//    images = listOf(
//        TripPhoto.Resource(R.drawable.newyork1),
//        TripPhoto.Resource(R.drawable.newyork2),
//    ),
//    tags = listOf("Nightlife", "City", "USA"),
//    description = "Explore NYC by day and party by night — from rooftop bars to Broadway shows.",
//    stops = listOf(
//        Stage(stageName = "Times Square", startDate = "", endDate = "", freeRoaming = false),
//        Stage(stageName = "Brooklyn", startDate = "", endDate = "", freeRoaming = true),
//        Stage(stageName = "Central Park", startDate = "", endDate = "", freeRoaming = true)
//    ),
//    priceEstimation = PriceRange(min = 900, max = 1500),
//    suggestedActivities = listOf("Bar Crawl", "Museum", "Broadway"),
//    filters = listOf(Filter.LGBTQ_FRIENDLY),
//    ageRange = AgeRange(min = 22, max = 35),
//    reviews = listOf(tripReview7),
//    favoritesUsers = listOf("5", "6", "7")
//)
//
//val digitalDetox = Trip(
//    tripId = 7,
//    name = "Digital Detox Retreat in Bali",
//    type = TripType.RELAX,
//    destination = "Indonesia",
//    creator = LauraProfile.userId,
//    numParticipants = 4,
//    maxParticipants = 10,
//    reservationsList = listOf(
//        Reservation(
//            numAdults = 2,
//            numChildren = 0,
//            bookerId = PaoloProfile.userId,
//            status = ReservationStatus.PENDING
//        ),
//    ),
//    startDate = ("20/10/2025"),
//    endDate = ("30/10/2025"),
//    images = listOf(TripPhoto.Resource(R.drawable.indonesia1)),
//    tags = listOf("Wellness", "Yoga", "Nature"),
//    description = "Disconnect to reconnect — a wellness retreat in Bali with yoga, meditation, and nature immersion.",
//    stops = listOf(
//        Stage(stageName = "Ubud", startDate = "", endDate = "", freeRoaming = true),
//        Stage(stageName = "Canggu", startDate = "", endDate = "", freeRoaming = false)
//    ),
//    priceEstimation = PriceRange(min = 600, max = 1100),
//    suggestedActivities = listOf("Yoga", "Meditation", "Nature Walks"),
//    filters = listOf(),
//    ageRange = AgeRange(min = 25, max = 45),
//    reviews = listOf(tripReview8),
//    favoritesUsers = listOf()
//)
//

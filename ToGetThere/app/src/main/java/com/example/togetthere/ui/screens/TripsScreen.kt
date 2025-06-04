package com.example.togetthere.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.togetthere.R
import com.example.togetthere.model.Trip
import com.example.togetthere.model.UserProfile
import com.example.togetthere.ui.components.NotLoggedInComponent
import com.example.togetthere.ui.components.TGT_AlertDialog
import com.example.togetthere.ui.navigation.ToGetThereDestinations
import com.example.togetthere.viewmodel.TripsPageViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun TripsScreen(navController: NavController, tripViewModel: TripsPageViewModel, user: UserProfile?, bottomPadding: Dp, isLandscape: Boolean) {

    // Show login required message and button
    if (user == null) {
        NotLoggedInComponent(isLandscape, navController, bottomPadding)
        return
    }

    tripViewModel.favoriteTripStates

    LaunchedEffect(user.userId) {
        tripViewModel.loadCreatedTrips(user.userId)
        tripViewModel.loadFavoriteTrips(user.userId)
        tripViewModel.loadBookedTrips(user.userId)
        tripViewModel.loadDoneTrips(user.userId)
    }

    if (isLandscape) {
        // Layout orizzontale per landscape
        var selectedSection by remember { mutableStateOf("Created trips") }

        Row(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(start = 16.dp, end = 16.dp, bottom = bottomPadding, top = 16.dp)
        ) {
            // Sidebar con menu delle sezioni
            Column(
                modifier = Modifier
                    .width(200.dp)
                    .fillMaxHeight()
                    .background(
                        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                        RoundedCornerShape(12.dp)
                    )
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {

                val sections = listOf("Created trips", "Booked trips", "Favourite trips", "Trips done")

                sections.forEach { section ->
                    SectionMenuItem(
                        title = section,
                        isSelected = selectedSection == section,
                        onClick = { selectedSection = section }
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Area principale con contenuto della sezione selezionata
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    Text(
                        text = selectedSection,
                        style = MaterialTheme.typography.headlineMedium,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }

                item {
                    when (selectedSection) {
                        "Created trips" -> {
                            val trips by tripViewModel.createdTrips.collectAsState()
                            if (trips.isNotEmpty()) {
                                TripCarousel(navController, trips = trips, isCreatedTrip = true, tripViewModel = tripViewModel, userId = user.userId)
                            } else {
                                Text("No created trips yet", modifier = Modifier.padding(16.dp), style = MaterialTheme.typography.titleMedium)
                            }
                        }
                        "Booked trips" -> {
                            val trips by tripViewModel.bookedTrips.collectAsState()
                            if (trips.isNotEmpty()) {
                                TripCarousel(navController, trips = trips, isCreatedTrip = false, tripViewModel = tripViewModel, userId = user.userId)
                            } else {
                                Text("No booked trips yet", modifier = Modifier.padding(16.dp), style = MaterialTheme.typography.titleMedium)
                            }
                        }
                        "Favourite trips" -> {
                            val trips by tripViewModel.favoriteTrips.collectAsState()
                            if (trips.isNotEmpty()) {
                                TripCarousel(navController, trips = trips, isCreatedTrip = false, tripViewModel = tripViewModel, userId = user.userId)
                            } else {
                                Text("No favorite trips yet", modifier = Modifier.padding(16.dp), style = MaterialTheme.typography.titleMedium)
                            }
                        }
                        "Trips done" -> {
                            val trips by tripViewModel.doneTrips.collectAsState()
                            if (trips.isNotEmpty()) {
                                TripCarousel(navController, trips = trips, isCreatedTrip = false, tripViewModel = tripViewModel, userId = user.userId)
                            } else {
                                Text("No trips done yet", modifier = Modifier.padding(16.dp), style = MaterialTheme.typography.titleMedium)
                            }
                        }
                    }
                }
            }
        }
    } else {
        // Layout verticale originale per portrait
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(start = 32.dp, end = 32.dp, bottom = bottomPadding, top = 32.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Text(
                    "Your Trips",
                    style = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
            }

            // Created trips
            item {
                ExpandableSection(
                    title = "Created trips",
                    content = {
                        val trips by tripViewModel.createdTrips.collectAsState()
                        if (trips.isNotEmpty()) {
                            TripCarousel(navController, trips = trips, isCreatedTrip = true, tripViewModel = tripViewModel, userId = user.userId)
                        } else {
                            Text("No created trips yet", modifier = Modifier.padding(16.dp), style = MaterialTheme.typography.titleMedium)
                        }
                    }
                )
            }

            // Booked trips
            item {
                ExpandableSection(
                    title = "Booked trips",
                    content = {
                        val trips by tripViewModel.bookedTrips.collectAsState()
                        if (trips.isNotEmpty()) {
                            TripCarousel(navController, trips = trips, isCreatedTrip = false, tripViewModel = tripViewModel, userId = user.userId)
                        } else {
                            Text("No booked trips yet", modifier = Modifier.padding(16.dp), style = MaterialTheme.typography.titleMedium)
                        }
                    }
                )
            }

            // Favourite trips
            item {
                ExpandableSection(
                    title = "Favourite trips",
                    content = {
                        val trips by tripViewModel.favoriteTrips.collectAsState()

                        if (trips.isNotEmpty()) {
                            TripCarousel(navController, trips = trips, isCreatedTrip = false, tripViewModel = tripViewModel, userId = user.userId)
                        } else {
                            Text("No favorite trips yet", modifier = Modifier.padding(16.dp), style = MaterialTheme.typography.titleMedium)
                        }
                    }
                )
            }

            // Trips done
            item {
                ExpandableSection(
                    title = "Trips done",
                    content = {
                        val trips by tripViewModel.doneTrips.collectAsState()
                        if (trips.isNotEmpty()) {
                            TripCarousel(navController, trips = trips, isCreatedTrip = false, tripViewModel = tripViewModel, userId = user.userId)
                        } else {
                            Text("No trips done yet", modifier = Modifier.padding(16.dp), style = MaterialTheme.typography.titleMedium)
                        }
                    }
                )
            }

            item {
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

@Composable
private fun SectionMenuItem(
    title: String,
    isSelected: Boolean,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(
                if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                else Color.Transparent
            )
            .clickable { onClick() }
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = if (isSelected) MaterialTheme.colorScheme.primary
                else MaterialTheme.colorScheme.onSurface,
                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
            )
        }
    }
}

@Composable
fun ExpandableSection(
    title: String,
    content: @Composable () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { expanded = !expanded }
                .padding(vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.weight(1f)
            )
            Icon(
                imageVector = if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                contentDescription = if (expanded) "Collapse" else "Expand"
            )
        }

        if (expanded) {
            content()
        }
    }
}

@Composable
fun TripCarousel(navController: NavController, trips: List<Trip>, isCreatedTrip: Boolean,  tripViewModel: TripsPageViewModel, userId: String?) {
    tripViewModel.favoriteTripStates

    Column(modifier = Modifier.fillMaxWidth()) {
        LazyRow(
            state = rememberLazyListState(),
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            contentPadding = PaddingValues(horizontal = 14.dp),
        ) {
            items(trips) { trip ->
                TripCard(
                    trip = trip,
                    isCreatedTrip = isCreatedTrip,
                    tripViewModel = tripViewModel,
                    userId = userId,
                    navigateToDetails = { navController.navigate(ToGetThereDestinations.tripDetailRoute(trip.tripId)) },
                    navigateToEdit = { navController.navigate(ToGetThereDestinations.tripEditRoute(trip.tripId)) }
                )
            }
        }
    }
}




@Composable
fun TripCard(
    trip: Trip,
    isCreatedTrip: Boolean = false,
    tripViewModel: TripsPageViewModel,
    userId: String?,
    navigateToDetails: () -> Unit = {},
    navigateToEdit: () -> Unit = {},
){
    var showPastTripAlert by remember { mutableStateOf(false) }
    val dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
    val tripEndDate = if (trip.endDate.isNotBlank()) {
        LocalDate.parse(trip.endDate, dateFormatter)
    } else {
        LocalDate.now()
    }
    val isPastTrip = tripEndDate.isBefore(LocalDate.now())

    val isFavorite = tripViewModel.isFavoriteTripForUserState(trip.tripId, userId)

   /* LaunchedEffect(trip.tripId, userId) {
        isFavorite.value = tripViewModel.isFavoriteTripForUser(trip.tripId, userId)
    }*/

    // Alert per viaggio passato
    if (showPastTripAlert) {
        AlertDialog(
            onDismissRequest = { showPastTripAlert = false },
            title = { Text("Edit Unavailable") },
            text = { Text("It's not possible to edit a trip that has already ended.") },
            confirmButton = {
                TextButton(
                    onClick = { showPastTripAlert = false }
                ) {
                    Text("OK")
                }
            }
        )
    }

    Box(
        modifier = Modifier
            .size(220.dp)
            .clip(RoundedCornerShape(20.dp))
            .clickable { navigateToDetails() }
    ) {
        val imageUrl = trip.images.firstOrNull()?.url
        val painter = if (!imageUrl.isNullOrBlank()) {
            rememberAsyncImagePainter(model = imageUrl)
        } else {
            painterResource(id = R.drawable.placeholder)
        }

        Image(
            painter = painter,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize(),
            alignment = Alignment.BottomCenter
        )

        Column(
            modifier = Modifier
                .padding(start = 16.dp, top = 14.dp)
        ) {
            Text(
                text = trip.name,
                color = Color.White,
                fontWeight = FontWeight.Medium,
                fontSize = 18.sp,
                modifier = Modifier
                    .fillMaxWidth(0.75f),
                style = TextStyle(
                    shadow = Shadow(
                        color = Color.Black.copy(alpha = 0.8f),
                        offset = Offset(4f, 4f),
                        blurRadius = 8f
                    )
                )
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = trip.type.displayName,
                color = Color.White,
                fontSize = 14.sp,
                style = TextStyle(
                    shadow = Shadow(Color.Black, Offset(1f, 1f), 4f)
                )
            )
        }

        Column(
            modifier = Modifier
                .padding(16.dp)
                .align(Alignment.BottomStart)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Default.Place,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(text = trip.destination, color = Color.White, fontSize = 12.sp)
                Spacer(modifier = Modifier.width(10.dp))

                Icon(
                    Icons.Default.Groups,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "${trip.numParticipants}",
                    color = Color.White,
                    fontSize = 12.sp
                )
            }

            Spacer(modifier = Modifier.height(2.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Default.CalendarToday,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "${trip.startDate.substring(0, 5)} - ${trip.endDate.substring(0, 5)}",
                    color = Color.White,
                    fontSize = 12.sp
                )
            }
        }
                if (isCreatedTrip) {

                    Column(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Edit Button
                        IconButton(
                            onClick = {
                                if (isPastTrip) {
                                    showPastTripAlert = true
                                } else {
                                    navigateToEdit()
                                }
                            },
                            modifier = Modifier
                                .background(
                                    if (isPastTrip) Color.Gray.copy(alpha = 0.2f)
                                    else Color.White.copy(alpha = 0.2f),
                                    shape = CircleShape
                                )
                                .clip(CircleShape)
                                .size(40.dp),
                        ) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "Edit Trip",
                                tint = if (isPastTrip) Color.Gray else Color.White
                            )
                        }

                        // Delete Button
                        DeleteTripButton(tripId = trip.tripId, userId = userId, tripViewModel = tripViewModel)
                    }

        } else {
            // Favorite Button
            IconButton(
                onClick = {
                    tripViewModel.toggleFavoriteStatus(trip.tripId, userId)
                },
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(8.dp)
                    .background(Color.White.copy(alpha = 0.2f), shape = CircleShape)
                    .clip(CircleShape)
                    .size(40.dp)
            ) {
                Icon(
                    imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    contentDescription = "Favorite",
                    tint = Color.White
                )
            }
        }
    }
}

@Composable
fun DeleteTripButton(tripId: Int, userId: String?, tripViewModel: TripsPageViewModel) {
    var showDialog by remember { mutableStateOf(false) }

    TGT_AlertDialog(
        showDialog = showDialog,
        onDismissRequest = { showDialog = false },
        title = "Delete Trip",
        message = "Are you sure you want to delete this trip?",
        confirmButtonText = "Delete",
        onConfirm = {
            tripViewModel.deleteTrip(tripId, userId)

            showDialog = false
        }
    )

//    if (showDialog) {
//        AlertDialog(
//            onDismissRequest = { showDialog = false },
//            text = { Text("Are you sure you want to delete this trip?") },
//            confirmButton = {
//                TextButton(onClick = {
//                    tripViewModel.deleteTrip(tripId, userId)
//                    showDialog = false
//                }) {
//                    Text("OK")
//                }
//            },
//            dismissButton = {
//                TextButton(onClick = { showDialog = false }) {
//                    Text("Cancel")
//                }
//            }
//        )
//    }

    // Bottone elimina
    IconButton(
        onClick = { showDialog = true },
        modifier = Modifier
            .background(Color.White.copy(alpha = 0.2f), shape = CircleShape)
            .clip(CircleShape)
            .size(40.dp)
    ) {
        Icon(
            imageVector = Icons.Default.Delete,
            contentDescription = "Delete Trip",
            tint = Color.White
        )
    }
}

package com.example.togetthere.ui.screens

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.MarkEmailRead
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.togetthere.model.ReservationStatus
import com.example.togetthere.ui.components.trip_comps.AdditionalInfo
import com.example.togetthere.ui.components.trip_comps.CopyTripCard
import com.example.togetthere.ui.components.trip_comps.CustomTripDialog
import com.example.togetthere.ui.components.trip_comps.Description
import com.example.togetthere.ui.components.trip_comps.EssentialInfoPill
import com.example.togetthere.ui.components.trip_comps.ImageCarousel
import com.example.togetthere.ui.components.trip_comps.Itinerary
import com.example.togetthere.ui.components.trip_comps.MembersList
import com.example.togetthere.ui.components.trip_comps.TagPill
import com.example.togetthere.ui.components.trip_comps.TripReviewDialog
import com.example.togetthere.ui.components.trip_comps.TripReviewsList
import com.example.togetthere.ui.components.trip_comps.TripTopBar
import com.example.togetthere.ui.navigation.ToGetThereDestinations
import com.example.togetthere.ui.theme.CustomTheme
import com.example.togetthere.viewmodel.TripReviewWithAuthor
import com.example.togetthere.viewmodel.TripUiState
import com.example.togetthere.viewmodel.TripViewModel

@Composable
fun TripView(navController: NavHostController, tripViewModel: TripViewModel, userId: String?, bottomPadding: Dp, highlightReviewId: Int? = null) {
    val screenHeight = LocalConfiguration.current.screenHeightDp.dp

    val uiState by tripViewModel.uiState.collectAsState()
    var showDialog by remember { mutableStateOf(false) }

    var showReviewDialog by remember { mutableStateOf(false) }
    var selectedReview by remember { mutableStateOf<TripReviewWithAuthor?>(null) }

    LaunchedEffect(highlightReviewId, uiState) {
            if (tripViewModel.reviewsWithAuthor.isNotEmpty()) {
                val reviewToHighlight = tripViewModel.reviewsWithAuthor.find { it.id == highlightReviewId }
                if (reviewToHighlight != null) {
                    selectedReview = reviewToHighlight
                    showReviewDialog = true
                }
            }
    }

    when (uiState) {
        is TripUiState.Loading -> {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }
        is TripUiState.Error -> {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(text = (uiState as TripUiState.Error).message, color = MaterialTheme.colorScheme.error)
            }
        }
        is TripUiState.Success -> {
            val tripState by tripViewModel.trip.collectAsState()

            if (tripState == null) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
                return
            }

            val trip = tripState!!
//            val tripCreator = tripViewModel.getCreator()
//            val tripParticipants = listOf(tripCreator) + tripViewModel.getParticipants()
            val tripCreator by tripViewModel.creator.collectAsState()
            val tripParticipants by tripViewModel.participants.collectAsState()
            Log.d("TripView", " tripParticipantssss = $tripParticipants")

            val requests = tripViewModel.tripReservationsList
            val confirmedRequests = remember(requests) {
                requests.filter { it.status == ReservationStatus.CONFIRMED }
            }

            val confirmedParticipants = remember(tripParticipants, confirmedRequests) {
                confirmedRequests.mapNotNull { r ->
                    tripParticipants.find { it.userId == r.bookerId }
                }
            }

//            val participantsToShow = tripCreator?.let { listOf(it) + tripParticipants } ?: tripParticipants
            val participantsToShow = tripCreator?.let { listOf(it) + confirmedParticipants } ?: confirmedParticipants

            Box(
                modifier = Modifier.fillMaxSize()
            ) {
                // Top-part of the view
                Box(
                    modifier = Modifier
//                        .fillMaxHeight(0.48f)
                        .fillMaxHeight(0.52f)
//                        .padding(bottom = bottomPadding + screenHeight * 0.02f)
                ) {
                    ImageCarousel(trip.images)

                    Column(
                        modifier = Modifier
                            .safeContentPadding()
                            .fillMaxHeight(0.9f),
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        TripTopBar(navController, trip.tripId)

                        Column {
                            Text(
                                text = trip.name,
                                style = CustomTheme.typography.displayLargeWithShadow,
                                color = MaterialTheme.colorScheme.onPrimary,
                            )

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                MembersList(
                                    participantsToShow,
                                    trip.tripId,
                                    tripViewModel.tripNumParticipants,
                                    tripViewModel.tripMaxParticipants,
                                    onMoreClick = {
                                        navController.navigate(ToGetThereDestinations.tripParticipantsRoute(tripViewModel.getTripId()))
                                    }
                                )

                                val isTripOver = tripViewModel.isTripOver()
                                val isFull = tripViewModel.tripNumParticipants >= tripViewModel.tripMaxParticipants
                                val isCreator = tripViewModel.tripCreator == userId
                                val hasApplied = if(userId.isNullOrBlank()) { false } else { tripViewModel.hasUserApplied(userId) }
                                val isConfirmedParticipant = if(userId.isNullOrBlank()) { false } else { tripViewModel.isUserConfirmed(userId) }

                                when {
                                    // 1. Trip ended and confirmed participant → show "Review"
                                    isTripOver && isConfirmedParticipant -> {
                                        FilledTonalButton(
                                            onClick = { showDialog = true }
                                        ) {
                                            Icon(Icons.Default.Edit, contentDescription = "Review", modifier = Modifier.size(ButtonDefaults.IconSize))
                                            Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                                            Text("Review")
                                        }
                                    }

                                    // 2. Trip creator → show "Edit / Delete"
                                    isCreator -> {
                                        FilledTonalButton(
                                            onClick = {
                                                navController.navigate(ToGetThereDestinations.tripEditRoute(tripViewModel.getTripId()))
                                            }
                                        ) {
                                            Icon(Icons.Outlined.Edit, contentDescription = "Edit")
                                            Text(" / ")
                                            Icon(Icons.Outlined.Delete, contentDescription = "Delete")
                                        }
                                    }

                                    // 3. Confirmed participant and trip is not over → show "You're in" (disabled)
                                    isConfirmedParticipant && !isTripOver -> {
                                        FilledTonalButton(
                                            onClick = {},
                                            enabled = false,
                                            colors = ButtonDefaults.filledTonalButtonColors(
                                                disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                                                disabledContentColor = MaterialTheme.colorScheme.primary
                                            )
                                        ) {
                                            Icon(Icons.Default.CheckCircle, contentDescription = "You're in", modifier = Modifier.size(ButtonDefaults.IconSize))
                                            Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                                            Text("You're in")
                                        }
                                    }

                                    // 4. Request sent and not confirmed → show "Request Sent" disabled
                                    hasApplied -> {
                                        FilledTonalButton(
                                            onClick = {},
                                            enabled = false,
                                            colors = ButtonDefaults.filledTonalButtonColors(
                                                disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                                                disabledContentColor = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        ) {
                                            Icon(Icons.Default.MarkEmailRead, contentDescription = "Request Sent", modifier = Modifier.size(ButtonDefaults.IconSize))
                                            Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                                            Text("Request Sent")
                                        }
                                    }

                                    // 5. Can join → show "Join"
                                    !isTripOver && !isFull && userId != null -> {
                                        FilledTonalButton(
                                            onClick = { tripViewModel.joinTrip(userId) },
                                            enabled = true
                                        ) {
                                            Icon(Icons.Default.Group, contentDescription = "Join", modifier = Modifier.size(ButtonDefaults.IconSize))
                                            Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                                            Text("Join")
                                        }
                                    }

                                    // 6. No button to show
                                    else -> {
                                        // Nothing
                                    }
                                }

                            }
                        }
                    }
                }

                // Bottom part with details
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = (screenHeight * 0.48f) /*- 28.dp*/)
                        .shadow(elevation = 16.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                            .verticalScroll(rememberScrollState()),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 24.dp),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            EssentialInfoPill(
                                title = "WHAT",
                                body = trip.type.displayName.replaceFirstChar { it.uppercaseChar() }
                            )

                            Spacer(modifier = Modifier.size(8.dp))

                            EssentialInfoPill(
                                title = "WHERE",
                                body = trip.destination
                            )

                            Spacer(modifier = Modifier.size(8.dp))

                            EssentialInfoPill(
                                title = "WHEN",
                                body = "${trip.startDate.split("/")[0]}/${trip.startDate.split("/")[1]} • ${trip.endDate.split("/")[0]}/${trip.endDate.split("/")[1]}"
                            )
                        }

                        Spacer(modifier = Modifier.size(6.dp))

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState())
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            for (tag in trip.tags) {
                                TagPill(title = tag)
                                Spacer(modifier = Modifier.size(10.dp))
                            }
                        }

                        Column(
                            modifier = Modifier
                                .padding(16.dp)
                                .clip(shape = RoundedCornerShape(16.dp))
                                .background(color = MaterialTheme.colorScheme.surface)
                                .padding(10.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Description(body = trip.description)

                            Itinerary(trip.stops)

                            AdditionalInfo(
                                trip.priceEstimation.min,
                                trip.priceEstimation.max,
                                trip.suggestedActivities
                            )

                            /** REVIEWS **/
                            if(tripViewModel.isTripOver() && tripViewModel.reviewsWithAuthor.isNotEmpty()) {
                                TripReviewsList(
                                    reviews = tripViewModel.reviewsWithAuthor,
                                    onReviewClick = { review ->
                                        selectedReview = review
                                        showReviewDialog = true
                                    }
                                )
                            }

                        }


                        HorizontalDivider(modifier = Modifier.padding(start = 16.dp, end = 16.dp), color = MaterialTheme.colorScheme.outlineVariant)

                        Column (
                            modifier = Modifier
                                .padding(16.dp)
                                .clip(shape = RoundedCornerShape(16.dp))
                                .background(color = MaterialTheme.colorScheme.surface)
                                .padding(10.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            CopyTripCard(
                                tripId = trip.tripId,
                                navController = navController,
                                onClick = {
                                    val createRouteWithTripId = "${ToGetThereDestinations.CREATE_ROUTE}?tripId=${trip.tripId}"
                                    navController.navigate(createRouteWithTripId) {
                                        launchSingleTop = true
                                        popUpTo(ToGetThereDestinations.HOME_ROUTE) {
                                            inclusive = true
                                        }
                                    }

                                }
                            )
                        }

                        Spacer(modifier = Modifier.height(bottomPadding))

                    }

                }
            }
        }
    }
    if(showDialog && userId != null) {
        TripReviewDialog(
            isOpen = showDialog,
            tripTitle = tripViewModel.tripName,
            onDismiss = { showDialog = false },
            onSubmitReview = { score, title, description, images ->
                tripViewModel.addReview(
                    tripId = tripViewModel.getTripId(),
                    author = userId,
                    score = score,
                    title = title,
                    description = description,
                    photos = images
                )
                showDialog = false
            }
        )
    }

    if (showReviewDialog && selectedReview != null) {
        CustomTripDialog(
            review = selectedReview!!,
            onDismiss = { showReviewDialog = false },
            onAuthorClick = { id ->
                navController.navigate(ToGetThereDestinations.profileRoute(id))
            }
        )
    }

}


@Composable
fun LandscapeTripView(navController: NavHostController, tripViewModel: TripViewModel, userId: String?, bottomPadding: Dp, highlightReviewId: Int? = null) {
    val screenHeight = LocalConfiguration.current.screenHeightDp.dp

    val uiState by tripViewModel.uiState.collectAsState()
    var showDialog by remember { mutableStateOf(false) }

    var showReviewDialog by remember { mutableStateOf(false) }
    var selectedReview by remember { mutableStateOf<TripReviewWithAuthor?>(null) }

    LaunchedEffect(highlightReviewId, uiState) {
        if (tripViewModel.reviewsWithAuthor.isNotEmpty()) {
            val reviewToHighlight = tripViewModel.reviewsWithAuthor.find { it.id == highlightReviewId }
            if (reviewToHighlight != null) {
                selectedReview = reviewToHighlight
                showReviewDialog = true
            }
        }
    }

    when (uiState) {
        is TripUiState.Loading -> {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }
        is TripUiState.Error -> {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(text = (uiState as TripUiState.Error).message, color = MaterialTheme.colorScheme.error)
            }
        }
        is TripUiState.Success -> {
            val tripState by tripViewModel.trip.collectAsState()

            if (tripState == null) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
                return
            }

            val trip = tripState!!
            val tripCreator by tripViewModel.creator.collectAsState()
            val tripParticipants by tripViewModel.participants.collectAsState()

            val requests = tripViewModel.tripReservationsList
            val confirmedRequests = remember(requests) {
                requests.filter { it.status == ReservationStatus.CONFIRMED }
            }

            val confirmedParticipants = remember(tripParticipants, confirmedRequests) {
                confirmedRequests.mapNotNull { r ->
                    tripParticipants.find { it.userId == r.bookerId }
                }
            }

            val participantsToShow = tripCreator?.let { listOf(it) + confirmedParticipants } ?: confirmedParticipants

            Row(modifier = Modifier.fillMaxSize()) {
                // Left part (Image, title, button)
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                ) {
                    ImageCarousel(trip.images)

                    Column(
                        modifier = Modifier
                            .safeContentPadding()
                            .fillMaxHeight(0.9f),
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        TripTopBar(navController, trip.tripId)

                        Column {
                            Text(
                                text = trip.name,
                                style = CustomTheme.typography.displayLargeWithShadow,
                                color = MaterialTheme.colorScheme.onPrimary,
                            )

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                MembersList(
                                    participantsToShow,
                                    trip.tripId,
                                    tripViewModel.tripNumParticipants,
                                    tripViewModel.tripMaxParticipants,
                                    onMoreClick = {
                                        navController.navigate(ToGetThereDestinations.tripParticipantsRoute(tripViewModel.getTripId()))
                                    }
                                )

                                val isTripOver = tripViewModel.isTripOver()
                                val isFull = tripViewModel.tripNumParticipants >= tripViewModel.tripMaxParticipants
                                val isCreator = tripViewModel.tripCreator == userId
                                val hasApplied = if(userId.isNullOrBlank()) { false } else { tripViewModel.hasUserApplied(userId) }
                                val isConfirmedParticipant = if(userId.isNullOrBlank()) { false } else { tripViewModel.isUserConfirmed(userId) }

                                when {
                                    isTripOver && isConfirmedParticipant -> {
                                        FilledTonalButton(onClick = { showDialog = true }) {
                                            Icon(Icons.Default.Edit, contentDescription = "Review", modifier = Modifier.size(ButtonDefaults.IconSize))
                                            Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                                            Text("Review")
                                        }
                                    }
                                    isCreator -> {
                                        FilledTonalButton(onClick = {
                                            navController.navigate(ToGetThereDestinations.tripEditRoute(tripViewModel.getTripId()))
                                        }) {
                                            Icon(Icons.Outlined.Edit, contentDescription = "Edit")
                                            Text(" / ")
                                            Icon(Icons.Outlined.Delete, contentDescription = "Delete")
                                        }
                                    }
                                    isConfirmedParticipant && !isTripOver -> {
                                        FilledTonalButton(
                                            onClick = {},
                                            enabled = false,
                                            colors = ButtonDefaults.filledTonalButtonColors(
                                                disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                                                disabledContentColor = MaterialTheme.colorScheme.primary
                                            )
                                        ) {
                                            Icon(Icons.Default.CheckCircle, contentDescription = "You're in", modifier = Modifier.size(ButtonDefaults.IconSize))
                                            Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                                            Text("You're in")
                                        }
                                    }
                                    hasApplied -> {
                                        FilledTonalButton(
                                            onClick = {},
                                            enabled = false,
                                            colors = ButtonDefaults.filledTonalButtonColors(
                                                disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                                                disabledContentColor = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        ) {
                                            Icon(Icons.Default.MarkEmailRead, contentDescription = "Request Sent", modifier = Modifier.size(ButtonDefaults.IconSize))
                                            Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                                            Text("Request Sent")
                                        }
                                    }
                                    !isTripOver && !isFull && userId != null -> {
                                        FilledTonalButton(onClick = { tripViewModel.joinTrip(userId) }) {
                                            Icon(Icons.Default.Group, contentDescription = "Join", modifier = Modifier.size(ButtonDefaults.IconSize))
                                            Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                                            Text("Join")
                                        }
                                    }
                                    else -> {}
                                }
                            }
                        }
                    }
                }

                // Right part (Details)
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    Row(
                        modifier = Modifier.fillMaxWidth().padding(top = 24.dp),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        EssentialInfoPill(title = "WHAT", body = trip.type.displayName.replaceFirstChar { it.uppercaseChar() })
                        Spacer(modifier = Modifier.size(8.dp))
                        EssentialInfoPill(title = "WHERE", body = trip.destination)
                        Spacer(modifier = Modifier.size(8.dp))
                        EssentialInfoPill(title = "WHEN", body = "${trip.startDate.split("/")[0]}/${trip.startDate.split("/")[1]} • ${trip.endDate.split("/")[0]}/${trip.endDate.split("/")[1]}")
                    }

                    Spacer(modifier = Modifier.size(6.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()).padding(12.dp),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        for (tag in trip.tags) {
                            TagPill(title = tag)
                            Spacer(modifier = Modifier.size(10.dp))
                        }
                    }

                    Column(
                        modifier = Modifier.padding(16.dp).clip(RoundedCornerShape(16.dp)).background(MaterialTheme.colorScheme.surface).padding(10.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Description(body = trip.description)
                        Itinerary(trip.stops)
                        AdditionalInfo(trip.priceEstimation.min, trip.priceEstimation.max, trip.suggestedActivities)

                        if(tripViewModel.isTripOver() && tripViewModel.reviewsWithAuthor.isNotEmpty()) {
                            TripReviewsList(reviews = tripViewModel.reviewsWithAuthor) { review ->
                                selectedReview = review
                                showReviewDialog = true
                            }
                        }
                    }

                    HorizontalDivider(modifier = Modifier.padding(start = 16.dp, end = 16.dp), color = MaterialTheme.colorScheme.outlineVariant)

                    Column(
                        modifier = Modifier.padding(16.dp).clip(RoundedCornerShape(16.dp)).background(MaterialTheme.colorScheme.surface).padding(10.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        CopyTripCard(
                            tripId = trip.tripId,
                            navController = navController,
                            onClick = {
                                val createRouteWithTripId = "${ToGetThereDestinations.CREATE_ROUTE}?tripId=${trip.tripId}"
                                navController.navigate(createRouteWithTripId) {
                                    launchSingleTop = true
                                    popUpTo(ToGetThereDestinations.HOME_ROUTE) {
                                        inclusive = true
                                    }
                                }
                            }
                        )
                    }

                    Spacer(modifier = Modifier.height(bottomPadding))
                }
            }
        }
    }

    if(showDialog && userId != null) {
        TripReviewDialog(
            isOpen = showDialog,
            tripTitle = tripViewModel.tripName,
            onDismiss = { showDialog = false },
            onSubmitReview = { score, title, description, images ->
                tripViewModel.addReview(tripViewModel.getTripId(), userId, score, title, description, images)
                showDialog = false
            }
        )
    }

    if (showReviewDialog && selectedReview != null) {
        CustomTripDialog(
            review = selectedReview!!,
            onDismiss = { showReviewDialog = false },
            onAuthorClick = { id -> navController.navigate(ToGetThereDestinations.profileRoute(id)) }
        )
    }
}
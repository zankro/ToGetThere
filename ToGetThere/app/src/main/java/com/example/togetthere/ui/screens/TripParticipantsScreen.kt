package com.example.togetthere.ui.screens

import android.util.Log
import androidx.compose.foundation.background
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
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import androidx.navigation.NavController
import com.example.togetthere.model.ParticipantActionState
import com.example.togetthere.model.ReservationStatus
import com.example.togetthere.ui.components.trip_comps.ImageCarousel
import com.example.togetthere.ui.components.trip_comps.MembersList
import com.example.togetthere.ui.components.TGT_ParticipantCard
import com.example.togetthere.ui.components.profile_comps.UserReviewDialog
import com.example.togetthere.ui.components.trip_comps.TripTopBar
import com.example.togetthere.ui.navigation.ToGetThereDestinations
import com.example.togetthere.ui.components.trip_comps.TripReviewDialog
import com.example.togetthere.ui.theme.CustomTheme
import com.example.togetthere.viewmodel.TripViewModel

@Composable
fun TripParticipantsView(navController: NavController, tripViewModel: TripViewModel, currentUserId: String?, bottomPadding: Dp) {
    val screenHeight = LocalConfiguration.current.screenHeightDp.dp

    val tripCreator by tripViewModel.creator.collectAsState()
    val tripParticipants by tripViewModel.participants.collectAsState()
    val requests = tripViewModel.tripReservationsList

    Log.d("TripParticipantsView", "🧾 currentUserId = $currentUserId")
////    val participantsToShow = tripCreator?.let { listOf(it) + tripParticipants } ?: tripParticipants
//
//    val requestsUsers = remember(tripParticipants, requests) {
//        // Se un partecipante ha una richiesta, viene usato anche qui sotto
//        requests.mapNotNull { r ->
//            tripParticipants.find { it.userId == r.bookerId }
//        }
//    }

    val confirmedRequests = remember(requests) {
        requests.filter { it.status == ReservationStatus.CONFIRMED }
    }

    val pendingRequests = remember(requests) {
        requests.filter { it.status == ReservationStatus.PENDING }
    }

    val confirmedParticipants = remember(tripParticipants, confirmedRequests) {
        confirmedRequests.mapNotNull { r ->
            tripParticipants.find { it.userId == r.bookerId }
        }
    }

    val pendingParticipants = remember(tripParticipants, pendingRequests) {
        pendingRequests.mapNotNull { r ->
            tripParticipants.find { it.userId == r.bookerId }
        }
    }

    val participantsToShow = tripCreator?.let { listOf(it) + confirmedParticipants } ?: confirmedParticipants

//    val requestsUsers = tripViewModel.getReservationsUsers()
    var actionState = ParticipantActionState.NONE
    var showDialog by remember { mutableStateOf(false) }
    var showReviewDialog by remember { mutableStateOf(false) }
    var userToReview by remember { mutableStateOf<String?>(null) }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        // Top-part of the view, where ImageCarousel, TripTopBar, TripTitle and MembersList are shown
        Box(
            modifier = Modifier
                .fillMaxHeight(0.52f)
        ) {

            ImageCarousel(tripViewModel.tripImages)

            Column(
                modifier = Modifier
                    .safeContentPadding()
                    .fillMaxHeight(0.9f),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                TripTopBar(navController, tripViewModel.getTripId())

                Column(
                    //                        modifier = Modifier
                    //                            .background(color = Color.Gray)
                    //                            .padding(vertical = 60.dp, horizontal = 16.dp),
                    //                        verticalArrangement = Arrangement.Bottom,
                    //                        modifier = Modifier.weight(0.8f)

                ) {
                    Text(
                        text = tripViewModel.tripName,
                        style = CustomTheme.typography.displayLargeWithShadow,
                        color = MaterialTheme.colorScheme.onPrimary,
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        MembersList(participantsToShow, tripViewModel.getTripId(), tripViewModel.tripNumParticipants, tripViewModel.tripMaxParticipants, onMoreClick = {})

                        val isTripOver = tripViewModel.isTripOver()
                        val isFull = tripViewModel.tripNumParticipants >= tripViewModel.tripMaxParticipants
                        val isCreator = tripViewModel.tripCreator == currentUserId
                        val hasApplied = if(currentUserId.isNullOrBlank()) { false } else { tripViewModel.hasUserApplied(currentUserId) }
                        val isConfirmedParticipant = if(currentUserId.isNullOrBlank()) { false } else { tripViewModel.isUserConfirmed(currentUserId) }
                        Log.d("TripParticipantsView", "isTripOver: $isTripOver, isFull: $isFull, isCreator: $isCreator, hasApplied: $hasApplied, isConfirmedParticipant: $isConfirmedParticipant")

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
                            !isTripOver && !isFull && currentUserId != null -> {
                                FilledTonalButton(
                                    onClick = { tripViewModel.joinTrip(currentUserId) },
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

        // Card
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = (screenHeight * 0.48f) /*- 28.dp*/) // or .offset() instead of .padding(), when not using a box
                .shadow(elevation = 16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    //                    .padding(24.dp)
                    //                    .padding(top = 24.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                // Essential Informations
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 24.dp),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Meet your buddies",
                        style = CustomTheme.typography.displayLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )

                }

                Spacer(modifier = Modifier.size(6.dp))

                // Participants List
                Column(
                    modifier = Modifier
                        .padding(16.dp)
                        .clip(shape = RoundedCornerShape(16.dp))
                        .background(color = MaterialTheme.colorScheme.surface)
                        .padding(10.dp),

                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    println("🚀 TripParticipantsView: tripParticipants = $tripParticipants")

                    // Creator
                    tripCreator?.let { creator ->   // This check if the creator is not null and then runs the code inside
                        TGT_ParticipantCard(
                            user = creator,
                            isCreator = true,
//                            actionState = actionState,
                            actionState = if(tripViewModel.isTripOver() && tripViewModel.tripCreator != currentUserId) {
                                ParticipantActionState.REVIEW
                            } else {
                                ParticipantActionState.NONE
                            },
                            onReviewClick = {
                                userToReview = tripViewModel.tripCreator
                                showReviewDialog = true
                            },
                            onAcceptClick = {},
                            onRejectClick = {},
                            onUserClick = {
                                navController.navigate(ToGetThereDestinations.profileRoute(creator.userId))
                            }
                        )
                    }

                    // Participants
                    if (confirmedParticipants.isNotEmpty()) {
                        for (participant in confirmedParticipants) {
                            actionState = if(tripViewModel.isTripOver() && participant.userId != currentUserId) {
                                ParticipantActionState.REVIEW
                            } else {
                                ParticipantActionState.NONE
                            }

                            val request = confirmedRequests.find { it.bookerId == participant.userId }!!
                            TGT_ParticipantCard(
                                user = participant,
                                extraParticipants = (request.numAdults + request.numChildren - 1),
                                isCreator = participant.userId == tripViewModel.getCreatorId(),
                                actionState = actionState,
                                onReviewClick = {
                                    userToReview = participant.userId
                                    showReviewDialog = true

                                },
                                onAcceptClick = {},
                                onRejectClick = {},
                                onUserClick = { navController.navigate(ToGetThereDestinations.profileRoute(participant.userId)) }
                            )
                        }
                    }

                    // Requests
                    if (!tripViewModel.isTripOver() && requests.isNotEmpty() && tripViewModel.tripCreator == currentUserId) {
                        for (request in pendingRequests) {
                            val user = pendingParticipants.find { it.userId == request.bookerId }

                            if (user != null) {
                                actionState = ParticipantActionState.PENDING_DECISION

                                TGT_ParticipantCard(
                                    user = user,
                                    extraParticipants = (request.numAdults + request.numChildren - 1),
                                    isCreator = false,
                                    actionState = actionState,
                                    onReviewClick = {},
                                    onAcceptClick = { tripViewModel.acceptReservation(request.bookerId) },
                                    onRejectClick = { tripViewModel.rejectReservation(request.bookerId) },
                                    onUserClick = { navController.navigate(ToGetThereDestinations.profileRoute(user.userId)) }
                                )
                            } else {
                                Log.w("TripParticipantsView", "⚠️ Utente non trovato per richiesta con id: ${request.bookerId}")
                            }
                        }
                    }

                }

                // Spacer for the bottom padding due to BottomBar
                Spacer(modifier = Modifier.height(bottomPadding))

            }
        }

    }

    if(showDialog && currentUserId != null) {
        TripReviewDialog(
            isOpen = showDialog,
            tripTitle = tripViewModel.tripName,
            onDismiss = { showDialog = false },
            onSubmitReview = { score, title, description, images ->
                tripViewModel.addReview(
                    tripId = tripViewModel.getTripId(),
                    author = currentUserId,
                    score = score,
                    title = title,
                    description = description,
                    photos = images
                )
                showDialog = false
            }
        )
    }

    if(showReviewDialog && currentUserId != null && userToReview != null) {
        UserReviewDialog(
            isOpen = showReviewDialog,
            reviewedUser = userToReview!!,
            reviewerUser = currentUserId,
            savedDescription = "",
            onDismiss = { showReviewDialog = false },
            onSubmitReview = { description, reviewed, reviewer ->
                tripViewModel.addUserReview(
                    reviewedUserId = reviewed,
                    reviewerUserId = reviewer,
                    description = description
                )
            },
            onDescriptionChange = { }
        )
    }

}
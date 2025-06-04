package com.example.togetthere.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.HourglassBottom
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SearchOff
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.navigation.NavController
import com.example.togetthere.model.Notification
import com.example.togetthere.model.NotificationType
import com.example.togetthere.model.UserProfile
import com.example.togetthere.ui.navigation.ToGetThereDestinations
import com.example.togetthere.ui.components.filters_comps.FilterCard
import com.example.togetthere.ui.components.trip_comps.TripPreview
import com.example.togetthere.ui.components.filters_comps.WhenCard
import com.example.togetthere.ui.components.filters_comps.WhereCard
import com.example.togetthere.ui.components.filters_comps.WhoCard
import com.example.togetthere.viewmodel.HomeFiltersViewModel
import com.example.togetthere.viewmodel.UserSessionViewModel
import java.text.SimpleDateFormat
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun HomeScreen(
    navController: NavController,
    homeFiltersViewModel: HomeFiltersViewModel,
    user: UserProfile?,
    landscape: Boolean,
    bottomPadding: Dp,
    topPadding: Dp
) {
    val isLoading by homeFiltersViewModel.isLoading

    val notifications = homeFiltersViewModel.notifications.collectAsState()
    val unreadCount by homeFiltersViewModel.unreadCount.collectAsState()
    var showNotifications by remember { mutableStateOf(false) }

    LaunchedEffect(user?.userId) {
        if (user != null) {
            homeFiltersViewModel.initializeTripsForUser(user.userId)
            homeFiltersViewModel.observeNotifications()
        } else {
            homeFiltersViewModel.initializeTripsForGuest()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = bottomPadding, top = topPadding)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            horizontalAlignment = if (landscape) Alignment.CenterHorizontally else Alignment.Start
        ) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    if (user != null) {
                        Text(
                            text = "Hi, ${user.name} \uD83D\uDC4B",
                            style = MaterialTheme.typography.headlineMedium,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    }
                    Text(
                        text = "Where do we go?",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }

                if (user != null) {
                    Surface(
                        modifier = Modifier
                            .size(48.dp)
                            .shadow(
                                elevation = 6.dp,
                                shape = CircleShape,
                            ),
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.surfaceContainer,
                        tonalElevation = 3.dp
                    ) {
                        IconButton(
                            onClick = { showNotifications = true },
                            modifier = Modifier.size(48.dp)
                        ) {
                            BadgedBox(
                                badge = {
                                    if (unreadCount > 0) {
                                        Badge {
                                            Text(
                                                text = if (unreadCount > 9) "9+" else unreadCount.toString(),
                                                color = MaterialTheme.colorScheme.onError
                                            )
                                        }
                                    }
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.Notifications,
                                    contentDescription = "Notifications",
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }
                }
            }

            if (landscape) {
                Box(
                    modifier = Modifier.fillMaxWidth(0.7f),
                    contentAlignment = Alignment.Center
                ) {
                    SearchBarComposable(onSearchClick = {
                        navController.navigate("filters")
                    })
                }
            } else {
                SearchBarComposable(onSearchClick = {
                    navController.navigate("filters")
                })
            }

            if (isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.HourglassBottom,
                            contentDescription = "Loading icon",
                            modifier = Modifier.size(70.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "Loading trips...",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            } else {
                TravelsPreviewList(navController, homeFiltersViewModel, landscape, user?.userId)
            }
        }
            if (showNotifications) {
                NotificationDropdown(
                    notifications = notifications.value,
                    unreadCount = unreadCount,
                    onNotificationClick = { notification ->
                        homeFiltersViewModel.removePending(notification.id)
                          when (notification.type) {
                              NotificationType.LastMinuteProposal -> {
                                  navController.navigate("tripDetailGraph/${notification.tripId}/tripDetailScreen")
                              }
                              NotificationType.TripReviewReceived -> {
                                  val reviewId = notification.reviewId
                                  if (reviewId != null) {
                                      navController.navigate(
                                          "tripDetailGraph/${notification.tripId}/tripDetailScreen?highlightReviewId=${notification.reviewId}"
                                      )
                                  } else {
                                      navController.navigate(
                                          "tripDetailGraph/${notification.tripId}/tripDetailScreen"
                                      )
                                  }
                              }
                              NotificationType.NewApplication -> {
                                  navController.navigate("tripDetailGraph/${notification.tripId}/tripDetailScreen")
                              }
                              NotificationType.ApplicationStatusUpdate -> {
                                  navController.navigate("tripDetailGraph/${notification.tripId}/tripDetailScreen")
                              }
                              NotificationType.UserReviewReceived -> {
                                  val reviewId = notification.reviewId
                                  if (reviewId != null) {
                                      navController.navigate("${ToGetThereDestinations.PROFILE_BASE_ROUTE}/${user?.userId}?reviewId=${reviewId}") {
                                      }
                                  } else {
                                      navController.navigate("${ToGetThereDestinations.PROFILE_BASE_ROUTE}/${user?.userId}") {
                                          launchSingleTop = true
                                      }
                                  }
                              }

                              NotificationType.Other -> {}
                          }
                        showNotifications = false
                    },
                    onDismiss = { showNotifications = false }
                )
            }
    }
}

@Composable
fun TravelsPreviewList(navController: NavController, homeFiltersViewModel: HomeFiltersViewModel, landscape: Boolean, userId: String?) {
    val allTrips by homeFiltersViewModel.filteredTrips.collectAsState()

    // Utility function invocation
//    LaunchedEffect(Unit) {
//        tripsViewModel.runTripImageMigrationIfNeeded()
//
//        navController.navigate("home") {
//            popUpTo("splash") { inclusive = true }
//        }
//    }

    if (allTrips.isEmpty()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = Icons.Default.SearchOff,
                    contentDescription = "Nessun risultato",
                    modifier = Modifier.size(64.dp),
                    tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "No trips match your filters",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                )
            }
        }
    } else {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 16.dp),
        ) {
            if (landscape) {
                items(allTrips.chunked(2)) { pair ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp) // padding esterno alla row
                    ) {
                        pair.forEachIndexed { index, trip ->
                            Column(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(240.dp)
                                    .padding(4.dp) // padding interno ai singoli item
                            ) {
                                val isFavourited =
                                    homeFiltersViewModel.favoriteTripStates[trip.tripId to userId]
                                        ?: false

                                LaunchedEffect(trip.tripId, userId) {
                                    homeFiltersViewModel.ensureFavoriteStatusLoaded(
                                        trip.tripId,
                                        userId
                                    )
                                }
                                TripPreview(
                                    trip,
                                    index,
                                    isFavourited,
                                    homeFiltersViewModel,
                                    userId
                                ) {
                                    navController.navigate(
                                        ToGetThereDestinations.tripDetailRoute(trip.tripId)
                                    )
                                }
                            }
                        }

                        if (pair.size == 1) {
                            Spacer(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(240.dp)
                                    .padding(4.dp)
                            )
                        }
                    }
                }

            } else {
                itemsIndexed(allTrips) { index, trip ->
                    Column(
                        modifier = Modifier
                            .height(240.dp)
                            .fillMaxWidth()
                    ) {
                        val isFavourited = homeFiltersViewModel.favoriteTripStates[trip.tripId to userId] ?: false

                        LaunchedEffect(trip.tripId, userId) {
                            homeFiltersViewModel.ensureFavoriteStatusLoaded(trip.tripId, userId)
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        TripPreview(trip, index, isFavourited, homeFiltersViewModel, userId) {
                            navController.navigate(
                                ToGetThereDestinations.tripDetailRoute(trip.tripId)
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun FilterScreen(navController: NavController,  homeFiltersViewModel: HomeFiltersViewModel, bottomPadding: Dp) {
    var isReset by rememberSaveable {mutableStateOf(false)}

    //Expanded
    var expandedDate by rememberSaveable { mutableStateOf(false) }
    var expandedPeople by rememberSaveable { mutableStateOf(false) }
    var expandedGeneralFilters by rememberSaveable { mutableStateOf(false) }

    //data
    val selectedPlace by homeFiltersViewModel.selectedPlace.collectAsState()
    val searchQuery by homeFiltersViewModel.searchQuery.collectAsState()
    val isSearchActive by homeFiltersViewModel.isSearchActive.collectAsState()
    val recentSearches by homeFiltersViewModel.recentSearches.collectAsState()
    val selectedStartDate by homeFiltersViewModel.selectedStartDate.collectAsState()
    val selectedEndDate by homeFiltersViewModel.selectedEndDate.collectAsState()
    val adultsCount by homeFiltersViewModel.adultsCount.collectAsState()
    val childrenCount by homeFiltersViewModel.childrenCount.collectAsState()
    val girlsOnly by homeFiltersViewModel.girlsOnly.collectAsState()
    val lgbtqFriendly by homeFiltersViewModel.lgbtqFriendly.collectAsState()
    val groupSize by homeFiltersViewModel.groupSize.collectAsState()
    val selectedTripType by homeFiltersViewModel.selectedTripType.collectAsState()
    val minAge by homeFiltersViewModel.minAge.collectAsState()
    val maxAge by homeFiltersViewModel.maxAge.collectAsState()
    val minPrice by homeFiltersViewModel.minPrice.collectAsState()
    val maxPrice by homeFiltersViewModel.maxPrice.collectAsState()

    LaunchedEffect(isReset) {
        if (isReset) {
            isReset = false
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Gray)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
            ) {
                Spacer(modifier = Modifier.height(54.dp))
                WhereCard(
                    selectedPlace = selectedPlace,
                    searchQuery = searchQuery,
                    isSearchActive = isSearchActive,
                    recentSearches = recentSearches,
                    onPlaceSelected = { homeFiltersViewModel.selectedPlace.value = it },
                    onSearchQueryChange = { homeFiltersViewModel.searchQuery.value = it },
                    onSearchActiveChange = { homeFiltersViewModel.isSearchActive.value = it },
                    onContinentSelected = { homeFiltersViewModel.selectedContinent.value = it },
                    onAddRecentSearch = { homeFiltersViewModel.addRecentSearch(it) },
                    isReset = isReset
                )
                Spacer(modifier = Modifier.height(14.dp))
                WhenCard(
                    selectedStartDate = selectedStartDate,
                    selectedEndDate = selectedEndDate,
                    onDatesSelected = { start, end ->
                        if (start != null && end != null) {
                            homeFiltersViewModel.selectedStartDate.value = start
                            homeFiltersViewModel.selectedEndDate.value = end
                        }
                    },
                    expanded = expandedDate,
                    onExpanded = {expandedDate = !expandedDate}
                )
                Spacer(modifier = Modifier.height(14.dp))
                WhoCard(
                    adultsCount = adultsCount,
                    childrenCount = childrenCount,
                    expanded = expandedPeople,
                    onExpanded = { expandedPeople = !expandedPeople },
                    onAdultsChange = { homeFiltersViewModel.adultsCount.value = it },
                    onChildrenChange = { homeFiltersViewModel.childrenCount.value = it },
                    onConfirm = {
                        homeFiltersViewModel.totalGuestCount.value = homeFiltersViewModel.adultsCount.value + homeFiltersViewModel.childrenCount.value
                        expandedPeople = false
                    },
                    onCancel = {
                        homeFiltersViewModel.adultsCount.value = 0
                        homeFiltersViewModel.childrenCount.value = 0
                        homeFiltersViewModel.totalGuestCount.value = 0
                    }
                )
                Spacer(modifier = Modifier.height(14.dp))
                FilterCard(
                    girlsOnly = girlsOnly,
                    lgbtqFriendly = lgbtqFriendly,
                    groupSize = groupSize,
                    selectedTripType = selectedTripType,
                    minAge = minAge,
                    maxAge = maxAge,
                    minPrice = minPrice,
                    maxPrice = maxPrice,
                    expanded = expandedGeneralFilters,
                    onExpanded = {expandedGeneralFilters = !expandedGeneralFilters},
                    onGirlsOnlyChanged = { homeFiltersViewModel.girlsOnly.value = it },
                    onLgbtqFriendlyChanged = { homeFiltersViewModel.lgbtqFriendly.value = it },
                    onGroupSizeChanged = { homeFiltersViewModel.groupSize.value = it },
                    onTripTypeSelected = { homeFiltersViewModel.selectedTripType.value = it },
                    onAgeRangeChanged = { min, max ->
                        homeFiltersViewModel.minAge.value = min
                        homeFiltersViewModel.maxAge.value = max
                    },
                    onPriceRangeChanged = { min, max ->
                        homeFiltersViewModel.minPrice.value = min
                        homeFiltersViewModel.maxPrice.value = max
                    }
                )
                Spacer(modifier = Modifier.weight(1f))
            }
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = bottomPadding),
                color = Color.Gray
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp, start = 16.dp, end = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    TextButton(onClick = {
                        homeFiltersViewModel.selectedPlace.value = ""
                        homeFiltersViewModel.selectedStartDate.value = ""
                        homeFiltersViewModel.selectedEndDate.value = ""
                        homeFiltersViewModel.totalGuestCount.value = 0
                        homeFiltersViewModel.girlsOnly.value = false
                        homeFiltersViewModel.lgbtqFriendly.value = false
                        homeFiltersViewModel.groupSize.value = 50f
                        homeFiltersViewModel.selectedTripType.value = null
                        homeFiltersViewModel.minAge.value = 0f
                        homeFiltersViewModel.maxAge.value = 100f
                        homeFiltersViewModel.minPrice.value = 0f
                        homeFiltersViewModel.maxPrice.value = 1500f
                        homeFiltersViewModel.adultsCount.value = 0
                        homeFiltersViewModel.childrenCount.value = 0
                        isReset = true

                        homeFiltersViewModel.resetFilters()
                    }) {
                        Text(
                            text = "Reset",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            textDecoration = TextDecoration.Underline,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    Button(
                        onClick = {
                            homeFiltersViewModel.applyFilters()
                            navController.popBackStack()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                        shape = RoundedCornerShape(20.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(5.dp))
                        Text("Search", color = MaterialTheme.colorScheme.primary)
                    }
                }
            }
        }
        Box(
            modifier = Modifier
                .padding(start = 22.dp, top = 26.dp)
                .size(30.dp) //
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.onPrimary)
                .border(1.dp, MaterialTheme.colorScheme.surfaceContainer, CircleShape)
                .clickable { navController.popBackStack() },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Close",
                tint = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}


@Composable
fun NotificationDropdown(
    notifications: List<Notification>,
    unreadCount: Int,
    onNotificationClick: (Notification) -> Unit,
    onDismiss: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.3f))
            .clickable(onClick = onDismiss)
            .zIndex(1f),
        contentAlignment = Alignment.TopEnd
    ) {
        Surface(
            modifier = Modifier
                .widthIn(min = 300.dp, max = 320.dp)
                .padding(top = 56.dp, end = 16.dp)
                .shadow(16.dp),
            shape = MaterialTheme.shapes.medium,
            color = MaterialTheme.colorScheme.surface
        ) {
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                // Header
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Notifications,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(30.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Notifications",
                        style = MaterialTheme.typography.titleLarge
                    )
                }

                HorizontalDivider()

                // Lista notifiche
                LazyColumn(
                    modifier = Modifier.heightIn(max = 500.dp)
                ) {
                    if (notifications.isEmpty()) {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(24.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("No notifications")
                            }
                        }
                    } else {
                        items(notifications) { notification ->
                            NotificationItem(
                                notification = notification,
                                onClick = { onNotificationClick(notification) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun NotificationItem(
    notification: Notification,
    onClick: () -> Unit
) {

    val date = notification.timestamp.toDate()
    val formatter = SimpleDateFormat("dd MMM HH:mm", Locale.getDefault())
    val formatted = formatter.format(date)

    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = if (notification.pending) {
            MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
        } else {
            MaterialTheme.colorScheme.surface
        },
        onClick = onClick
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Icona in base al tipo
                Icon(
                    imageVector = when (notification.type) {
                        NotificationType.LastMinuteProposal -> Icons.Default.Bolt
                        NotificationType.NewApplication -> Icons.Default.PersonAdd
                        NotificationType.TripReviewReceived -> Icons.Default.Star
                        NotificationType.ApplicationStatusUpdate -> Icons.Default.Info
                        NotificationType.UserReviewReceived -> Icons.Default.Star
                        NotificationType.Other -> TODO()
                    },
                    contentDescription = "notification_icon",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = when (notification.type) {
                        NotificationType.LastMinuteProposal -> "Last minute proposal waiting for you!"
                        NotificationType.NewApplication -> "Someone wants to join your trip!"
                        NotificationType.TripReviewReceived -> "Your trip received a review from someone"
                        NotificationType.ApplicationStatusUpdate -> "Update on your application"
                        NotificationType.UserReviewReceived -> "Someone wrote a review on your profile!"
                        NotificationType.Other -> ""
                    },
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = if (notification.pending) FontWeight.Bold else FontWeight.Normal
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = formatted,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchBarComposable(onSearchClick: () -> Unit) {
    SearchBar(
        query = "",
        onQueryChange = { },
        onSearch = { },
        active = false,
        onActiveChange = { onSearchClick() },
        modifier = Modifier.fillMaxWidth(),
        placeholder = { Text("Search destination") },
        leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search") },
        trailingIcon = { Icon(Icons.Default.FilterList, contentDescription = "Filter")}
    ) {
    }
}

@Composable
fun FilterScreenLandscape(navController: NavController, homeFiltersViewModel: HomeFiltersViewModel, bottomPadding: Dp) {
    var isReset by rememberSaveable {mutableStateOf(false)}

    //Expanded
    var expandedDate by rememberSaveable { mutableStateOf(true) }
    var expandedPeople by rememberSaveable { mutableStateOf(true) }
    var expandedGeneralFilters by rememberSaveable { mutableStateOf(true) }

    //data
    val selectedPlace by homeFiltersViewModel.selectedPlace.collectAsState()
    val searchQuery by homeFiltersViewModel.searchQuery.collectAsState()
    val isSearchActive by homeFiltersViewModel.isSearchActive.collectAsState()
    val recentSearches by homeFiltersViewModel.recentSearches.collectAsState()
    val selectedStartDate by homeFiltersViewModel.selectedStartDate.collectAsState()
    val selectedEndDate by homeFiltersViewModel.selectedEndDate.collectAsState()
    val adultsCount by homeFiltersViewModel.adultsCount.collectAsState()
    val childrenCount by homeFiltersViewModel.childrenCount.collectAsState()
    val girlsOnly by homeFiltersViewModel.girlsOnly.collectAsState()
    val lgbtqFriendly by homeFiltersViewModel.lgbtqFriendly.collectAsState()
    val groupSize by homeFiltersViewModel.groupSize.collectAsState()
    val selectedTripType by homeFiltersViewModel.selectedTripType.collectAsState()
    val minAge by homeFiltersViewModel.minAge.collectAsState()
    val maxAge by homeFiltersViewModel.maxAge.collectAsState()
    val minPrice by homeFiltersViewModel.minPrice.collectAsState()
    val maxPrice by homeFiltersViewModel.maxPrice.collectAsState()

    LaunchedEffect(isReset) {
        if (isReset) {
            isReset = false
        }
    }

    var selectedFilterIndex by rememberSaveable { mutableIntStateOf(0) }
    val filterOptions = listOf("Where", "When", "Who", "Filters")

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Gray)
            .padding(bottom = bottomPadding)
    ) {
        Row(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .width(170.dp)
                    .fillMaxHeight()
                    .background(MaterialTheme.colorScheme.surfaceContainer)
                    .padding(vertical = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .align(Alignment.Start)
                        .padding(start = 15.dp, top = 4.dp)
                        .size(30.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.onPrimary)
                        .border(1.dp, MaterialTheme.colorScheme.surfaceContainer, CircleShape)
                        .clickable { navController.popBackStack() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.size(18.dp)
                    )
                }

                Spacer(modifier = Modifier.height(5.dp))

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    filterOptions.forEachIndexed { index, label ->
                        FilterNavButton(
                            label = label,
                            isSelected = selectedFilterIndex == index,
                            onClick = { selectedFilterIndex = index }
                        )

                        if (index < filterOptions.size - 1) {
                            Spacer(modifier = Modifier.height(1.dp))
                        }
                    }
                }

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 22.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Button(
                        onClick = {
                            homeFiltersViewModel.applyFilters()
                            navController.popBackStack()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                        shape = RoundedCornerShape(20.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(5.dp))
                        Text("Search", color = MaterialTheme.colorScheme.primary)
                    }

                    TextButton(
                        onClick = {
                            homeFiltersViewModel.selectedPlace.value = ""
                            homeFiltersViewModel.selectedStartDate.value = ""
                            homeFiltersViewModel.selectedEndDate.value = ""
                            homeFiltersViewModel.totalGuestCount.value = 0
                            homeFiltersViewModel.girlsOnly.value = false
                            homeFiltersViewModel.lgbtqFriendly.value = false
                            homeFiltersViewModel.groupSize.value = 50f
                            homeFiltersViewModel.selectedTripType.value = null
                            homeFiltersViewModel.minAge.value = 0f
                            homeFiltersViewModel.maxAge.value = 100f
                            homeFiltersViewModel.minPrice.value = 0f
                            homeFiltersViewModel.maxPrice.value = 1500f
                            homeFiltersViewModel.adultsCount.value = 0
                            homeFiltersViewModel.childrenCount.value = 0
                            isReset = true

                            homeFiltersViewModel.resetFilters()
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "Reset",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            textDecoration = TextDecoration.Underline,
                            color = MaterialTheme.colorScheme.primary,
                        )
                    }
                }
            }

            // Right column
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight()
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                when (selectedFilterIndex) {
                    0 -> WhereCard(
                        selectedPlace = selectedPlace,
                        searchQuery = searchQuery,
                        isSearchActive = isSearchActive,
                        recentSearches = recentSearches,
                        onPlaceSelected = { homeFiltersViewModel.selectedPlace.value = it },
                        onSearchQueryChange = { homeFiltersViewModel.searchQuery.value = it },
                        onSearchActiveChange = { homeFiltersViewModel.isSearchActive.value = it },
                        onContinentSelected = { homeFiltersViewModel.selectedContinent.value = it },
                        onAddRecentSearch = { homeFiltersViewModel.addRecentSearch(it) },
                        isReset = isReset
                    )
                    1 -> WhenCard(
                        selectedStartDate = selectedStartDate,
                        selectedEndDate = selectedEndDate,
                        onDatesSelected = { start, end ->
                            if (start != null && end != null) {
                                homeFiltersViewModel.selectedStartDate.value = start
                                homeFiltersViewModel.selectedEndDate.value = end
                            }
                        },
                        expanded = expandedDate,
                        onExpanded = {expandedDate = !expandedDate}
                    )
                    2 -> WhoCard(
                        adultsCount = adultsCount,
                        childrenCount = childrenCount,
                        expanded = expandedPeople,
                        onExpanded = { expandedPeople = !expandedPeople },
                        onAdultsChange = { homeFiltersViewModel.adultsCount.value = it },
                        onChildrenChange = { homeFiltersViewModel.childrenCount.value = it },
                        onConfirm = {
                            homeFiltersViewModel.totalGuestCount.value = homeFiltersViewModel.adultsCount.value + homeFiltersViewModel.childrenCount.value
                            expandedPeople = false
                        },
                        onCancel = {
                            homeFiltersViewModel.adultsCount.value = 0
                            homeFiltersViewModel.childrenCount.value = 0
                            homeFiltersViewModel.totalGuestCount.value = 0
                        }
                    )
                    3 -> FilterCard(
                        girlsOnly = girlsOnly,
                        lgbtqFriendly = lgbtqFriendly,
                        groupSize = groupSize,
                        selectedTripType = selectedTripType,
                        minAge = minAge,
                        maxAge = maxAge,
                        minPrice = minPrice,
                        maxPrice = maxPrice,
                        expanded = expandedGeneralFilters,
                        onExpanded = {expandedGeneralFilters = !expandedGeneralFilters},
                        onGirlsOnlyChanged = { homeFiltersViewModel.girlsOnly.value = it },
                        onLgbtqFriendlyChanged = { homeFiltersViewModel.lgbtqFriendly.value = it },
                        onGroupSizeChanged = { homeFiltersViewModel.groupSize.value = it },
                        onTripTypeSelected = { homeFiltersViewModel.selectedTripType.value = it },
                        onAgeRangeChanged = { min, max ->
                            homeFiltersViewModel.minAge.value = min
                            homeFiltersViewModel.maxAge.value = max
                        },
                        onPriceRangeChanged = { min, max ->
                            homeFiltersViewModel.minPrice.value = min
                            homeFiltersViewModel.maxPrice.value = max
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun FilterNavButton(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 22.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (isSelected)
                MaterialTheme.colorScheme.primary
            else
                MaterialTheme.colorScheme.surface
        ),
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = if (isSelected) 6.dp else 2.dp
        ),
        shape = RoundedCornerShape(15.dp)
    ) {
        Text(
            text = label,
            color = if (isSelected)
                MaterialTheme.colorScheme.onPrimary
            else
                MaterialTheme.colorScheme.onSurface
        )
    }
}


package com.example.togetthere.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.togetthere.viewmodel.UserSessionViewModel

data class BottomNavItem(
    val label: String,
    val route: String,
    val icon: ImageVector
)

/**
 * Destinations used in the [com.example.togetthere.ui.navigation.ToGetThereApp].
 */
object ToGetThereDestinations {
    const val HOME_ROUTE = "home"
    const val TRIPS_ROUTE = "trips"
    const val CREATE_ROUTE = "create"
    const val CHATS_ROUTE = "chats"
//    const val PROFILE_ROUTE = "user"
    const val FILTERS_ROUTE = "filters"
    const val PROFILE_BASE_ROUTE = "user" // base
    const val TRIP_DETAIL_GRAPH = "tripDetailGraph"
    const val TRIP_DETAIL_ROUTE = "tripDetailScreen"
    const val TRIP_EDIT_ROUTE = "edit"
    const val TRIP_PARTICIPANTS_ROUTE = "tripParticipants"
    const val LOGIN_ROUTE = "login"
    const val SIGNUP_ROUTE = "registration"
    const val PROFILE_NOT_LOGGED_ROUTE = "guest"
    fun editProfileRoute(userId: String) = "$PROFILE_BASE_ROUTE/$userId/edit"

    fun tripDetailGraphRoute(tripId: Int) = "$TRIP_DETAIL_GRAPH/$tripId"
    fun tripDetailRoute(tripId: Int) = "$TRIP_DETAIL_GRAPH/$tripId/$TRIP_DETAIL_ROUTE"
    fun tripEditRoute(tripId: Int) = "$TRIP_DETAIL_GRAPH/$tripId/$TRIP_EDIT_ROUTE"
    fun tripParticipantsRoute(tripId: Int) = "$TRIP_DETAIL_GRAPH/$tripId/$TRIP_PARTICIPANTS_ROUTE"
    fun profileRoute(userId: String) = "$PROFILE_BASE_ROUTE/$userId"
}

/**
 * Models the navigation actions in the app.
 */
class ToGetThereNavigationActions(navController: NavHostController) {
    val navigateToHome: () -> Unit = {
        navController.navigate(ToGetThereDestinations.HOME_ROUTE) {
            // Pop up to the start destination of the graph to
            // avoid building up a large stack of destinations
            // on the back stack as users select items
            popUpTo(navController.graph.findStartDestination().id) {
//                saveState = true
            }
            // Avoid multiple copies of the same destination when
            // reselecting the same item
            launchSingleTop = true
            // Restore state when reselecting a previously selected item
//            restoreState = true
        }
    }
    val navigateToTrips: () -> Unit = {
        navController.navigate(ToGetThereDestinations.TRIPS_ROUTE) {
            popUpTo(navController.graph.findStartDestination().id) {
//                saveState = true
            }
            launchSingleTop = true

//            restoreState = true
        }
    }
    /*val navigateToCreate: () -> Unit = {
        navController.navigate(ToGetThereDestinations.CREATE_ROUTE) {
            popUpTo(navController.graph.findStartDestination().id) {
                saveState = true
            }
            launchSingleTop = true

            restoreState = true
        }
    }*/
    val navigateToCreate: (Int?) -> Unit = { tripId ->
        val route = if (tripId != null && tripId > 0) {
            "${ToGetThereDestinations.CREATE_ROUTE}?tripId=$tripId"
        } else {
            ToGetThereDestinations.CREATE_ROUTE
        }

        navController.navigate(route) {
            popUpTo(navController.graph.findStartDestination().id) {
                saveState = true
            }
            launchSingleTop = true
            restoreState = true
        }
    }
    val navigateToChats: () -> Unit = {
        navController.navigate(ToGetThereDestinations.CHATS_ROUTE) {
            popUpTo(navController.graph.findStartDestination().id) {
//                saveState = true
            }
            launchSingleTop = true

//            restoreState = true
        }
    }
    val navigateToProfile: (String) -> Unit = { userId ->
        navController.navigate(ToGetThereDestinations.profileRoute(userId)) {
            popUpTo(navController.graph.findStartDestination().id) {
//                saveState = true
            }
            launchSingleTop = true
//            restoreState = true
        }
    }
    val navigateToProfileEdit: (String) -> Unit = { userId ->
        navController.navigate(ToGetThereDestinations.editProfileRoute(userId)) {
            popUpTo(navController.graph.findStartDestination().id) {
                saveState = true
            }
            launchSingleTop = true
            restoreState = true
        }
    }
    val navigateToTripDetail: (Int) -> Unit = { tripId ->   // Consider using this later on
        navController.navigate("tripDetail/$tripId")
    }
//    val navigateToInterests: () -> Unit = {
//        navController.navigate(ToGetThereDestinations.INTERESTS_ROUTE) {
//            popUpTo(navController.graph.findStartDestination().id) {
//                saveState = true
//            }
//            launchSingleTop = true
//            restoreState = true
//        }
//    }
}

@Composable
fun BottomNavBar(navController: NavHostController, navigationActions: ToGetThereNavigationActions, userSessionViewModel: UserSessionViewModel) {
    val currentUserId = userSessionViewModel.currentUser.collectAsState().value?.userId

    val items = listOf(
        BottomNavItem("Home", ToGetThereDestinations.HOME_ROUTE, Icons.Default.Home),
        BottomNavItem("Trips", ToGetThereDestinations.TRIPS_ROUTE, Icons.AutoMirrored.Filled.ArrowForward),
        BottomNavItem("Create", ToGetThereDestinations.CREATE_ROUTE, Icons.Default.AddCircle),
        BottomNavItem("Chats", ToGetThereDestinations.CHATS_ROUTE, Icons.Default.Email),
        BottomNavItem("Profile", ToGetThereDestinations.PROFILE_BASE_ROUTE, Icons.Default.Person),
    )

    val currentDestination by navController.currentBackStackEntryAsState()
    val currentRoute = currentDestination?.destination?.route

    NavigationBar{
        items.forEach { item ->
            val action = when (item.label) {
                "Home" -> navigationActions.navigateToHome
                "Trips" -> navigationActions.navigateToTrips
                "Create" -> { -> navigationActions.navigateToCreate(null) }
                "Chats" -> navigationActions.navigateToChats
                "Profile" -> if (currentUserId != null) {
                    { navigationActions.navigateToProfile(currentUserId) }
                } else {
                    { navController.navigate(ToGetThereDestinations.PROFILE_NOT_LOGGED_ROUTE) }
                }
                else -> null
            }
            val isSelected = if (item.label == "Profile") {
                currentRoute?.startsWith(ToGetThereDestinations.PROFILE_BASE_ROUTE) == true ||
                        currentRoute == ToGetThereDestinations.PROFILE_NOT_LOGGED_ROUTE
            } else {
                currentRoute?.startsWith(item.route) == true
            }
            NavigationBarItem(
                selected = isSelected,
                onClick = {
                    if (currentRoute != item.route) {
//                        navController.navigate(item.route) {
//                            popUpTo(navController.graph.startDestinationId) {
//                                saveState = true
//                            }
//                            launchSingleTop = true
//                            restoreState = true
//                        }
                        action?.invoke()
                    }
                },
                icon = { Icon(item.icon, contentDescription = item.label) },
                label = { Text(item.label) }
            )
        }
    }
}
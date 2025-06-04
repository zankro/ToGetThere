package com.example.togetthere.ui.navigation

import android.app.Application
import android.content.res.Configuration
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.navArgument
import androidx.navigation.navDeepLink
import com.example.togetthere.AuthRepository
import com.example.togetthere.GoogleAuthUiClient
import com.example.togetthere.data.AppContainer
import com.example.togetthere.ui.components.NotLoggedInComponent
import com.example.togetthere.ui.screens.ChatsScreen
import com.example.togetthere.ui.screens.CreateScreen
import com.example.togetthere.ui.screens.EditProfileScreen
import com.example.togetthere.ui.screens.EditTravelProposalView
import com.example.togetthere.ui.screens.FilterScreen
import com.example.togetthere.ui.screens.FilterScreenLandscape
import com.example.togetthere.ui.screens.HomeScreen
import com.example.togetthere.ui.screens.LoginScreen
import com.example.togetthere.ui.screens.ProfileScreen
import com.example.togetthere.ui.screens.SignupFlow
import com.example.togetthere.ui.screens.TripParticipantsView
import com.example.togetthere.ui.screens.TripView
import com.example.togetthere.ui.screens.TripsScreen
import com.example.togetthere.viewmodel.ChatViewModel
import com.example.togetthere.viewmodel.HomeFiltersViewModel
import com.example.togetthere.viewmodel.ProfileViewModel
import com.example.togetthere.viewmodel.SignInViewModel
import com.example.togetthere.viewmodel.TripViewModel
import com.example.togetthere.viewmodel.TripsPageViewModel
import com.example.togetthere.viewmodel.TripsViewModel
import com.example.togetthere.viewmodel.UserSessionViewModel
import com.google.firebase.Firebase
import com.google.firebase.auth.auth

@Composable
fun ToGetThereNavGraph(
    appContainer: AppContainer,
    navController: NavHostController,
    modifier: Modifier = Modifier,
    startDestination: String = ToGetThereDestinations.HOME_ROUTE,
    googleAuthUiClient: GoogleAuthUiClient,
    topPadding: Dp,
    bottomPadding: Dp,
    userSessionViewModel: UserSessionViewModel
) {
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
    val tripsPageViewModel: TripsPageViewModel = viewModel(
        factory = TripsPageViewModel.provideFactory(appContainer.mainRepository.tripRepository)
    )
    val homeFiltersViewModel: HomeFiltersViewModel = viewModel(
        factory = HomeFiltersViewModel.provideFactory(
            appContainer.mainRepository.tripRepository,
            appContainer.mainRepository.notificationRepository,
            Firebase.auth.currentUser?.uid
        )
    )

//    val user = userSessionViewModel.currentUser.value?.userId
    val currentUser by userSessionViewModel.currentUser.collectAsState()

    LaunchedEffect(currentUser?.userId) {
        val userId = currentUser?.userId
        if (userId != null) {
            homeFiltersViewModel.initializeTripsForUser(userId)
        }
    }

    val navigationBarHeight = WindowInsets.navigationBars
        .asPaddingValues()
        .calculateBottomPadding()

    val correctedBottomPadding = maxOf(0.dp, bottomPadding - navigationBarHeight)

    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        composable(route = ToGetThereDestinations.HOME_ROUTE) {
            HomeScreen(
                navController,
                homeFiltersViewModel = homeFiltersViewModel,
                user = currentUser,
                landscape = isLandscape,
                bottomPadding = bottomPadding,
                topPadding = topPadding
            )
        }
        composable(
            route = "${ToGetThereDestinations.CREATE_ROUTE}?tripId={tripId}",
            arguments = listOf(
                navArgument("tripId") {
                    type = NavType.IntType
                    defaultValue = -1
                    nullable = false
                }
            )
        ) { backStackEntry ->
            val tripId = backStackEntry.arguments?.getInt("tripId")
            // Only pass tripId if it's not the default value
            val tripToCopy = if (tripId != -1) tripId else null
            val tripsViewModel: TripsViewModel = viewModel(
                factory = TripsViewModel.provideFactory(
                    appContainer.mainRepository
                )
            )
            val tripViewModel: TripViewModel = viewModel(
                factory = TripViewModel.provideFactory(appContainer.mainRepository, tripId!!)
            )
            CreateScreen(
                navController = navController,
                tripId = tripToCopy,
                tripsViewModel = tripsViewModel,
                isLandscape = isLandscape,
                bottomPadding = bottomPadding,
                user = currentUser
            )
        }

        composable(route = ToGetThereDestinations.TRIPS_ROUTE) {
            TripsScreen(
                navController = navController,
                tripViewModel = tripsPageViewModel,
                user = currentUser,
                bottomPadding = bottomPadding,
                isLandscape = isLandscape
            )
        }

        composable(route = ToGetThereDestinations.CHATS_ROUTE) {
            val chatViewModel: ChatViewModel = viewModel(
                factory = ChatViewModel.provideFactory(
                    appContainer.mainRepository.chatRepository,
                    appContainer.mainRepository.userProfileRepository
                )
            )

            ChatsScreen(
                isLandscape = isLandscape,
                navController = navController,
                bottomPadding = bottomPadding,
                userSessionViewModel = userSessionViewModel,
                chatViewModel = chatViewModel
            )
        }
//        composable(route = ToGetThereDestinations.PROFILE_ROUTE) { ProfileScreen(navController, userViewModel = viewModel(factory = UserProfileViewModel.provideFactory(appContainer.mainRepository.userProfileRepository, appContainer.currentUser.userId))) }
        composable(
            route = "${ToGetThereDestinations.PROFILE_BASE_ROUTE}/{userId}?reviewId={reviewId}",
            arguments = listOf(
                navArgument("userId") { type = NavType.StringType },
                navArgument("reviewId") {
                    type = NavType.IntType
                    defaultValue = -1
                    nullable = false
                }
            )
        ) { backStackEntry ->
            val userId = backStackEntry.arguments?.getString("userId")
            if (userId == null) {
                // Se manca userId, reindirizza alla schermata non loggata
                LaunchedEffect(Unit) {
                    navController.navigate(ToGetThereDestinations.PROFILE_NOT_LOGGED_ROUTE) {
                        popUpTo(0)
                    }
                }
                return@composable
            }

            val reviewId = backStackEntry.arguments?.getInt("reviewId") ?: -1

            val profileViewModel: ProfileViewModel = viewModel(
                factory = ProfileViewModel.provideFactory(
                    appContainer.mainRepository.userProfileRepository,
                    appContainer.mainRepository.reviewRepository,
                    userId
                )
            )

            val authRepository = remember { AuthRepository() }
            val context = LocalContext.current
            val application = context.applicationContext as Application

            val signInViewModel: SignInViewModel = viewModel(
                factory = object : ViewModelProvider.Factory {
                    override fun <T : ViewModel> create(modelClass: Class<T>): T {
                        if (modelClass.isAssignableFrom(SignInViewModel::class.java)) {
                            @Suppress("UNCHECKED_CAST")
                            return SignInViewModel(
                                application,
                                googleAuthUiClient,
                                authRepository,
                                userSessionViewModel
                            ) as T
                        }
                        throw IllegalArgumentException("Unknown ViewModel class")
                    }
                }
            )


            ProfileScreen(
                isLandscape = isLandscape,
                navController = navController,
                sessionVm = userSessionViewModel,
                vm = profileViewModel,
                user = currentUser,
                topPadding = topPadding,
                bottomPadding = bottomPadding,
                highlightReviewId = if (reviewId != -1) reviewId else null,
                signInViewModel = signInViewModel
            )
        }
        composable(
            route = "${ToGetThereDestinations.PROFILE_BASE_ROUTE}/{userId}/edit",
            arguments = listOf(navArgument("userId") { type = NavType.StringType })
        ) { backStackEntry ->
            val userId = backStackEntry.arguments?.getString("userId") ?: return@composable

            val profileViewModel: ProfileViewModel = viewModel(
                factory = ProfileViewModel.provideFactory(
                    appContainer.mainRepository.userProfileRepository,
                    appContainer.mainRepository.reviewRepository,
                    userId
                )
            )

            EditProfileScreen(
                navController = navController,
                vm = profileViewModel,
                topPadding = topPadding,
                bottomPadding = bottomPadding
            )
        }
        composable(
            route = ToGetThereDestinations.PROFILE_NOT_LOGGED_ROUTE // Usa la nuova rotta
        ) {
            val currentUser by userSessionViewModel.currentUser.collectAsState()

            if (currentUser == null) {
                NotLoggedInComponent(isLandscape, navController, bottomPadding)
            } else {
                LaunchedEffect(Unit) {
                    navController.navigate("${ToGetThereDestinations.PROFILE_BASE_ROUTE}/${currentUser?.userId}") {
                        popUpTo(ToGetThereDestinations.PROFILE_NOT_LOGGED_ROUTE) {
                            inclusive = true
                        }
                    }
                }
            }
        }
//        composable(
//            route = ToGetThereDestinations.TRIP_DETAIL_ROUTE + "/{tripId}",
//            arguments = listOf(navArgument("tripId") { type = NavType.IntType })
//        ) { backStackEntry ->
//            val tripId = backStackEntry.arguments?.getInt("tripId") ?: return@composable
//            TripView(navController, tripViewModel = viewModel(factory = TripViewModel.provideFactory(appContainer.mainRepository, tripId)), user, bottomPadding = bottomPadding)
//        }
//        composable(
//            route = ToGetThereDestinations.TRIP_DETAIL_ROUTE + "/{tripId}/" + ToGetThereDestinations.TRIP_PARTICIPANTS_ROUTE,
//            arguments = listOf(navArgument("tripId") { type = NavType.IntType })
//        ) { backStackEntry ->
//            val tripId = backStackEntry.arguments?.getInt("tripId") ?: return@composable
//            TripParticipantsView(navController, tripViewModel = viewModel(factory = TripViewModel.provideFactory(appContainer.mainRepository, tripId)), bottomPadding = bottomPadding)
//        }
//        composable(
//            route = ToGetThereDestinations.TRIP_DETAIL_ROUTE + "/{tripId}/" + ToGetThereDestinations.TRIP_EDIT_ROUTE,
//            arguments = listOf(navArgument("tripId") { type = NavType.IntType })
//        ) { backStackEntry ->
//            val tripId = backStackEntry.arguments?.getInt("tripId") ?: return@composable
//            EditTravelProposalView(navController, vm = viewModel(factory = TripViewModel.provideFactory(appContainer.mainRepository, tripId)), bottomPadding = correctedBottomPadding)
//        }
        navigation(
            route = "tripDetailGraph/{tripId}",
            startDestination = "tripDetailGraph/{tripId}/tripDetailScreen"
        ) {
            composable(
                route = "tripDetailGraph/{tripId}/tripDetailScreen?highlightReviewId={highlightReviewId}",
                arguments = listOf(
                    navArgument("tripId") { type = NavType.IntType },
                    navArgument("highlightReviewId") {
                        type = NavType.IntType
                        defaultValue = -1  // -1 = nessuna review da evidenziare
                    }
                ),
                deepLinks = listOf(
                    navDeepLink {
                        uriPattern = "https://togetthere.app/trip/{tripId}"
                    }
                )
            ) { backStackEntry ->
                val tripId = backStackEntry.arguments?.getInt("tripId") ?: return@composable
                val highlightReviewId =
                    backStackEntry.arguments?.getInt("highlightReviewId").takeIf { it != -1 }
                val parentEntry = remember(backStackEntry) {
                    navController.getBackStackEntry("tripDetailGraph/{tripId}")
                }
                val tripViewModel: TripViewModel = viewModel(
                    parentEntry,
                    factory = TripViewModel.provideFactory(appContainer.mainRepository, tripId)
                )
//                TripView(navController, tripViewModel, user.toString(), bottomPadding, highlightReviewId = highlightReviewId)
                val userId = currentUser?.userId

//                if (userId == null) {
//                    // Mostra un loader o gestisci il caso di utente non caricato
//                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
//                        CircularProgressIndicator()
//                    }
//                } else {
                TripView(
                    navController = navController,
                    tripViewModel = tripViewModel,
                    userId = userId,
                    bottomPadding = bottomPadding,
                    highlightReviewId = highlightReviewId
                )
//                }
            }

            composable(
                route = "tripDetailGraph/{tripId}/tripParticipants",
                arguments = listOf(navArgument("tripId") { type = NavType.IntType })
            ) { backStackEntry ->
                val tripId = backStackEntry.arguments?.getInt("tripId") ?: return@composable
                val parentEntry = remember(backStackEntry) {
                    navController.getBackStackEntry("tripDetailGraph/{tripId}")
                }
                val tripViewModel: TripViewModel = viewModel(
                    parentEntry,
                    factory = TripViewModel.provideFactory(appContainer.mainRepository, tripId)
                )
//                TripParticipantsView(navController, tripViewModel, user.toString(), bottomPadding)
                val userId = currentUser?.userId

//                if (userId == null) {
//                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
//                        CircularProgressIndicator()
//                    }
//                } else {
                TripParticipantsView(
                    navController = navController,
                    tripViewModel = tripViewModel,
                    currentUserId = userId,
                    bottomPadding = bottomPadding
                )
//                }
            }

            composable(
                route = "tripDetailGraph/{tripId}/edit",
                arguments = listOf(navArgument("tripId") { type = NavType.IntType })
            ) { backStackEntry ->
                val tripId = backStackEntry.arguments?.getInt("tripId") ?: return@composable
                val parentEntry = remember(backStackEntry) {
                    navController.getBackStackEntry("tripDetailGraph/{tripId}")
                }
                val tripViewModel: TripViewModel = viewModel(
                    parentEntry,
                    factory = TripViewModel.provideFactory(appContainer.mainRepository, tripId)
                )
                EditTravelProposalView(navController, tripViewModel, correctedBottomPadding)
            }
        }
        composable(route = ToGetThereDestinations.FILTERS_ROUTE) {
            if (isLandscape) {
                FilterScreenLandscape(
                    navController,
                    homeFiltersViewModel = homeFiltersViewModel,
                    bottomPadding = bottomPadding
                )
            } else {
                FilterScreen(
                    navController,
                    homeFiltersViewModel = homeFiltersViewModel,
                    bottomPadding = bottomPadding
                )
            }

        }
        composable(route = ToGetThereDestinations.LOGIN_ROUTE) {
            val userProfile by userSessionViewModel.currentUser.collectAsState()
            val authRepository = remember { AuthRepository() }
            val context = LocalContext.current
            val application = context.applicationContext as Application

            val signInViewModel: SignInViewModel = viewModel(
                factory = object : ViewModelProvider.Factory {
                    override fun <T : ViewModel> create(modelClass: Class<T>): T {
                        if (modelClass.isAssignableFrom(SignInViewModel::class.java)) {
                            @Suppress("UNCHECKED_CAST")
                            return SignInViewModel(
                                application,
                                googleAuthUiClient,
                                authRepository,
                                userSessionViewModel
                            ) as T
                        }
                        throw IllegalArgumentException("Unknown ViewModel class")
                    }
                }
            )

            LaunchedEffect(userProfile) {
                if (userProfile != null) {
                    navController.navigate("${ToGetThereDestinations.PROFILE_BASE_ROUTE}/${userProfile!!.userId}") {
                        popUpTo(ToGetThereDestinations.LOGIN_ROUTE) { inclusive = true }
                    }
                }
            }

            LoginScreen(
                isLandscape = isLandscape,
                navController = navController,
                userSessionViewModel = userSessionViewModel,
                signInViewModel = signInViewModel,
                onSignUpClick = { navController.navigate(ToGetThereDestinations.SIGNUP_ROUTE) },
                onCloseClick = { navController.popBackStack() }
            )
        }


        composable(route = ToGetThereDestinations.SIGNUP_ROUTE) {
            var registrationComplete by remember { mutableStateOf(false) }

            SignupFlow(
                navController = navController,
                bottomPadding = correctedBottomPadding,
                isLandscape = isLandscape,
                onSignUpComplete = {
                    registrationComplete = true
                    navController.popBackStack()
                }
            )
            val currentUser by userSessionViewModel.currentUser.collectAsState()

            LaunchedEffect(registrationComplete, currentUser) {
                if (registrationComplete && currentUser != null) {

                    kotlinx.coroutines.delay(100)
                    navController.navigate("${ToGetThereDestinations.PROFILE_BASE_ROUTE}/${currentUser!!.userId}") {
                        popUpTo(ToGetThereDestinations.HOME_ROUTE) {
                            inclusive = false
                        }
                    }
                }
            }
        }
    }
}
package com.example.togetthere.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.compose.rememberNavController
import android.annotation.SuppressLint
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.remember
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.togetthere.GoogleAuthUiClient
import com.example.togetthere.data.AppContainer
import com.example.togetthere.viewmodel.SignInViewModel
import com.example.togetthere.viewmodel.UserSessionViewModel

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun ToGetThereApp(
    appContainer: AppContainer,
    googleAuthUiClient: GoogleAuthUiClient
) {
    val navController = rememberNavController()
    val navigationActions = remember(navController) {
        ToGetThereNavigationActions(navController)
    }
    val userSessionViewModel: UserSessionViewModel = viewModel(
        factory = UserSessionViewModel.provideFactory(appContainer.mainRepository.userProfileRepository)
    )

    /*
    LaunchedEffect(Unit) {
        userSessionViewModel.login(appContainer.currentUser.userId)
    }
    */

    Scaffold(
        bottomBar = {
            BottomNavBar(navController, navigationActions, userSessionViewModel)
        }
    ) { innerPadding ->
        ToGetThereNavGraph(
            appContainer,
            navController/*, modifier = Modifier.padding(it)*/,
            topPadding = innerPadding.calculateTopPadding(),
            bottomPadding = innerPadding.calculateBottomPadding(),
            googleAuthUiClient = googleAuthUiClient,
            userSessionViewModel = userSessionViewModel,
        )
    }
}

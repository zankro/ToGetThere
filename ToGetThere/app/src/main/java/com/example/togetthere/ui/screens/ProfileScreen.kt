package com.example.togetthere.ui.screens

import android.app.Application
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import com.example.togetthere.R
import com.example.togetthere.model.GenderType
import com.example.togetthere.model.UserProfile
import com.example.togetthere.ui.components.NotLoggedInComponent
import com.example.togetthere.ui.components.profile_comps.AboutMe
import com.example.togetthere.ui.components.profile_comps.DreamTrips
import com.example.togetthere.ui.components.profile_comps.Interests
import com.example.togetthere.ui.components.profile_comps.Reviews
import com.example.togetthere.ui.components.profile_comps.Socials
import com.example.togetthere.ui.components.profile_comps.UserReviewDialog
import com.example.togetthere.ui.navigation.ToGetThereDestinations
import com.example.togetthere.utils.FlagUtils
import com.example.togetthere.viewmodel.ProfileUiState
import com.example.togetthere.viewmodel.ProfileViewModel
import com.example.togetthere.viewmodel.SignInViewModel
import com.example.togetthere.viewmodel.UserSessionViewModel

@Composable
fun ProfileScreen(isLandscape: Boolean, navController: NavHostController, sessionVm: UserSessionViewModel, vm: ProfileViewModel, user: UserProfile?, topPadding: Dp, bottomPadding: Dp, highlightReviewId: Int? = null, signInViewModel: SignInViewModel) {
    if(user == null){
        NotLoggedInComponent(isLandscape, navController, bottomPadding)
        return
    }

    LaunchedEffect(user.userId) {
        vm.loadUser()
    }

    //ProfileView(navController, sessionVm, vm, isLandscape, user.userId, topPadding, bottomPadding, highlightReviewId)
    when (vm.uiState) {
        is ProfileUiState.Loading -> {
            // Mostra un indicatore di caricamento
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }
        is ProfileUiState.Error -> {
             NotLoggedInComponent(isLandscape, navController, bottomPadding)
                return
        }
        is ProfileUiState.Success -> {
            ProfileView(
                navController,
                sessionVm,
                vm,
                isLandscape,
                user.userId,
                topPadding,
                bottomPadding,
                highlightReviewId,
                signInViewModel
            )
        }
    }
}


@Composable
fun ProfileView(
    navController: NavHostController,
    sessionVm: UserSessionViewModel,
    vm: ProfileViewModel,
    isLandscape: Boolean,
    currentUserId: String,
    topPadding: Dp,
    bottomPadding: Dp,
    highlightReviewId: Int? = null,
    signInViewModel: SignInViewModel
) {

    if(!isLandscape) {
        Box(modifier = Modifier.fillMaxSize()) {
            if (vm.userPhoto != null) {
                Image(
                    painter = if (!vm.userPhoto.isNullOrBlank()) {
                        rememberAsyncImagePainter(vm.userPhoto)
                    } else {
                        when (vm.userGender) {
                            GenderType.MALE -> painterResource(R.drawable.copertina_profilo_m_default)
                            GenderType.FEMALE -> painterResource(R.drawable.copertina_profilo_f_default)
                            else -> painterResource(R.drawable.copertina_profilo_neutro_default)
                        }
                    },
                    contentDescription = "Profile image",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(350.dp),
                    contentScale = ContentScale.Crop
                )
            } else {
                Box(Modifier.height(350.dp)) {
                    Image(
                        painter = painterResource(id = if (vm.userGender == GenderType.MALE) R.drawable.copertina_profilo_m_default else R.drawable.copertina_profilo_f_default),
                        contentDescription = "Default image",
                        modifier = Modifier
                            .fillMaxWidth()
                            .fillMaxHeight(),
                        contentScale = ContentScale.Crop
                    )
                }
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(195.dp))
            ProfileName(
                vm.userName,
                vm.userSurname,
                vm.userNickname,
                vm.userNationality,
                isLandscape
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .wrapContentHeight()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .offset(y = 25.dp)
                        .clip(RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp))
                        .background(color = MaterialTheme.colorScheme.background)
                        //.padding(bottom = 45.dp)
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacer(modifier = Modifier.height(20.dp))
                    AboutMe(vm.userDescription)
                    Spacer(modifier = Modifier.height(20.dp))
                    if(vm.userInterests.isNotEmpty())
                        Interests(vm.userInterests)
                    Spacer(modifier = Modifier.height(20.dp))
                    if(vm.userDreamTrips.isNotEmpty())
                        DreamTrips(vm.userDreamTrips)
                    Spacer(modifier = Modifier.height(20.dp))
                    if(vm.userReviewsWithAuthor.isNotEmpty())
                        Reviews(vm.userReviewsWithAuthor, navController, highlightReviewId)
                    Spacer(modifier = Modifier.height(20.dp))
                    if(vm.userSocials.isNotEmpty())
                        Socials(vm.userSocials)
                    Spacer(modifier = Modifier.height(bottomPadding))
                }

            }
        }
        Row(
            Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(16.dp),
            horizontalArrangement = Arrangement.End
        ) {
            MoreOptionsFab(navController, sessionVm, vm, currentUserId, topPadding, signInViewModel)
        }
    }

    else {
        val scrollState = rememberScrollState()

        Row(
            Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.surface)
        ) {
            Box(
                Modifier
                    .fillMaxHeight()
                    .weight(1f)
            ) {
                if (vm.userPhoto != null) {
                    Image(
                        painter = if (!vm.userPhoto.isNullOrBlank()) {
                            rememberAsyncImagePainter(vm.userPhoto)
                        } else {
                            when (vm.userGender) {
                                GenderType.MALE -> painterResource(R.drawable.copertina_profilo_m_default)
                                GenderType.FEMALE -> painterResource(R.drawable.copertina_profilo_f_default)
                                else -> painterResource(R.drawable.copertina_profilo_neutro_default)
                            }
                        },
                        contentDescription = "Profile image",
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(start = 10.dp, end = 10.dp, top = topPadding+10.dp, bottom = bottomPadding+10.dp)
                            .clip(RoundedCornerShape(10.dp)),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Image(
                        painter = painterResource(
                            id = if (vm.userGender == GenderType.MALE)
                                R.drawable.copertina_profilo_m_default
                            else
                                R.drawable.copertina_profilo_f_default
                        ),
                        contentDescription = "Default image",
                        modifier = Modifier
                            .fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }
            }

            Column(
                Modifier
                    .fillMaxHeight()
                    .weight(2f)
                    .verticalScroll(scrollState)
                    .padding(start = 16.dp, end = 16.dp, top = topPadding, bottom = bottomPadding),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(16.dp))

                ProfileName(vm.userName, vm.userSurname, vm.userNickname, vm.userNationality,  true)

                // Contenuto principale
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .wrapContentHeight()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(color = MaterialTheme.colorScheme.background)
                            .padding(vertical = 16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        AboutMe(vm.userDescription)
                        Interests(vm.userInterests)
//                DreamTrips(userProfile)
                        Spacer(modifier = Modifier.height(20.dp))
                        Reviews(vm.userReviewsWithAuthor, navController)
                        Spacer(modifier = Modifier.height(20.dp))
                        Socials(vm.userSocials)
                    }
                }

                Spacer(Modifier.height(16.dp))
            }
        }

        // Pulsanti in alto
        Row(
            Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(16.dp)
                .padding(end = 35.dp),
            horizontalArrangement = Arrangement.End
        ) {
            MoreOptionsFab(navController, sessionVm, vm, currentUserId, topPadding, signInViewModel)
        }
    }

}


@Composable
fun ProfileName(name: String, surname: String, nickname: String, nationality: String, isLandscape: Boolean) {
    var color = Color.White
    var colorShadow = Color.Black
    if (isLandscape) {
        color = MaterialTheme.colorScheme.onSurface
        colorShadow = MaterialTheme.colorScheme.surfaceDim
    }
    Text(
        text = "${name} ${surname} ",
        style = MaterialTheme.typography.titleLarge.copy(
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            color = color,
            shadow = Shadow(
                color = colorShadow.copy(alpha = 0.8f),
                offset = Offset(4f, 4f),
                blurRadius = 8f
            )
        ),
    )
    Text(
        text = "@${nickname} ${FlagUtils.getFlagEmoji(nationality)}",
        style = MaterialTheme.typography.titleMedium.copy(
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = color,
            shadow = Shadow(
                color = colorShadow.copy(alpha = 0.8f),
                offset = Offset(4f, 4f),
                blurRadius = 8f
            )
        ),
    )
}

@Composable
fun MoreOptionsFab(navController: NavController, sessionVm: UserSessionViewModel, vm: ProfileViewModel, currentUserId: String, topPadding: Dp, signInViewModel: SignInViewModel) {
    var expanded by rememberSaveable { mutableStateOf(false) }

    Box {
        FloatingActionButton(
            onClick = { expanded = true},
            modifier = Modifier.padding(top = topPadding).size(40.dp),
            containerColor = MaterialTheme.colorScheme.primary,
            shape = CircleShape
        ) {
            Icon(
                imageVector = Icons.Default.MoreVert,
                contentDescription = "More options"
            )
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
        ) {
            if (sessionVm.currentUser.collectAsState().value?.userId == vm.id) {
                DropdownMenuItem(
                    text = { Text("Edit profile") },
                    onClick = {
                        expanded = false
                        navController.navigate(ToGetThereDestinations.editProfileRoute(vm.id))
                    },
                    leadingIcon = {
                        Icon(Icons.Default.Edit, contentDescription = "edit_icon")
                    }
                )
                DropdownMenuItem(
                    text = { Text("Logout") },
                    onClick = {
                        expanded = false
                        sessionVm.logout()
                        signInViewModel.signOut()
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Logout,
                            contentDescription = "logout_icon"
                        )
                    }
                )
            }
            else{
                DropdownMenuItem(
                    text = { Text("Review ${vm.userName}") },
                    onClick = {
                        expanded = false
                        vm.showReviewDialog()
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "review_icon"
                        )
                    }
                )
            }
        }
    }

    if (vm.isReviewDialogVisible) {
        UserReviewDialog(
            isOpen = vm.isReviewDialogVisible,
            reviewedUser = vm.id,
            reviewerUser = currentUserId,
            savedDescription = vm.reviewDialogDescription,
            onDismiss = { vm.showReviewDialog() },
            onSubmitReview = { description, reviewed, reviewer ->
                vm.addReview(
                    reviewedUserId = reviewed,
                    reviewerUserId = reviewer,
                    description = description
                )
            },
            onDescriptionChange = { vm.updateReviewDialogDescription(it) }
        )
    }
}
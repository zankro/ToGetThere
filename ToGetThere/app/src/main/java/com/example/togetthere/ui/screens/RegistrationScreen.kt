package com.example.togetthere.ui.screens

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Camera
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.example.togetthere.CameraXActivity
import com.example.togetthere.R
import com.example.togetthere.firebase.registerUser
import com.example.togetthere.model.Destination
import com.example.togetthere.model.GenderType
import com.example.togetthere.model.UserProfile

// Enum for signup steps
enum class SignupStep {
    INTERESTS, BASIC_INFO, PROFILE_PHOTO, ABOUT_ME, SOCIAL_MEDIA, DESIRED_DESTINATIONS, CREDENTIALS
}

// Data classes
data class UserSignupData(
    val interests: List<String> = emptyList(),
    val firstName: String = "",
    val lastName: String = "",
    val nickname: String = "",
    val nationality: String = "",
    val profilePhotoUri: String? = null,
    val aboutMe: String = "",
    val facebookHandle: String = "",
    val instagramHandle: String = "",
    val desiredDestinations: List<DesiredDestination> = emptyList(),
    val email: String = "",
    val confirmEmail: String = "",
    val password: String = ""
)

fun UserSignupData.toUserProfileForRegistration(): UserProfile {
//    val parsedPhoto: UserPhoto? = profilePhotoUri?.let {
//        try {
//            UserPhoto.UriPhoto(Uri.parse(it))
//        } catch (e: Exception) {
//            null
//        }
//    }

    return UserProfile(
        userId = "", // Sarà riempito da Firebase
        name = firstName,
        surname = lastName,
        nickname = nickname,
        gender = GenderType.OTHER,
        nationality = nationality,
        description = aboutMe,
        photo = profilePhotoUri,
        interests = interests,
        desiredDestinations = desiredDestinations.map { it.toModel() },
        socials = emptyList()
    )
}

data class DesiredDestination(
    val name: String,
    val imageURL: String,
)

fun DesiredDestination.toModel(): Destination {
    return Destination(name, imageURL.toString())
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignupFlow(navController: NavHostController, bottomPadding: Dp, isLandscape: Boolean, onSignUpComplete: () -> Unit = {}) {
    var currentStep by remember { mutableStateOf(SignupStep.INTERESTS) }
    var signupData by remember { mutableStateOf(UserSignupData()) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Create Your Profile") },
                navigationIcon = {
                    IconButton(onClick = {
                        navController.navigateUp()
                    }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .padding(bottom = bottomPadding)
        ) {
            // Step indicator
            SignupStepIndicator(currentStep)
            Spacer(modifier = Modifier.height(24.dp))

            // Content based on current step
            Box(modifier = Modifier.weight(1f)) {
                when (currentStep) {
                    SignupStep.INTERESTS -> InterestsStep(
                        signupData = signupData,
                        onDataChange = { signupData = it },
                        onNext = { currentStep = SignupStep.BASIC_INFO },
                        isLandscape = isLandscape
                    )

                    SignupStep.BASIC_INFO -> BasicInfoStep(
                        signupData = signupData,
                        onDataChange = { signupData = it },
                        onNext = { currentStep = SignupStep.PROFILE_PHOTO },
                        onBack = { currentStep = SignupStep.INTERESTS },
                        isLandscape = isLandscape
                    )

                    SignupStep.PROFILE_PHOTO -> ProfilePhotoStep(
                        signupData = signupData,
                        onDataChange = { signupData = it },
                        onNext = { currentStep = SignupStep.ABOUT_ME },
                        onBack = { currentStep = SignupStep.BASIC_INFO },
                        isLandscape = isLandscape
                    )

                    SignupStep.ABOUT_ME -> AboutMeStep(
                        signupData = signupData,
                        onDataChange = { signupData = it },
                        onNext = { currentStep = SignupStep.SOCIAL_MEDIA },
                        onBack = { currentStep = SignupStep.PROFILE_PHOTO },
                        isLandscape = isLandscape
                    )

                    SignupStep.SOCIAL_MEDIA -> SocialMediaStep(
                        signupData = signupData,
                        onDataChange = { signupData = it },
                        onNext = { currentStep = SignupStep.DESIRED_DESTINATIONS },
                        onBack = { currentStep = SignupStep.ABOUT_ME },
                        isLandscape = isLandscape
                    )

                    SignupStep.DESIRED_DESTINATIONS -> DesiredDestinationsStep(
                        signupData = signupData,
                        onDataChange = { signupData = it },
                        onNext = { currentStep = SignupStep.CREDENTIALS },
                        onBack = { currentStep = SignupStep.SOCIAL_MEDIA },
                        isLandscape = isLandscape
                    )

                    SignupStep.CREDENTIALS -> CredentialsStep(
                        signupData = signupData,
                        onDataChange = { signupData = it },
                        onComplete = {
                            // Converti UserSignupData in UserProfile
                            val userProfile = signupData.toUserProfileForRegistration()

                            // Converti la stringa URI in Uri object (se presente)
                            val imageUri = signupData.profilePhotoUri?.let { uriString ->
                                try {
                                    android.net.Uri.parse(uriString)
                                } catch (e: Exception) {
                                    null
                                }
                            }

                            registerUser(
                                email = signupData.email,
                                password = signupData.password,
                                userProfile = userProfile,
                                imageUri = imageUri, // Ora passa il Uri object corretto
                                onSuccess = { finalUserProfile ->
                                    Log.d("Signup", "User registered successfully with ID: ${finalUserProfile.userId}")
                                    onSignUpComplete()
                                    navController.navigateUp()
                                },
                                onFailure = { exception ->
                                    Log.e("Signup", "Registration failed: ${exception.message}")
                                }
                            )
                        },
                        onBack = { currentStep = SignupStep.DESIRED_DESTINATIONS },
                        isLandscape = isLandscape
                    )
                }
            }
        }
    }
}

@Composable
fun SignupStepIndicator(currentStep: SignupStep) {
    val steps = SignupStep.entries.toTypedArray()
    val currentIndex = steps.indexOf(currentStep)

    Row (
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        steps.forEachIndexed { index, _ ->
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(4.dp)
                    .background(
                        color = if (index <= currentIndex) MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.outline,
                        shape = RoundedCornerShape(2.dp)
                    )
            )
        }
    }
}

// STEP 1: INTERESTS
@Composable
fun InterestsStep(
    signupData: UserSignupData,
    onDataChange: (UserSignupData) -> Unit,
    onNext: () -> Unit,
    isLandscape: Boolean
) {
    val interestEmojis = mapOf(
        "Relax" to "🧶",
        "Backpack" to "🎒",
        "Adventure" to "🪂",
        "Interrail" to "🚅",
        "Nature" to "🌿",
        "Art" to "🎭",
        "Culinary" to "🍽",
        "Photo" to "📸",
        "Hiking" to "🦾",
        "Culture" to "🎭",
        "Tech" to "💻",
        "Blogging" to "✍️",
        "On Road" to "🚐",
        "History" to "🏛️",
        "Museums" to "🏛️",
        "Camping" to "🔥",
        "Urban" to "🏙️",
        "Surf" to "🏄‍♂️"
    )

    val interestsList = interestEmojis.keys.toList()

    if (isLandscape) {
        // Layout orizzontale con descrizione e bottoni a sinistra, interessi a destra
        Row(
            modifier = Modifier.fillMaxSize()
        ) {
            // Colonna sinistra - Header, descrizione e bottone
            Column(
                modifier = Modifier
                    .weight(0.4f)
                    .padding(end = 16.dp)
            ) {
                Text(
                    text = "What are your interests?",
                    style = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                Text(
                    text = "Choose up to 6 interests that best describe you",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                // Bottoni di navigazione
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = onNext,
                        enabled = signupData.interests.isNotEmpty(),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Continue")
                    }

                }
            }

            // Colonna destra - Grid degli interessi
            LazyVerticalGrid(
                columns = GridCells.Fixed(6),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.weight(0.6f)
            ) {
                items(interestsList.size) { index ->
                    val interest = interestsList[index]
                    val isSelected = signupData.interests.contains(interest)
                    val canSelect = signupData.interests.size < 6 || isSelected

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable(enabled = canSelect) {
                                val newInterests = if (isSelected) {
                                    signupData.interests - interest
                                } else {
                                    signupData.interests + interest
                                }
                                onDataChange(signupData.copy(interests = newInterests))
                            },
                        colors = CardDefaults.cardColors(
                            containerColor = if (isSelected)
                                MaterialTheme.colorScheme.primaryContainer
                            else MaterialTheme.colorScheme.surface
                        ),
                        border = if (isSelected)
                            BorderStroke(2.dp, MaterialTheme.colorScheme.primary)
                        else null
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = interestEmojis[interest] ?: "",
                                style = MaterialTheme.typography.headlineSmall,
                                modifier = Modifier.padding(bottom = 4.dp)
                            )
                            Text(
                                text = interest,
                                style = MaterialTheme.typography.bodySmall,
                                textAlign = TextAlign.Center,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
            }
        }
    } else {
        // Layout verticale (portrait)
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            Text(
                text = "What are your interests?",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Text(
                text = "Choose up to 6 interests that best describe you",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = 24.dp)
            )

            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(interestsList.size) { index ->
                    val interest = interestsList[index]
                    val isSelected = signupData.interests.contains(interest)
                    val canSelect = signupData.interests.size < 6 || isSelected

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable(enabled = canSelect) {
                                val newInterests = if (isSelected) {
                                    signupData.interests - interest
                                } else {
                                    signupData.interests + interest
                                }
                                onDataChange(signupData.copy(interests = newInterests))
                            },
                        colors = CardDefaults.cardColors(
                            containerColor = if (isSelected)
                                MaterialTheme.colorScheme.primaryContainer
                            else MaterialTheme.colorScheme.surface
                        ),
                        border = if (isSelected)
                            BorderStroke(2.dp, MaterialTheme.colorScheme.primary)
                        else null
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = interestEmojis[interest] ?: "",
                                style = MaterialTheme.typography.headlineLarge,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )
                            Text(
                                text = interest,
                                style = MaterialTheme.typography.bodyMedium,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }

            Button(
                onClick = onNext,
                enabled = signupData.interests.isNotEmpty(),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Continue")
            }
        }
    }
}


// STEP 2: BASIC INFO
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BasicInfoStep(
    signupData: UserSignupData,
    onDataChange: (UserSignupData) -> Unit,
    onNext: () -> Unit,
    onBack: () -> Unit,
    isLandscape: Boolean
) {
    var expanded by remember { mutableStateOf(false) }

    val countries = listOf(
        "Italy", "United States", "United Kingdom", "Germany", "France",
        "Spain", "Canada", "Australia", "Japan", "Brazil", "Mexico",
        "Netherlands", "Sweden", "Norway", "Denmark", "Switzerland",
        "Austria", "Belgium", "Portugal", "Greece", "Turkey", "Russia",
        "China", "India", "South Korea", "Thailand", "Vietnam", "Indonesia"
    ).sorted()

    if (isLandscape) {
        // Layout orizzontale: due colonne
        Row(
            modifier = Modifier.fillMaxSize()
        ) {
            // Colonna sinistra con titolo e form
            Column(
                modifier = Modifier
                    .weight(0.6f)
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
            ) {
                Text(
                    text = "Basic Information",
                    style = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                OutlinedTextField(
                    value = signupData.firstName,
                    onValueChange = { onDataChange(signupData.copy(firstName = it)) },
                    label = { Text("First Name") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp)
                )

                OutlinedTextField(
                    value = signupData.lastName,
                    onValueChange = { onDataChange(signupData.copy(lastName = it)) },
                    label = { Text("Last Name") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp)
                )

                OutlinedTextField(
                    value = signupData.nickname,
                    onValueChange = { onDataChange(signupData.copy(nickname = it)) },
                    label = { Text("Nickname") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp)
                )

                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = signupData.nationality,
                        onValueChange = { },
                        readOnly = true,
                        label = { Text("Nationality") },
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor()
                    )

                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        countries.forEach { country ->
                            DropdownMenuItem(
                                text = { Text(country) },
                                onClick = {
                                    onDataChange(signupData.copy(nationality = country))
                                    expanded = false
                                }
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.width(24.dp))

            // Colonna destra con i pulsanti
            Column(
                modifier = Modifier
                    //.width(200.dp)
                    .weight(0.4f)
                    .fillMaxSize(),
                verticalArrangement = Arrangement.Bottom
            ) {
                OutlinedButton(
                    onClick = onBack,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp)
                ) {
                    Text("Back")
                }

                Button(
                    onClick = onNext,
                    enabled = signupData.firstName.isNotBlank() &&
                            signupData.lastName.isNotBlank() &&
                            signupData.nickname.isNotBlank(),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Continue")
                }
            }
        }
    } else {
        // Layout verticale originale
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            Text(
                text = "Basic Information",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(bottom = 24.dp)
            )

            OutlinedTextField(
                value = signupData.firstName,
                onValueChange = { onDataChange(signupData.copy(firstName = it)) },
                label = { Text("First Name") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            )

            OutlinedTextField(
                value = signupData.lastName,
                onValueChange = { onDataChange(signupData.copy(lastName = it)) },
                label = { Text("Last Name") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            )

            OutlinedTextField(
                value = signupData.nickname,
                onValueChange = { onDataChange(signupData.copy(nickname = it)) },
                label = { Text("Nickname") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            )

            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded },
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = signupData.nationality,
                    onValueChange = { },
                    readOnly = true,
                    label = { Text("Nationality") },
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor()
                )

                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    countries.forEach { country ->
                        DropdownMenuItem(
                            text = { Text(country) },
                            onClick = {
                                onDataChange(signupData.copy(nationality = country))
                                expanded = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OutlinedButton(
                    onClick = onBack,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Back")
                }

                Button(
                    onClick = onNext,
                    enabled = signupData.firstName.isNotBlank() &&
                            signupData.lastName.isNotBlank() &&
                            signupData.nickname.isNotBlank(),
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Continue")
                }
            }
        }
    }
}

/// STEP 4: PROFILE PHOTO
@Composable
fun ProfilePhotoStep(
    signupData: UserSignupData,
    onDataChange: (UserSignupData) -> Unit,
    onNext: () -> Unit,
    onBack: () -> Unit,
    isLandscape: Boolean
) {
    var showMenu by remember { mutableStateOf(false) }
    val context = LocalContext.current

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val imageUri = result.data?.getStringExtra("image_uri")?.let { android.net.Uri.parse(it) }
            if (imageUri != null) {
                onDataChange(signupData.copy(profilePhotoUri = imageUri.toString()))
            }
            Toast.makeText(context, "Photo captured successfully", Toast.LENGTH_SHORT).show()
        }
    }

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: android.net.Uri? ->
        uri?.let {
            onDataChange(signupData.copy(profilePhotoUri = it.toString()))
        }
    }

    if (isLandscape) {
        // Layout orizzontale: contenuto affiancato
        Row(
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Sezione sinistra: testo e foto
            Column(
                modifier = Modifier
                    .weight(0.6f)
                    .padding(end = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Add a profile photo",
                    style = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                Text(
                    text = "Choose a photo that represents you",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(bottom = 24.dp),
                    textAlign = TextAlign.Center
                )

                Box(
                    modifier = Modifier.size(160.dp), // Ridotto per landscape
                    contentAlignment = Alignment.Center
                ) {
                    if (signupData.profilePhotoUri != null) {
                        AsyncImage(
                            model = signupData.profilePhotoUri,
                            contentDescription = "Profile Photo",
                            modifier = Modifier
                                .size(160.dp)
                                .clip(CircleShape),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Box(
                            modifier = Modifier
                                .size(160.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.surfaceVariant),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = null,
                                modifier = Modifier.size(64.dp),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    // Photo menu button
                    Box(
                        modifier = Modifier.align(Alignment.BottomEnd)
                    ) {
                        FloatingActionButton(
                            onClick = { showMenu = !showMenu },
                            containerColor = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(48.dp) // Ridotto per landscape
                        ) {
                            Icon(
                                imageVector = Icons.Default.CameraAlt,
                                contentDescription = "Add Photo",
                                modifier = Modifier.size(24.dp)
                            )
                        }

                        DropdownMenu(
                            expanded = showMenu,
                            onDismissRequest = { showMenu = false },
                            offset = DpOffset(x = (-120).dp, y = 0.dp)
                        ) {
                            DropdownMenuItem(
                                text = { Text("Gallery") },
                                leadingIcon = {
                                    Icon(Icons.Filled.PhotoLibrary, contentDescription = "gallery_icon")
                                },
                                onClick = {
                                    showMenu = false
                                    galleryLauncher.launch("image/*")
                                }
                            )

                            DropdownMenuItem(
                                text = { Text("Camera") },
                                leadingIcon = {
                                    Icon(Icons.Filled.Camera, contentDescription = "camera_icon")
                                },
                                onClick = {
                                    showMenu = false
                                    val intent = Intent(context, CameraXActivity::class.java)
                                    cameraLauncher.launch(intent)
                                }
                            )
                        }
                    }
                }
            }

            // Sezione destra: pulsanti
            Column(
                modifier = Modifier.width(200.dp)
                    .weight(0.4f),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = onBack,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Back")
                }

                Button(
                    onClick = onNext,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Continue")
                }
            }
        }
    } else {
        // Layout verticale (portrait): layout originale
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Add a profile photo",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Text(
                text = "Choose a photo that represents you",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = 32.dp)
            )

            Box(
                modifier = Modifier.size(200.dp),
                contentAlignment = Alignment.Center
            ) {
                if (signupData.profilePhotoUri != null) {
                    AsyncImage(
                        model = signupData.profilePhotoUri,
                        contentDescription = "Profile Photo",
                        modifier = Modifier
                            .size(200.dp)
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .size(200.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.surfaceVariant),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = null,
                            modifier = Modifier.size(80.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // Photo menu button
                Box(
                    modifier = Modifier.align(Alignment.BottomEnd)
                ) {
                    FloatingActionButton(
                        onClick = { showMenu = !showMenu },
                        containerColor = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(56.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.CameraAlt,
                            contentDescription = "Add Photo"
                        )
                    }

                    DropdownMenu(
                        expanded = showMenu,
                        onDismissRequest = { showMenu = false },
                        offset = DpOffset(x = (-120).dp, y = 0.dp)
                    ) {
                        DropdownMenuItem(
                            text = { Text("Gallery") },
                            leadingIcon = {
                                Icon(Icons.Filled.PhotoLibrary, contentDescription = "gallery_icon")
                            },
                            onClick = {
                                showMenu = false
                                galleryLauncher.launch("image/*")
                            }
                        )

                        DropdownMenuItem(
                            text = { Text("Camera") },
                            leadingIcon = {
                                Icon(Icons.Filled.Camera, contentDescription = "camera_icon")
                            },
                            onClick = {
                                showMenu = false
                                val intent = Intent(context, CameraXActivity::class.java)
                                cameraLauncher.launch(intent)
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OutlinedButton(
                    onClick = onBack,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Back")
                }

                Button(
                    onClick = onNext,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Continue")
                }
            }
        }
    }
}

// STEP 5: ABOUT ME
@Composable
fun AboutMeStep(
    signupData: UserSignupData,
    onDataChange: (UserSignupData) -> Unit,
    onNext: () -> Unit,
    onBack: () -> Unit,
    isLandscape: Boolean
) {
    if (isLandscape) {
        // Layout orizzontale: contenuto affiancato
        Row(
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Sezione sinistra: testo e campo di input
            Column(
                modifier = Modifier
                    .weight(0.6f)
                    .padding(end = 24.dp)
            ) {
                Text(
                    text = "Tell us about yourself",
                    style = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                Text(
                    text = "Share what makes you unique and what you're passionate about",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(bottom = 20.dp)
                )

                OutlinedTextField(
                    value = signupData.aboutMe,
                    onValueChange = { onDataChange(signupData.copy(aboutMe = it)) },
                    label = { Text("About Me") },
                    placeholder = { Text("Write a brief description about yourself...") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(140.dp), // Ridotto per landscape
                    maxLines = 6
                )

                Text(
                    text = "${signupData.aboutMe.length}/500",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    textAlign = TextAlign.End
                )
            }

            // Sezione destra: pulsanti
            Column(
                modifier = Modifier.width(200.dp)
                    .weight(0.4f),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = onBack,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Back")
                }

                Button(
                    onClick = onNext,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Continue")
                }
            }
        }
    } else {
        // Layout verticale (portrait): layout originale
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            Text(
                text = "Tell us about yourself",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Text(
                text = "Share what makes you unique and what you're passionate about",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = 24.dp)
            )

            OutlinedTextField(
                value = signupData.aboutMe,
                onValueChange = { onDataChange(signupData.copy(aboutMe = it)) },
                label = { Text("About Me") },
                placeholder = { Text("Write a brief description about yourself...") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                maxLines = 8
            )

            Text(
                text = "${signupData.aboutMe.length}/500",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                textAlign = TextAlign.End
            )

            Spacer(modifier = Modifier.weight(1f))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OutlinedButton(
                    onClick = onBack,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Back")
                }

                Button(
                    onClick = onNext,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Continue")
                }
            }
        }
    }
}

// STEP 6: SOCIAL MEDIA
@Composable
fun SocialMediaStep(
    signupData: UserSignupData,
    onDataChange: (UserSignupData) -> Unit,
    onNext: () -> Unit,
    onBack: () -> Unit,
    isLandscape: Boolean
) {
    if (isLandscape) {
        // Layout orizzontale con contenuto in due colonne
        Row(
            modifier = Modifier.fillMaxSize()
        ) {
            // Colonna sinistra - Header e descrizione
            Column(
                modifier = Modifier
                    .weight(0.4f)
                    .padding(end = 16.dp)
            ) {
                Text(
                    text = "Connect your social media",
                    style = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                Text(
                    text = "Optional: Add your social media handles to connect with other travelers",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(bottom = 24.dp)
                )
                // Bottoni di navigazione
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = onBack,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Back")
                    }

                    Button(
                        onClick = onNext,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Continue")
                    }
                }
            }

            // Colonna destra - Form e bottoni
            Column(
                modifier = Modifier
                    .weight(0.6f)
                    .fillMaxHeight()
            ) {
                OutlinedTextField(
                    value = signupData.facebookHandle,
                    onValueChange = { onDataChange(signupData.copy(facebookHandle = it)) },
                    label = { Text("Facebook") },
                    placeholder = { Text("@username") },
                    leadingIcon = {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_facebook),
                            contentDescription = "Facebook"
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                )

                OutlinedTextField(
                    value = signupData.instagramHandle,
                    onValueChange = { onDataChange(signupData.copy(instagramHandle = it)) },
                    label = { Text("Instagram") },
                    placeholder = { Text("@username") },
                    leadingIcon = {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_instagram),
                            contentDescription = "Instagram"
                        )
                    },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.weight(1f))
            }
        }
    } else {
        // Layout verticale (portrait)
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            Text(
                text = "Connect your social media",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Text(
                text = "Optional: Add your social media handles to connect with other travelers",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = 24.dp)
            )

            OutlinedTextField(
                value = signupData.facebookHandle,
                onValueChange = { onDataChange(signupData.copy(facebookHandle = it)) },
                label = { Text("Facebook") },
                placeholder = { Text("@username") },
                leadingIcon = {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_facebook),
                        contentDescription = "Facebook"
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            )

            OutlinedTextField(
                value = signupData.instagramHandle,
                onValueChange = { onDataChange(signupData.copy(instagramHandle = it)) },
                label = { Text("Instagram") },
                placeholder = { Text("@username") },
                leadingIcon = {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_instagram),
                        contentDescription = "Instagram"
                    )
                },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.weight(1f))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OutlinedButton(
                    onClick = onBack,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Back")
                }

                Button(
                    onClick = onNext,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Continue")
                }
            }
        }
    }
}

// STEP 7: DESIRED DESTINATIONS
@Composable
fun DesiredDestinationsStep(
    signupData: UserSignupData,
    onDataChange: (UserSignupData) -> Unit,
    onNext: () -> Unit,
    onBack: () -> Unit,
    isLandscape: Boolean
) {
    var showAddDialog by remember { mutableStateOf(false) }

    if (isLandscape) {
        // Layout orizzontale
        Row(
            modifier = Modifier.fillMaxSize()
        ) {
            // Colonna sinistra - Header
            Column(
                modifier = Modifier
                    .weight(0.4f)
                    .padding(end = 16.dp)
            ) {
                Text(
                    text = "Dream destinations",
                    style = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                Text(
                    text = "Add places you'd love to visit",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(bottom = 24.dp)
                )

                // Bottoni di navigazione in landscape
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = onBack,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Back")
                    }

                    Button(
                        onClick = onNext,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Continue")
                    }
                }
            }

            // Colonna destra - Lista destinazioni
            Column(
                modifier = Modifier.weight(0.6f)
            ) {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(signupData.desiredDestinations.size) { index ->
                        val destination = signupData.desiredDestinations[index]
                        DestinationCard(
                            destination = destination,
                            onRemove = {
                                val newDestinations = signupData.desiredDestinations - destination
                                onDataChange(signupData.copy(desiredDestinations = newDestinations))
                            }
                        )
                    }

                    item {
                        OutlinedButton(
                            onClick = { showAddDialog = true },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = null,
                                modifier = Modifier.padding(end = 8.dp)
                            )
                            Text("Add Destination")
                        }
                    }
                }
            }
        }
    } else {
        // Layout verticale (portrait)
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            Text(
                text = "Dream destinations",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Text(
                text = "Add places you'd love to visit",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = 24.dp)
            )

            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(signupData.desiredDestinations.size) { index ->
                    val destination = signupData.desiredDestinations[index]
                    DestinationCard(
                        destination = destination,
                        onRemove = {
                            val newDestinations = signupData.desiredDestinations - destination
                            onDataChange(signupData.copy(desiredDestinations = newDestinations))
                        }
                    )
                }

                item {
                    OutlinedButton(
                        onClick = { showAddDialog = true },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = null,
                            modifier = Modifier.padding(end = 8.dp)
                        )
                        Text("Add Destination")
                    }
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OutlinedButton(
                    onClick = onBack,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Back")
                }

                Button(
                    onClick = onNext,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Continue")
                }
            }
        }
    }

    if (showAddDialog) {
        AddDestinationDialog(
            onDismiss = { showAddDialog = false },
            onAdd = { destination ->
                val newDestinations = signupData.desiredDestinations + destination
                onDataChange(signupData.copy(desiredDestinations = newDestinations))
                showAddDialog = false
            }
        )
    }
}

@Composable
fun DestinationCard(
    destination: DesiredDestination,
    onRemove: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (destination.imageURL != null) {
                AsyncImage(
                    model = destination.imageURL,
                    contentDescription = destination.name,
                    modifier = Modifier
                        .size(60.dp)
                        .clip(RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Crop
                )
            } else {
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Text(
                text = destination.name,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 16.dp)
            )

            IconButton(onClick = onRemove) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Remove",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

@Composable
fun AddDestinationDialog(
    onDismiss: () -> Unit,
    onAdd: (DesiredDestination) -> Unit
) {
    var destinationName by remember { mutableStateOf("") }
    var selectedImageUri by remember { mutableStateOf<String?>(null) }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        selectedImageUri = uri?.toString()
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Destination") },
        text = {
            Column {
                OutlinedTextField(
                    value = destinationName,
                    onValueChange = { destinationName = it },
                    label = { Text("Destination Name") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                )

                OutlinedButton(
                    onClick = { launcher.launch("image/*") },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        imageVector = Icons.Default.Image,
                        contentDescription = null,
                        modifier = Modifier.padding(end = 8.dp)
                    )
                    Text(if (selectedImageUri != null) "Change Image" else "Add Image")
                }

                selectedImageUri?.let { uri ->
                    AsyncImage(
                        model = uri,
                        contentDescription = "Selected image",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp)
                            .padding(top = 8.dp)
                            .clip(RoundedCornerShape(8.dp)),
                        contentScale = ContentScale.Crop
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (destinationName.isNotBlank() && selectedImageUri != null) {
                        val parsedUri = Uri.parse(selectedImageUri)
                        onAdd(DesiredDestination(destinationName, parsedUri.toString()))
                    }
                },
                enabled = destinationName.isNotBlank()
            ) {
                Text("Add")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}


// STEP 8: CREDENTIALS
@Composable
fun CredentialsStep(
    signupData: UserSignupData,
    onDataChange: (UserSignupData) -> Unit,
    onComplete: () -> Unit,
    onBack: () -> Unit,
    isLandscape: Boolean
) {
    var passwordVisible by remember { mutableStateOf(false) }

    // Validation states
    val isEmailValid = android.util.Patterns.EMAIL_ADDRESS.matcher(signupData.email).matches()
    val doEmailsMatch = signupData.email == signupData.confirmEmail && signupData.confirmEmail.isNotBlank()
    val isPasswordValid = signupData.password.length >= 8
    val isFormValid = isEmailValid && doEmailsMatch && isPasswordValid

    if (isLandscape) {
        // Layout orizzontale con form più compatto
        Row(
            modifier = Modifier.fillMaxSize()
        ) {
            // Colonna sinistra - Header
            Column(
                modifier = Modifier
                    .weight(0.4f)
                    .padding(end = 16.dp)
            ) {
                Text(
                    text = "Account credentials",
                    style = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                Text(
                    text = "Create your account with email and password",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(bottom = 24.dp)
                )

                // Bottoni di navigazione
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = onBack,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Back")
                    }

                    Button(
                        onClick = onComplete,
                        enabled = isFormValid,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Complete")
                    }
                }
            }

            // Colonna destra - Form campi con scrolling
            Column(
                modifier = Modifier
                    .weight(0.6f)
                    .verticalScroll(rememberScrollState())
            ) {
                // Email field
                OutlinedTextField(
                    value = signupData.email,
                    onValueChange = { onDataChange(signupData.copy(email = it)) },
                    label = { Text("Email") },
                    placeholder = { Text("your.email@example.com") },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Email,
                        imeAction = ImeAction.Next
                    ),
                    isError = signupData.email.isNotBlank() && !isEmailValid,
                    supportingText = {
                        if (signupData.email.isNotBlank() && !isEmailValid) {
                            Text(
                                text = "Please enter a valid email address",
                                color = MaterialTheme.colorScheme.error,
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                )

                // Confirm email field
                OutlinedTextField(
                    value = signupData.confirmEmail,
                    onValueChange = { onDataChange(signupData.copy(confirmEmail = it)) },
                    label = { Text("Confirm Email") },
                    placeholder = { Text("Confirm your email address") },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Email,
                        imeAction = ImeAction.Next
                    ),
                    isError = signupData.confirmEmail.isNotBlank() && !doEmailsMatch,
                    supportingText = {
                        if (signupData.confirmEmail.isNotBlank() && !doEmailsMatch) {
                            Text(
                                text = "Email addresses do not match",
                                color = MaterialTheme.colorScheme.error,
                                style = MaterialTheme.typography.bodySmall
                            )
                        } else if (doEmailsMatch && signupData.confirmEmail.isNotBlank()) {
                            Text(
                                text = "Email addresses match ✓",
                                color = MaterialTheme.colorScheme.primary,
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                )

                // Password field
                OutlinedTextField(
                    value = signupData.password,
                    onValueChange = { onDataChange(signupData.copy(password = it)) },
                    label = { Text("Password") },
                    placeholder = { Text("Enter your password") },
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Password,
                        imeAction = ImeAction.Done
                    ),
                    trailingIcon = {
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(
                                imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                contentDescription = if (passwordVisible) "Hide password" else "Show password"
                            )
                        }
                    },
                    isError = signupData.password.isNotBlank() && !isPasswordValid,
                    supportingText = {
                        if (signupData.password.isNotBlank() && !isPasswordValid) {
                            Text(
                                text = "Password must be at least 8 characters long",
                                color = MaterialTheme.colorScheme.error,
                                style = MaterialTheme.typography.bodySmall
                            )
                        } else if (isPasswordValid) {
                            Text(
                                text = "Password strength: Good ✓",
                                color = MaterialTheme.colorScheme.primary,
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    } else {
        // Layout verticale (portrait)
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            Text(
                text = "Account credentials",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Text(
                text = "Create your account with email and password",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = 24.dp)
            )

            // Email field
            OutlinedTextField(
                value = signupData.email,
                onValueChange = { onDataChange(signupData.copy(email = it)) },
                label = { Text("Email") },
                placeholder = { Text("your.email@example.com") },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Next
                ),
                isError = signupData.email.isNotBlank() && !isEmailValid,
                supportingText = {
                    if (signupData.email.isNotBlank() && !isEmailValid) {
                        Text(
                            text = "Please enter a valid email address",
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            )

            // Confirm email field
            OutlinedTextField(
                value = signupData.confirmEmail,
                onValueChange = { onDataChange(signupData.copy(confirmEmail = it)) },
                label = { Text("Confirm Email") },
                placeholder = { Text("Confirm your email address") },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Next
                ),
                isError = signupData.confirmEmail.isNotBlank() && !doEmailsMatch,
                supportingText = {
                    if (signupData.confirmEmail.isNotBlank() && !doEmailsMatch) {
                        Text(
                            text = "Email addresses do not match",
                            color = MaterialTheme.colorScheme.error
                        )
                    } else if (doEmailsMatch && signupData.confirmEmail.isNotBlank()) {
                        Text(
                            text = "Email addresses match ✓",
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            )

            // Password field
            OutlinedTextField(
                value = signupData.password,
                onValueChange = { onDataChange(signupData.copy(password = it)) },
                label = { Text("Password") },
                placeholder = { Text("Enter your password") },
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Done
                ),
                trailingIcon = {
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(
                            imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                            contentDescription = if (passwordVisible) "Hide password" else "Show password"
                        )
                    }
                },
                isError = signupData.password.isNotBlank() && !isPasswordValid,
                supportingText = {
                    if (signupData.password.isNotBlank() && !isPasswordValid) {
                        Text(
                            text = "Password must be at least 8 characters long",
                            color = MaterialTheme.colorScheme.error
                        )
                    } else if (isPasswordValid) {
                        Text(
                            text = "Password strength: Good ✓",
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp)
            )

            Spacer(modifier = Modifier.weight(1f))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OutlinedButton(
                    onClick = onBack,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Back")
                }

                Button(
                    onClick = onComplete,
                    enabled = isFormValid,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Complete Signup")
                }
            }
        }
    }
}
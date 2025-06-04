package com.example.togetthere.ui.screens

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.content.res.Configuration
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Camera
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.togetthere.CameraXActivity
import com.example.togetthere.R
import com.example.togetthere.firebase.updateDreamTripImage
import com.example.togetthere.firebase.updateUserProfileWithPhoto
import com.example.togetthere.firebase.uploadDreamTripImage
import com.example.togetthere.model.Destination
import com.example.togetthere.model.GenderType
import com.example.togetthere.model.UserProfile
import com.example.togetthere.ui.components.TGT_SmallFAB
import com.example.togetthere.ui.components.profile_comps.EditSocials
import com.example.togetthere.ui.components.profile_comps.SelectableInterests
import com.example.togetthere.ui.navigation.ToGetThereDestinations
import com.example.togetthere.viewmodel.ProfileViewModel


@Composable
fun EditProfileScreen(navController: NavController, vm: ProfileViewModel, topPadding: Dp, bottomPadding: Dp) {
    if (LocalConfiguration.current.orientation == Configuration.ORIENTATION_LANDSCAPE) {
        ProfileEditView(navController, vm, true, topPadding, bottomPadding)
    } else {
        ProfileEditView(navController, vm, false, topPadding, bottomPadding)
    }
}


@Composable
fun PhotoButton(
    topPadding: Dp,
    userProfile: UserProfile,
    onProfileUpdated: (UserProfile) -> Unit,
    onError: (String) -> Unit = {}
) {
    var showMenu by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }

    val context = LocalContext.current

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val imageUri = result.data?.getStringExtra("image_uri")?.let { Uri.parse(it) }
            if (imageUri != null) {
                isLoading = true
                updateUserProfileWithPhoto(
                    userProfile = userProfile,
                    newImageUri = imageUri,
                    onSuccess = { updatedProfile ->
                        isLoading = false
                        onProfileUpdated(updatedProfile)
                        Toast.makeText(context, "Foto profilo aggiornata", Toast.LENGTH_SHORT).show()
                    },
                    onFailure = { e ->
                        isLoading = false
                        onError("Errore nell'aggiornamento della foto: ${e.message}")
                        Toast.makeText(context, "Errore nell'aggiornamento", Toast.LENGTH_SHORT).show()
                    }
                )
            }
        }
    }

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            isLoading = true
            updateUserProfileWithPhoto(
                userProfile = userProfile,
                newImageUri = uri,
                onSuccess = { updatedProfile ->
                    isLoading = false
                    onProfileUpdated(updatedProfile)
                    Toast.makeText(context, "Foto profilo aggiornata", Toast.LENGTH_SHORT).show()
                },
                onFailure = { e ->
                    isLoading = false
                    onError("Errore nell'aggiornamento della foto: ${e.message}")
                    Toast.makeText(context, "Errore nell'aggiornamento", Toast.LENGTH_SHORT).show()
                }
            )
        }
    }

    Box(
        contentAlignment = Alignment.TopEnd
    ) {
        TGT_SmallFAB(
            Icons.Default.CameraAlt,
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
            contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
            elevation = FloatingActionButtonDefaults.elevation(0.dp, 0.dp)
        ) {
            if (!isLoading) {
                showMenu = !showMenu
            }
        }

        // Indicatore di caricamento
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.size(24.dp),
                strokeWidth = 2.dp
            )
        }

        DropdownMenu(
            expanded = showMenu && !isLoading,
            onDismissRequest = { showMenu = false },
            modifier = Modifier.background(Color.White)
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

            // Mostra l'opzione "Remove" solo se c'è una foto
            if (!userProfile.photo.isNullOrBlank()) {
                DropdownMenuItem(
                    text = { Text("Remove Photo") },
                    leadingIcon = {
                        Icon(Icons.Filled.Delete, contentDescription = "remove_photo_icon")
                    },
                    onClick = {
                        showMenu = false
                        isLoading = true
                        updateUserProfileWithPhoto(
                            userProfile = userProfile,
                            newImageUri = null, // null significa rimuovi
                            onSuccess = { updatedProfile ->
                                isLoading = false
                                onProfileUpdated(updatedProfile)
                                Toast.makeText(context, "Foto profilo rimossa", Toast.LENGTH_SHORT).show()
                            },
                            onFailure = { e ->
                                isLoading = false
                                onError("Errore nella rimozione della foto: ${e.message}")
                                Toast.makeText(context, "Errore nella rimozione", Toast.LENGTH_SHORT).show()
                            }
                        )
                    }
                )
            }
        }
    }
}


// Aggiorna il file EditProfileScreen.kt con questa sezione aggiornata

@Composable
fun ProfileEditView(navController: NavController, vm: ProfileViewModel, isLandscape: Boolean, topPadding: Dp, bottomPadding: Dp) {
    val context = LocalContext.current

    Box(modifier = Modifier.fillMaxSize()) {

        // ---------- FOTO PROFILO ----------

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

        // ---------- CONTENUTO ----------
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(195.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .offset(y = 25.dp)
                    .clip(RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp))
                    .background(MaterialTheme.colorScheme.background)
                    .padding(start = 8.dp, end = 8.dp, bottom = 48.dp)
            ) {
                Spacer(Modifier.height(20.dp))

                Text("Use the fields below to edit your profile.",
                    Modifier.fillMaxWidth().padding(start = 16.dp),
                    textAlign = TextAlign.Left,
                    fontWeight = FontWeight.Medium)
                Spacer(Modifier.height(16.dp))

                // ---------- CAMPI PROFILO ----------
                ProfileTextField("Name", vm.userName) { vm.userName = it }
                ProfileTextField("Surname", vm.userSurname) { vm.userSurname = it }
                ProfileTextField("Nickname", vm.userNickname) { vm.userNickname = it }
                ProfileTextField("About Me", vm.userDescription) { vm.userDescription = it }

                Spacer(modifier = Modifier.height(16.dp))

                // ---------- INTERESSI ----------
                SelectableInterests(
                    initialSelected = vm.userInterests,
                    onSelectionChanged = { vm.userInterests = it }
                )

                Spacer(modifier = Modifier.height(16.dp))

                // ---------- DREAM TRIPS ----------
                EditDreamTrips(
                    dreamTrips = vm.userDreamTrips,
                    onDreamTripsChange = { vm.userDreamTrips = it }
                )

                Spacer(modifier = Modifier.height(16.dp))

                // ---------- SOCIAL ----------
                EditSocials(vm.userSocials,
                    onSocialChange = { updated ->
                        val existingIndex = vm.userSocials.indexOfFirst { it.platform == updated.platform }

                        if (existingIndex != -1) {
                            // Se il social esiste già, aggiornalo
                            vm.userSocials = vm.userSocials.mapIndexed { index, social ->
                                if (index == existingIndex) updated else social
                            }
                        } else {
                            // Se il social non esiste ancora, aggiungilo alla lista
                            vm.userSocials = vm.userSocials + updated
                        }
                    })
                Spacer(modifier = Modifier.height(8.dp))

                // ---------- PULSANTE SALVA ----------
                Button(
                    onClick = {
                        vm.validateAndSave { success ->
                            if (!success) {
                                Toast.makeText(
                                    context,
                                    "Please fill all the fields",
                                    Toast.LENGTH_SHORT
                                ).show()
                            } else {
                                Toast.makeText(context, "Profile updated!", Toast.LENGTH_SHORT).show()
                                navController.navigate(ToGetThereDestinations.profileRoute(vm.id))
                            }
                        }
                    },
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth()
                ) {
                    Text("Save changes")
                }
                Spacer(Modifier.height(bottomPadding))
            }
        }

        // ---------- PULSANTI ----------
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .safeContentPadding(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            TGT_SmallFAB(
                Icons.AutoMirrored.Filled.ArrowBack,
                containerColor = MaterialTheme.colorScheme.surfaceVariant,
                contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                elevation = FloatingActionButtonDefaults.elevation(0.dp, 0.dp)
            ) {
                navController.popBackStack()
            }

            // Creo il UserProfile dal ViewModel per passarlo al PhotoButton
            val currentUserProfile = UserProfile(
                userId =  vm.id,
                name = vm.userName,
                surname = vm.userSurname,
                nickname = vm.userNickname,
                description = vm.userDescription,
                photo = vm.userPhoto,
                gender = vm.userGender,
                interests = vm.userInterests,
                socials = vm.userSocials,
                desiredDestinations = vm.userDreamTrips // Aggiungi anche i dream trips
            )

            PhotoButton(
                topPadding = topPadding,
                userProfile = currentUserProfile,
                onProfileUpdated = { updatedProfile ->
                    // Aggiorna il ViewModel con i nuovi dati del profilo
                    vm.userPhoto = updatedProfile.photo
                },
                onError = { errorMessage ->
                    Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show()
                }
            )
        }
    }
}


@Composable
fun EditDreamTrips(
    dreamTrips: List<Destination>,
    onDreamTripsChange: (List<Destination>) -> Unit
) {
    var showAddDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Dream Trips",
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp
            )

            Button(
                onClick = { showAddDialog = true },
                modifier = Modifier.height(32.dp),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add dream trip",
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "Add",
                    fontSize = 12.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(horizontal = 16.dp)
        ) {
            items(
                items = dreamTrips,
                key = { destination -> destination.name }
            ) { destination ->
                DreamTripCard(
                    destination = destination,
                    onEdit = { updatedDestination ->
                        val updatedList = dreamTrips.map { currentDestination ->
                            if (currentDestination.name == destination.name) {
                                updatedDestination
                            } else {
                                currentDestination
                            }
                        }
                        onDreamTripsChange(updatedList)
                    },
                    onDelete = {
                        val updatedList = dreamTrips.filter { currentDestination ->
                            currentDestination.name != destination.name
                        }
                        onDreamTripsChange(updatedList)
                    }
                )
            }
        }

        if (dreamTrips.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .padding(horizontal = 16.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                        modifier = Modifier.size(32.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "No dream trips added yet",
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
                        fontSize = 14.sp,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }

    // Dialog per aggiungere nuovo dream trip
    if (showAddDialog) {
        AddDreamTripDialog(
            onDismiss = { showAddDialog = false },
            onAdd = { newDestination ->
                val updatedList = dreamTrips + newDestination
                onDreamTripsChange(updatedList)
                showAddDialog = false
            }
        )
    }
}

@Composable
fun DreamTripCard(
    destination: Destination,
    onEdit: (Destination) -> Unit,
    onDelete: () -> Unit
) {
    var showEditDialog by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .width(160.dp)
            .height(120.dp),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            // Immagine di background
            if (destination.imageURL.isNotBlank()) {
                Image(
                    painter = rememberAsyncImagePainter(destination.imageURL),
                    contentDescription = destination.name,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.primaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.size(32.dp)
                    )
                }
            }

            // Overlay scuro per leggibilità del testo
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.Transparent,
                                Color.Black.copy(alpha = 0.7f)
                            )
                        )
                    )
            )

            // Nome della destinazione
            Text(
                text = destination.name,
                color = Color.White,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(8.dp),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            // Pulsanti azione
            Row(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(4.dp)
            ) {
                IconButton(
                    onClick = { showEditDialog = true },
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit",
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                }

                IconButton(
                    onClick = onDelete,
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete",
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }

    // Dialog per modificare il dream trip
    if (showEditDialog) {
        EditDreamTripDialog(
            destination = destination,
            onDismiss = { showEditDialog = false },
            onSave = { updatedDestination ->
                onEdit(updatedDestination)
                showEditDialog = false
            }
        )
    }
}

@Composable
fun AddDreamTripDialog(
    onDismiss: () -> Unit,
    onAdd: (Destination) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var isLoading by remember { mutableStateOf(false) }

    val context = LocalContext.current

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        imageUri = uri
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Add Dream Trip")
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Destination Name") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Image:")

                    Button(
                        onClick = { galleryLauncher.launch("image/*") },
                        modifier = Modifier.height(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.PhotoLibrary,
                            contentDescription = "Select image",
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Select")
                    }
                }

                if (imageUri != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Image selected ✓",
                        color = MaterialTheme.colorScheme.primary,
                        fontSize = 12.sp
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isNotBlank()) {
                        isLoading = true

                        if (imageUri != null) {
                            // TODO: Implementare upload dell'immagine
                            uploadDreamTripImage(
                                imageUri = imageUri!!,
                                destinationName = name,
                                onSuccess = { imageUrl ->
                                    isLoading = false
                                    onAdd(Destination(name = name, imageURL = imageUrl))
                                },
                                onFailure = { e ->
                                    isLoading = false
                                    Toast.makeText(context, "Error uploading image: ${e.message}", Toast.LENGTH_SHORT).show()
                                    // Aggiungi comunque senza immagine
                                    onAdd(Destination(name = name, imageURL = ""))
                                }
                            )
                        } else {
                            isLoading = false
                            onAdd(Destination(name = name, imageURL = ""))
                        }
                    }
                },
                enabled = name.isNotBlank() && !isLoading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(16.dp),
                        strokeWidth = 2.dp
                    )
                } else {
                    Text("Add")
                }
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun EditDreamTripDialog(
    destination: Destination,
    onDismiss: () -> Unit,
    onSave: (Destination) -> Unit
) {
    var name by remember { mutableStateOf(destination.name) }
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var isLoading by remember { mutableStateOf(false) }

    val context = LocalContext.current

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        imageUri = uri
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Edit Dream Trip")
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Destination Name") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Change Image:")

                    Button(
                        onClick = { galleryLauncher.launch("image/*") },
                        modifier = Modifier.height(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.PhotoLibrary,
                            contentDescription = "Select image",
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Select")
                    }
                }

                if (imageUri != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "New image selected ✓",
                        color = MaterialTheme.colorScheme.primary,
                        fontSize = 12.sp
                    )
                } else if (destination.imageURL.isNotBlank()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Current image will be kept",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 12.sp
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isNotBlank()) {
                        isLoading = true

                        if (imageUri != null) {
                            // TODO: Implementare upload dell'immagine e cancellazione della vecchia
                            updateDreamTripImage(
                                currentImageUrl = destination.imageURL,
                                newImageUri = imageUri!!,
                                destinationName = name,
                                onSuccess = { newImageUrl ->
                                    isLoading = false
                                    onSave(destination.copy(name = name, imageURL = newImageUrl))
                                },
                                onFailure = { e ->
                                    isLoading = false
                                    Toast.makeText(context, "Error updating image: ${e.message}", Toast.LENGTH_SHORT).show()
                                    // Salva comunque con il nome aggiornato
                                    onSave(destination.copy(name = name))
                                }
                            )
                        } else {
                            isLoading = false
                            onSave(destination.copy(name = name))
                        }
                    }
                },
                enabled = name.isNotBlank() && !isLoading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(16.dp),
                        strokeWidth = 2.dp
                    )
                } else {
                    Text("Save")
                }
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun ProfileTextField(label: String, value: String, onValueChange: (String) -> Unit) {
    Text(
        text = label,
        color = MaterialTheme.colorScheme.onSurface,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(start = 16.dp),
        fontSize = 20.sp,
    )

    Spacer(modifier = Modifier.height(12.dp))

    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    )

    Spacer(modifier = Modifier.height(12.dp))
}
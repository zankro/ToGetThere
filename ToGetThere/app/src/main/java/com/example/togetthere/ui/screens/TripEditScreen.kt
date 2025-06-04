package com.example.togetthere.ui.screens

//import androidx.compose.runtime.saveable.ListSaver
import android.annotation.SuppressLint
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.togetthere.ui.components.TGT_AlertDialog
import com.example.togetthere.ui.components.TGT_FAB
import com.example.togetthere.ui.components.TGT_SmallFAB
import com.example.togetthere.ui.components.trip_comps.DateRangePickerModal
import com.example.togetthere.ui.components.trip_comps.TGT_ActivitiesInput
import com.example.togetthere.ui.components.trip_comps.TGT_DatesPicker
import com.example.togetthere.ui.components.trip_comps.TGT_ImagePicker
import com.example.togetthere.ui.components.trip_comps.TGT_PriceSlider
//import com.example.togetthere.ui.components.trip_comps.TGT_StopsInput
import com.example.togetthere.ui.components.trip_comps.TGT_StopsInputWithDates
import com.example.togetthere.ui.components.trip_comps.TGT_TagsEditor
import com.example.togetthere.ui.components.trip_comps.TGT_TextInput
import com.example.togetthere.ui.components.trip_comps.TGT_TripTypePicker
import com.example.togetthere.ui.navigation.ToGetThereDestinations
import com.example.togetthere.ui.theme.CustomTheme
import com.example.togetthere.ui.theme.ToGetThereTheme
import com.example.togetthere.utils.convertDateToMillis
import com.example.togetthere.utils.convertMillisToDate
import com.example.togetthere.viewmodel.TripViewModel

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun EditTravelProposalView(navController: NavController, vm: TripViewModel, bottomPadding: Dp) {
//    val context = LocalActivity.current
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())
    var showDatePicker by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    val context = LocalContext.current
//    var images: List<TripPhoto> by rememberSaveable(
//        stateSaver = listSaver(
//            save = { list -> list.map { (it as TripPhoto.UriPhoto).uri.toString() } },
//            restore = { list -> list.map { TripPhoto.UriPhoto(Uri.parse(it)) } }
//        )
//    ) {
//        mutableStateOf(emptyList())
//    }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetMultipleContents()
    ) { uris ->
        if (uris.isNotEmpty()) {
            vm.addGalleryImages(uris)
        }
    }


    val tripState by vm.trip.collectAsState()

    if (tripState == null) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    val trip = tripState!!
    vm.resetFields()

    ToGetThereTheme {
        Scaffold(
            modifier = Modifier
                .nestedScroll(scrollBehavior.nestedScrollConnection)
                .padding(bottom = bottomPadding),
            topBar = {
                MediumTopAppBar(
                    colors = TopAppBarDefaults.mediumTopAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surface,
                        titleContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    ),
                    title = {
                        Text(
                            text = "Edit Trip",
                            style = CustomTheme.typography.headlineMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    },
                    navigationIcon = {
                        TGT_SmallFAB(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            containerColor = MaterialTheme.colorScheme.surfaceVariant,
                            contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            elevation = FloatingActionButtonDefaults.elevation(0.dp, 0.dp)
                        ) {
                            navController.popBackStack()
                        }
                    },
                    actions = {
                        TGT_SmallFAB(
                            Icons.Outlined.Delete,
                            containerColor = MaterialTheme.colorScheme.errorContainer,
                            contentColor = MaterialTheme.colorScheme.onErrorContainer,
                            elevation = FloatingActionButtonDefaults.elevation(0.dp, 0.dp)
                        ) { /*vm.removeTrip(); navController.popBackStack(ToGetThereDestinations.HOME_ROUTE, false)*/ showDeleteDialog = true }
                    },
                    scrollBehavior = scrollBehavior
                )
            },
            floatingActionButton = {
                TGT_FAB(
                    icon = Icons.Filled.Done,
                    onClick = {
                        if (vm.validate()) {
                            vm.applyValidatedTrip()
                            navController.popBackStack()
                        } else {
                            Toast.makeText(context, "Please check fields", Toast.LENGTH_SHORT).show()
                        }
                    }
                )
            },
        ) { innerPadding ->
            if (showDatePicker) {
                DateRangePickerModal(
                    initialStartDateMillis = convertDateToMillis(vm.tripStartDate),
                    initialEndDateMillis = convertDateToMillis(vm.tripEndDate),
                    onDateRangeSelected = {
                        if (it.first != null && it.second != null) {
                            vm.tripStartDate = convertMillisToDate(it.first!!)
                            vm.tripEndDate = convertMillisToDate(it.second!!)
                        }
                        showDatePicker = false
                    },
                    onDismiss = { showDatePicker = false }
                )
            }

            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .padding(start = 16.dp, end = 16.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                Text(
                    text = trip.name,
                    style = CustomTheme.typography.headlineLarge
                )

                Column(
                    modifier = Modifier
                        .padding(bottom = 16.dp)
                        .clip(shape = RoundedCornerShape(16.dp))
                        .background(color = MaterialTheme.colorScheme.surfaceContainerLow)
                        .fillMaxWidth()
                        .padding(10.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    TGT_TextInput(
                        title = "Name",
                        value = vm.tripName,
                        error = vm.nameError,
                        minLines = 1,
                        onValueChange = { vm.tripName = it })
//                    TGT_ImagePicker(
//                        images = vm.tripImages,
//                        onAddImage = { vm.addMockImage() },
//                        onRemoveImage = { image -> vm.removeImage(image) })
                    TGT_ImagePicker(
                        images = vm.tripImages,
                        onAddImageClick = { launcher.launch("image/*") },
                        onRemoveImage = { image -> vm.removeImage(image) }
                    )
                    TGT_TextInput(
                        title = "Location",
                        value = vm.tripDestination,
                        error = vm.destinationError,
                        minLines = 1,
                        onValueChange = { vm.tripDestination = it })
                    TGT_TripTypePicker(
                        selectedTripType = vm.tripType,
                        onTripTypeSelected = { vm.tripType = it })
//                    TGT_DatesPicker(
//                        start = vm.tripStartDate,
//                        end = vm.tripEndDate,
//                        error = vm.dateError,
//                        showPicker = { showDatePicker = true })
                    TGT_TagsEditor(
                        tags = vm.tripTags,
                        error = vm.tagsError,
                        onTagsChange = { vm.tripTags = it })
                    TGT_TextInput(
                        title = "Description",
                        value = vm.tripDescription,
                        error = vm.descriptionError,
                        onValueChange = { vm.tripDescription = it })
//                    TGT_StopsInput(stops = vm.tripStops, onStopsChange = { vm.tripStops = it })
                    TGT_StopsInputWithDates(
                        stops = vm.tripStops,
                        error = vm.stopsError,
                        onStopsChange = { vm.tripStops = it },
                        onTripDatesUpdated = { start, end ->
                            vm.tripStartDate = start
                            vm.tripEndDate = end
                        }
                    )
                    TGT_ActivitiesInput(
                        activities = vm.tripSuggestedActivities,
                        onAddActivity = {
                            vm.tripSuggestedActivities = vm.tripSuggestedActivities + it
                        },
                        onRemoveActivity = {
                            vm.tripSuggestedActivities = vm.tripSuggestedActivities - it
                        })
                    TGT_PriceSlider(
                        priceEsteem = vm.tripPrice,
                        onPriceChange = { vm.tripPrice = it })
                }
            }
        }

        TGT_AlertDialog(
            showDialog = showDeleteDialog,
            onDismissRequest = { showDeleteDialog = false },
            title = "Delete Trip",
            message = "Are you sure you want to delete this trip? This action cannot be undone.",
            confirmButtonText = "Delete",
            onConfirm = {
                vm.removeTrip()
                navController.popBackStack(ToGetThereDestinations.HOME_ROUTE, false)
            }
        )
//            AlertDialog(
//                onDismissRequest = { showDeleteDialog = false },
//                title = { Text("Delete Trip") },
//                text = { Text("Are you sure you want to delete this trip? This action cannot be undone.") },
//                confirmButton = {
//                    TextButton(onClick = {
//                        vm.removeTrip()
//                        showDeleteDialog = false
//                        navController.popBackStack(ToGetThereDestinations.HOME_ROUTE, false)
//                    }) {
//                        Text("Delete", color = MaterialTheme.colorScheme.error)
//                    }
//                },
//                dismissButton = {
//                    TextButton(onClick = { showDeleteDialog = false }) {
//                        Text("Cancel")
//                    }
//                }
//            )
    }
}


package com.example.togetthere.ui.screens

import android.net.Uri
import android.os.Bundle
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DateRangePicker
import androidx.compose.material3.DateRangePickerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.RangeSlider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.core.os.bundleOf
import androidx.lifecycle.ViewModel
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import com.example.togetthere.data.PaoloProfile
import com.example.togetthere.model.AgeRange
import com.example.togetthere.model.Filter
import com.example.togetthere.model.PriceRange
import com.example.togetthere.model.Stage
import com.example.togetthere.model.Trip
import com.example.togetthere.model.TripPhoto
import com.example.togetthere.model.TripReview
import com.example.togetthere.model.TripType
import com.example.togetthere.model.UserProfile
import com.example.togetthere.ui.components.NotLoggedInComponent
import com.example.togetthere.ui.navigation.ToGetThereNavigationActions
import com.example.togetthere.utils.millisToString
import com.example.togetthere.viewmodel.TripsViewModel
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.util.Calendar
import java.util.Date
import java.util.Locale
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import com.example.togetthere.utils.continentToCountries
import kotlin.math.roundToInt

// Enum for travel creation steps
enum class TravelCreationStep {
    TYPE, TAG, TRIPSTAGES, DESCRIPTION, IMAGES, SUGGESTEDACTIVITY, LASTDETAILS
}

fun createDefaultStage(): Stage {
    return Stage(
        stageName = "",
        startDate = "",
        endDate = "",
        freeRoaming = false
    )
}

// Comprehensive data class for travel proposal
data class TravelProposalData(
    val name: String = "",
    val type: TripType = TripType.ADVENTURE,
    val destination: String = "",
    val creator: String = "",
    val numParticipants: Int = 1,
    val maxParticipants: Int = 10,
    val startDate: String = "",
    val endDate: String = "",
    val images: List<TripPhoto> = emptyList(),
    val tags: List<String> = emptyList(),
    val description: String = "",
    val stops: List<Stage> = emptyList(),
    val priceEstimation: PriceRange = PriceRange(0, 0),
    val suggestedActivities: List<String> = emptyList(),
    val filters: List<Filter> = emptyList(),
    val ageRange: AgeRange = AgeRange(18, 35),
    val reviews: List<TripReview>? = null,
    val favoritesUsers: List<String>? = null
)


// ViewModel for travel creation
class TravelCreationViewModel : ViewModel() {
    var travelData = mutableStateOf(TravelProposalData())
    var currentStep = mutableStateOf(TravelCreationStep.TYPE)
    var isEditingStages = mutableStateOf(false)
    var currentStageIndex = mutableIntStateOf(0)
}

// Singleton ViewModel Provider
object TravelViewModelProvider {
    private var sharedViewModel: TravelCreationViewModel? = null

    fun getViewModel(): TravelCreationViewModel {
        return sharedViewModel ?: TravelCreationViewModel().also {
            sharedViewModel = it
        }
    }
}

@Composable
fun CreateScreen(
    navController: NavHostController,
    tripId: Int? = null,
    tripsViewModel: TripsViewModel,
    isLandscape: Boolean,
    bottomPadding: Dp,
    user: UserProfile?
) {
    val configuration = LocalConfiguration.current
    val orientation = configuration.orientation

    val currentTrip by tripsViewModel.trip.collectAsState()
    val viewModel = TravelViewModelProvider.getViewModel()

    // Show login required message and button
    if (user == null) {
        NotLoggedInComponent(isLandscape, navController, bottomPadding)
        return
    }

    LaunchedEffect(tripId) {
        if (tripId != null) {
            tripsViewModel.loadTripById(tripId)
        }
    }


    LaunchedEffect(tripId, currentTrip) {
        if (tripId != null && currentTrip != null) {
            val trip = currentTrip!!
            println("siamo qui")

            val travelData = TravelProposalData(
                name = trip.name,
                destination = trip.destination,
                creator = user.userId,
                numParticipants = trip.numParticipants,
                maxParticipants = trip.maxParticipants,
                startDate = trip.startDate,
                endDate = trip.endDate,
                type = trip.type,
                tags = trip.tags,
                stops = trip.stops,
                description = trip.description,
                filters = trip.filters,
                images = trip.images,
                priceEstimation = trip.priceEstimation,
                suggestedActivities = trip.suggestedActivities,
                ageRange = trip.ageRange
            )

            viewModel.travelData.value = travelData
            viewModel.currentStep.value = TravelCreationStep.LASTDETAILS
        } else if (tripId == null) {
            viewModel.travelData.value = TravelProposalData(
                creator = user.userId
            )
            viewModel.currentStep.value = TravelCreationStep.TYPE
        }
    }

    when (orientation) {
        android.content.res.Configuration.ORIENTATION_LANDSCAPE -> TravelCreationFlowLandscape(
            navController,
            tripsViewModel
        )

        else -> TravelCreationFlow(navController, tripsViewModel)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TravelCreationFlow(navController: NavHostController, tripsViewModel: TripsViewModel) {
    val viewModel = TravelViewModelProvider.getViewModel()
    var travelData by viewModel.travelData
    var currentStep by viewModel.currentStep
    var isEditingStages by viewModel.isEditingStages
    var currentStageIndex by viewModel.currentStageIndex

    val navigationActions = remember(navController) {
        ToGetThereNavigationActions(navController)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        if (isEditingStages) "Edit Stage ${currentStageIndex + 1}"
                        else "Create Travel Proposal"
                    )
                },
                navigationIcon = {
                    IconButton(onClick = {
                        when {
                            isEditingStages -> isEditingStages = false
                            currentStep == TravelCreationStep.TYPE ->
                                navigationActions.navigateToHome()

                            currentStep == TravelCreationStep.TAG ->
                                currentStep = TravelCreationStep.TYPE

                            currentStep == TravelCreationStep.TRIPSTAGES ->
                                currentStep = TravelCreationStep.TAG

                            currentStep == TravelCreationStep.DESCRIPTION ->
                                currentStep = TravelCreationStep.TRIPSTAGES

                            currentStep == TravelCreationStep.IMAGES ->
                                currentStep = TravelCreationStep.DESCRIPTION

                            currentStep == TravelCreationStep.SUGGESTEDACTIVITY ->
                                currentStep = TravelCreationStep.IMAGES

                            currentStep == TravelCreationStep.LASTDETAILS ->
                                currentStep = TravelCreationStep.SUGGESTEDACTIVITY
                        }
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
        ) {
            // Step indicator
            if (!isEditingStages) {
                StepIndicator(currentStep)
                Spacer(modifier = Modifier.height(24.dp))
            }

            // Content based on current state
            Box(modifier = Modifier.weight(1f)) {
                when {
                    isEditingStages -> StageCreationScreen(
                        travelData = travelData,
                        stageIndex = currentStageIndex,
                        onDataChange = { travelData = it },
                        onPrevious = {
                            if (currentStageIndex > 0) currentStageIndex--
                        },
                        onNext = {
                            if (currentStageIndex < travelData.stops.size - 1) currentStageIndex++
                        },
                        onDone = { isEditingStages = false }
                    )

                    else -> when (currentStep) {
                        TravelCreationStep.TYPE -> TravelTypeStep(
                            travelData = travelData,
                            onDataChange = { travelData = it },
                            onNext = { currentStep = TravelCreationStep.TAG },
                            onBack = { navigationActions.navigateToHome() }
                        )

                        TravelCreationStep.TAG -> TravelTagStep(
                            travelData = travelData,
                            onDataChange = { travelData = it },
                            onNext = { currentStep = TravelCreationStep.TRIPSTAGES },
                            onBack = { currentStep = TravelCreationStep.TYPE }
                        )

                        TravelCreationStep.TRIPSTAGES -> TripStagesStep(
                            travelData = travelData,
                            onDataChange = { travelData = it },
                            onNext = { currentStep = TravelCreationStep.DESCRIPTION },
                            onBack = { currentStep = TravelCreationStep.TAG },
                            onEditStage = { index ->
                                currentStageIndex = index
                                isEditingStages = true
                            }
                        )

                        TravelCreationStep.DESCRIPTION -> TripDescriptionStep(
                            travelData = travelData,
                            onDataChange = { travelData = it },
                            onNext = { currentStep = TravelCreationStep.IMAGES },
                            onBack = { currentStep = TravelCreationStep.TRIPSTAGES }
                        )

                        TravelCreationStep.IMAGES -> TripImagesStep(
                            travelData = travelData,
                            onDataChange = { travelData = it },
                            onNext = { currentStep = TravelCreationStep.SUGGESTEDACTIVITY },
                            onBack = { currentStep = TravelCreationStep.DESCRIPTION }
                        )

                        TravelCreationStep.SUGGESTEDACTIVITY -> SuggestedActivityStep(
                            travelData = travelData,
                            onDataChange = { travelData = it },
                            onNext = { currentStep = TravelCreationStep.LASTDETAILS },
                            onBack = { currentStep = TravelCreationStep.IMAGES }
                        )

                        TravelCreationStep.LASTDETAILS -> LastDetailsStep(
                            travelData = travelData,
                            onDataChange = { travelData = it },
                            tripsViewModel = tripsViewModel,
                            onComplete = { navigationActions.navigateToHome() },
                            onBack = { currentStep = TravelCreationStep.SUGGESTEDACTIVITY }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TravelCreationFlowLandscape(navController: NavHostController, tripsViewModel: TripsViewModel) {
    val viewModel = TravelViewModelProvider.getViewModel()
    var travelData by viewModel.travelData
    var currentStep by viewModel.currentStep
    var isEditingStages by viewModel.isEditingStages
    var currentStageIndex by viewModel.currentStageIndex

    val scrollState = rememberScrollState()

    val navigationActions = remember(navController) {
        ToGetThereNavigationActions(navController)
    }

    Row(modifier = Modifier.fillMaxSize()) {
        // Pannello sinistro (40%)
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .weight(0.4f)
                .background(MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                // Top: Icona e titolo
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    contentAlignment = Alignment.CenterStart
                ) {
                    IconButton(onClick = {
                        when {
                            isEditingStages -> isEditingStages = false
                            currentStep == TravelCreationStep.TYPE ->
                                navigationActions.navigateToHome()

                            else -> {
                                val previousStep = when (currentStep) {
                                    TravelCreationStep.TAG -> TravelCreationStep.TYPE
                                    TravelCreationStep.TRIPSTAGES -> TravelCreationStep.TAG
                                    TravelCreationStep.DESCRIPTION -> TravelCreationStep.TRIPSTAGES
                                    TravelCreationStep.IMAGES -> TravelCreationStep.DESCRIPTION
                                    TravelCreationStep.SUGGESTEDACTIVITY -> TravelCreationStep.IMAGES
                                    TravelCreationStep.LASTDETAILS -> TravelCreationStep.SUGGESTEDACTIVITY
                                    else -> TravelCreationStep.TYPE
                                }
                                currentStep = previousStep
                            }
                        }
                    }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }

                    Text(
                        text = "Travel Planner",
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.padding(start = 56.dp)
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))

                if (!isEditingStages) {
                    Text(
                        text = "Create Travel Proposal",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    StepIndicator(currentStep)
                    Spacer(modifier = Modifier.height(24.dp))

                    val stepDescription = when (currentStep) {
                        TravelCreationStep.TYPE -> "Choose the type of travel you want to plan"
                        TravelCreationStep.TAG -> "Select tags that describe your travel"
                        TravelCreationStep.TRIPSTAGES -> "Define the stages of your journey"
                        TravelCreationStep.DESCRIPTION -> "Describe your travel experience"
                        TravelCreationStep.IMAGES -> "Add photos to showcase your trip"
                        TravelCreationStep.SUGGESTEDACTIVITY -> "Add suggested activities"
                        TravelCreationStep.LASTDETAILS -> "Review final details"
                    }

                    Text(
                        text = stepDescription,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                } else {
                    Text(
                        text = "Edit Stage ${currentStageIndex + 1}",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    travelData.stops.forEachIndexed { index, stop ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .background(
                                        if (index == currentStageIndex)
                                            MaterialTheme.colorScheme.primary
                                        else
                                            MaterialTheme.colorScheme.surfaceVariant,
                                        shape = CircleShape
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "${index + 1}",
                                    color = if (index == currentStageIndex)
                                        MaterialTheme.colorScheme.onPrimary
                                    else
                                        MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            Spacer(modifier = Modifier.width(16.dp))

                            Text(
                                text = stop.stageName,
                                style = MaterialTheme.typography.bodyMedium,
                                color = if (index == currentStageIndex)
                                    MaterialTheme.colorScheme.primary
                                else
                                    MaterialTheme.colorScheme.onSurface,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.weight(1f))

                if (!isEditingStages && currentStep.ordinal >= TravelCreationStep.DESCRIPTION.ordinal) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "Travel Preview",
                                style = MaterialTheme.typography.titleSmall
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = "${travelData.name} (${travelData.stops.size} stages)",
                                style = MaterialTheme.typography.bodyMedium
                            )

                            Text(
                                text = "From ${travelData.stops.firstOrNull()?.stageName ?: ""} to ${travelData.stops.lastOrNull()?.stageName ?: ""}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }

        // Pannello destro (60%)
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .weight(0.6f)
                .background(MaterialTheme.colorScheme.background)
                .padding(horizontal = 24.dp, vertical = 16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
            ) {
                when {
                    isEditingStages -> StageCreationScreen(
                        travelData = travelData,
                        stageIndex = currentStageIndex,
                        onDataChange = { travelData = it },
                        onPrevious = {
                            if (currentStageIndex > 0) currentStageIndex--
                        },
                        onNext = {
                            if (currentStageIndex < travelData.stops.size - 1) currentStageIndex++
                        },
                        onDone = { isEditingStages = false }
                    )

                    else -> when (currentStep) {
                        TravelCreationStep.TYPE -> TravelTypeStep(
                            travelData = travelData,
                            onDataChange = { travelData = it },
                            onNext = { currentStep = TravelCreationStep.TAG },
                            onBack = { navigationActions.navigateToHome() }
                        )

                        TravelCreationStep.TAG -> TravelTagStep(
                            travelData = travelData,
                            onDataChange = { travelData = it },
                            onNext = { currentStep = TravelCreationStep.TRIPSTAGES },
                            onBack = { currentStep = TravelCreationStep.TYPE }
                        )

                        TravelCreationStep.TRIPSTAGES -> TripStagesStep(
                            travelData = travelData,
                            onDataChange = { travelData = it },
                            onNext = { currentStep = TravelCreationStep.DESCRIPTION },
                            onBack = { currentStep = TravelCreationStep.TAG },
                            onEditStage = { index ->
                                currentStageIndex = index
                                isEditingStages = true
                            }
                        )

                        TravelCreationStep.DESCRIPTION -> TripDescriptionStep(
                            travelData = travelData,
                            onDataChange = { travelData = it },
                            onNext = { currentStep = TravelCreationStep.IMAGES },
                            onBack = { currentStep = TravelCreationStep.TRIPSTAGES }
                        )

                        TravelCreationStep.IMAGES -> TripImagesStep(
                            travelData = travelData,
                            onDataChange = { travelData = it },
                            onNext = { currentStep = TravelCreationStep.SUGGESTEDACTIVITY },
                            onBack = { currentStep = TravelCreationStep.DESCRIPTION }
                        )

                        TravelCreationStep.SUGGESTEDACTIVITY -> SuggestedActivityStep(
                            travelData = travelData,
                            onDataChange = { travelData = it },
                            onNext = { currentStep = TravelCreationStep.LASTDETAILS },
                            onBack = { currentStep = TravelCreationStep.IMAGES }
                        )

                        TravelCreationStep.LASTDETAILS -> LastDetailsStep(
                            travelData = travelData,
                            onDataChange = { travelData = it },
                            tripsViewModel = tripsViewModel,
                            onComplete = { navigationActions.navigateToHome() },
                            onBack = { currentStep = TravelCreationStep.SUGGESTEDACTIVITY }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))
            }
        }
    }
}

@Composable
fun StepIndicator(currentStep: TravelCreationStep) {
    Column {
        Text(
            text = "Step ${currentStep.ordinal + 1} of ${TravelCreationStep.entries.size}",
            style = MaterialTheme.typography.titleMedium
        )
        Text(
            text = when (currentStep) {
                TravelCreationStep.TYPE -> "Select Travel Type"
                TravelCreationStep.TAG -> "Add Tags"
                TravelCreationStep.TRIPSTAGES -> "Plan Trip Stages"
                TravelCreationStep.DESCRIPTION -> "Describe Your Trip"
                TravelCreationStep.IMAGES -> "Add Images"
                TravelCreationStep.SUGGESTEDACTIVITY -> "Suggested Activities"
                TravelCreationStep.LASTDETAILS -> "Final Details"
            },
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        LinearProgressIndicator(
            progress = { (currentStep.ordinal + 1).toFloat() / TravelCreationStep.entries.size },
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
fun TravelTypeStep(
    travelData: TravelProposalData,
    onDataChange: (TravelProposalData) -> Unit,
    onNext: () -> Unit,
    onBack: () -> Unit,
) {
    var selectedType by remember { mutableStateOf<String?>(null) }

    // Update state when travelData changes
    LaunchedEffect(travelData.type) {
        selectedType = travelData.type.toString()
    }

    val availableTypes = TripType.entries

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = 50.dp) // Add padding at the bottom to account for navigation bar
            .padding(horizontal = 16.dp, vertical = 16.dp) // Add some padding on all sides
    ) {
        Text(
            "What type of travel are you planning?",
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(bottom = 4.dp)
        )
        Text(
            "Select one",
            style = MaterialTheme.typography.bodyMedium
        )

        Spacer(modifier = Modifier.height(16.dp))

        // This will take available space but leave room for buttons
        Box(modifier = Modifier.weight(1f)) {
            // Fix: Use a standard Column instead of LazyVerticalGrid
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                availableTypes.chunked(2).forEach { rowTypes ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        rowTypes.forEach { type ->
                            // Fix: Use type.toString() instead of type.name
                            val typeString = type.toString()
                            val isSelected = selectedType == typeString

                            FilterChip(
                                selected = isSelected,
                                onClick = {
                                    selectedType = if (isSelected) null else typeString
                                    val newType = selectedType?.let { TripType.valueOf(it) }
                                        ?: TripType.ADVENTURE
                                    onDataChange(travelData.copy(type = newType))
                                },
                                label = {
                                    // Fix: Use type.toString() and properly capitalize
                                    Text(
                                        text = type.toString().lowercase()
                                            .replaceFirstChar { it.uppercase() },
                                        style = MaterialTheme.typography.bodyLarge
                                    )
                                },
                                modifier = Modifier
                                    .weight(1f)
                                    .heightIn(min = 48.dp)
                                    .padding(vertical = 4.dp)
                            )
                        }
                    }
                }
            }
        }

        // Add spacer before buttons
        Spacer(modifier = Modifier.height(16.dp))

        // Button row with explicit padding and height
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
                .height(48.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            OutlinedButton(
                onClick = onBack,
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 8.dp)
            ) {
                Text("Back")
            }

            Button(
                onClick = onNext,
                enabled = selectedType != null,
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 8.dp)
            ) {
                Text("Next")
            }
        }
    }
}

@Composable
fun TravelTagStep(
    travelData: TravelProposalData,
    onDataChange: (TravelProposalData) -> Unit,
    onNext: () -> Unit,
    onBack: () -> Unit,
) {
    var selectedTags by remember { mutableStateOf(travelData.tags) }
    var newTagValue by remember { mutableStateOf("") }
    var allAvailableTags by remember {
        mutableStateOf(
            listOf(
                "Family",
                "Friends",
                "Solo",
                "Romantic",
                "Budget",
                "Luxury",
                "Weekend",
                "Beach",
                "City",
                "Mountain",
                "Cultural",
                "Food",
                "Adventure",
                "Nature",
                "Hiking",
                "Camping",
                "Ski",
                "Snowboard",
                "Historical",
                "Festival",
                "Workation",
                "Yoga",
                "Spa",
                "Photography",
                "Wildlife",
                "Backpack",
                "Cruise",
                "Safari",
                "Island",
                "Countryside",
                "Vineyard",
                "Desert",
                "Eco",
                "Volunteer",
                "Music",
                "Roadtrip",
                "Train",
                "Flight",
                "Nomad",
                "Digital",
                "Local",
                "Sunset",
                "Museum",
                "Architecture",
                "Shopping",
                "Fishing",
                "Diving",
                "Snorkeling",
                "Surfing",
                "Climbing",
                "Boat",
                "Trekking",
                "Picnic",
                "Forest",
                "Castle",
                "Temple",
                "Market",
                "Gastronomy"
            ).sorted()
        )
    }

    val mostUsedTags = listOf("Relax", "Party")

    val listState = rememberLazyListState()

    LaunchedEffect(selectedTags.size) {
        // Scroll all'ultimo elemento
        if (selectedTags.isNotEmpty()) {
            listState.animateScrollToItem(selectedTags.lastIndex)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 0.dp)
            .padding(bottom = 50.dp) // Add padding for navigation bar
    ) {
        if (selectedTags.isEmpty()) {
            Text("Select tags that describe your trip:", style = MaterialTheme.typography.bodyLarge)
            Spacer(modifier = Modifier.height(16.dp))
        }


        // Campo per aggiungere nuovi tag
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = newTagValue,
                onValueChange = { newTagValue = it },
                label = { Text("Add new tag") },
                modifier = Modifier.weight(1f),
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(onDone = {
                    if (newTagValue.isNotBlank()) {
                        val newTag = newTagValue.trim().replaceFirstChar { it.uppercase() }
                        // Aggiungi ai tag selezionati
                        selectedTags = selectedTags + newTag
                        // Aggiungi alla lista di tutti i tag se non esiste già
                        if (!allAvailableTags.contains(newTag)) {
                            allAvailableTags = allAvailableTags + newTag
                        }
                        onDataChange(travelData.copy(tags = selectedTags))
                        newTagValue = ""
                    }
                })
            )

            Spacer(modifier = Modifier.width(8.dp))

            Button(
                onClick = {
                    if (newTagValue.isNotBlank()) {
                        val newTag = newTagValue.trim().replaceFirstChar { it.uppercase() }
                        // Aggiungi ai tag selezionati
                        selectedTags = selectedTags + newTag
                        // Aggiungi alla lista di tutti i tag se non esiste già
                        if (!allAvailableTags.contains(newTag)) {
                            allAvailableTags = allAvailableTags + newTag
                        }
                        onDataChange(travelData.copy(tags = selectedTags))
                        newTagValue = ""
                    }
                }
            ) {
                Text("Add")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (selectedTags.isNotEmpty()) {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFFCEE9DB)),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
            ) {
                Column(
                    modifier = Modifier
                        .padding(start = 16.dp, end = 16.dp, bottom = 8.dp, top = 16.dp)
                ) {
                    Text("Selected tags:", style = MaterialTheme.typography.bodyMedium)
                    //Spacer(modifier = Modifier.height(8.dp))

                    // Area scorrevole per i tag selezionati
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                    ) {
                        LazyRow(
                            state = listState,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(selectedTags) { tag ->
                                AssistChip(
                                    onClick = {
                                        selectedTags = selectedTags - tag
                                        onDataChange(travelData.copy(tags = selectedTags))
                                    },
                                    label = { Text(tag) },
                                    colors = AssistChipDefaults.assistChipColors(
                                        containerColor = Color(0xFFFFFFFF),
                                        labelColor = Color.Black,
                                        leadingIconContentColor = Color.Black
                                    ),
                                    leadingIcon = {
                                        Icon(
                                            imageVector = Icons.Default.Close,
                                            contentDescription = "Remove tag",
                                            modifier = Modifier.size(16.dp)
                                        )
                                    },
                                    modifier = Modifier.padding(end = 4.dp)
                                )
                            }
                        }

                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }

        // Area scorrevole per i tag disponibili - riduciamo il peso per far spazio ai bottoni
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
        ) {
            // Most Frequently Used
//            Text("Most frequently used", style = MaterialTheme.typography.bodyMedium)
//            Spacer(modifier = Modifier.height(8.dp))

//            Row(
//                modifier = Modifier.fillMaxWidth(),
//                horizontalArrangement = Arrangement.spacedBy(8.dp)
//            ) {
//                mostUsedTags.forEach { tag ->
//                    val isSelected = selectedTags.contains(tag)
//                    FilterChip(
//                        selected = isSelected,
//                        onClick = {
//                            selectedTags = if (isSelected) {
//                                selectedTags - tag
//                            } else {
//                                selectedTags + tag
//                            }
//                            onDataChange(travelData.copy(tags = selectedTags))
//                        },
//                        label = { Text(tag) },
//                        modifier = Modifier.padding(vertical = 4.dp)
//                    )
//                }
//            }
//
//            Spacer(modifier = Modifier.height(24.dp))

            // All Tags
            Text("All tags", style = MaterialTheme.typography.bodyMedium)
            Spacer(modifier = Modifier.height(8.dp))

            allAvailableTags.chunked(3).forEach { rowTags ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    rowTags.forEach { tag ->
                        val isSelected = selectedTags.contains(tag)
                        FilterChip(
                            selected = isSelected,
                            onClick = {
                                selectedTags = if (isSelected) {
                                    selectedTags - tag
                                } else {
                                    selectedTags + tag
                                }
                                onDataChange(travelData.copy(tags = selectedTags))
                            },
                            label = { Text(tag) },
                            modifier = Modifier.padding(vertical = 4.dp),
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = Color(0xFFCEE9DB)
                            )
                        )
                    }
                }
            }
        }

        // Spacer esplicito prima dei bottoni
        Spacer(modifier = Modifier.height(16.dp))

        // Navigation Buttons - stile migliorato
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
                .height(48.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            OutlinedButton(
                onClick = onBack,
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 8.dp)
            ) {
                Text("Back")
            }

            Button(
                onClick = onNext,
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 8.dp)
            ) {
                Text("Next")
            }
        }
    }
}

@Composable
fun TripStagesStep(
    travelData: TravelProposalData,
    onDataChange: (TravelProposalData) -> Unit,
    onNext: () -> Unit,
    onBack: () -> Unit,
    onEditStage: (Int) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .padding(bottom = 50.dp) // Add padding for navigation bar
    ) {
        Text("Plan your trip stages", style = MaterialTheme.typography.bodyLarge)

        if (travelData.stops.isEmpty()) {
            Text(
                "Each stage represents a different location or significant part of your journey.",
                style = MaterialTheme.typography.bodySmall
            )
            Spacer(modifier = Modifier.height(24.dp))
        }


        // Show current stages if any
        if (travelData.stops.isNotEmpty()) {
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                "Current stages (${travelData.stops.size}):",
                style = MaterialTheme.typography.titleMedium
            )

            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
            ) {
                travelData.stops.forEachIndexed { index, stage ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    "Stage ${index + 1}: ${if (stage.stageName.isNotBlank()) stage.stageName else "Not specified yet"}",
                                    style = MaterialTheme.typography.bodyLarge
                                )
                                if (stage.startDate.isNotBlank() || stage.endDate.isNotBlank()) {
                                    Text(
                                        "${stage.startDate} - ${stage.endDate}",
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                }
                            }
                            IconButton(onClick = { onEditStage(index) }) {
                                Icon(
                                    imageVector = Icons.Default.Edit,
                                    contentDescription = "Edit stage"
                                )
                            }
                        }
                    }
                }
            }
        } else {
            // Show a message when no stages are added yet
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "No stages added yet. Add your first stage!",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            contentAlignment = Alignment.Center
        ) {
            Button(
                onClick = {
                    val newStages = travelData.stops + createDefaultStage()
                    onDataChange(travelData.copy(stops = newStages))
                    onEditStage(newStages.size - 1)
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF186B53)
                ),
                modifier = Modifier.padding(vertical = 8.dp)
            ) {
                Text("Add Stage")
                Spacer(modifier = Modifier.size(5.dp))
                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = "Add",
                    tint = Color.White,
                    modifier = Modifier.size(16.dp)
                )
            }
        }

        //Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
                .height(48.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            OutlinedButton(
                onClick = onBack,
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 8.dp)
            ) {
                Text("Back")
            }

            Button(
                onClick = onNext,
                enabled = travelData.stops.isNotEmpty(),
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF186B53)
                )
            ) {
                Text("Next")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StageCreationScreen(
    travelData: TravelProposalData,
    stageIndex: Int,
    onDataChange: (TravelProposalData) -> Unit,
    onPrevious: () -> Unit,
    onNext: () -> Unit,
    onDone: () -> Unit,
    isLandscape: Boolean = false
) {
    // Get current stage or create a new one if it doesn't exist
    var currentStage by remember {
        mutableStateOf(
            travelData.stops.getOrNull(stageIndex) ?: createDefaultStage()
        )
    }
    // State for showing date picker
    var datePickerExpanded by remember { mutableStateOf(false) }

    // Validation states
    var locationError by remember { mutableStateOf(false) }
    var datesError by remember { mutableStateOf(false) }
    var showValidationErrors by remember { mutableStateOf(false) }
    var pastDateError by remember { mutableStateOf(false) }
    var overlappingDatesError by remember { mutableStateOf(false) }
    var datesHoleDate by remember { mutableStateOf(false) }

    // Function to update the stage and propagate changes
    val updateStage = { updatedStage: Stage ->
        val updatedStages = travelData.stops.toMutableList().apply {
            if (stageIndex < size) {
                this[stageIndex] = updatedStage
            } else {
                add(updatedStage)
            }
        }
        onDataChange(travelData.copy(stops = updatedStages))
        currentStage = updatedStage
    }

    // Format for displaying dates
    val dateFormatter = remember { SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()) }

    // Get today's date (start of the day)
    val todayInMillis = remember {
        Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis
    }

    // Determine the minimum allowed start date based on the previous stage
    val minStartDateMillis = remember(stageIndex, travelData.stops) {
        if (stageIndex > 0 && travelData.stops.size > stageIndex - 1) {
            travelData.stops[stageIndex - 1].endDate.takeIf { it.isNotBlank() }?.let { endDateStr ->
                dateFormatter.parse(endDateStr)?.time?.let { it + 24 * 60 * 60 * 1000 } // Aggiungi un giorno
            } ?: todayInMillis
        } else {
            todayInMillis
        }
    }

    // Parse existing dates if they exist
    val startDateMillis = remember(currentStage.startDate) {
        currentStage.startDate.takeIf { it.isNotEmpty() }?.let {
            dateFormatter.parse(it)?.time
        }
    }

    val endDateMillis = remember(currentStage.endDate) {
        currentStage.endDate.takeIf { it.isNotEmpty() }?.let {
            dateFormatter.parse(it)?.time
        }
    }

    // Crea un set di date occupate dalle tappe precedenti
    val occupiedDates = remember(travelData.stops) {
        mutableSetOf<String>()
    }

    // Aggiorna il set delle date occupate
    LaunchedEffect(travelData.stops) {
        occupiedDates.clear()

        // Per ogni tappa precedente
        for (i in 0 until stageIndex) {
            val stage = travelData.stops[i]
            if (stage.startDate.isNotBlank() && stage.endDate.isNotBlank()) {
                val startDate = dateFormatter.parse(stage.startDate)?.time ?: continue
                val endDate = dateFormatter.parse(stage.endDate)?.time ?: continue

                // Aggiungi tutte le date nell'intervallo
                var currentDate = startDate
                while (currentDate <= endDate) {
                    occupiedDates.add(dateFormatter.format(Date(currentDate)))
                    // Avanza di un giorno
                    currentDate += 24 * 60 * 60 * 1000
                }
            }
        }
    }

    val dateRangePickerStateSaver = Saver<DateRangePickerState, Bundle>(
        save = { state ->
            bundleOf(
                "start" to state.selectedStartDateMillis?.let {
                    Instant.ofEpochMilli(it).atZone(ZoneId.systemDefault()).toLocalDate()
                        .toEpochDay()
                },
                "end" to state.selectedEndDateMillis?.let {
                    Instant.ofEpochMilli(it).atZone(ZoneId.systemDefault()).toLocalDate()
                        .toEpochDay()
                }
            )
        },
        restore = { bundle ->
            val startEpoch = bundle.getLong("start", -1)
            val endEpoch = bundle.getLong("end", -1)
            DateRangePickerState(
                initialSelectedStartDateMillis = if (startEpoch != -1L) startEpoch * 86400000 else null,
                initialSelectedEndDateMillis = if (endEpoch != -1L) endEpoch * 86400000 else null,
                yearRange = IntRange(LocalDate.now().year, LocalDate.now().year + 5),
                locale = Locale.getDefault()
            )
        }
    )

    val dateRangePickerState =
        rememberSaveable(saver = dateRangePickerStateSaver) {
            DateRangePickerState(
                initialSelectedStartDateMillis = startDateMillis,
                initialSelectedEndDateMillis = endDateMillis,
                initialDisplayedMonthMillis = startDateMillis ?: todayInMillis,
                yearRange = IntRange(LocalDate.now().year, LocalDate.now().year + 5),
                locale = Locale.getDefault()
            )
        }

    // Define startDateText and endDateText for the text fields
    var startDateText by remember(currentStage.startDate) { mutableStateOf(currentStage.startDate) }
    var endDateText by remember(currentStage.endDate) { mutableStateOf(currentStage.endDate) }

    // Update the dates when they change in the picker
    LaunchedEffect(
        dateRangePickerState.selectedStartDateMillis,
        dateRangePickerState.selectedEndDateMillis
    ) {
        val start = dateRangePickerState.selectedStartDateMillis
        val end = dateRangePickerState.selectedEndDateMillis

        if (start != null && end != null) {

            val formattedStartDate = millisToString(start)
            val formattedEndDate = millisToString(end)

            startDateText = formattedStartDate
            endDateText = formattedEndDate

            val updatedStage = currentStage.copy(
                startDate = formattedStartDate,
                endDate = formattedEndDate
            )
            updateStage(updatedStage)
            datesError = false
            pastDateError = false
            overlappingDatesError = false
            datesHoleDate = false
        } else {
            startDateText = ""
            endDateText = ""
            updateStage(currentStage.copy(startDate = "", endDate = ""))
            pastDateError = false
            overlappingDatesError = false
            datesHoleDate = false
        }
    }
    // Calculate if navigation buttons should be enabled
    val hasPrevious = stageIndex > 0
    val hasNext = stageIndex < travelData.stops.size - 1

    // Effect to ensure the current stage is synchronized with travelData
    LaunchedEffect(stageIndex, travelData.stops) {
        val stage = travelData.stops.getOrNull(stageIndex) ?: createDefaultStage()
        currentStage = stage
        startDateText = stage.startDate
        endDateText = stage.endDate

        // Reset validation errors when changing stage
        showValidationErrors = false
        locationError = false
        datesError = false
        pastDateError = false
        overlappingDatesError = false
        datesHoleDate = false
    }

    // Funzione per validare lo stage
    val validateStage = {
        locationError = currentStage.stageName.isBlank()
        datesError = currentStage.startDate.isBlank() || currentStage.endDate.isBlank()
        overlappingDatesError = false // Reset per ogni validazione
        datesHoleDate = false

        if (!datesError) {
            val currentStartMillis = dateFormatter.parse(currentStage.startDate)?.time
            val currentEndMillis = dateFormatter.parse(currentStage.endDate)?.time

            if (currentStartMillis != null && currentEndMillis != null) {
                if (stageIndex == 0 && currentStartMillis < todayInMillis) {
                    pastDateError = true
                } else if (stageIndex > 0 && travelData.stops.size > stageIndex - 1) {
                    val previousEndDateMillis =
                        travelData.stops[stageIndex - 1].endDate.takeIf { it.isNotBlank() }?.let {
                            dateFormatter.parse(it)?.time
                        }

                    if (previousEndDateMillis != null && currentStartMillis <= previousEndDateMillis) {
                        overlappingDatesError = true
                    }
                    if (previousEndDateMillis != null && currentStartMillis != previousEndDateMillis + (24 * 60 * 60 * 1000)) {
                        datesHoleDate = true
                    }
                }
            }
        }

        showValidationErrors = true

        // Restituisce true se valido, false altrimenti
        !locationError && !datesError && !pastDateError && !overlappingDatesError && !datesHoleDate
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .then(if (!isLandscape) Modifier.verticalScroll(rememberScrollState()) else Modifier)
            // Aggiunto padding alla parte inferiore per evitare che i bottoni siano coperti dalla navbar
            .padding(start = 16.dp, top = 16.dp, end = 16.dp, bottom = 72.dp)
    ) {
        Text("Stage Creation", style = MaterialTheme.typography.titleLarge)

        Spacer(modifier = Modifier.height(8.dp))
        Text(
            "Use the search bar to find the name of your stage please",
            style = MaterialTheme.typography.bodySmall
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Location",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
        )

        // Stage details form - LOCATION FIELD
        OutlinedTextField(
            value = currentStage.stageName,
            onValueChange = { locationValue ->
                val updatedStage = currentStage.copy(stageName = locationValue)
                updateStage(updatedStage)
                if (locationValue.isNotBlank()) {
                    locationError = false
                }
            },
            label = { Text("Location") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            isError = showValidationErrors && locationError,
            supportingText = {
                if (showValidationErrors && locationError) {
                    Text(
                        text = "Location is required",
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Date selection section with two text fields
        Column(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = "Stage Dates",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Start Date TextField
            OutlinedTextField(
                value = startDateText,
                onValueChange = { /* Read-only field */ },
                label = { Text("Start Date") },
                readOnly = true,
                trailingIcon = {
                    Icon(
                        Icons.Default.DateRange,
                        contentDescription = "Select dates",
                        modifier = Modifier.clickable { datePickerExpanded = true }
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { datePickerExpanded = true },
                isError = showValidationErrors && datesError || pastDateError || overlappingDatesError || datesHoleDate,
                supportingText = {
                    if (showValidationErrors && datesError) {
                        Text(
                            text = "Both start and end dates are required",
                            color = MaterialTheme.colorScheme.error
                        )
                    } else if (pastDateError) {
                        Text(
                            text = "Start date of the first stage cannot be in the past",
                            color = MaterialTheme.colorScheme.error
                        )
                    } else if (overlappingDatesError) {
                        Text(
                            text = "Start date cannot be on or before the previous stage's end date",
                            color = MaterialTheme.colorScheme.error
                        )
                    } else if (datesHoleDate) {
                        Text(
                            text = "There are some days missing from the last stage",
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
            )

            Spacer(modifier = Modifier.height(8.dp))

            // End Date TextField
            OutlinedTextField(
                value = endDateText,
                onValueChange = { /* Read-only field */ },
                label = { Text("End Date") },
                readOnly = true,
                trailingIcon = {
                    Icon(
                        Icons.Default.DateRange,
                        contentDescription = "Select dates",
                        modifier = Modifier.clickable { datePickerExpanded = true }
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { datePickerExpanded = true },
                isError = showValidationErrors && datesError,
                supportingText = {
                    if (showValidationErrors && datesError) {
                        Text(
                            text = "Both start and end dates are required",
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
            )
        }

        // Date picker card that shows when expanded
        if (datePickerExpanded) {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.onPrimary,
                    contentColor = MaterialTheme.colorScheme.onSurface
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp)
            ) {
                Column {
                    Text(
                        text = "Select Date Range",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(16.dp)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    DateRangePicker(
                        state = dateRangePickerState,
                        title = null,
                        headline = null,
                        showModeToggle = false,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(290.dp),
                        colors = DatePickerDefaults.colors(
                            containerColor = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.1f),
                            selectedDayContainerColor = MaterialTheme.colorScheme.primary,
                            selectedDayContentColor = MaterialTheme.colorScheme.onPrimary,
                            todayDateBorderColor = MaterialTheme.colorScheme.primary,
                            dayInSelectionRangeContainerColor = MaterialTheme.colorScheme.primary.copy(
                                alpha = 0.2f
                            ),
                            dayInSelectionRangeContentColor = MaterialTheme.colorScheme.primary,
                            disabledDayContentColor = Color.Gray.copy(alpha = 0.5f)
                        ),
                        dateFormatter = DatePickerDefaults.dateFormatter(),
                        // We handle date selection and validation in the "OK" button
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(
                            onClick = {
                                dateRangePickerState.setSelection(null, null)
                                startDateText = ""
                                endDateText = ""
                                updateStage(currentStage.copy(startDate = "", endDate = ""))
                                pastDateError = false
                                overlappingDatesError = false
                                datesHoleDate = false
                            }
                        ) {
                            Text("Clear")
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        TextButton(
                            onClick = {
                                val startMillis = dateRangePickerState.selectedStartDateMillis
                                val endMillis = dateRangePickerState.selectedEndDateMillis

                                if (startMillis != null && endMillis != null) {
                                    val selectedStartDate = Date(startMillis)
                                    val selectedEndDate = Date(endMillis)

                                    val formattedStartDate = dateFormatter.format(selectedStartDate)
                                    val formattedEndDate = dateFormatter.format(selectedEndDate)

                                    var isDateValid = true

                                    // Check if the selected start date is in the past for the first stage
                                    if (stageIndex == 0 && startMillis < todayInMillis) {
                                        pastDateError = true
                                        isDateValid = false
                                    } else if (stageIndex > 0 && travelData.stops.size > stageIndex - 1) {

                                        val previousEndDateMillis =
                                            travelData.stops[stageIndex - 1].endDate.takeIf { it.isNotBlank() }
                                                ?.let {
                                                    dateFormatter.parse(it)?.time
                                                }

                                        if (previousEndDateMillis != null && startMillis <= previousEndDateMillis) {
                                            overlappingDatesError = true
                                            isDateValid = false
                                        }

                                        /*
                                        if (previousEndDateMillis != null && startMillis != previousEndDateMillis + (24 * 60 * 60 * 1000)) {
                                            datesHoleDate = true
                                            isDateValid = false
                                        }
                                        */

                                    }

                                    // Check if any date in the selected range is occupied
                                    var currentDateMillis = startMillis
                                    while (currentDateMillis <= endMillis) {
                                        val formattedCurrentDate =
                                            dateFormatter.format(Date(currentDateMillis))
                                        if (occupiedDates.contains(formattedCurrentDate)) {
                                            datesError = true
                                            isDateValid = false
                                            break
                                        }
                                        currentDateMillis += 24 * 60 * 60 * 1000
                                    }

                                    if (isDateValid) {
                                        datesError = false
                                        startDateText = formattedStartDate
                                        endDateText = formattedEndDate

                                        // Update stage dates
                                        updateStage(
                                            currentStage.copy(
                                                startDate = startDateText,
                                                endDate = endDateText
                                            )
                                        )
                                        datePickerExpanded = false
                                    }
                                } else {
                                    datesError = true
                                }
                            },
                            enabled = dateRangePickerState.selectedStartDateMillis != null &&
                                    dateRangePickerState.selectedEndDateMillis != null
                        ) {
                            Text("OK")
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Switch: Free Stage
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Text(
                text = "Free stage",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f)
            )

            Spacer(modifier = Modifier.width(8.dp))

            Switch(
                checked = currentStage.freeRoaming == true,
                onCheckedChange = { isChecked ->
                    updateStage(currentStage.copy(freeRoaming = isChecked))
                },
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color.White,
                    checkedTrackColor = Color(0xFF166F5C),
                    uncheckedThumbColor = Color(0xFF166F5C),
                    uncheckedTrackColor = Color.White
                )
            )
        }

        // Add validation message when errors are present
        if (showValidationErrors && (locationError || datesError || pastDateError || overlappingDatesError || datesHoleDate)) {
            Spacer(modifier = Modifier.height(16.dp))
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    if (locationError) {
                        Text(
                            text = "Please enter a location",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onErrorContainer
                        )
                    }
                    if (datesError) {
                        Text(
                            text = "Please select valid start and end dates, ensuring no overlap with previous stages",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onErrorContainer
                        )
                    }
                    if (pastDateError) {
                        Text(
                            text = "Start date of the first stage cannot be in the past",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onErrorContainer
                        )
                    }
                    if (overlappingDatesError) {
                        Text(
                            text = "Start date cannot be on or before the previous stage's end date",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onErrorContainer
                        )
                    }
                    if (datesHoleDate) {
                        Text(
                            text = "There are some days missing in from the ending of the previous step",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onErrorContainer
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Bottoni spostati in alto, lontano dalla navbar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
                .height(48.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Previous/Back controls
            Box(
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 8.dp)
            ) {
                if (hasPrevious) {
                    OutlinedButton(
                        onClick = onPrevious,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Previous Stage")
                    }
                }
            }

            // Next/Done controls
            Box(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 8.dp)
            ) {
                if (hasNext) {
                    OutlinedButton(
                        onClick = {
                            if (validateStage()) {
                                onNext()
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Next Stage")
                    }
                } else {
                    Button(
                        onClick = {
                            if (validateStage()) {
                                val finalStages = travelData.stops.toMutableList().apply {
                                    if (stageIndex < size) {
                                        this[stageIndex] = currentStage
                                    } else {
                                        add(currentStage)
                                    }
                                }
                                onDataChange(travelData.copy(stops = finalStages))
                                onDone()
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF186B53)
                        )
                    ) {
                        Text("Done")
                    }
                }
            }
        }

        // Aggiunto spazio extra alla fine per assicurarsi che il contenuto sia completamente scrollabile
        Spacer(modifier = Modifier.height(24.dp))
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TripDescriptionStep(
    travelData: TravelProposalData,
    onDataChange: (TravelProposalData) -> Unit,
    onNext: () -> Unit,
    onBack: () -> Unit
) {
    var tripName by remember { mutableStateOf(travelData.name) }
    var selectedContinent by remember { mutableStateOf("") }
    var selectedDestination by remember { mutableStateOf(travelData.destination) }
    var description by remember { mutableStateOf(travelData.description) }
    var showDestinationDropdown by remember { mutableStateOf(false) }

    var tripNameError by remember { mutableStateOf(false) }
    var destinationError by remember { mutableStateOf(false) }
    var descriptionError by remember { mutableStateOf(false) }
    var showValidationErrors by remember { mutableStateOf(false) }

    // Get available destinations based on selected continent
    val availableDestinations = if (selectedContinent.isNotEmpty()) {
        continentToCountries[selectedContinent.lowercase()] ?: emptyList()
    } else {
        continentToCountries.values.flatten()
    }

    val validateFields = {
        tripNameError = tripName.isBlank() || tripName.length < 3
        destinationError = selectedDestination.isBlank()
        descriptionError = description.isBlank() || description.length >= 400
        showValidationErrors = true

        !tripNameError && !destinationError && !descriptionError
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 16.dp)
            .padding(bottom = 50.dp)
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
        ) {

            // Trip name
            Text("Trip name", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
            OutlinedTextField(
                value = tripName,
                onValueChange = {
                    tripName = it
                    onDataChange(travelData.copy(name = it))
                    if (it.isNotBlank()) tripNameError = false
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                isError = showValidationErrors && tripNameError,
                supportingText = {
                    if (showValidationErrors && tripNameError) {
                        val message = when {
                            tripName.isBlank() -> "Trip name is required"
                            tripName.length < 3 -> "Trip name must be at least 3 characters"
                            else -> ""
                        }
                        Text(message, color = MaterialTheme.colorScheme.error)
                    }
                }
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Continent filter (optional)
            Text(
                "Filter by continent (optional)",
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray
            )
            LazyRow(
                modifier = Modifier.padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    FilterChip(
                        onClick = {
                            selectedContinent = ""
                            selectedDestination = ""
                        },
                        label = { Text("All") },
                        selected = selectedContinent.isEmpty()
                    )
                }
                items(continentToCountries.keys.toList()) { continent ->
                    FilterChip(
                        onClick = {
                            selectedContinent = continent
                            selectedDestination = ""
                        },
                        label = { Text(continent.replaceFirstChar { it.uppercase() }) },
                        selected = selectedContinent == continent
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Destination field with dropdown
            Text("Destination", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
            ExposedDropdownMenuBox(
                expanded = showDestinationDropdown,
                onExpandedChange = { showDestinationDropdown = !showDestinationDropdown }
            ) {
                OutlinedTextField(
                    value = selectedDestination,
                    onValueChange = { },
                    readOnly = true,
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = showDestinationDropdown)
                    },
                    placeholder = { Text("Select destination") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(),
                    shape = RoundedCornerShape(8.dp),
                    isError = showValidationErrors && destinationError,
                    supportingText = {
                        if (showValidationErrors && destinationError) {
                            Text(
                                "Please select a destination",
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                )

                ExposedDropdownMenu(
                    expanded = showDestinationDropdown,
                    onDismissRequest = { showDestinationDropdown = false }
                ) {
                    availableDestinations.forEach { destination ->
                        DropdownMenuItem(
                            text = { Text(destination) },
                            onClick = {
                                selectedDestination = destination
                                onDataChange(travelData.copy(destination = destination))
                                showDestinationDropdown = false
                                if (destination.isNotBlank()) destinationError = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Description
            Text("Description", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
            OutlinedTextField(
                value = description,
                onValueChange = {
                    description = it
                    onDataChange(travelData.copy(description = it))
                    if (it.isNotBlank()) descriptionError = false
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 120.dp),
                shape = RoundedCornerShape(8.dp),
                isError = showValidationErrors && descriptionError,
                supportingText = {
                    if (showValidationErrors && descriptionError) {
                        val message = when {
                            description.isBlank() -> "Description is required"
                            description.length >= 400 -> "Description must be less than 400 characters"
                            else -> ""
                        }
                        Text(message, color = MaterialTheme.colorScheme.error)
                    }
                }
            )

//            Text(
//                "Describe your trip",
//                style = MaterialTheme.typography.bodySmall,
//                color = Color.Gray,
//                modifier = Modifier.padding(top = 4.dp)
//            )

            if (showValidationErrors && (tripNameError || destinationError || descriptionError)) {
                Spacer(modifier = Modifier.height(16.dp))
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Please fill in all required fields before proceeding",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Navigation buttons
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
                .height(48.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            OutlinedButton(
                onClick = onBack,
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 8.dp)
            ) {
                Text("Back")
            }

            Button(
                onClick = {
                    if (validateFields()) {
                        onNext()
                    }
                },
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF186B53))
            ) {
                Text("Next")
            }
        }
    }
}

// New TripImagesStep function
@Composable
fun TripImagesStep(
    travelData: TravelProposalData,
    onDataChange: (TravelProposalData) -> Unit,
    onNext: () -> Unit,
    onBack: () -> Unit
) {
    var imageUrls by remember { mutableStateOf(travelData.images.map { it.url }) }
    val context = LocalContext.current

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetMultipleContents()
    ) { uris: List<Uri> ->
        if (uris.isNotEmpty()) {
            val newUrls = uris.map { it.toString() }
            imageUrls = imageUrls + newUrls
            onDataChange(
                travelData.copy(images = imageUrls.map { TripPhoto(it) })
            )
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 16.dp)
            .padding(bottom = 50.dp)
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
        ) {
            Text(
                text = "Add Trip Images",
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Text(
                text = "Add photos to showcase your travel experience. You can select multiple images.",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray,
                modifier = Modifier.padding(bottom = 24.dp)
            )

            // Select images button
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { galleryLauncher.launch("image/*") },
                colors = CardDefaults.cardColors(containerColor = Color(0xFFE0F2E9)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        Icons.Default.AddPhotoAlternate,
                        contentDescription = "Select Images",
                        tint = Color(0xFF186B53),
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        "Select Images",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color(0xFF186B53)
                    )
                    Text(
                        "Choose photos from your gallery",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }

            // Show selected images
            if (imageUrls.isNotEmpty()) {
                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "Selected Images (${imageUrls.size})",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier.heightIn(max = 400.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(imageUrls.size) { index ->
                        Box(
                            modifier = Modifier.aspectRatio(1f)
                        ) {
                            Image(
                                painter = rememberAsyncImagePainter(imageUrls[index]),
                                contentDescription = "Trip image ${index + 1}",
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clip(RoundedCornerShape(8.dp)),
                                contentScale = ContentScale.Crop
                            )

                            // Remove button
                            IconButton(
                                onClick = {
                                    imageUrls = imageUrls.toMutableList().apply { removeAt(index) }
                                    onDataChange(travelData.copy(images = imageUrls.map {
                                        TripPhoto(
                                            it
                                        )
                                    }))
                                },
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .padding(4.dp)
                                    .background(
                                        Color.Black.copy(alpha = 0.6f),
                                        CircleShape
                                    )
                                    .size(32.dp)
                            ) {
                                Icon(
                                    Icons.Default.Close,
                                    contentDescription = "Remove image",
                                    tint = Color.White,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    "Tip: You can add more images or remove unwanted ones",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            } else {
                Spacer(modifier = Modifier.height(24.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Text(
                        text = "No images selected yet. Images are optional, but they help showcase your travel experience!",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
        }

        //Spacer(modifier = Modifier.height(16.dp))

        // Navigation buttons
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
                .height(48.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            OutlinedButton(
                onClick = onBack,
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 8.dp)
            ) {
                Text("Back")
            }

            Button(
                onClick = onNext,
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF186B53))
            ) {
                Text("Next")
            }
        }
    }
}

@Composable
fun SuggestedActivityStep(
    travelData: TravelProposalData,
    onDataChange: (TravelProposalData) -> Unit,
    onNext: () -> Unit,
    onBack: () -> Unit
) {
    var selectedActivities by remember { mutableStateOf(travelData.suggestedActivities) }
    var newActivityValue by remember { mutableStateOf("") }
    var allAvailableActivities by remember {
        mutableStateOf(
            listOf(
                // Adventure activities
                "Surfing", "MTB", "Climbing", "Hiking", "Kayaking", "Paragliding", "Scuba Diving",
                "Snorkeling", "Zip-line", "Rafting", "Bungee Jumping", "Rock Climb", "Canyoning",
                "Kitesurfing", "Windsurf", "Sailing", "Caving", "Skydiving", "Snowboarding",
                // Less adventurous activities
                "Sightseeing", "Photo", "Cooking Class", "Wine Tasting", "Shopping", "Spa",
                "Museum Visit", "City Tour", "Beach Relax", "Bird Watch", "Yoga", "Meditation",
                "Picnic", "Local Cuisine", "Sunset Watching", "Boat Trip", "Cultural Tour",
                "Theatre", "Concert", "Fishing", "Golf", "Tennis", "Swimming", "Walking Tour"
            )
        )
    }

    val mostUsedActivities = listOf("Sightseeing", "Swimming", "Local Cuisine", "Hiking")

    val activityListState = rememberLazyListState()

    LaunchedEffect(selectedActivities.size) {
        if (selectedActivities.isNotEmpty()) {
            activityListState.animateScrollToItem(selectedActivities.lastIndex)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .padding(bottom = 50.dp) // Aggiungi padding per la navigation bar
    ) {
        if (selectedActivities.isEmpty()) {
            Text("Select activities for your trip:", style = MaterialTheme.typography.bodyLarge)

            Spacer(modifier = Modifier.height(16.dp))
        }


        // Campo per aggiungere nuove attività
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = newActivityValue,
                onValueChange = { newActivityValue = it },
                label = { Text("Add new activity") },
                modifier = Modifier.weight(1f),
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(onDone = {
                    if (newActivityValue.isNotBlank()) {
                        val newActivity =
                            newActivityValue.trim().replaceFirstChar { it.uppercase() }
                        // Aggiungi alle attività selezionate
                        selectedActivities = selectedActivities + newActivity
                        // Aggiungi alla lista di tutte le attività se non esiste già
                        if (!allAvailableActivities.contains(newActivity)) {
                            allAvailableActivities = allAvailableActivities + newActivity
                        }
                        onDataChange(travelData.copy(suggestedActivities = selectedActivities))
                        newActivityValue = ""
                    }
                })
            )

            Spacer(modifier = Modifier.width(8.dp))

            Button(
                onClick = {
                    if (newActivityValue.isNotBlank()) {
                        val newActivity =
                            newActivityValue.trim().replaceFirstChar { it.uppercase() }
                        // Aggiungi alle attività selezionate
                        selectedActivities = selectedActivities + newActivity
                        // Aggiungi alla lista di tutte le attività se non esiste già
                        if (!allAvailableActivities.contains(newActivity)) {
                            allAvailableActivities = allAvailableActivities + newActivity
                        }
                        onDataChange(travelData.copy(suggestedActivities = selectedActivities))
                        newActivityValue = ""
                    }
                }
            ) {
                Text("Add")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (selectedActivities.isNotEmpty()) {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFFCEE9DB)),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp) // Altezza fissa per la card
            ) {
                Column(
                    modifier = Modifier
                        .padding(start = 16.dp, end = 16.dp, top = 16.dp)
                ) {
                    Text("Selected activities:", style = MaterialTheme.typography.bodyMedium)
                    //Spacer(modifier = Modifier.height(8.dp))

                    // Area scorrevole per le attività selezionate
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                    ) {
                        LazyRow(
                            state = activityListState,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(selectedActivities) { activity ->
                                AssistChip(
                                    onClick = {
                                        selectedActivities = selectedActivities - activity
                                        onDataChange(travelData.copy(suggestedActivities = selectedActivities))
                                    },
                                    label = { Text(activity) },
                                    colors = AssistChipDefaults.assistChipColors(
                                        containerColor = Color(0xFFFFFFFF),
                                        labelColor = Color.Black,
                                        leadingIconContentColor = Color.Black
                                    ),
                                    leadingIcon = {
                                        Icon(
                                            imageVector = Icons.Default.Close,
                                            contentDescription = "Remove activity",
                                            modifier = Modifier.size(16.dp)
                                        )
                                    },
                                    modifier = Modifier.padding(end = 4.dp)
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
        }

        // Area scorrevole per le attività disponibili - riduciamo il peso per far spazio ai bottoni
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
        ) {
            // Most Frequently Used
//            Text("Most frequently used", style = MaterialTheme.typography.bodyMedium)
//            Spacer(modifier = Modifier.height(8.dp))
//
//            Row(
//                modifier = Modifier.fillMaxWidth(),
//                horizontalArrangement = Arrangement.spacedBy(8.dp)
//            ) {
//                mostUsedActivities.forEach { activity ->
//                    val isSelected = selectedActivities.contains(activity)
//                    FilterChip(
//                        selected = isSelected,
//                        onClick = {
//                            selectedActivities = if (isSelected) {
//                                selectedActivities - activity
//                            } else {
//                                selectedActivities + activity
//                            }
//                            onDataChange(travelData.copy(suggestedActivities = selectedActivities))
//                        },
//                        label = { Text(activity) },
//                        modifier = Modifier.padding(vertical = 4.dp)
//                    )
//                }
//            }
//
            Spacer(modifier = Modifier.height(12.dp))

            // All Activities
            Text("All activities", style = MaterialTheme.typography.bodyMedium)
            Spacer(modifier = Modifier.height(8.dp))

            allAvailableActivities.chunked(3).forEach { rowActivities ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    rowActivities.forEach { activity ->
                        val isSelected = selectedActivities.contains(activity)
                        FilterChip(
                            selected = isSelected,
                            onClick = {
                                selectedActivities = if (isSelected) {
                                    selectedActivities - activity
                                } else {
                                    selectedActivities + activity
                                }
                                onDataChange(travelData.copy(suggestedActivities = selectedActivities))
                            },
                            label = { Text(activity) },
                            modifier = Modifier.padding(vertical = 4.dp),
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = Color(0xFFCEE9DB)
                            )
                        )
                    }
                }
            }
        }

        // Spacer esplicito prima dei bottoni
        Spacer(modifier = Modifier.height(8.dp))

        // Navigation Buttons - stile migliorato
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 2.dp)
                .height(48.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            OutlinedButton(
                onClick = onBack,
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 8.dp)
            ) {
                Text("Back")
            }

            Button(
                onClick = onNext,
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 8.dp)
            ) {
                Text("Next")
            }
        }
    }
}

@Composable
fun LastDetailsStep(
    travelData: TravelProposalData,
    onDataChange: (TravelProposalData) -> Unit,
    onComplete: () -> Unit,
    onBack: () -> Unit,
    tripsViewModel: TripsViewModel
) {
    val initialBudgetStart = 500f
    val initialBudgetEnd = 1500f

    var budgetRange by remember { mutableStateOf(initialBudgetStart..initialBudgetEnd) }

    var maxParticipants by remember { mutableIntStateOf(travelData.maxParticipants) }
    var participantsError by remember { mutableStateOf(false) }

    var girlsOnly by remember { mutableStateOf(travelData.filters.contains(Filter.GIRLS_ONLY)) }
    var lgbtqFriendly by remember { mutableStateOf(travelData.filters.contains(Filter.LGBTQ_FRIENDLY)) }

    val initialAgeStart = travelData.ageRange.min.toFloat()
    val initialAgeEnd = travelData.ageRange.max.toFloat()
    var ageRange by remember { mutableStateOf(initialAgeStart..initialAgeEnd) }

    var showValidationErrors by remember { mutableStateOf(false) }
    var showConfirmation by remember { mutableStateOf(false) }

    val validateFields = {
        participantsError = maxParticipants <= 0
        !participantsError
    }

    if (showConfirmation) {
        val updatedFilters = mutableListOf<Filter>().apply {
            if (girlsOnly) add(Filter.GIRLS_ONLY)
            if (lgbtqFriendly) add(Filter.LGBTQ_FRIENDLY)
        }

        val updatedTravelData = travelData.copy(
            priceEstimation = PriceRange(
                min = budgetRange.start.toInt(),
                max = budgetRange.endInclusive.toInt()
            ),
            maxParticipants = maxParticipants,
            filters = updatedFilters,
            ageRange = AgeRange(
                min = ageRange.start.toInt(),
                max = ageRange.endInclusive.toInt()
            )
        )

        TravelSummaryConfirmation(
            travelData = updatedTravelData,
            ageRange = ageRange,
            girlsOnly = girlsOnly,
            lgbtqFriendly = lgbtqFriendly,
            onBack = { showConfirmation = false },

            onConfirm = {
                // Update data with final values before completing
                onDataChange(updatedTravelData)
                // Call the completion handler
                onComplete()
            },
            tripsViewModel = tripsViewModel
        )
    } else {
        // Scrollable content without duplicate header
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 16.dp)
        ) {
            Text(
                text = "Budget range (€)",
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.SemiBold
            )

            RangeSlider(
                value = budgetRange,
                onValueChange = {
                    budgetRange = it
                    onDataChange(
                        travelData.copy(
                            priceEstimation = PriceRange(
                                min = it.start.toInt(),
                                max = it.endInclusive.toInt()
                            )
                        )
                    )
                },
                valueRange = 0f..10000f,
                steps = 99,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp),
                colors = SliderDefaults.colors(
                    thumbColor = Color(0xFF186B53),
                    activeTrackColor = Color(0xFF186B53).copy(alpha = 0.3f),
                    inactiveTrackColor = Color(0xFF186B53).copy(alpha = 0.1f)
                )
            )



            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .padding(horizontal = 8.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = "€${budgetRange.start.toInt()}",
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .padding(horizontal = 8.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = "€${budgetRange.endInclusive.toInt()}",
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Max # people",
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = maxParticipants.toString(),
                onValueChange = {
                    val parsedValue = it.toIntOrNull() ?: 0
                    maxParticipants = parsedValue
                    onDataChange(travelData.copy(maxParticipants = parsedValue))
                    if (parsedValue > 0) {
                        participantsError = false
                    }
                },
                label = { Text("Max # people") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                isError = showValidationErrors && participantsError,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF186B53),
                    cursorColor = Color(0xFF186B53)
                ),
                supportingText = {
                    if (showValidationErrors && participantsError) {
                        Text(
                            text = "Please enter a valid number of participants",
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Age range",
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(Modifier.height(8.dp))

            RangeSlider(
                value = ageRange,
                onValueChange = {
                    ageRange = it
                    onDataChange(
                        travelData.copy(
                            ageRange = AgeRange(
                                min = it.start.toInt(),
                                max = it.endInclusive.toInt()
                            )
                        )
                    )
                },
                valueRange = 0f..100f,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp),
                colors = SliderDefaults.colors(
                    thumbColor = Color(0xFF186B53),
                    activeTrackColor = Color(0xFF186B53).copy(alpha = 0.3f),
                    inactiveTrackColor = Color(0xFF186B53).copy(alpha = 0.1f)
                )
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .padding(horizontal = 8.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = ageRange.start.toInt().toString(),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .padding(horizontal = 8.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = ageRange.endInclusive.toInt().toString(),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Girls only", modifier = Modifier.weight(1f))
                Switch(
                    checked = girlsOnly,
                    onCheckedChange = {
                        girlsOnly = it
                        val updatedFilters = travelData.filters.toMutableList().apply {
                            if (it && !contains(Filter.GIRLS_ONLY)) {
                                add(Filter.GIRLS_ONLY)
                            } else if (!it) {
                                remove(Filter.GIRLS_ONLY)
                            }
                        }
                        onDataChange(travelData.copy(filters = updatedFilters))
                    },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = Color(0xFF166F5C),
                        uncheckedThumbColor = Color(0xFF166F5C),
                        uncheckedTrackColor = Color.White
                    )
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("LGBTQIA+ friendly", modifier = Modifier.weight(1f))
                Switch(
                    checked = lgbtqFriendly,
                    onCheckedChange = {
                        lgbtqFriendly = it
                        val updatedFilters = travelData.filters.toMutableList().apply {
                            if (it && !contains(Filter.LGBTQ_FRIENDLY)) {
                                add(Filter.LGBTQ_FRIENDLY)
                            } else if (!it) {
                                remove(Filter.LGBTQ_FRIENDLY)
                            }
                        }
                        onDataChange(travelData.copy(filters = updatedFilters))
                    },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = Color(0xFF166F5C),
                        uncheckedThumbColor = Color(0xFF166F5C),
                        uncheckedTrackColor = Color.White
                    )
                )
            }

            // Submit button at the end of the form
            Button(
                onClick = {
                    if (validateFields()) {
                        showConfirmation = true
                    } else {
                        showValidationErrors = true
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp)
                    .height(48.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF186B53)
                )
            ) {
                Text("Create Travel Proposal")
            }

            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}

@Composable
fun TravelSummaryConfirmation(
    travelData: TravelProposalData,
    ageRange: ClosedFloatingPointRange<Float>,
    girlsOnly: Boolean,
    lgbtqFriendly: Boolean,
    onBack: () -> Unit,
    onConfirm: () -> Unit,
    tripsViewModel: TripsViewModel
) {
    val startDate = travelData.stops.firstOrNull()?.startDate
    val endDate = travelData.stops.lastOrNull()?.endDate

    val updatedProposal = travelData.copy(
        startDate = startDate.toString(),
        endDate = endDate.toString()
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 12.dp)
            .padding(bottom = 50.dp)
    ) {
        Text(
            "Confirm Your Trip",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(16.dp))

        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(buildAnnotatedString {
                    withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                        append("Name of the trip: ")
                    }
                    append(travelData.name)
                })
                Spacer(modifier = Modifier.height(3.dp))

                Text(buildAnnotatedString {
                    withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                        append("Dates of the trip: ")
                    }
                    append("$startDate - $endDate")
                })
                Spacer(modifier = Modifier.height(3.dp))

                Text(buildAnnotatedString {
                    withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                        append("Type: ")
                    }
                    append(travelData.type.name)
                })
                Spacer(modifier = Modifier.height(3.dp))

                Text(buildAnnotatedString {
                    withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                        append("Tags: ")
                    }
                    append(travelData.tags.joinToString(", "))
                })
                Spacer(modifier = Modifier.height(3.dp))

                travelData.suggestedActivities.takeIf { it.isNotEmpty() }?.let {
                    Text(buildAnnotatedString {
                        withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                            append("Activities: ")
                        }
                        append(it.joinToString(", "))
                    })
                    Spacer(modifier = Modifier.height(3.dp))
                }

                Text(buildAnnotatedString {
                    withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                        append("Stages: ")
                    }
                    append(travelData.stops.size.toString())
                })
                Spacer(modifier = Modifier.height(3.dp))

                Text(buildAnnotatedString {
                    withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                        append("First location: ")
                    }
                    append(travelData.stops.firstOrNull()?.stageName ?: "Not specified")
                })
                Spacer(modifier = Modifier.height(3.dp))

                val filterDisplayText = buildString {
                    if (girlsOnly) append("Girls only, ")
                    if (lgbtqFriendly) append("LGBTQIA+ friendly, ")
                    append("Age ${ageRange.start.toInt()}-${ageRange.endInclusive.toInt()} years")
                }

                Text(buildAnnotatedString {
                    withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                        append("Filters: ")
                    }
                    append(filterDisplayText)
                })
                Spacer(modifier = Modifier.height(3.dp))

                Text(buildAnnotatedString {
                    withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                        append("Budget: ")
                    }
                    append("${travelData.priceEstimation.min} - ${travelData.priceEstimation.max}")
                })
                Spacer(modifier = Modifier.height(3.dp))

                Text(buildAnnotatedString {
                    withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                        append("Max Participants: ")
                    }
                    append(travelData.maxParticipants.toString())
                })
            }

            Spacer(modifier = Modifier.weight(1f))

        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
                .height(48.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            OutlinedButton(
                onClick = onBack,
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 8.dp)
            ) {
                Text("Back")
            }

            Button(
                onClick = {
                    println("[DEBUG] Create button clicked")
                    println("[DEBUG] Proposal: $updatedProposal")

                    val newTrip = convertToTrip(updatedProposal)
                    println("[DEBUG] Converted to Trip: ${newTrip.creator}")

                    tripsViewModel.validateAndCreate(newTrip)
                    println("[DEBUG] validateAndCreate called")

                    println("[DEBUGNewTrip] Trip dates: ${newTrip.startDate} to ${newTrip.endDate}")
                    println("[DEBUGTravelData] Travel dates: ${updatedProposal.startDate} to ${updatedProposal.endDate}")

                    onConfirm()
                    println("[DEBUG] onConfirm callback executed")
                },
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 8.dp)
            ) {
                Text("Create")
            }
        }
    }
}


fun convertToTrip(proposal: TravelProposalData): Trip {
    println("[DEBUG] Converting proposal to trip")
    return Trip(
        tripId = 0,
        name = proposal.name,
        type = proposal.type,
        destination = proposal.destination,
        creator = proposal.creator,
        numParticipants = proposal.numParticipants,
        maxParticipants = proposal.maxParticipants,
        reservationsList = listOf(),
        startDate = proposal.startDate,
        endDate = proposal.endDate,
        images = proposal.images,
        tags = proposal.tags,
        description = proposal.description,
        stops = proposal.stops,
        priceEstimation = proposal.priceEstimation,
        suggestedActivities = proposal.suggestedActivities,
        filters = proposal.filters,
        ageRange = proposal.ageRange,
        reviews = proposal.reviews ?: listOf(),
        favoritesUsers = proposal.favoritesUsers ?: listOf()
    ).also {
        println("[DEBUG] Converted trip: $it")
    }
}
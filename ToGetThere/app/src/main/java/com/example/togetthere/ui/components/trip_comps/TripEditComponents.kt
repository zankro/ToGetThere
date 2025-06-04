package com.example.togetthere.ui.components.trip_comps

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.outlined.Groups
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DateRangePicker
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.RangeSlider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDateRangePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.example.togetthere.R
import com.example.togetthere.model.PriceRange
import com.example.togetthere.model.Stage
import com.example.togetthere.model.TripPhoto
import com.example.togetthere.model.TripType
import com.example.togetthere.ui.theme.CustomTheme
import com.example.togetthere.utils.convertDateToMillis
import com.example.togetthere.utils.convertMillisToDate
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import kotlin.math.roundToInt

@Composable
fun TGT_TextInput(modifier: Modifier = Modifier, title: String, error: String? = null, subtitle: String? = null, value: String, minLines: Int = 5, onValueChange: (String) -> Unit) {
    Column(
        modifier = modifier
            .clip(shape = RoundedCornerShape(5.dp))
            .background(color = MaterialTheme.colorScheme.surfaceVariant)
            .fillMaxWidth()
            .padding(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)

    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(text = title, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Medium)
                if(!subtitle.isNullOrEmpty()) {
                    Text(text = subtitle, style = CustomTheme.typography.subTitleSmall)
                }
            }

            Box {
                Text(
                    text = error ?: "",
                    style = CustomTheme.typography.labelVariant,
                    color = if (error != null) MaterialTheme.colorScheme.error else Color.Transparent,
                    modifier = Modifier
                        .shadow(elevation = if (error != null) 2.dp else 0.dp, shape = RoundedCornerShape(4.dp))
                        .clip(RoundedCornerShape(4.dp))
                        .background(
                            color = if (error != null) MaterialTheme.colorScheme.errorContainer else Color.Transparent,
                            shape = RoundedCornerShape(4.dp)
                        )
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }

        }

//        BasicTextField(
//            value = value,
//            onValueChange = onValueChange,
//            minLines = minLines,
//            maxLines = 5,
//            modifier = Modifier
//                .clip(shape = RoundedCornerShape(4.dp))
//                .background(color = MaterialTheme.colorScheme.surface)
//                .fillMaxWidth()
//                .padding(2.dp)
//
//        )

        OutlinedTextField(
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = MaterialTheme.colorScheme.onSurface,
                unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                focusedBorderColor = MaterialTheme.colorScheme.outline,
                unfocusedBorderColor = MaterialTheme.colorScheme.surface,
                focusedContainerColor = MaterialTheme.colorScheme.surface,
                unfocusedContainerColor = MaterialTheme.colorScheme.surface
            ),
            textStyle = TextStyle(
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.Normal,
                fontSize = MaterialTheme.typography.bodyMedium.fontSize
            ),
            value = value,
            onValueChange = onValueChange,
            minLines = minLines,
            maxLines = 5,
            modifier = Modifier
                .fillMaxWidth()
        )
    }
}

@Composable
fun TGT_DatesPicker(start: String, end: String, error: String?, showPicker: () -> Unit = {},onValueChange: (String) -> Unit = {}) {
    Column(
        modifier = Modifier
            .clip(shape = RoundedCornerShape(5.dp))
            .background(color = MaterialTheme.colorScheme.surfaceVariant)
            .fillMaxWidth()
            .padding(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)

    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "Travel Dates", style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Medium)

            Box {
                Text(
                    text = error ?: "",
                    style = CustomTheme.typography.labelVariant,
                    color = if (error != null) MaterialTheme.colorScheme.error else Color.Transparent,
                    modifier = Modifier
                        .shadow(elevation = if (error != null) 2.dp else 0.dp, shape = RoundedCornerShape(4.dp))
                        .clip(RoundedCornerShape(4.dp))
                        .background(
                            color = if (error != null) MaterialTheme.colorScheme.errorContainer else Color.Transparent,
                            shape = RoundedCornerShape(4.dp)
                        )
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }

        }

        Row(
            modifier = Modifier
                .clip(shape = RoundedCornerShape(4.dp))
                .fillMaxWidth()
                .background(color = MaterialTheme.colorScheme.surface),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                modifier = Modifier
                    .padding(start = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(32.dp)
            ) {
                Column {
                    Text(
                        text = "Start",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = start,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium
                    )
                }

                Column {
                    Text(
                        text = "End",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = end,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            FilledTonalButton(
                modifier = Modifier.padding(end = 4.dp),
                colors = ButtonDefaults.filledTonalButtonColors(
                    contentColor = MaterialTheme.colorScheme.primary,
                    containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
                ),
                contentPadding = ButtonDefaults.ButtonWithIconContentPadding,
                onClick = showPicker,
                shape = RoundedCornerShape(2.dp) // oppure CutCornerShape, o qualsiasi altro
            ) {
                Icon(
                    imageVector = Icons.Default.DateRange,
                    contentDescription = "Dates",
                    modifier = Modifier.size(ButtonDefaults.IconSize)
                )
                Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                Text("Change")
            }

        }
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DateRangePickerModal(
    initialStartDateMillis: Long?,
    initialEndDateMillis: Long?,
    onDateRangeSelected: (Pair<Long?, Long?>) -> Unit,
    onDismiss: () -> Unit
) {
    val dateRangePickerState = rememberDateRangePickerState(
        initialSelectedStartDateMillis = initialStartDateMillis,
        initialSelectedEndDateMillis = initialEndDateMillis
    )

    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = {
                    onDateRangeSelected(
                        Pair(
                            dateRangePickerState.selectedStartDateMillis,
                            dateRangePickerState.selectedEndDateMillis
                        )
                    )
                    onDismiss()
                }
            ) {
                Text("OK")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    ) {
        DateRangePicker(
            state = dateRangePickerState,
            title = {
                Text(
                    text = "Select date range"
                )
            },
            showModeToggle = false,
            modifier = Modifier
                .fillMaxWidth()
                .height(500.dp)
                .padding(16.dp)
        )
    }
}

@Composable
fun TGT_TripTypePicker(
    modifier: Modifier = Modifier,
    selectedTripType: TripType,
    onTripTypeSelected: (TripType) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .clip(RoundedCornerShape(5.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .fillMaxWidth()
            .padding(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(text = "Trip Type", style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Medium)

        Row(
            modifier = Modifier
                .clip(RoundedCornerShape(4.dp))
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surface),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.padding(start = 8.dp, top = 8.dp, bottom = 8.dp)
            ) {
                Text(
                    text = "Selected",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = selectedTripType.displayName,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium
                )
            }

            Box {
                FilledTonalButton(
                    modifier = Modifier.padding(end = 4.dp),
                    colors = ButtonDefaults.filledTonalButtonColors(
                        contentColor = MaterialTheme.colorScheme.primary,
                        containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
                    ),
                    contentPadding = ButtonDefaults.ButtonWithIconContentPadding,
                    onClick = { expanded = true },
                    shape = RoundedCornerShape(2.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit, // puoi cambiare icona se vuoi
                        contentDescription = "Change Trip Type",
                        modifier = Modifier.size(ButtonDefaults.IconSize)
                    )
                    Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                    Text("Change")
                }

                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    TripType.entries.forEach { type ->
                        DropdownMenuItem(
                            text = { Text(type.displayName) },
                            onClick = {
                                onTripTypeSelected(type)
                                expanded = false
                            }
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun TGT_TagsEditor(
    tags: List<String>,
    error: String? = null,
    onTagsChange: (List<String>) -> Unit
) {
    var newTag by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(5.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .fillMaxWidth()
            .padding(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Tags", style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Medium)

            Box {
                Text(
                    text = error ?: "",
                    style = CustomTheme.typography.labelVariant,
                    color = if (error != null) MaterialTheme.colorScheme.error else Color.Transparent,
                    modifier = Modifier
                        .shadow(elevation = if (error != null) 2.dp else 0.dp, shape = RoundedCornerShape(4.dp))
                        .clip(RoundedCornerShape(4.dp))
                        .background(
                            color = if (error != null) MaterialTheme.colorScheme.errorContainer else Color.Transparent,
                            shape = RoundedCornerShape(4.dp)
                        )
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }

        }

        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Start,
            verticalArrangement = Arrangement.Top,
        ) {
            tags.forEach { tag ->
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = MaterialTheme.colorScheme.surfaceContainerLow,
                    tonalElevation = 2.dp,
                    modifier = Modifier
                        .padding(end = 8.dp, bottom = 8.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = tag,
                            style = CustomTheme.typography.labelVariant,
                        )
                        Spacer(Modifier.width(8.dp))
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Remove tag",
                            tint = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier
                                .size(16.dp)
                                .clickable {
                                    onTagsChange(tags - tag)
                                }
                        )
                    }
                }
            }
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            OutlinedTextField(
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = MaterialTheme.colorScheme.onSurface,
                    unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                    focusedBorderColor = MaterialTheme.colorScheme.outline,
                    unfocusedBorderColor = MaterialTheme.colorScheme.surface,
                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface
                ),
                value = newTag,
                onValueChange = { newTag = it },
                placeholder = { Text("Awesome tag") },  // ToDo: Gamificate by generating a random placeholder
                modifier = Modifier
                    .weight(1f),
                singleLine = true,
                trailingIcon = {
                    FilledTonalButton(
                        modifier = Modifier.padding(end = 4.dp),
                        colors = ButtonDefaults.filledTonalButtonColors(
                            contentColor = MaterialTheme.colorScheme.primary,
                            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
                        ),
                        contentPadding = ButtonDefaults.ButtonWithIconContentPadding,
                        onClick = {
                            val trimmedTag = newTag.trim()
                            if (trimmedTag.isNotEmpty() && !tags.contains(trimmedTag)) {
                                onTagsChange(tags + trimmedTag)
                                newTag = ""  // Clear the input after adding the tag
                            }
                        },
                        shape = RoundedCornerShape(2.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Add Tag",
                            modifier = Modifier.size(ButtonDefaults.IconSize)
                        )
                        Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                        Text("Add")
                    }
                }
            )

        }
    }
}

//@Composable
//fun TGT_StopsInput(
//    stops: List<Stage>,
//    onStopsChange: (List<Stage>) -> Unit
//) {
//    var currentInput by remember { mutableStateOf("") }
//
//    Column(
//        modifier = Modifier
//            .fillMaxWidth()
//            .clip(RoundedCornerShape(8.dp))
//            .background(MaterialTheme.colorScheme.surfaceVariant)
//            .padding(8.dp),
//        verticalArrangement = Arrangement.spacedBy(8.dp)
//    ) {
//        Text("Itinerary", style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Medium)
//
//        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
//            stops.forEachIndexed { index, stop ->
//                Row(
//                    modifier = Modifier
//                        .clip(RoundedCornerShape(4.dp))
//                        .fillMaxWidth()
//                        .background(MaterialTheme.colorScheme.surfaceContainerLow)
//                        .padding(8.dp),
//                    verticalAlignment = Alignment.CenterVertically,
//                    horizontalArrangement = Arrangement.SpaceBetween
//                ) {
//                    Column(modifier = Modifier.weight(1f)) {
//                        Text(
//                            text = stop.stageName,
//                            style = MaterialTheme.typography.bodyMedium,
//                            fontWeight = FontWeight.Medium
//                        )
//                        Text(
//                            text = if (stop.freeRoaming) "Free Roam" else "Group Stop",
//                            style = MaterialTheme.typography.labelSmall,
//                            color = MaterialTheme.colorScheme.onSurfaceVariant
//                        )
//                    }
//
//                    Row {
//                        IconButton(onClick = {
//                            val updated = stops.toMutableList()
//                            updated[index] = stop.copy(freeRoaming = !stop.freeRoaming)
//                            onStopsChange(updated)
//                        }) {
//                            Icon(
//                                imageVector = if (stop.freeRoaming) Icons.Outlined.Groups else Icons.Default.Groups,
//                                contentDescription = "Toggle group"
//                            )
//                        }
//
//                        IconButton(onClick = {
//                            onStopsChange(stops.toMutableList().also { it.removeAt(index) })
//                        }) {
//                            Icon(Icons.Default.Close, contentDescription = "Remove stop")
//                        }
//                    }
//                }
//            }
//        }
//
//        OutlinedTextField(
//            colors = OutlinedTextFieldDefaults.colors(
//                focusedTextColor = MaterialTheme.colorScheme.onSurface,
//                unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
//                focusedBorderColor = MaterialTheme.colorScheme.outline,
//                unfocusedBorderColor = MaterialTheme.colorScheme.surface,
//                focusedContainerColor = MaterialTheme.colorScheme.surface,
//                unfocusedContainerColor = MaterialTheme.colorScheme.surface
//            ),
//            value = currentInput,
//            onValueChange = { currentInput = it },
//            placeholder = { Text("Awesome place") },
//            modifier = Modifier
//                .fillMaxWidth(),
//            singleLine = true,
//            trailingIcon = {
//                FilledTonalButton(
//                    modifier = Modifier.padding(end = 4.dp),
//                    colors = ButtonDefaults.filledTonalButtonColors(
//                        contentColor = MaterialTheme.colorScheme.primary,
//                        containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
//                    ),
//                    contentPadding = ButtonDefaults.ButtonWithIconContentPadding,
//                    onClick = {
//                        if (currentInput.isNotBlank()) {
//                            onStopsChange(
//                                stops + Stage(currentInput.trim(), freeRoaming = false, startDate = "", endDate = "") // ToDo: Fix this
//                            )
//                            currentInput = ""
//                        }
//                    },
//                    shape = RoundedCornerShape(2.dp)
//                ) {
//                    Icon(
//                        imageVector = Icons.Default.Add,
//                        contentDescription = "Add Stop",
//                        modifier = Modifier.size(ButtonDefaults.IconSize)
//                    )
//                    Spacer(Modifier.size(ButtonDefaults.IconSpacing))
//                    Text("Add")
//                }
//            }
//        )
//    }
//}

@Composable
fun TGT_StopsInputWithDates(
    stops: List<Stage>,
    error: String? = null,
    onStopsChange: (List<Stage>) -> Unit,
    onTripDatesUpdated: (String, String) -> Unit
) {
    var currentInput by remember { mutableStateOf("") }
    var stopToEdit by remember { mutableStateOf<Pair<Int, Stage>?>(null) }
    var showDatePicker by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Itinerary", style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Medium)

            Box {
                Text(
                    text = error ?: "",
                    style = CustomTheme.typography.labelVariant,
                    color = if (error != null) MaterialTheme.colorScheme.error else Color.Transparent,
                    modifier = Modifier
                        .shadow(elevation = if (error != null) 2.dp else 0.dp, shape = RoundedCornerShape(4.dp))
                        .clip(RoundedCornerShape(4.dp))
                        .background(
                            color = if (error != null) MaterialTheme.colorScheme.errorContainer else Color.Transparent,
                            shape = RoundedCornerShape(4.dp)
                        )
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }

        }

        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            stops.forEachIndexed { index, stop ->
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.surfaceContainerLow)
                        .padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = stop.stageName,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = "${stop.startDate} → ${stop.endDate}".takeIf { stop.startDate.isNotBlank() && stop.endDate.isNotBlank() }
                                ?: "Tap calendar to set dates",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Row {
                        IconButton(onClick = {
                            stopToEdit = index to stop
                            showDatePicker = true
                        }) {
                            Icon(Icons.Default.DateRange, contentDescription = "Edit dates")
                        }

                        IconButton(onClick = {
                            val updated = stops.toMutableList().apply {
                                this[index] = stop.copy(freeRoaming = !stop.freeRoaming)
                            }
                            onStopsChange(updated)
                        }) {
                            Icon(
                                imageVector = if (stop.freeRoaming) Icons.Outlined.Groups else Icons.Default.Groups,
                                contentDescription = "Toggle group"
                            )
                        }

                        IconButton(onClick = {
                            onStopsChange(stops.toMutableList().also { it.removeAt(index) })
                        }) {
                            Icon(Icons.Default.Close, contentDescription = "Remove stop")
                        }
                    }
                }
            }
        }

        OutlinedTextField(
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = MaterialTheme.colorScheme.onSurface,
                unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                focusedBorderColor = MaterialTheme.colorScheme.outline,
                unfocusedBorderColor = MaterialTheme.colorScheme.surface,
                focusedContainerColor = MaterialTheme.colorScheme.surface,
                unfocusedContainerColor = MaterialTheme.colorScheme.surface
            ),
            value = currentInput,
            onValueChange = { currentInput = it },
            placeholder = { Text("Awesome place") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            trailingIcon = {
                FilledTonalButton(
                    modifier = Modifier.padding(end = 4.dp),
                    colors = ButtonDefaults.filledTonalButtonColors(
                        contentColor = MaterialTheme.colorScheme.primary,
                        containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
                    ),
                    contentPadding = ButtonDefaults.ButtonWithIconContentPadding,
                    onClick = {
                        if (currentInput.isNotBlank()) {
                            val newStop = Stage(
                                stageName = currentInput.trim(),
                                freeRoaming = false,
                                startDate = "",
                                endDate = ""
                            )
                            val updatedStops = stops + newStop
                            onStopsChange(updatedStops)
                            currentInput = ""

                            // Subito apre il picker per la nuova tappa
                            stopToEdit = updatedStops.lastIndex to newStop
                            showDatePicker = true
                        }
                    },
                    shape = RoundedCornerShape(2.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add Stop",
                        modifier = Modifier.size(ButtonDefaults.IconSize)
                    )
                    Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                    Text("Add")
                }
            }
        )
    }

    stopToEdit?.let { (index, stop) ->
        if (showDatePicker) {
            DateRangePickerModal(
                initialStartDateMillis = stop.startDate.takeIf { it.isNotBlank() }?.let { convertDateToMillis(it) },
                initialEndDateMillis = stop.endDate.takeIf { it.isNotBlank() }?.let { convertDateToMillis(it) },
                onDateRangeSelected = { (startMillis, endMillis) ->
                    if (startMillis != null && endMillis != null) {
                        val newStart = convertMillisToDate(startMillis)
                        val newEnd = convertMillisToDate(endMillis)

                        val updated = stops.toMutableList().also {
                            it[index] = it[index].copy(startDate = newStart, endDate = newEnd)
                        }
                        onStopsChange(updated)

                        val first = updated.minOfOrNull { it.startDate.takeIf { it.isNotBlank() } ?: "9999-12-31" }
                        val last = updated.maxOfOrNull { it.endDate.takeIf { it.isNotBlank() } ?: "0000-01-01" }
                        if (first != null && last != null) {
                            onTripDatesUpdated(first, last)
                        }
                    }
                    showDatePicker = false
                    stopToEdit = null
                },
                onDismiss = {
                    showDatePicker = false
                    stopToEdit = null
                }
            )
        }
    }
}


@Composable
fun TGT_ActivitiesInput(
    activities: List<String>,
    onAddActivity: (String) -> Unit,
    onRemoveActivity: (String) -> Unit
) {
    var currentInput by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text("Suggested Activities", style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Medium)

        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            activities.forEachIndexed { index, activity ->
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.surfaceContainerLow)
                        .padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = activity,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium
                    )

                    IconButton(onClick = { onRemoveActivity(activity) }) {
                        Icon(Icons.Default.Close, contentDescription = "Remove")
                    }
                }
            }
        }

        OutlinedTextField(
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = MaterialTheme.colorScheme.onSurface,
                unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                focusedBorderColor = MaterialTheme.colorScheme.outline,
                unfocusedBorderColor = MaterialTheme.colorScheme.surface,
                focusedContainerColor = MaterialTheme.colorScheme.surface,
                unfocusedContainerColor = MaterialTheme.colorScheme.surface
            ),
            value = currentInput,
            onValueChange = { currentInput = it },
            placeholder = { Text("Awesome activity") },  // ToDo: Gamificate by generating a random placeholder
            modifier = Modifier
                .fillMaxWidth(),
            singleLine = true,
            trailingIcon = {
                FilledTonalButton(
                    modifier = Modifier.padding(end = 4.dp),
                    colors = ButtonDefaults.filledTonalButtonColors(
                        contentColor = MaterialTheme.colorScheme.primary,
                        containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
                    ),
                    contentPadding = ButtonDefaults.ButtonWithIconContentPadding,
                    onClick = {
                        val trimmed = currentInput.trim()
                        if (trimmed.isNotEmpty()) {
                            onAddActivity(trimmed)
                            currentInput = ""
                        }
                    },
                    shape = RoundedCornerShape(2.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add Activity",
                        modifier = Modifier.size(ButtonDefaults.IconSize)
                    )
                    Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                    Text("Add")
                }
            }
        )

    }
}

@Composable
fun TGT_PriceSlider(
    priceEsteem: PriceRange,
    onPriceChange: (PriceRange) -> Unit,
    minAllowed: Int = 0,
    maxAllowed: Int = 2000,
    step: Int = 100
) {
    val minF = minAllowed.toFloat()
    val maxF = maxAllowed.toFloat()

    var sliderPosition by remember {
        mutableStateOf(priceEsteem.min.toFloat()..priceEsteem.max.toFloat())
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text("Price Estimation", style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Medium)

        Text(
            text = "$${sliderPosition.start.toInt()} - $${sliderPosition.endInclusive.toInt()}",
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
        )

        RangeSlider(
            value = sliderPosition,
            onValueChange = { range ->
                val start = (range.start / step).roundToInt() * step
                val end = (range.endInclusive / step).roundToInt() * step
                sliderPosition = start.toFloat()..end.toFloat()
            },
            onValueChangeFinished = {
                val newPrice = PriceRange(
                    min = sliderPosition.start.toInt(),
                    max = sliderPosition.endInclusive.toInt()
                )
                onPriceChange(newPrice)
            },
            valueRange = minF..maxF,
            steps = (maxAllowed - minAllowed) / step - 1
        )
    }
}

@Composable
fun TGT_ImagePicker(
    modifier: Modifier = Modifier,
    images: List<TripPhoto>,
    onAddImageClick: () -> Unit,
    onRemoveImage: (TripPhoto) -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(5.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text("Preview Images", style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Medium)

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            itemsIndexed(images) { index, image ->
                val painter = rememberAsyncImagePainter(
                    model = image.url.ifBlank { null },
                    placeholder = painterResource(id = R.drawable.placeholder),
                    error = painterResource(id = R.drawable.placeholder)
                )

                Box {
                    Image(
                        painter = painter,
                        contentDescription = null,
                        modifier = Modifier
                            .size(100.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(4.dp)),
                        contentScale = ContentScale.Crop
                    )
                    IconButton(
                        onClick = { onRemoveImage(image) },
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .size(24.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Remove image",
                            tint = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            item {
                IconButton(
                    onClick = onAddImageClick,
                    modifier = Modifier
                        .size(100.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(4.dp))
                        .background(MaterialTheme.colorScheme.surface),
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add image",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(32.dp)
                    )
                }
            }
        }
    }
}
package com.example.togetthere.ui.components.filters_comps

import android.os.Bundle
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DateRangePicker
import androidx.compose.material3.DateRangePickerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.os.bundleOf
import java.time.*
import java.time.format.DateTimeFormatter
import java.util.Locale
import com.example.togetthere.utils.millisToString
import com.example.togetthere.utils.stringToMillis

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WhenCard(
    selectedStartDate: String?,
    selectedEndDate: String?,
    onDatesSelected: (String?, String?) -> Unit,
    expanded: Boolean,
    onExpanded: () -> Unit
) {
    var errorMessageVisible by remember { mutableStateOf(false) }

    val todayInMillis = remember {
        LocalDate.now()
            .atStartOfDay(ZoneId.systemDefault())
            .toInstant()
            .toEpochMilli()
    }

    val startDateMillis = remember(selectedStartDate) {
        //selectedStartDate?.let { stringToMillis(it) }
        selectedStartDate?.let {
            runCatching { stringToMillis(it) }.getOrNull()
        }
    }

    val endDateMillis = remember(selectedEndDate) {
        //selectedEndDate?.let { stringToMillis(it) }
        selectedEndDate?.let {
            runCatching { stringToMillis(it) }.getOrNull()
        }
    }

    val dateRangePickerStateSaver = Saver<DateRangePickerState, Bundle>(
        save = { state ->
            bundleOf(
                "start" to state.selectedStartDateMillis?.let {
                    Instant.ofEpochMilli(it).atZone(ZoneId.systemDefault()).toLocalDate().toEpochDay()
                },
                "end" to state.selectedEndDateMillis?.let {
                    Instant.ofEpochMilli(it).atZone(ZoneId.systemDefault()).toLocalDate().toEpochDay()
                }
            )
        },
        restore = { bundle ->
            val startEpoch = bundle.getLong("start", -1)
            val endEpoch = bundle.getLong("end", -1)
            DateRangePickerState(
                initialSelectedStartDateMillis = if (startEpoch != -1L) startEpoch * 86400000 else null,
                initialSelectedEndDateMillis = if (endEpoch != -1L) endEpoch * 86400000 else null,
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

    LaunchedEffect(
        dateRangePickerState.selectedStartDateMillis,
        dateRangePickerState.selectedEndDateMillis
    ) {
        val start = dateRangePickerState.selectedStartDateMillis
        val end = dateRangePickerState.selectedEndDateMillis
        errorMessageVisible = (start != null && start < todayInMillis)

        if (start != null && end != null && start >= todayInMillis) {
            onDatesSelected(
                millisToString(start),
                millisToString(end)
            )
            errorMessageVisible = false
        }
    }

    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.onPrimary,
            contentColor = MaterialTheme.colorScheme.onSurface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onExpanded)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "When?",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                if (!expanded && !selectedStartDate.isNullOrBlank() && !selectedEndDate.isNullOrBlank()) {
                    val formatter = remember {
                        DateTimeFormatter.ofPattern("dd MMM yyyy", Locale.getDefault())
                    }
                    val zone = ZoneId.systemDefault()
                    val start = Instant.ofEpochMilli(stringToMillis(selectedStartDate)).atZone(zone).toLocalDate()
                    val end = Instant.ofEpochMilli(stringToMillis(selectedEndDate)).atZone(zone).toLocalDate()
                    Text(
                        text = "${formatter.format(start)} - ${formatter.format(end)}",
                        fontSize = 14.sp
                    )
                }
            }

            if (expanded) {
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
                        containerColor = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.1f)
                    )
                )

                if (errorMessageVisible) {
                    Text(
                        text = "You can't select dates in the past!",
                        color = Color.Red,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = {
                        dateRangePickerState.setSelection(null, null)
                        onDatesSelected("", "")
                        errorMessageVisible = false
                    }) {
                        Text("Cancel")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    TextButton(
                        onClick = {
                            errorMessageVisible = false
                            onExpanded()
                        },
                        enabled = dateRangePickerState.selectedStartDateMillis != null &&
                                dateRangePickerState.selectedEndDateMillis != null &&
                                dateRangePickerState.selectedStartDateMillis!! >= todayInMillis
                    ) {
                        Text("OK")
                    }
                }
            }
        }
    }
}

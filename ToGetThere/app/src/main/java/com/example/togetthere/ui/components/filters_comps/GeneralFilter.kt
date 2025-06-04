package com.example.togetthere.ui.components.filters_comps

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RangeSlider
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.togetthere.model.TripType

@Composable
fun RangeFilter(
    title: String,
    currentRange: ClosedFloatingPointRange<Float>,
    valueRange: ClosedFloatingPointRange<Float>,
    onRangeChange: (ClosedFloatingPointRange<Float>) -> Unit,
) {

    Column {
        Text(
            text = title,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.SemiBold
        )
        Spacer(Modifier.height(8.dp))

        RangeSlider(
            value = currentRange,
            onValueChange = { range ->
                onRangeChange(range)
            },
            valueRange = valueRange,
            modifier = Modifier.fillMaxWidth()
                .padding(start=10.dp, end=10.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth()
                .padding(start = 20.dp, end=20.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .padding(horizontal = 8.dp, vertical = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = currentRange.start.toInt().toString(),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .padding(horizontal = 8.dp, vertical = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = currentRange.endInclusive.toInt().toString(),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

    }
}

@Composable
fun FilterCard(
    girlsOnly: Boolean,
    lgbtqFriendly: Boolean,
    groupSize: Float,
    selectedTripType: TripType?,
    minAge: Float,
    maxAge: Float,
    minPrice: Float,
    maxPrice: Float,
    expanded: Boolean,
    onExpanded: () -> Unit,
    onGirlsOnlyChanged: (Boolean) -> Unit,
    onLgbtqFriendlyChanged: (Boolean) -> Unit,
    onGroupSizeChanged: (Float) -> Unit,
    onTripTypeSelected: (TripType?) -> Unit,
    onAgeRangeChanged: (Float, Float) -> Unit,
    onPriceRangeChanged: (Float, Float) -> Unit
) {
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
            if (expanded) {
                Text(text = "Filters", fontSize = 30.sp, fontWeight = FontWeight.Bold)

                Spacer(modifier = Modifier.height(8.dp))

                // Girls only switch
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Girls only",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.SemiBold
                    )
                    Switch(
                        checked = girlsOnly,
                        onCheckedChange = onGirlsOnlyChanged,
                        modifier = Modifier.scale(0.8f)
                    )
                }

                HorizontalDivider(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    color = MaterialTheme.colorScheme.surfaceContainer,
                    thickness = 3.dp
                )

                // LGBTQ+ switch
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "LGBTQ+ Friendly",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.SemiBold
                    )
                    Switch(
                        checked = lgbtqFriendly,
                        onCheckedChange = onLgbtqFriendlyChanged,
                        modifier = Modifier.scale(0.8f)
                    )
                }

                HorizontalDivider(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    color = MaterialTheme.colorScheme.surfaceContainer,
                    thickness = 3.dp
                )

                // Age range slider
                RangeFilter(
                    title = "Age range",
                    currentRange = minAge..maxAge,
                    valueRange = 0f..100f,
                    onRangeChange = { newRange ->
                        onAgeRangeChanged(newRange.start, newRange.endInclusive)
                    }
                )

                HorizontalDivider(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    color = MaterialTheme.colorScheme.surfaceContainer,
                    thickness = 3.dp
                )

                // Price range slider
                RangeFilter(
                    title = "Price range",
                    currentRange = minPrice..maxPrice,
                    valueRange = 0f..1500f,
                    onRangeChange = { newRange ->
                        onPriceRangeChanged(newRange.start, newRange.endInclusive)
                    }
                )

                HorizontalDivider(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    color = MaterialTheme.colorScheme.surfaceContainer,
                    thickness = 3.dp
                )

                // Group size slider
                GroupSizeSlider(
                    groupSize = groupSize,
                    onValueChange = onGroupSizeChanged
                )

                HorizontalDivider(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    color = MaterialTheme.colorScheme.surfaceContainer,
                    thickness = 3.dp
                )

                // Trip type dropdown
                TripTypeDropdownMenu(
                    selectedTripType = selectedTripType,
                    onTripTypeSelected = onTripTypeSelected
                )

                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = {
                        onGirlsOnlyChanged(false)
                        onLgbtqFriendlyChanged(false)
                        onGroupSizeChanged(50f)
                        onTripTypeSelected(null)
                        onAgeRangeChanged(0f, 100f)
                        onPriceRangeChanged(0f, 1500f)
                    }) {
                        Text("Cancel")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    TextButton(onClick = onExpanded) {
                        Text("OK")
                    }
                }
            } else {
                Text(
                    text = "Filters",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                )
            }
        }
    }
}


@Composable
fun TripTypeDropdownMenu(
    selectedTripType: TripType?,
    onTripTypeSelected: (TripType) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .padding(top = 16.dp, bottom = 16.dp, start = 15.dp, end=80.dp)
            .border(1.dp, MaterialTheme.colorScheme.onBackground, shape = RoundedCornerShape(15.dp))
            .clickable { expanded = !expanded }
            .padding(horizontal = 12.dp, vertical = 8.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.wrapContentWidth()
        ) {
            Text(
                text = selectedTripType?.name ?: "Trip type",
                modifier = Modifier.weight(1f),
                color = if (selectedTripType != null) MaterialTheme.colorScheme.onBackground else MaterialTheme.colorScheme.outline
            )
            Icon(
                imageVector = if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                contentDescription = "Dropdown Arrow"
            )
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            TripType.entries.forEach { type ->
                DropdownMenuItem(
                    text = { Text(type.name) },
                    onClick = {
                        onTripTypeSelected(type)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
fun GroupSizeSlider(
    groupSize: Float,
    onValueChange: (Float) -> Unit
){
    Column {
        Text(
            text = "Maximum group size",
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.SemiBold
        )
        Spacer(Modifier.height(8.dp))
        Slider(
            value = groupSize,
            valueRange = 2f..50f,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth()
                .padding(start=10.dp, end=10.dp)
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentWidth(Alignment.CenterHorizontally)
                .clip(RoundedCornerShape(10.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .padding(vertical = 8.dp, horizontal = 8.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = groupSize.toInt().toString(),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
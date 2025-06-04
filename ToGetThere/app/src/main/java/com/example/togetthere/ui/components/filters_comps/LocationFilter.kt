package com.example.togetthere.ui.components.filters_comps

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DockedSearchBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import kotlinx.coroutines.FlowPreview

data class PredefinedDestination(
    val name: String,
    val imageURL: String
)

val predefinedDestinations = listOf(
    PredefinedDestination("Anywhere", "https://upload.wikimedia.org/wikipedia/commons/thumb/9/9d/Afro-Eurasia_%28orthographic_projection%29_political.svg/825px-Afro-Eurasia_%28orthographic_projection%29_political.svg.png?20190910192457"),
    PredefinedDestination("Africa", "https://upload.wikimedia.org/wikipedia/commons/thumb/8/86/Africa_%28orthographic_projection%29.svg/900px-Africa_%28orthographic_projection%29.svg.png?20250330160640"),
    PredefinedDestination("Europe", "https://upload.wikimedia.org/wikipedia/commons/thumb/4/44/Europe_orthographic_Caucasus_Urals_boundary_%28with_borders%29.svg/806px-Europe_orthographic_Caucasus_Urals_boundary_%28with_borders%29.svg.png?20220913092104"),
    PredefinedDestination("Asia", "https://upload.wikimedia.org/wikipedia/commons/thumb/8/80/Asia_%28orthographic_projection%29.svg/812px-Asia_%28orthographic_projection%29.svg.png?20250209214250"),
    PredefinedDestination("North America", "https://upload.wikimedia.org/wikipedia/commons/thumb/4/43/Location_North_America.svg/825px-Location_North_America.svg.png?20230329164336"),
    PredefinedDestination("South America", "https://upload.wikimedia.org/wikipedia/commons/thumb/0/0f/South_America_%28orthographic_projection%29.svg/812px-South_America_%28orthographic_projection%29.svg.png?20120912202456"),
    PredefinedDestination("Oceania", "https://upload.wikimedia.org/wikipedia/commons/thumb/e/e3/Oceania_orthographic.png/1200px-Oceania_orthographic.png?20230318130723")
)


@Composable
fun WhereCard(
    selectedPlace: String,
    searchQuery: String,
    isSearchActive: Boolean,
    recentSearches: List<String>,
    onPlaceSelected: (String) -> Unit,
    onSearchQueryChange: (String) -> Unit,
    onSearchActiveChange: (Boolean) -> Unit,
    onContinentSelected: (String?) -> Unit,
    onAddRecentSearch: (String) -> Unit,
    isReset: Boolean
) {
    var expanded by remember { mutableStateOf(true) }

    LaunchedEffect(isReset) {
        if (isReset) {
            onPlaceSelected("")
            onSearchQueryChange("")
            onSearchActiveChange(false)
            onContinentSelected(null)
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
            .clickable { expanded = !expanded }
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            if (expanded) {
                Text(text = "Where?", fontSize = 30.sp, fontWeight = FontWeight.Bold)

                DestinationSearchBar(
                    expanded = isSearchActive,
                    onChangeExpanded = { onSearchActiveChange(it) },
                    query = searchQuery,
                    onQueryChange = { onSearchQueryChange(it) },
                    onDestinationSelected = { dest ->
                        onSearchQueryChange(dest)
                        onPlaceSelected(dest)
                        if (dest.isNotBlank()) {
                            onAddRecentSearch(dest)
                        }
                    },
                    predefinedDestinations = listOf("Rome", "Tokyo", "Paris"),
                    previousSearches = recentSearches,
                    selectedDestination = selectedPlace
                )

                Spacer(modifier = Modifier.height(5.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(
                        onClick = {
                            onPlaceSelected("")
                            onSearchQueryChange("")
                            onSearchActiveChange(false)
                            onContinentSelected(null)
                        }
                    ) {
                        Text("Cancel")
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    TextButton(
                        onClick = {
                            expanded = false
                        },
                    ) {
                        Text("OK")
                    }
                }
            } else {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "Where?", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                    if (selectedPlace.isNotEmpty()) {
                        Text(text = selectedPlace, color = MaterialTheme.colorScheme.outline)
                    }
                }
            }
        }
    }
}

@OptIn(FlowPreview::class,ExperimentalMaterial3Api::class)
@Composable
fun DestinationSearchBar(
    expanded: Boolean,
    onChangeExpanded: (Boolean) -> Unit,
    query: String,
    selectedDestination: String?,
    onQueryChange: (String) -> Unit,
    onDestinationSelected: (String) -> Unit,
    predefinedDestinations: List<String>,
    previousSearches: List<String> = emptyList(),
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()

    LaunchedEffect(isFocused) {
        if (isFocused && !expanded) {
            onChangeExpanded(true)
        }
    }
    Column {
        DockedSearchBar(
            expanded = expanded,
            onExpandedChange = onChangeExpanded,
            modifier = Modifier
                .fillMaxWidth()
                .height(if (expanded) 300.dp else 80.dp)
                .padding(8.dp),
            inputField = {
                TextField(
                    interactionSource = interactionSource,
                    value = query,
                    onValueChange = {
                        onQueryChange(it)
                        if (!expanded) onChangeExpanded(true)
                    },
                    placeholder = { Text("Search destination") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search") },
                    trailingIcon = {
                        if (query.isNotEmpty()) {
                            IconButton(onClick = {
                                onQueryChange("")
                                onDestinationSelected("")
                                onChangeExpanded(false)
                            }) {
                                Icon(Icons.Default.Clear, contentDescription = "Clear")
                            }
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(50),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = MaterialTheme.colorScheme.surfaceContainer,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainer,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        disabledIndicatorColor = Color.Transparent,
                        cursorColor = MaterialTheme.colorScheme.primary
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    keyboardOptions = KeyboardOptions.Default.copy(
                        imeAction = ImeAction.Search
                    ),
                    keyboardActions = KeyboardActions(
                        onSearch = {
                            if (query.isNotBlank()) {
                                onDestinationSelected(query)
                                onChangeExpanded(false)
                                keyboardController?.hide()
                            }
                        }
                    )
                )
            },
            content = {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 300.dp)
                ) {
                    if (query.isEmpty()) {
                        items(previousSearches) { prev ->
                            ListItem(
                                headlineContent = { Text(prev) },
                                leadingContent = {
                                    Icon(Icons.Default.History, contentDescription = "History")
                                },
                                modifier = Modifier.clickable {
                                    onQueryChange(prev)
                                    onDestinationSelected(prev)
                                    onChangeExpanded(false)
                                }
                            )
                        }
                    } else {
                        val destinationsToShow = predefinedDestinations.filter {
                            it.contains(query, ignoreCase = true)
                        }
                        if (destinationsToShow.isNotEmpty()) {
                            items(destinationsToShow) { destination ->
                                ListItem(
                                    headlineContent = { Text(destination) },
                                    modifier = Modifier.clickable {
                                        onQueryChange(destination)
                                        onDestinationSelected(destination)
                                        onChangeExpanded(false)
                                    }
                                )
                            }
                        }
                    }
                }
            }
        )

        if (!expanded) {
            Spacer(modifier = Modifier.height(16.dp))

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(horizontal = 4.dp)
            ) {
                items(com.example.togetthere.ui.components.filters_comps.predefinedDestinations) { destination ->
                    DestinationCard(
                        destination = destination,
                        isSelected = selectedDestination == destination.name,
                        onDestinationSelected = {
                            onQueryChange(destination.name)
                            onDestinationSelected(destination.name)
                        }
                    )
                }
            }
        }
    }
}



@Composable
fun DestinationCard(
    destination: PredefinedDestination,
    isSelected: Boolean,
    onDestinationSelected: (PredefinedDestination) -> Unit
) {
    Column(
        modifier = Modifier.clickable { onDestinationSelected(destination) }
    ) {
        Card(
            shape = RoundedCornerShape(16.dp),
            border = BorderStroke(
                width = if (isSelected) 3.dp else 1.dp,
                color = if (isSelected) MaterialTheme.colorScheme.onSurface else Color.LightGray
            ),
            modifier = Modifier.size(120.dp)
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                AsyncImage(
                    model = destination.imageURL,
                    contentDescription = "Image of ${destination.name}",
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(20.dp)),
                    contentScale = ContentScale.Crop,
                )
            }
        }

        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = destination.name,
            fontSize = 16.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            textAlign = TextAlign.Start,
            modifier = Modifier.padding(start = 6.dp)
        )
    }
}
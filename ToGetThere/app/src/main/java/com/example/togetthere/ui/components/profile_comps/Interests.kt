package com.example.togetthere.ui.components.profile_comps

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


@Composable
fun SelectableInterests(
    initialSelected: List<String> = emptyList(),
    onSelectionChanged: (List<String>) -> Unit
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

    val allInterests = listOf("Relax","Backpack","Adventure", "Interrail", "Nature", "Art", "Culinary", "Photo", "Hiking", "Culture", "Tech", "Blogging", "On Road", "History", "Museums", "Camping", "Urban", "Surf").sorted()

    var selectedInterests by remember { mutableStateOf(initialSelected.toMutableSet()) }

    LaunchedEffect(initialSelected) {
        selectedInterests = initialSelected.toMutableSet()
    }

    println("Selected interests: $selectedInterests")
    println("Initial selected: $initialSelected")

    Column {
        Text(
            "Choose Your Interests (max 6)",
            modifier = Modifier.padding(start = 8.dp),
            color = MaterialTheme.colorScheme.onSurface,
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp
        )
        Spacer(modifier = Modifier.height(16.dp))

        allInterests.chunked(3).forEach { rowItems ->
            Row(
                modifier = Modifier
                    .padding(start = 8.dp, bottom = 8.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                rowItems.forEach { interest ->
                    val isSelected = interest in selectedInterests

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(30.dp))
                            .background(
                                if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
                                else Color.Transparent
                            )
                            .border(
                                1.dp,
                                if (isSelected) MaterialTheme.colorScheme.primary
                                else MaterialTheme.colorScheme.surfaceDim,
                                RoundedCornerShape(30.dp)
                            )
                            .clickable {
                                selectedInterests = selectedInterests.toMutableSet().apply {
                                    if (contains(interest)) {
                                        remove(interest)
                                    } else if (size < 6) {
                                        add(interest)
                                    }
                                }
                                onSelectionChanged(selectedInterests.toList())
                            }
                            .padding(6.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "${interestEmojis[interest] ?: "❓"} $interest",
                            color = MaterialTheme.colorScheme.onSurface,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}


@Composable
fun Interests(interests: List<String>) {
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

    Box(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Column {
            Text(
                "Interests",
                Modifier.padding(start = 8.dp, end = 8.dp),
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp
            )
            Spacer(
                modifier = Modifier
                    .height(16.dp)
            )

            interests.chunked(3).forEach { rowItems ->
                Row(
                    modifier = Modifier
                        .padding(start = 0.dp, bottom = 8.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    rowItems.forEach { interest ->
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(30.dp))
                                .border(
                                    1.dp,
                                    MaterialTheme.colorScheme.surfaceDim,
                                    RoundedCornerShape(30.dp)
                                )
                                .padding(6.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                "${interestEmojis.getOrElse(interest){ "❓" }} $interest",
                                color = MaterialTheme.colorScheme.onSurface,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }
        }
    }
}
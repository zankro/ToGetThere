package com.example.togetthere.ui.components.profile_comps

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import com.example.togetthere.model.Destination
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter

@Composable
fun DreamTrips(desiredDestinations: List<Destination>) {

    Box(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Column {
            Text(
                "Dream Trips",
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp
            )
            Spacer(
                modifier = Modifier
                    .height(16.dp)
            )
            LazyRow(
                state = rememberLazyListState(),
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(14.dp),
                contentPadding = PaddingValues(start = 14.dp),
            ) {
                items(desiredDestinations) { desiredDestination ->
                    Box(
                        modifier = Modifier
                            .size(220.dp)
                            .clip(RoundedCornerShape(20.dp))
                    ) {
                        Image(
                            painter = rememberAsyncImagePainter(desiredDestination.imageURL),
                            contentDescription = "Image of ${desiredDestination.name}",
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(RoundedCornerShape(20.dp)),
                            contentScale = ContentScale.Crop
                        )
                        Text(
                            text = desiredDestination.name,
                            modifier = Modifier.padding(start = 16.dp, top = 14.dp),
                            color = Color.White,
                            fontWeight = FontWeight.Medium,
                            fontSize = 18.sp,
                            style = TextStyle(
                                shadow = Shadow(
                                    color = Color.Black.copy(alpha = 0.8f),
                                    offset = Offset(4f, 4f),
                                    blurRadius = 8f
                                )
                            )
                        )
                    }
                }
            }
        }
    }
}


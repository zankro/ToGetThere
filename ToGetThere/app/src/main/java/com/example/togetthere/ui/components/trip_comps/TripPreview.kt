package com.example.togetthere.ui.components.trip_comps

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.example.togetthere.R
import com.example.togetthere.model.Trip
import com.example.togetthere.viewmodel.HomeFiltersViewModel

@Composable
fun TripPreview(
    trip: Trip,
    index: Int,
    isFavourited: Boolean,
    viewModel: HomeFiltersViewModel,
    userId: String?,
    navigateToDetails: () -> Unit
) {
   /*val painter = rememberAsyncImagePainter(
        model = trip.images.firstOrNull()?.url ?: "",
        placeholder = painterResource(R.drawable.placeholder),
        error = painterResource(R.drawable.placeholder)
    )*/
    var isLoading by remember { mutableStateOf(true) }
    val showDefaultImage = trip.images.isEmpty()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .clip(shape = RoundedCornerShape(28.dp))
            .background(color = Color.Gray)
            .clickable { navigateToDetails() }
    ) {
        if (showDefaultImage) {
            Image(
                painter = painterResource(R.drawable.placeholder),
                contentDescription = "Default trip image",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize(),
                alignment = Alignment.BottomCenter
            )
        }

        // Immagine effettiva (se disponibile)
        if (trip.images.isNotEmpty()) {
            val painter = rememberAsyncImagePainter(
                model = trip.images[0].url,
                onLoading = { isLoading = true },
                onSuccess = { isLoading = false}
            )
            Image(
                painter = painter,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize(),
                alignment = Alignment.BottomCenter
            )
            if (isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.onPrimary),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        color = Color.White,
                        strokeWidth = 2.dp
                    )
                }
            }
        }


        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalArrangement = Arrangement.SpaceBetween

        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(0.85f)
                ) {
                    Text(
                        text = trip.name,
                        color = Color.White,
                        style = TextStyle(
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            shadow = Shadow(
                                color = Color.Black,
                                offset = Offset(2f, 2f),
                                blurRadius = 4f
                            )
                        )
                    )

                    Text(
                        text = trip.type.displayName,
                        color = Color.White,
                        style = TextStyle(
                            fontSize = 18.sp,
                            shadow = Shadow(
                                color = Color.Black,
                                offset = Offset(2f, 2f),
                                blurRadius = 4f
                            )
                        )
                    )
                }

                if(!userId.isNullOrEmpty()) {
                    FloatingActionButton(
                        onClick = {
                            viewModel.toggleFavoriteStatus(trip.tripId, userId)
                        },
                        modifier = Modifier
                            .padding(top = 4.dp)
                            .size(40.dp),
                        containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.30f),
                        contentColor = MaterialTheme.colorScheme.onPrimary,
                        shape = CircleShape
                    ) {
                        Icon(
                            imageVector = if (isFavourited) Icons.Default.Favorite
                            else Icons.Default.FavoriteBorder,
                            contentDescription = "Favorite",
                            tint = Color.White
                        )
                    }
                }
            }

                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically

                ) {
                    Column()
                    {
                        Row(
                            verticalAlignment = Alignment.CenterVertically

                        ) {
                            Icon(
                                Icons.Default.LocationOn,
                                contentDescription = "location",
                                modifier = Modifier.size(18.dp),
                                tint = Color.White
                            )
                            Text(
                                text = trip.destination, // TripData.Location
                                color = Color.White
                            )
                            Spacer(Modifier.width(8.dp))
                            Icon(
                                Icons.Default.Person,
                                contentDescription = "n_people",
                                modifier = Modifier.size(18.dp),
                                tint = Color.White
                            )
                            Text(
                                text = trip.maxParticipants.toString(),
                                color = Color.White
                            )
                        }

                        Row(
                            verticalAlignment = Alignment.CenterVertically

                        ) {
                            Icon(
                                Icons.Default.DateRange,
                                contentDescription = "location",
                                modifier = Modifier.size(18.dp),
                                tint = Color.White
                            )
                            Text(
                                text = "${trip.startDate.take(5)} • ${trip.endDate.take(5)}",
                                color = Color.White
                            )
                        }
                    }
                }


            }
        }
}


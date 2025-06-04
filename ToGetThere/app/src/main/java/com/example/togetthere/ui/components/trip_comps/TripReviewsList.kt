package com.example.togetthere.ui.components.trip_comps

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import coil.compose.rememberAsyncImagePainter
import com.example.togetthere.R
import com.example.togetthere.model.GenderType
import com.example.togetthere.viewmodel.TripReviewWithAuthor


@Composable
fun TripReviewsList(
    reviews: List<TripReviewWithAuthor>,
    onReviewClick: (TripReviewWithAuthor) -> Unit
) {
    Column(modifier = Modifier
        .fillMaxWidth()
        .wrapContentHeight()
        .clip(shape = RoundedCornerShape(8.dp))
        .background(color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.6f))
        .padding(6.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp))
    {
        Text(
            "Trip Reviews",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface,
        )

        LazyRow(
            state = rememberLazyListState(),
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            contentPadding = PaddingValues(horizontal = 8.dp),
        ) {
            items(reviews) { review ->
                Card(
                    colors = CardDefaults.cardColors(
                        contentColor = MaterialTheme.colorScheme.onBackground,
                        containerColor = MaterialTheme.colorScheme.background,
                    ),
                    shape = RoundedCornerShape(20.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                    modifier = Modifier
                        .width(180.dp)
                        .height(170.dp),
                    onClick = {
                        onReviewClick(review)
                    }
                ) {
                    Box(modifier = Modifier.padding(16.dp)) {
                        Column {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Image(
                                    painter = if (!review.authorPhoto.isNullOrBlank()) {
                                        rememberAsyncImagePainter(review.authorPhoto)
                                    } else {
                                        when (review.authorGender) {
                                            GenderType.MALE -> painterResource(R.drawable.copertina_profilo_m_default)
                                            GenderType.FEMALE -> painterResource(R.drawable.copertina_profilo_f_default)
                                            else -> painterResource(R.drawable.copertina_profilo_neutro_default)
                                        }
                                    },
                                    contentDescription = "Author image",
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier
                                        .clip(CircleShape)
                                        .size(30.dp)
                                )
                                Text(
                                    text = "  " + review.authorName,
                                    fontStyle = FontStyle.Italic,
                                    fontSize = 16.sp
                                )
                            }

                            Spacer(modifier = Modifier.height(6.dp))

                            Text(
                                text = review.title,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 16.sp
                            )

                            Text(
                                text = if (review.description.length > 70)
                                    review.description.take(60) + "..."
                                else review.description,
                                style = MaterialTheme.typography.bodyMedium,
                                fontSize = 14.sp
                            )

                            Spacer(modifier = Modifier.height(4.dp))

                            Row {
                                repeat(5) { index ->
                                    Icon(
                                        imageVector = if (index < review.score) Icons.Default.Star else Icons.Default.StarBorder,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(5.dp))
    }
}

@Composable
fun CustomTripDialog(review: TripReviewWithAuthor, onDismiss: () -> Unit, onAuthorClick: (String) -> Unit) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(4.dp, shape = MaterialTheme.shapes.medium),
            color = MaterialTheme.colorScheme.background,
            shape = RoundedCornerShape(20.dp)
        ) {
            Column(modifier = Modifier
                .padding(30.dp)
                .verticalScroll(rememberScrollState()))
            {

                Box(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Image(
                            painter = if (!review.authorPhoto.isNullOrBlank()) {
                                rememberAsyncImagePainter(review.authorPhoto)
                            } else {
                                when (review.authorGender) {
                                    GenderType.MALE -> painterResource(R.drawable.copertina_profilo_m_default)
                                    GenderType.FEMALE -> painterResource(R.drawable.copertina_profilo_f_default)
                                    else -> painterResource(R.drawable.copertina_profilo_neutro_default)
                                }
                            },
                            contentDescription = "Author photo",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .clip(CircleShape)
                                .size(30.dp)
                        )
                        Text(
                            text = "  " + review.authorName,
                            fontStyle = FontStyle.Italic,
                            fontSize = 18.sp,
                            modifier = Modifier.clickable{
                                onAuthorClick(review.authorId)
                            }
                        )
                    }

                    FloatingActionButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .size(30.dp),
                        shape = CircleShape,
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    ) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                Row {
                    repeat(5) { index ->
                        Icon(
                            imageVector = if (index < review.score) Icons.Default.Star else Icons.Default.StarBorder,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                if (review.photos.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(16.dp))
                    LazyRow(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(review.photos) { photo ->
                            val painter = rememberAsyncImagePainter(
                                model = photo.url,
                                placeholder = painterResource(R.drawable.placeholder),
                                error = painterResource(R.drawable.placeholder)
                            )

                            Image(
                                painter = painter,
                                contentDescription = "Trip photo",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .size(100.dp)
                                    .clip(RoundedCornerShape(10.dp))
                            )
                        }
                    }
                }

                Text(
                    text = review.description,
                    style = MaterialTheme.typography.bodyLarge,
                    fontSize = 18.sp
                )

            }
        }
    }
}


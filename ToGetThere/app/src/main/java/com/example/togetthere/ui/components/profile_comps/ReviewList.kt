package com.example.togetthere.ui.components.profile_comps

import androidx.compose.foundation.Image
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.togetthere.R
import com.example.togetthere.model.GenderType
import com.example.togetthere.ui.navigation.ToGetThereDestinations
import com.example.togetthere.viewmodel.UserReviewWithAuthor

@Composable
fun Reviews(reviews: List<UserReviewWithAuthor>, navController: NavController, highlightReviewId: Int? = null) {
    var showDialog by remember { mutableStateOf(false) }
    var selectedReview by remember { mutableStateOf<UserReviewWithAuthor?>(null) }

    LaunchedEffect(highlightReviewId, reviews) {
        if (highlightReviewId != null && reviews.isNotEmpty()) {

            val reviewToHighlight = reviews.find { it.id == highlightReviewId }
            if (reviewToHighlight != null) {
                selectedReview = reviewToHighlight
                showDialog = true
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Column {
            Text(
                "Personal Reviews",
                modifier = Modifier.padding(start = 8.dp),
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
                contentPadding = PaddingValues(start = 8.dp, end = 8.dp),
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
                            .width(200.dp)
                            .height(150.dp),
                        onClick = {
                            selectedReview = review
                            showDialog = true
                        }
                    ) {
                        Box(modifier = Modifier.padding(16.dp)) {
                            Column {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Image(
                                        painter = if (!review.authorPhoto.isNullOrEmpty()) {
                                            rememberAsyncImagePainter(review.authorPhoto)
                                        } else {
                                            when (review.authorGender) {
                                                GenderType.MALE -> painterResource(R.drawable.copertina_profilo_m_default)
                                                GenderType.FEMALE -> painterResource(R.drawable.copertina_profilo_f_default)
                                                else -> painterResource(R.drawable.copertina_profilo_neutro_default)
                                            }
                                        },
                                        contentDescription = "Review image",
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier
                                            .clip(CircleShape)
                                            .size(30.dp)
                                    )
                                    Text(
                                        text = "  " + review.authorName,
                                        fontStyle = FontStyle.Italic,
                                        fontSize = 18.sp
                                    )
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = if (review.description.length > 90) review.description.take(90) + "..." else review.description,
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontSize = 15.sp
                                )
                            }

                        }
                    }
                }
            }
        }
    }
    if (showDialog && selectedReview != null) {
        CustomDialog(review = selectedReview!!,
            onDismiss = {showDialog = false},
            onAuthorClick = { id ->
                navController.navigate(ToGetThereDestinations.profileRoute(id))
            }
        )
    }
}

@Composable
fun CustomDialog(review: UserReviewWithAuthor, onDismiss: () -> Unit, onAuthorClick: (String) -> Unit) {
    Dialog(
        onDismissRequest = onDismiss,

        ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(4.dp, shape = MaterialTheme.shapes.medium),
            color = MaterialTheme.colorScheme.background,
            shape = RoundedCornerShape(20.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(30.dp)
                    .fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Image(
                            painter = if (!review.authorPhoto.isNullOrEmpty()) {
                                rememberAsyncImagePainter(review.authorPhoto)
                            } else {
                                when (review.authorGender) {
                                    GenderType.MALE -> painterResource(R.drawable.copertina_profilo_m_default)
                                    GenderType.FEMALE -> painterResource(R.drawable.copertina_profilo_f_default)
                                    else -> painterResource(R.drawable.copertina_profilo_neutro_default)
                                }
                            },
                            contentDescription = "Review image",
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
                    FloatingActionButton (
                        onClick = onDismiss,
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(0.dp)
                            .size(30.dp),
                        shape = CircleShape ,
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    ) {
                        Icon(Icons.Default.Close, contentDescription = "close")
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = review.description,
                    style = MaterialTheme.typography.bodyLarge,
                    fontSize = 20.sp,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }
        }
    }
}
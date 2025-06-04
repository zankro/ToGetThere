package com.example.togetthere.ui.components.trip_comps

import android.content.Intent
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import com.example.togetthere.R
import com.example.togetthere.model.GenderType
import com.example.togetthere.model.Stage
import com.example.togetthere.model.TripPhoto
import com.example.togetthere.model.UserProfile
import com.example.togetthere.ui.theme.CustomTheme

@Composable
fun TripSection(modifier: Modifier = Modifier) {
    // val context = LocalContext.current

}

@Composable
fun TripTopBar(navController: NavController, tripId: Int) {
    val context = LocalContext.current
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {

        IconButton(onClick = { navController.popBackStack() }) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back",
                tint = Color.White
            )
        }

        IconButton(onClick = {
            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(
                    Intent.EXTRA_TEXT,
                    "Hey! Take a look to this trip on ToGetThere: https://togetthere.app/trip/${tripId}"
                )
            }
            context.startActivity(Intent.createChooser(shareIntent, "Share via"))
        }) {
            Icon(
                imageVector = Icons.Default.Share,
                contentDescription = "Share",
                tint = Color.White
            )
        }

    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ImageCarousel(tripImages: List<TripPhoto> = listOf()) {

    val pagerState = rememberPagerState(pageCount = {
        tripImages.size
    })

    Box {

        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize(),
            key = { index -> tripImages[index].url }
        ) { index ->

            val image = tripImages[index]
            var isLoading by remember { mutableStateOf(true) }

            val painter = rememberAsyncImagePainter(
                model = image.url,
                onLoading = { isLoading = true },
                onSuccess = { isLoading = false}
            )

            Image(
                painter = painter,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxSize()
                    .drawWithCache {
                        val gradient = Brush.verticalGradient(
                            colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.55f)),
                            startY = size.height / 3,
                            endY = size.height
                        )
                        onDrawWithContent {
                            drawContent()
                            drawRect(gradient, blendMode = BlendMode.Multiply)
                        }
                    },
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

        Row(
            Modifier
                .wrapContentHeight()
                .fillMaxWidth()
                .fillMaxHeight(0.16f)
                .align(Alignment.BottomCenter),
            horizontalArrangement = Arrangement.Center
        ) {
            repeat(pagerState.pageCount) { iteration ->
                val color =
                    if (pagerState.currentPage == iteration) Color.DarkGray else Color.LightGray
                Box(
                    modifier = Modifier
                        .padding(2.dp)
                        .clip(CircleShape)
                        .background(color)
                        .size(6.dp)
                )
            }
        }
    }
}

@Composable
fun MembersList(participants: List<UserProfile>,
                tripId: Int,
                numParticipants: Int,
                maxNumParticipants: Int,
                onMoreClick: () -> Unit) {
    val maxVisible = 3
    val visibleParticipants = participants.take(maxVisible)
    val remainingCount = participants.size - maxVisible
    val spacing = 20

    Box(modifier = Modifier.height(40.dp)) {
        visibleParticipants.forEachIndexed { index, user ->
            Box(
                modifier = Modifier
                    .zIndex(index.toFloat())
                    .align(Alignment.CenterEnd)
                    .offset(x = (index * spacing).dp)
            ) {
                SmallFloatingActionButton(
                    onClick = { onMoreClick() },
                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                    contentColor = MaterialTheme.colorScheme.onSurface,
                    shape = CircleShape,
                    modifier = Modifier.size(40.dp)
                ) {
                    Image(
                        painter = if (!user.photo.isNullOrBlank()) {
                            rememberAsyncImagePainter(user.photo)
                        } else {
                            when (user.gender) {
                                GenderType.MALE -> painterResource(R.drawable.copertina_profilo_m_default)
                                GenderType.FEMALE -> painterResource(R.drawable.copertina_profilo_f_default)
                                else -> painterResource(R.drawable.copertina_profilo_neutro_default)
                            }
                        },
                        contentDescription = "Profile picture of ${user.name}",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                    )
                }
            }
        }

        if (remainingCount > 0) {
            Box(
                modifier = Modifier
                    .zIndex(visibleParticipants.size.toFloat())
                    .align(Alignment.CenterEnd)
                    .offset(x = ((visibleParticipants.size) * spacing).dp)
            ) {
                SmallFloatingActionButton(
                    onClick = { onMoreClick() },
                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                    contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                    shape = CircleShape,
                    modifier = Modifier.size(40.dp)
                ) {
                    Text(
                        text = "+$remainingCount/${maxNumParticipants}",
                        style = MaterialTheme.typography.labelLarge
                    )
                }
            }
        }
    }

//    Row(
//        modifier = Modifier
//            .clip(shape = RoundedCornerShape(4.dp))
//            .background(MaterialTheme.colorScheme.surfaceVariant)
//            .padding(horizontal = 4.dp, vertical = 2.dp),
//        verticalAlignment = Alignment.CenterVertically,
//        horizontalArrangement = Arrangement.spacedBy(6.dp)
//    ) {
//        Icon(
//            imageVector = Icons.Default.Group,
//            contentDescription = "Group",
//            tint = MaterialTheme.colorScheme.onSurfaceVariant
//        )
//        Text(text = "$numParticipants/$maxNumParticipants", color = MaterialTheme.colorScheme.onSurfaceVariant, fontWeight = FontWeight.Bold)
//    }

//    FilledTonalButton(
//        onClick = { onMoreClick() },
////        containerColor = MaterialTheme.colorScheme.secondaryContainer,
////        contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
//        shape = CircleShape,
////        modifier = Modifier.size(40.dp)
//    ) {
//        Icon(
//            imageVector = Icons.Default.Group,
//            contentDescription = "Share"
//        )
//        Text(
//            text = "${numParticipants}/${maxNumParticipants}",
//            style = MaterialTheme.typography.labelLarge
//        )
//    }
}

@Composable
fun Description(body: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .clip(shape = RoundedCornerShape(8.dp))
            .background(color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.6f))
            .padding(6.dp)
    ) {
        Text(
            text = "Description",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface,
        )

        Text(
            text = body,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )

    }
}

@Composable
fun Itinerary(stops: List<Stage>) {
    val selectedOption = remember { mutableStateOf(0) }

    var isExpanded by remember { mutableStateOf(false) }

    // Filtering stops based on selected option
    val filteredStops = when (selectedOption.value) {
        1 -> stops.filter { !it.freeRoaming } // Group
        2 -> stops.filter { it.freeRoaming }  // Free Roam
        else -> stops // All Stops
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .clip(shape = RoundedCornerShape(8.dp))
            .background(color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.6f))
            .padding(6.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = "Itinerary",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface,
        )

        TabViewSample(selectedOption)

        Box() {
            Column(
                verticalArrangement = Arrangement.spacedBy(2.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(shape = RoundedCornerShape(4.dp))
                    .background(color = MaterialTheme.colorScheme.surfaceBright)
                    .clickable { isExpanded = !isExpanded; }
                    .animateContentSize()
                    .heightIn(min = 42.dp, max = if (isExpanded) Dp.Unspecified else 42.dp)
                    .padding(4.dp)
            ) {
                filteredStops.forEach { stop ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp, vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "• ${stop.stageName}",
                            style = CustomTheme.typography.bodyBold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.weight(1f, fill = false)
                        )

                        Text(
                            // text = "${stop.startDate.split("/")[0]}/${stop.startDate.split("/")[1]} - ${stop.endDate.split("/")[0]}/${stop.endDate.split("/")[1]}",
                            text = "${stop.startDate} - ${stop.endDate}",
                            style = CustomTheme.typography.body,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            if (!isExpanded) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(20.dp)
                        .align(Alignment.BottomCenter)
                        .clip(shape = RoundedCornerShape(4.dp))
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color.Transparent,
                                    MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.6f)
                                )
                            )
                        )
                )
            }
        }
    }
}

@Composable
fun AdditionalInfo(min: Int, max: Int, suggestedActivities: List<String>) {
    val concatenated = suggestedActivities.joinToString(separator = ", ")

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .clip(shape = RoundedCornerShape(8.dp))
            .background(color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.6f))
            .padding(6.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)

    ) {
        Text(
            text = "Additional Info & Details",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface,
        )

        Column() {
            Text(
                text = "Price estimation",
                style = CustomTheme.typography.subTitleSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Text(
                text = "$min - $max",
                style = CustomTheme.typography.body,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }

        Column() {
            Text(
                text = "Suggested Activities",
                style = CustomTheme.typography.subTitleSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Text(
                text = concatenated,
                style = CustomTheme.typography.body,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
fun EssentialInfoPill(title: String, body: String) {
    Column(
//        modifier = Modifier
//            .shadow(elevation = 10.dp, shape = RoundedCornerShape(24.dp), clip = true)
////            .padding(4.dp)
//            .fillMaxWidth(0.32f)
//            .clip(shape = RoundedCornerShape(24.dp))
//            .background(color = MaterialTheme.colorScheme.surfaceContainerLowest)
//            .padding(start = 30.dp, end = 30.dp, top = 4.dp, bottom = 12.dp),

        modifier = Modifier
            .shadow(
                elevation = 4.dp,
                spotColor = Color(0x0F000000),
                ambientColor = Color(0x0F000000)
            )
            .width(106.dp)
            .height(68.dp)
            .background(
                color = MaterialTheme.colorScheme.surfaceContainerLowest,
                shape = RoundedCornerShape(size = 24.dp)
            )
            .padding(start = 12.dp, top = 4.dp, end = 12.dp, bottom = 12.dp)
            .wrapContentWidth(unbounded = true),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = title,
            style = CustomTheme.typography.labelEmphasized,
//            fontWeight = FontWeight.ExtraBold,
            color = MaterialTheme.colorScheme.onSurface,
        )

        Spacer(
            modifier = Modifier.size(12.dp)
        )

        Text(
            text = body,
            style = CustomTheme.typography.label,
//            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSecondaryContainer,
        )


    }

}

@Composable
fun TagPill(title: String) {
    Row(
        modifier = Modifier
            .clip(shape = RoundedCornerShape(size = 36.dp))
            .background(color = MaterialTheme.colorScheme.surfaceContainerLow)
//            .padding(6.dp)
//            .height(33.dp)
            .widthIn(min = 92.dp)
            .heightIn(min = 33.dp, max = 33.dp),

        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center

    ) {
        Text(
            text = title,
            textAlign = TextAlign.Center,
            style = CustomTheme.typography.labelVariant,
//            modifier = Modifier
//                .background(color = Color.Cyan)
//                .padding(6.dp)
//                .widthIn(min = 92.dp)
//                .heightIn(min = 33.dp)
        )
    }
}

@Composable
fun CopyTripCard(
    modifier: Modifier = Modifier,
    tripId: Int,
    navController: NavHostController,
    onClick: () -> Unit = {},
) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.6f),
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = "Like the idea?",
                    style = CustomTheme.typography.labelEmphasized,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Text(
                    text = "Create a trip just like this!",
                    style = CustomTheme.typography.label,
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Use the onClick handler passed as parameter instead
            IconButton(
                onClick = {
                    // Use the onClick handler passed to the composable
                    onClick()
                },
                colors = IconButtonDefaults.iconButtonColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow)
            ) {
                Icon(Icons.Default.ContentCopy, contentDescription = "Copy Trip", tint = MaterialTheme.colorScheme.primary)
            }
        }
    }
}

@Composable
fun TabViewSample(selectedOption: MutableState<Int>) {
    val list = listOf("All Stops", "Group", "Free Roam")

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        for (i in list.indices) {
            ProgressTabView(
                modifier = Modifier,
                backgroundColor = if (selectedOption.value != i) {
                    Color.Transparent
                } else {
                    MaterialTheme.colorScheme.surface
                },
                onClick = {
                    selectedOption.value = i
                },
                titleText = list[i],
                textColor = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun ProgressTabView(
    modifier: Modifier,
    backgroundColor: Color,
    onClick: () -> Unit,
    titleText: String,
    textColor: Color,
) {
    OutlinedButton(
        onClick = {
            onClick()
        },
        colors = ButtonDefaults.buttonColors(
            containerColor = backgroundColor,
            contentColor = Color.White,
            disabledContainerColor = backgroundColor,
            disabledContentColor = Color.White
        ),
        contentPadding = PaddingValues(0.dp),
        modifier = modifier
            .size(width = 70.dp, height = 18.dp)
//            .padding(2.dp)
            .clip(RoundedCornerShape(25.dp))
//            .widthIn(min = 92.dp)
//            .heightIn(min = 33.dp, max = 33.dp),
    ) {
//        Text(
//            text = titleText,
//            modifier = Modifier
//                .align(Alignment.CenterVertically)
//                .background(color = Color.Gray)
//                .padding(8.dp),
//            color = textColor,
//            lineHeight = 16.sp,
//        )
        Text(
            text = titleText,
            style = CustomTheme.typography.labelVariant,
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier
                .align(Alignment.CenterVertically),
        )

    }
}

@Preview
@Composable
fun ProgressTabView2(
//    modifier: Modifier,
//    backgroundColor: Color,
//    onClick: () -> Unit,
//    titleText: String,
//    textColor: Color,
) {
    OutlinedButton(
        onClick = {
//            onClick()
        },
        colors = ButtonDefaults.buttonColors(
//            containerColor = backgroundColor,
//            contentColor = Color.White,
//            disabledContainerColor = backgroundColor,
//            disabledContentColor = Color.White
        ),
//        contentPadding = PaddingValues(top = 1.dp, bottom = 1.dp, start = 20.dp, end = 8.dp),
        contentPadding = PaddingValues(0.dp),
        modifier = Modifier
            .size(width = 70.dp, height = 18.dp)
//            .padding(2.dp)
            .clip(RoundedCornerShape(25.dp))
//            .widthIn(min = 92.dp)
//            .heightIn(min = 33.dp, max = 33.dp),
    ) {
//        Text(
//            text = titleText,
//            modifier = Modifier
//                .align(Alignment.CenterVertically)
//                .background(color = Color.Gray)
//                .padding(8.dp),
//            color = textColor,
//            lineHeight = 16.sp,
//        )
        Text(
            text = "titleText",
            style = CustomTheme.typography.labelVariant,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier
                .align(Alignment.CenterVertically)
                .background(color = Color.Yellow),

            )
    }
}





@Preview
@Composable
fun CustomBezierShape() {
    Canvas(modifier = Modifier
        .size(200.dp)
        .background(color = Color.Gray)) {
        val path = Path().apply {
            moveTo(50f, 50f)
            cubicTo(100f, 100f, 150f, 50f, 200f, 150f)
        }
        drawPath(path, color = Color.Blue)
    }
}
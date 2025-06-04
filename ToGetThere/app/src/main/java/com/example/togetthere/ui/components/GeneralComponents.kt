package com.example.togetthere.ui.components

//import com.example.togettherelab4.convertDateToMillis
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.FloatingActionButtonElevation
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.example.togetthere.R
import com.example.togetthere.model.GenderType
import com.example.togetthere.model.ParticipantActionState
import com.example.togetthere.model.UserProfile

@Composable
fun TGT_SmallFAB(
    icon: ImageVector,
    containerColor: Color = MaterialTheme.colorScheme.surfaceContainerHigh,
    contentColor: Color = MaterialTheme.colorScheme.primary,
    elevation: FloatingActionButtonElevation = FloatingActionButtonDefaults.elevation(),
    modifier: Modifier = Modifier,
    onClick: () -> Unit) {
    SmallFloatingActionButton(
        onClick = onClick,
        shape = CircleShape,
        containerColor = containerColor,
        contentColor = contentColor,
        elevation = elevation,
        modifier = modifier
    ) {
        Icon(
            imageVector = icon,
            contentDescription = "Go Back to Trip"
        )
    }
}

@Composable
fun TGT_FAB(
    icon: ImageVector,
    containerColor: Color = MaterialTheme.colorScheme.surfaceContainerHigh,
    contentColor: Color = MaterialTheme.colorScheme.primary,
    modifier: Modifier = Modifier,
    onClick: () -> Unit) {
    FloatingActionButton(
        onClick = onClick,
        shape = CircleShape,
        containerColor = containerColor,
        contentColor = contentColor,
        modifier = modifier
    ) {
        Icon(
            imageVector = icon,
            contentDescription = "Go Back to Trip"
        )
    }
}

@Composable
fun TGT_ParticipantCard(
    modifier: Modifier = Modifier,
    user: UserProfile,
    extraParticipants: Int = 0,
    isCreator: Boolean,
    actionState: ParticipantActionState,
    onReviewClick: () -> Unit = {},
    onAcceptClick: () -> Unit = {},
    onRejectClick: () -> Unit = {},
    onUserClick: () -> Unit = {}
) {
    Card(
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        ),
        modifier = modifier
            .fillMaxWidth()
//            .padding(vertical = 4.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(12.dp)
        ) {
            // Profile image
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
                    .size(48.dp)
                    .clip(CircleShape)
                    .clickable { onUserClick() }
            )

            Spacer(Modifier.width(12.dp))

            Column(
                modifier = Modifier
                    .weight(1f)
                    .clickable(
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() }
                    ) {
                        onUserClick()
                    }
            ) {
                if (isCreator) {
                    Text(
                        text = "Leader \uD83D\uDC51",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    // horizontalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    Text(
                        text = user.name,
                        style = MaterialTheme.typography.bodyLarge,
                    )

                    if (extraParticipants > 0) {
                        Text(
                            text = " +$extraParticipants",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }


                Text(
                    text = user.description,
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )

            }

            Spacer(Modifier.width(8.dp))

            when (actionState) {
                ParticipantActionState.REVIEW -> {
                    Button(
                        onClick = onReviewClick,
                        shape = RoundedCornerShape(40),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow, contentColor = MaterialTheme.colorScheme.primary)
                    ) {
                        Icon(Icons.Outlined.Edit, contentDescription = "Review")
                        Spacer(Modifier.width(6.dp))
                        Text("Review")
                    }
                }

                ParticipantActionState.PENDING_DECISION -> {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        IconButton(onClick = onAcceptClick, colors = IconButtonDefaults.iconButtonColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow)) {
                            Icon(Icons.Default.Check, contentDescription = "Accept", tint = MaterialTheme.colorScheme.primary)
                        }
                        IconButton(onClick = onRejectClick, colors = IconButtonDefaults.iconButtonColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow)) {
                            Icon(Icons.Default.Close, contentDescription = "Reject", tint = MaterialTheme.colorScheme.error)
                        }
                    }
                }

                ParticipantActionState.NONE -> {
//                    Button(
//                        modifier = Modifier.defaultMinSize(minWidth = 100.dp),
//                        onClick = onRejectClick,
//                        shape = RoundedCornerShape(40),
//                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
//                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow, contentColor = MaterialTheme.colorScheme.primary)
//                    ) {
//                        Icon(Icons.Default.Close, contentDescription = "Kick")
//                        Spacer(Modifier.width(6.dp))
//                        Text("Kick")
//                    }
                }
            }
        }
    }
}

@Composable
fun TGT_AlertDialog(
    showDialog: Boolean,
    onDismissRequest: () -> Unit,
    title: String,
    message: String,
    confirmButtonText: String = "Confirm",
    dismissButtonText: String = "Cancel",
    onConfirm: () -> Unit
) {
    if (showDialog) {
        AlertDialog(
            onDismissRequest = onDismissRequest,
            title = { Text(title) },
            text = { Text(message) },
            confirmButton = {
                TextButton(onClick = {
                    onConfirm()
                    onDismissRequest()
                }) {
                    Text(confirmButtonText, color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = onDismissRequest) {
                    Text(dismissButtonText)
                }
            }
        )
    }
}

@Composable
fun NothingToSeeHere(
    title: String,
    text: String,
){
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
//        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "\uD83E\uDD7A",
            textAlign = TextAlign.Center,
            fontSize = 48.sp
        )

        Spacer(modifier = Modifier.height(8.dp))

        if(title.isNotEmpty())
            Text(
                text = title,
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center
            )

        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center
        )
    }
}
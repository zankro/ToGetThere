package com.example.togetthere.ui.components.profile_comps

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties

@Composable
fun UserReviewDialog(
    isOpen: Boolean,
    reviewedUser: String,
    reviewerUser: String,
    savedDescription: String,
    onDismiss: () -> Unit,
    onDescriptionChange: (String) -> Unit,
    onSubmitReview: (description: String, reviewedUserId: String, reviewerUserId: String) -> Unit
) {
    var description by rememberSaveable { mutableStateOf(savedDescription) }
    val isSubmitEnabled = description.trim().isNotEmpty()

    if (!isOpen) return

    LaunchedEffect(description) {
        if (description != savedDescription) {
            onDescriptionChange(description)
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = true,
            usePlatformDefaultWidth = false
        ),
        title = {
            Text(
                text = "Leave your review",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it
                                    onDescriptionChange(description)},
                    label = { Text("Write something nice...") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 120.dp)
                        .padding(vertical = 8.dp),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = false,
                    maxLines = 6
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onSubmitReview(description.trim(), reviewedUser, reviewerUser)
                    onDismiss()
                },
                enabled = isSubmitEnabled
            ) {
                Text(
                    text = "Send",
                    style = MaterialTheme.typography.labelLarge.copy(fontSize = 16.sp)
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(
                    text = "Cancel",
                    style = MaterialTheme.typography.labelLarge.copy(fontSize = 16.sp)
                )
            }
        },
        shape = RoundedCornerShape(20.dp),
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 8.dp,
        modifier = Modifier.padding(horizontal = 16.dp)
    )
}


package com.example.togetthere.ui.components.trip_comps

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.example.togetthere.R
import com.example.togetthere.model.TripPhoto

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TripReviewDialog(
    isOpen: Boolean,
    tripTitle: String,
    onDismiss: () -> Unit,
    onSubmitReview: (score: Int, title: String, description: String, photos: List<TripPhoto>) -> Unit
) {
    if (!isOpen) return

    var score by rememberSaveable { mutableIntStateOf(0) }
    var title by rememberSaveable { mutableStateOf("") }
    var description by rememberSaveable { mutableStateOf("") }
    var selectedImages by rememberSaveable { mutableStateOf<List<TripPhoto>>(emptyList()) }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetMultipleContents()
    ) { uris ->
        selectedImages = selectedImages + uris.map { TripPhoto(it.toString()) }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Button(
                onClick = {
                    onSubmitReview(score, title, description.trim(), selectedImages)
                    onDismiss()
                },
                enabled = title.isNotBlank() && description.isNotBlank() && score > 0,
                shape = RoundedCornerShape(24.dp)
            ) {
                Icon(Icons.Default.Check, contentDescription = "Confirm")
                Spacer(Modifier.width(8.dp))
                Text("Confirm")
            }
        },
        dismissButton = {
            TextButton (onClick = onDismiss) {
                Text("Cancel")
            }
        },
        title = {
            Text(
                text = "Review",
                style = MaterialTheme.typography.titleMedium
            )
        },
        text = {
            Column (modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState())
                        .heightIn(max = 500.dp)){
                Text(
                    text = tripTitle,
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                Surface(
                    color = MaterialTheme.colorScheme.secondaryContainer,
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text("Assign a score", style = MaterialTheme.typography.bodyMedium)

                        Row {
                            repeat(5) { index ->
                                IconButton(onClick = { score = index + 1 }) {
                                    Icon(
                                        imageVector = if (index < score) Icons.Default.Star else Icons.Default.StarBorder,
                                        contentDescription = "Star ${index + 1}",
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Button(
                        onClick = { launcher.launch("image/*") },
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Icon(Icons.Default.AddPhotoAlternate, contentDescription = "Add Photo")
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Add photos")
                    }
                }

                if (selectedImages.isNotEmpty()) {
                    Spacer(Modifier.height(8.dp))

                    LazyRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(selectedImages.size) { index ->
                            val tripPhoto = selectedImages[index]
                            val painter = rememberAsyncImagePainter(
                                model = tripPhoto.url,
                                placeholder = painterResource(R.drawable.placeholder),
                                error = painterResource(R.drawable.placeholder)
                            )

                            Box(
                                modifier = Modifier
                                    .size(100.dp)
                                    .clip(RoundedCornerShape(8.dp))
                            ) {
                                Image(
                                    painter = painter,
                                    contentDescription = "Selected image",
                                    modifier = Modifier.size(100.dp),
                                    contentScale = ContentScale.Crop
                                )

                                // Delete button
                                IconButton(
                                    onClick = { selectedImages = selectedImages.filter { it != tripPhoto }},
                                    modifier = Modifier
                                        .align(Alignment.TopEnd)
                                        .size(24.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "Remove image",
                                        tint = MaterialTheme.colorScheme.onSurface,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(Modifier.height(12.dp))

                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    placeholder = { Text("Be nice and respectful") },
                    label = { Text("Title") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 60.dp)
                        .padding(vertical = 4.dp),
                    shape = RoundedCornerShape(12.dp),
                    maxLines = 2
                )

                Spacer(Modifier.height(12.dp))

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    placeholder = { Text("Write something about your experience") },
                    label = { Text("Description") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 120.dp)
                        .padding(vertical = 4.dp),
                    shape = RoundedCornerShape(12.dp),
                    maxLines = 6
                )
            }
        },
        shape = RoundedCornerShape(20.dp),
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 8.dp,
        modifier = Modifier.padding(horizontal = 16.dp)
    )
}

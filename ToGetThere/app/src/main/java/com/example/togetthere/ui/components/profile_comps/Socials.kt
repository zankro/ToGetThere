package com.example.togetthere.ui.components.profile_comps

//import androidx.compose.material3.Card
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import com.example.togetthere.R
import com.example.togetthere.model.SocialHandle
import com.example.togetthere.model.SocialPlatform

@Composable
fun Socials(socials: List<SocialHandle>) {
    val context = LocalContext.current

    Box(modifier = Modifier.fillMaxWidth()) {
        Column {
            Text(
                "Social",
                modifier = Modifier.padding(start = 8.dp),
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp
            )
            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                socials.forEach { social ->
                    val icon = when (social.platform) {
                        SocialPlatform.INSTAGRAM -> painterResource(R.drawable.instagram_icon)
                        SocialPlatform.FACEBOOK -> painterResource(R.drawable.facebook_icon)
                    }

                    Card(
                        colors = CardDefaults.cardColors(
                            contentColor = MaterialTheme.colorScheme.onSurface,
                            containerColor = Color.White,
                        ),
                        shape = RoundedCornerShape(20.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                        modifier = Modifier
                            .width(170.dp)
                            .height(60.dp),
                        onClick = {
                            openSocialLink(context, social)
                        }
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                painter = icon,
                                contentDescription = "${social.platform.displayName} icon",
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = social.username,
                                color = MaterialTheme.colorScheme.onSurface,
                                textAlign = TextAlign.Center,
                                style = TextStyle(fontSize = 18.sp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(30.dp))
        }
    }
}

fun openSocialLink(context: Context, social: SocialHandle) {
    val (intent, fallback) = when (social.platform) {
        SocialPlatform.INSTAGRAM -> {
            val appIntent = Intent(Intent.ACTION_VIEW).apply {
                data = "http://instagram.com/_u/${social.username}".toUri()
                setPackage("com.instagram.android")
            }
            val browserIntent = Intent(Intent.ACTION_VIEW).apply {
                data = "https://instagram.com/${social.username}".toUri()
            }
            appIntent to browserIntent
        }

        SocialPlatform.FACEBOOK -> {
            val appIntent = Intent(Intent.ACTION_VIEW).apply {
                data = "fb://facewebmodal/f?href=https://facebook.com/${social.username}".toUri()
                setPackage("com.facebook.katana")
            }
            val browserIntent = Intent(Intent.ACTION_VIEW).apply {
                data = "https://facebook.com/${social.username}".toUri()
            }
            appIntent to browserIntent
        }
    }

    try {
        context.startActivity(intent)
    } catch (e: ActivityNotFoundException) {
        context.startActivity(fallback)
    }
}


@Composable
fun EditSocials(
    socials: List<SocialHandle>,
    onSocialChange: (SocialHandle) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            "Social",
            modifier = Modifier.padding(start = 8.dp),
            color = MaterialTheme.colorScheme.onSurface,
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp
        )
        Spacer(modifier = Modifier.height(16.dp))

        val existingSocials = socials.associateBy { it.platform }

        SocialPlatform.entries.forEach { platform ->
            val icon = when (platform) {
                SocialPlatform.INSTAGRAM -> painterResource(R.drawable.instagram_icon)
                SocialPlatform.FACEBOOK -> painterResource(R.drawable.facebook_icon)
            }

            val currentSocial = existingSocials[platform] ?: SocialHandle(platform = platform, username = "")
            var username by remember(platform, socials) { mutableStateOf(currentSocial.username) }

            Card(
                colors = CardDefaults.cardColors(
                    contentColor = MaterialTheme.colorScheme.onSurface,
                    containerColor = Color.White,
                ),
                shape = RoundedCornerShape(20.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .height(60.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 12.dp)
                ) {
                    Icon(
                        painter = icon,
                        contentDescription = "${platform.displayName} icon",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    TextField(
                        value = username,
                        onValueChange = {
                            username = it
                            val updatedSocial = currentSocial.copy(username = it)
                            onSocialChange(updatedSocial)
                        },
                        singleLine = false,
                        modifier = Modifier.fillMaxWidth(),
                        textStyle = LocalTextStyle.current.copy(fontSize = 16.sp),
                    )
                }
            }
        }
    }
}
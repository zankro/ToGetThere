package com.example.togetthere.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.togetthere.ui.navigation.ToGetThereDestinations

@Composable
fun NotLoggedInComponent(isLandscape: Boolean, navController: NavController, bottomPadding: Dp)
{
    if (!isLandscape) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "\uD83E\uDD7A",
                textAlign = TextAlign.Center,
                fontSize = 48.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Hold up! You need to log in to see the good stuff",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = {
                    navController.navigate(ToGetThereDestinations.LOGIN_ROUTE)
                },
                modifier = Modifier
                    .height(56.dp)
                    .padding(horizontal = 8.dp)
            ) {
                Text(
                    text = "Login",
                    fontSize = 18.sp
                )
            }
        }
    } else {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .padding(bottom = bottomPadding),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = "\uD83E\uDD7A",
                    textAlign = TextAlign.Center,
                    fontSize = 48.sp
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Hold up! You need to log in to see the good stuff",
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(32.dp))

                Button(
                    onClick = {
                        navController.navigate(ToGetThereDestinations.LOGIN_ROUTE)
                    },
                    modifier = Modifier
                        .width(200.dp)
                        .height(48.dp)
                ) {
                    Text("Login")
                }
            }
        }
    }
}
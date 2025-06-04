package com.example.testwithpoetry.presentation.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.testwithpoetry.data.local.datastore.AppSettings
import com.example.testwithpoetry.data.local.model.User

@Composable
fun ProfileScreen(
    navController: NavController,
    appSettings: AppSettings,
) {
    val user by appSettings.userData.collectAsState(initial = User("", "", ""))

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Top
    ) {
        Text(
            text = "Name: ${user.name}",
            style = MaterialTheme.typography.bodyLarge
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Email: ${user.email}",
            style = MaterialTheme.typography.bodyLarge
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Birthday: ${user.birthday}",
            style = MaterialTheme.typography.bodyLarge
        )
    }
}


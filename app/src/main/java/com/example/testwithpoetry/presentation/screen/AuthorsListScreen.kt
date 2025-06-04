package com.example.testwithpoetry.presentation.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.testwithpoetry.data.local.datastore.AppSettings
import com.example.testwithpoetry.data.local.model.Author
import com.example.testwithpoetry.data.local.model.User
import com.example.testwithpoetry.presentation.navigation.NavigationRoute
import com.example.testwithpoetry.presentation.viewmodel.AuthorsEvent
import com.example.testwithpoetry.presentation.viewmodel.AuthorsUiState
import com.example.testwithpoetry.presentation.viewmodel.AuthorsViewModel
import android.app.Activity
import androidx.activity.compose.BackHandler

@Composable
fun AuthorsListScreen(
    navController: NavController,
    appSettings: AppSettings,
    snackbarHostState: SnackbarHostState, //changed
    viewModel: AuthorsViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val activity = (context as? Activity)
    val user by appSettings.userData.collectAsState(initial = User("", "", ""))

    LaunchedEffect(Unit) {
        viewModel.snackbarEvents.collect { message ->
            snackbarHostState.showSnackbar(message)
        }
    }

    BackHandler {
        activity?.finish()
    }

    when (uiState) {
        is AuthorsUiState.Loading -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }

        is AuthorsUiState.Success -> {
            val authors = (uiState as AuthorsUiState.Success).authors
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp)
            ) {
                items(authors) { author ->
                    AuthorItem(
                        author = author,
                        onStarClick = {
                            viewModel.handleEvent(AuthorsEvent.ToggleFavorite(author))
                        },
                        onClick = {
                            navController.navigate(NavigationRoute.AuthorDetails.createRoute(author.name))
                        }
                    )
                }
            }
        }

        is AuthorsUiState.Error -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(text = (uiState as AuthorsUiState.Error).message)
            }
        }
    }
}

@Composable
private fun AuthorItem(
    author: Author,
    onStarClick: () -> Unit,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = author.name,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.weight(1f)
            )

            IconButton(onClick = onStarClick) {
                Icon(
                    imageVector = if (author.isFavourite) Icons.Filled.Star else Icons.Outlined.Star,
                    contentDescription = if (author.isFavourite) "Favorite" else "Not Favorite",
                    tint = if (author.isFavourite) Color.Yellow else MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}
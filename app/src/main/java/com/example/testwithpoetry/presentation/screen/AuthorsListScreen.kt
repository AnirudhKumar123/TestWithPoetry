package com.example.testwithpoetry.presentation.screen

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.testwithpoetry.presentation.state.NetworkResource
import com.example.testwithpoetry.data.local.datastore.AppSettings
import com.example.testwithpoetry.data.local.model.Author
import com.example.testwithpoetry.data.local.model.User
import com.example.testwithpoetry.presentation.viewmodel.AuthorsViewModel
import android.app.Activity
import androidx.activity.compose.BackHandler

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuthorsListScreen(
    navController: NavController,
    appSettings: AppSettings,
    viewModel: AuthorsViewModel = hiltViewModel(),
) {
    val authorsResource by viewModel.authors.observeAsState(initial = NetworkResource.Loading)
    val showSnackbar by viewModel.showSnackbar.observeAsState(initial = false)

    val context = LocalContext.current
    val activity = (context as? Activity)

    BackHandler {
        activity?.finish()
    }

    val user by appSettings.userData.collectAsState(initial = User("", "", ""))

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Welcome ${user.name}") }
            )
        },
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    icon = { Icon(Icons.Filled.Star, contentDescription = "Poetry") },
                    label = { Text("Poetry") },
                    selected = true,
                    onClick = {
                        navController.navigate("authors") {

                            //Removes screens from the navigation stack before navigating to a new screen
                            popUpTo("profile") { inclusive = true }
                        }
                    }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Filled.Person, contentDescription = "Account") },
                    label = { Text("Account") },
                    selected = false,
                    onClick = { navController.navigate("profile") }
                )
            }
        }
    ) { padding ->

        if (showSnackbar) {
            LaunchedEffect(Unit) {
                viewModel.snackbarShown()
            }
        }

        when (authorsResource) {
            is NetworkResource.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            is NetworkResource.Success -> {
                val authors = (authorsResource as NetworkResource.Success<List<Author>>).data
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentPadding = PaddingValues(16.dp)
                ) {
                    items(authors) { author ->
                        AuthorItem(
                            author = author,
                            onStarClick = {
                                viewModel.toggleFavorite(author)
                                Toast.makeText(
                                    context,
                                    if (!author.isFavourite)
                                        "Author added to favorites"
                                    else
                                        "Author removed from favorites",
                                    Toast.LENGTH_SHORT
                                ).show()
                            },
                            onClick = { navController.navigate("authorDetails/${author.name}") }
                        )
                    }
                }
            }

            is NetworkResource.Fail -> {
                val message = (authorsResource as NetworkResource.Fail).error ?: "Unknown error"
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "Error loading authors: $message")
                }
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

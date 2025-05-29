package com.example.testwithpoetry.presentation.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.testwithpoetry.presentation.state.NetworkResource
import com.example.testwithpoetry.presentation.viewmodel.PoetryViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuthorDetailsScreen(
    navController: NavController,
    authorName: String,
    viewModel: PoetryViewModel = hiltViewModel(),

) {
    val poemTitlesState = viewModel.poemTitles.observeAsState(initial = NetworkResource.Loading)
    val selectedPoemState = viewModel.selectedPoem.observeAsState()
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(authorName) {
        viewModel.loadPoemTitles(authorName)
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text(text = authorName) })
        },
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    icon = { Icon(Icons.Filled.Star, contentDescription = "Poetry") },
                    label = { Text("Poetry") },
                    selected = true,
                    onClick = {
                        navController.navigate("authors") {
                            popUpTo("profile") { inclusive = true }
                        }
                    }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Filled.Person, contentDescription = "Account") },
                    label = { Text("Account") },
                    selected = false,
                    onClick = {navController.navigate("profile")}
                )
            }
        }
    ) { padding ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            when (val titles = poemTitlesState.value) {
                is NetworkResource.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }

                is NetworkResource.Fail -> {
                    Text(
                        text = titles.error ?: "Failed to load poem titles",
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                is NetworkResource.Success -> {
                    val titlesList = titles.data
                    if (titlesList.isEmpty()) {
                        Text(
                            text = "No poems available.",
                            modifier = Modifier.align(Alignment.Center)
                        )
                    } else {
                        LazyColumn(
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(titlesList) { title ->
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            coroutineScope.launch {
                                                viewModel.loadPoem(authorName, title)
                                            }
                                        },
                                    elevation = CardDefaults.cardElevation()
                                ) {
                                    Text(
                                        text = title,
                                        style = MaterialTheme.typography.titleMedium,
                                        modifier = Modifier.padding(16.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            when (val selectedPoem = selectedPoemState.value) {
                is NetworkResource.Success -> {
                    val poem = selectedPoem.data
                    AlertDialog(
                        onDismissRequest = { viewModel.clearSelectedPoem() },
                        title = { Text(text = poem.title) },
                        text = {
                            Column {
                                Text(
                                    text = "by ${poem.content}",
                                    style = MaterialTheme.typography.labelMedium
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                poem.lines.forEach { line ->
                                    Text(text = line)
                                }
                            }
                        },
                        confirmButton = {
                            TextButton(onClick = { viewModel.clearSelectedPoem() }) {
                                Text("Close")
                            }
                        }
                    )
                }

                is NetworkResource.Fail -> {
                    AlertDialog(
                        onDismissRequest = { viewModel.clearSelectedPoem() },
                        title = { Text("Error") },
                        text = {
                            Text(
                                text = selectedPoem.error ?: "Unknown error",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        },
                        confirmButton = {
                            TextButton(onClick = { viewModel.clearSelectedPoem() }) {
                                Text("Close")
                            }
                        }
                    )
                }

                else -> Unit
            }
        }
    }
}

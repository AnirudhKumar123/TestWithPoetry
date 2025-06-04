package com.example.testwithpoetry.presentation.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.testwithpoetry.presentation.state.NetworkResource
import com.example.testwithpoetry.presentation.viewmodel.PoetryViewModel
import kotlinx.coroutines.launch

@Composable
fun AuthorDetailsScreen(
    navController: NavController,
    authorName: String,
    viewModel: PoetryViewModel = hiltViewModel(),
) {
    val poemTitlesState by viewModel.poemTitles.collectAsState()
    val selectedPoemState by viewModel.selectedPoem.collectAsState()
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(authorName) {
        viewModel.loadPoemTitles(authorName)
    }

    Box(modifier = Modifier.fillMaxSize()) {
        when (poemTitlesState) {
            is NetworkResource.Loading -> {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }

            is NetworkResource.Fail -> {
                Text(
                    text = (poemTitlesState as NetworkResource.Fail).error ?: "Failed to load poem titles",
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            is NetworkResource.Success -> {
                val titlesList = (poemTitlesState as NetworkResource.Success<List<String>>).data
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

        when (selectedPoemState) {
            is NetworkResource.Success -> {
                val poem = (selectedPoemState as NetworkResource.Success<com.example.testwithpoetry.data.local.model.Poem>).data
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
                            text = (selectedPoemState as NetworkResource.Fail).error ?: "Unknown error",
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


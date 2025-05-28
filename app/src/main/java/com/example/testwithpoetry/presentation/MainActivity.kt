package com.example.testwithpoetry.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.testwithpoetry.presentation.viewmodel.MainViewModel
import com.example.testwithpoetry.presentation.theme.TestWithPoetryTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TestWithPoetryTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Greeting(
                        name = "Android",
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    val viewModel: MainViewModel = hiltViewModel()
    Box (
        contentAlignment = Alignment.Center,
        modifier = modifier.fillMaxSize()
    ) {
        Button(
            onClick = {
                viewModel.action()
            },
            modifier = modifier
        ) {
            Text(
                text = "Try me $name",
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    TestWithPoetryTheme {
        Greeting("Android")
    }
}
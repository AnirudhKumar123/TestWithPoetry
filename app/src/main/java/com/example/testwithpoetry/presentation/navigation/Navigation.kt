package com.example.testwithpoetry.presentation.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.testwithpoetry.data.local.datastore.AppSettings
import com.example.testwithpoetry.data.local.model.User
import com.example.testwithpoetry.presentation.screen.AuthorDetailsScreen
import com.example.testwithpoetry.presentation.screen.AuthorsListScreen
import com.example.testwithpoetry.presentation.screen.ProfileScreen
import com.example.testwithpoetry.presentation.screen.WelcomeScreen
import kotlinx.coroutines.launch
import androidx.compose.runtime.rememberCoroutineScope

@Composable
fun Navigation(navController: NavHostController, appSettings: AppSettings) {

    val isFirstLaunchState = appSettings.isFirstLaunch.collectAsState(initial = null)
    val isFirstLaunch = isFirstLaunchState.value

    val coroutineScope = rememberCoroutineScope()

    if (isFirstLaunch == null) {

        Box(modifier = Modifier.fillMaxSize()) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        }
    } else {
        NavHost(
            navController = navController,
            startDestination = if (isFirstLaunch) "welcome" else "authors"
        ) {
            composable("welcome") {
                WelcomeScreen(onContinue = { user: User ->
                    coroutineScope.launch {
                        appSettings.saveUser(user)
                        appSettings.saveFirstLaunchComplete()
                        navController.navigate("authors") {
                            popUpTo("welcome") { inclusive = true }
                        }
                    }
                })
            }

            composable("authors") {
                AuthorsListScreen(navController, appSettings)
            }

            composable(
                route = "authorDetails/{authorName}",
                arguments = listOf(navArgument("authorName") { type = NavType.StringType })
            ) { backStackEntry ->
                val authorName = backStackEntry.arguments?.getString("authorName") ?: ""
                AuthorDetailsScreen(navController, authorName = authorName)
            }

            composable("profile") {
                ProfileScreen(navController, appSettings)
            }
        }
    }
}

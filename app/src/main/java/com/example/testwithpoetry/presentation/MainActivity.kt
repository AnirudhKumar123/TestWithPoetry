package com.example.testwithpoetry.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.testwithpoetry.data.local.datastore.AppSettings
import com.example.testwithpoetry.presentation.navigation.Navigation
import com.example.testwithpoetry.presentation.navigation.NavigationRoute
import com.example.testwithpoetry.presentation.theme.TestWithPoetryTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@OptIn(ExperimentalMaterial3Api::class)
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject
    lateinit var appSettings: AppSettings

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TestWithPoetryTheme {
                val navController = rememberNavController()
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val snackbarHostState = remember { SnackbarHostState() } //changed
                val currentRoute = NavigationRoute.fromRoute(navBackStackEntry?.destination?.route)
                val authorName = navBackStackEntry?.arguments?.getString("authorName")
                val user by appSettings.userData.collectAsState(initial = com.example.testwithpoetry.data.local.model.User("", "", ""))

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    snackbarHost = { SnackbarHost(snackbarHostState) },
                    topBar = {
                        if (currentRoute != NavigationRoute.Welcome) {
                            TopAppBar(
                                title = {
                                    Text(
                                        text = when (currentRoute) {
                                            is NavigationRoute.AuthorDetails -> if (!authorName.isNullOrBlank()) authorName else "Author Details"
                                            NavigationRoute.Authors -> "Welcome ${user.name}".trimEnd()
                                            NavigationRoute.Profile -> "Profile"
                                            else -> currentRoute?.title ?: ""
                                        }
                                    )
                                }
                            )
                        }
                    },
                    bottomBar = {
                        if (currentRoute != NavigationRoute.Welcome) {
                            NavigationBar {
                                NavigationRoute.bottomBarRoutes.forEach { route ->
                                    NavigationBarItem(
                                        icon = { route.icon?.let { Icon(it, contentDescription = route.title) } },
                                        label = { Text(route.title) },
                                        selected = currentRoute == route,
                                        onClick = {
                                            if (currentRoute != route) {
                                                navController.navigate(route.route) {
                                                    if (route == NavigationRoute.Authors) {
                                                        popUpTo(NavigationRoute.Profile.route) { inclusive = true }
                                                    }
                                                }
                                            }
                                        }
                                    )
                                }
                            }
                        }
                    }
                ) { padding ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(padding)
                    ) {
                        Navigation(navController, appSettings, snackbarHostState) //changed
                    }
                }
            }
        }
    }
}



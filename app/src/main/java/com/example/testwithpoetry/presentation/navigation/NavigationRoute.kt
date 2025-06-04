package com.example.testwithpoetry.presentation.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.ui.graphics.vector.ImageVector

sealed class NavigationRoute(
    val route: String,
    val title: String,
    val icon: ImageVector? = null,
    val showInBottomBar: Boolean = false
) {
    object Welcome : NavigationRoute("welcome", "Welcome")
    object Authors : NavigationRoute(
        route = "authors",
        title = "Poetry",
        icon = Icons.Filled.Star,
        showInBottomBar = true
    )
    object Profile : NavigationRoute(
        route = "profile",
        title = "Profile",
        icon = Icons.Filled.Person,
        showInBottomBar = true
    )
    object AuthorDetails : NavigationRoute(
        route = "authorDetails/{authorName}",
        title = "Author Details"
    ) {
        fun createRoute(authorName: String) = "authorDetails/$authorName"
    }

    companion object {
        val bottomBarRoutes = listOf(Authors, Profile)

        fun fromRoute(route: String?): NavigationRoute? {
            return when (route) {
                Welcome.route -> Welcome
                Authors.route -> Authors
                Profile.route -> Profile
                else -> {
                    if (route?.startsWith("authorDetails/") == true) {
                        AuthorDetails
                    } else null
                }
            }
        }
    }
} 
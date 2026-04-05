package com.movieapp.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.movieapp.ui.detail.DetailScreen
import com.movieapp.ui.movielist.MovieListScreen
import com.movieapp.ui.search.SearchScreen

sealed class Screen(val route: String) {
    object MovieList : Screen("movie_list")
    object Search : Screen("search")
    object Detail : Screen("detail/{imdbId}") {
        fun createRoute(imdbId: String) = "detail/$imdbId"
    }
}

@Composable
fun NavGraph() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Screen.MovieList.route
    ) {
        // Home: all movies list
        composable(Screen.MovieList.route) {
            MovieListScreen(
                onMovieClick = { imdbId ->
                    navController.navigate(Screen.Detail.createRoute(imdbId))
                },
                onSearchClick = {
                    navController.navigate(Screen.Search.route)
                }
            )
        }

        // Search screen
        composable(Screen.Search.route) {
            SearchScreen(
                onMovieClick = { imdbId ->
                    navController.navigate(Screen.Detail.createRoute(imdbId))
                },
                onBackClick = { navController.popBackStack() }
            )
        }

        // Detail screen
        composable(
            route = Screen.Detail.route,
            arguments = listOf(navArgument("imdbId") { type = NavType.StringType })
        ) { backStackEntry ->
            val imdbId = backStackEntry.arguments?.getString("imdbId") ?: return@composable
            DetailScreen(
                imdbId = imdbId,
                onBackClick = { navController.popBackStack() }
            )
        }
    }
}

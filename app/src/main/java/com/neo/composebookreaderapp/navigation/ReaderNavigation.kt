package com.neo.composebookreaderapp.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.neo.composebookreaderapp.screens.ReaderSplashScreen
import com.neo.composebookreaderapp.screens.details.BookDetailsScreen
import com.neo.composebookreaderapp.screens.home.HomeScreen
import com.neo.composebookreaderapp.screens.home.ReaderHomeViewModel
import com.neo.composebookreaderapp.screens.login.LoginScreen
import com.neo.composebookreaderapp.screens.search.ReaderBookSearchViewModel
import com.neo.composebookreaderapp.screens.search.SearchScreen
import com.neo.composebookreaderapp.screens.stats.StatsScreen
import com.neo.composebookreaderapp.screens.update.BookUpdateScreen


@ExperimentalComposeUiApi
@Composable
fun ReaderNavigation() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = ReaderScreens.SplashScreen.name) {
        composable(route = ReaderScreens.SplashScreen.name) {
            ReaderSplashScreen(navController)
        }

        composable(ReaderScreens.LoginScreen.name) {
            LoginScreen(navController)
        }

        composable(ReaderScreens.CreateAccountScreen.name) {

        }

        composable(ReaderScreens.ReaderHomeScreen.name) {
            val homeViewModel = hiltViewModel<ReaderHomeViewModel>()
            HomeScreen(navController, homeViewModel)
        }

        composable(ReaderScreens.SearchScreen.name) {
            val viewModel = hiltViewModel<ReaderBookSearchViewModel>()
            SearchScreen(navController, viewModel)
        }


        composable("${ReaderScreens.DetailScreen.name}/{bookId}", arguments = listOf(navArgument(
            "bookId"
        ) {
            type = NavType.StringType
        }
        )) { backStackEntry ->
            backStackEntry.arguments?.getString("bookId").let { bookId ->
                BookDetailsScreen(navController, bookId = bookId.toString())
            }
        }


        composable(
            "${ReaderScreens.UpdateScreen.name}/{bookItemId}",
            arguments = listOf(navArgument("bookItemId"){
                type = NavType.StringType
            })
        ) { backStackEntry ->
            backStackEntry.arguments?.getString("bookItemId").let { bookItemId ->
                BookUpdateScreen(navController, bookItemId.toString())
            }
        }

        composable(ReaderScreens.ReaderStatsScreen.name) {
            StatsScreen(navController)
        }


    }

}
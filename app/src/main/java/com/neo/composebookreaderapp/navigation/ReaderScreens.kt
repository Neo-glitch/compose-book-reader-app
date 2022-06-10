package com.neo.composebookreaderapp.navigation

import com.neo.composebookreaderapp.screens.home.HomeScreen
import java.lang.IllegalArgumentException

enum class ReaderScreens {
    SplashScreen,
    LoginScreen,
    CreateAccountScreen,
    ReaderHomeScreen,
    SearchScreen,
    DetailScreen,
    UpdateScreen,
    ReaderStatsScreen;


    companion object {

        /**
         * takes a full route with it's argument attached to it and just gets
         * the route to the screen needed
         */
        fun fromRoute(route: String?): ReaderScreens {
            // string before delimiter which is the main navigation route
            return when (route?.substringBefore("/")) {
                SplashScreen.name -> SplashScreen
                LoginScreen.name -> LoginScreen
                CreateAccountScreen.name -> CreateAccountScreen
                ReaderHomeScreen.name -> ReaderHomeScreen
                SearchScreen.name -> SearchScreen
                DetailScreen.name -> DetailScreen
                UpdateScreen.name -> UpdateScreen
                ReaderStatsScreen.name -> ReaderStatsScreen
                null -> ReaderHomeScreen
                else -> throw IllegalArgumentException("Route: '$route' not found")
            }
        }

    }
}
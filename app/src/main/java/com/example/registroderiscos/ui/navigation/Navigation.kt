package com.example.registroderiscos.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.registroderiscos.ui.screens.HomeScreen
import com.example.registroderiscos.ui.screens.LoginScreen
import com.example.registroderiscos.ui.screens.RegisterRiskScreen

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "LoginScreen") {
        composable("LoginScreen") {
            LoginScreen(navController = navController)
        }
        composable("HomeScreen") {
            HomeScreen(navController = navController)
        }
        composable("RegisterRiskScreen") {
            RegisterRiskScreen()
        }
    }
}
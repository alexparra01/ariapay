package com.ariapay.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.ariapay.ui.screens.*

sealed class Screen(val route: String) {
    data object Splash : Screen("splash")
    data object Login : Screen("login")
    data object Home : Screen("home")
    data object Payment : Screen("payment")
    data object PaymentResult : Screen("payment_result/{success}/{message}") {
        fun createRoute(success: Boolean, message: String) = "payment_result/$success/$message"
    }
    data object TransactionHistory : Screen("transaction_history")
    data object Settings : Screen("settings")
}

@Composable
fun AriaPayNavHost(
    navController: NavHostController = rememberNavController()
) {
    NavHost(navController = navController, startDestination = Screen.Splash.route) {
        
        composable(Screen.Splash.route) {
            SplashScreen(
                onNavigateToLogin = {
                    navController.navigate(Screen.Login.route) { popUpTo(Screen.Splash.route) { inclusive = true } }
                },
                onNavigateToHome = {
                    navController.navigate(Screen.Home.route) { popUpTo(Screen.Splash.route) { inclusive = true } }
                }
            )
        }
        
        composable(Screen.Login.route) {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate(Screen.Home.route) { popUpTo(Screen.Login.route) { inclusive = true } }
                }
            )
        }
        
        composable(Screen.Home.route) {
            HomeScreen(
                onNavigateToPayment = { navController.navigate(Screen.Payment.route) },
                onNavigateToHistory = { navController.navigate(Screen.TransactionHistory.route) },
                onNavigateToSettings = { navController.navigate(Screen.Settings.route) }
            )
        }
        
        composable(Screen.Payment.route) {
            PaymentScreen(
                onPaymentComplete = { success, message ->
                    navController.navigate(Screen.PaymentResult.createRoute(success, message)) {
                        popUpTo(Screen.Payment.route) { inclusive = true }
                    }
                },
                onNavigateBack = { navController.popBackStack() }
            )
        }
        
        composable(Screen.PaymentResult.route) { backStackEntry ->
            val success = backStackEntry.arguments?.getString("success")?.toBoolean() ?: false
            val message = backStackEntry.arguments?.getString("message") ?: ""
            PaymentResultScreen(
                success = success,
                message = message,
                onNavigateToHome = {
                    navController.navigate(Screen.Home.route) { popUpTo(Screen.Home.route) { inclusive = true } }
                }
            )
        }
        
        composable(Screen.TransactionHistory.route) {
            TransactionHistoryScreen(onNavigateBack = { navController.popBackStack() })
        }
        
        composable(Screen.Settings.route) {
            SettingsScreen(
                onNavigateBack = { navController.popBackStack() },
                onLogout = {
                    navController.navigate(Screen.Login.route) { popUpTo(0) { inclusive = true } }
                }
            )
        }
    }
}

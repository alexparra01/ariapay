package com.ariapay.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.ariapay.ui.features.auth.LoginScreen
import com.ariapay.ui.features.SplashScreen
import com.ariapay.ui.features.auth.AuthenticationViewModel
import com.ariapay.ui.features.auth.BiometricAuthScreen
import com.ariapay.ui.features.auth.CreateAccountScreen
import com.ariapay.ui.features.auth.OtpVerificationScreen
import com.ariapay.ui.features.auth.PasscodeScreen
import com.ariapay.ui.features.auth.ProfileScreen
import com.ariapay.ui.features.home.HomeScreen
import com.ariapay.ui.features.onboard.OnboardScreen
import com.ariapay.ui.features.payment.PaymentScreen
import com.ariapay.ui.features.payment.PaymentResultScreen
import com.ariapay.ui.features.transactionhistory.TransactionHistoryScreen
import com.ariapay.ui.features.settings.SettingsScreen
import org.koin.androidx.compose.koinViewModel

sealed class Screen(val route: String) {
    data object Splash : Screen("splash")
    data object Login : Screen("login")
    data object Onboarding: Screen("onboarding")
    data object CreateAccount : Screen("create_account")
    data object OTPVerification : Screen("otp_verification/{phoneNumber}") {
        fun createRoute(phoneNumber: String) = "otp_verification/$phoneNumber"
    }
    data object Profile : Screen("profile")

    data object Passcode: Screen("passcode")
    data object BiometricAuth: Screen("biometric_auth")
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
                onNavigateToOnBoarding = {
                    navController.navigate(Screen.Onboarding.route) { popUpTo(Screen.Splash.route) { inclusive = true } }
                },
                onNavigateToHome = {
                    navController.navigate(Screen.Home.route) { popUpTo(Screen.Splash.route) { inclusive = true } }
                }
            )
        }

        composable(Screen.Onboarding.route) {
            OnboardScreen(
                onNavigateToLogin = {
                    navController.navigate(Screen.Login.route) { popUpTo(Screen.Splash.route) { inclusive = true } }
                },
                onNavigateToCreateAccount = {
                    navController.navigate(Screen.CreateAccount.route) { popUpTo(Screen.Splash.route) { inclusive = true } }
                }
            )
        }

        composable(Screen.CreateAccount.route) {
            val authViewModel: AuthenticationViewModel = koinViewModel()
            //TODO send otp code to the phone number once the user confirms the phone number
            CreateAccountScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToLogin = {
                    navController.navigate(Screen.Login.route) { popUpTo(Screen.CreateAccount.route) { inclusive = true } }
                },
                onCreateAccount = { phoneNumber, countryCode ->
                    navController.navigate(Screen.OTPVerification.createRoute("$countryCode$phoneNumber"))
                }
            )
        }

        composable(
            route = Screen.OTPVerification.route,
            arguments = listOf(navArgument("phoneNumber") { type = NavType.StringType })
        ) {
            //TODO Include code validation in the viewModel and resent code action. Also error handling when the code is invalid or expired
            OtpVerificationScreen(
                phoneNumber = it.arguments?.getString("phoneNumber") ?: "",
                errorMessage = "Invalid code. Please try again.",
                onNavigateBack = { navController.popBackStack() },
                onVerifyOtp = {  navController.navigate(Screen.Profile.route) { popUpTo(Screen.OTPVerification.route) { inclusive = true } } },
                onResendCode = {}
            )
        }

        composable(
            route = Screen.Profile.route,
        ) {
            ProfileScreen(
                onNavigateBack = { navController.popBackStack() }
            ){ firstName, lastName, email ->
                //TODO save profile data using API service
                navController.navigate(Screen.Passcode.route) { popUpTo(Screen.Profile.route) { inclusive = true } }
            }
        }

        composable(
            route = Screen.Passcode.route,
        ) {
            PasscodeScreen(
                onClose = { navController.popBackStack() },
                onPasscodeCreated = {
                    navController.navigate(Screen.BiometricAuth.route) { popUpTo(Screen.Passcode.route) { inclusive = true } }
                    //TODO Include storage of passcode when user creates the account. And passcode validation when the user is already sign in and it is going to do a payment or change profile
                }
            )
        }

        composable(
            route = Screen.BiometricAuth.route,
        ) {
            BiometricAuthScreen(
                onBiometricEnabled = { navController.navigate(Screen.Home.route) { popUpTo(Screen.Passcode.route) { inclusive = true } } },
                onSkip = { navController.navigate(Screen.Home.route) { popUpTo(Screen.Passcode.route) { inclusive = true } }  }
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

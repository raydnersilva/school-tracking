package com.schooltrack.mobile.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.schooltrack.mobile.data.network.TokenManager
import com.schooltrack.mobile.ui.screens.auth.ForgotPasswordScreen
import com.schooltrack.mobile.ui.screens.auth.LoginScreen
import com.schooltrack.mobile.ui.screens.auth.RegisterScreen
import com.schooltrack.mobile.ui.screens.dashboard.DashboardScreen
import com.schooltrack.mobile.ui.screens.notifications.NotificationsScreen
import com.schooltrack.mobile.ui.screens.schedule.ScheduleScreen
import com.schooltrack.mobile.ui.screens.settings.SettingsScreen
import com.schooltrack.mobile.ui.screens.chat.ChatScreen
import com.schooltrack.mobile.ui.screens.driver.DriverDashboardScreen
import com.schooltrack.mobile.ui.screens.rating.RatingScreen

object Routes {
    const val LOGIN = "login"
    const val REGISTER = "register"
    const val FORGOT_PASSWORD = "forgot_password"
    const val DASHBOARD = "dashboard"
    const val NOTIFICATIONS = "notifications"
    const val SCHEDULE = "schedule"
    const val SETTINGS = "settings"
    const val CHAT = "chat"
    const val DRIVER = "driver"
    const val RATING = "rating"
}

@Composable
fun AppNavigation(tokenManager: TokenManager) {
    val navController = rememberNavController()
    val startDestination = if (tokenManager.isAuthenticated) Routes.DASHBOARD else Routes.LOGIN

    NavHost(navController = navController, startDestination = startDestination) {
        composable(Routes.LOGIN) {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate(Routes.DASHBOARD) {
                        popUpTo(Routes.LOGIN) { inclusive = true }
                    }
                },
                onNavigateToRegister = { navController.navigate(Routes.REGISTER) },
                onNavigateToForgotPassword = { navController.navigate(Routes.FORGOT_PASSWORD) },
            )
        }

        composable(Routes.REGISTER) {
            RegisterScreen(
                onRegisterSuccess = {
                    navController.navigate(Routes.DASHBOARD) {
                        popUpTo(Routes.LOGIN) { inclusive = true }
                    }
                },
                onNavigateToLogin = { navController.popBackStack() },
            )
        }

        composable(Routes.FORGOT_PASSWORD) {
            ForgotPasswordScreen(
                onNavigateToLogin = { navController.popBackStack() },
            )
        }

        composable(Routes.DASHBOARD) {
            DashboardScreen(
                onNavigateToNotifications = { navController.navigate(Routes.NOTIFICATIONS) },
                onNavigateToSchedule = { navController.navigate(Routes.SCHEDULE) },
                onNavigateToSettings = { navController.navigate(Routes.SETTINGS) },
                onNavigateToChat = { navController.navigate(Routes.CHAT) },
                onNavigateToDriver = { navController.navigate(Routes.DRIVER) },
                onNavigateToRating = { navController.navigate(Routes.RATING) },
                onLogout = {
                    tokenManager.clearAuth()
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(0) { inclusive = true }
                    }
                },
            )
        }

        composable(Routes.NOTIFICATIONS) {
            NotificationsScreen(onBack = { navController.popBackStack() })
        }

        composable(Routes.SCHEDULE) {
            ScheduleScreen(onBack = { navController.popBackStack() })
        }

        composable(Routes.SETTINGS) {
            SettingsScreen(
                onBack = { navController.popBackStack() },
                onLogout = {
                    tokenManager.clearAuth()
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(0) { inclusive = true }
                    }
                },
            )
        }

        composable(Routes.CHAT) {
            ChatScreen(onBack = { navController.popBackStack() })
        }

        composable(Routes.DRIVER) {
            DriverDashboardScreen(onBack = { navController.popBackStack() })
        }

        composable(Routes.RATING) {
            RatingScreen(onBack = { navController.popBackStack() })
        }
    }
}

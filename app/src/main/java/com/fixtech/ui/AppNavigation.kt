package com.fixtech.ui

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.fixtech.ui.screens.GuideDetailScreen
import com.fixtech.ui.screens.HomeScreen
import com.fixtech.ui.screens.LoginScreen
import com.fixtech.ui.screens.ProfileScreen
import com.fixtech.ui.screens.RegisterScreen
import com.fixtech.ui.screens.RepairStepScreen
import com.fixtech.ui.screens.SplashScreen

sealed class Screen(val route: String) {
    data object Splash : Screen("splash")
    data object Login : Screen("login")
    data object Register : Screen("register")
    data object Home : Screen("home")
    data object Profile : Screen("profile")
    data object GuideDetail : Screen("guide_detail")
    data object RepairStep : Screen("repair_step")
}

@Composable
fun AppNavigation(
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route
    ) {
        composable(Screen.Splash.route) {
            SplashScreen(
                onNavigateToLogin = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Login.route) {
            LoginScreen(
                onNavigateToHome = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                },
                onNavigateToRegister = {
                    navController.navigate(Screen.Register.route)
                }
            )
        }

        composable(Screen.Register.route) {
            RegisterScreen(
                onNavigateToLogin = {
                    navController.popBackStack()
                },
                onRegisterSuccess = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Home.route) {
            HomeScreen(
                onNavigateToGuideDetail = {
                    navController.navigate(Screen.GuideDetail.route)
                },
                onNavigateToProfile = {
                    navController.navigate(Screen.Profile.route)
                }
            )
        }

        composable(Screen.Profile.route) {
            ProfileScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onLogout = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.GuideDetail.route) {
            GuideDetailScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onStartRepair = {
                    navController.navigate(Screen.RepairStep.route)
                }
            )
        }

        composable(Screen.RepairStep.route) {
            RepairStepScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}

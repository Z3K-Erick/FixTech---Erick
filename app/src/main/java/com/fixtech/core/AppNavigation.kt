package com.fixtech.core

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.fixtech.core.components.SplashScreen
import com.fixtech.feature.auth.ui.LoginScreen
import com.fixtech.feature.auth.ui.RegisterScreen
import com.fixtech.feature.auth.ui.TechLoginScreen
import com.fixtech.feature.auth.ui.TechRegisterScreen
import com.fixtech.feature.guide.ui.GuideDetailScreen
import com.fixtech.feature.guide.ui.RepairStepScreen
import com.fixtech.feature.home.ui.HomeScreen
import com.fixtech.feature.profile.ui.ProfileScreen
import com.fixtech.feature.profile.ui.TechProfileScreen

sealed class Screen(val route: String) {
    data object Splash : Screen("splash")
    data object Login : Screen("login")
    data object Register : Screen("register")
    data object TechLogin : Screen("tech_login")
    data object TechRegister : Screen("tech_register")
    data object Home : Screen("home")
    data object Profile : Screen("profile")
    data object TechProfile : Screen("tech_profile")
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
                },
                onNavigateToTechLogin = {
                    navController.navigate(Screen.TechLogin.route)
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
                },
                onNavigateToTechRegister = {
                    navController.navigate(Screen.TechRegister.route)
                }
            )
        }

        composable(Screen.TechLogin.route) {
            TechLoginScreen(
                onNavigateToTechProfile = {
                    navController.navigate(Screen.TechProfile.route) {
                        popUpTo(Screen.TechLogin.route) { inclusive = true }
                    }
                },
                onNavigateToTechRegister = {
                    navController.navigate(Screen.TechRegister.route)
                },
                onNavigateToUserLogin = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.TechLogin.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.TechRegister.route) {
            TechRegisterScreen(
                onNavigateToTechLogin = {
                    navController.popBackStack()
                },
                onRegisterSuccess = {
                    navController.navigate(Screen.TechProfile.route) {
                        popUpTo(Screen.TechLogin.route) { inclusive = true }
                    }
                },
                onNavigateToUserRegister = {
                    navController.navigate(Screen.Register.route)
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
                onNavigateToHome = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Home.route) { inclusive = true }
                    }
                },
                onLogout = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.TechProfile.route) {
            TechProfileScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onNavigateToHome = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Home.route) { inclusive = true }
                    }
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

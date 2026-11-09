package com.fixtech.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.fixtech.core.ui.components.SplashScreen
import com.fixtech.core.ui.theme.BackgroundDark
import com.fixtech.di.FixTechViewModelFactory
import com.fixtech.presentation.auth.AuthViewModel
import com.fixtech.presentation.auth.LoginScreen
import com.fixtech.presentation.auth.RegisterScreen
import com.fixtech.presentation.auth.TechLoginScreen
import com.fixtech.presentation.auth.TechRegisterScreen
import com.fixtech.presentation.guide.CreateGuideScreen
import com.fixtech.presentation.guide.DeviceGuidesScreen
import com.fixtech.presentation.guide.GuideDetailScreen
import com.fixtech.presentation.guide.GuideViewModel
import com.fixtech.presentation.guide.RepairStepScreen
import com.fixtech.presentation.home.AddDeviceScreen
import com.fixtech.presentation.home.DiagnosticScreen
import com.fixtech.presentation.home.HomeScreen
import com.fixtech.presentation.home.HomeViewModel
import com.fixtech.presentation.home.TechHomeScreen
import com.fixtech.presentation.profile.ProfileScreen
import com.fixtech.presentation.profile.ProfileViewModel
import com.fixtech.presentation.profile.PublicTechProfileScreen
import com.fixtech.presentation.profile.TechProfileScreen

@Composable
fun AppNavGraph() {
    val navController = rememberNavController()
    val authViewModel: AuthViewModel = viewModel(factory = FixTechViewModelFactory)
    val homeViewModel: HomeViewModel = viewModel(factory = FixTechViewModelFactory)
    val guideViewModel: GuideViewModel = viewModel(factory = FixTechViewModelFactory)
    val profileViewModel: ProfileViewModel = viewModel(factory = FixTechViewModelFactory)

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = BackgroundDark
    ) {
        NavHost(navController = navController, startDestination = Routes.Splash.route) {
            composable(Routes.Splash.route) {
                SplashScreen(
                    authViewModel = authViewModel,
                    onNavigateToLogin = {
                        navController.navigate(Routes.Login.route) {
                            popUpTo(Routes.Splash.route) { inclusive = true }
                        }
                    },
                    onNavigateToHome = {
                        navController.navigate(Routes.Home.route) {
                            popUpTo(0) { inclusive = true }
                        }
                    },
                    onNavigateToTechHome = {
                        navController.navigate(Routes.TechHome.route) {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                )
            }

            composable(Routes.Login.route) {
                LoginScreen(
                    authViewModel = authViewModel,
                    onNavigateToHome = {
                        navController.navigate(Routes.Home.route) {
                            popUpTo(Routes.Login.route) { inclusive = true }
                        }
                    },
                    onNavigateToRegister = {
                        navController.navigate(Routes.Register.route)
                    },
                    onNavigateToTechLogin = {
                        navController.navigate(Routes.TechLogin.route)
                    }
                )
            }

            composable(Routes.Register.route) {
                RegisterScreen(
                    authViewModel = authViewModel,
                    onRegisterSuccess = {
                        navController.navigate(Routes.Home.route) {
                            popUpTo(Routes.Login.route) { inclusive = true }
                        }
                    },
                    onNavigateToLogin = {
                        navController.popBackStack()
                    },
                    onNavigateToTechRegister = {
                        navController.navigate(Routes.TechRegister.route)
                    }
                )
            }

            composable(Routes.TechLogin.route) {
                TechLoginScreen(
                    authViewModel = authViewModel,
                    onNavigateToTechHome = {
                        navController.navigate(Routes.TechHome.route) {
                            popUpTo(Routes.Login.route) { inclusive = true }
                        }
                    },
                    onNavigateToTechProfile = {
                        navController.navigate(Routes.TechProfile.route) {
                            popUpTo(Routes.Login.route) { inclusive = true }
                        }
                    },
                    onNavigateToTechRegister = {
                        navController.navigate(Routes.TechRegister.route)
                    },
                    onNavigateToUserLogin = {
                        navController.popBackStack()
                    }
                )
            }

            composable(Routes.TechRegister.route) {
                TechRegisterScreen(
                    authViewModel = authViewModel,
                    onRegisterSuccess = {
                        navController.navigate(Routes.TechHome.route) {
                            popUpTo(Routes.Login.route) { inclusive = true }
                        }
                    },
                    onNavigateToTechLogin = {
                        navController.popBackStack()
                    },
                    onNavigateToUserRegister = {
                        navController.navigate(Routes.Register.route)
                    }
                )
            }

            composable(Routes.Home.route) {
                HomeScreen(
                    homeViewModel = homeViewModel,
                    guideViewModel = guideViewModel,
                    profileViewModel = profileViewModel,
                    onNavigateToDeviceGuides = { deviceId ->
                        navController.navigate(Routes.DeviceGuides.createRoute(deviceId))
                    },
                    onNavigateToGuideDetail = { guideId ->
                        navController.navigate(Routes.GuideDetail.createRoute(guideId))
                    },
                    onNavigateToAddDevice = {
                        navController.navigate(Routes.AddDevice.route)
                    },
                    onNavigateToDiagnostic = {
                        navController.navigate(Routes.Diagnostic.route)
                    },
                    onNavigateToProfile = {
                        navController.navigate(Routes.Profile.route)
                    },
                    onLogout = {
                        profileViewModel.clearSession()
                        navController.navigate(Routes.Login.route) {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                )
            }

            composable(Routes.TechHome.route) {
                TechHomeScreen(
                    homeViewModel = homeViewModel,
                    guideViewModel = guideViewModel,
                    profileViewModel = profileViewModel,
                    onNavigateBack = {
                        val popped = navController.popBackStack()
                        if (!popped) {
                            profileViewModel.clearSession()
                            navController.navigate(Routes.Login.route) {
                                popUpTo(0) { inclusive = true }
                            }
                        }
                    },
                    onNavigateToAddDevice = {
                        navController.navigate(Routes.AddDevice.route)
                    },
                    onNavigateToDeviceGuides = { deviceId ->
                        navController.navigate(Routes.DeviceGuides.createRoute(deviceId))
                    },
                    onNavigateToGuideDetail = { guideId ->
                        navController.navigate(Routes.TechGuideDetail.createRoute(guideId))
                    },
                    onNavigateToCreateGuide = {
                        navController.navigate(Routes.CreateGuide.route)
                    },
                    onNavigateToTechProfile = {
                        navController.navigate(Routes.TechProfile.route)
                    },
                    onLogout = {
                        profileViewModel.clearSession()
                        navController.navigate(Routes.Login.route) {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                )
            }

            composable(Routes.AddDevice.route) {
                AddDeviceScreen(
                    homeViewModel = homeViewModel,
                    onNavigateBack = { navController.popBackStack() },
                    onSaveDevice = { navController.popBackStack() }
                )
            }

            composable(Routes.Diagnostic.route) {
                DiagnosticScreen(
                    homeViewModel = homeViewModel,
                    guideViewModel = guideViewModel,
                    onNavigateBack = { navController.popBackStack() },
                    onOpenRecommendedGuide = { guideId ->
                        navController.navigate(Routes.GuideDetail.createRoute(guideId)) {
                            popUpTo(Routes.Diagnostic.route) { inclusive = true }
                        }
                    }
                )
            }

            composable(
                route = Routes.DeviceGuides.route,
                arguments = listOf(navArgument(Routes.DeviceGuides.ARG_DEVICE_ID) { type = NavType.StringType })
            ) { backStackEntry ->
                val deviceId = backStackEntry.arguments?.getString(Routes.DeviceGuides.ARG_DEVICE_ID)
                DeviceGuidesScreen(
                    deviceIdArg = deviceId,
                    homeViewModel = homeViewModel,
                    guideViewModel = guideViewModel,
                    onNavigateBack = { navController.popBackStack() },
                    onSelectGuide = { guideId ->
                        navController.navigate(Routes.GuideDetail.createRoute(guideId))
                    },
                    onViewTechProfile = { techId ->
                        navController.navigate(Routes.PublicTechProfile.createRoute(techId))
                    }
                )
            }

            composable(
                route = Routes.PublicTechProfile.route,
                arguments = listOf(navArgument(Routes.PublicTechProfile.ARG_TECH_ID) { type = NavType.StringType })
            ) { backStackEntry ->
                val techId = backStackEntry.arguments?.getString(Routes.PublicTechProfile.ARG_TECH_ID)
                PublicTechProfileScreen(
                    techIdArg = techId,
                    homeViewModel = homeViewModel,
                    guideViewModel = guideViewModel,
                    profileViewModel = profileViewModel,
                    onNavigateBack = { navController.popBackStack() },
                    onSelectGuide = { guideId ->
                        navController.navigate(Routes.GuideDetail.createRoute(guideId))
                    }
                )
            }

            composable(Routes.Profile.route) {
                ProfileScreen(
                    profileViewModel = profileViewModel,
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToHome = {
                        navController.navigate(Routes.Home.route) {
                            popUpTo(Routes.Home.route) { inclusive = true }
                        }
                    },
                    onLogout = {
                        profileViewModel.clearSession()
                        navController.navigate(Routes.Login.route) {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                )
            }

            composable(Routes.TechProfile.route) {
                TechProfileScreen(
                    profileViewModel = profileViewModel,
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToHome = {
                        navController.navigate(Routes.TechHome.route) {
                            popUpTo(Routes.TechHome.route) { inclusive = true }
                        }
                    },
                    onLogout = {
                        profileViewModel.clearSession()
                        navController.navigate(Routes.Login.route) {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                )
            }

            composable(
                route = Routes.GuideDetail.route,
                arguments = listOf(navArgument(Routes.GuideDetail.ARG_GUIDE_ID) { type = NavType.StringType })
            ) { backStackEntry ->
                val guideId = backStackEntry.arguments?.getString(Routes.GuideDetail.ARG_GUIDE_ID)
                GuideDetailScreen(
                    guideIdArg = guideId,
                    guideViewModel = guideViewModel,
                    homeViewModel = homeViewModel,
                    onNavigateBack = { navController.popBackStack() },
                    onStartRepair = { selectedGuideId ->
                        navController.navigate(Routes.RepairStep.createRoute(selectedGuideId))
                    },
                    onViewTechProfile = { techId ->
                        navController.navigate(Routes.PublicTechProfile.createRoute(techId))
                    }
                )
            }

            composable(
                route = Routes.TechGuideDetail.route,
                arguments = listOf(navArgument(Routes.TechGuideDetail.ARG_GUIDE_ID) { type = NavType.StringType })
            ) { backStackEntry ->
                val guideId = backStackEntry.arguments?.getString(Routes.TechGuideDetail.ARG_GUIDE_ID)
                GuideDetailScreen(
                    guideIdArg = guideId,
                    guideViewModel = guideViewModel,
                    homeViewModel = homeViewModel,
                    onNavigateBack = { navController.popBackStack() },
                    onStartRepair = { selectedGuideId ->
                        navController.navigate(Routes.RepairStep.createRoute(selectedGuideId))
                    },
                    onViewTechProfile = { techId ->
                        navController.navigate(Routes.PublicTechProfile.createRoute(techId))
                    }
                )
            }

            composable(
                route = Routes.RepairStep.route,
                arguments = listOf(navArgument(Routes.RepairStep.ARG_GUIDE_ID) { type = NavType.StringType })
            ) { backStackEntry ->
                val guideId = backStackEntry.arguments?.getString(Routes.RepairStep.ARG_GUIDE_ID)
                RepairStepScreen(
                    guideIdArg = guideId,
                    guideViewModel = guideViewModel,
                    onNavigateBack = { navController.popBackStack() },
                    onFinishGuide = {
                        val poppedToTech = navController.popBackStack(Routes.TechHome.route, inclusive = false)
                        if (!poppedToTech) {
                            val poppedToHome = navController.popBackStack(Routes.Home.route, inclusive = false)
                            if (!poppedToHome) {
                                navController.navigate(Routes.Home.route) {
                                    popUpTo(0) { inclusive = true }
                                }
                            }
                        }
                    }
                )
            }

            composable(Routes.CreateGuide.route) {
                CreateGuideScreen(
                    guideViewModel = guideViewModel,
                    homeViewModel = homeViewModel,
                    onNavigateBack = { navController.popBackStack() },
                    onPublishGuide = {
                        navController.popBackStack()
                    }
                )
            }
        }
    }
}

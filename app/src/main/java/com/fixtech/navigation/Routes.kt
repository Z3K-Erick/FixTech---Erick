package com.fixtech.navigation

sealed class Routes(val route: String) {
    data object Splash : Routes("splash")
    data object Login : Routes("login")
    data object Register : Routes("register")
    data object TechLogin : Routes("tech_login")
    data object TechRegister : Routes("tech_register")
    data object Home : Routes("home")
    data object TechHome : Routes("tech_home")
    data object AddDevice : Routes("add_device")
    data object Diagnostic : Routes("diagnostic")

    data object DeviceGuides : Routes("device_guides/{deviceId}") {
        const val ARG_DEVICE_ID = "deviceId"
        fun createRoute(deviceId: String): String = "device_guides/$deviceId"
    }

    data object PublicTechProfile : Routes("public_tech_profile/{techId}") {
        const val ARG_TECH_ID = "techId"
        fun createRoute(techId: String): String = "public_tech_profile/$techId"
    }

    data object Profile : Routes("profile")
    data object TechProfile : Routes("tech_profile")

    data object GuideDetail : Routes("guide_detail/{guideId}") {
        const val ARG_GUIDE_ID = "guideId"
        fun createRoute(guideId: String): String = "guide_detail/$guideId"
    }

    data object TechGuideDetail : Routes("tech_guide_detail/{guideId}") {
        const val ARG_GUIDE_ID = "guideId"
        fun createRoute(guideId: String): String = "tech_guide_detail/$guideId"
    }

    data object RepairStep : Routes("repair_step/{guideId}") {
        const val ARG_GUIDE_ID = "guideId"
        fun createRoute(guideId: String): String = "repair_step/$guideId"
    }

    data object CreateGuide : Routes("create_guide")
}

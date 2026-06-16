package com.kalbim.ui.screens

import androidx.compose.runtime.*
import androidx.navigation.compose.*
import com.kalbim.viewmodel.KalbimViewModel

sealed class Screen(val route: String) {
    object Splash      : Screen("splash")
    object Legal       : Screen("legal")
    object Onboarding  : Screen("onboarding")
    object Main        : Screen("main")
}

sealed class Tab(val route: String, val labelKey: String, val icon: String) {
    object Home    : Tab("home",    "nav_home",    "home")
    object Profile : Tab("profile", "nav_profile", "person")
    object List    : Tab("list",    "nav_list",    "list")
    object Chart   : Tab("chart",   "nav_chart",   "bar_chart")
    object Report  : Tab("report",  "nav_report",  "picture_as_pdf")
}
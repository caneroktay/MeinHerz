package com.kalbim.ui.screens

import androidx.compose.runtime.*
import com.kalbim.viewmodel.KalbimViewModel

@Composable
fun AppRoot(vm: KalbimViewModel) {
    val profile by vm.profile.collectAsState()

    when {
        profile == null            -> SplashLegalScreen(vm)
        !profile!!.onboardingDone  -> OnboardingScreen(vm)
        else                       -> MainScaffold(vm)
    }
}
package com.kalbim

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.core.os.LocaleListCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import com.kalbim.data.db.KalbimDatabase
import com.kalbim.notification.LanguageManager
import com.kalbim.notification.NotificationHelper
import com.kalbim.notification.ReminderScheduler
import com.kalbim.ui.screens.AppRoot
import com.kalbim.ui.theme.KalbimTheme
import com.kalbim.viewmodel.KalbimViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density

class MainActivity : ComponentActivity() {
    private val vm: KalbimViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)

        // Dil ayarını uygula
        val savedLang = runBlocking {
            val profile = KalbimDatabase.getInstance(applicationContext)
                .dao().getProfile().first()
            profile?.language ?: LanguageManager.getDeviceLanguage()
        }
        AppCompatDelegate.setApplicationLocales(
            LocaleListCompat.forLanguageTags(savedLang)
        )

        enableEdgeToEdge()
        WindowCompat.setDecorFitsSystemWindows(window, false)
        NotificationHelper.createChannel(this)
        ReminderScheduler.scheduleAll(this)

        setContent {
            // 1. Tema Durumunu Belirle
            val profile by vm.profile.collectAsState()
            val isDark = when (profile?.themeMode ?: "auto") {
                "dark"  -> true
                "light" -> false
                else    -> isSystemInDarkTheme()
            }

            // 2. Durum Çubuğu (Status Bar) İkon Rengini Yönet
            val windowInsetsController = WindowCompat.getInsetsController(window, window.decorView)
            LaunchedEffect(isDark) {
                // isAppearanceLightStatusBars = true ise ikonlar SİYAH olur (Açık tema)
                // isAppearanceLightStatusBars = false ise ikonlar BEYAZ olur (Koyu tema)
                windowInsetsController.isAppearanceLightStatusBars = !isDark
            }

            // 3. Font ve Tema Provider'ları
            val overriddenDensity = Density(
                density = LocalDensity.current.density,
                fontScale = 1.0f
            )

            CompositionLocalProvider(LocalDensity provides overriddenDensity) {
                KalbimTheme(darkTheme = isDark) {
                    AppRoot(vm)
                }
            }

            // Diğer LaunchedEffect'leriniz burada kalabilir
            LaunchedEffect(profile) {
                profile?.let { p ->
                    ReminderScheduler.scheduleAll(this@MainActivity, p)
                }
            }
        }
    }
}
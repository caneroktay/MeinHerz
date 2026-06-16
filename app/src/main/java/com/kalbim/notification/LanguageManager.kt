package com.kalbim.notification

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import java.util.Locale

object LanguageManager {

    private val supportedLanguages = listOf("tr", "de", "en")

    fun getDeviceLanguage(): String {
        val deviceLang = Locale.getDefault().language
        return if (deviceLang in supportedLanguages) deviceLang else "en"
    }

    fun applyLanguage(languageCode: String) {
        val localeList = LocaleListCompat.forLanguageTags(languageCode)
        AppCompatDelegate.setApplicationLocales(localeList)
    }

    fun getCurrentLanguage(): String {
        val current = AppCompatDelegate.getApplicationLocales()
        return if (current.isEmpty) getDeviceLanguage()
        else current[0]?.language ?: getDeviceLanguage()
    }

    // Activity'yi tamamen yeniden başlat
    fun restartActivity(activity: Activity) {
        val intent = activity.intent
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
        activity.finish()
        activity.startActivity(intent)
    }
}
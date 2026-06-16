package com.kalbim.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.kalbim.notification.LanguageManager

@Entity(tableName = "user_profile")
data class UserProfile(
    @PrimaryKey val id: Int = 1,
    val firstName: String = "",
    val lastName: String = "",
    val birthDate: String = "",
    val weightKg: Float = 0f,
    val medicalHistory: String = "",
    val medications: String = "",
    val language: String = LanguageManager.getDeviceLanguage(),
    val themeMode: String = "auto",       //  "auto", "light", "dark"
    val notifMorningHour: Int = 7,
    val notifMorningMin: Int = 0,
    val notifNoonHour: Int = 12,
    val notifNoonMin: Int = 0,
    val notifEveningHour: Int = 20,
    val notifEveningMin: Int = 0,
    val onboardingDone: Boolean = false
)
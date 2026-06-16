package com.kalbim.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

val KalbimTypography = Typography(
    headlineLarge  = TextStyle(fontSize = 28.sp, fontWeight = FontWeight.Bold,     lineHeight = 34.sp),
    headlineMedium = TextStyle(fontSize = 24.sp, fontWeight = FontWeight.Bold,     lineHeight = 30.sp),
    headlineSmall  = TextStyle(fontSize = 20.sp, fontWeight = FontWeight.SemiBold, lineHeight = 26.sp),
    titleLarge     = TextStyle(fontSize = 20.sp, fontWeight = FontWeight.SemiBold, lineHeight = 26.sp),
    titleMedium    = TextStyle(fontSize = 18.sp, fontWeight = FontWeight.SemiBold, lineHeight = 24.sp),
    titleSmall     = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.Medium,   lineHeight = 22.sp),
    bodyLarge      = TextStyle(fontSize = 17.sp, fontWeight = FontWeight.Normal,   lineHeight = 24.sp),
    bodyMedium     = TextStyle(fontSize = 15.sp, fontWeight = FontWeight.Normal,   lineHeight = 22.sp),
    bodySmall      = TextStyle(fontSize = 13.sp, fontWeight = FontWeight.Normal,   lineHeight = 18.sp),
    labelLarge     = TextStyle(fontSize = 17.sp, fontWeight = FontWeight.SemiBold, lineHeight = 22.sp),
    labelMedium    = TextStyle(fontSize = 15.sp, fontWeight = FontWeight.Medium,   lineHeight = 20.sp),
    labelSmall     = TextStyle(fontSize = 13.sp, fontWeight = FontWeight.Medium,   lineHeight = 18.sp),
)

private val LightColors = lightColorScheme(
    primary             = NavyPrimary,
    onPrimary           = Color.White,
    primaryContainer    = NavyLight,
    onPrimaryContainer  = Color.White,
    secondary           = AccentTeal,
    onSecondary         = Color.White,
    background          = LightBackground,
    onBackground        = LightTextPrimary,
    surface             = LightSurface,
    onSurface           = LightTextPrimary,
    surfaceVariant      = LightCard,
    onSurfaceVariant    = LightTextSecondary,
    outline             = LightBorder,
    error               = AccentRed,
    onError             = Color.White,
)

private val DarkColors = darkColorScheme(
    primary             = NavyLight,
    onPrimary           = Color.White,
    primaryContainer    = NavyDark,
    onPrimaryContainer  = DarkTextPrimary,
    secondary           = AccentTeal,
    onSecondary         = Color.White,
    background          = DarkBackground,
    onBackground        = DarkTextPrimary,
    surface             = DarkSurface,
    onSurface           = DarkTextPrimary,
    surfaceVariant      = DarkCard,
    onSurfaceVariant    = DarkTextSecondary,
    outline             = DarkBorder,
    error               = AccentRed,
    onError             = Color.White,
)

@Composable
fun KalbimTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColors else LightColors,
        typography  = KalbimTypography,
        content     = content
    )
}
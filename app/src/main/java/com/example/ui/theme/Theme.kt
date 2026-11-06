package com.example.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val ArenaDarkColorScheme = darkColorScheme(
    primary = CrimsonNeon,
    onPrimary = ArenaBgDark,
    primaryContainer = CrimsonNeonDark,
    onPrimaryContainer = TextPrimary,
    secondary = CyanNeon,
    onSecondary = ArenaBgDark,
    secondaryContainer = CyanNeonDark,
    onSecondaryContainer = TextPrimary,
    tertiary = GoldNeon,
    onTertiary = ArenaBgDark,
    background = ArenaBgDark,
    onBackground = TextPrimary,
    surface = ArenaSurfaceDark,
    onSurface = TextPrimary,
    surfaceVariant = ArenaSurfaceElevated,
    onSurfaceVariant = TextSecondary,
    outline = ArenaBorder,
    outlineVariant = ArenaBorderGlow
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true, // Default to Dark OLED theme as required by PRD
    dynamicColor: Boolean = false, // Keep high-contrast arena identity
    content: @Composable () -> Unit
) {
    val colorScheme = ArenaDarkColorScheme
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = ArenaBgDark.toArgb()
            window.navigationBarColor = ArenaBgDark.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
            WindowCompat.getInsetsController(window, view).isAppearanceLightNavigationBars = false
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

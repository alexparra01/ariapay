package com.ariapay.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

object AriaPayColors {
    val Primary = Color(0xFF1A237E)
    val PrimaryLight = Color(0xFF534BAE)
    val Secondary = Color(0xFF00BFA5)
    val SecondaryLight = Color(0xFF5DF2D6)
    val Success = Color(0xFF4CAF50)
    val Error = Color(0xFFE53935)
    val Warning = Color(0xFFFF9800)
    val BackgroundLight = Color(0xFFF5F5F5)
    val BackgroundDark = Color(0xFF121212)
    val SurfaceLight = Color(0xFFFFFFFF)
    val SurfaceDark = Color(0xFF1E1E1E)
}

private val LightColorScheme = lightColorScheme(
    primary = AriaPayColors.Primary,
    onPrimary = Color.White,
    secondary = AriaPayColors.Secondary,
    onSecondary = Color.Black,
    error = AriaPayColors.Error,
    onError = Color.White,
    background = AriaPayColors.BackgroundLight,
    onBackground = Color(0xFF1C1B1F),
    surface = AriaPayColors.SurfaceLight,
    onSurface = Color(0xFF1C1B1F)
)

private val DarkColorScheme = darkColorScheme(
    primary = AriaPayColors.PrimaryLight,
    onPrimary = Color.Black,
    secondary = AriaPayColors.SecondaryLight,
    onSecondary = Color.Black,
    error = AriaPayColors.Error,
    onError = Color.White,
    background = AriaPayColors.BackgroundDark,
    onBackground = Color(0xFFE6E1E5),
    surface = AriaPayColors.SurfaceDark,
    onSurface = Color(0xFFE6E1E5)
)

@Composable
fun AriaPayTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    val view = LocalView.current
    
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography(),
        content = content
    )
}

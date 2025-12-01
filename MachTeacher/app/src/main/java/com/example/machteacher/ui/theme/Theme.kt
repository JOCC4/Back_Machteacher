package com.example.machteacher.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import com.example.machteacher.ui.design.AppColors
import com.example.machteacher.ui.design.AppTypography

// 🎨 Esquema de colores claros y oscuros
private val DarkColorScheme = darkColorScheme(
    primary = AppColors.PrimaryBlue,
    secondary = AppColors.SecondaryBlue,
    background = AppColors.TextPrimary, // Usando el color unificado
    onBackground = AppColors.BackgroundLight
)

private val LightColorScheme = lightColorScheme(
    primary = AppColors.PrimaryBlue,
    secondary = AppColors.SecondaryBlue,
    background = AppColors.BackgroundLight,
    onBackground = AppColors.TextPrimary // Usando el color unificado
)

// 🌗 Tema principal de la app
@Composable
fun MachTeacherTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = AppTypography, // Usando la tipografía unificada
        content = content
    )
}

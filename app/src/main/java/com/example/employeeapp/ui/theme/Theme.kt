package com.example.employeeapp.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = Purple80,
    secondary = PurpleGrey80,
    tertiary = Pink80
)

private val LightColorScheme = lightColorScheme(
    primary = Purple40,
    secondary = PurpleGrey40,
    tertiary = Pink40,

    /* Other default colors to override */
    background = Color(0xFFFFFBFE),
    surface = Color(0xFFFFFBFE),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color(0xFF1C1B1F),
    onSurface = Color(0xFF1C1B1F)
)

/**
 * Theme utama aplikasi EmployeeApp
 * Mendukung light mode dan dark mode
 */
@Composable
fun EmployeeAppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(), // Ikuti setting sistem
    content: @Composable () -> Unit
) {
    // Warna berdasarkan mode theme kustom aplikasi
    val colorScheme = if (darkTheme) {
        darkColorScheme(
            primary = Primary80,
            secondary = Secondary80,
            error = ErrorDark,
            background = SurfaceDark,
            surface = SurfaceDark
        )
    } else {
        lightColorScheme(
            primary = Primary40,
            secondary = Secondary40,
            error = ErrorLight,
            background = SurfaceLight,
            surface = SurfaceLight
        )
    }

    // Aplikasikan Material Theme dengan colorScheme yang sudah ditentukan
    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography, // Menggunakan objek Typography bawaan/kustom
        content = content
    )
}
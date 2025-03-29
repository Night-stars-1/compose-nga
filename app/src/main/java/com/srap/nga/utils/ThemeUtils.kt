package com.srap.nga.utils

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

object ThemeUtils {
    @Composable
    fun getThemeColors(): Map<String, String> {
        val colors = MaterialTheme.colorScheme
        return mapOf(
            "primary" to colorToHex(colors.primary),
            "onPrimary" to colorToHex(colors.onPrimary),
            "primaryContainer" to colorToHex(colors.primaryContainer),
            "onPrimaryContainer" to colorToHex(colors.onPrimaryContainer),
            "secondary" to colorToHex(colors.secondary),
            "onSecondary" to colorToHex(colors.onSecondary),
            "secondaryContainer" to colorToHex(colors.secondaryContainer),
            "onSecondaryContainer" to colorToHex(colors.onSecondaryContainer),
            "tertiary" to colorToHex(colors.tertiary),
            "onTertiary" to colorToHex(colors.onTertiary),
            "tertiaryContainer" to colorToHex(colors.tertiaryContainer),
            "onTertiaryContainer" to colorToHex(colors.onTertiaryContainer),
            "error" to colorToHex(colors.error),
            "onError" to colorToHex(colors.onError),
            "errorContainer" to colorToHex(colors.errorContainer),
            "onErrorContainer" to colorToHex(colors.onErrorContainer),
            "background" to colorToHex(colors.background),
            "onBackground" to colorToHex(colors.onBackground),
            "surface" to colorToHex(colors.surface),
            "onSurface" to colorToHex(colors.onSurface),
            "surfaceVariant" to colorToHex(colors.surfaceVariant),
            "onSurfaceVariant" to colorToHex(colors.onSurfaceVariant),
            "outline" to colorToHex(colors.outline),
            "outlineVariant" to colorToHex(colors.outlineVariant),
            "scrim" to colorToHex(colors.scrim),
            "inverseSurface" to colorToHex(colors.inverseSurface),
            "inverseOnSurface" to colorToHex(colors.inverseOnSurface),
            "inversePrimary" to colorToHex(colors.inversePrimary),
            "surfaceDim" to colorToHex(colors.surfaceDim),
            "surfaceBright" to colorToHex(colors.surfaceBright),
            "surfaceContainerLowest" to colorToHex(colors.surfaceContainerLowest),
            "surfaceContainerLow" to colorToHex(colors.surfaceContainerLow),
            "surfaceContainer" to colorToHex(colors.surfaceContainer),
            "surfaceContainerHigh" to colorToHex(colors.surfaceContainerHigh),
            "surfaceContainerHighest" to colorToHex(colors.surfaceContainerHighest),
        )
    }

    fun colorToHex(color: Color): String {
        val red = (color.red * 255).toInt()
        val green = (color.green * 255).toInt()
        val blue = (color.blue * 255).toInt()
        return String.format("#%02X%02X%02X", red, green, blue)
    }
}
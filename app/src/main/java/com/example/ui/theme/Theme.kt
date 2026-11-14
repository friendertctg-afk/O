package com.example.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp

private val AlQuranShapes = Shapes(
    small = RoundedCornerShape(10.dp),
    medium = RoundedCornerShape(16.dp),
    large = RoundedCornerShape(22.dp),
    extraLarge = RoundedCornerShape(28.dp),
)

@Composable
fun AlQuranTheme(
    preset: IslamicThemePreset = IslamicThemePreset.EMERALD_GOLD,
    content: @Composable () -> Unit,
) {
    val colorScheme = if (preset.isDarkTheme) {
        darkColorScheme(
            primary = preset.primary,
            onPrimary = preset.onPrimary,
            primaryContainer = preset.primaryContainer,
            onPrimaryContainer = preset.onPrimaryContainer,
            secondary = preset.secondary,
            onSecondary = preset.onSecondary,
            secondaryContainer = preset.secondaryContainer,
            onSecondaryContainer = preset.onSecondaryContainer,
            background = preset.background,
            onBackground = preset.onBackground,
            surface = preset.surface,
            onSurface = preset.onSurface,
            surfaceVariant = preset.surfaceVariant,
            onSurfaceVariant = preset.onSurfaceVariant,
            outline = preset.outline,
        )
    } else {
        lightColorScheme(
            primary = preset.primary,
            onPrimary = preset.onPrimary,
            primaryContainer = preset.primaryContainer,
            onPrimaryContainer = preset.onPrimaryContainer,
            secondary = preset.secondary,
            onSecondary = preset.onSecondary,
            secondaryContainer = preset.secondaryContainer,
            onSecondaryContainer = preset.onSecondaryContainer,
            background = preset.background,
            onBackground = preset.onBackground,
            surface = preset.surface,
            onSurface = preset.onSurface,
            surfaceVariant = preset.surfaceVariant,
            onSurfaceVariant = preset.onSurfaceVariant,
            outline = preset.outline,
        )
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = AlQuranTypography,
        shapes = AlQuranShapes,
        content = content,
    )
}

package com.example.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp

private val NathBatteryColorScheme = darkColorScheme(
    primary = NeonVoltGreen,
    onPrimary = DeepCharcoal,
    primaryContainer = NeonGreenGlow,
    onPrimaryContainer = NeonVoltGreen,
    secondary = ElectricCyan,
    onSecondary = DeepCharcoal,
    secondaryContainer = ElevatedSteel,
    onSecondaryContainer = MetallicWhite,
    tertiary = AmberGold,
    onTertiary = DeepCharcoal,
    background = DeepCharcoal,
    onBackground = MetallicWhite,
    surface = ObsidianSurface,
    onSurface = MetallicWhite,
    surfaceVariant = ElevatedSteel,
    onSurfaceVariant = BrushedSilver,
    outline = GlassBorder,
    outlineVariant = MutedTitanium,
    error = ErrorCoral,
    onError = DeepCharcoal
)

val NathBatteryShapes = Shapes(
    extraSmall = RoundedCornerShape(8.dp),
    small = RoundedCornerShape(12.dp),
    medium = RoundedCornerShape(16.dp),
    large = RoundedCornerShape(20.dp),
    extraLarge = RoundedCornerShape(28.dp)
)

@Composable
fun NathBatteryTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = NathBatteryColorScheme,
        typography = NathBatteryTypography,
        shapes = NathBatteryShapes,
        content = content
    )
}

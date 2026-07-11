package ir.alibahmani.ironorecalc.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection

val LocalDarkTheme = staticCompositionLocalOf { false }

private val DarkColorScheme = darkColorScheme(
    primary = IronOrange,
    onPrimary = androidx.compose.ui.graphics.Color.White,
    primaryContainer = androidx.compose.ui.graphics.Color(0xFF8C3A0A),
    onPrimaryContainer = androidx.compose.ui.graphics.Color(0xFFFFDBCB),
    secondary = SteelBlue,
    onSecondary = androidx.compose.ui.graphics.Color.White,
    secondaryContainer = DeepBlue,
    onSecondaryContainer = androidx.compose.ui.graphics.Color(0xFFB8D4F0),
    background = NavyBlue,
    onBackground = androidx.compose.ui.graphics.Color(0xFFE8E8E8),
    surface = CardDark,
    onSurface = androidx.compose.ui.graphics.Color(0xFFE8E8E8),
    surfaceVariant = DeepBlue,
    onSurfaceVariant = androidx.compose.ui.graphics.Color(0xFFB0B8C4),
    error = ErrorRed,
    onError = androidx.compose.ui.graphics.Color.White,
    outline = androidx.compose.ui.graphics.Color(0xFF3A4A5A),
    outlineVariant = androidx.compose.ui.graphics.Color(0xFF2A3A4A)
)

private val LightColorScheme = lightColorScheme(
    primary = androidx.compose.ui.graphics.Color(0xFFB85A1A),
    onPrimary = androidx.compose.ui.graphics.Color.White,
    primaryContainer = androidx.compose.ui.graphics.Color(0xFFFFDBCB),
    onPrimaryContainer = androidx.compose.ui.graphics.Color(0xFF3A1500),
    secondary = SteelBlue,
    onSecondary = androidx.compose.ui.graphics.Color.White,
    secondaryContainer = androidx.compose.ui.graphics.Color(0xFFD0E8FF),
    onSecondaryContainer = androidx.compose.ui.graphics.Color(0xFF001E30),
    background = BlueSurface,
    onBackground = TextPrimary,
    surface = CardLight,
    onSurface = TextPrimary,
    surfaceVariant = androidx.compose.ui.graphics.Color(0xFFECF0F5),
    onSurfaceVariant = TextSecondary,
    error = ErrorRed,
    onError = androidx.compose.ui.graphics.Color.White,
    outline = Divider,
    outlineVariant = androidx.compose.ui.graphics.Color(0xFFD0D5DD)
)

@Composable
fun IronOreCalcTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    CompositionLocalProvider(
        LocalLayoutDirection provides LayoutDirection.Rtl,
        LocalDarkTheme provides darkTheme
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = AppTypography,
            content = content
        )
    }
}

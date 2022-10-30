package uv.index.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import uv.index.features.preferences.data.ThemeMode

class UVSkinColors(
    val typeI: Color,
    val typeII: Color,
    val typeIII: Color,
    val typeIV: Color,
    val typeV: Color,
    val typeVI: Color
) {
    fun getColor(position: Int): Color {
        return when (position) {
            0 -> typeI
            1 -> typeII
            2 -> typeIII
            3 -> typeIV
            4 -> typeV
            5 -> typeVI
            else -> throw IllegalArgumentException()
        }
    }
}

private val standardUVSkinColors = UVSkinColors(
    typeI = Color(0xFFF1D1B1),
    typeII = Color(0xFFE4B590),
    typeIII = Color(0xFFCF9F7D),
    typeIV = Color(0xFFB67851),
    typeV = Color(0xFFA15E2D),
    typeVI = Color(0xFF513938)
)

private val LocalAppUVSkinColors = staticCompositionLocalOf { standardUVSkinColors }

class UVIColors(
    val night: Color,
    val twilight: Color,
    val lowUV: Color,
    val moderateUV: Color,
    val highUV: Color,
    val veryHighUV: Color,
    val extremeUV: Color
)

private val uviColors = UVIColors(
    night = Color(0xFF104568),
    twilight = Color(0xFF1369A0),
    lowUV = Color(0xFF47C0AF),
    moderateUV = Color(0xFFFFB74D),
    highUV = Color(0xFFFF7043),
    veryHighUV = Color(0xFFF44336),
    extremeUV = Color(0xFFAB47BC)
)

//private val uviColors = UVIColors(
//    night = Color(0xFF104568),
//    twilight = Color(0xFF1369A0),
//    lowUV = Color(0xFF4DB6AC),
//    moderateUV = Color(0xFF06284E), // A300
//    highUV = Color(0xFFFE8340),
//    veryHighUV = Color(0xFFFA1818),
//    extremeUV = Color(0xFFBA00FF)
//)

private val LocalUVIColors = staticCompositionLocalOf { uviColors }

object UVITheme {
    val colors: UVIColors
        @Composable
        get() = LocalUVIColors.current

    val skinColors: UVSkinColors
        @Composable
        get() = LocalAppUVSkinColors.current
}

@Composable
@ReadOnlyComposable
fun UVSkinColors.contentColorFor(backgroundColor: Color): Color {
    return when (backgroundColor) {
        typeI, typeII, typeIII -> Color.Black.copy(alpha = 0.9f)
        else -> Color.White
    }
}

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFFDCE9FD),
    onPrimary = Color(0xFF4A6894),
    secondary =  Color.White,
    onSecondary = Color(0xFF00796B),
    tertiaryContainer = Color(red = 127, green = 121, blue = 153, alpha = 255),
    onTertiaryContainer = Color(red = 222, green = 235, blue = 248, alpha = 255),
    background = Color(0xFF1A1A1A),
    onBackground = Color(0xFFF7F7F7),
    surface = Color(0xFF262C35),
    onSurface = Color(0xFFF7F7F7),
    surfaceVariant = Color(0xFF262C35),
    onSurfaceVariant = Color(0xFFFFFFFF),
    inverseOnSurface = Color.White,
    )

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF4A6894),
    secondary = Color(0xFF00796B),
    onSecondary = Color.White,
    tertiary = Color(0xFF7D5260),
    tertiaryContainer = Color(red = 222, green = 235, blue = 248, alpha = 255),
    onTertiaryContainer = Color(red = 29, green = 25, blue = 43),

    background = Color(0xFFF7F7F7),
    onBackground = Color(0xFF4A6894),
    surface = Color(0xFFFFFFFF),
    onSurface = Color(0xFF354A69),
    surfaceVariant = Color(0xFFFFFFFF),
    onSurfaceVariant = Color(0xFF354A69),
    inverseOnSurface = Color.White,

    secondaryContainer = Color(red = 232, green = 222, blue = 248),
    onSecondaryContainer = Color(red = 29, green = 25, blue = 43)

//    surfaceTint = Color.Red,
//    scrim = Color.Green,
//    onSecondary = Color.White,
//    onPrimary = Color.White,
//    onError = Color.Red,
//    onErrorContainer = Color.Red,
//    onPrimaryContainer = Color.Yellow,
//    onSecondaryContainer = Color.Green,

)

private val LightColors = lightColorScheme(
    primary = md_theme_light_primary,
    onPrimary = md_theme_light_onPrimary,
    primaryContainer = md_theme_light_primaryContainer,
    onPrimaryContainer = md_theme_light_onPrimaryContainer,
    secondary = md_theme_light_secondary,
    onSecondary = md_theme_light_onSecondary,
    secondaryContainer = md_theme_light_secondaryContainer,
    onSecondaryContainer = md_theme_light_onSecondaryContainer,
    tertiary = md_theme_light_tertiary,
    onTertiary = md_theme_light_onTertiary,
    tertiaryContainer = md_theme_light_tertiaryContainer,
    onTertiaryContainer = md_theme_light_onTertiaryContainer,
    error = md_theme_light_error,
    errorContainer = md_theme_light_errorContainer,
    onError = md_theme_light_onError,
    onErrorContainer = md_theme_light_onErrorContainer,
    background = md_theme_light_background,
    onBackground = md_theme_light_onBackground,
    surface = md_theme_light_surface, // bottom navigation, top bar color
    onSurface = md_theme_light_onSurface,
    surfaceVariant = md_theme_light_surfaceVariant, // card color
    onSurfaceVariant = md_theme_light_onSurfaceVariant,
    outline = md_theme_light_outline, // switch border colors
    inverseOnSurface = md_theme_light_inverseOnSurface,
    inverseSurface = md_theme_light_inverseSurface,
    inversePrimary = md_theme_light_inversePrimary,
    surfaceTint = md_theme_light_surfaceTint, // bottom navigation tint, top bar color tint
)


private val DarkColors = darkColorScheme(
    primary = md_theme_dark_primary,
    onPrimary = md_theme_dark_onPrimary,
    primaryContainer = md_theme_dark_primaryContainer,
    onPrimaryContainer = md_theme_dark_onPrimaryContainer,
    secondary = md_theme_dark_secondary,
    onSecondary = md_theme_dark_onSecondary,
    secondaryContainer = md_theme_dark_secondaryContainer,
    onSecondaryContainer = md_theme_dark_onSecondaryContainer,
    tertiary = md_theme_dark_tertiary,
    onTertiary = md_theme_dark_onTertiary,
    tertiaryContainer = md_theme_dark_tertiaryContainer,
    onTertiaryContainer = md_theme_dark_onTertiaryContainer,
    error = md_theme_dark_error,
    errorContainer = md_theme_dark_errorContainer,
    onError = md_theme_dark_onError,
    onErrorContainer = md_theme_dark_onErrorContainer,
    background = md_theme_dark_background,
    onBackground = md_theme_dark_onBackground,
    surface = md_theme_dark_surface,
    onSurface = md_theme_dark_onSurface,
    surfaceVariant = md_theme_dark_surfaceVariant,
    onSurfaceVariant = md_theme_dark_onSurfaceVariant,
    outline = md_theme_dark_outline,
    inverseOnSurface = md_theme_dark_inverseOnSurface,
    inverseSurface = md_theme_dark_inverseSurface,
    inversePrimary = md_theme_dark_inversePrimary,
    surfaceTint = md_theme_dark_surfaceTint,
)

@Composable
fun UVIndexAppTheme(
    themeMode: ThemeMode = ThemeMode.System,
//    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (themeMode == ThemeMode.System) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        themeMode == ThemeMode.System -> {
            if (isSystemInDarkTheme()) DarkColors else LightColors
        }
        themeMode == ThemeMode.Dark -> DarkColors
        themeMode == ThemeMode.Light -> LightColors
        else -> LightColors
    }

//    val view = LocalView.current
//    if (!view.isInEditMode) {
//        SideEffect {
//            (view.context as Activity).window.statusBarColor = Color.TRANSPARENT
//            ViewCompat.getWindowInsetsController(view)?.isAppearanceLightStatusBars = darkTheme
//        }
//    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
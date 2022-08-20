package uv.index.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

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
}

private val DarkColorScheme = darkColorScheme(
    primary = Purple80,
    secondary = PurpleGrey80,
    tertiary = Pink80,
    background = Color(0xFF090909),
    onBackground = Color(0xFFF7F7F7),
    surface = Color(0xFF354A69),
    onSurface = Color(0xFFF7F7F7),
    surfaceVariant = Color(0xFF354A69),
    onSurfaceVariant = Color(0xFFFFFFFF),
    inverseOnSurface = Color.White,
    )

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF4A6894),
    secondary = Color.Red,
    tertiary = Color(0xFFC8E6C9),
    onTertiary = Color(0xFF4A6894),
    background = Color(0xFFF7F7F7),
    onBackground = Color(0xFF4A6894),
    surface = Color(0xFFFFFFFF),
    onSurface = Color(0xFF354A69),
    surfaceVariant = Color(0xFFFFFFFF),
    onSurfaceVariant = Color(0xFF354A69),
    inverseOnSurface = Color.White,
//    surfaceTint = Color.Red,
    scrim = Color.Green,
//    onTertiaryContainer = Color.White,
//    onSecondary = Color.White,
//    onPrimary = Color.White,
//    onError = Color.Red,
//    onErrorContainer = Color.Red,
//    onPrimaryContainer = Color.Yellow,
//    onSecondaryContainer = Color.Green,
//    onSurfaceVariant = Color.Magenta,




    /* Other default colors to override
    background = Color(0xFFFFFBFE),
    surface = Color(0xFFFFFBFE),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color(0xFF1C1B1F),
    onSurface = Color(0xFF1C1B1F),
    */
)

@Composable
fun UVIndexAppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = false,
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

//    val view = LocalView.current
//    if (!view.isInEditMode) {
//        SideEffect {
//            (view.context as Activity).window.statusBarColor = Color.TRANSPARENT
//            ViewCompat.getWindowInsetsController(view)?.isAppearanceLightStatusBars = darkTheme
//        }
//    }

    MaterialTheme(
        colorScheme = LightColorScheme,
        typography = Typography,
        content = content
    )
}
package uv.index.navigation

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.Stable
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.Lifecycle
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import uv.index.LocalAppState
import uv.index.parts.main.ui.MainScreen

fun NavBackStackEntry.lifecycleIsResumed() =
    lifecycle.currentState == Lifecycle.State.RESUMED

class AppNavigationActions(navController: NavController) : NavigationActions(navController) {
    val popBack = { from: NavBackStackEntry ->
        if (from.lifecycleIsResumed()) navController.popBackStack()
    }
}

@Stable
sealed class AppScreen(
    override val route: String,
    val isDarkSystemIcons: Boolean = true,
) : Screen<AppNavigationActions>(
    enterTransition = { fadeIn(animationSpec = tween(450)) },
    exitTransition = { fadeOut(animationSpec = tween(250, 300)) },
) {

    object Main : AppScreen(route = "main") {
        override val content: @Composable (backStackEntry: NavBackStackEntry) -> Unit =
            { _ ->
                UIEffect(isDarkSystemIcons = isDarkSystemIcons)
                MainScreen()
            }
    }
}

@Composable
private fun UIEffect(
    isDarkSystemIcons: Boolean,
    color: Color = Color.Transparent
) {
    val uiController = LocalAppState.current.uiController
    SideEffect {
        uiController.setSystemBarsColor(
            color = color,
            darkIcons = isDarkSystemIcons
        )
    }
}
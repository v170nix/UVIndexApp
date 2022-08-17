package uv.index.navigation

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.LocalOverscrollConfiguration
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.Stable
import androidx.compose.ui.graphics.Color
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import uv.index.LocalAppState
import uv.index.parts.main.ui.MainViewModel
import uv.index.parts.main.ui.composable.MainScreen
import uv.index.parts.place.ui.composable.PlaceListScreen

fun NavBackStackEntry.lifecycleIsResumed() =
    lifecycle.currentState == Lifecycle.State.RESUMED

class AppNavigationActions(navController: NavController) : NavigationActions(navController) {
    val popBack = { from: NavBackStackEntry ->
        if (from.lifecycleIsResumed()) navController.popBackStack()
    }

    val nestedNavigateToPlace = { from: NavBackStackEntry ->
        if (from.lifecycleIsResumed()) navController.navigate(
            AppScreen.Place.List.route
        )
    }
}

@Stable
sealed class AppScreen(
    override val route: String,
    val isDarkSystemIcons: Boolean = false,
) : Screen<AppNavigationActions>(
    enterTransition = { fadeIn(animationSpec = tween(450)) },
    exitTransition = { fadeOut(animationSpec = tween(250, 300)) },
) {

    object Main : AppScreen(route = "main") {
        @OptIn(ExperimentalFoundationApi::class)
        override val content: @Composable AppNavigationActions.(backStackEntry: NavBackStackEntry) -> Unit =
            { entry ->
                UIEffect(isDarkSystemIcons = isDarkSystemIcons)
                val viewModel: MainViewModel = hiltViewModel()
                CompositionLocalProvider(LocalOverscrollConfiguration provides null) {
                    MainScreen(
                        viewModel = viewModel,
                        onChangePlace = { nestedNavigateToPlace(entry) }
                    )
                }
            }
    }

    object Place {
        val nestedItems = NestedScreens(
            startDestination = "place/list",
            route = "place",
            screens = listOf(List)
        )

        object List : AppScreen(
            route = "place/list",
            isDarkSystemIcons = true
        ) {
            override val content: @Composable AppNavigationActions.(backStackEntry: NavBackStackEntry) -> Unit
                get() = {
                    UIEffect(isDarkSystemIcons = isDarkSystemIcons)
                    PlaceListScreen()
                }

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
package uv.index.navigation

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.LocalOverscrollConfiguration
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import uv.index.LocalAppState
import uv.index.features.main.ui.MainViewModel
import uv.index.features.main.ui.composable.MainScreen
import uv.index.features.main.ui.composable.SkinScreen
import uv.index.features.place.parts.editlocation.ui.PlaceEditLocationViewModel
import uv.index.features.place.parts.editzone.ui.PlaceEditTimeZoneViewModel
import uv.index.features.place.parts.list.ui.PlaceListViewModel
import uv.index.features.place.ui.composable.PlaceListScreen
import uv.index.features.place.ui.composable.PlaceLocationScreen
import uv.index.features.place.ui.composable.PlaceTimeZoneScreen
import uv.index.features.preference.ui.PreferenceScreen

fun NavBackStackEntry.lifecycleIsResumed() =
    lifecycle.currentState == Lifecycle.State.RESUMED

class PlaceNavigationActions(
    navController: NavController,
    listRoute: String,
    locationRoute: String,
    timeZoneRoute: String
) : NavigationActions(navController) {
    val wizardNavigateToLocationPlace = { from: NavBackStackEntry ->
        if (from.lifecycleIsResumed())
            navController.navigate(locationRoute)
    }

    val wizardNavigateToTimeZone = { from: NavBackStackEntry ->
        if (from.lifecycleIsResumed())
            navController.navigate(timeZoneRoute)
    }
    val wizardReturnToList = { from: NavBackStackEntry ->
        if (from.lifecycleIsResumed()) navController.popBackStack(
            listRoute,
            false
        )
    }
}

class AppNavigationActions(navController: NavController) : NavigationActions(navController) {
    val popBack = { from: NavBackStackEntry ->
        if (from.lifecycleIsResumed()) navController.popBackStack()
    }

    val nestedNavigateToPlace = { from: NavBackStackEntry ->
        if (from.lifecycleIsResumed()) navController.navigate(
            AppScreen.Place.List.route
        )
    }

    val bottomBarNavigateTo = { to: AppScreen ->
        navController.navigate(to.route) {
            popUpTo(navController.graph.findStartDestination().id) {
                saveState = true
            }
            launchSingleTop = true
            restoreState = true
        }
    }

    val placeActions = PlaceNavigationActions(
        navController,
        listRoute = AppScreen.Place.List.route,
        locationRoute = AppScreen.Place.Location.route,
        timeZoneRoute = AppScreen.Place.TimeZone.route
    )
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

    object SkinType: AppScreen(route = "skin") {
        override val content: @Composable AppNavigationActions.(backStackEntry: NavBackStackEntry) -> Unit
            get() = { entry: NavBackStackEntry ->
                UIEffect(isDarkSystemIcons = isDarkSystemIcons)

                val parentEntry = remember(entry) { navController.getBackStackEntry(Main.route) }
                val viewModel = hiltViewModel<MainViewModel>(parentEntry)
                SkinScreen(
                    viewModel = viewModel,
                    onNavigateUp = {
                        popBack(entry)
                    }
                )
            }
    }

    object Preference: AppScreen(route = "preference") {
        override val content: @Composable AppNavigationActions.(backStackEntry: NavBackStackEntry) -> Unit
            get() = {
                UIEffect(isDarkSystemIcons = isDarkSystemIcons)
                PreferenceScreen()
            }

    }

    object Place {
        val nestedItems = NestedScreens(
            startDestination = "place/list",
            route = "place",
            screens = listOf(List, Location, TimeZone)
        )

        object List : AppScreen(
            route = "place/list",
            isDarkSystemIcons = true
        ) {
            override val content: @Composable AppNavigationActions.(backStackEntry: NavBackStackEntry) -> Unit
                get() = { entry ->
                    UIEffect(isDarkSystemIcons = isDarkSystemIcons)
                    val viewModel: PlaceListViewModel = hiltViewModel()
                    PlaceListScreen(
                        viewModel = viewModel,
                        onNavigateUp = {
                            popBack(entry)
                        },
                        onNextScreen = {
                            placeActions.wizardNavigateToLocationPlace(entry)
                        }
                    )
                }
        }

        object Location : AppScreen(
            route = "place/location",
            isDarkSystemIcons = true
        ) {
            override val content: @Composable AppNavigationActions.(backStackEntry: NavBackStackEntry) -> Unit
                get() = { entry ->
                    UIEffect(isDarkSystemIcons = isDarkSystemIcons)
                    val viewModel: PlaceEditLocationViewModel = hiltViewModel()
                    PlaceLocationScreen(
                        viewModel = viewModel,
                        onNavigateBackStack = {
                            popBack(entry)
                        },
                        onNextScreen = {
                            placeActions.wizardNavigateToTimeZone(entry)
                        }
                    )
                }
        }

        object TimeZone : AppScreen(
            route = "place/timezone",
            isDarkSystemIcons = true
        ) {
            override val content: @Composable AppNavigationActions.(backStackEntry: NavBackStackEntry) -> Unit
                get() = { entry ->
                    UIEffect(isDarkSystemIcons = isDarkSystemIcons)
                    val viewModel: PlaceEditTimeZoneViewModel = hiltViewModel()
                    PlaceTimeZoneScreen(
                        viewModel = viewModel,
                        onNavigateBackStack = {
                            popBack(entry)
                        },
                        onFinish = {
                            placeActions.wizardReturnToList(entry)
                        }
                    )
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
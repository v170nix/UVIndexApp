package uv.index.navigation

import androidx.activity.ComponentActivity
import androidx.annotation.ArrayRes
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.LocalOverscrollConfiguration
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.navArgument
import uv.index.LocalAppState
import uv.index.features.main.ui.MainViewModel
import uv.index.features.main.ui.composable.MainScreen
import uv.index.features.main.ui.composable.SkinScreen
import uv.index.features.more.parts.ui.MorePartScreen
import uv.index.features.more.ui.MoreScreen
import uv.index.features.place.parts.editlocation.ui.PlaceEditLocationViewModel
import uv.index.features.place.parts.editzone.ui.PlaceEditTimeZoneViewModel
import uv.index.features.place.parts.list.ui.PlaceListViewModel
import uv.index.features.place.ui.composable.PlaceListScreen
import uv.index.features.place.ui.composable.PlaceLocationScreen
import uv.index.features.place.ui.composable.PlaceTimeZoneScreen
import uv.index.features.preferences.ui.isAppInDarkTheme

fun NavBackStackEntry.lifecycleIsResumed() =
    lifecycle.currentState == Lifecycle.State.RESUMED

class PlaceNavigationActions(
    override val navController: NavController,
    listRoute: String,
    locationRoute: String,
    timeZoneRoute: String,
) : NavigationActions {
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

class AppNavigationActions(override val navController: NavController) : NavigationActions {
    val popBack = { from: NavBackStackEntry ->
        if (from.lifecycleIsResumed()) navController.popBackStack()
    }

    val nestedNavigateToPlace = { from: NavBackStackEntry ->
        if (from.lifecycleIsResumed()) navController.navigate(
            AppScreen.Place.List.route,
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

@Suppress("MagicNumber")
@Stable
sealed class AppScreen(
    override val route: String,
) : Screen<AppNavigationActions>(
    enterTransition = { fadeIn(animationSpec = tween(450)) },
    exitTransition = { fadeOut(animationSpec = tween(250, 300)) },
) {

    object Main : AppScreen(route = "main") {
        @OptIn(ExperimentalFoundationApi::class)
        override val content: @Composable AppNavigationActions.(backStackEntry: NavBackStackEntry) -> Unit =
            { entry ->
                UIEffect(
                    isDarkStatusIcons = false,
                    isDarkNavigationIcons = !isAppInDarkTheme()
                )

                val viewModel: MainViewModel = hiltViewModel()
                CompositionLocalProvider(LocalOverscrollConfiguration provides null) {
                    MainScreen(
                        viewModel = viewModel,
                        onChangePlace = { nestedNavigateToPlace(entry) }
                    )
                }
            }
    }

    object SkinType : AppScreen(
        route = "skin"
    ) {
        override val content: @Composable AppNavigationActions.(backStackEntry: NavBackStackEntry) -> Unit
            get() = { entry: NavBackStackEntry ->

                UIEffect(isDarkSystemIcons = !isAppInDarkTheme())

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

    object More : AppScreen(
        route = "more"
    ) {

        override val content: @Composable AppNavigationActions.(backStackEntry: NavBackStackEntry) -> Unit
            get() = { entry: NavBackStackEntry ->

                UIEffect(isDarkSystemIcons = !isAppInDarkTheme())

                MoreScreen(
                    themeViewModel = hiltViewModel(LocalContext.current as ComponentActivity),
                    onDetailInfo = {
                        navController.navigate("more/${it}")
                    },
                    onNavigateUp = {
                        popBack(entry)
                    }
                )
            }

        object Parts : AppScreen(
            route = "more/{screenId}"
        ) {

            enum class Item(@ArrayRes val infoId: Int) {
                UVInfo(uv.index.R.array.info_uv_index),
                PrivacyInfo(uv.index.R.array.privacy_policy_info);

                companion object {
                    fun of(screenId: String?, defaultItem: Item) = runCatching {
                        if (screenId == null) return@runCatching defaultItem
                        valueOf(screenId)
                    }.getOrDefault(defaultItem)
                }
            }

            override var arguments = listOf(
                navArgument("screenId") {
                    type = NavType.StringType
                }
            )

            override val content: @Composable AppNavigationActions.(backStackEntry: NavBackStackEntry) -> Unit
                get() = { entry: NavBackStackEntry ->
                    UIEffect(isDarkSystemIcons = !isAppInDarkTheme())

                    val part =
                        Item.of(entry.arguments?.getString("screenId"), Item.UVInfo)

                    MorePartScreen(
                        infoId = part.infoId,
                        onNavigateUp = {
                            popBack(entry)
                        }
                    )
                }
        }

    }

    object Place {
        val nestedItems = NestedScreens(
            startDestination = "place/list",
            route = "place",
            screens = listOf(List, Location, TimeZone)
        )

        object List : AppScreen(
            route = "place/list"
        ) {
            override val content: @Composable AppNavigationActions.(backStackEntry: NavBackStackEntry) -> Unit
                get() = { entry ->
                    UIEffect(isDarkSystemIcons = !isAppInDarkTheme())
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
            route = "place/location"
        ) {
            override val content: @Composable AppNavigationActions.(backStackEntry: NavBackStackEntry) -> Unit
                get() = { entry ->
                    UIEffect(isDarkSystemIcons = !isAppInDarkTheme())
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
            route = "place/timezone"
        ) {
            override val content: @Composable AppNavigationActions.(backStackEntry: NavBackStackEntry) -> Unit
                get() = { entry ->
                    UIEffect(isDarkSystemIcons = !isAppInDarkTheme())
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

@Composable
private fun UIEffect(
    isDarkStatusIcons: Boolean,
    isDarkNavigationIcons: Boolean,
    color: Color = Color.Transparent
) {
    val uiController = LocalAppState.current.uiController
    SideEffect {
        uiController.setStatusBarColor(
            color = color,
            darkIcons = isDarkStatusIcons
        )
        uiController.setNavigationBarColor(
            color = color,
            darkIcons = isDarkNavigationIcons
        )
    }
}
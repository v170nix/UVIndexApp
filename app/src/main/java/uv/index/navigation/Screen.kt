package uv.index.navigation

import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.navigation.*
import com.google.accompanist.navigation.animation.composable

abstract class NavigationActions(val navController: NavController)

@Immutable
data class NestedScreens<T : NavigationActions>(
    val startDestination: String,
    val route: String,
    val screens: List<Screen<T>>
)

@OptIn(ExperimentalAnimationApi::class)
abstract class Screen<T : NavigationActions>(
    open var arguments: List<NamedNavArgument> = emptyList(),
    open val deepLinks: List<NavDeepLink> = emptyList(),
    open val enterTransition: (AnimatedContentScope<NavBackStackEntry>.() -> EnterTransition?)? = null,
    open val exitTransition: (AnimatedContentScope<NavBackStackEntry>.() -> ExitTransition?)? = null,
    open val popEnterTransition: (AnimatedContentScope<NavBackStackEntry>.() -> EnterTransition?)? = enterTransition,
    open val popExitTransition: (AnimatedContentScope<NavBackStackEntry>.() -> ExitTransition?)? = exitTransition,
    open val nestedItems: NestedScreens<T>? = null
) {
    abstract val route: String
    abstract val content: @Composable (backStackEntry: NavBackStackEntry) -> Unit
}

@OptIn(ExperimentalAnimationApi::class)
fun <T : NavigationActions> NavGraphBuilder.contentGraph(
    list: List<Screen<T>>
) {
    list.forEach { screen ->
        composable(
            screen.route,
            screen.arguments,
            deepLinks = screen.deepLinks,
            enterTransition = screen.enterTransition,
            exitTransition = screen.exitTransition,
            popEnterTransition = screen.popEnterTransition,
            popExitTransition = screen.popExitTransition
        ) { entry: NavBackStackEntry ->
            screen.content(entry)
        }

        screen.nestedItems?.run {
            nestedGraph(this)
        }
    }
}

fun <T : NavigationActions> NavGraphBuilder.nestedGraph(
    nestedScreens: NestedScreens<T>
) {
    navigation(nestedScreens.startDestination, nestedScreens.route) {
        contentGraph(nestedScreens.screens)
    }
}
package uv.index.navigation

import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.navigation.*
import androidx.navigation.compose.composable

//import com.google.accompanist.navigation.animation.composable

interface NavigationActions {
    val navController: NavController
}

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
    open val enterTransition: (AnimatedContentTransitionScope<NavBackStackEntry>.() -> EnterTransition?)? = null,
    open val exitTransition: (AnimatedContentTransitionScope<NavBackStackEntry>.() -> ExitTransition?)? = null,
    open val popEnterTransition: (AnimatedContentTransitionScope<NavBackStackEntry>.() -> EnterTransition?)? = enterTransition,
    open val popExitTransition: (AnimatedContentTransitionScope<NavBackStackEntry>.() -> ExitTransition?)? = exitTransition,
    open val nestedItems: NestedScreens<T>? = null
) {
    abstract val route: String
    abstract val content: @Composable T.(backStackEntry: NavBackStackEntry) -> Unit
}

@OptIn(ExperimentalAnimationApi::class)
fun <T : NavigationActions> NavGraphBuilder.contentGraph(
    list: List<Screen<T>>,
    actions: T
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
            screen.content(actions, entry)
        }

        screen.nestedItems?.run {
            nestedGraph(this, actions)
        }
    }
}

fun <T : NavigationActions> NavGraphBuilder.nestedGraph(
    nestedScreens: NestedScreens<T>,
    actions: T
) {
    navigation(nestedScreens.startDestination, nestedScreens.route) {
        contentGraph(nestedScreens.screens, actions)
    }
}
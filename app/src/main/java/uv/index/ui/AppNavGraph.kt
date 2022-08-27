package uv.index.ui

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import com.google.accompanist.navigation.animation.AnimatedNavHost
import uv.index.LocalAppState
import uv.index.navigation.AppScreen
import uv.index.navigation.contentGraph
import uv.index.navigation.nestedGraph

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun AppNavGraph(
    startDestination: String
) {
    val navController = LocalAppState.current.navController as NavHostController
    val actions = LocalAppState.current.actions

    AnimatedNavHost(navController = navController, startDestination = startDestination) {
        contentGraph(
            listOf(
                AppScreen.Main, AppScreen.SkinType,
                AppScreen.More,
                AppScreen.More.Parts
            ), actions
        )
        nestedGraph(AppScreen.Place.nestedItems, actions)
    }

}
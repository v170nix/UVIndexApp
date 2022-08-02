package uv.index.ui

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import com.google.accompanist.navigation.animation.AnimatedNavHost
import uv.index.LocalAppState
import uv.index.navigation.AppScreen
import uv.index.navigation.contentGraph

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun AppNavGraph(
    startDestination: String
) {
    val navController = LocalAppState.current.navController as NavHostController

    AnimatedNavHost(navController = navController, startDestination = startDestination) {
        contentGraph(
            listOf(AppScreen.Main)
        )
    }

}
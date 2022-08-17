package uv.index

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.remember
import androidx.navigation.NavController
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import com.google.accompanist.systemuicontroller.SystemUiController
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import uv.index.navigation.AppNavigationActions

@Stable
class AppState(
    val navController: NavController,
    val uiController: SystemUiController,
    val actions: AppNavigationActions,
)

val LocalAppState = compositionLocalOf<AppState> { error("No App State") }

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun rememberAppState(
    navController: NavController = rememberAnimatedNavController(),
    uiController: SystemUiController = rememberSystemUiController(),
    actions: AppNavigationActions = remember(navController) { AppNavigationActions(navController) },
): AppState = remember {
    AppState(
        navController, uiController, actions
    )
}
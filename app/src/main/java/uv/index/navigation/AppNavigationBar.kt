package uv.index.navigation

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.compose.currentBackStackEntryAsState
import uv.index.LocalAppState
import uv.index.R

@Composable
fun AppNavigationBar() {

    val navController = LocalAppState.current.navController
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    val actions = LocalAppState.current.actions

    NavigationBar {
        items.forEach { item ->
            NavigationBarItem(
                icon = { Icon(imageVector = item.icon, contentDescription = null) },
                label = { Text( stringResource(id = item.nameId)) },
                selected = currentDestination?.hierarchy?.any {
                    when (item) {
                        is NavigationItems.Notification -> false
                        is NavigationItems.Screen -> it.route == item.data.route
                    }
                } == true,
                onClick = {
                    when (item) {
                        is NavigationItems.Notification -> {}
                        is NavigationItems.Screen -> {
                            actions.bottomBarNavigateTo(item.data)
                        }
                    }

                }
            )
        }
    }
}

private sealed class NavigationItems(val icon: ImageVector, @StringRes val nameId: Int) {
    class Screen(
        val data: AppScreen,
        icon: ImageVector,
        @StringRes nameId: Int
    ) : NavigationItems(icon, nameId)

    class Notification(
        val isOn: Boolean,
        icon: ImageVector,
        @StringRes nameId: Int
    ) : NavigationItems(icon, nameId)
}

private val items = listOf<NavigationItems>(
    NavigationItems.Screen(AppScreen.Main, Icons.Filled.Home, R.string.navigation_bar_main_screen_title),
    NavigationItems.Screen(AppScreen.SkinType, Icons.Filled.Person, R.string.navigation_bar_skin_screen_title),
    //   NavigationItems.Notification(false, Icons.Filled.Notifications, "Извещения"),
    NavigationItems.Screen(AppScreen.More, Icons.Filled.Menu, R.string.navigation_bar_more_screen_title)
)
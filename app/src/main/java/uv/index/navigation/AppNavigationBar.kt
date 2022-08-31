package uv.index.navigation

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
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.compose.currentBackStackEntryAsState
import uv.index.LocalAppState

@Composable
fun AppNavigationBar() {

    val navController = LocalAppState.current.navController
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    val actions = LocalAppState.current.actions

    NavigationBar {
        items.forEachIndexed { index, item ->
            NavigationBarItem(
                icon = { Icon(imageVector = item.icon, contentDescription = null) },
                label = { Text(item.name) },
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
//
//                            navController.navigate(item.data.route) {
//                                popUpTo(navController.graph.findStartDestination().id) {
//                                    saveState = true
//                                }
//                                launchSingleTop = true
//                                restoreState = true
//                            }
                        }
                    }

                }
            )
        }
    }
}

private sealed class NavigationItems(val icon: ImageVector, val name: String) {
    class Screen(
        val data: AppScreen,
        icon: ImageVector,
        name: String
    ) : NavigationItems(icon, name)

    class Notification(
        val isOn: Boolean,
        icon: ImageVector,
        name: String
    ) : NavigationItems(icon, name)
}

private val items = listOf<NavigationItems>(
    NavigationItems.Screen(AppScreen.Main, Icons.Filled.Home, "В начало"),
    NavigationItems.Screen(AppScreen.SkinType, Icons.Filled.Person, "Тип кожи"),
 //   NavigationItems.Notification(false, Icons.Filled.Notifications, "Извещения"),
    NavigationItems.Screen(AppScreen.More, Icons.Filled.Menu, "Далее")
)
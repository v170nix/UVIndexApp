package uv.index.navigation

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.compose.currentBackStackEntryAsState
import net.arwix.mvi.EventHandler
import uv.index.LocalAppState
import uv.index.R
import uv.index.features.main.ui.MainContract

@Composable
fun AppNavigationBar(
    viewMode: MainContract.ViewMode? = null,
    handler: EventHandler<MainContract.Event>? = null
) {

    val navController = LocalAppState.current.navController
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    val actions = LocalAppState.current.actions

    BottomAppBar(
        actions = {
            items.forEach { item ->
                when (item) {
                    is NavigationItem.Notification -> {

                    }
                    is NavigationItem.Screen -> {
//                        IconButton(
//                            modifier = Modifier.selectable(
//                                selected =  currentDestination?.hierarchy?.any {
//                                    it.route == item.data.route
//                                } == true,
//                                onClick = {
//                                    actions.bottomBarNavigateTo(item.data)
//                                }
//                            ),
//                            onClick = { actions.bottomBarNavigateTo(item.data) }
//                        ) {
//                            Icon(imageVector = item.icon, contentDescription = stringResource(id = item.nameId))
//                        }
                        NavigationBarItem(
                            icon = { Icon(imageVector = item.icon, contentDescription = null) },
                            label = { Text(stringResource(id = item.nameId)) },
                            selected = currentDestination?.hierarchy?.any {
                                it.route == item.data.route
                            } == true,
                            onClick = {
                                actions.bottomBarNavigateTo(item.data)
                            }
                        )
                    }
                    NavigationItem.Switcher -> {

                    }
                    else -> {}
                }

            }
        },
        floatingActionButton = {

                FloatingActionButton(

//                modifier = Modifier.defaultMinSize(64.dp),
                    containerColor = BottomAppBarDefaults.bottomAppBarFabColor,
                    elevation = FloatingActionButtonDefaults.bottomAppBarFabElevation(),
                    onClick = {
                        handler?.doEvent(MainContract.Event.DoChangeViewMode)
                    }) {
                    Icon(
                        imageVector = ImageVector.vectorResource(id = R.drawable.ic_change_mode),
                        "Switch"
                    )
                }

        }
    )

//    NavigationBar(
//
//    ) {
//
//        items.forEach { item ->
//            when (item) {
//                is NavigationItem.Notification -> {
//                    NavigationBarItem(
//                        icon = { Icon(imageVector = item.icon, contentDescription = null) },
//                        label = { Text(stringResource(id = item.nameId)) },
//                        selected = currentDestination?.hierarchy?.any { false } == true,
//                        onClick = {}
//                    )
//                }
//                is NavigationItem.Screen -> {
//                    NavigationBarItem(
//                        icon = { Icon(imageVector = item.icon, contentDescription = null) },
//                        label = { Text(stringResource(id = item.nameId)) },
//                        selected = currentDestination?.hierarchy?.any {
//                            it.route == item.data.route
//                        } == true,
//                        onClick = {
//                            actions.bottomBarNavigateTo(item.data)
//                        }
//                    )
//                }
//                NavigationItem.Switcher -> {
//                    NavigationBarItem(
//                        selected = false,
//                        icon = {
//                            Switch(checked =
//                            viewMode == MainContract.ViewMode.UV, onCheckedChange = {
//                                handler?.doEvent(MainContract.Event.DoChangeViewMode)
//                            })
//                        },
//                        onClick = { }
//                    )
//                }
//            }
//
//        }
//    }
}

@Stable
private sealed class NavigationItem(
    open val icon: ImageVector?,
    @StringRes open val nameId: Int?
) {
    class Screen(
        val data: AppScreen,
        override val icon: ImageVector,
        @StringRes override val nameId: Int
    ) : NavigationItem(icon, nameId)

    class Notification(
        val isOn: Boolean,
        override val icon: ImageVector,
        @StringRes override val nameId: Int
    ) : NavigationItem(icon, nameId)

    object Switcher : NavigationItem(null, null)
}

private val items = listOf<NavigationItem>(
    NavigationItem.Screen(
        AppScreen.Main,
        Icons.Filled.Home,
        R.string.navigation_bar_main_screen_title
    ),
//    NavigationItem.Switcher,
    NavigationItem.Screen(
        AppScreen.SkinType,
        Icons.Filled.Person,
        R.string.navigation_bar_skin_screen_title
    ),
    //   NavigationItems.Notification(false, Icons.Filled.Notifications, "Извещения"),
    NavigationItem.Screen(
        AppScreen.More,
        Icons.Filled.Menu,
        R.string.navigation_bar_more_screen_title
    )
)
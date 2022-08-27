package uv.index.features.more.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import uv.index.navigation.AppNavigationBar
import uv.index.navigation.AppScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MoreScreen(
    onDetailInfo: (item: AppScreen.More.Parts.Item) -> Unit,
    onNavigateUp: () -> Unit
) {

    val scrollBehavior: TopAppBarScrollBehavior =
        TopAppBarDefaults.enterAlwaysScrollBehavior(
            rememberTopAppBarState()
        )

    Scaffold(
//        containerColor = Color.Transparent,
        topBar = {
            SmallTopAppBar(
                title = {
                    Text(
                        text = "Preferences"
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateUp) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "back")
                    }
                },
                scrollBehavior = scrollBehavior
            )
        },
        bottomBar = {
            AppNavigationBar()
        }
    ) {
        LazyColumn(Modifier.padding(it)) {
            item {
                ListItem(
                    modifier = Modifier.clickable {
                        onDetailInfo(AppScreen.More.Parts.Item.UVInfo)
                    },
                    headlineText = {
                        Text(text = "Об ультрафиолете")
                    }
                )
            }
            item {
                ListItem(
                    headlineText = {
                        Text(text = "Премиум версия")
                    }
                )
            }
            item {
                ListItem(
                    headlineText = {
                        Text(text = "Что улучшить?")
                    }
                )
            }
            item {
                ListItem(
                    headlineText = {
                        Text(text = "Поделиться")
                    }
                )
            }
            item {
                ListItem(
                    modifier = Modifier.clickable {
                        onDetailInfo(AppScreen.More.Parts.Item.PrivacyInfo)
                    },
                    headlineText = {
                        Text(text = "Конфиденциальность")
                    }
                )
            }
            item {
                ListItem(
                    headlineText = {
                        Text(text = "Отказ от ответвенности")
                    }
                )
            }
        }
    }
}
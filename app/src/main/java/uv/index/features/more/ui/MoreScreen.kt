package uv.index.features.more.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.selection.selectable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import uv.index.R
import uv.index.features.preferences.data.ThemeMode
import uv.index.features.preferences.ui.ThemeViewModel
import uv.index.navigation.AppNavigationBar
import uv.index.navigation.AppScreen
import uv.index.ui.theme.Dimens

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MoreScreen(
    themeViewModel: ThemeViewModel,
    onDetailInfo: (item: AppScreen.More.Parts.Item) -> Unit,
    onNavigateUp: () -> Unit
) {

    val scrollBehavior: TopAppBarScrollBehavior =
        TopAppBarDefaults.enterAlwaysScrollBehavior(
            rememberTopAppBarState()
        )

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .nestedScroll(scrollBehavior.nestedScrollConnection),
//        containerColor = Color.Transparent,
        topBar = {
            SmallTopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.uvindex_more_title)
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
        LazyColumn(
            Modifier
                .fillMaxSize()
                .padding(it)
                .padding(top = Dimens.grid_2),
            verticalArrangement = Arrangement.spacedBy(Dimens.grid_1)
        ) {

            item {
                PreferenceCard(
                    modifier = Modifier.padding(horizontal = Dimens.grid_2),
                    headlineText = stringResource(id = R.string.uvindex_more_remove_ads_title)
                ) {
                    ListItem(
                        modifier = Modifier.clickable {
//                            onDetailInfo(AppScreen.More.Parts.Item.UVInfo)
                        },
                        headlineContent = {
                            Text(text = stringResource(id = R.string.uvindex_more_remove_ads_item))
                        }
                    )
                }
            }

            item {
                PreferenceCard(
                    modifier = Modifier.padding(horizontal = Dimens.grid_2),
                    headlineText = stringResource(id = R.string.uvindex_more_color_title)
                ) {
                    val themeState by themeViewModel.state.collectAsState()
                    val nameThemeModes =
                        stringArrayResource(id = R.array.uvindex_more_color_light_items)

                    ThemeMode.values().forEach { theme ->
                        Row(
                            Modifier
                                .fillMaxWidth()
                                .height(56.dp)
                                .selectable(
                                    selected = (theme == themeState),
                                    onClick = {
                                        themeViewModel.updateMode(theme)
                                    },
                                    role = Role.RadioButton
                                )
                                .padding(horizontal = Dimens.grid_2),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(selected = (theme == themeState), onClick = null)
                            Text(
                                text = nameThemeModes[theme.ordinal],
                                style = MaterialTheme.typography.bodyLarge,
                                modifier = Modifier.padding(start = Dimens.grid_2)
                            )
                        }
                    }
                }
            }

            item {
                PreferenceCard(
                    modifier = Modifier.padding(horizontal = Dimens.grid_2),
                    headlineText = stringResource(id = R.string.uvindex_more_info_title)
                ) {
                    ListItem(
                        modifier = Modifier.clickable {
                            onDetailInfo(AppScreen.More.Parts.Item.UVInfo)
                        },
                        headlineContent = {
                            Text(text = stringResource(id = R.string.uvindex_more_info_uvindex_item))
                        }
                    )
                }
            }

            item {

                PreferenceCard(
                    modifier = Modifier.padding(horizontal = Dimens.grid_2),
                    headlineText = stringResource(id = R.string.uvindex_more_support_title)
                ) {
                    ListItem(
                        modifier = Modifier.clickable {
                            onDetailInfo(AppScreen.More.Parts.Item.PrivacyInfo)
                        },
                        headlineContent = {
                            Text(text = stringResource(id = R.string.uvindex_more_support_privacy_item))
                        }
                    )
                }
            }

        }
    }
}

@Composable
private fun HeaderTitle(
    modifier: Modifier = Modifier,
    text: String
) {
    Text(
        modifier = modifier,
        text = text.uppercase(),
        style = MaterialTheme.typography.labelLarge,
        color = MaterialTheme.colorScheme.secondary
    )
}

@Composable
private fun PreferenceCard(
    modifier: Modifier = Modifier,
    headlineText: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = modifier
    ) {
        Column {
            HeaderTitle(
                modifier = Modifier.padding(
                    start = Dimens.grid_2,
                    end = Dimens.grid_2,
                    top = Dimens.grid_2,
                    bottom = Dimens.grid_1
                ),
                text = headlineText
            )
            content()
        }
    }
}
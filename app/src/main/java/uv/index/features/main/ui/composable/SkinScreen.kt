package uv.index.features.main.ui.composable

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.selectable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import uv.index.R
import uv.index.features.main.ui.MainContract
import uv.index.features.main.ui.MainViewModel
import uv.index.lib.data.UVSkinType
import uv.index.navigation.AppNavigationBar
import uv.index.ui.theme.Dimens
import uv.index.ui.theme.UVITheme
import uv.index.ui.theme.contentColorFor

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SkinScreen(
    viewModel: MainViewModel,
    onNavigateUp: () -> Unit
) {
    val state by viewModel.state.collectAsState()

    val scrollBehavior: TopAppBarScrollBehavior =
        TopAppBarDefaults.enterAlwaysScrollBehavior(
            rememberTopAppBarState()
        )

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
//        containerColor = Color.Transparent,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        stringResource(id = R.string.uvindex_skin_type_select_title)
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
            modifier = Modifier.padding(it),
            verticalArrangement = Arrangement.spacedBy(Dimens.grid_1),
            contentPadding = PaddingValues(vertical = Dimens.grid_1)
//            state = lazyListState,
        ) {
            items(UVSkinType.values()) { item ->
                SkinItem(
                    skin = item,
                    selected = state.skinType == item,
                    onClick = { skin ->
                        viewModel.doEvent(MainContract.Event.DoChangeSkin(skin))
                    }
                )
            }
        }
    }
}

@Composable
private fun SkinItem(
    skin: UVSkinType,
    selected: Boolean,
    selectedContainerColor: Color = MaterialTheme.colorScheme.primaryContainer,
    selectedContentColor: Color = MaterialTheme.colorScheme.onPrimaryContainer,
    onClick: (UVSkinType) -> Unit
) {

    val backgroundColor by animateColorAsState(
        if (selected) selectedContainerColor else MaterialTheme.colorScheme.surface
    )

    val textColor by animateColorAsState(
        if (selected) selectedContentColor else MaterialTheme.colorScheme.onSurface
    )


    Card(
        modifier = Modifier.padding(horizontal = Dimens.grid_2),
    ) {

        Column(
            modifier = Modifier
                .selectable(
                    selected = selected,
                    onClick = {
                        onClick(skin)
                    }
                )
                .fillMaxWidth()
                .background(backgroundColor)
                .padding(Dimens.grid_1_5)
        ) {

            Row(
                modifier = Modifier
//                    .heightIn(min = 56.dp)
                ,
                verticalAlignment = Alignment.Top
            ) {
                Box(
                    Modifier
//                        .widthIn(min = 56.dp)
//                        .padding(
//                            top = 4.dp,
//                            bottom = 4.dp
//                        )
                    ,
                    contentAlignment = Alignment.Center
                ) {
                    Spacer(
                        modifier = Modifier
                            .size(40.dp)
                            .background(
                                UVITheme.skinColors.getColor(skin.ordinal),
                                MaterialTheme.shapes.small
                            )
                    )
                    Text(
                        text = (skin.ordinal + 1).toString(),
                        color = UVITheme.skinColors.contentColorFor(
                            backgroundColor = UVITheme.skinColors.getColor(skin.ordinal)
                        )
                    )
                }

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = Dimens.grid_2),
                    verticalArrangement = Arrangement.spacedBy(Dimens.grid_0_25)
                ) {
                    Text(
                        text = stringResource(id = R.string.uvindex_skin_typical_features_title).uppercase(),
                        style = MaterialTheme.typography.labelLarge,
                        color = textColor
                    )
                    Text(
                        text = stringArrayResource(id = R.array.uvindex_skin_typical_features)[skin.ordinal],
                        style = MaterialTheme.typography.bodyMedium,
                        color = textColor
                    )
                    Spacer(
                        modifier = Modifier.size(Dimens.grid_1)
                    )
                    Text(
                        text = stringResource(id = R.string.uvindex_skin_tanning_ability_title).uppercase(),
                        style = MaterialTheme.typography.labelLarge,
                        color = textColor
                    )
                    Text(
                        text = stringArrayResource(id = R.array.uvindex_skin_tanning_ability)[skin.ordinal],
                        style = MaterialTheme.typography.bodyMedium,
                        color = textColor
                    )
                }
            }
        }
    }

}
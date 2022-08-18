package uv.index.features.place.ui.composable

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import net.arwix.mvi.SimpleViewModel
import uv.index.R
import uv.index.features.place.parts.list.ui.PlaceListContract
import uv.index.features.place.parts.list.ui.PlaceListViewModel
import uv.index.features.place.parts.list.ui.composable.PlaceListSection

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlaceListScreen(
    viewModel: PlaceListViewModel,
    onNavigateUp: () -> Unit,
    onNextScreen: () -> Unit
) {

    LaunchedEffect(SimpleViewModel.SIDE_EFFECT_LAUNCH_ID) {
        viewModel.effect.onEach { effect ->
            when (effect) {
                PlaceListContract.Effect.ToEdit -> onNextScreen()
            }
        }.collect()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
    ) {

        Scaffold(
            modifier = Modifier.statusBarsPadding(),
            topBar = {
                SmallTopAppBar(
                    title = {
                        Text(
                            stringResource(id = R.string.place_screen_title_list)
                        )
                    }
                )
            }
        ) {
            PlaceListSection(
                Modifier.padding(it),
                viewModel,
//                scaffoldState = scaffoldState,
            )
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
                .navigationBarsPadding()
        ) {

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 32.dp)
            ) {

                ExtendedFloatingActionButton(
                    modifier = Modifier.weight(1f),
                    content = {
                        Text(
                            text = "Back"
                        )
                    },
                    onClick = onNavigateUp
                )

                Spacer(modifier = Modifier.width(16.dp))

                Row(
                    modifier = Modifier
                        .weight(1f),
                    horizontalArrangement = Arrangement.End
                ) {

                    PlaceListAddButton(
                        isLockAddPlace = false,
                        onAddPlace = {
                            viewModel.doEvent(PlaceListContract.Event.AddPlace)
                        }
                    )
                }

            }


//
//            PlaceWizardBottomBarComponent(
//                Modifier
//                    .align(Alignment.BottomCenter)
//                    .navigationBarsPadding(),
//                false,
//                isShowNextStep = false,
//                previousName = stringResource(R.string.place_navigation_bottom_bar_back),
//                nextName = stringResource(R.string.place_navigation_bottom_bar_next),
//                buttonShape = MaterialTheme.shapes.small,
//                onPreviousClick = {
//                },
//                onNextClick = {}
//            )
        }
    }
}

@Composable
internal fun PlaceListAddButton(
    modifier: Modifier = Modifier,
    isLockAddPlace: Boolean,
    onAddPlace: () -> Unit
) {
    val bgColor =
        if (isLockAddPlace) MaterialTheme.colorScheme.error
        else MaterialTheme.colorScheme.secondary
    val tint =
        (if (isLockAddPlace) MaterialTheme.colorScheme.onError
        else LocalContentColor.current).copy(alpha = LocalContentColor.current.alpha)
    val icon = if (isLockAddPlace) Icons.Filled.Lock else Icons.Filled.Add

    FloatingActionButton(
        modifier = modifier,
        onClick = onAddPlace,
//        backgroundColor = bgColor
    ) {
        Icon(icon, "add", tint = tint)
    }
}
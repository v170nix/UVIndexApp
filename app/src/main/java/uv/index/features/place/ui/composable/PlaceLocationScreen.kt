package uv.index.features.place.ui.composable

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import uv.index.R
import uv.index.features.place.parts.editlocation.ui.PlaceEditLocationViewModel
import uv.index.features.place.parts.editlocation.ui.composable.InputLocationBoxComponent
import uv.index.features.place.parts.editlocation.ui.composable.PlaceEditPositionComponent
import uv.index.ui.theme.Dimens

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlaceLocationScreen(
    viewModel: PlaceEditLocationViewModel,
    onNavigateBackStack: () -> Unit,
    onNextScreen: () -> Unit
) {

    val state by viewModel.state.collectAsState()

//    Box(
//        modifier = Modifier
//            .fillMaxSize()
//            .background(MaterialTheme.colorScheme.surface)
//    ) {

        Scaffold(
            topBar = {
                SmallTopAppBar(
                    modifier = Modifier.statusBarsPadding(),
                    title = {
                        Text(
                            stringResource(id = R.string.place_screen_title_select_location)
                        )
                    },
                    colors = TopAppBarDefaults.smallTopAppBarColors(
                        containerColor = Color.Transparent
                    )
                )
            }
        ) { paddingValues ->
            PlaceEditPositionComponent(
                modifier = Modifier.fillMaxSize(),
                logoOffset = DpOffset(
                    Dimens.grid_2 + paddingValues.calculateLeftPadding(LocalLayoutDirection.current),
                    72.dp
                            + paddingValues.calculateBottomPadding()
                            + WindowInsets.navigationBars.asPaddingValues()
                        .calculateBottomPadding()
                ),
                eventHandler = viewModel,
                state = state,
                effectFlow = viewModel.effect,
                onTimeZonePart = {}
//                    editPositionViewMapBridger!!
            )

            Column(
                modifier = Modifier.padding(paddingValues)
            ) {
//                PlaceTopAppBarComponent(
//                    title = stringResource(id = androidx.compose.foundation.layout.R.string.place_screen_title_select_location),
//                    isBlackColor = true,
//                    onNavigateBack = onNavigateBackStack
//                )

                InputLocationBoxComponent(
                    state = state.inputState,
                    eventHandler = viewModel,
                    onPreviousClick = onNavigateBackStack
                )
            }

//            PlaceWizardBottomBarComponent(
//                Modifier
//                    .align(Alignment.BottomCenter)
//                    .navigationBarsPadding(),
//                state.nextStepIsAvailable,
//                true,
//                stringResource(androidx.compose.foundation.layout.R.string.place_navigation_bottom_bar_back),
//                stringResource(androidx.compose.foundation.layout.R.string.place_navigation_bottom_bar_next),
//                buttonShape = MaterialTheme.shapes.small,
//                onPreviousClick = {
//                    editPositionViewModel.doEvent(PlaceEditPositionContract.Event.ClearData)
//                    onNavigateBackStack()
//                },
//                onNextClick = {
//                    editPositionViewModel.doEvent(PlaceEditPositionContract.Event.Submit)
//                }
//            )
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
                    onClick = onNavigateBackStack
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
//                            viewModel.doEvent(PlaceListContract.Event.AddPlace)
                        }
                    )
                }

            }

        }
//    }
}
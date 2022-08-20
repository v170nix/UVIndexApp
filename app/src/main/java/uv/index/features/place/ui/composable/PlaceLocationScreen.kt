package uv.index.features.place.ui.composable

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.DpOffset
import uv.index.R
import uv.index.features.place.parts.editlocation.ui.PlaceEditLocationViewModel
import uv.index.features.place.parts.editlocation.ui.PlaceLocationContract
import uv.index.features.place.parts.editlocation.ui.composable.InputLocationBoxComponent
import uv.index.features.place.parts.editlocation.ui.composable.PlaceEditPositionSection
import uv.index.features.place.ui.composable.components.PlaceWizardBottomBarComponent
import uv.index.ui.theme.Dimens

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlaceLocationScreen(
    viewModel: PlaceEditLocationViewModel,
    onNavigateBackStack: () -> Unit,
    onNextScreen: () -> Unit
) {

    val state by viewModel.state.collectAsState()
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
        },
        bottomBar = {
            PlaceWizardBottomBarComponent(
                isEnableNextStep = state.nextStepIsAvailable,
                isShowNextStep = true,
                previousName = stringResource(R.string.place_navigation_bottom_bar_back),
                nextName = stringResource(R.string.place_navigation_bottom_bar_next),
                onPreviousClick = {
                    viewModel.doEvent(PlaceLocationContract.Event.ClearData)
                    onNavigateBackStack()
                },
                onNextClick = {
                    viewModel.doEvent(PlaceLocationContract.Event.Submit)
                }
            )
        }
    ) { paddingValues ->

        PlaceEditPositionSection(
            modifier = Modifier.fillMaxSize(),
            logoOffset = DpOffset(
                Dimens.grid_2 + paddingValues.calculateLeftPadding(LocalLayoutDirection.current),
                Dimens.grid_1 + paddingValues.calculateBottomPadding()
            ),
            eventHandler = viewModel,
            state = state,
            effectFlow = viewModel.effect,
            onTimeZonePart = {}
        )

        Column(
            modifier = Modifier.padding(paddingValues)
        ) {
            InputLocationBoxComponent(
                state = state.inputState,
                eventHandler = viewModel,
                onPreviousClick = onNavigateBackStack
            )
        }
    }
}
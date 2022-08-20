package uv.index.features.place.ui.composable

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import net.arwix.mvi.SimpleViewModel
import uv.index.R
import uv.index.features.place.parts.list.ui.PlaceListContract
import uv.index.features.place.parts.list.ui.PlaceListViewModel
import uv.index.features.place.parts.list.ui.composable.PlaceListSection
import uv.index.features.place.ui.composable.components.PlaceWizardBottomBarComponent

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
            },
            bottomBar = {
                PlaceWizardBottomBarComponent(
                    isEnableNextStep = true,
                    isShowNextStep = true,
                    previousName = stringResource(R.string.place_navigation_bottom_bar_back),
                    nextName = stringResource(R.string.place_navigation_bottom_bar_add),
                    onPreviousClick = onNavigateUp,
                    onNextClick = {
                        viewModel.doEvent(PlaceListContract.Event.AddPlace)
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
    }
}
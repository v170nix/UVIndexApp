package uv.index.features.place.ui.composable

import androidx.compose.animation.rememberSplineBasedDecay
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import net.arwix.mvi.SimpleViewModel
import uv.index.R
import uv.index.features.place.parts.editzone.ui.PlaceEditTimeZoneContract
import uv.index.features.place.parts.editzone.ui.PlaceEditTimeZoneViewModel
import uv.index.features.place.parts.editzone.ui.composable.PlaceEditTimeZoneSection
import uv.index.features.place.ui.composable.components.PlaceWizardBottomBarComponent
import uv.index.ui.theme.Dimens

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlaceTimeZoneScreen(
    viewModel: PlaceEditTimeZoneViewModel,
    onNavigateBackStack: () -> Unit,
    onFinish: () -> Unit
) {

    val decayAnimationSpec = rememberSplineBasedDecay<Float>()
    val scrollBehavior: TopAppBarScrollBehavior =
        TopAppBarDefaults.exitUntilCollapsedScrollBehavior(
            decayAnimationSpec,
            rememberTopAppBarState()
        )

    val state by viewModel.state.collectAsState()

    LaunchedEffect(SimpleViewModel.SIDE_EFFECT_LAUNCH_ID) {
        viewModel.effect.onEach { effect ->
            when (effect) {
                PlaceEditTimeZoneContract.Effect.OnSubmitData ->
                    onFinish()
            }
        }.collect()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
    ) {

        Scaffold(
            modifier = Modifier.statusBarsPadding().nestedScroll(scrollBehavior.nestedScrollConnection),
            topBar = {
                SmallTopAppBar(
                    title = {
                        Text(
                            stringResource(id = R.string.place_screen_title_select_time_zone)
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = onNavigateBackStack) {
                            Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "back")
                        }
                    },
                    scrollBehavior = scrollBehavior
                )
            },
            bottomBar = {
                PlaceWizardBottomBarComponent(
                    isEnableNextStep = state.finishStepAvailable,
                    isShowNextStep = true,
                    previousName = stringResource(R.string.place_navigation_bottom_bar_back),
                    nextName = stringResource(R.string.place_navigation_bottom_bar_finish),
                    onPreviousClick = {
                        onNavigateBackStack()
                    },
                    onNextClick = {
                        viewModel.doEvent(PlaceEditTimeZoneContract.Event.Submit)
                    }
                )
            }
        ) { paddingValues ->

            val direction = LocalLayoutDirection.current
            val dimens = Dimens

            val paddingExceptTopBar by remember(paddingValues, direction) {
                derivedStateOf {
                    PaddingValues(
                        top = paddingValues.calculateTopPadding(),
                        start = paddingValues.calculateStartPadding(direction),
                        end = paddingValues.calculateEndPadding(direction),
                        bottom = 0.dp
                    )
                }
            }

            val paddingExceptBottomBar by remember(paddingValues, dimens) {
                derivedStateOf {
                    PaddingValues(
                        top = 0.dp,
                        start = 0.dp,
                        end = 0.dp,
                        bottom = paddingValues.calculateBottomPadding() + dimens.grid_1
                    )
                }
            }

            Column(
                modifier = Modifier.padding(paddingExceptTopBar)
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    PlaceEditTimeZoneSection(
                        state = state,
                        eventHandler = viewModel,
                        contentListPadding = paddingExceptBottomBar
                    )
                    Spacer(
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.BottomCenter)
                            .height(
                                WindowInsets.navigationBars
                                    .asPaddingValues()
                                    .calculateBottomPadding()
                            )
                            .background(MaterialTheme.colorScheme.background.copy(alpha = 0.8f))
                    )
                }
            }
        }
    }
}
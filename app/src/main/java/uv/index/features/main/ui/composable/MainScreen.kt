package uv.index.features.main.ui.composable

import android.util.Log
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.toUpperCase
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import uv.index.R
import uv.index.common.LifecycleTimer
import uv.index.features.astronomy.data.SunPosition
import uv.index.features.main.common.getUVIColor
import uv.index.features.main.ui.MainContract
import uv.index.features.main.ui.MainViewModel
import uv.index.features.main.ui.composable.sections.dataview.MainDataSection
import uv.index.features.main.ui.composable.sections.dataview.components.MainPlacePart
import uv.index.features.main.ui.composable.sections.emptyplace.EmptyPlaceSection
import uv.index.ui.theme.Dimens
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    viewModel: MainViewModel,
    onChangePlace: () -> Unit
) {

    val scrollBehavior: TopAppBarScrollBehavior =
        TopAppBarDefaults.exitUntilCollapsedScrollBehavior(
            rememberTopAppBarState()
        )

    val state by viewModel.state.collectAsState()

    LaunchedEffect(key1 = state.place) {
        Log.e("state launch", state.place.toString())
    }

    LifecycleTimer(timeMillis = 60_000L) {
        viewModel.doEvent(MainContract.Event.DoDataAutoUpdate)
    }

    LifecycleTimer(timeMillis = 1_000L) {
        viewModel.doEvent(MainContract.Event.DoUpdateWithCurrentTime)
    }

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {

        Log.e("state launch1", state.place.toString())
        if (state.place == null) {
            Log.e("state.place", state.toString())
            if (!state.isLoadingPlace) {
                Log.e("state", "!isLoading Place")
                EmptyPlaceSection(
                    modifier = Modifier.fillMaxSize(),
                    onAddPlaceScreen = onChangePlace
                )
            } else {
                Text("place == null and isLoadingPlace == true")
            }
        } else {
            DataPart(
                scrollBehavior = scrollBehavior,
                state = state,
                onChangePlace = onChangePlace,
                onRetryClick = {
                    viewModel.doEvent(MainContract.Event.DoDataManualUpdate)
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun BoxWithConstraintsScope.DataPart(
    scrollBehavior: TopAppBarScrollBehavior,
    state: MainContract.State,
    onChangePlace: () -> Unit,
    onRetryClick: () -> Unit
) {

    val isDataLoaded by remember(state) {
        derivedStateOf {
            state.place != null && state.uvCurrentData != null
        }
    }

    MainBackground(
        behaviorState = scrollBehavior.state,
        state = state,
        collapsedHeight = 64.dp,
        backgroundColor = MaterialTheme.colorScheme.background
    )

//    AnimatedVisibility(visible = isDataLoaded) {
//
//    }

//    MainPlacePart(
//        modifier = Modifier
//            .padding(horizontal = Dimens.grid_1)
//            .fillMaxWidth(),
//        onEditPlace = onChangePlace,
//        place = state.place
//    )

    Crossfade(
        targetState = isDataLoaded, animationSpec = tween(1500)
    ) {targetState ->
        when (targetState) {
            true -> {
                MainDataSection(
                    modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
                    scrollBehavior,
                    placeContent = {
                        MainPlacePart(
                            modifier = Modifier
                                .padding(horizontal = Dimens.grid_1)
                                .fillMaxWidth(),
                            onEditPlace = onChangePlace,
                            place = state.place
                        )
                    },
                    state,
                    onEditPlace = onChangePlace
                )

            }
            false -> {
                LoadingDataPart(
                    loaderIsVisible = state.isViewLoadingData && !isDataLoaded,
                    retryIsVisible = state.isViewRetry,
                    placeContent = {
                        MainPlacePart(
                            modifier = Modifier
                                .padding(horizontal = Dimens.grid_1)
                                .fillMaxWidth(),
                            onEditPlace = onChangePlace,
                            place = state.place
                        )
                    },
                    onRetryClick = onRetryClick
                )
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class)
@Composable
private fun LoadingDataPart(
    loaderIsVisible: Boolean,
    retryIsVisible: Boolean,
    placeContent: @Composable () -> Unit,
    onRetryClick: () -> Unit
) {
    Scaffold(
        containerColor = Color.Transparent,
    ) {

        Box(Modifier.padding(it)) {
            CompositionLocalProvider(
                LocalContentColor provides Color.White
            ) {
                placeContent()
            }
        }
        Box(modifier = Modifier.fillMaxSize()) {
            Column(modifier = Modifier.align(Alignment.Center)) {

                val innerRetryIsVisible by remember(retryIsVisible, loaderIsVisible) {
                    derivedStateOf {
                        retryIsVisible && !loaderIsVisible
                    }
                }

                AnimatedContent(
                    targetState = innerRetryIsVisible,
                    transitionSpec = {
                        fadeIn(animationSpec = tween(220, delayMillis = 90)) with fadeOut(
                            animationSpec = tween(90)
                        )
                    }
                ) { targetState ->
                    when (targetState) {
                        true -> {
                            Column(modifier = Modifier.align(Alignment.CenterHorizontally)) {
                                Text(
                                    text = stringResource(R.string.app_load_data_error_info),
                                    textAlign = TextAlign.Center,
                                    style = MaterialTheme.typography.bodyLarge.copy(
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                )
                                Spacer(modifier = Modifier.height(Dimens.grid_2))
                                Button(
                                    modifier = Modifier.align(Alignment.CenterHorizontally),
                                    onClick = onRetryClick,
                                    colors = ButtonDefaults.filledTonalButtonColors()
//                                shape = MaterialTheme.shapes.button
                                ) {
                                    Text(
                                        text = stringResource(R.string.app_load_data_error_button)
                                            .toUpperCase(Locale.current)
                                    )
                                }
                            }
//                                    }
                        }
                        false -> {
                            AnimatedVisibility(
                                visible = loaderIsVisible,
                                enter = fadeIn(),
                                exit = fadeOut(),
                                modifier = Modifier.align(Alignment.CenterHorizontally)
                            ) {
                                CircularProgressIndicator()
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
@Suppress("MagicNumber")
private fun BoxWithConstraintsScope.MainBackground(
    modifier: Modifier = Modifier,
    behaviorState: TopAppBarState,
    state: MainContract.State,
    collapsedHeight: Dp,
    backgroundColor: Color,
) {
    val boxScope = this
    val density = LocalDensity.current
    val statusBar = WindowInsets.statusBars

    val currentIndexIntValue by remember(state.uvCurrentData?.index) {
        derivedStateOf {
            state.uvCurrentData?.index?.roundToInt() ?: 0
        }
    }

    val currentSunPosition by remember(state.currentSunData) {
        derivedStateOf {
            state.currentSunData?.position ?: SunPosition.Above
        }
    }

    val currentHighlightColor by animateColorAsState(
        targetValue = getUVIColor(currentIndexIntValue, currentSunPosition, backgroundColor),
        animationSpec = tween(
            durationMillis = 500,
        )
    )
    val currentBgColor by animateColorAsState(
        targetValue = backgroundColor, animationSpec = tween(
            durationMillis = 500,
        )
    )

    val xCenterOffset by remember(density, boxScope) {
        derivedStateOf { boxScope.maxWidth.value * 0.8f * density.density }
    }

    val radius by remember(density, boxScope) {
        derivedStateOf { boxScope.maxWidth.value * 1.4f * density.density }
    }

    val endYVerticalGradient by remember(density, statusBar, collapsedHeight) {
        derivedStateOf {
            statusBar.getTop(density) + collapsedHeight.value * density.density
        }
    }

    Spacer(modifier = modifier
        .fillMaxSize()
        .graphicsLayer {
            translationY = radius * (-behaviorState.collapsedFraction) * 1.1f
            alpha = (1f - behaviorState.collapsedFraction / 0.9f).coerceIn(0.01f, 1f)
        }
        .background(
            brush = Brush.radialGradient(
                colors = listOf(
                    currentHighlightColor,
                    currentHighlightColor.copy(alpha = 0xAA.toFloat() / 0xFF),
                    currentBgColor
                ),
                center = Offset(xCenterOffset, 0f),
                radius = radius,
            )
        ))

    Spacer(modifier = modifier
        .fillMaxSize()
        .graphicsLayer {
            alpha = (behaviorState.collapsedFraction + 0.5f).coerceIn(0.01f, 0.8f)
        }
        .background(
            brush = Brush.verticalGradient(
                colors = listOf(
                    currentHighlightColor.copy(alpha = 0xFF.toFloat() / 0xFF),
                    Color.Transparent,
                ), startY = 0f, endY = endYVerticalGradient
            )
        ))
}
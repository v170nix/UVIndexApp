package uv.index.features.main.ui.composable

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.rememberSplineBasedDecay
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import uv.index.common.LifecycleTimer
import uv.index.features.main.common.getUVIColor
import uv.index.features.main.domain.SunPosition
import uv.index.features.main.ui.MainContract
import uv.index.features.main.ui.MainViewModel
import uv.index.features.main.ui.composable.sections.dataview.MainDataSection
import uv.index.features.main.ui.composable.sections.emptyplace.EmptyPlaceSection
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    viewModel: MainViewModel,
    onChangePlace: () -> Unit
) {

    val decayAnimationSpec = rememberSplineBasedDecay<Float>()
    val scrollBehavior: TopAppBarScrollBehavior =
        TopAppBarDefaults.exitUntilCollapsedScrollBehavior(
            decayAnimationSpec,
            rememberTopAppBarState()
        )

    val state by viewModel.state.collectAsState()

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

        if (state.place == null) {
            if (!state.isLoadingPlace) {
                EmptyPlaceSection(
                    modifier = Modifier.fillMaxSize(),
                    onAddPlaceScreen = onChangePlace
                )
            }
        } else {
            DataPart(
                scrollBehavior = scrollBehavior,
                state = state,
                onChangePlace = onChangePlace
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun BoxWithConstraintsScope.DataPart(
    scrollBehavior: TopAppBarScrollBehavior,
    state: MainContract.State,
    onChangePlace: () -> Unit
) {

    val isDataLoaded by remember(state) {
        derivedStateOf {
            state.currentDayData != null && state.place != null && state.currentIndexValue != null
        }
    }

    if (isDataLoaded) {
        MainBackground(
            behaviorState = scrollBehavior.state,
            state = state,
            collapsedHeight = 64.dp,
            backgroundColor = MaterialTheme.colorScheme.background
        )

        MainDataSection(
            modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
            scrollBehavior,
            state,
            onEditPlace = onChangePlace
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
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

    val currentIndexIntValue by remember(state.currentIndexValue) {
        derivedStateOf {
            state.currentIndexValue?.roundToInt() ?: Int.MIN_VALUE
        }
    }

    val currentSunPosition by remember(state.currentSunPosition) {
        derivedStateOf {
            state.currentSunPosition ?: SunPosition.Above
        }
    }

    val currentHighlightColor by animateColorAsState(
        targetValue = getUVIColor(currentIndexIntValue, currentSunPosition, backgroundColor),
        animationSpec = tween(
            durationMillis = 500,
        )
    )
    val currentBgColor by animateColorAsState(
        targetValue = backgroundColor,
        animationSpec = tween(
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

    Spacer(
        modifier = modifier
            .fillMaxSize()
            .graphicsLayer {
                translationY = radius * (-behaviorState.collapsedFraction) * 1.1f
                alpha = (1f - behaviorState.collapsedFraction / 0.9f).coerceIn(0.01f, 1f)
            }
            .background(
                brush =
                Brush.radialGradient(
                    colors = listOf(
                        currentHighlightColor,
                        currentHighlightColor.copy(alpha = 0xAA.toFloat() / 0xFF),
                        currentBgColor
                    ),
                    center = Offset(xCenterOffset, 0f),
                    radius = radius,
                )
            )
    )

    Spacer(
        modifier = modifier
            .fillMaxSize()
            .graphicsLayer {
                alpha = (behaviorState.collapsedFraction + 0.5f).coerceIn(0.01f, 0.8f)
            }
            .background(
                brush =
                Brush.verticalGradient(
                    colors = listOf(
                        currentHighlightColor.copy(alpha = 0xFF.toFloat() / 0xFF),
                        Color.Transparent,
                    ),
                    startY = 0f,
                    endY = endYVerticalGradient
                )
            )
    )
}
package uv.index.parts.main.ui.composable

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.rememberSplineBasedDecay
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import uv.index.R
import uv.index.common.LifecycleTimer
import uv.index.lib.data.UVIndexData
import uv.index.lib.data.UVSummaryDayData
import uv.index.parts.main.common.getUVIColor
import uv.index.parts.main.domain.SunPosition
import uv.index.parts.main.ui.MainContract
import uv.index.parts.main.ui.MainViewModel
import java.time.LocalDate
import java.time.LocalTime
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(viewModel: MainViewModel) {

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

        val isDataLoaded by remember(state) {
            derivedStateOf {
                state.currentDayData != null && state.place != null && state.currentIndexValue != null
            }
        }

        val lazyListState = rememberLazyListState()
        MainBackground(
            behaviorState = scrollBehavior.state,
            state = state,
            collapsedHeight = 64.dp,
            backgroundColor = MaterialTheme.colorScheme.background
        )

        if (isDataLoaded) {
            DataPart(
                lazyListState = lazyListState,
                scrollBehavior = scrollBehavior,
                state = state
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun BoxWithConstraintsScope.DataPart(
    lazyListState: LazyListState,
    scrollBehavior: TopAppBarScrollBehavior,
    state: MainContract.State
) {

    Scaffold(
        containerColor = Color.Transparent,
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            MainCurrentInfoTopBar(
                scrollBehavior = scrollBehavior,
                state = state
            )
        }
    ) {

        CompositionLocalProvider(
            LocalContentColor provides contentColorFor(
                MaterialTheme.colorScheme.surface
            )
        ) {

            LazyColumn(
                modifier = Modifier.padding(it),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp),
                state = lazyListState,
            ) {

                mainBackgroundHeader(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(16.dp),
                    state = scrollBehavior.state
                )

                item {
                    MainTimeToEventPart(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        timeToBurn = state.currentTimeToBurn
                    )
                }
                item {
                    MainProtectionPart(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 24.dp, bottom = 0.dp),
                        uvSummaryDayData = state.currentSummaryDayData
                    )
                }

                item {
                    MainSunRiseSetPart(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 0.dp),
                        riseTime = state.riseTime,
                        setTime = state.setTime
                    )
                }

                item {
                    Column(
                        Modifier
                            .fillMaxWidth()
                            .padding(top = 24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = stringResource(id = R.string.uvindex_forecast_title),
                            style = MaterialTheme.typography.labelLarge
                        )

                        Spacer(modifier = Modifier.height(16.dp))
                        MainForecastHoursPart(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 0.dp),
                            hoursList = state.currentUiHoursData
                        )
                    }
                }

                item {
                    MainForecastDayPart(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                            .padding(top = 16.dp),
                        data = listOf(
                            UVSummaryDayData(
                                day = LocalDate.now().plusDays(1),
                                maxIndex = UVIndexData(0L, 0, 0, 4.3),
                                timeProtectionBegin = LocalTime.now(),
                                timeProtectionEnd = LocalTime.now()
                            ),
                            UVSummaryDayData(
                                day = LocalDate.now().plusDays(2),
                                maxIndex = UVIndexData(0L, 0, 0, 7.3),
                                timeProtectionBegin = LocalTime.now(),
                                timeProtectionEnd = LocalTime.now()
                            ),
                            UVSummaryDayData(
                                day = LocalDate.now().plusDays(3),
                                maxIndex = UVIndexData(0L, 0, 0, 9.8),
                                timeProtectionBegin = LocalTime.now(),
                                timeProtectionEnd = LocalTime.now()
                            )
                        )
                    )
                }
            }
        }
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

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
private fun LazyListScope.mainBackgroundHeader(
    modifier: Modifier = Modifier,
    state: TopAppBarState,
) {
    stickyHeader {
        Box(modifier = modifier) {

            val alpha by remember(state.collapsedFraction) {
                derivedStateOf {
                    ((state.collapsedFraction - 0.9)
                        .coerceAtLeast(0.0) * 10.0)
                        .coerceIn(0.01, 1.0)
                        .toFloat()
                }
            }
            Spacer(
                modifier = Modifier
                    .fillMaxSize()
                    .alpha(alpha)
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.background,
                                Color.Transparent
                            ),
                        )
                    )
            )
        }
    }
}
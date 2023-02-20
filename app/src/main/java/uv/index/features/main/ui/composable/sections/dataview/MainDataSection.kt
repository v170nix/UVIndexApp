package uv.index.features.main.ui.composable.sections.dataview

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import uv.index.R
import uv.index.features.main.ui.MainContract
import uv.index.features.main.ui.composable.sections.dataview.components.*
import uv.index.features.weather.ui.weatherPart
import uv.index.navigation.AppNavigationBar
import uv.index.ui.theme.Dimens
import java.time.ZonedDateTime

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Suppress("LongMethod", "FunctionNaming")
@Composable
internal fun BoxWithConstraintsScope.MainDataSection(
    modifier: Modifier = Modifier,
    scrollBehavior: TopAppBarScrollBehavior,
    placeContent: @Composable (fraction: Float) -> Unit,
    state: MainContract.State,
    onEditPlace: () -> Unit
) {

    val lazyListState = rememberLazyListState()

    var visibleWeatherPart by remember() {
        mutableStateOf(false)
    }

    val isShowCurrentZdt by remember(state.currentDateTime) {
        derivedStateOf {
            ZonedDateTime.now().offset.totalSeconds != state.currentDateTime?.offset?.totalSeconds
        }
    }

    val infoState = remember {
        MainInfoState(MainInfoData(""))
    }

    val uvIndexInfoDialogState = rememberUVIndexInfoDialogState(state.uvCurrentData?.index)
    MainUVIndexInfoDialog(uvIndexInfoDialogState)

    Scaffold(
        containerColor = Color.Transparent,
        modifier = modifier,
        topBar = {
            MainCurrentInfoTopBarPart(
                scrollBehavior = scrollBehavior,
                state = state,
                placeContent = placeContent,
                onEditPlace = onEditPlace,
                onShowIndexInfo = {
                    uvIndexInfoDialogState.isShow = true
                }
            )
        },
        bottomBar = {
            AppNavigationBar()
        },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                visibleWeatherPart = !visibleWeatherPart
            }) {
                Icon(Icons.Default.KeyboardArrowUp, "Switch")
            }
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

                if (!visibleWeatherPart) {

                    item(key = 1) {
                        MainTimeToEventPart(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = Dimens.grid_2)
                                .animateItemPlacement(),
                            infoState = infoState,
                            timeToBurn = state.uvCurrentData?.timeToBurn,
                            timeToVitaminD = state.uvCurrentData?.timeToVitaminD,
                        )
                    }

                    item(key = 2) {
                        MainSunscreenReminder(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = Dimens.grid_2)
                                .padding(top = Dimens.grid_2),
                        )
                    }

                    item(key = 3) {
                        MainProtectionPart(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = Dimens.grid_1_5),
                            uvSummaryDayData = state.uvCurrentSummaryDayData
                        )
                    }

                    item(key = 4) {
                        Column(
                            modifier = Modifier,
                            horizontalAlignment = Alignment.CenterHorizontally

                        ) {
                            Divider(
                                modifier = Modifier
                                    .padding(
                                        horizontal = Dimens.grid_2,
                                        vertical = Dimens.grid_1_5
                                    )
                                    .fillMaxWidth()
                            )
                            Text(
                                text = stringResource(id = R.string.uvindex_local_time_text),
                                style = MaterialTheme.typography.labelLarge,
                            )
                            MainCurrentTimePart(
                                modifier = Modifier,
                                currentZdt = state.currentDateTime
                            )
                        }
                    }

                    item(key = 5) {
                        MainSunRiseSetPart(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 0.dp),
                            riseTime = state.currentSunData?.riseTime,
                            setTime = state.currentSunData?.setTime
                        )
                    }

                    item(key = 6) {
                        Column(
                            Modifier
                                .fillMaxWidth()
                                .padding(top = 0.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Divider(
                                modifier = Modifier
                                    .padding(
                                        horizontal = Dimens.grid_2,
                                        vertical = Dimens.grid_1_5
                                    )
                                    .fillMaxWidth()
                            )
                            Text(
                                text = stringResource(id = R.string.uvindex_forecast_title),
                                style = MaterialTheme.typography.labelLarge
                            )

                            Spacer(modifier = Modifier.height(16.dp))
                            MainForecastHoursPart(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 0.dp),
                                place = state.place,
                                hoursList = state.uvForecastHours
                            )
                        }
                    }

                    item(key = 7) {

                        MainForecastDayPart(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp)
                                .padding(top = 16.dp),
                            data = state.uvForecastDays
                        )
                    }

                } else {
                    weatherPart()
                }
            }

            MainInfoHost(infoState)

//            } else {
//                WeatherPart()
//            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Suppress("MagicNumber")
private fun LazyListScope.mainBackgroundHeader(
    modifier: Modifier = Modifier,
    state: TopAppBarState,
) {
    stickyHeader(key = 200) {

        Box(modifier = modifier.animateItemPlacement()) {

            val alpha by remember(state.collapsedFraction) {
                derivedStateOf {
                    (
                            (state.collapsedFraction - 0.9)
                                .coerceAtLeast(0.0) * 10.0
                            )
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

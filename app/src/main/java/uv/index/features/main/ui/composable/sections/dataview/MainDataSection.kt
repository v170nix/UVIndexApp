package uv.index.features.main.ui.composable.sections.dataview

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import net.arwix.mvi.EventHandler
import uv.index.R
import uv.index.features.main.ui.MainContract
import uv.index.features.main.ui.composable.sections.dataview.components.*
import uv.index.features.weather.ui.composable.weatherPart
import uv.index.navigation.AppNavigationBar
import uv.index.ui.theme.Dimens

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Suppress("LongMethod", "FunctionNaming")
@Composable
internal fun BoxWithConstraintsScope.MainDataSection(
    modifier: Modifier = Modifier,
    scrollBehavior: TopAppBarScrollBehavior,
    placeContent: @Composable (fraction: Float) -> Unit,
    state: MainContract.State,
    handler: EventHandler<MainContract.Event>
) {
    val lazyListState = rememberLazyListState()

    val infoState = remember {
        MainInfoState(MainInfoData(""))
    }

    val uvIndexInfoDialogState = rememberUVIndexInfoDialogState(state.uvCurrentData?.index)
    UVIndexInfoDialog(uvIndexInfoDialogState)

    Scaffold(
        containerColor = Color.Transparent,
        modifier = modifier,
        topBar = {
            MainCurrentInfoTopBarPart(
                scrollBehavior = scrollBehavior,
                state = state,
                placeContent = placeContent,
                onShowIndexInfo = {
                    uvIndexInfoDialogState.isShow = true
                }
            )
        },
        bottomBar = {
            AppNavigationBar()
        },
        floatingActionButton = {
            FloatingActionButton(
                modifier = Modifier.defaultMinSize(64.dp),
                onClick = {
                    handler.doEvent(MainContract.Event.DoChangeViewMode)
                }) {
                Icon(
                    imageVector = ImageVector.vectorResource(id = R.drawable.ic_change_mode),
                    "Switch"
                )
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

                if (state.viewMode == MainContract.ViewMode.UV) {

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

//                    item(key = 2) {
//                        UVSunscreenReminder(
//                            modifier = Modifier
//                                .fillMaxWidth()
//                                .padding(horizontal = Dimens.grid_2)
//                                .padding(top = Dimens.grid_2),
//                        )
////                        Spacer(modifier = Modifier.height(200.dp))
//                    }

                    item(key = 3) {
                        if (state.currentDateTime != null) {
                            Column {
                                MainProtectionPart(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(
                                            top = Dimens.grid_5_5,
                                            start = Dimens.grid_2,
                                            end = Dimens.grid_2,
                                            bottom = 0.dp
                                        ),
                                    uvSummaryDayData = state.uvCurrentSummaryDayData,
                                    currentDayHours = state.uvCurrentDayHours,
                                    currentZdt = state.currentDateTime,
                                    titleStyle = MaterialTheme.typography.titleSmall.copy(
                                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                                    ),
                                    textStyle = MaterialTheme.typography.bodyLarge
                                )
                                MainSunRiseSetPart(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 16.dp, vertical = 0.dp),
                                    riseTime = state.currentSunData?.riseTime,
                                    setTime = state.currentSunData?.setTime,
                                    titleStyle = MaterialTheme.typography.titleSmall.copy(
                                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                                    ),
                                    textStyle = MaterialTheme.typography.bodyLarge

                                )
                            }
                        }
                    }

                    item(key = 6) {
                        Column(
                            Modifier
                                .fillMaxWidth()
                                .padding(top = 0.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Spacer(modifier = Modifier.height(Dimens.grid_2_5))
                            Divider(
                                modifier = Modifier
                                    .padding(
                                        horizontal = Dimens.grid_2,
                                        vertical = Dimens.grid_1_5
                                    )
                                    .fillMaxWidth()
                            )
                            Spacer(modifier = Modifier.height(Dimens.grid_2_5))
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = Dimens.grid_2),
                                verticalAlignment = Alignment.CenterVertically,
//                                horizontalArrangement = Arrangement.Center
                            ) {
//                                Icon(
//                                    ImageVector.vectorResource(id = R.drawable.ic_forecast),
//                                    contentDescription = null
//                                )
                                Text(
                                    text = stringResource(id = R.string.uvindex_forecast_hour_title).uppercase(),
                                    style = MaterialTheme.typography.labelLarge.copy(
                                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                                    )
                                )
//                                Spacer(modifier = Modifier.width(48.dp))
                            }

                            Spacer(modifier = Modifier.height(Dimens.grid_2))
                            UVForecastHoursPart(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 0.dp),
                                place = state.place,
                                hoursList = state.uvForecastHours
                            )
                        }
                    }

                    item(key = 7) {

                        Spacer(modifier = Modifier.height(Dimens.grid_2_5))
                        Divider(
                            modifier = Modifier
                                .padding(
                                    horizontal = Dimens.grid_2,
                                    vertical = Dimens.grid_1_5
                                )
                                .fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(Dimens.grid_2_5))

                        Text(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = Dimens.grid_2),
                            text = stringResource(id = R.string.uvindex_forecast_day_title).uppercase(),
                            style = MaterialTheme.typography.labelLarge.copy(
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                            ),
                            textAlign = TextAlign.Start
                        )

                        Spacer(modifier = Modifier.height(Dimens.grid_1))

                        UVForecastDayPart(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp),
                            data = state.uvForecastDays,
                            titleStyle = MaterialTheme.typography.titleSmall,
                            textStyle = MaterialTheme.typography.bodyLarge
                                .copy(
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                                )
                        )
                        Spacer(modifier = Modifier.height(96.dp))
                    }

                } else {
                    if (state.place?.zone != null) {
                        weatherPart(
                            zoneId = state.place.zone,
                            data = state.weatherData
                        )
                    }
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

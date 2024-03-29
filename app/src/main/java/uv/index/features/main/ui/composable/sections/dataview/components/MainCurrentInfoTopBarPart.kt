package uv.index.features.main.ui.composable.sections.dataview.components

import androidx.annotation.DrawableRes
import androidx.compose.animation.*
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.max
import androidx.compose.ui.unit.sp
import uv.index.R
import uv.index.features.main.ui.MainContract
import uv.index.features.preferences.ui.rememberWeatherMetricsMode
import uv.index.features.uvi.data.UVLevel
import uv.index.features.uvi.ui.UVTitle
import uv.index.features.weather.ui.composable.WeatherBigTemperature
import uv.index.features.weather.ui.composable.WeatherFeelsLike
import uv.index.features.weather.ui.composable.WeatherTitle
import uv.index.ui.theme.Dimens
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class)
@Composable
@Suppress("LongMethod")
fun BoxWithConstraintsScope.MainCurrentInfoTopBarPart(
    modifier: Modifier = Modifier,
    scrollBehavior: TopAppBarScrollBehavior,
    placeContent: @Composable (fraction: Float) -> Unit,
    collapsedHeight: Dp = 64.dp,
    state: MainContract.State,
    onShowIndexInfo: () -> Unit
) {
    val statusHeight: Dp by animateDpAsState(
        targetValue = max(
            this.maxWidth * (1 - scrollBehavior.state.collapsedFraction),
            collapsedHeight
        ),
        label = "topHeight"
    )

    val currentIndex by remember(state.uvCurrentData?.index) {
        derivedStateOf {
            state.uvCurrentData?.index?.roundToInt() ?: 0
        }
    }

    val maxIndex by remember(state.uvCurrentSummaryDayData) {
        derivedStateOf {
            state.uvCurrentSummaryDayData?.maxIndex?.getIntIndex() ?: 0
        }
    }

    TopAppBar(
        modifier = modifier
            .statusBarsPadding()
            .height(statusHeight),
        title = {},
        colors = TopAppBarDefaults.smallTopAppBarColors(
            containerColor = Color.Transparent,
            scrolledContainerColor = Color.Transparent,
            titleContentColor = Color.Transparent
        ),
        scrollBehavior = scrollBehavior
    )

    Column(
        Modifier
            .statusBarsPadding()
            .height(statusHeight)
    ) {

        val inverseSurface = contentColorFor(MaterialTheme.colorScheme.inverseSurface)
        val surface = contentColorFor(MaterialTheme.colorScheme.surface)
        val metricsMode = rememberWeatherMetricsMode()

        MainCurrentInfoTopBarInnerPart(
            minHeight = collapsedHeight,
            collapsedFraction = scrollBehavior.state.collapsedFraction,
            textStyles = MainTopBarDefaults.mainTopBarTextStyles(
                placeExpandedStyle = MaterialTheme.typography.labelLarge.copy(color = Color.White),
                placeCollapsedStyle = MaterialTheme.typography.labelLarge.copy(color = inverseSurface),
                titleExpandedStyle = MaterialTheme.typography.displaySmall.copy(color = Color.White),
                titleCollapsedStyle = MaterialTheme.typography.titleLarge.copy(color = surface),
                subTitleTextStyle = MaterialTheme.typography.displaySmall.copy(color = Color.White),
                indexExpandedStyle = MaterialTheme.typography.displayLarge
                    .copy(fontWeight = FontWeight.SemiBold, fontSize = 72.sp)
                    .copy(color = surface),
                indexCollapsedStyle = MaterialTheme.typography.titleLarge.copy(color = surface),
                peakHourStyle = MaterialTheme.typography.labelLarge.copy(color = surface),
            ),
            placeContent = placeContent,
            titleContent = {
                val style = LocalTextStyle.current
                if (state.currentSunData != null) {

                    AnimatedContent(
                        modifier = Modifier.padding(end = Dimens.grid_1),
                        targetState = state.viewMode,
                        label = "title"
                    ) { viewMode ->
                        ViewModeSwitcher(
                            mode = viewMode,
                            uv = {
                                UVTitle(
                                    uvCurrentData = state.uvCurrentData,
                                    sunPosition = state.currentSunData.position,
                                    onClick = onShowIndexInfo,
                                    style = style
                                )
                            },
                            weather = {
                                WeatherTitle(
                                    condition = state.weatherData?.realTime?.condition,
                                    sunPosition = state.currentSunData.position,
                                )
                            }
                        )
                    }
                }
            },
            subTitleContent = {
                Column(
                    horizontalAlignment = Alignment.End
                ) {
                    IconsInfo(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(
                                start = Dimens.grid_1,
                                end = Dimens.grid_1,
                                top = Dimens.grid_2
                            ),
                        currentIndexValue = state.uvCurrentData?.index,
                        onClick = onShowIndexInfo
                    )
                    MainSubTitle(
                        modifier = Modifier
                            .padding(
                                start = Dimens.grid_1,
                                end = Dimens.grid_1,
                                top = Dimens.grid_2
                            ),
                        viewMode = state.viewMode,
                        metricsMode = metricsMode,
                        uvCurrentIndex = currentIndex,
                        uvMaxIndex = maxIndex,
                        weatherData = state.weatherData,
                        timeToBurn = state.uvCurrentData?.timeToBurn,
                        timeToVitaminD = state.uvCurrentData?.timeToVitaminD,
                        currentSunData = state.currentSunData
                    )
//                    ViewModeSwitcher(
//                        mode = state.viewMode,
//                        uv = {
//                            if (state.weatherData != null && state.currentSunData != null) {
//                                WeatherSubTitle(
//                                    modifier = Modifier
//                                        .fillMaxWidth()
//                                        .padding(
//                                            start = Dimens.grid_1,
//                                            end = Dimens.grid_1,
//                                            top = Dimens.grid_2
//                                        ),
//                                    weatherData = state.weatherData,
//                                    sunPosition = state.currentSunData.position
//                                )
//                            }
//                        },
//                        weather = {
//                            if (state.weatherData != null && state.currentSunData != null) {
//                                UVIndexSubTitle(
//                                    modifier = Modifier
//                                        .fillMaxWidth()
//                                        .padding(
//                                            start = Dimens.grid_1,
//                                            end = Dimens.grid_2,
//                                            top = Dimens.grid_2
//                                        ),
//                                    currentIndex = currentIndex, maxIndex = maxIndex,
//                                )
//                            }
//                        }
//                    )
                }
            },
            indexContent = {
                ViewModeSwitcher(
                    mode = state.viewMode,
                    uv = {
                        UVBigIndex(
                            modifier = Modifier.padding(horizontal = Dimens.grid_2),
                            currentIndex = currentIndex, maxIndex = maxIndex
                        )
                    },
                    weather = {
                        if (state.weatherData?.realTime?.temperature != null) {
                            WeatherBigTemperature(
                                modifier = Modifier.padding(horizontal = Dimens.grid_2),
                                displayMode = metricsMode,
                                temperature = state.weatherData.realTime.temperature
                            )
                        }
                    }
                )
            },
            maxTimeContent = {
                ViewModeSwitcher(
                    modifier = Modifier.padding(start = 0.dp, end = Dimens.grid_2),
                    mode = state.viewMode,
                    uv = {
                        UVIndexPeakTime(maxTime = state.peakTime)
                    },
                    weather = {
                        if (state.weatherData?.realTime?.temperature != null) {
                            WeatherFeelsLike(
                                displayMode = metricsMode,
                                temperature = state.weatherData.realTime.temperature
                            )
                        }
                    }
                )

            }
        )
    }
}

@Composable
internal fun IconsInfo(
    modifier: Modifier = Modifier,
    currentIndexValue: Double?,
    onClick: () -> Unit
) {

    val currentIndexInt by remember(currentIndexValue) {
        derivedStateOf {
            UVLevel.valueOf(currentIndexValue?.roundToInt() ?: Int.MIN_VALUE) ?: UVLevel.Low
//            currentIndexValue?.roundToInt() ?: Int.MIN_VALUE
        }
    }

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.End,
        verticalAlignment = Alignment.CenterVertically
    ) {


        if (currentIndexInt > UVLevel.Low) {
            InfoIconButton(
                modifier = Modifier.size(48.dp),
                id = R.drawable.ic_glasses,
                onClick = onClick
            )
            Spacer(modifier = Modifier.width(8.dp))
        }

        if (currentIndexInt > UVLevel.Low) {
            InfoIconButton(
                modifier = Modifier.size(36.dp),
                id = R.drawable.ic_sunblock_alt,
                onClick = onClick
            )

            Spacer(modifier = Modifier.width(8.dp))

            InfoIconButton(
                modifier = Modifier.size(48.dp),
                id = R.drawable.ic_hat,
                onClick = onClick
            )

            Spacer(modifier = Modifier.width(8.dp))
        }

        if (currentIndexInt > UVLevel.Moderate) {

            InfoIconButton(
                modifier = Modifier.size(40.dp),
                id = R.drawable.ic_shirt,
                onClick = onClick
            )

            Spacer(modifier = Modifier.width(8.dp))

        }

        if (currentIndexInt > UVLevel.High) {
            InfoIconButton(
                modifier = Modifier.size(40.dp),
                id = R.drawable.beach_shadow,
                onClick = onClick
            )
        }
    }
}

@Composable
private fun InfoIconButton(
    modifier: Modifier = Modifier,
    @DrawableRes id: Int,
    onClick: () -> Unit
) {
    Icon(
        modifier = modifier
            .clickable(
                onClick = onClick,
                enabled = true,
                role = Role.Button,
                interactionSource = remember { MutableInteractionSource() },
                indication = rememberRipple(
                    bounded = false,
                    radius = 48.dp / 2,
                    color = MaterialTheme.colorScheme.inverseOnSurface
                )
            ),
        tint = Color.White,
        painter = painterResource(id = id),
        contentDescription = null
    )
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
private fun UVBigIndex(
    modifier: Modifier = Modifier,
    currentIndex: Int,
    maxIndex: Int
) {
    Row(
        modifier = modifier
    ) {
        AnimatedContent(
            targetState = currentIndex,
            transitionSpec = {
                if (targetState > initialState) {
                    slideInVertically { height -> height } + fadeIn() with
                            slideOutVertically { height -> -height } + fadeOut()
                } else {
                    slideInVertically { height -> -height } + fadeIn() with
                            slideOutVertically { height -> height } + fadeOut()
                }.using(
                    SizeTransform(clip = false)
                )
            },
            label = "bigIndex"
        ) { state ->
            Text(
                text = state.toString()
            )
        }
        Text(
            text = "/"
        )
        Crossfade(targetState = maxIndex, label = "maxIndex") {
            Text(
                text = it.toString()
            )
        }
    }
}

@Composable
private fun UVIndexPeakTime(
    modifier: Modifier = Modifier,
    maxTime: LocalTime?
) {

    val stringHour by remember(maxTime) {
        derivedStateOf {
            maxTime?.format(DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT))
        }
    }

    Crossfade(
        modifier = modifier,
        targetState = stringHour,
        label = "PeakTime"
    ) { state ->
        when (state) {
            null -> {}
            else -> {
                Column {
                    Text(text = stringResource(id = R.string.uvindex_peak_time))
                    Text(text = state)
                }
            }
        }
    }
}
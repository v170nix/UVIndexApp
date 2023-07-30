package uv.index.features.main.ui.composable.sections.dataview.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.Crossfade
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.with
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.flow.merge
import uv.index.R
import uv.index.features.astronomy.data.SunPosition
import uv.index.features.main.ui.MainContract
import uv.index.features.preferences.ui.rememberWeatherMetricsMode
import uv.index.features.uvi.ui.rememberTimeToBurnString
import uv.index.features.uvi.ui.rememberTimeToVitaminDString
import uv.index.features.weather.data.Weather
import uv.index.features.weather.domain.WeatherMetricsMode
import uv.index.features.weather.ui.rememberConditionIcon
import uv.index.features.weather.ui.rememberTemperatureText
import uv.index.features.weather.ui.rememberWindText
import uv.index.ui.theme.Dimens

@Composable
fun MainSubTitle(
    modifier: Modifier = Modifier,
    viewMode: MainContract.ViewMode,
    metricsMode: WeatherMetricsMode,
    uvCurrentIndex: Int,
    uvMaxIndex: Int,
    timeToBurn: MainContract.TimeToEvent?,
    timeToVitaminD: MainContract.TimeToEvent?,
    weatherData: Weather.Data? = null,
    currentSunData: MainContract.SunData? = null
) {
    ViewModeSwitcher(
        modifier = modifier,
        mode = viewMode,
        uv = {
            SubTitleSlots(
                icon = {
                    WeatherSubTitleIcon(
                        condition = weatherData?.realTime?.condition,
                        position = currentSunData?.position
                    )
                },
                title = {
                    WeatherSubTitleCaption(
                        metricsMode = metricsMode,
                        temperature = weatherData?.realTime?.temperature
                    )
                },
                subTitle = {
                    WeatherSubTitleUnderText(
                        metricsMode = metricsMode,
                        humidity = weatherData?.realTime?.humidity,
                        wind = weatherData?.realTime?.wind
                    )
                }
            )
        },
        weather = {
            SubTitleSlots(
                icon = {
                    UVIndexSubTitleIcon()
                },
                title = {
                    UVIndexSubTitleCaption(
                        currentIndex = uvCurrentIndex,
                        maxIndex = uvMaxIndex
                    )
                },
                subTitle = {
                    UVIndexSubTitleUnderText(
                        timeToBurn = timeToBurn,
                        timeToVitaminD = timeToVitaminD
                    )
                }
            )
        }
    )

}


@Composable
private fun SubTitleSlots(
    modifier: Modifier = Modifier,
    icon: @Composable () -> Unit,
    title: @Composable () -> Unit,
    subTitle: @Composable () -> Unit,
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Dimens.grid_1)
    ) {
        Column(
            verticalArrangement = Arrangement.Center
        ) {
            icon()
        }
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            title()
            Spacer(modifier = Modifier.height(Dimens.grid_0_5))
            subTitle()
        }
    }
}

@Composable
private fun WeatherSubTitleIcon(
    condition: Weather.Condition?,
    position: SunPosition?
) {
    val weatherId = rememberConditionIcon(condition, position)
    if (weatherId != null) {
        Icon(
            modifier = Modifier.size(
                with(LocalDensity.current) {
                    MaterialTheme.typography.displaySmall.fontSize.toDp() +
                            MaterialTheme.typography.titleSmall.fontSize.toDp() + 16.dp
                }
            ),
            imageVector = ImageVector.vectorResource(id = weatherId),
            contentDescription = ""
        )
    }
}

@Composable
private fun UVIndexSubTitleIcon() {
    Icon(
        modifier = Modifier.size(
            with(LocalDensity.current) {
                MaterialTheme.typography.displaySmall.fontSize.toDp() +
                        MaterialTheme.typography.titleSmall.fontSize.toDp() + 16.dp
            }
        ),
        imageVector = ImageVector.vectorResource(id = R.drawable.ic_shield),
        contentDescription = ""
    )
}

@Composable
private fun WeatherSubTitleCaption(
    metricsMode: WeatherMetricsMode?,
    temperature: Weather.Temperature?
) {
    if (metricsMode != null && temperature != null) {
        Text(
            text = rememberTemperatureText(metricsMode, temperature),
            textAlign = TextAlign.End,
        )
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
private fun UVIndexSubTitleCaption(
    currentIndex: Int,
    maxIndex: Int
) {
    Row {
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
            }
        ) { state ->
            Text(
                text = state.toString()
            )
        }
        Text(
            text = "/"
        )
        Crossfade(targetState = maxIndex) {
            Text(
                text = it.toString()
            )
        }
    }
}

@Composable
private fun WeatherSubTitleUnderText(
    metricsMode: WeatherMetricsMode,
    humidity: Weather.Humidity? = null,
    wind: Weather.Wind? = null
) {
    Row(
        horizontalArrangement = Arrangement.Center
    ) {

        val humidityId by remember(humidity) {
            derivedStateOf {
                when (humidity?.percentage) {
                    in (0..33) -> R.drawable.w_humidity_low
                    in (34..66) -> R.drawable.w_humidity_mid
                    in (67..100) -> R.drawable.w_humidity_high
                    else -> null
                }
            }
        }

        val innerHumidityId = humidityId
        if (innerHumidityId != null) {
            Icon(
                modifier = Modifier.size(
                    with(LocalDensity.current) {
                        MaterialTheme.typography.titleSmall.lineHeight.toDp()
                    }
                ),
                imageVector = ImageVector.vectorResource(id = innerHumidityId),
                contentDescription = ""
            )
        }

        if (humidity != null) {
            Text(
                modifier = Modifier,
                text = "${humidity.percentage}%",
                style = MaterialTheme.typography.titleSmall,
                textAlign = TextAlign.End
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        Icon(
            modifier = Modifier.size(
                with(LocalDensity.current) {
                    MaterialTheme.typography.titleSmall.lineHeight.toDp()
                }
            ),
            imageVector = ImageVector.vectorResource(id = R.drawable.w_wind),
            contentDescription = ""
        )
        if (wind != null) {
            Text(
                modifier = Modifier,
                text = rememberWindText(metricsMode, wind.speed),
                style = MaterialTheme.typography.titleSmall,
                textAlign = TextAlign.End
            )
        }
    }
}

@Composable
private fun UVIndexSubTitleUnderText(
    timeToBurn: MainContract.TimeToEvent?,
    timeToVitaminD: MainContract.TimeToEvent?
) {
    Row(
        horizontalArrangement = Arrangement.Center
    ) {

//
//        val innerHumidityId = null
//        if (innerHumidityId != null) {
//            Icon(
//                modifier = Modifier.size(
//                    with(LocalDensity.current) {
//                        MaterialTheme.typography.titleSmall.lineHeight.toDp()
//                    }
//                ),
//                imageVector = ImageVector.vectorResource(id = innerHumidityId),
//                contentDescription = ""
//            )
//        }

        Text(
            modifier = Modifier,
            text = rememberTimeToBurnString(timeToBurn),
            style = MaterialTheme.typography.titleSmall,
            textAlign = TextAlign.End
        )

        Spacer(modifier = Modifier.width(8.dp))

//        Icon(
//            modifier = Modifier.size(
//                with(LocalDensity.current) {
//                    MaterialTheme.typography.titleSmall.lineHeight.toDp()
//                }
//            ),
//            imageVector = ImageVector.vectorResource(id = R.drawable.w_wind),
//            contentDescription = ""
//        )
        Text(
            modifier = Modifier,
            text = rememberTimeToVitaminDString(timeToVitaminD),
            style = MaterialTheme.typography.titleSmall,
            textAlign = TextAlign.End
        )
    }
}
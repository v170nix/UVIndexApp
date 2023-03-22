package uv.index.features.weather.ui.composable

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import uv.index.R
import uv.index.features.preferences.ui.rememberWeatherDisplayMode
import uv.index.features.weather.data.Weather
import uv.index.features.weather.domain.WeatherDisplayMode
import uv.index.features.weather.domain.getValue
import uv.index.features.weather.ui.getBeaufortIndex
import uv.index.features.weather.ui.getBeaufortIndexColor
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

@Composable
fun WeatherWindHours(
    modifier: Modifier,
    data: Weather.Data,
) {
    val displayMode = rememberWeatherDisplayMode()

    val maxBeaufortIndex by remember(data.days[0]) {
        derivedStateOf {
            getBeaufortIndex(data.days[0].windSpeedMax)
        }
    }

    WeatherHours(
        modifier = modifier
    ) {
        items(data.days[0].hours) { item ->
            HourBox(
                displayMode = displayMode,
                maxBeaufortIndex = maxBeaufortIndex,
                hour = item.time,
                wind = item.wind,
            )
        }
    }
}

@Composable
private fun LazyItemScope.HourBox(
    displayMode: WeatherDisplayMode,
    maxBeaufortIndex: Int,
    hour: LocalTime,
    wind: Weather.Wind,
) {
    Box(
        modifier = Modifier.height(
            32.dp + 24.dp + 4.dp +
                    with(LocalDensity.current) {
                        MaterialTheme.typography.bodyMedium.lineHeight.toDp() +
                                MaterialTheme.typography.labelSmall.lineHeight.toDp()
                    }
        ),
    ) {

        Icon(
            modifier = Modifier
                .rotate(180f + wind.degree.toFloat())
                .align(Alignment.TopCenter),
            imageVector = ImageVector.vectorResource(id = R.drawable.ic_compass_arrow),
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onTertiary
        )

        val windSpeedString by remember(displayMode.wind, wind.speed) {
            derivedStateOf {
                displayMode.wind.getValue(wind.speed).toString()
            }
        }

        val currentDeltaBeaufortIndex by remember(maxBeaufortIndex, wind.speed) {
            derivedStateOf {
                val currentHourBeaufortIndex = getBeaufortIndex(wind.speed)
                var delta = currentHourBeaufortIndex.toFloat() / maxBeaufortIndex
                if (delta > 1f) delta = 1f
                if (delta <= 0f) delta = 0.1f
                delta
            }
        }

        Column(
            modifier = Modifier
//                    .fillMaxWidth(1f)
//                    .width(32.dp)
//                    .fillMaxHeight()
                .align(Alignment.BottomCenter),
            verticalArrangement = Arrangement.spacedBy(2.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = windSpeedString,
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(
                modifier = Modifier
                    .width(32.dp)
                    .height(32.dp * currentDeltaBeaufortIndex)
                    .background(
                        getBeaufortIndexColor(
                            getBeaufortIndex(wind.speed), Color.Transparent
                        )
                    )
            )
            HourTextPart(hour = hour, formatter = formatter)
        }
    }
}

private val formatter: DateTimeFormatter by lazy {
    DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT)
}

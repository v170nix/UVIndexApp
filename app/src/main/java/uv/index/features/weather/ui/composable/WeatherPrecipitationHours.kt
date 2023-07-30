package uv.index.features.weather.ui.composable

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import uv.index.R
import uv.index.features.preferences.ui.rememberWeatherMetricsMode
import uv.index.features.weather.data.Weather
import uv.index.features.weather.domain.WeatherMetricsMode
import uv.index.ui.theme.Dimens
import java.lang.Integer.max
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import kotlin.math.roundToInt

@Composable
fun WeatherPrecipitationHours(
    modifier: Modifier,
    data: Weather.Data,
) {
    val displayMode = rememberWeatherMetricsMode()


    WeatherHours(
        modifier = modifier
    ) {
        items(data.days[0].hours) { item ->
            HourBox(
                displayMode = displayMode,
                hour = item.time,
                humidity = item.humidity,
                precipitation = item.precipitation,
                chanceOf = item.chanceOf
            )
        }
    }
}

@Composable
private fun LazyItemScope.HourBox(
    displayMode: WeatherMetricsMode,
    hour: LocalTime,
    humidity: Weather.Humidity,
    precipitation: Weather.Precipitation,
    chanceOf: Weather.ChanceOf
) {
    Column(
        modifier = Modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(Dimens.grid_0_5)
    ) {

        val chance by remember(precipitation.millimeters, chanceOf) {
            derivedStateOf {
                if (precipitation.millimeters <= 0.0) return@derivedStateOf 0
                val snow = chanceOf.snow ?: 0
                val rain = chanceOf.rain ?: 0
                max(rain, snow).let { (it / 10.0).roundToInt() * 10 }
            }
        }

        val chanceString by remember(chance) {
            derivedStateOf {
                buildString {
                    append(chance)
                    append("%")
                }
            }
        }

        Text(
            modifier = Modifier,
            text = chanceString,
            style = MaterialTheme.typography.bodyMedium
        )

        val iconId by remember(precipitation.millimeters) {
            derivedStateOf {
                if (precipitation.millimeters <= 0)
                    return@derivedStateOf R.drawable.w_humidity_low
                if (precipitation.millimeters < 1)
                    return@derivedStateOf R.drawable.w_humidity_mid
                R.drawable.w_humidity_high
            }
        }

        Icon(
            modifier = Modifier.size(32.dp),
            imageVector = ImageVector.vectorResource(id = iconId),
            contentDescription = "",
            tint = MaterialTheme.colorScheme.onTertiary
        )

        Text(
            modifier = Modifier,
            text = stringResource(
                id = R.string.weather_precipitation_mm,
                precipitation.millimeters
            ),
            style = MaterialTheme.typography.bodyMedium
        )

        HourTextPart(
            formatter = formatter,
            hour = hour
        )
    }
}

private val formatter: DateTimeFormatter by lazy {
    DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT)
}
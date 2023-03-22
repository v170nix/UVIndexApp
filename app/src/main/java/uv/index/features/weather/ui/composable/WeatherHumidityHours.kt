package uv.index.features.weather.ui.composable

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import uv.index.features.preferences.ui.rememberWeatherDisplayMode
import uv.index.features.weather.data.Weather
import uv.index.features.weather.domain.WeatherDisplayMode
import uv.index.ui.theme.Dimens
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

@Composable
fun WeatherHumidityHours(
    modifier: Modifier,
    data: Weather.Data,
) {
    val displayMode = rememberWeatherDisplayMode()


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
    displayMode: WeatherDisplayMode,
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
//        Text(
//            modifier = Modifier,
//            text = chanceOf.snow.toString() + "%",
//            style = MaterialTheme.typography.bodyMedium
//        )
//        Text(
//            modifier = Modifier,
//            text = chanceOf.rain.toString() + "%",
//            style = MaterialTheme.typography.bodyMedium
//        )
        Text(
            modifier = Modifier,
            text = humidity.percentage.toString() + "%",
            style = MaterialTheme.typography.bodyMedium
        )

        Text(
            modifier = Modifier,
            text = precipitation.millimeters.toString() + "mm",
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
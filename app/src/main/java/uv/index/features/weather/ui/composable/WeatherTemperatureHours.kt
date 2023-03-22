package uv.index.features.weather.ui.composable

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import uv.index.features.astronomy.data.SunPosition
import uv.index.features.preferences.ui.rememberWeatherDisplayMode
import uv.index.features.weather.data.Weather
import uv.index.features.weather.domain.WeatherDisplayMode
import uv.index.features.weather.ui.rememberConditionIcon
import uv.index.features.weather.ui.rememberTemperatureText
import uv.index.ui.theme.Dimens
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

@Composable
fun WeatherTemperatureHours(
    modifier: Modifier = Modifier,
    data: Weather.Data
) {
    val displayMode = rememberWeatherDisplayMode()

    WeatherHours(
        modifier = modifier
    ) {
        items(data.days[0].hours) { item ->
            HourBox(
                displayMode = displayMode,
                hour = item.time,
                condition = item.condition,
                temperature = item.temperature,
                sunPosition = SunPosition.Above
            )
        }
    }
}

@Composable
private fun LazyItemScope.HourBox(
    displayMode: WeatherDisplayMode,
    hour: LocalTime,
    condition: Weather.Condition,
    temperature: Weather.Temperature,
    sunPosition: SunPosition
) {
    Column(
        modifier = Modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(Dimens.grid_0_5)
    ) {
        val weatherId = rememberConditionIcon(condition, sunPosition)
        Text(
            modifier = Modifier,
            text = rememberTemperatureText(
                displayMode, temperature
            ),
            style = MaterialTheme.typography.bodyMedium
        )

        if (weatherId != null) {
            Icon(
                imageVector = ImageVector.vectorResource(id = weatherId),
                contentDescription = "",
                tint = MaterialTheme.colorScheme.onTertiary
            )
        }
        HourTextPart(
            formatter = formatter,
            hour = hour
        )
    }
}

private val formatter: DateTimeFormatter by lazy {
    DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT)
}

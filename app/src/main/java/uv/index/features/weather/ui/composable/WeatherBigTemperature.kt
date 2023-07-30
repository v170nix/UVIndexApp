package uv.index.features.weather.ui.composable

import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import uv.index.features.preferences.ui.rememberWeatherMetricsMode
import uv.index.features.weather.data.Weather
import uv.index.features.weather.domain.WeatherMetricsMode
import uv.index.features.weather.ui.rememberTemperatureText

@Composable
fun WeatherBigTemperature(
    modifier: Modifier = Modifier,
    displayMode: WeatherMetricsMode = rememberWeatherMetricsMode(),
    temperature: Weather.Temperature
) {
    Row(
        modifier = modifier
    ) {
        Text(
            text = rememberTemperatureText(
                displayMode, temperature
            )
        )
    }

}
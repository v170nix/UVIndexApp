package uv.index.features.weather.ui.composable

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import uv.index.R
import uv.index.features.preferences.ui.rememberWeatherMetricsMode
import uv.index.features.weather.data.Weather
import uv.index.features.weather.domain.WeatherMetricsMode
import uv.index.features.weather.ui.rememberTemperatureText

@Composable
fun WeatherFeelsLike(
    modifier: Modifier = Modifier,
    displayMode: WeatherMetricsMode = rememberWeatherMetricsMode(),
    temperature: Weather.Temperature?
) {

    Crossfade(
        modifier = modifier,
        targetState = temperature
    ) { state ->
        when (state) {
            null -> {}
            else -> {
                Column {
                    Text(text = stringResource(id = R.string.weather_temperature_feels_like))
                    Text(text = rememberTemperatureText(displayMode, state, isFeelsLike = true)
                    )
                }
            }
        }
    }
}
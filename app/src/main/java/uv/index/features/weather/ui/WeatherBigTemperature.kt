package uv.index.features.weather.ui

import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import uv.index.features.weather.data.Weather

@Composable
fun WeatherBigTemperature(
    modifier: Modifier = Modifier,
    temperature: Weather.Temperature?
) {
    Row(
        modifier = modifier
    ) {
        Text(
            text = temperature?.value?.value.toString()
        )
    }

}
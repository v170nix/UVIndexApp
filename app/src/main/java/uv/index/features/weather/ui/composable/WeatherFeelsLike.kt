package uv.index.features.weather.ui.composable

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import uv.index.R
import uv.index.features.weather.data.Weather

@Composable
fun WeatherFeelsLike(
    modifier: Modifier = Modifier,
    temperature: Weather.Temperature?
) {


    val stringHour by remember(temperature) {
        derivedStateOf {
            temperature?.feelsLike?.value?.toString()
        }
    }

    Crossfade(
        modifier = modifier,
        targetState = stringHour
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
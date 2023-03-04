package uv.index.features.weather.ui

import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import uv.index.R
import uv.index.features.astronomy.data.SunPosition
import uv.index.features.preferences.ui.rememberWeatherDisplayMode
import uv.index.features.weather.data.Weather
import uv.index.features.weather.domain.WeatherDisplayMode.Temperature.Celsius
import uv.index.features.weather.domain.WeatherDisplayMode.Temperature.Fahrenheit
import uv.index.features.weather.domain.WeatherDisplayMode.Wind.*
import uv.index.features.weather.domain.getIconId
import uv.index.features.weather.domain.getValue
import uv.index.ui.theme.Dimens
import kotlin.math.roundToInt

@Composable
fun WeatherSubTitle(
    modifier: Modifier = Modifier,
    weatherData: Weather.Data,
    sunPosition: SunPosition
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.End,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically,
//                            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                verticalArrangement = Arrangement.Center
            ) {
                val weatherId by remember(
                    weatherData.realTime.condition,
                    sunPosition
                ) {
                    derivedStateOf {
                        weatherData.realTime.condition
                            .getIconId(sunPosition == SunPosition.Above)
                    }
                }

                if (weatherId != null) {
                    Icon(
                        modifier = Modifier.size(
                            with(LocalDensity.current) {
                                MaterialTheme.typography.displaySmall.fontSize.toDp() +
                                        MaterialTheme.typography.titleSmall.fontSize.toDp() + 16.dp
                            }
                        ),
                        imageVector = ImageVector.vectorResource(id = weatherId!!),
                        contentDescription = ""
                    )
                }
            }
            Spacer(
                modifier = Modifier.width(8.dp)
            )
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                val displayMode = rememberWeatherDisplayMode()
                val context = LocalContext.current

                val tempString by remember(context, displayMode.temperature) {
                    derivedStateOf {
                        val id = when (displayMode.temperature) {
                            Celsius -> R.string.uvindex_weather_temperature_celsius
                            Fahrenheit -> R.string.uvindex_weather_temperature_fahrenheit
                        }
                        val prefix =
                            if (weatherData.realTime.temperature.value.value > 0.0) "+" else ""

                        prefix + context.resources.getString(
                            id,
                            displayMode.temperature
                                .getValue(weatherData.realTime.temperature.value)
                                .roundToInt()
                        )
                    }
                }

                val windSpeedString by remember(context, displayMode.wind) {
                    derivedStateOf {
                        val id = when (displayMode.wind) {
                            KilometerPerHour -> R.string.uvindex_weather_speed_kph
                            MilePerHour -> R.string.uvindex_weather_speed_mph
                            MeterPerSeconds -> R.string.uvindex_weather_speed_mps
                        }
                        context.resources.getString(
                            id,
                            displayMode.wind
                                .getValue(weatherData.realTime.wind.speed)
                        )
                    }
                }

                Text(
                    modifier = Modifier,
                    text = tempString,
                    textAlign = TextAlign.End,
                )
                Spacer(modifier = Modifier.height(Dimens.grid_0_5))
                Row(
                    horizontalArrangement = Arrangement.Center
                ) {

                    val humidityId by remember(weatherData.realTime.humidity) {
                        derivedStateOf {
                            if (weatherData.realTime.humidity.percentage < 34)
                                return@derivedStateOf R.drawable.w_humidity_low
                            if (weatherData.realTime.humidity.percentage < 67)
                                return@derivedStateOf R.drawable.w_humidity_mid
                            R.drawable.w_humidity_high
                        }
                    }

                    Icon(
                        modifier = Modifier.size(
                            with(LocalDensity.current) {
                                MaterialTheme.typography.titleSmall.lineHeight.toDp()
                            }
                        ),
                        imageVector = ImageVector.vectorResource(id = humidityId),
                        contentDescription = ""
                    )
                    Text(
                        modifier = Modifier,
                        text = "${weatherData.realTime.humidity.percentage}%",
                        style = MaterialTheme.typography.titleSmall,
                        textAlign = TextAlign.End
                    )
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
                    Text(
                        modifier = Modifier,
                        text = windSpeedString,
                        style = MaterialTheme.typography.titleSmall,
                        textAlign = TextAlign.End
                    )
                }
            }
        }
    }
}
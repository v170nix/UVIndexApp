package uv.index.features.weather.ui

import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import uv.index.R
import uv.index.features.weather.data.SpeedKph
import uv.index.features.weather.data.Weather
import uv.index.features.weather.domain.WeatherDisplayMode
import uv.index.features.weather.domain.getValue
import uv.index.ui.theme.UVITheme
import kotlin.math.roundToInt

fun getBeaufortIndex(speedKph: SpeedKph): Int {
    return when (speedKph.value) {
        in 0.0..2.0 -> 0
        in 2.0..5.0 -> 1
        in 5.0..11.0 -> 2
        in 11.0..19.0 -> 3
        in 19.0..28.0 -> 4
        in 28.0..38.0 -> 5
        in 38.0..49.0 -> 6
        in 49.0..61.0 -> 7
        in 61.0..74.0 -> 8
        in 74.0..88.0 -> 9
        in 88.0..102.0 -> 10
        in 102.0..117.0 -> 11
        in 118.0..Double.MAX_VALUE -> 12
        else -> (-1)
    }

}


@Composable
@Stable
fun rememberWindText(
    displayMode: WeatherDisplayMode,
    speed: SpeedKph
): String {

    val context = LocalContext.current

    val windSpeedString by remember(context, displayMode.wind) {
        derivedStateOf {
            val id = when (displayMode.wind) {
                WeatherDisplayMode.Wind.KilometerPerHour -> R.string.uvindex_weather_speed_kph
                WeatherDisplayMode.Wind.MilePerHour -> R.string.uvindex_weather_speed_mph
                WeatherDisplayMode.Wind.MeterPerSeconds -> R.string.uvindex_weather_speed_mps
            }
            context.resources.getString(
                id,
                displayMode.wind
                    .getValue(speed)
                    .let {
                        if (displayMode.wind === WeatherDisplayMode.Wind.MeterPerSeconds)
                            it.toDouble()
                        else
                            it.toInt()
                    }
            )
        }
    }

    return windSpeedString
}

@Composable
@Stable
fun rememberWindGustText(
    displayMode: WeatherDisplayMode, wind: Weather.Wind
): String {

    val context = LocalContext.current

    val windSpeedString by remember(context, displayMode.wind) {
        derivedStateOf {
            val id = when (displayMode.wind) {
                WeatherDisplayMode.Wind.KilometerPerHour -> R.string.uvindex_weather_speed_kph
                WeatherDisplayMode.Wind.MilePerHour -> R.string.uvindex_weather_speed_mph
                WeatherDisplayMode.Wind.MeterPerSeconds -> R.string.uvindex_weather_speed_mps
            }
            context.resources.getString(id, displayMode.wind.getValue(wind.gust))
        }
    }

    return windSpeedString
}

// https://meyerweb.com/eric/tools/color-blend/#47C0AF:FFB74D:2:hex
// https://www.windfinder.com/#5/42.7470/103.2275
@Composable
@Stable
fun getBeaufortIndexColor(beaufortIndex: Int, transparentColor: Color): Color {
    return when (beaufortIndex) {
        0 -> UVITheme.colors.lowUV
        1 -> Color(0xFFAEF1F9)
        2 -> Color(0xFF96F7B4)
        3 -> Color(0xFF6FF46F)
        4 -> Color(0xFFA4ED12)
        5 -> Color(0xFFDAED12)
        6 -> UVITheme.colors.moderateUV
        7 -> Color(0xFFFF9448)
        8 -> UVITheme.colors.highUV
        9 -> Color(0xFFFA5A3D)
        10 -> UVITheme.colors.veryHighUV
        11 -> UVITheme.colors.extremeUV
        12 -> Color.Magenta
        else -> transparentColor
    }
}

//@Composable
//@Stable
//fun getBeaufortIndexColor(beaufortIndex: Int, transparentColor: Color): Color {
//    return when (beaufortIndex) {
//        0 -> UVITheme.colors.lowUV
//        1 -> Color(0xFFAEF1F9)
//        2 -> Color(0xFF96F7DC)
//        3 -> Color(0xFF96F7B4)
//        4 -> Color(0xFF6FF46F)
//        5 -> Color(0xFF73ED12)
//        6 -> Color(0xFFA4ED12)
//        7 -> Color(0xFFDAED12)
//        8 -> UVITheme.colors.moderateUV
//        9 -> UVITheme.colors.highUV
//        10 -> UVITheme.colors.veryHighUV
//        11 -> UVITheme.colors.extremeUV
//        12 -> Color.Magenta
//        else -> transparentColor
//    }
//}

@Composable
@Stable
fun rememberTemperatureText(
    displayMode: WeatherDisplayMode, temperature: Weather.Temperature
): String {

    val context = LocalContext.current

    val tempString by remember(context, displayMode.temperature) {
        derivedStateOf {
            val id = when (displayMode.temperature) {
                WeatherDisplayMode.Temperature.Celsius -> R.string.uvindex_weather_temperature_celsius
                WeatherDisplayMode.Temperature.Fahrenheit -> R.string.uvindex_weather_temperature_fahrenheit
            }
            val prefix = if (temperature.value.value > 0.0) "+" else ""

            prefix + context.resources.getString(
                id, displayMode.temperature.getValue(temperature.value).roundToInt()
            )
        }
    }

    return tempString
}

@Composable
@Stable
fun rememberBeaufortText(
    speed: SpeedKph
): String {
    val context = LocalContext.current
    val beaufortIndex by remember(speed) {
        derivedStateOf {
            getBeaufortIndex(speed)
        }
    }
    val beaufortIndexString by remember(beaufortIndex) {
        derivedStateOf {
            runCatching {
                context.resources.getStringArray(R.array.weather_wind_beaufort_scale_text)[beaufortIndex]
            }.getOrDefault("")
        }
    }
    return beaufortIndexString
}

@Composable
@Stable
fun rememberPointOfCompassText(
    degree: Int
): String {
    val context = LocalContext.current
    val rhumb by remember(degree) {
        derivedStateOf {
            val azimth = degree.toDouble().mod(360.0)
            (azimth / 45.0 - 0.25).roundToInt()
        }
    }
    val rhumbString by remember(rhumb) {
        derivedStateOf {
            runCatching {
                context.resources.getStringArray(R.array.weather_azimuth_text)[rhumb]
            }.getOrDefault("")
        }
    }
    return rhumbString
}




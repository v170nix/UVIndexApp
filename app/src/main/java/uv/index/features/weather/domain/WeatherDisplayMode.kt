package uv.index.features.weather.domain

import androidx.compose.runtime.Stable
import uv.index.features.weather.data.Celsius
import uv.index.features.weather.data.SpeedKph
import uv.index.features.weather.data.Weather

@Stable
data class WeatherDisplayMode(
    val temperature: Temperature,
    val pressure: Pressure,
    val wind: Wind
) {

    val length: Length
        get() {
            return when (wind) {
                Wind.KilometerPerHour -> Length.Kilometer
                Wind.MilePerHour -> Length.Mile
                Wind.MeterPerSeconds -> Length.Meter
            }
        }

    enum class Temperature { Celsius, Fahrenheit }
    enum class Pressure { Millibars, Inches, Millimeter }
    enum class Wind { KilometerPerHour, MilePerHour, MeterPerSeconds }
    enum class Length { Kilometer, Mile, Meter }
}

fun WeatherDisplayMode.Temperature.getValue(celsius: Celsius): Double {
    if (this == WeatherDisplayMode.Temperature.Celsius) return celsius.value
    return 9.0 / 5.0 * celsius.value + 32.0
}

fun WeatherDisplayMode.Pressure.getValue(pressure: Weather.Pressure): Double {
    return when (this) {
        WeatherDisplayMode.Pressure.Millibars -> pressure.millibars.toDouble()
        WeatherDisplayMode.Pressure.Inches -> pressure.millibars / 33.86389
        WeatherDisplayMode.Pressure.Millimeter -> pressure.millibars * 0.750063755419211
    }
}

fun WeatherDisplayMode.Wind.getValue(kph: SpeedKph): Double {
    return when (this) {
        WeatherDisplayMode.Wind.KilometerPerHour -> kph.value
        WeatherDisplayMode.Wind.MilePerHour -> kph.value / 1.609
        WeatherDisplayMode.Wind.MeterPerSeconds -> kph.value / 3.6
    }
}

fun WeatherDisplayMode.Length.getValue(km: Double): Double {
    return when (this) {
        WeatherDisplayMode.Length.Kilometer -> km
        WeatherDisplayMode.Length.Mile -> km / 1.609344
        WeatherDisplayMode.Length.Meter -> km * 1000.0
    }
}


package uv.index.features.weather.domain

import androidx.compose.runtime.Stable
import uv.index.features.weather.data.Celsius
import uv.index.features.weather.data.SpeedKph
import uv.index.features.weather.data.Weather
import kotlin.math.roundToInt

@Stable
data class WeatherMetricsMode(
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

fun WeatherMetricsMode.Temperature.getValue(celsius: Celsius): Double {
    if (this == WeatherMetricsMode.Temperature.Celsius) return celsius.value
    return 9.0 / 5.0 * celsius.value + 32.0
}

fun WeatherMetricsMode.Pressure.getValue(pressure: Weather.Pressure): Double {
    return when (this) {
        WeatherMetricsMode.Pressure.Millibars -> pressure.millibars.toDouble()
        WeatherMetricsMode.Pressure.Inches -> pressure.millibars / 33.86389
        WeatherMetricsMode.Pressure.Millimeter -> pressure.millibars * 0.750063755419211
    }
}

fun WeatherMetricsMode.Wind.getValue(kph: SpeedKph): Number {
    return when (this) {
        WeatherMetricsMode.Wind.KilometerPerHour -> (kph.value).roundToInt()
        WeatherMetricsMode.Wind.MilePerHour -> (kph.value / 1.609).roundToInt()
        WeatherMetricsMode.Wind.MeterPerSeconds -> (kph.value / 3.6)
    }
}

fun WeatherMetricsMode.Length.getValue(km: Double): Double {
    return when (this) {
        WeatherMetricsMode.Length.Kilometer -> km
        WeatherMetricsMode.Length.Mile -> km / 1.609344
        WeatherMetricsMode.Length.Meter -> km * 1000.0
    }
}


package uv.index.features.weather.data

import androidx.compose.runtime.Stable
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZonedDateTime

@Stable
interface Weather {
    data class Wind(val speed: SpeedKph, val degree: Int, val gust: SpeedKph)
    data class Temperature(val value: Celsius, val feelsLike: Celsius)

    @JvmInline
    value class Condition(val code: Int)

    @JvmInline
    value class Pressure(val millibars: Int) {
        companion object {
            val Unspecified = Pressure(millibars = Int.MAX_VALUE)
        }
    }

    @JvmInline
    value class Precipitation(val millimeters: Double) {
        companion object {
            val Unspecified = Precipitation(millimeters = Double.NaN)
        }
    }

    @JvmInline
    value class Humidity(val percentage: Int) {
        companion object {
            val Unspecified = Humidity(percentage = Int.MAX_VALUE)
        }
    }

    @JvmInline
    value class RangeOfVisibility(val km: Double) {
        companion object {
            val Unspecified = RangeOfVisibility(km = Double.NaN)
        }
    }

    @JvmInline
    value class Cloud(val percentage: Int) {
        companion object {
            val Unspecified = Cloud(percentage = Int.MAX_VALUE)
        }
    }

    data class ChanceOf(
        val rain: Int?,
        val snow: Int?,
    )

    data class Data(
        val latitude: Double,
        val longitude: Double,
        val realTime: RealTime,
        val days: List<Day>
    )

    data class RealTime(
        val date: ZonedDateTime,
        val temperature: Temperature,
        val condition: Condition,
        val wind: Wind,
        val pressure: Pressure,
        val precipitation: Precipitation,
        val humidity: Humidity,
        val cloud: Cloud,
        val uv: Double? = null,
        val visible: RangeOfVisibility? = null,
        val airQuality: AirQuality? = null
    )

    data class AirQuality(
        val co: MGPerM3,
        val no2: MGPerM3,
        val o3: MGPerM3,
        val so2: MGPerM3,
        val pm2: MGPerM3,
        val pm10: MGPerM3,
        val usEPAIndex: Int?,
        val gbDEFRAIndex: Int?
    )

    data class Day(
        val date: LocalDate,
        val condition: Condition,
        val temperatureMax: Celsius,
        val temperatureMin: Celsius,
        val temperatureAvg: Celsius,
        val windSpeedMax: SpeedKph,
        val precipitationTotal: Precipitation,
        val snowTotalInCm: Double? = null,
        val visibleAvg: RangeOfVisibility,
        val humidityAvg: Humidity,
        val uv: Double? = null,
        val willItRain: Int? = null,
        val chanceOfRain: Int? = null,
        val willItSnow: Int? = null,
        val chanceOfSnow: Int? = null,
        val hours: List<Hour> = listOf(),
    )

    data class Hour(
        val time: LocalTime,
        val temperature: Temperature,
        val condition: Condition,
        val wind: Wind,
        val pressure: Pressure,
        val precipitation: Precipitation,
        val humidity: Humidity,
        val cloud: Cloud? = null,
        val uv: Double? = null,
        val visible: RangeOfVisibility? = null,
        val windchill: Double? = null,
        val heatIndex: Double? = null,
        val dewPoint: Double? = null,
        val willItRain: Int? = null,
        val chanceOf: ChanceOf,
        val willItSnow: Int? = null,
    )

}

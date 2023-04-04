package uv.index.features.weather.data

import androidx.annotation.Keep
import androidx.compose.runtime.Stable
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZonedDateTime

@Stable
interface Weather {

    @Keep
    @Serializable
    data class Wind(val speed: SpeedKph, val degree: Int, val gust: SpeedKph)

    @Keep
    @Serializable
    data class Temperature(val value: Celsius, val feelsLike: Celsius)

    @JvmInline
    @Keep
    @Serializable
    value class Condition(val code: Int)

    @JvmInline
    @Keep
    @Serializable
    value class Pressure(val millibars: Int) {
        companion object {
            val Unspecified = Pressure(millibars = Int.MAX_VALUE)
        }
    }

    @JvmInline
    @Keep
    @Serializable
    value class Precipitation(val millimeters: Double) {
        companion object {
            val Unspecified = Precipitation(millimeters = Double.NaN)
        }
    }

    @JvmInline
    @Keep
    @Serializable
    value class Humidity(val percentage: Int) {
        companion object {
            val Unspecified = Humidity(percentage = Int.MAX_VALUE)
        }
    }

    @JvmInline
    @Keep
    @Serializable
    value class RangeOfVisibility(val km: Double) {
        companion object {
            val Unspecified = RangeOfVisibility(km = Double.NaN)
        }
    }

    @JvmInline
    @Keep
    @Serializable
    value class Cloud(val percentage: Int) {
        companion object {
            val Unspecified = Cloud(percentage = Int.MAX_VALUE)
        }
    }

    @Keep
    @Serializable
    data class ChanceOf(
        val rain: Int?,
        val snow: Int?,
    )

    @Keep
    @Serializable
    data class Data(
        val latitude: Double,
        val longitude: Double,
        val realTime: RealTime,
        val days: List<Day>
    )

    @Keep
    @Serializable
    data class RealTime(
        @Serializable(ZonedDateTimeSerializer::class)
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

    @Keep
    @Serializable
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

    @Keep
    @Serializable
    data class Day(
        @Serializable(LocalDateSerializer::class)
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

    @Keep
    @Serializable
    data class Hour(
        @Serializable(LocalTimeSerializer::class)
        val time: LocalTime,
        val isDay: Boolean,
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

object ZonedDateTimeSerializer : KSerializer<ZonedDateTime> {
    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("ZonedDateTime", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: ZonedDateTime) {
        encoder.encodeString(value.toString())
    }

    override fun deserialize(decoder: Decoder): ZonedDateTime {
        val string = decoder.decodeString()
        return ZonedDateTime.parse(string)
    }
}

object LocalTimeSerializer : KSerializer<LocalTime> {
    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("LocalTime", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: LocalTime) {
        encoder.encodeString(value.toString())
    }

    override fun deserialize(decoder: Decoder): LocalTime {
        val string = decoder.decodeString()
        return LocalTime.parse(string)
    }
}

object LocalDateSerializer : KSerializer<LocalDate> {
    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("LocalDate", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: LocalDate) {
        encoder.encodeString(value.toString())
    }

    override fun deserialize(decoder: Decoder): LocalDate {
        val string = decoder.decodeString()
        return LocalDate.parse(string)
    }
}

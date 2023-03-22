package uv.index.features.weather.data

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import io.ktor.client.request.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonNames
import uv.index.lib.net.CertOkHttpClient
import java.time.*

class WeatherApi(
    private val client: CertOkHttpClient,
    private val url: String,
) {

    suspend fun get(latitude: Double, longitude: Double): Weather.Data? {
        return client.get<Result>(url) {
            parameter("lat", latitude)
            parameter("lon", longitude)
        }.data?.let {
            Weather.Data(
                it.location.lat,
                it.location.lon,
                realTime = it.createRealTime(),
                days = it.forecast.map { forecast -> forecast.toWeatherDay() }
            )
        }
    }

    @Keep
    @Serializable
    data class Result(
        val status: String = "",
        val data: Data? = null
    )

    @Keep
    @Serializable
    data class Data(
        val location: Location,
        val current: Current,
        val forecast: List<Forecast> = listOf()
    )

    private fun Data.createRealTime(): Weather.RealTime {
        return Weather.RealTime(
            date = Instant.ofEpochMilli(current.time).atZone(ZoneId.of("UTC")),
            temperature = Weather.Temperature(
                value = current.temperature.celsius,
                feelsLike = current.fellsLike?.celsius ?: Celsius.Unspecified
            ),
            condition = Weather.Condition(current.conditionCode),
            wind = Weather.Wind(
                speed = current.windSpeed.kph,
                degree = current.windDegree,
                gust = current.windGust?.kph ?: SpeedKph.Unspecified
            ),
            pressure = current.pressure?.let(Weather::Pressure) ?: Weather.Pressure.Unspecified,
            precipitation = current.precipitation?.let(Weather::Precipitation)
                ?: Weather.Precipitation.Unspecified,
            humidity = current.humidity?.let(Weather::Humidity) ?: Weather.Humidity.Unspecified,
            cloud = current.cloud?.let(Weather::Cloud) ?: Weather.Cloud.Unspecified,
            uv = current.uv,
            visible = current.visible?.toDouble()?.let(Weather::RangeOfVisibility)
                ?: Weather.RangeOfVisibility.Unspecified,
            airQuality = current.air?.createAirQuality()
        )
    }


    @Keep
    @Serializable
    data class Location(
        val name: String? = null,
        val region: String? = null,
        val country: String? = null,
        val lat: Double,
        val lon: Double,
        @JsonNames("tz_id") val tzId: String? = null,
        @JsonNames("localtime_epoch") val localtimeEpoch: Long? = null,
    )

    @Keep
    @Serializable
    data class Current(
        val time: Long,
        val temperature: Double,
        val conditionCode: Int,
        val windSpeed: Double,
        val windDegree: Int,
        val windGust: Double?,
        val fellsLike: Double? = null,
        val pressure: Int? = null,
        val precipitation: Double? = null,
        val humidity: Int? = null,
        val cloud: Int? = null,
        val uv: Double? = null,
        val visible: Double? = null,
        val air: Air? = Air()
    )

    @Keep
    @Serializable
    data class Air(
        val co: Double? = null,
        val no2: Double? = null,
        val o3: Double? = null,
        val so2: Double? = null,
        @JsonNames("pm2_5") val pm25: Double? = null,
        val pm10: Double? = null,
        @JsonNames("usEpaIndex") val usEPAIndex: Int? = null,
        @JsonNames("gbDefraIndex") val gbDEFRAIndex: Int? = null
    )

    private fun Air.createAirQuality(): Weather.AirQuality {
        return Weather.AirQuality(
            co = co?.mgPm3 ?: MGPerM3.Unspecified,
            no2 = no2?.mgPm3 ?: MGPerM3.Unspecified,
            o3 = o3?.mgPm3 ?: MGPerM3.Unspecified,
            so2 = so2?.mgPm3 ?: MGPerM3.Unspecified,
            pm2 = pm25?.mgPm3 ?: MGPerM3.Unspecified,
            pm10 = pm10?.mgPm3 ?: MGPerM3.Unspecified,
            usEPAIndex = usEPAIndex,
            gbDEFRAIndex = gbDEFRAIndex
        )
    }

    @Keep
    @Serializable
    data class Forecast(
        @SerializedName("date") val date: Long,
        @SerializedName("conditionCode") val conditionCode: Int,
        @SerializedName("temperatureMax") val temperatureMax: Double? = null,
        @SerializedName("temperatureMin") val temperatureMin: Double? = null,
        @SerializedName("temperatureAvg") val temperatureAvg: Double? = null,
        @SerializedName("windSpeedMax") val windSpeedMax: Double? = null,
        @SerializedName("hours") val hours: List<Hour> = listOf(),
        @SerializedName("precipitationTotal") val precipitationTotal: Double? = null,
        @SerializedName("snowTotal") val snowTotal: Double? = null,
        @SerializedName("visibleAvg") val visibleAvg: Double? = null,
        @SerializedName("humidityAvg") val humidityAvg: Int? = null,
        @SerializedName("uv") val uv: Double? = null,
        @SerializedName("willItRain") val willItRain: Int? = null,
        @SerializedName("chanceOfRain") val chanceOfRain: Int? = null,
        @SerializedName("willItSnow") val willItSnow: Int? = null,
        @SerializedName("chanceOfSnow") val chanceOfSnow: Int? = null
    )

    private fun Forecast.toWeatherDay(): Weather.Day {
        val date = Instant.ofEpochMilli(this.date).atZone(ZoneId.of("UTC")).toLocalDate()
        return Weather.Day(
            date = date,
            condition = Weather.Condition(this.conditionCode),
            uv = uv,
            temperatureMax = temperatureMax?.celsius ?: Celsius.Unspecified,
            temperatureMin = temperatureMin?.celsius ?: Celsius.Unspecified,
            temperatureAvg = temperatureAvg?.celsius ?: Celsius.Unspecified,
            windSpeedMax = windSpeedMax?.kph ?: SpeedKph.Unspecified,
            precipitationTotal = precipitationTotal?.let(Weather::Precipitation) ?: Weather.Precipitation.Unspecified,
            snowTotalInCm = snowTotal,
            visibleAvg = visibleAvg?.let(Weather::RangeOfVisibility) ?: Weather.RangeOfVisibility.Unspecified,
            humidityAvg = humidityAvg?.let(Weather::Humidity) ?: Weather.Humidity.Unspecified,
            willItRain = willItRain,
            chanceOfRain = chanceOfRain,
            willItSnow = willItSnow,
            chanceOfSnow = chanceOfSnow,
            hours = hours.mapIndexed { index, hour -> hour.toWeatherHour(date, index.toLong()) }
        )
    }

    @Keep
    @Serializable
    data class Hour(
        @SerializedName("time") val time: Long,
        @SerializedName("conditionCode") val conditionCode: Int,
        @SerializedName("temperature") val temperature: Double,
        @SerializedName("windSpeed") val windSpeed: Double,
        @SerializedName("windDegree") val windDegree: Int,
        @SerializedName("windGust") val windGust: Double? = null,
        @SerializedName("fellsLike") val fellsLike: Double? = null,
        @SerializedName("pressure") val pressure: Int? = null,
        @SerializedName("precipitation") val precipitation: Double? = null,
        @SerializedName("humidity") val humidity: Int? = null,
        @SerializedName("cloud") val cloud: Int? = null,
        @SerializedName("uv") val uv: Double? = null,
        @SerializedName("visible") val visible: Int? = null,
        @SerializedName("windchill") val windchill: Double? = null,
        @SerializedName("heatIndex") val heatIndex: Double? = null,
        @SerializedName("dewPoint") val dewPoint: Double? = null,
        @SerializedName("willItRain") val willItRain: Int? = null,
        @SerializedName("chanceOfRain") val chanceOfRain: Int? = null,
        @SerializedName("willItSnow") val willItSnow: Int? = null,
        @SerializedName("chanceOfSnow") val chanceOfSnow: Int? = null
    )

    private fun Hour.toWeatherHour(dateAsStartDay: LocalDate, hour: Long): Weather.Hour {
        return Weather.Hour(
            time = dateAsStartDay.atStartOfDay().plusHours(hour).toLocalTime(),
            temperature = Weather.Temperature(
                value = temperature.celsius,
                fellsLike?.celsius ?: Celsius.Unspecified
            ),
            condition = Weather.Condition(this.conditionCode),
            wind = Weather.Wind(
                speed = windSpeed.kph,
                degree = windDegree,
                gust = windGust?.kph ?: SpeedKph.Unspecified
            ),
            pressure = pressure?.let(Weather::Pressure) ?: Weather.Pressure.Unspecified,
            precipitation = precipitation?.let(Weather::Precipitation)
                ?: Weather.Precipitation.Unspecified,
            humidity = humidity?.let(Weather::Humidity) ?: Weather.Humidity.Unspecified,
            cloud = cloud?.let(Weather::Cloud) ?: Weather.Cloud.Unspecified,
            uv = uv,
            visible = visible?.toDouble()?.let(Weather::RangeOfVisibility)
                ?: Weather.RangeOfVisibility.Unspecified,
            windchill = windchill,
            heatIndex = heatIndex,
            dewPoint = dewPoint,
            willItRain = willItRain,
            chanceOf = Weather.ChanceOf(chanceOfRain, chanceOfSnow),
            willItSnow = willItSnow
        )
    }


}
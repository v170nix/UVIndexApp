package uv.index.features.place.data.gtimezone

import com.google.android.gms.maps.model.LatLng
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.okhttp.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import uv.index.di.TimeZoneKey
import java.time.ZoneId
import java.time.ZonedDateTime
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TimeZoneGoogleRepository @Inject constructor(
    @TimeZoneKey private val key: String
) {

    @Throws
    suspend fun getZoneId(latLng: LatLng): ZoneId = withContext(Dispatchers.IO) {
        val data = autoGetTimeZoneResult(latLng)
        data.toZoneId() ?: throw IllegalArgumentException(data.id)
    }

    @Throws(TimeZoneGoogleResponseException::class)
    private suspend fun autoGetTimeZoneResult(
        latLng: LatLng,
        @Suppress("UNUSED_PARAMETER") language: String = "en"
    ): TimeZoneGoogleData {
        return HttpClient(OkHttp) {
            install(ContentNegotiation) {
                json(json)
            }
            defaultRequest {
                contentType(ContentType.Application.Json)
                accept(ContentType.Application.Json)
            }
        }.use { client ->
            client.get("https://maps.googleapis.com/maps/api/timezone/json") {
                parameter("key", key)
                parameter("location", "${latLng.latitude},${latLng.longitude}")
                parameter("timestamp", ZonedDateTime.now().toEpochSecond())
            }.body()
        }
    }
}

private val json = Json {
    ignoreUnknownKeys = true
    isLenient = true
    encodeDefaults = false
}
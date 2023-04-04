package uv.index.features.weather.data.repository

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import uv.index.features.weather.data.Weather
import kotlin.math.roundToInt

@Dao
interface WeatherCacheDao {

    @Query("SELECT * FROM weather_cache WHERE latitude == :latitude AND longitude == :longitude")
    suspend fun getLonLatData(
        longitude: Int,
        latitude: Int,
    ): WeatherCacheData?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(data: WeatherCacheData)

    suspend fun getLonLatData(
        request: WeatherRequest.Location
    ): Weather.Data? {
        val dbData = getLonLatData(
            (request.latLng.longitude * 100).roundToInt(),
            (request.latLng.latitude * 100).roundToInt()
        ) ?: return null
        return withContext(Dispatchers.IO) {
            Json.decodeFromString<Weather.Data>(dbData.value)
        }
    }

    suspend fun insert(
        request: WeatherRequest.Location,
        data: Weather.Data
    ) {
        val dbData = withContext(Dispatchers.IO) {
            val json = Json.encodeToString(data)
            WeatherCacheData(
                (request.latLng.longitude * 100).roundToInt(),
                (request.latLng.latitude * 100).roundToInt(),
                json
            )
        }
        insert(dbData)
    }


//    @Query("DELETE FROM uv_index WHERE time <= :time")
//    suspend fun deleteObsoleteData(time: Long = Instant.now().epochSecond - 86_000 * 2)
}
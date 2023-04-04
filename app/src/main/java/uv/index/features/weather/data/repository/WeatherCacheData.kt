package uv.index.features.weather.data.repository

import androidx.room.Entity

@Entity(tableName = "weather_cache", primaryKeys = ["longitude", "latitude"])
data class WeatherCacheData(
    val longitude: Int,
    val latitude: Int,
    val value: String
)
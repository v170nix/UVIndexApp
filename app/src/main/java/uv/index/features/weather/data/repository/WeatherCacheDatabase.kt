package uv.index.features.weather.data.repository

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [
        WeatherCacheData::class
    ],
    version = 1,
    exportSchema = false
)
abstract class WeatherCacheDatabase: RoomDatabase() {
    abstract fun getDao(): WeatherCacheDao
}
package uv.index.parts.place.data.room

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [PlaceData::class],
    version = 10,
    exportSchema = false
)
abstract class PlaceDatabase : RoomDatabase() {
    abstract fun getPlaceDao(): PlaceDao
}
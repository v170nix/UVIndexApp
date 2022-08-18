package uv.index.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import uv.index.features.place.data.room.PlaceDao
import uv.index.features.place.data.room.PlaceDatabase

@Module
@InstallIn(SingletonComponent::class)
object PlaceModule {

    @Provides
    fun provideDb(@ApplicationContext context: Context): PlaceDatabase {
        return Room.databaseBuilder(
            context,
            PlaceDatabase::class.java, "place-db"
        ).fallbackToDestructiveMigration().build()
    }

    @Provides
    fun provideIndexDao(db: PlaceDatabase): PlaceDao {
        return db.getPlaceDao()
    }

}
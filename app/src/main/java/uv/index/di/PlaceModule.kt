package uv.index.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import uv.index.BuildConfig
import uv.index.features.place.data.room.PlaceDao
import uv.index.features.place.data.room.PlaceDatabase
import javax.inject.Qualifier
import javax.inject.Singleton

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class PlaceKey

@Module
@InstallIn(SingletonComponent::class)
object PlaceModule {

    @Provides
    @Singleton
    fun provideDb(@ApplicationContext context: Context): PlaceDatabase {
        return Room.databaseBuilder(
            context,
            PlaceDatabase::class.java, "place-db"
        ).fallbackToDestructiveMigration().build()
    }

    @Provides
    @Singleton
    fun provideDao(db: PlaceDatabase): PlaceDao {
        return db.getPlaceDao()
    }

    @PlaceKey
    @Provides
    @Singleton
    fun providePlaceKey(): String {
        return BuildConfig.PLACE_API_KEY
    }


}
package uv.index.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import uv.index.features.main.domain.WeatherUseCase
import uv.index.features.main.domain.WeatherUseCaseFactory
import uv.index.features.weather.data.WeatherApi
import uv.index.features.weather.data.repository.WeatherCacheDao
import uv.index.features.weather.data.repository.WeatherCacheDatabase
import uv.index.features.weather.data.repository.WeatherLocalRepository
import uv.index.features.weather.data.repository.WeatherRemoteRepository
import uv.index.lib.net.CertOkHttpClient
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object WeatherModule {

    @Provides
    @Singleton
    fun provideDb(@ApplicationContext context: Context): WeatherCacheDatabase {
        return Room.databaseBuilder(
            context,
            WeatherCacheDatabase::class.java, "weather-db"
        ).fallbackToDestructiveMigration().build()
    }

    @Provides
    @Singleton
    fun provideIndexDao(db: WeatherCacheDatabase): WeatherCacheDao {
        return db.getDao()
    }

    @Provides
    @Singleton
    fun provideWeatherApi(client: CertOkHttpClient): WeatherApi {
        return WeatherApi(
            client = client,
            url = "https://functions.yandexcloud.net/d4e1sk0rde800m1u4v1j",
        )
    }

    @Provides
    fun provideWeatherUseCaseFactory(
        localRepository: WeatherLocalRepository,
        remoteRepository: WeatherRemoteRepository
    ): WeatherUseCaseFactory =
        WeatherUseCaseFactory { scope, remoteCheckDeltaInMins, remoteRetryCount ->
            WeatherUseCase(
                scope,
                localRepository, remoteRepository, remoteCheckDeltaInMins, remoteRetryCount
            )
        }

}
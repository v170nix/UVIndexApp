package uv.index.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import uv.index.features.weather.data.WeatherApi
import uv.index.lib.net.CertOkHttpClient
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object WeatherModule {

    @Provides
    @Singleton
    fun provideWeatherApi(client: CertOkHttpClient): WeatherApi {
        return WeatherApi(
            client = client,
            url = "https://functions.yandexcloud.net/d4e1sk0rde800m1u4v1j",
        )
    }

}
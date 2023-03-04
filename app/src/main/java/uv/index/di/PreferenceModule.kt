package uv.index.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import uv.index.features.preferences.data.ThemePreferences
import uv.index.features.preferences.data.WeatherDisplayPreferences
import uv.index.features.preferences.domain.ThemePreferencesImplDataStore
import uv.index.features.preferences.domain.WeatherDisplayDataImplStore
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface PreferenceModule {

    @Binds
    @Singleton
    fun bindThemePreferences(themePreferences: ThemePreferencesImplDataStore): ThemePreferences

    @Binds
    @Singleton
    fun bindWeatherDisplayPreference(weatherPreference: WeatherDisplayDataImplStore): WeatherDisplayPreferences

}
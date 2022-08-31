package uv.index.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import uv.index.features.preferences.data.ThemePreferences
import uv.index.features.preferences.data.ThemePreferencesImplDataStore
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface PreferenceModule {


    @Binds
    @Singleton
    fun bindUserPreferences(themePreferences: ThemePreferencesImplDataStore): ThemePreferences

}
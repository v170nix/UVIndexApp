package uv.index.features.preferences.data

import kotlinx.coroutines.flow.Flow

interface ThemePreferences {
    val modeAsStateFlow: Flow<ThemeMode>
    fun getMode(): ThemeMode
    suspend fun updateMode(mode: ThemeMode)
}
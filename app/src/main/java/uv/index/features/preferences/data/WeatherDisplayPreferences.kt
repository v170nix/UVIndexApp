package uv.index.features.preferences.data

import kotlinx.coroutines.flow.Flow
import uv.index.features.weather.domain.WeatherDisplayMode

interface WeatherDisplayPreferences {
    val modeAsStateFlow: Flow<WeatherDisplayMode>
    fun getMode(): WeatherDisplayMode
    suspend fun updateMode(mode: WeatherDisplayMode)
}
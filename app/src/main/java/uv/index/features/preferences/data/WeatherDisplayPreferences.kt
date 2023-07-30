package uv.index.features.preferences.data

import kotlinx.coroutines.flow.Flow
import uv.index.features.weather.domain.WeatherMetricsMode

interface WeatherDisplayPreferences {
    val modeAsStateFlow: Flow<WeatherMetricsMode>
    fun getMode(): WeatherMetricsMode
    suspend fun updateMode(mode: WeatherMetricsMode)
}
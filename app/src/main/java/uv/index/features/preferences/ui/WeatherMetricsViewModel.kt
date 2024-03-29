package uv.index.features.preferences.ui

import androidx.activity.ComponentActivity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import uv.index.features.preferences.data.WeatherDisplayPreferences
import uv.index.features.weather.domain.WeatherMetricsMode
import javax.inject.Inject

@HiltViewModel
class WeatherMetricsViewModel @Inject constructor(
    private val preferences: WeatherDisplayPreferences
): ViewModel() {
    val state = preferences.modeAsStateFlow.stateIn(
        viewModelScope,
        initialValue = preferences.getMode(),
        started = SharingStarted.Lazily
    )

    fun updateMode(mode: WeatherMetricsMode) {
        viewModelScope.launch {
            preferences.updateMode(mode)
        }
    }
}

@Composable
@Stable
fun rememberWeatherMetricsMode(): WeatherMetricsMode {
    val model = hiltViewModel<WeatherMetricsViewModel>(LocalContext.current as ComponentActivity)
    val mode by model.state.collectAsState()
    return mode
}
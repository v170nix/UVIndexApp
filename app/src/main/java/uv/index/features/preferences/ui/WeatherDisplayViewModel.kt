package uv.index.features.preferences.ui

import androidx.activity.ComponentActivity
import androidx.compose.runtime.Composable
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
import uv.index.features.weather.domain.WeatherDisplayMode
import javax.inject.Inject

@HiltViewModel
class WeatherDisplayViewModel @Inject constructor(
    private val preferences: WeatherDisplayPreferences
): ViewModel() {
    val state = preferences.modeAsStateFlow.stateIn(
        viewModelScope,
        initialValue = preferences.getMode(),
        started = SharingStarted.Lazily
    )

    fun updateMode(mode: WeatherDisplayMode) {
        viewModelScope.launch {
            preferences.updateMode(mode)
        }
    }
}

@Composable
fun rememberWeatherDisplayMode(): WeatherDisplayMode {
    val model = hiltViewModel<WeatherDisplayViewModel>(LocalContext.current as ComponentActivity)
    val mode by model.state.collectAsState()
    return mode
}
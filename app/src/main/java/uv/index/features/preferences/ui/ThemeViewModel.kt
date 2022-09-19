package uv.index.features.preferences.ui

import android.content.res.Configuration
import androidx.activity.ComponentActivity
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import uv.index.features.preferences.data.ThemeMode
import uv.index.features.preferences.data.ThemePreferences
import javax.inject.Inject

@HiltViewModel
class ThemeViewModel @Inject constructor(
    private val preferences: ThemePreferences
) : ViewModel() {

    val state = preferences.modeAsStateFlow.stateIn(
        viewModelScope,
        initialValue = preferences.getMode(),
        started = SharingStarted.Lazily
    )


    fun updateMode(mode: ThemeMode) {
        viewModelScope.launch {
            preferences.updateMode(mode)
        }
    }

}

@Composable
fun isAppInDarkTheme(): Boolean {
    val model = hiltViewModel<ThemeViewModel>(LocalContext.current as ComponentActivity)
    val mode by model.state.collectAsState()

    val configuration = LocalConfiguration.current
    val isDarkTheme by remember(mode, configuration) {
        derivedStateOf {
            when (mode) {
                ThemeMode.System -> {
                    val uiMode = configuration.uiMode
                    (uiMode and Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES
                }
                ThemeMode.Dark -> true
                ThemeMode.Light -> false
            }
        }
    }
    return isDarkTheme
}
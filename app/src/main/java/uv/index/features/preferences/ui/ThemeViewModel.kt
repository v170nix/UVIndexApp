package uv.index.features.preferences.ui

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
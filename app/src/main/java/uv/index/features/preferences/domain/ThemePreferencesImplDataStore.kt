package uv.index.features.preferences.domain

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.intPreferencesKey
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking
import uv.index.features.place.common.except
import uv.index.features.preferences.data.ThemeMode
import uv.index.features.preferences.data.ThemePreferences
import javax.inject.Inject

class ThemePreferencesImplDataStore @Inject constructor(
    private val dataStore: DataStore<Preferences>
) : ThemePreferences {
    override val modeAsStateFlow: Flow<ThemeMode>
        get() = dataStore.data
            .catch {
                emit(emptyPreferences())
            }
            .map { preferences ->
                ThemeMode.values()[preferences[PREF_THEME_MODE_KEY] ?: ThemeMode.System.ordinal]
            }

    override fun getMode(): ThemeMode {
        return runBlocking {
            runCatching {
                val ordinal = dataStore.data.firstOrNull()?.get(PREF_THEME_MODE_KEY)
                    ?: ThemeMode.System.ordinal
                ThemeMode.values()[ordinal]
            }
                .except<CancellationException, ThemeMode>()
                .getOrDefault(ThemeMode.System)
        }
    }

    override suspend fun updateMode(mode: ThemeMode) {
        dataStore.edit {
            it[PREF_THEME_MODE_KEY] = mode.ordinal
        }
    }

    private companion object {
        val PREF_THEME_MODE_KEY = intPreferencesKey(name = "theme.mode.isDark")
    }
}
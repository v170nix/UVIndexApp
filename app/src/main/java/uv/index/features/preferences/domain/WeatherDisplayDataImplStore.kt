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
import uv.index.features.preferences.data.WeatherDisplayPreferences
import uv.index.features.weather.domain.WeatherDisplayMode
import java.util.*
import javax.inject.Inject

class WeatherDisplayDataImplStore @Inject constructor(
    private val dataStore: DataStore<Preferences>,
) : WeatherDisplayPreferences {

    override val modeAsStateFlow: Flow<WeatherDisplayMode>
        get() = dataStore.data
            .catch {
                emit(emptyPreferences())
            }
            .map { preferences ->
                getDataFromPreference(Locale.getDefault(), preferences)
            }

    override fun getMode(): WeatherDisplayMode {
        val locale = Locale.getDefault()
        return runBlocking {
            runCatching {
                val preferences: Preferences? = dataStore.data.firstOrNull()
                return@runCatching if (preferences != null)
                    getDataFromPreference(locale, preferences)
                else
                    getDataFromPreference(locale)
            }
                .except<CancellationException, WeatherDisplayMode>()
                .getOrDefault(getDataFromPreference(locale))
        }
    }

    override suspend fun updateMode(mode: WeatherDisplayMode) {
        dataStore.edit {
            it[PREF_TEMPERATURE_MODE_KEY] = mode.temperature.ordinal
            it[PREF_PRESSURE_MODE_KEY] = mode.pressure.ordinal
            it[PREF_WIND_MODE_KEY] = mode.wind.ordinal
        }
    }

    private companion object {
        val PREF_TEMPERATURE_MODE_KEY = intPreferencesKey(name = "weather.mode.temperature")
        val PREF_PRESSURE_MODE_KEY = intPreferencesKey(name = "weather.mode.pressure")
        val PREF_WIND_MODE_KEY = intPreferencesKey(name = "weather.mode.wind")

        private fun getDataFromPreference(
            locale: Locale,
            preferences: Preferences
        ): WeatherDisplayMode {
            return WeatherDisplayMode(
                temperature = WeatherDisplayMode.Temperature.values()[
                        preferences[PREF_TEMPERATURE_MODE_KEY] ?: if (locale == Locale.US)
                            WeatherDisplayMode.Temperature.Fahrenheit.ordinal
                        else
                            WeatherDisplayMode.Temperature.Celsius.ordinal
                ],
                pressure = WeatherDisplayMode.Pressure.values()[
                        preferences[PREF_PRESSURE_MODE_KEY] ?: if (locale == Locale.US)
                            WeatherDisplayMode.Pressure.Inches.ordinal
                        else
                            WeatherDisplayMode.Pressure.Millibars.ordinal
                ],
                wind = WeatherDisplayMode.Wind.values()[
                        preferences[PREF_WIND_MODE_KEY] ?: if (locale == Locale.US)
                            WeatherDisplayMode.Wind.MilePerHour.ordinal
                        else
                            WeatherDisplayMode.Wind.KilometerPerHour.ordinal

                ]
            )
        }

        private fun getDataFromPreference(locale: Locale): WeatherDisplayMode {
            return WeatherDisplayMode(
                temperature = WeatherDisplayMode.Temperature.values()[
                        if (locale == Locale.US)
                            WeatherDisplayMode.Temperature.Fahrenheit.ordinal
                        else
                            WeatherDisplayMode.Temperature.Celsius.ordinal
                ],
                pressure = WeatherDisplayMode.Pressure.values()[
                        if (locale == Locale.US)
                            WeatherDisplayMode.Pressure.Inches.ordinal
                        else
                            WeatherDisplayMode.Pressure.Millibars.ordinal
                ],
                wind = WeatherDisplayMode.Wind.values()[
                        if (locale == Locale.US)
                            WeatherDisplayMode.Wind.MilePerHour.ordinal
                        else
                            WeatherDisplayMode.Wind.KilometerPerHour.ordinal
                ]
            )
        }
    }

}
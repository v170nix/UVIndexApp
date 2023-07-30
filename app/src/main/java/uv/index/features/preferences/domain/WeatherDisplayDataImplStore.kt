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
import uv.index.features.weather.domain.WeatherMetricsMode
import java.util.*
import javax.inject.Inject

class WeatherDisplayDataImplStore @Inject constructor(
    private val dataStore: DataStore<Preferences>,
) : WeatherDisplayPreferences {

    override val modeAsStateFlow: Flow<WeatherMetricsMode>
        get() = dataStore.data
            .catch {
                emit(emptyPreferences())
            }
            .map { preferences ->
                getDataFromPreference(Locale.getDefault(), preferences)
            }

    override fun getMode(): WeatherMetricsMode {
        val locale = Locale.getDefault()
        return runBlocking {
            runCatching {
                val preferences: Preferences? = dataStore.data.firstOrNull()
                return@runCatching if (preferences != null)
                    getDataFromPreference(locale, preferences)
                else
                    getDataFromPreference(locale)
            }
                .except<CancellationException, WeatherMetricsMode>()
                .getOrDefault(getDataFromPreference(locale))
        }
    }

    override suspend fun updateMode(mode: WeatherMetricsMode) {
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
        ): WeatherMetricsMode {
            return WeatherMetricsMode(
                temperature = WeatherMetricsMode.Temperature.values()[
                        preferences[PREF_TEMPERATURE_MODE_KEY] ?: if (locale == Locale.US)
                            WeatherMetricsMode.Temperature.Fahrenheit.ordinal
                        else
                            WeatherMetricsMode.Temperature.Celsius.ordinal
                ],
                pressure = WeatherMetricsMode.Pressure.values()[
                        preferences[PREF_PRESSURE_MODE_KEY] ?: if (locale == Locale.US)
                            WeatherMetricsMode.Pressure.Inches.ordinal
                        else
                            WeatherMetricsMode.Pressure.Millibars.ordinal
                ],
                wind = WeatherMetricsMode.Wind.values()[
                        preferences[PREF_WIND_MODE_KEY] ?: if (locale == Locale.US)
                            WeatherMetricsMode.Wind.MilePerHour.ordinal
                        else
                            WeatherMetricsMode.Wind.KilometerPerHour.ordinal

                ]
            )
        }

        private fun getDataFromPreference(locale: Locale): WeatherMetricsMode {
            return WeatherMetricsMode(
                temperature = WeatherMetricsMode.Temperature.values()[
                        if (locale == Locale.US)
                            WeatherMetricsMode.Temperature.Fahrenheit.ordinal
                        else
                            WeatherMetricsMode.Temperature.Celsius.ordinal
                ],
                pressure = WeatherMetricsMode.Pressure.values()[
                        if (locale == Locale.US)
                            WeatherMetricsMode.Pressure.Inches.ordinal
                        else
                            WeatherMetricsMode.Pressure.Millibars.ordinal
                ],
                wind = WeatherMetricsMode.Wind.values()[
                        if (locale == Locale.US)
                            WeatherMetricsMode.Wind.MilePerHour.ordinal
                        else
                            WeatherMetricsMode.Wind.KilometerPerHour.ordinal
                ]
            )
        }
    }

}
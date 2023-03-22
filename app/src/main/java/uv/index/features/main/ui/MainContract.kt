package uv.index.features.main.ui

import androidx.compose.runtime.Stable
import net.arwix.mvi.UIEvent
import net.arwix.mvi.UIState
import uv.index.features.astronomy.data.SunPosition
import uv.index.features.place.data.room.PlaceData
import uv.index.features.weather.data.Weather
import uv.index.lib.data.UVSkinType
import uv.index.lib.data.UVSummaryDayData
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZonedDateTime

@Stable
interface MainContract {

    @Stable
    data class State constructor(
        val place: PlaceData? = null,

        val isViewRetry: Boolean = false,
        val isViewLoadingData: Boolean = false,
        val isLoadingPlace: Boolean = true,

        val currentDateTime: ZonedDateTime? = null,
        val currentSunData: SunData? = null,
        val uvCurrentDayHours: List<UVHourData> = listOf(),

        val uvCurrentSummaryDayData: UVSummaryDayData? = null,
        val uvCurrentData: UVCurrentData? = null,
        val uvForecastDays: List<UVSummaryDayData> = listOf(),
        val uvForecastHours: List<UVHourData> = listOf(),
        @Stable val weatherData: Weather.Data? = null,
        val skinType: UVSkinType? = null,
        val peakTime: LocalTime? = null,
        val viewMode: ViewMode = ViewMode.Weather
    ) : UIState

    @Stable
    data class SunData(
        val position: SunPosition,
        val riseTime: LocalTime?,
        val setTime: LocalTime?,
    )

    @Stable
    sealed interface ViewMode {
        object UV: ViewMode
        object Weather: ViewMode
    }

    @Stable
    data class UVCurrentData(
        val index: Double?,
        val timeToBurn: TimeToEvent,
        val timeToVitaminD: TimeToEvent,
    )

    @Stable
    sealed class Event : UIEvent {
        data class DoChangeSkin(val skin: UVSkinType) : Event()
        object DoDataManualUpdate : Event()
        object DoDataAutoUpdate : Event()
        object DoUpdateWithCurrentTime : Event()
        object DoChangeViewMode: Event()
    }

    @Stable
    sealed class TimeToEvent {

        object Infinity : TimeToEvent()

        @Stable
        data class Value(
            val minTimeInMins: Int,
            val maxTimeInMins: Int?
        ) : TimeToEvent()
    }

    @Stable
    sealed class UVHourData {

        @Stable
        data class Item(
            val localDateTime: LocalDateTime,
            val sIndex: String,
            val iIndex: Int,
            val timeText: String,
        ) : UVHourData()

        object Divider : UVHourData()
    }
}
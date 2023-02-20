package uv.index.features.main.ui

import androidx.compose.runtime.Immutable
import net.arwix.mvi.UIEvent
import net.arwix.mvi.UIState
import uv.index.features.astronomy.data.SunPosition
import uv.index.features.place.data.room.PlaceData
import uv.index.lib.data.UVSkinType
import uv.index.lib.data.UVSummaryDayData
import java.time.LocalTime
import java.time.ZonedDateTime

@Immutable
interface MainContract {

    @Immutable
    data class State constructor(
        val place: PlaceData? = null,

        val isViewRetry: Boolean = false,
        val isViewLoadingData: Boolean = false,
        val isLoadingPlace: Boolean = true,

        val currentDateTime: ZonedDateTime? = null,
        val currentSunData: SunData? = null,

        val uvCurrentSummaryDayData: UVSummaryDayData? = null,
        val uvCurrentData: UVCurrentData? = null,
        val uvForecastDays: List<UVSummaryDayData> = listOf(),
        val uvForecastHours: List<UVHourData> = listOf(),

        val skinType: UVSkinType? = null,

        val peakTime: LocalTime? = null,
    ) : UIState

    @Immutable
    data class SunData(
        val position: SunPosition,
        val riseTime: LocalTime?,
        val setTime: LocalTime?,
    )

    @Immutable
    data class UVCurrentData(
        val index: Double?,
        val timeToBurn: TimeToEvent,
        val timeToVitaminD: TimeToEvent,
    )

    @Immutable
    sealed class Event : UIEvent {
        data class DoChangeSkin(val skin: UVSkinType) : Event()
        object DoDataManualUpdate : Event()
        object DoDataAutoUpdate : Event()
        object DoUpdateWithCurrentTime : Event()
    }

    @Immutable
    sealed class TimeToEvent {

        object Infinity : TimeToEvent()

        @Immutable
        data class Value(
            val minTimeInMins: Int,
            val maxTimeInMins: Int?
        ) : TimeToEvent()
    }

    @Immutable
    sealed class UVHourData {

        @Immutable
        data class Item(
            val sIndex: String,
            val iIndex: Int,
            val time: String,
        ) : UVHourData()

        object Divider : UVHourData()
    }
}
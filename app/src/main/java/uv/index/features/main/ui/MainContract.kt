package uv.index.features.main.ui

import androidx.compose.runtime.Immutable
import net.arwix.mvi.UIEvent
import net.arwix.mvi.UIState
import uv.index.features.main.domain.SunPosition
import uv.index.features.place.data.room.PlaceData
import uv.index.lib.data.UVIndexData
import uv.index.lib.data.UVSkinType
import uv.index.lib.data.UVSummaryDayData
import java.time.LocalTime
import java.time.ZonedDateTime

@Immutable
interface MainContract {

    @Immutable
    data class State(
        val place: PlaceData? = null,

        val isLoadingPlace: Boolean = true,
        val currentDayData: List<UVIndexData>? = null,
        val currentSummaryDayData: UVSummaryDayData? = null,
        val daysForecast: List<UVSummaryDayData>? = null,
        val hoursForecast: List<UVIndexData> = listOf(),
        val isViewRetry: Boolean = false,
        val isViewLoadingData: Boolean = false,
        val skinType: UVSkinType,

        val currentUiHoursData: List<UIHourData> = listOf(),
        val riseTime: LocalTime? = null,
        val setTime: LocalTime? = null,
        val currentZdt: ZonedDateTime? = null,
        val currentIndexValue: Double? = null,
        val currentSunPosition: SunPosition? = null,
        val currentTimeToBurn: TimeToEvent? = null,
        val currentTimeToVitaminD: TimeToEvent? = null,
        val currentPeakTime: LocalTime? = null

    ): UIState

    @Immutable
    sealed class Event : UIEvent {
        data class DoChangeSkin(val skin: UVSkinType): Event()
        object DoDataManualUpdate: Event()
        object DoDataAutoUpdate: Event()
        object DoUpdateWithCurrentTime: Event()
    }

    @Immutable
    sealed class TimeToEvent {

        object Infinity: TimeToEvent()

        @Immutable
        data class Value(
            val minTimeInMins: Int,
            val maxTimeInMins: Int?
        ): TimeToEvent()
    }

    @Immutable
    sealed class UIHourData {

        @Immutable
        data class Item(
            val sIndex: String,
            val iIndex: Int,
            val time: String,
        ) : UIHourData()

        object Divider : UIHourData()
    }
}
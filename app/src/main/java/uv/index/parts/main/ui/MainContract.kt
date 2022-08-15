package uv.index.parts.main.ui

import androidx.compose.runtime.Immutable
import net.arwix.mvi.UIEvent
import net.arwix.mvi.UIState
import uv.index.lib.data.UVIPlaceData
import uv.index.lib.data.UVIndexData
import uv.index.lib.data.UVSkinType
import uv.index.lib.data.UVSummaryDayData
import uv.index.parts.main.domain.SunPosition
import java.time.LocalTime
import java.time.ZonedDateTime

@Immutable
interface MainContract {

    @Immutable
    data class State(
        val place: UVIPlaceData? = null,

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
        val currentTimeToBurn: TimeToBurn? = null

    ): UIState

    @Immutable
    sealed class Event : UIEvent {
        data class DoChangeSkin(val skin: UVSkinType): Event()
        object DoDataManualUpdate: Event()
        object DoDataAutoUpdate: Event()
        object DoUpdateWithCurrentTime: Event()
    }

    @Immutable
    sealed class TimeToBurn {

        object Infinity: TimeToBurn()

        @Immutable
        data class Value(
            val minTimeInMins: Int,
            val maxTimeInMins: Int?
        ): TimeToBurn()
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
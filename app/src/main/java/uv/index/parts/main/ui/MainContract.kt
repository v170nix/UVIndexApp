package uv.index.parts.main.ui

import androidx.compose.runtime.Immutable
import net.arwix.mvi.UIEvent
import net.arwix.mvi.UIState
import uv.index.lib.data.UVIPlaceData
import uv.index.lib.data.UVIndexData
import uv.index.lib.data.UVSkinType
import uv.index.lib.data.UVSummaryDayData

@Immutable
interface MainContract {

    @Immutable
    data class State(
        val place: UVIPlaceData? = null,
        val isLoadingPlace: Boolean = true,
        val currentDayData: List<UVIndexData>? = null,
        val currentSummaryDayData: UVSummaryDayData? = null,
        val forecastData: List<UVSummaryDayData>? = null,
        val hoursData: List<UVIndexData>? = null,
        val isViewRetry: Boolean = false,
        val isViewLoadingData: Boolean = false,
        val skinType: UVSkinType
    ): UIState

    @Immutable
    sealed class Event : UIEvent {
        data class DoChangeSkin(val skin: UVSkinType): Event()
        object DoManualUpdate: Event()
        object DoAutoUpdate: Event()
    }
}
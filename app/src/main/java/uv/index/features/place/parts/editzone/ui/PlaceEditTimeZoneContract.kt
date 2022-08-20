package uv.index.features.place.parts.editzone.ui

import com.google.android.gms.maps.model.LatLng
import net.arwix.mvi.UIEvent
import net.arwix.mvi.UISideEffect
import net.arwix.mvi.UIState
import uv.index.features.place.data.PlaceEditInnerData
import uv.index.features.place.data.gtimezone.TimeZoneDisplayEntry
import uv.index.features.place.data.gtimezone.TimeZoneRepository.Companion.getDisplayEntry
import java.time.Instant

object PlaceEditTimeZoneContract {
    data class State(
        val listZones: List<TimeZoneDisplayEntry> = listOf(),
        val autoTimeZoneEntry: AutoTimeZoneEntry? = null,
        val selectedItem: SelectedItem? = null,
        val finishStepAvailable: Boolean = false
    ) : UIState {

        sealed class SelectedItem {
            data class FromList(val value: TimeZoneDisplayEntry) : SelectedItem()
            data class FromAutoTimeZone(val value: AutoTimeZoneEntry.Ok) : SelectedItem()

            companion object {
                fun createSelectedItem(
                    data: PlaceEditInnerData,
                    instant: Instant
                ): SelectedItem? {
                    data.zoneId ?: return null
                    return if (data.isAutoZone) {
                        FromAutoTimeZone(
                            AutoTimeZoneEntry.Ok(
                                latLng = data.latLng,
                                timeZoneDisplayEntry = getDisplayEntry(data.zoneId, instant)
                            )
                        )
                    } else {
                        FromList(
                            TimeZoneDisplayEntry(data.zoneId, instant)
                        )
                    }
                }
            }
        }

        sealed class AutoTimeZoneEntry {
            object Denied : AutoTimeZoneEntry()
            data class Loading(val latLng: LatLng) : AutoTimeZoneEntry()
            data class Ok(val latLng: LatLng, val timeZoneDisplayEntry: TimeZoneDisplayEntry) :
                AutoTimeZoneEntry()

            data class Error(val latLng: LatLng, val error: Throwable) : AutoTimeZoneEntry()

            fun getLatitudeLongitude(): LatLng? {
                return when (this) {
                    is Error -> latLng
                    Denied -> null
                    is Loading -> latLng
                    is Ok -> latLng
                }
            }
        }

        companion object {
            fun TimeZoneDisplayEntry.isSelectedItem(item: SelectedItem?) =
                if (item is SelectedItem.FromList) item.value == this
                else false

            fun AutoTimeZoneEntry.isSelectedItem(item: SelectedItem?) =
                if (item is SelectedItem.FromAutoTimeZone) item.value == this
                else false
        }
    }

    sealed class Event : UIEvent {
        object GetPremium : Event()
        data class SelectItem(val item: State.SelectedItem) : Event()
        object Submit : Event()
        object ClearData : Event()
    }

    sealed class Effect : UISideEffect {
        object OnSubmitData : Effect()
    }

}
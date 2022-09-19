package uv.index.features.place.parts.editlocation.ui

import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.PointOfInterest
import net.arwix.mvi.UIEvent
import net.arwix.mvi.UISideEffect
import net.arwix.mvi.UIState
import uv.index.features.place.data.PlaceAutocompleteResult

@Suppress("MagicNumber")
object PlaceLocationContract {

    data class State(
        val cameraPosition: CameraPosition? = null,
        val nextStepIsAvailable: Boolean = false,
        val inputState: InputState = InputState()
    ) : UIState {
        data class InputState(
            val name: String = "",
            val subName: String = "",
            val latitude: String = "",
            val longitude: String = ""
        ) {
            @Suppress("ReturnCount")
            fun getLatLng(): LatLng? {
                val lat = latitude.toDoubleOrNull() ?: return null
                val lng = longitude.toDoubleOrNull() ?: return null
                if (lat < -90.0 || lat > 90.0) return null
                if (lng < -180.0 || lng > 180.0) return null
                return LatLng(lat, lng)
            }
        }
    }

    sealed class Event : UIEvent {
        data class SelectLocationFromMap(
            val latLng: LatLng,
            val cameraPosition: CameraPosition?
        ) : Event()
        data class SelectLocationFromPOI(
            val point: PointOfInterest,
            val cameraPosition: CameraPosition?
        ): Event()
        data class NotifyUpdateCameraPosition(val cameraPosition: CameraPosition): Event()
        data class ChangeLatitudeFromInput(val latitude: String) : Event()
        data class ChangeLongitudeFromInput(val longitude: String) : Event()
        data class ChangeLocationFromPlace(val result: PlaceAutocompleteResult) : Event()
        object Submit: Event()
        object ClearData: Event()
    }

    sealed class Effect : UISideEffect {
        data class ChangeLocationOnMap(
            val latLng: LatLng,
            val cameraPosition: CameraPosition?,
            val updateZoom: Boolean = false,
        ) : Effect()
        data class ChangeCenterMapToLatLng(val latLng: LatLng): Effect()
        object SubmitData: Effect()
    }

}
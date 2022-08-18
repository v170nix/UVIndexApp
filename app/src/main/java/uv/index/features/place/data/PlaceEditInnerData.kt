package uv.index.features.place.data

import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import uv.index.features.place.data.room.PlaceData
import java.time.ZoneId

data class PlaceEditInnerData(
    val id: Int? = null,
    val name: String? = null,
    val subName: String? = null,
    val zoneId: ZoneId? = null,
    val isAutoZone: Boolean = false,
    val latLng: LatLng,
    val cameraPosition: CameraPosition? = null,
    val isSelectedPlace: Boolean = false,
) {
    companion object {

        @JvmStatic
        fun createFromPlaceData(data: PlaceData): PlaceEditInnerData {
            return PlaceEditInnerData(
                data.id,
                data.name,
                data.subName,
                data.zone,
                data.isAutoZone,
                data.latLng,
                CameraPosition.builder()
                    .target(data.latLng)
                    .apply { data.zoom?.run(::zoom) }
                    .apply { data.tilt?.run(::tilt) }
                    .apply { data.bearing?.run(::bearing) }
                    .build(),
                data.isSelected
            )
        }
    }
}
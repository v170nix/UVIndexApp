package uv.index.parts.place.data

import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import uv.index.parts.place.data.room.PlaceDao
import uv.index.parts.place.data.room.PlaceData
import java.time.ZoneId
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PlaceEditInnerRepository @Inject constructor(private val dao: PlaceDao)
{
    private val _state = MutableStateFlow<PlaceEditInnerData?>(null)
    val data = _state.asStateFlow()

    fun clearData() {
        _state.value = null
    }

    fun editData(placeData: PlaceData) {
        _state.value = PlaceEditInnerData.createFromPlaceData(placeData)
    }

    fun updateLocation(
        name: String,
        subName: String,
        latLng: LatLng,
        cameraPosition: CameraPosition?
    ) {
        _state.update {
            it?.copy(name = name, subName = subName, latLng = latLng, cameraPosition = cameraPosition) ?:
            PlaceEditInnerData(
                null, name, subName, latLng = latLng, cameraPosition = cameraPosition
            )
        }
    }

    suspend fun updateTimeZone(
        zoneId: ZoneId,
        isAutoZone: Boolean,
    ): Boolean {
        val value = _state.value ?: return false
        _state.value = value.copy(zoneId = zoneId, isAutoZone = isAutoZone)
        return submit()
    }

    private suspend fun submit(): Boolean {
        val value = _state.value ?: return false
        value.zoneId ?: return false
        val place = PlaceData(
            id = value.id,
            name = value.name,
            subName = value.subName,
            latLng = value.latLng,
            zone = value.zoneId,
            isAutoZone = value.isAutoZone,
            zoom  = value.cameraPosition?.zoom,
            bearing  = value.cameraPosition?.bearing,
            tilt = value.cameraPosition?.tilt,
            isSelected = value.isSelectedPlace,
            isAutoLocation = false
        )
        if (place.id == null) {
            dao.insert(place)
        } else {
            val listIsNotSelectedItems = dao.getSelectedItem() == null
            if (listIsNotSelectedItems) {
                dao.update(place.copy(isSelected = true))
            } else dao.update(place)
        }
        return true
    }

}
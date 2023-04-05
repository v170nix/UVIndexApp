package uv.index.features.place.parts.list.domain

import android.content.Context
import android.location.Location
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import net.arwix.coroutines.ConflatedJob
import uv.index.features.place.common.getLocation
import uv.index.features.place.common.locationCheckPermission
import uv.index.features.place.data.GeocoderRepository
import uv.index.features.place.data.room.PlaceDao
import uv.index.features.place.data.room.PlaceData
import uv.index.features.place.parts.list.data.PlaceListItem
import java.time.ZoneId
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PlaceListUseCase @Inject constructor(
    @ApplicationContext context: Context,
    private val dao: PlaceDao,
    private val geocoder: GeocoderRepository,
) {

    private val applicationContext = context.applicationContext
    private val geolocationJob = ConflatedJob()
    private lateinit var sideScope: CoroutineScope

    private val innerAutoState = MutableStateFlow<PlaceListItem.Auto?>(null)
    private val dbPlaces = dao.getAll()
        .map { list -> list.map(::transformToUI) }
        .withIndex()

    val places = dbPlaces
        .combine(innerAutoState) { (index, list: List<PlaceListItem>), innerAutoItemUI ->
            val autoItem = list.getAutoItem()
            if (autoItem != null) {
                if (index == 0) updateCurrentLocation()
                list
            } else {
                list.toMutableList().apply {
                    if (innerAutoItemUI != null) {
                        add(0, innerAutoItemUI)
                    } else {
                        innerAutoState.value = createAutoItem()
                    }
                }
            }
        }
        .filter { list -> list.firstOrNull { it is PlaceListItem.Auto } != null }

    fun attachSideScope(scope: CoroutineScope) {
        sideScope = scope
    }

    suspend fun deleteItem(id: Int?) {
        if (id != null) dao.deleteById(id)
    }

    suspend fun undoDeleteItem(item: PlaceListItem.Custom) {
        dao.insert(item.place)
    }

    suspend fun selectAutoItem() {
        dao.selectAutoItem()
    }

    suspend fun selectCustomItem(place: PlaceData) {
        dao.selectCustomItem(place.copy(isSelected = true))
    }

    suspend fun requestUpdateAutoLocation(isForceUpdateLocation: Boolean = false) {
        innerAutoState.value = createAutoItem(isForceUpdateLocation)
    }

    @Suppress("MagicNumber")
    private suspend fun createAutoItem(isForceUpdateLocation: Boolean = false): PlaceListItem.Auto? {
        val permission = applicationContext.locationCheckPermission()

        var location: Location? = null
        val timeOut = 3000L
        val delta = 1000L
        val maxCount = 5
        var count = 0

        while (location == null && count < maxCount) {
            count++
            location = withTimeoutOrNull(timeOut + delta * count) {
                applicationContext.getLocation(isForceUpdateLocation)
            }
            if (location == null) location = applicationContext.getLocation(false)
        }

        return if (location == null) {
            if (permission) PlaceListItem.Auto(PlaceListItem.Auto.State.Allow(null))
            else PlaceListItem.Auto(PlaceListItem.Auto.State.Denied)
        } else {
            saveCurrentLocation(location)
            null
        }
    }

    private suspend fun updateCurrentLocation() {
        sideScope.launch {
            val location = applicationContext.getLocation(false) ?: return@launch
            saveCurrentLocation(location)
        }
    }

    private suspend fun saveCurrentLocation(location: Location) {
        dao.updateAutoItem(location, ZoneId.systemDefault())
        geolocationJob += sideScope.launch(Dispatchers.IO, CoroutineStart.LAZY) geo@{
            val address =
                geocoder.getAddressOrNull(location.latitude, location.longitude) ?: return@geo
            ensureActive()
            dao.updateAutoItem(address)
        }
        geolocationJob.start()
    }

    private companion object {
        private fun transformToUI(placeData: PlaceData) =
            if (!placeData.isAutoLocation) PlaceListItem.Custom(placeData)
            else PlaceListItem.Auto(PlaceListItem.Auto.State.Allow(placeData))

        private fun List<PlaceListItem>.getAutoItem() = find { it is PlaceListItem.Auto }

    }
}

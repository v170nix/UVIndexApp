package uv.index.features.place.parts.editlocation.ui

import android.content.Context
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.Places
import com.google.maps.android.ktx.model.cameraPosition
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import net.arwix.extension.ConflatedJob
import net.arwix.mvi.SimpleViewModel
import uv.index.di.PlaceKey
import uv.index.features.place.data.GeocoderRepository
import uv.index.features.place.data.PlaceEditInnerRepository
import uv.index.features.place.data.getSubTitle
import uv.index.features.place.data.getTitle
import uv.index.features.place.parts.editlocation.ui.PlaceLocationContract.Effect
import uv.index.features.place.parts.editlocation.ui.PlaceLocationContract.Event
import uv.index.features.place.parts.editlocation.ui.PlaceLocationContract.Event.*
import uv.index.features.place.parts.editlocation.ui.PlaceLocationContract.State
import javax.inject.Inject
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract

@HiltViewModel
class PlaceEditLocationViewModel @Inject constructor(
    @ApplicationContext applicationContext: Context,
    @PlaceKey placeKey: String,
    private val geocoderRepository: GeocoderRepository,
    private val editInnerRepository: PlaceEditInnerRepository
) : SimpleViewModel<Event, State, Effect>(
    State()
) {

    private val actionJob = ConflatedJob()
    private val geocodeJob = ConflatedJob()

    init {
        if (!Places.isInitialized()) {
            Places.initialize(applicationContext.applicationContext, placeKey)
        }
        editInnerRepository.data
            .onEach { innerData ->
                if (innerData == null) {
                    doEvent(ClearData)
                } else {
                    reduceState {
                        copy(
                            inputState = State.InputState(
                                innerData.name ?: "",
                                innerData.subName ?: "",
                                innerData.latLng.latitude.toString(),
                                innerData.latLng.longitude.toString()
                            ),
                            cameraPosition = innerData.cameraPosition,
                            nextStepIsAvailable = true,
                        )
                    }
                    applyEffect {
                        Effect.ChangeLocationOnMap(innerData.latLng, innerData.cameraPosition)
                    }
                }
            }
            .launchIn(viewModelScope)
    }

    override fun handleEvents(event: Event) {
        when (event) {
            is SelectLocationFromMap -> {
                reduceState {
                    copy(
                        nextStepIsAvailable = true,
                        inputState = State.InputState(
                            "", "",
                            event.latLng.latitude.toString(),
                            event.latLng.longitude.toString()
                        ),
                        cameraPosition = event.cameraPosition
                    )
                }
                requestNames(event.latLng)
                applyEffect {
                    Effect.ChangeCenterMapToLatLng(event.latLng)
                }
            }
            is SelectLocationFromPOI -> {
                val name = event.point.name.takeWhile { it != "\n".toCharArray().first() }
                reduceState {
                    copy(
                        nextStepIsAvailable = true,
                        inputState = State.InputState(
                            name = name,
                            latitude = event.point.latLng.latitude.toString(),
                            longitude = event.point.latLng.longitude.toString()
                        )
                    )
                }
                requestNames(event.point.latLng, name)
                applyEffect {
                    Effect.ChangeCenterMapToLatLng(event.point.latLng)
                }
            }
            is ChangeLatitudeFromInput -> {
                reduceState {
                    copy(inputState = inputState.copy(latitude = event.latitude))
                }
                doActionChangeLatitudeFromInput(event.latitude)
                state.value.inputState.getLatLng()?.let {
                    applyEffect {
                        Effect.ChangeLocationOnMap(it, state.value.cameraPosition)
                    }
                }
            }
            is ChangeLongitudeFromInput -> {
                reduceState {
                    copy(inputState = inputState.copy(longitude = event.longitude))
                }
                doActionChangeLongitudeFromInput(event.longitude)
                state.value.inputState.getLatLng()?.let {
                    applyEffect {
                        Effect.ChangeLocationOnMap(it, state.value.cameraPosition)
                    }
                }
            }
            is ChangeLocationFromPlace -> {
                val place = event.result.getPlaceInResult() ?: return
                if (place.latLng != null) {
                    geocodeJob.cancel()
                    reduceState {
                        copy(
                            nextStepIsAvailable = true,
                            inputState = inputState.copy(
                                name = place.name.orEmpty(),
                                subName = place.getSubTitle(),
                                latitude = place.latLng!!.latitude.toString(),
                                longitude = place.latLng!!.longitude.toString()
                            ),
                            cameraPosition = if (cameraPosition != null) {
                                cameraPosition {
                                    bearing(cameraPosition.bearing)
                                    target(cameraPosition.target)
                                    zoom(cameraPosition.zoom.coerceAtLeast(10f))
                                    tilt(cameraPosition.tilt)
                                }
                            } else null
                        )
                    }
                    applyEffect {
                        Effect.ChangeLocationOnMap(place.latLng!!, state.value.cameraPosition, updateZoom = true)
                    }
                }
            }
            ClearData -> {
                reduceState {
                    copy(
                        cameraPosition = null,
                        nextStepIsAvailable = false,
                        inputState = State.InputState()
                    )
                }
                editInnerRepository.clearData()
            }
            Submit -> {
                val name = state.value.inputState.name
                val subName = state.value.inputState.subName
                val latitude = state.value.inputState.latitude.toDoubleOrNull() ?: return
                val longitude = state.value.inputState.longitude.toDoubleOrNull() ?: return
                editInnerRepository.updateLocation(
                    name, subName, LatLng(latitude, longitude), state.value.cameraPosition
                )
                applyEffect {
                    Effect.SubmitData
                }
            }
            is NotifyUpdateCameraPosition -> {
                reduceState {
                    copy(
                        cameraPosition = event.cameraPosition
                    )
                }
            }
        }
    }

    private fun doActionChangeLatitudeFromInput(inputLatitude: String?) {
        actionJob += viewModelScope.launch {
            geocodeJob.cancel()
            val longitude = state.value.inputState.longitude.toDoubleOrNull()
            val latitude = inputLatitude?.toDoubleOrNull()
            if (checkLatLng(latitude, longitude)) {
                reduceStateFromInputError()
            } else {
                reduceStateFromInput(LatLng(latitude, longitude))
            }
        }
    }

    private fun doActionChangeLongitudeFromInput(inputLongitude: String?) {
        actionJob += viewModelScope.launch {
            geocodeJob.cancel()
            val longitude = inputLongitude?.toDoubleOrNull()
            val latitude = state.value.inputState.latitude.toDoubleOrNull()
            if (checkLatLng(latitude, longitude)) {
                reduceStateFromInputError()
            } else {
                reduceStateFromInput(LatLng(latitude, longitude))
            }
        }
    }

    private fun requestNames(latLng: LatLng, name: String? = null) {
        geocodeJob += viewModelScope.launch(Dispatchers.IO) {
            val address = geocoderRepository.getAddressOrNull(latLng.latitude, latLng.longitude)
            ensureActive()
            if (address == null) return@launch
            reduceState {
                copy(
                    nextStepIsAvailable = true,
                    inputState = inputState.copy(
                        name = name ?: address.getTitle(),
                        subName = address.getSubTitle()
                    )
                )
            }
        }
    }

    private fun reduceStateFromInputError() {
        reduceState {
            copy(
                nextStepIsAvailable = false,
                inputState = inputState.copy(name = "", subName = "")
            )
        }
    }

    private fun reduceStateFromInput(latLng: LatLng) {
        reduceState {
            copy(
                nextStepIsAvailable = true,
                inputState = inputState.copy(
                    name = "",
                    subName = "",
                    latitude = latLng.latitude.toString(),
                    longitude = latLng.longitude.toString()
                )
            )
        }
        applyEffect {
            Effect.ChangeLocationOnMap(
                latLng,
                state.value.cameraPosition
            )
        }
        requestNames(latLng)
    }

    private companion object {

        @OptIn(ExperimentalContracts::class)
        private fun checkLatLng(latitude: Double?, longitude: Double?): Boolean {
            contract {
                returns(false) implies (latitude != null)
                returns(false) implies (longitude != null)
            }
            return latitude == null || latitude < -90.0 || latitude > 90.0 ||
                    longitude == null || longitude < -180.0 || longitude > 180.0
        }
    }

}


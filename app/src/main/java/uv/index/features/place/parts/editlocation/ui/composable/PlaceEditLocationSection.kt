package uv.index.features.place.parts.editlocation.ui.composable

import android.os.SystemClock
import android.view.ViewGroup
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.PointOfInterest
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.rememberCameraPositionState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.plus
import net.arwix.mvi.EventHandler
import net.arwix.mvi.SimpleViewModel
import uv.index.R
import uv.index.common.TextFieldState
import uv.index.common.getViewsByType
import uv.index.common.setLogoPadding
import uv.index.features.place.common.except
import uv.index.features.place.data.PlaceAutocompleteResult
import uv.index.features.place.parts.editlocation.ui.PlaceLocationContract
import uv.index.features.place.parts.editlocation.ui.composable.components.InputLocationBoxPart
import uv.index.ui.theme.Dimens
import java.util.concurrent.CancellationException
import kotlin.math.abs
import kotlin.random.Random


@Composable
fun PlaceEditPositionSection(
    modifier: Modifier = Modifier,
    logoOffset: DpOffset,
    state: PlaceLocationContract.State,
    eventHandler: EventHandler<PlaceLocationContract.Event>,
    effectFlow: Flow<PlaceLocationContract.Effect>,
    onTimeZonePart: () -> Unit
) {

    val cameraPositionState = rememberCameraPositionState(Random.nextDouble().toString()) {
        if (state.cameraPosition != null) this.position = state.cameraPosition
    }

    LaunchedEffect(cameraPositionState.position) {
        eventHandler.doEvent(
            PlaceLocationContract.Event.NotifyUpdateCameraPosition(
                cameraPositionState.position
            )
        )
    }

    val markerPosition: LatLng? by remember(state.inputState) {
        derivedStateOf {
            val lat = state.inputState.latitude.toDoubleOrNull() ?: return@derivedStateOf null
            val lon = state.inputState.longitude.toDoubleOrNull() ?: return@derivedStateOf null
            LatLng(lat, lon)
        }
    }

    LaunchedEffect(SimpleViewModel.SIDE_EFFECT_LAUNCH_ID) {
        effectFlow.onEach { effect ->
            when (effect) {
                PlaceLocationContract.Effect.SubmitData -> {
                    onTimeZonePart()
                }
                is PlaceLocationContract.Effect.ChangeLocationOnMap -> {
                    val newZoom = if (effect.updateZoom) {
                        (effect.cameraPosition?.zoom
                            ?: cameraPositionState.position.zoom).coerceAtLeast(10f)
                    } else cameraPositionState.position.zoom

                    val newPosition = CameraPosition.builder().apply {
                        effect.cameraPosition?.run {
                            bearing(bearing)
                            zoom(newZoom)
                            tilt(tilt)
                        } ?: zoom(newZoom)
                    }
                        .target(effect.latLng)
                        .build()

                    cameraPositionState.move(CameraUpdateFactory.newCameraPosition(newPosition))
                    eventHandler.doEvent(
                        PlaceLocationContract.Event.NotifyUpdateCameraPosition(
                            newPosition
                        )
                    )

                }
                is PlaceLocationContract.Effect.ChangeCenterMapToLatLng -> {
                    launch {
                        runCatching {
                            cameraPositionState.animate(
                                CameraUpdateFactory.newLatLng(effect.latLng)
                            )
                        }.except<CancellationException, Any>()
                    }
                }
            }
        }.launchIn(this + Dispatchers.Main.immediate)
    }

    val context = LocalContext.current

    GoogleMap(
        modifier = modifier,
        cameraPositionState = cameraPositionState,
        uiSettings = MapUiSettings(
            compassEnabled = false,
            mapToolbarEnabled = false,
            zoomControlsEnabled = false
        ),
        onMapClick = { latLng ->
            eventHandler.doEvent(
                PlaceLocationContract.Event.SelectLocationFromMap(
                    latLng,
                    cameraPositionState.position
                )
            )
        },
        onPOIClick = { point: PointOfInterest ->
            eventHandler.doEvent(
                PlaceLocationContract.Event.SelectLocationFromPOI(
                    point,
                    cameraPositionState.position
                )
            )
        },
    ) {
        markerPosition?.run {
            Marker(position = this)
        }
    }

    LaunchedEffect(Unit) {
        runCatching {
            val activity = context as ComponentActivity
            val view: ViewGroup = activity.findViewById<ViewGroup>(android.R.id.content)
            val mapView = view.getViewsByType(MapView::class.java).firstOrNull()
            if (mapView != null) {
                setLogoPadding(activity, logoOffset, mapView)
            }
        }
    }


}

@Composable
fun InputLocationBoxComponent(
    modifier: Modifier = Modifier,
    state: PlaceLocationContract.State.InputState,
    eventHandler: EventHandler<PlaceLocationContract.Event>,
    onPreviousClick: () -> Unit
) {
    val latTextError =
        stringResource(R.string.place_location_error_range, -90, 90)
    val lngTextError =
        stringResource(R.string.place_location_error_range, -180, 180)

    val latState by remember(state.latitude, eventHandler) {
        derivedStateOf {
            TextFieldState(
                state.latitude,
                onValueChange = { eventHandler.doEvent(PlaceLocationContract.Event.ChangeLatitudeFromInput(it)) },
                isError = isLatitudeError(state.latitude),
                textError = latTextError
            )
        }
    }

    val lngState by remember(state.longitude, eventHandler) {
        derivedStateOf {
            TextFieldState(
                state.longitude,
                onValueChange = { eventHandler.doEvent(PlaceLocationContract.Event.ChangeLongitudeFromInput(it)) },
                isError = isLongitudeError(state.longitude),
                textError = lngTextError
            )
        }
    }

    var searchResultTime by remember(Unit) { mutableStateOf(0L) }

    BackHandler {
        val currentTime = SystemClock.elapsedRealtime()
        if (currentTime > searchResultTime + 300L) {
            eventHandler.doEvent(PlaceLocationContract.Event.ClearData)
            onPreviousClick()
        }
    }

    Box(
        modifier = modifier,
        contentAlignment = Alignment.BottomEnd
    ) {
        InputLocationBoxPart(
            Modifier.padding(start = Dimens.grid_2, top = 0.dp, end = Dimens.grid_2),
            state.name,
            state.subName,
            latState,
            lngState,
            onSearchClick = {
            },
            onSearchResult = { placeResult: PlaceAutocompleteResult ->
                searchResultTime = SystemClock.elapsedRealtime()
                eventHandler.doEvent(PlaceLocationContract.Event.ChangeLocationFromPlace(placeResult))
            }
        )
    }
}

private fun isLatitudeError(latitude: String): Boolean {
    if (latitude.isBlank()) return false
    val dLatitude = latitude.toDoubleOrNull() ?: return true
    return abs(dLatitude) > 90
}

private fun isLongitudeError(longitude: String): Boolean {
    if (longitude.isBlank()) return false
    val dLongitude = longitude.toDoubleOrNull() ?: return true
    return abs(dLongitude) > 180
}


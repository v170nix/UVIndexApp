package uv.index.features.place.parts.editzone.ui

import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import net.arwix.extension.ConflatedJob
import net.arwix.mvi.SimpleViewModel
import net.arwix.mvi.UISideEffect
import uv.index.features.place.common.except
import uv.index.features.place.data.PlaceEditInnerRepository
import uv.index.features.place.data.gtimezone.TimeZoneDisplayEntry
import uv.index.features.place.data.gtimezone.TimeZoneGoogleRepository
import uv.index.features.place.data.gtimezone.TimeZoneRepository
import uv.index.features.place.parts.editzone.ui.PlaceEditTimeZoneContract.Event
import uv.index.features.place.parts.editzone.ui.PlaceEditTimeZoneContract.State
import uv.index.features.place.parts.editzone.ui.PlaceEditTimeZoneContract.State.AutoTimeZoneEntry
import java.time.Instant
import java.time.ZoneId
import java.util.concurrent.CancellationException
import javax.inject.Inject

@HiltViewModel
class PlaceEditTimeZoneViewModel @Inject constructor(
    private val tzRepository: TimeZoneRepository,
    private val googleTzRepository: TimeZoneGoogleRepository,
    private val editInnerRepository: PlaceEditInnerRepository,
) : SimpleViewModel<Event, State, UISideEffect>(
    State(autoTimeZoneEntry = null)
) {

    private val autoLocationJob = ConflatedJob()
    private var saveToDbJob: Job? = null

    init {
        viewModelScope.launch(Dispatchers.IO) {
            val list = tzRepository.getZonesList()
            reduceState {
                copy(listZones = list)
            }
            editInnerRepository.data
                .onEach { innerData ->
                    if (innerData == null) {
                        doEvent(Event.ClearData)
                    } else {
                        val selectedItem = State.SelectedItem.createSelectedItem(
                            innerData,
                            Instant.now()
                        )
                        reduceState {
                            copy(
                                selectedItem = selectedItem,
                                finishStepAvailable = selectedItem != null
                            )
                        }
                        updateAutoZone(innerData.latLng)
                    }
                }
                .collect()
        }
    }

    override fun handleEvents(event: Event) {
        when (event) {
            Event.GetPremium -> {
                // TODO
            }
            is Event.SelectItem -> {
                reduceState {
                    copy(
                        selectedItem = event.item,
                        finishStepAvailable = true
                    )
                }
            }
            Event.Submit -> {
                val selectedItem = state.value.selectedItem ?: return
                if (saveToDbJob?.isActive == true) return
                saveToDbJob = viewModelScope.launch {
                    val isSuccess = when (selectedItem) {
                        is State.SelectedItem.FromAutoTimeZone -> {
                            editInnerRepository.updateTimeZone(
                                selectedItem.value.timeZoneDisplayEntry.id,
                                true
                            )
                        }
                        is State.SelectedItem.FromList -> {
                            editInnerRepository.updateTimeZone(selectedItem.value.id, false)
                        }
                    }
                    if (isSuccess) {
                        applyEffect {
                            PlaceEditTimeZoneContract.Effect.OnSubmitData
                        }
                    }
                }
            }
            Event.ClearData -> {
                reduceState {
                    copy(
                        selectedItem = null,
                        finishStepAvailable = false
                    )
                }
            }
        }
    }

    private fun updateAutoZone(latLng: LatLng) {
        if (state.value.autoTimeZoneEntry == AutoTimeZoneEntry.Denied) return
        autoLocationJob += viewModelScope.launch {
            val autoZoneState = state.value.autoTimeZoneEntry
            if (autoZoneState is AutoTimeZoneEntry.Ok && autoZoneState.latLng == latLng) return@launch
            reduceState {
                val isSelectedItemFromAuto = state.value.selectedItem is State.SelectedItem.FromAutoTimeZone
                copy(
                    autoTimeZoneEntry = AutoTimeZoneEntry.Loading(latLng),
                    selectedItem = if (isSelectedItemFromAuto) null else selectedItem,
                    finishStepAvailable = if (isSelectedItemFromAuto) false else finishStepAvailable
                )
            }
            delay(1000L)
            runCatching {
                googleTzRepository.getZoneId(latLng)
            }
                .except<CancellationException, ZoneId>()
                .onSuccess { zoneId ->
                    val currentInstant = Instant.now()
                    val entry = AutoTimeZoneEntry.Ok(
                        latLng,
                        TimeZoneDisplayEntry(zoneId, currentInstant)
                    )
                    reduceState {
                        copy(
                            autoTimeZoneEntry = entry,
                            selectedItem = selectedItem
                                ?: State.SelectedItem.FromAutoTimeZone(entry),
                            finishStepAvailable = true
                        )
                    }
                }
                .onFailure {
                    reduceState {
                        val isSelectedItemFromAuto = state.value.selectedItem is State.SelectedItem.FromAutoTimeZone
                        copy(
                            autoTimeZoneEntry = AutoTimeZoneEntry.Error(latLng, it),
                            selectedItem = if (isSelectedItemFromAuto) null else selectedItem,
                            finishStepAvailable = if (isSelectedItemFromAuto) false else finishStepAvailable
                        )
                    }
                }
        }
    }

}
package uv.index.parts.place.parts.list.ui

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import net.arwix.extension.ConflatedJob
import net.arwix.mvi.SimpleViewModel
import uv.index.parts.place.data.PlaceEditInnerRepository
import uv.index.parts.place.parts.list.data.PlaceListItem
import uv.index.parts.place.parts.list.domain.PlaceListUseCase
import uv.index.parts.place.parts.list.ui.PlaceListContract.Effect
import uv.index.parts.place.parts.list.ui.PlaceListContract.Event
import uv.index.parts.place.parts.list.ui.PlaceListContract.State

class PlaceListViewModel(
    private val placeListUseCase: PlaceListUseCase,
    private val placeEditInnerRepository: PlaceEditInnerRepository
) : SimpleViewModel<Event, State, Effect>(
    State(listOf())
) {
    private val jobLocationUpdateJob = ConflatedJob()

    // https://stackoverflow.com/questions/64721218/jetpack-compose-launch-activityresultcontract-request-from-composable-function
    // https://ngengesenior.medium.com/pick-image-from-gallery-in-jetpack-compose-5fa0d0a8ddaf

    init {
        placeListUseCase.attachSideScope(viewModelScope)
        placeListUseCase.places.map {
            reduceState {
                copy(list = it)
            }
        }
            .launchIn(viewModelScope)
    }

    override fun handleEvents(event: Event) {
        when (event) {
            Event.AddPlace -> {
                placeEditInnerRepository.clearData()
                applyEffect {
                    Effect.ToEdit
                }
            }
            Event.UpdateLocation -> {
                doLocationUpdate(true)
            }
            is Event.UpdateLocationPermission -> {
                if (event.isGrained) doLocationUpdate()
            }
            is Event.SelectItem -> {
                viewModelScope.launch {
                    when (event.item) {
                        is PlaceListItem.Auto -> {
                            placeListUseCase.selectAutoItem()
                        }
                        is PlaceListItem.Custom -> {
                            placeListUseCase.selectCustomItem(event.item.place)
                        }
                    }
                }
            }
            is Event.EditItem -> {
                placeEditInnerRepository.editData(event.item.place)
                applyEffect {
                    Effect.ToEdit
                }
            }
            is Event.DeleteItem -> {
                viewModelScope.launch {
                    placeListUseCase.deleteItem(event.item.place.id!!)
                }
            }
            is Event.UndoDeleteItem -> {
                viewModelScope.launch {
                    placeListUseCase.undoDeleteItem(event.item)
                }
            }
        }
    }

    private fun doLocationUpdate(isForceUpdateLocation: Boolean = false) {
        jobLocationUpdateJob += viewModelScope.launch {
            placeListUseCase.requestUpdateAutoLocation(isForceUpdateLocation)
        }
    }

}
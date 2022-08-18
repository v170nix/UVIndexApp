package uv.index.features.place.parts.list.ui

import net.arwix.mvi.UIEvent
import net.arwix.mvi.UISideEffect
import net.arwix.mvi.UIState
import uv.index.features.place.parts.list.data.PlaceListItem

object PlaceListContract {
    data class State(
        val list: List<PlaceListItem>
    ) : UIState


    sealed class Event: UIEvent {
        object AddPlace: Event()
        object UpdateLocation: Event()
        data class UpdateLocationPermission(val isGrained: Boolean): Event()
        data class SelectItem(val item: PlaceListItem): Event()
        data class EditItem(val item: PlaceListItem.Custom): Event()
        data class DeleteItem(val item: PlaceListItem.Custom): Event()
        data class UndoDeleteItem(val item: PlaceListItem.Custom): Event()
    }

    sealed class Effect: UISideEffect {
        object ToEdit: Effect()
    }


}
package uv.index.parts.place.parts.list.data

import uv.index.parts.place.data.room.PlaceData

sealed class PlaceListItem(val isSelected: Boolean, val isSelectable: Boolean) {
    data class Auto(val state: State) : PlaceListItem(
        isSelected = run {
            if (state is State.Allow && state.data != null) state.data.isSelected else false
        },
        isSelectable = state is State.Allow && state.data != null
    ) {

        sealed class State {
            object None : State()
            object Denied : State()
            object DeniedRationale : State()
            data class Allow(val data: PlaceData?) : State()
        }
    }

    data class Custom(val place: PlaceData) : PlaceListItem(place.isSelected, true)


}


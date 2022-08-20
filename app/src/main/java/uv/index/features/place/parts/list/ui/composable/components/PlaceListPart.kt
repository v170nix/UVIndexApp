package uv.index.features.place.parts.list.ui.composable.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import net.arwix.mvi.EventHandler
import uv.index.features.place.parts.list.data.PlaceListItem
import uv.index.features.place.parts.list.ui.PlaceListContract

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PlaceListComponent(
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues,
    state: PlaceListContract.State,
    eventHandler: EventHandler<PlaceListContract.Event>,
    onLocationPermission: (isGrained: Boolean) -> Unit,
    onLocationUpdate: () -> Unit,
    onShowUndoSnackbar: (PlaceListItem.Custom) -> Unit
) {
    LazyColumn(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = contentPadding
    ) {
        items(
            items = state.list,
            key = { item -> item.getKey() }
        ) { item ->
            ItemRow(
                modifier = Modifier.animateItemPlacement(),
                item = item,
                onLocationPermission = onLocationPermission,
                onAutoUpdate = onLocationUpdate,
                onSelect = { place ->
                    eventHandler.doEvent(PlaceListContract.Event.SelectItem(place))
                },
                onEdit = {
                    eventHandler.doEvent(PlaceListContract.Event.EditItem(it as PlaceListItem.Custom))
                },
                onDelete = {
                    eventHandler.doEvent(PlaceListContract.Event.DeleteItem(it as PlaceListItem.Custom))
                    onShowUndoSnackbar(it)
                }
            )
        }
    }
}
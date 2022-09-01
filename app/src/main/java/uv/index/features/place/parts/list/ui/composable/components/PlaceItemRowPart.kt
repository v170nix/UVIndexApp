package uv.index.features.place.parts.list.ui.composable.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.selection.selectable
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import uv.index.features.place.parts.list.data.PlaceListItem

@Composable
internal fun ItemRow(
    modifier: Modifier = Modifier,
    item: PlaceListItem,
    onLocationPermission: (isGrained: Boolean) -> Unit,
    onAutoUpdate: () -> Unit,
    selectedContainerColor: Color = MaterialTheme.colorScheme.primaryContainer,
    selectedContentColor: Color = MaterialTheme.colorScheme.onPrimaryContainer,
    onSelect: (PlaceListItem) -> Unit,
    onEdit: (PlaceListItem) -> Unit,
    onDelete: (PlaceListItem) -> Unit
) {

    val backgroundColor by animateColorAsState(
        if (item.isSelected) selectedContainerColor else MaterialTheme.colorScheme.surfaceVariant
    )

    val textColor by animateColorAsState(
        if (item.isSelected) selectedContentColor else MaterialTheme.colorScheme.onSurfaceVariant
    )

    Card(
        modifier = modifier
            .selectable(
                enabled = item.isSelectable,
                selected = item.isSelected,
                onClick = {
                    onSelect(item)
                },
                role = Role.Button
            )
            .fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = backgroundColor,
            contentColor = textColor
        ),
//        backgroundColor = backgroundColor

    ) {
        when (item) {
            is PlaceListItem.Auto -> {
                when (item.state) {
                    is PlaceListItem.Auto.State.Allow -> {
                        if (item.state.data != null) {
                            AutoAllowRow(item.state.data, onAutoUpdate)
                        } else {
                            AutoAllowButNotDataRow(onAutoUpdate)
                        }
                    }
                    PlaceListItem.Auto.State.Denied -> AutoDeniedRow(onLocationPermission)
                    PlaceListItem.Auto.State.DeniedRationale -> AutoDeniedRow(onLocationPermission)
                    PlaceListItem.Auto.State.None -> AutoDeniedRow(onLocationPermission)
                }
            }
            is PlaceListItem.Custom -> CustomRow(
                item.place,
                onEditItem = {
                    onEdit(item)
                },
                onDeleteItem = {
                    onDelete(item)
                }
            )
        }
    }
}
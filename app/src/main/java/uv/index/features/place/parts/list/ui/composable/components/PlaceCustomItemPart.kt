package uv.index.features.place.parts.list.ui.composable.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import uv.index.features.place.common.getGmtOffsetText
import uv.index.features.place.data.room.PlaceData

@Composable
internal fun CustomRow(
    data: PlaceData,
    onEditItem: () -> Unit,
    onDeleteItem: () -> Unit
) {
    Column(
        Modifier.padding(
            top = 16.dp,
            bottom = 8.dp,
            start = 8.dp,
            end = 4.dp,
        )
    ) {
        BodyPart(
            title = data.name ?: "",
            subTitle = data.subName ?: "",
            latLng = data.latLng,
            zoneId = data.zone
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 8.dp),
                style = MaterialTheme.typography.bodyMedium,
                text = data.zone.getGmtOffsetText()
            )
            IconButton(onClick = onEditItem) {
                Icon(Icons.Filled.Edit, "edit")
            }
            IconButton(onClick = onDeleteItem) {
                Icon(Icons.Filled.Delete, "delete")
            }
        }
    }
}
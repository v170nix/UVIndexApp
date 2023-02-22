package uv.index.features.main.ui.composable.sections.dataview.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import uv.index.features.main.common.getUVIColor
import uv.index.features.main.ui.MainContract
import uv.index.features.place.data.room.PlaceData


@Composable
fun UVForecastHoursPart(
    modifier: Modifier = Modifier,
    place: PlaceData?,
    hoursList: List<MainContract.UVHourData>,
) {

    val listState = rememberSaveable(
        place,
        key = place?.latLng?.toString(),
        saver = listSaver(
            save = {
                listOf(
                    it.firstVisibleItemIndex,
                    it.firstVisibleItemScrollOffset,
                    place?.latLng
                )
            },
            restore = {
                runCatching {
                    if (it[2] == place?.latLng) {
                        LazyListState(
                            firstVisibleItemIndex = it[0] as Int,
                            firstVisibleItemScrollOffset = it[1] as Int
                        )
                    } else {
                        LazyListState(
                            firstVisibleItemIndex = 0,
                            firstVisibleItemScrollOffset = 0
                        )
                    }
                }.getOrDefault(
                    LazyListState(
                        firstVisibleItemIndex = 0,
                        firstVisibleItemScrollOffset = 0
                    )
                )
            }
        ),
    ) {
        LazyListState(0, 0)
    }

    LazyRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
        state = listState
    ) {

        item {
            Spacer(Modifier.width(8.dp))
        }

        items(hoursList) { item ->
            when (item) {
                MainContract.UVHourData.Divider -> Divider(
                    modifier = Modifier
                        .height(32.dp)
                        .width(1.dp),
                    color = MaterialTheme.colorScheme.onBackground
                )
                is MainContract.UVHourData.Item -> HourBox(
                    bgColor = getUVIColor(item.iIndex, Color.Transparent),
                    index = item.sIndex,
                    hour = item.time
                )
            }
        }

        item {
            Spacer(Modifier.width(8.dp))
        }
    }
}

@Composable
private fun HourBox(
    bgColor: Color,
    index: String,
    hour: String
) {
    Card(
        modifier = Modifier.aspectRatio(1f),
        colors = CardDefaults.cardColors(
            containerColor = bgColor, //Color(0xFFE53935),
            contentColor = Color.White
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = index,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = hour,
                    style = MaterialTheme.typography.titleSmall
                )
            }
        }
    }

}
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
import uv.index.ui.theme.Dimens


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
        horizontalArrangement = Arrangement.spacedBy(Dimens.grid_1),
        verticalAlignment = Alignment.CenterVertically,
        state = listState
    ) {

        item {
            Spacer(Modifier.width(Dimens.grid_1))
        }

        items(hoursList) { item ->
            when (item) {
                MainContract.UVHourData.Divider -> Divider(
                    modifier = Modifier
                        .height(Dimens.grid_4)
                        .width(1.dp),
                    color = MaterialTheme.colorScheme.onBackground
                )
                is MainContract.UVHourData.Item -> HourBox(
                    bgColor = getUVIColor(item.iIndex, Color.Transparent),
                    index = item.sIndex,
                    hour = item.timeText
                )
            }
        }

        item {
            Spacer(Modifier.width(Dimens.grid_1))
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
                .widthIn(min = 64.dp)
                .padding(Dimens.grid_1),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(Dimens.grid_0_5)
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
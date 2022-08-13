package uv.index.parts.main.ui.composable

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import uv.index.lib.data.UVIndexData
import uv.index.parts.main.common.getUVIColor
import java.time.Instant
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import kotlin.math.roundToInt

@Immutable
private sealed class UIData {

    @Immutable
    data class Item(
        val sIndex: String,
        val iIndex: Int,
        val time: String
    ) : UIData()

    object Divider : UIData()
}


@Composable
fun MainForecastHoursPart(
    modifier: Modifier = Modifier,
    currentDateTime: ZonedDateTime?,
    hoursList: List<UVIndexData>?,
) {

    var visibleList: List<UIData> by remember(Unit) {
        mutableStateOf(listOf())
    }

    LaunchedEffect(currentDateTime, hoursList) {
        currentDateTime ?: return@LaunchedEffect
        hoursList ?: return@LaunchedEffect
        val firstTime = currentDateTime.toEpochSecond() - 3600L
        visibleList = withContext(Dispatchers.IO) {
            hoursList
                .asSequence()
                .filter { it.time > firstTime }
                .take(24)
                .flatMap {
                    sequence {
                        val zdt = Instant
                            .ofEpochSecond(it.time)
                            .atZone(currentDateTime.zone)

                        if (zdt.hour < 1) yield(UIData.Divider)

                        val index1 = (it.value * 10).roundToInt() / 10.0

                        yield(
                            UIData.Item(
                                sIndex = index1.toString(),
                                iIndex = index1.roundToInt(),
                                time = zdt
                                    .toLocalTime()
                                    .format(DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT))
                            )
                        )
                    }
                }
                .toList()
        }
    }

    LazyRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        item {
            Spacer(Modifier.width(8.dp))
        }

        items(visibleList) { item ->
            when (item) {
                UIData.Divider -> Divider(
                    modifier = Modifier.height(32.dp).width(1.dp),
                    color = MaterialTheme.colorScheme.onBackground
                )
                is UIData.Item -> HourBox(
                    bgColor = getUVIColor(item.iIndex),
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
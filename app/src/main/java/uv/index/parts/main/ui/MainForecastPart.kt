package uv.index.parts.main.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import uv.index.lib.data.UVSummaryDayData
import uv.index.parts.main.common.rememberPeriod
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

@OptIn(ExperimentalAnimationApi::class)
@Composable
internal fun MainForecastPart(
    modifier: Modifier = Modifier,
    data: List<UVSummaryDayData>
) {

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
//        Text(
//            text = "Прогноз",
//            style = MaterialTheme.typography.displaySmall
//        )
//
//        Spacer(modifier = Modifier.height(8.dp))

        AnimatedContent(targetState = data) { currentData ->
            ForecastItemsPart(currentData)
        }
        Spacer(modifier = Modifier.height(8.dp))
    }

}

@Composable
private fun ForecastItemsPart(list: List<UVSummaryDayData>) {
    Column {
        list.forEachIndexed { index, item ->
            ForecastContentItem(item)
            if (index < list.size - 1) {
//                Divider(modifier = Modifier.fillMaxWidth(), color = Color.LightGray)
                Spacer(Modifier.height(16.dp))
            }
        }
    }
}

@Composable
private fun ForecastContentItem(
    item: UVSummaryDayData
) {
    val dayText by remember(item.day) {
        derivedStateOf {
            item.day.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT))
        }
    }

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(
            modifier = Modifier,
            text = dayText,
            style = MaterialTheme.typography.titleMedium,
            textAlign = TextAlign.Center
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            when (val it = rememberPeriod(item)) {
                null -> {
                    Text(
                        modifier = Modifier.weight(1f),
                        text = "protection not required"
                    )
                }
                else -> {
                    Text(
                        modifier = Modifier.weight(1f),
                        text = "item_protection_required"
                    )
                }
            }

            Text(
                modifier = Modifier
//                    .background(
//                        color = getUVIColor(index = item.maxIndex.getIntIndex()),
//                        shape = MaterialTheme.shapes.small
//                    )
                    .widthIn(min = 64.dp)
                    .padding(8.dp),
                text = item.maxIndex.getIntIndex().toString(),
//                color = WeatherTheme.colors.contentColorFor(getUVIColor(index = item.maxIndex.getIntIndex())),
                textAlign = TextAlign.Center,
            )
        }
    }

}
package uv.index.features.main.ui.composable.sections.dataview.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import uv.index.R
import uv.index.features.main.common.getUVIColor
import uv.index.features.main.common.rememberPeriod
import uv.index.lib.data.UVSummaryDayData
import uv.index.ui.theme.Dimens
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

@OptIn(ExperimentalAnimationApi::class)
@Composable
internal fun UVForecastDayPart(
    modifier: Modifier = Modifier,
    data: List<UVSummaryDayData>?,
    titleStyle: TextStyle = LocalTextStyle.current,
    textStyle: TextStyle = LocalTextStyle.current
) {

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AnimatedContent(targetState = data) { currentData ->
            if (currentData != null) ForecastItemsPart(
                titleStyle,
                textStyle,
                currentData
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
    }

}

@Composable
private fun ForecastItemsPart(
    titleStyle: TextStyle,
    textStyle: TextStyle,
    list: List<UVSummaryDayData>,
) {
    Column {
        list.forEachIndexed { index, item ->
            ForecastContentItem(titleStyle, textStyle, item)
            if (index < list.size - 1) {
                Spacer(Modifier.height(16.dp))
            }
        }
    }
}

@Composable
private fun ForecastContentItem(
    titleStyle: TextStyle,
    textStyle: TextStyle,
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
            style = titleStyle,
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
                        text = stringResource(id =  R.string.uvindex_forecast_item_protection_not_required),
                        style = textStyle
                    )
                }
                else -> {
                    Text(
                        modifier = Modifier.weight(1f),
                        text = stringResource(
                            id = R.string.uvindex_forecast_item_protection_required,
                            it.first, it.second
                        ),
                        style = textStyle
                    )
                }
            }

            Box(
                modifier = Modifier
                    .widthIn(min = 64.dp)
                    .background(
                        color = getUVIColor(index = item.maxIndex.getIntIndex(), Color.White),
                        shape = MaterialTheme.shapes.small
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    modifier = Modifier.padding(Dimens.grid_1),
                    text = item.maxIndex.getIntIndex().toString(),
                    textAlign = TextAlign.Center,
                    color = Color.White
                )
            }


        }
    }

}
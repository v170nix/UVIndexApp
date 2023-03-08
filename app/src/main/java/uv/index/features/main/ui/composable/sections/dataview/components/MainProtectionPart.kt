package uv.index.features.main.ui.composable.sections.dataview.components

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import uv.index.R
import uv.index.common.AutoSizeText
import uv.index.features.main.common.rememberPeriod
import uv.index.features.main.ui.MainContract
import uv.index.features.uvi.data.UVLevel
import uv.index.lib.data.UVSummaryDayData
import uv.index.ui.theme.Dimens
import uv.index.ui.theme.UVITheme
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

@Composable
@Suppress("MagicNumber")
internal fun MainProtectionPart(
    modifier: Modifier = Modifier,
    titleStyle: TextStyle = MaterialTheme.typography.labelLarge,
    textStyle: TextStyle = MaterialTheme.typography.bodyMedium,
    currentZdt: ZonedDateTime,
    uvSummaryDayData: UVSummaryDayData?,
    currentDayHours: List<MainContract.UVHourData>
) {

    val stringZDT by remember(currentZdt) {
        derivedStateOf {
            currentZdt.format(
                DateTimeFormatter.ofLocalizedDateTime(
                    FormatStyle.MEDIUM,
                    FormatStyle.SHORT
                )
            )
        }
    }


    Column(
        modifier = modifier,
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Icon(
                imageVector = ImageVector.vectorResource(id = R.drawable.ic_shield),
                contentDescription = null
            )
            Crossfade(
                targetState = rememberPeriod(data = uvSummaryDayData)
            ) {
                Column(
                    modifier = Modifier.padding(horizontal = Dimens.grid_0_5),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    when (it) {
                        null -> {
                            Text(
                                text = stringResource(id = R.string.uvindex_current_protection_not_required),
                                style = titleStyle,
                                textAlign = TextAlign.Center
                            )
                        }
                        else -> {

                            Text(
                                text = stringResource(id = R.string.uvindex_current_protection_required_first_part),
                                style = titleStyle
                            )

                            Box(
                                modifier = Modifier.fillMaxWidth(0.7f),
                                contentAlignment = Alignment.Center
                            ) {
                                AutoSizeText(
                                    text = stringResource(
                                        id = R.string.uvindex_current_protection_required_second_part,
                                        it.first,
                                        it.second
                                    ),
                                    textStyle = textStyle
                                )
                            }
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.width(48.dp))
        }

        Spacer(modifier = Modifier.height(Dimens.grid_2))

        LinearProtectionPart(
            modifier = Modifier
                .fillMaxWidth(),
            currentZdt = currentZdt,
            currentDayHours = currentDayHours,
            content = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.Center),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        modifier = Modifier,
                        text = stringResource(id = R.string.uvindex_local_time_text),
                        style = titleStyle
                    )

                    Text(
                        text = stringZDT,
                        style = textStyle
                    )
                }
            }
        )
    }


}

@Composable
private fun LinearProtectionPart(
    modifier: Modifier = Modifier,
    currentZdt: ZonedDateTime,
    currentDayHours: List<MainContract.UVHourData>,
    mainLineHeight: Dp = 8.dp,
    bottomPadding: Dp = 36.dp,
    content: @Composable BoxScope.() -> Unit = {}
) {
    val uviColors = UVITheme.colors

    val colors by remember(uviColors, currentDayHours) {
        derivedStateOf {
            val innerList = currentDayHours
                .filterIsInstance<MainContract.UVHourData.Item>()
            val count = innerList.size.toFloat()
            if (count < 2) {
                return@derivedStateOf arrayOf(
                    0f to uviColors.night,
                    1f to uviColors.night
                )
            }
            innerList
                .mapIndexed { index, uvHourData ->
                    val color = when (uvHourData.iIndex) {
                        -2 -> uviColors.night
                        -1 -> uviColors.night
                        else -> {
                            when (UVLevel.valueOf(uvHourData.iIndex)) {
                                UVLevel.Low -> uviColors.lowUV
                                UVLevel.Moderate -> uviColors.moderateUV
                                UVLevel.High -> uviColors.highUV
                                UVLevel.VeryHigh -> uviColors.veryHighUV
                                UVLevel.Extreme -> uviColors.extremeUV
                                else -> Color.Transparent
                            }
                        }
                    }
                    index / count to color
                }
                .toTypedArray()
        }
    }

    BoxWithConstraints(
        modifier = modifier
    ) {

        val delta by remember(maxWidth, currentZdt) {
            derivedStateOf {
                val dayPart = currentZdt.toLocalTime().toSecondOfDay() / 60.0 / 60.0 / 24.0
                maxWidth * dayPart.toFloat()
            }
        }

        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .height(mainLineHeight)
                .background(brush = Brush.horizontalGradient(*colors))
        )

        val paddingValues by remember(maxWidth, delta) {
            derivedStateOf {
                if (maxWidth - delta <= maxWidth / 2f) {
                    PaddingValues(start = maxWidth / 2f, end = maxWidth - delta - 1.dp)
                } else {
                    PaddingValues(start = delta, end = maxWidth / 2f)
                }
            }
        }

        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .padding(paddingValues)
                .padding(
                    top = mainLineHeight + bottomPadding / 2
                )
                .height(1.dp)
                .background(MaterialTheme.colorScheme.onSurface)
        )

        Spacer(
            modifier = Modifier
                .padding(start = delta)
                .height(mainLineHeight + bottomPadding / 2)
                .width(1.dp)
                .background(MaterialTheme.colorScheme.onSurface)
        )

        Spacer(
            modifier = Modifier
                .padding(
                    start = maxWidth / 2f,
                    top = mainLineHeight + bottomPadding / 2
                )
                .height(bottomPadding / 2)
                .width(1.dp)
                .background(MaterialTheme.colorScheme.onSurface)
        )

        Box(
            modifier = Modifier
                .padding(
                    top = mainLineHeight + bottomPadding
                )
        ) {
            content()
        }

    }
}
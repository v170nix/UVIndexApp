package uv.index.parts.main.common

import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import uv.index.common.LifecycleTimer
import uv.index.lib.data.UVIPlaceData
import uv.index.lib.data.UVSummaryDayData
import uv.index.ui.theme.UVITheme
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

@Composable
internal fun getUVIColor(index: Int): Color {
    return when (index) {
        in 0..2 -> UVITheme.colors.lowUV
        in 3..5 -> UVITheme.colors.moderateUV
        in 6..7 -> UVITheme.colors.highUV
        in 7..10 -> UVITheme.colors.veryHighUV
        in 11..Int.MAX_VALUE -> UVITheme.colors.extremeUV
        else -> UVITheme.colors.lowUV
    }
}

@Composable
internal fun rememberPeriod(data: UVSummaryDayData?): Pair<String, String>? {
    val period by remember(data) {
        derivedStateOf {
            data ?: return@derivedStateOf null
            val begin = data.timeProtectionBegin ?: return@derivedStateOf null
            val end = data.timeProtectionEnd ?: return@derivedStateOf null
            begin.format(periodFormatter) to
                    end.format(periodFormatter)
        }
    }
    return period
}

private val periodFormatter by lazy {
    DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT)
}

@Composable
internal fun rememberCurrentZonedDateTime(
    place: UVIPlaceData?,
    updateTimeInMillis: Long = 1000L
): ZonedDateTime? {
    var timer: Int by remember {
        mutableStateOf(0)
    }

    val zdt: ZonedDateTime? by remember(place, timer) {
        derivedStateOf {
            val zoneId = place?.zone ?: return@derivedStateOf null
            ZonedDateTime.now(zoneId)
        }
    }

    LifecycleTimer(timeMillis = updateTimeInMillis) { timer++ }

    return zdt
}
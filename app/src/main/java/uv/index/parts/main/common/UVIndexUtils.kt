package uv.index.parts.main.common

import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import uv.index.common.LifecycleTimer
import uv.index.lib.data.UVIPlaceData
import uv.index.lib.data.UVSummaryDayData
import uv.index.lib.data.getCurrentIndex
import uv.index.parts.main.domain.SunPosition
import uv.index.parts.main.ui.MainContract
import uv.index.ui.theme.UVITheme
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import kotlin.math.roundToInt


@Composable
internal fun rememberCurrentIndexValue(
    currentDateTime: ZonedDateTime?,
    state: MainContract.State,
): State<Int?> {

    return remember(state.currentDayData, currentDateTime) {
        derivedStateOf {
            val time = currentDateTime ?: return@derivedStateOf null
            state.currentDayData?.getCurrentIndex(time.hour + time.minute / 60.0)?.roundToInt()
        }
    }
}

internal fun getUVITitle(index: Int, sunPosition: SunPosition,  array: Array<String>): String {
    return when (sunPosition) {
        SunPosition.Night -> array[0]
        SunPosition.Twilight -> array[1]
        SunPosition.Above -> {
            when (index) {
                in 0..2 -> array[2]
                in 3..5 -> array[3]
                in 6..7 -> array[4]
                in 7..10 -> array[5]
                in 11..Int.MAX_VALUE -> array[6]
                else -> ""
            }
        }
    }
}

@Composable
internal fun getUVIColor(index: Int, sunPosition: SunPosition, transparentColor: Color): Color {
    return when (sunPosition) {
        SunPosition.Twilight -> UVITheme.colors.twilight
        SunPosition.Night -> UVITheme.colors.night
        SunPosition.Above -> {
            when (index) {
                in 0..2 -> UVITheme.colors.lowUV
                in 3..5 -> UVITheme.colors.moderateUV
                in 6..7 -> UVITheme.colors.highUV
                in 7..10 -> UVITheme.colors.veryHighUV
                in 11..Int.MAX_VALUE -> UVITheme.colors.extremeUV
                else -> transparentColor
            }
        }
    }
}

@Composable
internal fun getUVIColor(index: Int, transparentColor: Color): Color {
    return when (index) {
        -2 -> UVITheme.colors.night
        -1 -> UVITheme.colors.twilight
        in 0..2 -> UVITheme.colors.lowUV
        in 3..5 -> UVITheme.colors.moderateUV
        in 6..7 -> UVITheme.colors.highUV
        in 7..10 -> UVITheme.colors.veryHighUV
        in 11..Int.MAX_VALUE -> UVITheme.colors.extremeUV
        else -> transparentColor
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
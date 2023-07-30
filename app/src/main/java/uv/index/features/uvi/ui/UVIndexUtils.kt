package uv.index.features.uvi.ui

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import uv.index.R
import uv.index.features.astronomy.data.SunPosition
import uv.index.features.main.ui.MainContract
import uv.index.features.uvi.data.UVLevel
import uv.index.lib.data.UVSummaryDayData
import uv.index.ui.theme.UVITheme
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import kotlin.math.roundToInt

@Suppress("MagicNumber")
internal fun getUVITitle(index: Int, sunPosition: SunPosition, array: Array<String>): String {
    return when (sunPosition) {
        SunPosition.Night -> array[0]
        SunPosition.Twilight -> array[1]
        SunPosition.Above -> {
            when (UVLevel.valueOf(index)) {
                UVLevel.Low -> array[2]
                UVLevel.Moderate -> array[3]
                UVLevel.High -> array[4]
                UVLevel.VeryHigh -> array[5]
                UVLevel.Extreme -> array[6]
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
            when (UVLevel.valueOf(index)) {
                UVLevel.Low -> UVITheme.colors.lowUV
                UVLevel.Moderate -> UVITheme.colors.moderateUV
                UVLevel.High -> UVITheme.colors.highUV
                UVLevel.VeryHigh -> UVITheme.colors.veryHighUV
                UVLevel.Extreme -> UVITheme.colors.extremeUV
                else -> transparentColor
            }
        }
    }
}

@Composable
@Suppress("MagicNumber")
internal fun getUVIColor(index: Int, transparentColor: Color): Color {
    return when (index) {
        -2 -> UVITheme.colors.night
        -1 -> UVITheme.colors.twilight
        else -> {
            when (UVLevel.valueOf(index)) {
                UVLevel.Low -> UVITheme.colors.lowUV
                UVLevel.Moderate -> UVITheme.colors.moderateUV
                UVLevel.High -> UVITheme.colors.highUV
                UVLevel.VeryHigh -> UVITheme.colors.veryHighUV
                UVLevel.Extreme -> UVITheme.colors.extremeUV
                else -> transparentColor
            }
        }
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

@Composable
internal fun rememberTimeToBurnString(timeToBurn: MainContract.TimeToEvent?): String {
    val context = LocalContext.current
    val timeToBurnString by remember(timeToBurn, context) {
        derivedStateOf {
            when (timeToBurn) {
                MainContract.TimeToEvent.Infinity -> "∞"
                is MainContract.TimeToEvent.Value -> {
                    buildString {
                        timeToString(
                            context,
                            (timeToBurn.minTimeInMins + (timeToBurn.maxTimeInMins
                                ?: (1.5 * timeToBurn.minTimeInMins)).toInt()) / 2
                        )
                    }
                }
                else -> ""
            }
        }
    }
    return timeToBurnString
}

@Composable
internal fun rememberTimeToVitaminDString(timeToVitaminD: MainContract.TimeToEvent?): String {
    val context = LocalContext.current
    val timeToVitaminDString by remember(timeToVitaminD, context) {
        derivedStateOf {
            when (timeToVitaminD) {
                MainContract.TimeToEvent.Infinity -> "∞"
                is MainContract.TimeToEvent.Value -> {
                    val time = (timeToVitaminD.minTimeInMins + (timeToVitaminD.maxTimeInMins
                        ?: (1.5 * timeToVitaminD.minTimeInMins)).toInt()) / 2
                    var roundTime = (time / 5.0).roundToInt() * 5
//                    time = (time / 5) * 5
                    if (time < 5) roundTime = time
                    buildString {
                        timeToString(context, roundTime)
                    }
                }
                else -> ""
            }
        }
    }
    return timeToVitaminDString
}


private fun StringBuilder.timeToString(context: Context, time: Int): StringBuilder {
    val (hourPart, minPart) = timeInMinsToHHMM(time)
    if (hourPart > 0) {
        append(hourPart)
        append(" ")
        append(context.getString(R.string.uvindex_sunburn_hour_part))
        append(" ")
    }
    if (minPart > 0) {
        append(minPart)
        append(" ")
        append(context.getString(R.string.uvindex_sunburn_min_part))
    }

    return this
}

private fun timeInMinsToHHMM(time: Int): Pair<Int, Int> {
    return getHourPart(time) to getMinPart(time)
}

private fun getMinPart(duration: Int) = duration % 60
private fun getHourPart(duration: Int) = duration / 60


private val periodFormatter by lazy {
    DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT)
}
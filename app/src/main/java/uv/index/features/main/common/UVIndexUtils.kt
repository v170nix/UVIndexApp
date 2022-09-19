package uv.index.features.main.common

import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import uv.index.features.main.data.SunPosition
import uv.index.features.main.data.UVLevel
import uv.index.lib.data.UVSummaryDayData
import uv.index.ui.theme.UVITheme
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

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

private val periodFormatter by lazy {
    DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT)
}
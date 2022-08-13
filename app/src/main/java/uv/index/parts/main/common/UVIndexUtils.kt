package uv.index.parts.main.common

import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import uv.index.lib.data.UVSummaryDayData
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

@Composable
fun rememberPeriod(data: UVSummaryDayData): Pair<String, String>? {
    val period by remember(data) {
        derivedStateOf {
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

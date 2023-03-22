package uv.index.features.weather.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringArrayResource
import uv.index.R
import uv.index.ui.theme.UVIColors
import uv.index.ui.theme.UVITheme

@Composable
fun getAQIColor(epaIndex: Int?, transparentColor: Color): Color {
    return when (epaIndex) {
        1 -> UVITheme.colors.lowUV
        2 -> UVITheme.colors.moderateUV
        3 -> UVITheme.colors.highUV
        4 -> UVITheme.colors.veryHighUV
        5 -> UVITheme.colors.extremeUV
        6 -> Color.Magenta
        else -> transparentColor
    }
}

@Suppress("unused")
fun UVIColors.getAQIColor(epaIndex: Int?, transparentColor: Color): Color {
    return when (epaIndex) {
        1 -> lowUV
        2 -> moderateUV
        3 -> highUV
        4 -> veryHighUV
        5 -> extremeUV
        6 -> Color.Magenta
        else -> transparentColor
    }
}

@Suppress("MagicNumber")
fun getAQIText(epaIndex: Int?, array: Array<String>): String {
    return when (epaIndex) {
        in 1..6 -> array[epaIndex!! - 1]
        else -> ""
    }
}

@Composable
fun rememberAQIText(epaIndex: Int?): String {
    val array = stringArrayResource(id = R.array.uvindex_weather_aqi_index_text)
    val text by remember(epaIndex) {
        derivedStateOf {
            epaIndex ?: return@derivedStateOf ""
            getAQIText(epaIndex, array)
        }
    }
    return text
}

@Composable
fun rememberAQIDescription(epaIndex: Int?): String {
    val array = stringArrayResource(id = R.array.uvindex_weather_aqi_description)
    val text by remember(epaIndex) {
        derivedStateOf {
            epaIndex ?: return@derivedStateOf ""
            getAQIText(epaIndex, array)
        }
    }
    return text
}
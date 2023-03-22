package uv.index.features.uvi.ui

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import uv.index.R
import uv.index.features.astronomy.data.SunPosition
import uv.index.features.main.ui.MainContract
import uv.index.ui.theme.Dimens
import kotlin.math.roundToInt

@Composable
fun UVTitle(
    modifier: Modifier = Modifier,
    style: TextStyle = LocalTextStyle.current,
    uvCurrentData: MainContract.UVCurrentData?,
    sunPosition: SunPosition,
    onClick: () -> Unit,
) {
    if (uvCurrentData == null) return
    val context = LocalContext.current
    val currentIndex by remember(uvCurrentData.index) {
        derivedStateOf {
            uvCurrentData.index?.roundToInt() ?: 0
        }
    }

    @Suppress("NAME_SHADOWING")
    val titleString by remember(
        uvCurrentData.index,
        sunPosition,
        context
    ) {
        derivedStateOf {
            val currentIndex = uvCurrentData.index?.roundToInt() ?: Int.MIN_VALUE
            val array = context.resources.getStringArray(R.array.uvindex_status_info)
            getUVITitle(currentIndex, sunPosition, array)
        }
    }

    TextButton(
        colors = ButtonDefaults.textButtonColors(contentColor = LocalContentColor.current),
        modifier = modifier,
        contentPadding = PaddingValues(0.dp),
        onClick = {
            if (currentIndex > 2) onClick()
        },
        enabled = currentIndex > 2

    ) {
        Text(
            modifier = Modifier.padding(horizontal = Dimens.grid_2),
            text = titleString,
            style = style
        )
    }
}
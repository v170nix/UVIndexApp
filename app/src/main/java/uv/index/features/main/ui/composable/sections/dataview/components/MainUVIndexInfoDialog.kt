package uv.index.features.main.ui.composable.sections.dataview.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import uv.index.R
import uv.index.features.main.common.getUVITitle
import uv.index.features.main.domain.SunPosition
import kotlin.math.roundToInt

@Stable
class MainUVIndexInfoDialogState {
    var isShow by mutableStateOf(false)
    var currentIndex by mutableStateOf(0)
}

@Composable
fun rememberUVIndexInfoDialogState(currentIndex: Double?): MainUVIndexInfoDialogState {
    val state = remember(Unit) {
        MainUVIndexInfoDialogState()
    }

    LaunchedEffect(currentIndex) {
        state.currentIndex = currentIndex?.roundToInt() ?: Int.MIN_VALUE
    }

    return state
}

@Composable
fun MainUVIndexInfoDialog(
    state: MainUVIndexInfoDialogState
) {

    if (state.isShow) {

        val context = LocalContext.current

        @Suppress("NAME_SHADOWING")
        val titleString by remember(
            state.currentIndex,
            context
        ) {
            derivedStateOf {
                val currentIndex = state.currentIndex
                val array = context.resources.getStringArray(R.array.uvindex_status_info)
                getUVITitle(currentIndex, SunPosition.Above, array)
            }
        }

        val drawableArray by remember(Unit) {
            mutableStateOf(
                arrayOf(
                    R.drawable.ic_glasses to 48,
                    R.drawable.ic_sunblock_alt to 36,
                    R.drawable.ic_hat to 48,
                    R.drawable.ic_shirt to 40,
                    R.drawable.beach_shadow to 40

                )
            )
        }

        val dataCount by remember(state.currentIndex) {
            derivedStateOf {
                when (state.currentIndex) {
                    in 3..4 -> 3
                    5 -> 4
                    in 6..20 -> 5
                    else -> 0
                }
            }
        }

        MainInfoDialog(
            headline = titleString,
            onDismissRequest = {
                state.isShow = false
            },
        ) {

            items(dataCount) { i ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        modifier = Modifier.size(drawableArray[i].second.dp),
                        tint = MaterialTheme.colorScheme.onSurface,
                        painter = painterResource(id = drawableArray[i].first),
                        contentDescription = ""
                    )
                }
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = stringArrayResource(id = R.array.uvindex_alert_info_headline)[i],
                    style = MaterialTheme.typography.titleMedium,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = stringArrayResource(id = R.array.uvindex_alert_info_text)[i],
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center
                )

                if (i < dataCount - 1) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Divider(modifier = Modifier)
                }
            }

        }
    }

}
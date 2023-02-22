package uv.index.features.main.ui.composable.sections.dataview.components

import androidx.compose.runtime.*
import kotlin.math.roundToInt

@Stable
class UVIndexInfoDialogState {
    var isShow by mutableStateOf(false)
    var currentIndex by mutableStateOf(0)
}

@Composable
fun rememberUVIndexInfoDialogState(currentIndex: Double?): UVIndexInfoDialogState {
    val state = remember(Unit) {
        UVIndexInfoDialogState()
    }

    LaunchedEffect(currentIndex) {
        state.currentIndex = currentIndex?.roundToInt() ?: Int.MIN_VALUE
    }

    return state
}

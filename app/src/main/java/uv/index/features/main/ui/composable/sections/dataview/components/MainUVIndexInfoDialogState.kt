package uv.index.features.main.ui.composable.sections.dataview.components

import androidx.compose.runtime.*
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

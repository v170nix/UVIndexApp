package uv.index.features.main.ui.composable.sections.dataview.components

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import uv.index.features.main.ui.MainContract

@Composable
fun ViewModeSwitcher(
    modifier: Modifier = Modifier,
    mode: MainContract.ViewMode,
    uv: @Composable () -> Unit,
    weather: @Composable () -> Unit
) {
    Box(
        modifier = modifier
    ) {
        if (mode == MainContract.ViewMode.Weather) weather() else uv()
    }
}
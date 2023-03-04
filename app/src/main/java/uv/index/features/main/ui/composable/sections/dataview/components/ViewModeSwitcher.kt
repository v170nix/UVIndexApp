package uv.index.features.main.ui.composable.sections.dataview.components

import androidx.compose.runtime.Composable
import uv.index.features.main.ui.MainContract

@Composable
fun ViewModeSwitcher(
    mode: MainContract.ViewMode,
    uv: @Composable () -> Unit,
    weather: @Composable () -> Unit
) {
    if (mode == MainContract.ViewMode.Weather) weather()
    else uv()
}
package uv.index.features.weather.ui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier

@OptIn(ExperimentalFoundationApi::class)
fun LazyListScope.weatherPart() {

    item(key = 21) {
        Text("weather part", modifier = Modifier.animateItemPlacement())
    }
}
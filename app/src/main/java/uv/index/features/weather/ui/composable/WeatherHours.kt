package uv.index.features.weather.ui.composable

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import uv.index.ui.theme.Dimens
import java.time.LocalTime
import java.time.format.DateTimeFormatter

@Composable
fun WeatherHours(
    modifier: Modifier = Modifier,
    content: LazyListScope.() -> Unit
) {
    LazyRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(Dimens.grid_1),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        content()
    }

}



@Composable
internal fun LazyItemScope.HourTextPart(
    hour: LocalTime,
    formatter: DateTimeFormatter
) {
    Text(
        text = hour.format(formatter),
        style = MaterialTheme.typography.labelSmall,
        textAlign = TextAlign.Center
    )
}
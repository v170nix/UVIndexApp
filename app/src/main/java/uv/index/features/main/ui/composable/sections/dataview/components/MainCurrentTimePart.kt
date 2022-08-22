package uv.index.features.main.ui.composable.sections.dataview.components

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

@Composable
internal fun MainCurrentTimePart(
    modifier: Modifier = Modifier,
    currentZdt: ZonedDateTime?
) {
    val stringZDT by remember(currentZdt) {
        derivedStateOf {
            currentZdt ?: return@derivedStateOf ""
            currentZdt.format(
                DateTimeFormatter.ofLocalizedDateTime(
                    FormatStyle.MEDIUM,
                    FormatStyle.SHORT
                )
            )
        }
    }

    Text(
        modifier = modifier,
        text = stringZDT,
        style = MaterialTheme.typography.titleMedium
    )
}
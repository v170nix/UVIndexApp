package uv.index.features.main.ui.composable.sections.dataview.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.LastBaseline
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

@Composable
internal fun MainPlacePart(
    modifier: Modifier = Modifier,
    currentDateTime: ZonedDateTime?,
) {

    val stringZDT by remember(currentDateTime) {
        derivedStateOf {
            currentDateTime ?: return@derivedStateOf ""
            currentDateTime
                .format(
                    DateTimeFormatter.ofLocalizedDateTime(
                        FormatStyle.MEDIUM,
                        FormatStyle.SHORT
                    )
                )
        }
    }

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            modifier = Modifier
//                .weight(1f)
                .alignByBaseline()
            ,
            text = stringZDT,
            style = MaterialTheme.typography.labelLarge
        )

        TextButton(
            modifier = Modifier
//                .weight(1f)
                .alignBy(LastBaseline)
            ,
            colors = ButtonDefaults.textButtonColors(
                contentColor = LocalContentColor.current
            ),
            onClick = { /*TODO*/ }
        ) {
            Row(
                modifier = Modifier,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.LocationOn, contentDescription = "change place",
                )
                Text(
                    modifier = Modifier.padding(vertical = 8.dp),
                    maxLines = 1,
                    text = "Санкт-Петербург, Россия",
                    style = MaterialTheme.typography.labelLarge,
                    textAlign = TextAlign.End
                )
            }
        }
    }
}
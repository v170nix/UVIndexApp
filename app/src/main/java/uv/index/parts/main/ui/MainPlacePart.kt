package uv.index.parts.main.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.LastBaseline
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
internal fun MainPlacePart(
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            modifier = Modifier.alignByBaseline(),
            text = "26 августа, 17:25",
            style = MaterialTheme.typography.labelLarge
        )

        TextButton(
            modifier = Modifier.alignBy(LastBaseline),
            colors = ButtonDefaults.textButtonColors(
                contentColor = LocalContentColor.current
            ),
            onClick = { /*TODO*/ }
        ) {
            Row(modifier = Modifier,
                verticalAlignment = Alignment.CenterVertically) {
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
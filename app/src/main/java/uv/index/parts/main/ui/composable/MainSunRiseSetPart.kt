package uv.index.parts.main.ui.composable

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import uv.index.R

@Composable
internal fun MainSunRiseSetPart(
    modifier: Modifier = Modifier
) {
    CompositionLocalProvider(
        LocalContentColor provides contentColorFor(
            MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {

                Icon(
                    modifier = Modifier.size(32.dp),
                    painter = painterResource(id = R.drawable.ic_sunrise),
                    contentDescription = ""
                )

                Text(
                    text = "7:23",
                    textAlign = TextAlign.Start,
                    style = MaterialTheme.typography.titleMedium
                )
            }
            Row(
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "18:25",
                    textAlign = TextAlign.End,
                    style = MaterialTheme.typography.titleMedium
                )
                Icon(
                    modifier = Modifier.size(32.dp),
                    painter = painterResource(id = R.drawable.ic_sunset),
                    contentDescription = ""
                )
            }
        }
    }
}
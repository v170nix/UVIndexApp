package uv.index.parts.main.ui.composable

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import uv.index.R
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

private val formatter by lazy {
    DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT)
}

@Composable
internal fun MainSunRiseSetPart(
    modifier: Modifier = Modifier,
    riseTime: LocalTime?,
    setTime: LocalTime?
) {

    val riseString by remember(riseTime) {
        derivedStateOf {
            riseTime?.format(formatter) ?: "--:--"
        }
    }

    val setString by remember(setTime) {
        derivedStateOf {
            setTime?.format(formatter) ?: "--:--"
        }
    }


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
                    text = riseString,
                    textAlign = TextAlign.Start,
                    style = MaterialTheme.typography.titleMedium
                )
            }
            Row(
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = setString,
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
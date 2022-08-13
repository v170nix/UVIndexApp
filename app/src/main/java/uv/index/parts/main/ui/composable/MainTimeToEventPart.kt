package uv.index.parts.main.ui.composable

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Card
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
internal fun MainTimeToEventPart(
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        InnerCard(
            modifier = Modifier.weight(1f),
            info = "1 час 23 мин",
            description = "Время до ожога"
        )

        InnerCard(
            modifier = Modifier.weight(1f),
            info = "20 min",
            description = "Витамин Д"
        )
    }
}

@Composable
private fun InnerCard(
    modifier: Modifier = Modifier,
    info: String,
    description: String
) {
    Card(
        modifier = modifier,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
//            Icon(imageVector = Icons.Default.Person, contentDescription = null)
                Text(
                    text = info,
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.titleMedium
                )
            }
            Text(
                text = description,
                style = MaterialTheme.typography.labelLarge,
                color = LocalContentColor.current.copy(alpha = 0.6f)
            )
        }
    }
}

@Composable
private fun MainTimeToBurnPart(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
//            Icon(imageVector = Icons.Default.Person, contentDescription = null)
            Text(
                text = "1 час 23 мин",
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.titleMedium
            )
        }
        Text(
            text = "Время до ожога",
            style = MaterialTheme.typography.labelLarge,
            color = LocalContentColor.current.copy(alpha = 0.6f)
        )
    }
}

@Composable
fun MainVitaminDPart(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
//            Icon(imageVector = Icons.Default.Person, contentDescription = null)
            Text(
                text = "20 min",
                style = MaterialTheme.typography.titleMedium
            )
        }

        Text(
            text = "Витамин Д",
            style = MaterialTheme.typography.labelLarge,
            color = LocalContentColor.current.copy(alpha = 0.6f)
        )
    }
}
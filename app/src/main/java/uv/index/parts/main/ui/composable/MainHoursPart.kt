package uv.index.parts.main.ui.composable

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun MainHourPart(
    modifier: Modifier = Modifier
) {
    LazyRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {

        item {
            Spacer(Modifier.width(8.dp))
        }

        item {
            HourBox(
                index = "0.0",
                hour = "02:00"
            )
        }
        item {
            HourBox(
                index = "0.0",
                hour = "03:00"
            )
        }
        item {
            HourBox(
                index = "0.0",
                hour = "04:00"
            )
        }
        item {
            HourBox(
                index = "0.2",
                hour = "05:00"
            )
        }
        item {
            HourBox(
                index = "0.4",
                hour = "06:00"
            )
        }
        item {
            HourBox(
                index = "2.4",
                hour = "07:00"
            )
        }
        item {
            HourBox(
                index = "4.4",
                hour = "08:00"
            )
        }

        item {
            Spacer(Modifier.width(8.dp))
        }
    }
}

@Composable
private fun HourBox(
    index: String,
    hour: String
) {
    Card(
        modifier = Modifier.aspectRatio(1f),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFE53935),
            contentColor = Color.White
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = index,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = hour,
                    style = MaterialTheme.typography.titleSmall
                )
            }
        }
    }

}
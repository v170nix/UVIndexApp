package uv.index.parts.main.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.LastBaseline
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun MainPlacePart(
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

@Composable
fun MainProtectionPart(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            modifier = Modifier,
            text = "Protection advice",
            style = MaterialTheme.typography.labelLarge
        )
        Text(
            modifier = Modifier,
            text = "from 10:50 to 14:29",
            style = MaterialTheme.typography.titleLarge
        )
    }

}

@Composable
fun MainTimeToBurnPart(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(imageVector = Icons.Default.Person, contentDescription = null)
            Text(
                text = "60 min",
                style = MaterialTheme.typography.titleMedium
            )
        }

        Text(
            text = "Time to sunburn",
            style = MaterialTheme.typography.labelLarge
        )
    }
}

@Composable
fun MainVitaminDPart(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(imageVector = Icons.Default.Person, contentDescription = null)
            Text(
                text = "20 min",
                style = MaterialTheme.typography.titleMedium
            )
        }

        Text(
            text = "Vitamin D",
            style = MaterialTheme.typography.labelLarge
        )
    }
}

@Composable
fun MainHourPart(
    modifier: Modifier = Modifier
) {
    LazyRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
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
    }
}

@Composable
private fun HourBox(
    index: String,
    hour: String
) {
    Card(modifier = Modifier.aspectRatio(1f)) {
        Box(modifier = Modifier
            .fillMaxSize()
            .padding(8.dp)) {
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

@Composable
fun MainReminderPart(
    modifier: Modifier = Modifier
) {

}

//@Preview(
//    name = "Current Index",
//    showBackground = true,
//    widthDp = 480,
//    heightDp = 480
//)
//@Composable
//private fun PreviewCurrentIndexPart() {
//    UVIndexAppTheme {
//        MainCurrentIndexPart(
//            modifier = Modifier
//                .fillMaxSize()
//                .aspectRatio(1f / 0.69f)
//        )
//    }
//}
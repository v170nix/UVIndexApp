package uv.index.parts.main.ui

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.LastBaseline
import androidx.compose.ui.layout.layout
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

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
            text = "26 May, Thursday",
            style = MaterialTheme.typography.labelLarge
        )

        TextButton(
            modifier = Modifier.alignBy(LastBaseline),
            colors = ButtonDefaults.textButtonColors(
                contentColor = LocalContentColor.current
            ),
            onClick = { /*TODO*/ }
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.LocationOn, contentDescription = "change place",
                )
                Text(
                    modifier = Modifier.padding(vertical = 8.dp),
                    text = "Berlin",
                    style = MaterialTheme.typography.labelLarge,
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainCurrentIndexPart(
    modifier: Modifier = Modifier,
    scrollBehavior: TopAppBarScrollBehavior
) {



    val displaySmall = MaterialTheme.typography.displaySmall
    val titleLarge = MaterialTheme.typography.titleLarge

    val fontSize by remember(displaySmall, scrollBehavior) {
        derivedStateOf {
            val delta = displaySmall.fontSize.value - titleLarge.fontSize.value
            titleLarge.fontSize.value + delta * (1f - scrollBehavior.state.collapsedFraction)

        }
    }

    val colorAsState by animateColorAsState(targetValue = if (scrollBehavior.state.collapsedFraction < 0.5) Color.White else Color.Black

    )

    val displayLarge = MaterialTheme.typography.displayLarge

    val font2Size by remember(displayLarge, scrollBehavior) {
        derivedStateOf {
            val delta = displayLarge.fontSize.value - displaySmall.fontSize.value
            displaySmall.fontSize.value + delta * (1f - scrollBehavior.state.collapsedFraction)

        }
    }

    Box(
        modifier = modifier.fillMaxSize(),
    ) {

        @OptIn(ExperimentalMaterial3Api::class)
        fun Modifier.alignBehavior(scrollBehavior: TopAppBarScrollBehavior): Modifier {

            return if (scrollBehavior.state.collapsedFraction < 0.5) {
                then(
                    align(Alignment.TopEnd)
                )
            } else {
                then(
                    align(Alignment.CenterEnd)
                )
            }

        }

        Text(
            modifier = Modifier
                .alignBehaviorTop(scrollBehavior)
//                .alignBehavior(scrollBehavior)
//                .align(Alignment.TopEnd)

                .padding(end = 16.dp),
            text = "Экстремальный УФ",
            style = MaterialTheme.typography.displaySmall,
            color = colorAsState,
            fontSize = fontSize.sp
        )

        Text(
            modifier = Modifier
                .align(Alignment.BottomStart)
            ,
            text = "4/10",
            style = MaterialTheme.typography.displayLarge,
            color = Color.Black,
            fontSize = font2Size.sp
        )
//
//        Column(
//            modifier = Modifier.align(Alignment.BottomEnd),
//        ) {
//            Text(
//                modifier = Modifier,
//                text = "peak hour",
//                style = MaterialTheme.typography.bodyLarge,
//                color = Color.Black
//            )
//            Text(
//                modifier = Modifier,
//                text = "14:00",
//                style = MaterialTheme.typography.bodyLarge,
//                color = Color.Black
//            )
//        }

    }
}

@OptIn(ExperimentalMaterial3Api::class)
fun Modifier.alignBehaviorTop(scrollBehavior: TopAppBarScrollBehavior): Modifier {

    return layout { measurable, constraints ->
        val placeable = measurable.measure(constraints)
        val height = placeable.height

        layout(placeable.width, height) {
            placeable.placeRelative(0, 0)
        }
    }
//
//    return if (scrollBehavior.state.collapsedFraction < 0.5) {
//        then(
//            align(Alignment.TopEnd)
//        )
//    } else {
//        then(
//            align(Alignment.CenterEnd)
//        )
//    }

}



@Composable
fun MainForecastPart(
    modifier: Modifier = Modifier
) {

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
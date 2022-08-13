package uv.index.parts.main.ui.composable

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.max
import androidx.compose.ui.unit.sp
import uv.index.R
import uv.index.lib.data.getCurrentIndex
import uv.index.parts.main.ui.MainContract
import java.time.ZonedDateTime
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BoxWithConstraintsScope.MainCurrentInfoTopBar(
    modifier: Modifier = Modifier,
    scrollBehavior: TopAppBarScrollBehavior,
    collapsedHeight: Dp = 64.dp,
    currentDateTime: ZonedDateTime?,
    state: MainContract.State
) {
    val statusHeight: Dp by animateDpAsState(
        targetValue = max(
            this.maxWidth * (1 - scrollBehavior.state.collapsedFraction),
            collapsedHeight
        )
    )

    val currentIndexValue by rememberCurrentIndexValue(currentDateTime, state)

    val indexString by remember(currentIndexValue, state.currentSummaryDayData) {
        derivedStateOf {
            val maxIndex = state.currentSummaryDayData?.maxIndex?.getIntIndex() ?: 0
            "$currentIndexValue/$maxIndex"
        }
    }


    SmallTopAppBar(
        modifier = modifier
            .statusBarsPadding()
            .height(statusHeight),
        title = {
            Column(Modifier.statusBarsPadding()) {

                val inverseSurface = contentColorFor(MaterialTheme.colorScheme.inverseSurface)
                val surface = contentColorFor(MaterialTheme.colorScheme.surface)

                MainTopBarBoxPart(
                    minHeight = collapsedHeight,
                    collapsedFraction = scrollBehavior.state.collapsedFraction,
                    textStyles = MainTopBarDefaults.mainTopBarTextStyles(
                        placeExpandedStyle = MaterialTheme.typography.labelLarge.copy(color = inverseSurface),
                        placeCollapsedStyle = MaterialTheme.typography.labelLarge.copy(color = inverseSurface),
                        titleExpandedStyle = MaterialTheme.typography.displaySmall.copy(color = inverseSurface),
                        titleCollapsedStyle = MaterialTheme.typography.titleLarge.copy(color = surface),
                        riseSetTextStyle = MaterialTheme.typography.labelLarge.copy(color = inverseSurface),
                        indexExpandedStyle = MaterialTheme.typography.displayLarge
                            .copy(fontWeight = FontWeight.SemiBold, fontSize = 72.sp)
                            .copy(color = surface),
                        indexCollapsedStyle = MaterialTheme.typography.titleLarge.copy(color = surface),
                        peakHourStyle = MaterialTheme.typography.labelLarge.copy(color = surface),
                    ),
                    placeContent = {
                        MainPlacePart(
                            modifier = Modifier
                                .padding(end = 8.dp)
                                .fillMaxWidth()
                        )
                    },
                    titleContent = {
                        Text(
                            modifier = Modifier.padding(end = 16.dp),
                            text = "Экстремальный УФ",
                        )
                    },
                    indexContent = {
                        Text(
                            modifier = Modifier,
                            text = indexString,
                        )
                    },
                    subTitleContent = {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(end = 16.dp, top = 16.dp),
                            horizontalArrangement = Arrangement.End,
                            verticalAlignment = Alignment.CenterVertically
                        ) {

                            Icon(
                                modifier = Modifier.size(48.dp),
                                tint = Color.White,
                                painter = painterResource(id = R.drawable.ic_glasses),
                                contentDescription = ""
                            )

                            Spacer(modifier = Modifier.width(8.dp))

                            Icon(
                                modifier = Modifier.size(36.dp),
                                tint = Color.White,
                                painter = painterResource(id = R.drawable.ic_sunblock_alt),
                                contentDescription = ""
                            )

                            Spacer(modifier = Modifier.width(8.dp))

                            Icon(
                                modifier = Modifier.size(48.dp),
                                tint = Color.White,
                                painter = painterResource(id = R.drawable.ic_hat),
                                contentDescription = ""
                            )

                            Spacer(modifier = Modifier.width(8.dp))

                            Icon(
                                modifier = Modifier.size(40.dp),
                                tint = Color.White,
                                painter = painterResource(id = R.drawable.ic_shirt),
                                contentDescription = ""
                            )

                            Spacer(modifier = Modifier.width(8.dp))

                            Icon(
                                modifier = Modifier.size(40.dp),
                                tint = Color.White,
                                painter = painterResource(id = R.drawable.beach_shadow),
                                contentDescription = ""
                            )
                        }
                    },
                    maxHourContent = {
                        Column(
                            modifier = Modifier.padding(start = 8.dp, end = 16.dp)
                        ) {
                            Text(text = "пиковый час")
                            Text(text = "14:00")
                        }
                    }
                )
            }
        },
        colors = TopAppBarDefaults.smallTopAppBarColors(
            containerColor = Color.Transparent,
            scrolledContainerColor = Color.Transparent,
            titleContentColor = Color.Transparent
        ),
        scrollBehavior = scrollBehavior
    )
}

@Composable
private fun rememberCurrentIndexValue(
    currentDateTime: ZonedDateTime?,
    state: MainContract.State,
): State<Int?> {

    return remember(state.currentDayData, currentDateTime) {
        derivedStateOf {
            val time = currentDateTime ?: return@derivedStateOf null
            state.currentDayData?.getCurrentIndex(time.hour + time.minute / 60.0)?.roundToInt()
        }
    }


}
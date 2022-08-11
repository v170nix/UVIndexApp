package uv.index.parts.main.ui

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.max
import androidx.compose.ui.unit.sp


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BoxWithConstraintsScope.MainTopBar(
    modifier: Modifier = Modifier,
    scrollBehavior: TopAppBarScrollBehavior,
    collapsedHeight: Dp = 64.dp
) {
    val statusHeight: Dp by animateDpAsState(
        targetValue = max(this.maxWidth  * (1 - scrollBehavior.state.collapsedFraction), collapsedHeight)
    )

    SmallTopAppBar(
        modifier = modifier
            .statusBarsPadding()
            .height(statusHeight)
        ,
        title = {
            Column(Modifier.statusBarsPadding()) {
                MainTopBarBoxPart(
                    minHeight = collapsedHeight,
                    collapsedFraction = scrollBehavior.state.collapsedFraction,
                    textStyles = MainTopBarDefaults.mainTopBarTextStyles(
                        placeExpandedStyle = MaterialTheme.typography.labelLarge.copy(color = Color.White),
                        placeCollapsedStyle = MaterialTheme.typography.labelLarge.copy(color = Color.White),
                        titleExpandedStyle = MaterialTheme.typography.displaySmall.copy(color = Color.White),
                        titleCollapsedStyle = MaterialTheme.typography.titleLarge,
                        riseSetTextStyle = MaterialTheme.typography.labelLarge,
                        indexExpandedStyle = MaterialTheme.typography.displayLarge
                            .copy(fontWeight = FontWeight.SemiBold, fontSize = 72.sp),
                        indexCollapsedStyle = MaterialTheme.typography.titleLarge,
                        peakHourStyle = MaterialTheme.typography.labelLarge,
                        ),
                    placeContent = {
                        MainPlacePart(
                            modifier = Modifier.fillMaxWidth()
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
                            text = "4/10",
//                            fontWeight = FontWeight.ExtraBold,
                        )
                    },
                    riseSetContent = {

                    },
                    maxHourContent = {
                        Column(
                            modifier = Modifier.padding(start = 8.dp, end = 16.dp)
                        ) {
                            Text(text = "peak hour")
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
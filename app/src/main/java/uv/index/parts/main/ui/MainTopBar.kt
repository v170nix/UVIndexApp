package uv.index.parts.main.ui

import android.util.Log
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BoxWithConstraintsScope.MainTopBar(
    modifier: Modifier = Modifier,
    scrollBehavior: TopAppBarScrollBehavior,
    collapsedHeight: Dp = 64.dp
) {
    val statusHeight: Dp by animateDpAsState(
        targetValue = max(
            this.maxWidth * (1 - scrollBehavior.state.collapsedFraction),
            collapsedHeight
        )
    )

    SmallTopAppBar(
        modifier = modifier
            .statusBarsPadding()
            .height(statusHeight),
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
                            text = "4/10",
//                            fontWeight = FontWeight.ExtraBold,
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


//                        Row(
//                            modifier.fillMaxWidth()
//                                .padding(
//                                    top = 16.dp,
//                                    end = 16.dp
//                                ),
//                            horizontalArrangement = Arrangement.SpaceBetween
//                        ) {
//                            Text(
//                                text = "Восход\n7:23",
//                                textAlign = TextAlign.Start
//                            )
//                            Text(
//                                text = "Закат\n18:25",
//                                textAlign = TextAlign.End
//                            )
//                        }

                    },
                    maxHourContent = {
                        Column(
                            modifier = Modifier.padding(start = 8.dp, end = 16.dp)
                        ) {
                            Log.e("4", "4")
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
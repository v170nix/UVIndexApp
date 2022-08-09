package uv.index.parts.main.ui

import android.util.Log
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.max

@OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class)
@Composable
fun BoxWithConstraintsScope.MainTopBarPart(
    modifier: Modifier = Modifier,
    scrollBehavior: TopAppBarScrollBehavior
) {
    val density = LocalDensity.current
    val statusBar = WindowInsets.statusBars
    val topStatusBarHeight: Dp by remember(density, statusBar) {
        derivedStateOf {
            statusBar.asPaddingValues(density).calculateTopPadding().also {
//                Log.e("dp top", it.toString())
            }
            scrollBehavior.state.heightOffset.also {
//                Log.e("contentOffset", it.toString())
            }

            Dp(statusBar.getTop(density) / density.density).also {
                Log.e("topStatusBarHeight", it.toString())
            }
        }
    }


    val isShowIndexPart by remember(scrollBehavior) {
        derivedStateOf {
            scrollBehavior.state.collapsedFraction < 0.5f
        }
    }
    
    val statusHeight: Dp by animateDpAsState(targetValue =
    max(this.maxWidth  * (1 - scrollBehavior.state.collapsedFraction), 64.dp)
    )

    SmallTopAppBar(
        modifier = modifier
            .statusBarsPadding()
            .height(statusHeight)
      //      .height(topStatusBarHeight + 64.dp + 200.dp * (1 - scrollBehavior.state.collapsedFraction))
//            .height(0.dp + 300.dp )
        ,
        title = {
            Column(Modifier.statusBarsPadding()) {

                MainTopBarBoxPart(
                    minHeight = 64.dp,
                    collapsedFraction = scrollBehavior.state.collapsedFraction,
                    topBarColors = MainTopBarDefaults.mainTopBarColors(
                        placeExpandedColor = Color.White,
                        titleExpandedColor = Color.White,
                        titleCollapsedColor = Color.Black,
                        indexExpandedColor = Color.Black,
                        indexCollapsedColor = Color.Black
                    ),
                    placeContent = {
                        MainPlacePart(
                            modifier = Modifier.fillMaxWidth()
                        )
                    },
                    uvTitleText = "Экстремальный УФ",
                    indexContent = {
                        Text(
                            modifier = Modifier,
                            text = "4/10",
//                            style = MaterialTheme.typography.displayLarge,
                            color = Color.Black,
                            fontWeight = FontWeight.ExtraBold,
                        )
                    }
                )
//
//                AnimatedContent(targetState = isShowIndexPart) { targetState ->
//                    if (targetState) {
//                        MainPlacePart(
//                            modifier = Modifier.fillMaxWidth()
//                        )
//                    }
//                }

//                if (isShowIndexPart) {
//                    MainPlacePart(
//                        modifier = Modifier.fillMaxWidth()
//                    )
//                }

//                if (isShowIndexPart) {
//                    MainCurrentIndexPart(
//                        modifier = Modifier,
//                        scrollBehavior = scrollBehavior
//                    )
//                }
            }
        },
        colors = TopAppBarDefaults.smallTopAppBarColors(
            containerColor = Color.Transparent,
//            scrolledContainerColor = Color(0xAAE53935),
            scrolledContainerColor = Color.Transparent,
            titleContentColor = Color.White
        ),
        scrollBehavior = scrollBehavior
    )

//    Column(Modifier
//        .statusBarsPadding()
//        .height(64.dp )
//        .background(Color.Yellow)
//    ) {
//
//        WindowInsets
//
//        Column(
//            Modifier.fillMaxWidth().background(Color.Green)
//        ) {
//
//
//            AnimatedContent(
//                targetState = isShowIndexPart
//            ) { targetState ->
//                if (targetState) {
//                    MainPlacePart(
//                        modifier = Modifier.fillMaxWidth()
//                    )
//                }
//            }
//
////                if (isShowIndexPart) {
////                    MainPlacePart(
////                        modifier = Modifier.fillMaxWidth()
////                    )
////                }
//
////                if (isShowIndexPart) {
//            MainCurrentIndexPart(
//                modifier = Modifier,
//                scrollBehavior = scrollBehavior
//            )
//        }
////                }
//    }
}
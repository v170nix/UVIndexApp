package uv.index.parts.main.ui

import androidx.compose.animation.rememberSplineBasedDecay
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import uv.index.ui.theme.UVIndexAppTheme

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun MainScreen() {



    val decayAnimationSpec = rememberSplineBasedDecay<Float>()
    val scrollBehavior: TopAppBarScrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(
        decayAnimationSpec,
        rememberTopAppBarState()
    )

    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {

        val boxWithConstraintsScope = this
        val density = LocalDensity.current

        val pxRadius by remember(density, boxWithConstraintsScope) {
            derivedStateOf {
                boxWithConstraintsScope.maxWidth.value * density.density * 1.1f

            }
        }

        val dX by remember(density, boxWithConstraintsScope) {
            derivedStateOf {
                boxWithConstraintsScope.maxWidth.value * density.density * 0.8f

            }
        }

        val lazyListState = rememberLazyListState()

        val centerOffset by remember(dX, density, lazyListState, scrollBehavior.state) {
            derivedStateOf {
                Offset(dX, dX * (-scrollBehavior.state.collapsedFraction) * 1f)
            }
        }


        val gColor2 by remember(scrollBehavior.state.collapsedFraction) {
            derivedStateOf {
                val placeCollapsedColor = Color(0x99E53935)
                placeCollapsedColor.copy(alpha = scrollBehavior.state.collapsedFraction)
            }
        }

        Spacer(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush =
                    Brush.radialGradient(
                        colors = listOf(
                            Color(0xFFE53935),
                            Color(0xAAE53935),
//                                    Color(0x99FF753E),
                            Color.Transparent
                        ),
                        center = centerOffset,
                        radius = pxRadius,
                    )
                )
                .background(
                    brush =
                    Brush.verticalGradient(
                        colors = listOf(
//                            Color(0xFFE53935),
                            gColor2,
//                                    Color(0x99FF753E),
                            Color.Transparent,
                        ),
                        startY = centerOffset.y / 10f,
                        endY = with(density) { WindowInsets.statusBars.asPaddingValues().calculateTopPadding().toPx() + 64.dp.toPx() } // pxRadius / 3f + centerOffset.y / 10f
                    )
                )
        )

        Scaffold(
            containerColor = Color.Transparent,
            modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
            topBar = {
                    MainTopBarPart(
//                    modifier = Modifier.systemBarsPadding(),
                        scrollBehavior = scrollBehavior
                    )
            }
        ) {
            LazyColumn(
                modifier = Modifier
//                    .fillMaxSize()
//                    .systemBarsPadding()
                    .padding(it)
                ,
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp),
                state = lazyListState
            ) {

//                item {
//
//                    Column(
//                        modifier = Modifier
//                            .fillMaxWidth()
//                            .padding(vertical = 16.dp),
//
//                        ) {
//
//                        Box(
//                            modifier = Modifier
//                                .fillMaxWidth()
//                                .padding(horizontal = 16.dp),
//                        ) {
//                            MainPlacePart(
//                                modifier = Modifier.fillMaxWidth()
//                            )
//                        }
//
//                        MainCurrentIndexPart(
//                            modifier = Modifier
//                                .fillMaxSize()
//                                .aspectRatio(1f / 1f)
//                                .padding(horizontal = 16.dp)
//                        )
//                    }
//                }

                item {
                    MainProtectionPart(
                        modifier = Modifier
                            .fillMaxWidth()
//                            .padding(horizontal = 16.dp)
                    )
                }

                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                    ) {
                        MainTimeToBurnPart(
                            modifier = Modifier.weight(1f)
                        )
                        MainVitaminDPart(
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                item {
                    MainHourPart(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                    )
                }
                items(100) {
                    Text(
                        modifier = Modifier.fillMaxWidth(),
                        text = "5"
                    )
                }
            }
        }





    }
}

@Preview(
    name = "Main Screen",
    showBackground = true
)
@Composable
private fun PreviewMainScreen() {
    UVIndexAppTheme {
        Surface {
            MainScreen()
        }
    }
}
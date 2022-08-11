package uv.index.parts.main.ui

import androidx.compose.animation.rememberSplineBasedDecay
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import uv.index.ui.theme.UVIndexAppTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen() {

    val decayAnimationSpec = rememberSplineBasedDecay<Float>()
    val scrollBehavior: TopAppBarScrollBehavior =
        TopAppBarDefaults.exitUntilCollapsedScrollBehavior(
            decayAnimationSpec,
            rememberTopAppBarState()
        )

    BoxWithConstraints(modifier = Modifier.fillMaxSize().background(Color.White)) {

        val lazyListState = rememberLazyListState()

        MainBackground(
            state = scrollBehavior.state,
            collapsedHeight = 64.dp,
            highlightColor = Color(0xFFE53935),
            backgroundColor = Color.White
        )

        Scaffold(
            containerColor = Color.Transparent,
            modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
            topBar = {
                MainTopBar(
                    scrollBehavior = scrollBehavior
                )
            }
        ) {

            LazyColumn(
                modifier = Modifier.padding(it),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp),
                state = lazyListState
            ) {

                mainBackgroundHeader(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(16.dp),
                    state = scrollBehavior.state
                )

                item {
                    MainProtectionPart(modifier = Modifier.fillMaxWidth())
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun BoxWithConstraintsScope.MainBackground(
    modifier: Modifier = Modifier,
    state: TopAppBarState,
    collapsedHeight: Dp,
    highlightColor: Color,
    backgroundColor: Color,
) {
    val boxScope = this
    val density = LocalDensity.current
    val statusBar = WindowInsets.statusBars

    val xCenterOffset by remember(density, boxScope) {
        derivedStateOf { boxScope.maxWidth.value * 0.8f * density.density }
    }

    val radius by remember(density, boxScope) {
        derivedStateOf { boxScope.maxWidth.value * 1.4f * density.density }
    }

    val endYVerticalGradient by remember(density, statusBar, collapsedHeight) {
        derivedStateOf {
            statusBar.getTop(density) + collapsedHeight.value * density.density
        }
    }

    Spacer(
        modifier = modifier
            .fillMaxSize()
            .graphicsLayer {
                translationY = radius * (-state.collapsedFraction) * 1.1f
                alpha = (1f - state.collapsedFraction / 0.9f).coerceIn(0.01f, 1f)
            }
            .background(
                brush =
                Brush.radialGradient(
                    colors = listOf(
                        highlightColor,
                        highlightColor.copy(alpha = 0xAA.toFloat() / 0xFF),
                        backgroundColor
                    ),
                    center = Offset(xCenterOffset, 0f),
                    radius = radius,
                )
            )
    )

    Spacer(
        modifier = modifier
            .fillMaxSize()
            .graphicsLayer {
                alpha = (state.collapsedFraction + 0.5f).coerceIn(0.01f, 0.8f)
            }
            .background(
                brush =
                Brush.verticalGradient(
                    colors = listOf(
                        highlightColor.copy(alpha = 0xFF.toFloat() / 0xFF),
                        Color.Transparent,
                    ),
                    startY = 0f,
                    endY = endYVerticalGradient
                )
            )
    )
}

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
private fun LazyListScope.mainBackgroundHeader(
    modifier: Modifier = Modifier,
    state: TopAppBarState,
) {
    stickyHeader {
        Box(modifier = modifier) {

            val alpha by remember(state.collapsedFraction) {
                derivedStateOf {
                    ((state.collapsedFraction - 0.9)
                        .coerceAtLeast(0.0) * 10.0)
                        .coerceIn(0.01, 1.0)
                        .toFloat()
                }
            }
            Spacer(
                modifier = Modifier
                    .fillMaxSize()
                    .alpha(alpha)
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                Color.White,
                                Color.Transparent
                            ),
                        )
                    )
            )

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
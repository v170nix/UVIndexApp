package uv.index.parts.main.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import uv.index.ui.theme.UVIndexAppTheme

@Composable
fun MainScreen() {

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .systemBarsPadding()
    ) {

        val boxWithConstraintsScope = this
        val density = LocalDensity.current

        val pxRadius by remember(density, boxWithConstraintsScope) {
            derivedStateOf {
                boxWithConstraintsScope.maxWidth.value * density.density * 1.2f

            }
        }

        val dX by remember(density, boxWithConstraintsScope) {
            derivedStateOf {
                boxWithConstraintsScope.maxWidth.value * density.density

            }
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {


            item {

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
//                        .background(
//                            brush =
//                            Brush.radialGradient(
//                                colors = listOf(
//                                    Color(0xFFFF753E),
//                                    Color(0xBBFF753E),
////                                    Color(0x99FF753E),
//                                    Color.Transparent
//                                ),
//                                center = Offset(dX, 0f - 128.dp.value * density.density),
//                                radius = pxRadius,
//                            )
//                        )
                    ,

                ) {

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        contentAlignment = Alignment.CenterEnd
                    ) {
                        MainPlacePart(
                            modifier = Modifier
                        )
                    }

                    MainCurrentIndexPart(
                        modifier = Modifier
                            .fillMaxSize()
                            .aspectRatio(1f / 1f)
                    )
                }
            }

            item {
                MainProtectionPart(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
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
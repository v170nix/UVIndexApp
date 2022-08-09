package uv.index.parts.main.ui

import androidx.compose.animation.rememberSplineBasedDecay
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.*
import uv.index.ui.theme.UVIndexAppTheme

@Stable
interface MainTopBarColors {
    @Composable
    fun placeTextColor(colorTransitionFraction: Float): State<Color>

    @Composable
    fun titleTextColor(colorTransitionFraction: Float): State<Color>

    @Composable
    fun indexTextColor(colorTransitionFraction: Float): State<Color>
}

@Stable
interface MainTopBarTextSizes {
    @Composable
    fun titleTextSize(textTransitionFraction: Float): State<TextUnit>

    @Composable
    fun indexTextSize(textTransitionFraction: Float): State<TextUnit>
}

object MainTopBarDefaults {

    @Composable
    fun mainTopBarColors(
        placeExpandedColor: Color,
        placeCollapsedColor: Color = placeExpandedColor,
        titleExpandedColor: Color,
        titleCollapsedColor: Color = titleExpandedColor,
        indexExpandedColor: Color,
        indexCollapsedColor: Color = indexExpandedColor

    ): MainTopBarColors {
        return remember(
            placeExpandedColor,
            placeCollapsedColor,
            titleExpandedColor,
            titleCollapsedColor,
            indexExpandedColor,
            indexCollapsedColor
        ) {
            AnimatingMainTopBarColors(
                placeExpandedColor,
                placeCollapsedColor,
                titleExpandedColor,
                titleCollapsedColor,
                indexExpandedColor,
                indexCollapsedColor
            )
        }
    }

    @Composable
    fun mainTopBarTextSizes(
        titleExpandedFontSize: TextUnit,
        titleCollapsedFontSize: TextUnit,
        indexExpandedSp: TextUnit,
        indexCollapsedSp: TextUnit,
    ): MainTopBarTextSizes {
        return remember {
            AnimatingMainTopBarTextSizes(
                titleExpandedFontSize,
                titleCollapsedFontSize,
                indexExpandedSp,
                indexCollapsedSp,
            )
        }
    }
}

@Composable
fun MainTopBarBoxPart(
    modifier: Modifier = Modifier,
    collapsedFraction: Float,
    minHeight: Dp,
    topBarColors: MainTopBarColors,
    topBarTextSizes: MainTopBarTextSizes = MainTopBarDefaults.mainTopBarTextSizes(
        titleExpandedFontSize = MaterialTheme.typography.displaySmall.fontSize,
        titleCollapsedFontSize = MaterialTheme.typography.titleLarge.fontSize,
        indexExpandedSp = MaterialTheme.typography.displayLarge.fontSize,
        indexCollapsedSp = MaterialTheme.typography.titleLarge.fontSize
    ),
    placeContent: @Composable () -> Unit,
    uvTitleText: String,
    indexContent: @Composable () -> Unit

) {
    val placeAlpha = (1f - collapsedFraction * 1.5f).coerceAtLeast(0f)
    val colorTransitionFraction = 1f - collapsedFraction

    val placeTextColor by topBarColors.placeTextColor(colorTransitionFraction = colorTransitionFraction)
    val titleTextColor by topBarColors.titleTextColor(colorTransitionFraction = colorTransitionFraction)
    val titleFontSize by topBarTextSizes.titleTextSize(textTransitionFraction = colorTransitionFraction)
    val indexFontSize by topBarTextSizes.indexTextSize(textTransitionFraction = colorTransitionFraction)


    Layout(
        modifier = modifier,
        content = {
            Box(
                Modifier.layoutId("placeContent")
            ) {
                CompositionLocalProvider(
                    LocalContentColor provides placeTextColor.copy(alpha = placeAlpha)
                ) {
                    placeContent()
                }
            }

            Box(
                Modifier.layoutId("uvTitleContent")
            ) {
                Text(
                    modifier = Modifier.padding(end = 16.dp),
                    text = uvTitleText,
                    style = MaterialTheme.typography.displaySmall,
                    fontSize = titleFontSize,
                    color = titleTextColor
                )
            }

            Box(
                Modifier.layoutId("indexContent")
            ) {

                ProvideTextStyle(
                    value = MaterialTheme.typography.displayLarge.copy(
                        fontSize = indexFontSize
                    )
                ) {
                    indexContent()
                }
            }
        }
    )
    { measurables, constraints ->

        val placePlaceable = measurables.first { it.layoutId == "placeContent" }
            .measure(constraints.copy(minHeight = 0))

        val uvTitlePlaceable = measurables.first { it.layoutId == "uvTitleContent" }
            .measure(constraints)

        val indexPlaceable = measurables.first { it.layoutId == "indexContent" }
            .measure(constraints)




        layout(constraints.maxWidth, constraints.maxHeight) {

            val yPlacePosition = (0 - collapsedFraction * placePlaceable.height).toInt()

            val yUVTopPosition = (yPlacePosition + placePlaceable.height)
                .coerceAtLeast(0)

            val yUVCurrentPosition = ((minHeight.toPx().toInt() - uvTitlePlaceable.height) / 2)
                .coerceAtLeast(yUVTopPosition)


            val yIndexCollapsedPosition = (minHeight.toPx().toInt() - indexPlaceable.height) / 2
            val yIndexExpandedPosition = constraints.maxHeight - indexPlaceable.height

            val yIndexPosition = yIndexCollapsedPosition +
                    ((1f - collapsedFraction) * (yIndexExpandedPosition - yIndexCollapsedPosition)).toInt()


            placePlaceable.placeRelative(
                x = 0,
                y = yPlacePosition
            )

            uvTitlePlaceable.placeRelative(
                x = constraints.maxWidth - uvTitlePlaceable.width,
                y = yUVCurrentPosition
            )

            indexPlaceable.placeRelative(
                x = 0,
                y = yIndexPosition
            )

        }
    }


}

@Stable
private class AnimatingMainTopBarColors(
    private val placeExpandedColor: Color,
    private val placeCollapsedColor: Color,
    private val titleExpandedColor: Color,
    private val titleCollapsedColor: Color,
    private val indexExpandedColor: Color,
    private val indexCollapsedColor: Color,
) : MainTopBarColors {
    @Composable
    override fun placeTextColor(colorTransitionFraction: Float): State<Color> {
        return remember(colorTransitionFraction) {
            derivedStateOf {
                Color(
                    red = (placeCollapsedColor.red + colorTransitionFraction * (placeExpandedColor.red - placeCollapsedColor.red)).coerceIn(
                        0f,
                        1f
                    ),
                    green = (placeCollapsedColor.green + colorTransitionFraction * (placeExpandedColor.green - placeCollapsedColor.green)).coerceIn(
                        0f,
                        1f
                    ),
                    blue = (placeCollapsedColor.blue + colorTransitionFraction * (placeExpandedColor.blue - placeCollapsedColor.blue)).coerceIn(
                        0f,
                        1f
                    ),
                )
            }
        }
    }

    @Composable
    override fun titleTextColor(colorTransitionFraction: Float): State<Color> {
        return remember(colorTransitionFraction) {
            derivedStateOf {
                Color(
                    red = (titleCollapsedColor.red + colorTransitionFraction * (titleExpandedColor.red - titleCollapsedColor.red)).coerceIn(
                        0f,
                        1f
                    ),
                    green = (titleCollapsedColor.green + colorTransitionFraction * (titleExpandedColor.green - titleCollapsedColor.green)).coerceIn(
                        0f,
                        1f
                    ),
                    blue = (titleCollapsedColor.blue + colorTransitionFraction * (titleExpandedColor.blue - titleCollapsedColor.blue)).coerceIn(
                        0f,
                        1f
                    ),
                )
            }
        }
    }

    @Composable
    override fun indexTextColor(colorTransitionFraction: Float): State<Color> {
        return remember(colorTransitionFraction) {
            derivedStateOf {
                Color(
                    red = (indexCollapsedColor.red + colorTransitionFraction * (indexExpandedColor.red - indexCollapsedColor.red)).coerceIn(
                        0f,
                        1f
                    ),
                    green = (indexCollapsedColor.green + colorTransitionFraction * (indexExpandedColor.green - indexCollapsedColor.green)).coerceIn(
                        0f,
                        1f
                    ),
                    blue = (indexCollapsedColor.blue + colorTransitionFraction * (indexExpandedColor.blue - indexCollapsedColor.blue)).coerceIn(
                        0f,
                        1f
                    ),
                )
            }
        }
    }
}

@OptIn(ExperimentalUnitApi::class)
@Stable
private class AnimatingMainTopBarTextSizes(
    private val titleExpandedSp: TextUnit,
    private val titleCollapsedSp: TextUnit,
    private val indexExpandedSp: TextUnit,
    private val indexCollapsedSp: TextUnit,
) : MainTopBarTextSizes {

    @Composable
    override fun titleTextSize(textTransitionFraction: Float): State<TextUnit> {
        return remember(textTransitionFraction) {
            derivedStateOf {
                TextUnit(
                    titleCollapsedSp.value + textTransitionFraction * (titleExpandedSp.value - titleCollapsedSp.value),
                    TextUnitType.Sp
                )
            }
        }
    }

    @Composable
    override fun indexTextSize(textTransitionFraction: Float): State<TextUnit> {
        return remember(textTransitionFraction) {
            derivedStateOf {
                TextUnit(
                    indexCollapsedSp.value + textTransitionFraction * (indexExpandedSp.value - indexCollapsedSp.value),
                    TextUnitType.Sp
                )
            }
        }
    }

}


@OptIn(ExperimentalMaterial3Api::class)
@Preview(
    showBackground = true
)
@Composable
private fun Preview() {

    val decayAnimationSpec = rememberSplineBasedDecay<Float>()
    val scrollBehavior: TopAppBarScrollBehavior =
        TopAppBarDefaults.exitUntilCollapsedScrollBehavior(
            decayAnimationSpec,
            rememberTopAppBarState()
        )

    UVIndexAppTheme {
        Surface {


            MainTopBarBoxPart(
                collapsedFraction = scrollBehavior.state.collapsedFraction,
                topBarColors = MainTopBarDefaults.mainTopBarColors(
                    placeExpandedColor = Color.White,
                    titleExpandedColor = Color.White,
                    indexExpandedColor = Color.Black
                ),
                minHeight = 64.dp,
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
                        style = MaterialTheme.typography.displayLarge,
                        color = Color.Black,
                    )
                }

            )
        }
    }
}
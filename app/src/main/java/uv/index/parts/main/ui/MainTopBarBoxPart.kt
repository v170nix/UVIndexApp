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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.*
import uv.index.ui.theme.UVIndexAppTheme

@Stable
interface MainTopBarTextStyles {
    @Composable
    fun placeTextStyle(fraction: Float): State<TextStyle>

    @Composable
    fun titleTextStyle(fraction: Float): State<TextStyle>

    @Composable
    fun indexTextStyle(fraction: Float): State<TextStyle>
}

object MainTopBarDefaults {
    @Composable
    fun mainTopBarTextStyles(
        placeExpandedStyle: TextStyle,
        placeCollapsedStyle: TextStyle,
        titleExpandedStyle: TextStyle,
        titleCollapsedStyle: TextStyle,
        indexExpandedStyle: TextStyle,
        indexCollapsedStyle: TextStyle
    ): MainTopBarTextStyles {
        return remember(
            placeExpandedStyle,
            placeCollapsedStyle,
            titleExpandedStyle,
            titleCollapsedStyle,
            indexExpandedStyle,
            indexCollapsedStyle
        ) {
            AnimatingMainTopBarTextStyles(
                placeExpandedStyle,
                placeCollapsedStyle,
                titleExpandedStyle,
                titleCollapsedStyle,
                indexExpandedStyle,
                indexCollapsedStyle
            )
        }

    }
}

@Composable
fun MainTopBarBoxPart(
    modifier: Modifier = Modifier,
    collapsedFraction: Float,
    minHeight: Dp,
    textStyles: MainTopBarTextStyles,
    placeContent: @Composable (fraction: Float) -> Unit,
    titleContent: @Composable (fraction: Float) -> Unit,
    indexContent: @Composable (fraction: Float) -> Unit

) {
    val placeAlpha = (1f - collapsedFraction * 1.5f).coerceAtLeast(0f)
    val fraction = 1f - collapsedFraction

    val placeStyle by textStyles.placeTextStyle(fraction = fraction)
    val titleStyle by textStyles.titleTextStyle(fraction = fraction)
    val indexStyle by textStyles.indexTextStyle(fraction = fraction)

    Layout(
        modifier = modifier,
        content = {
            Box(
                Modifier.layoutId("placeContent")
            ) {
                ProvideTextStyle(value = placeStyle) {
                    CompositionLocalProvider(
                        LocalContentColor provides placeStyle.color.copy(alpha = placeAlpha)
                    ) {
                        placeContent(fraction)
                    }
                }
            }

            Box(
                Modifier.layoutId("uvTitleContent")
            ) {
                ProvideTextStyle(value = titleStyle) { titleContent(fraction) }
            }

            Box(
                Modifier.layoutId("indexContent")
            ) {
                ProvideTextStyle(value = indexStyle) { indexContent(fraction) }
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
private class AnimatingMainTopBarTextStyles(
    private val placeExpandedStyle: TextStyle,
    private val placeCollapsedStyle: TextStyle,
    private val titleExpandedStyle: TextStyle,
    private val titleCollapsedStyle: TextStyle,
    private val indexExpandedStyle: TextStyle,
    private val indexCollapsedStyle: TextStyle

) : MainTopBarTextStyles {

    @Composable
    override fun placeTextStyle(fraction: Float): State<TextStyle> {
        return remember(fraction) {
            derivedStateOf {
                getFractionTextStyle(placeCollapsedStyle, placeExpandedStyle, fraction)
            }
        }
    }

    @Composable
    override fun titleTextStyle(fraction: Float): State<TextStyle> {
        return remember(fraction) {
            derivedStateOf {
                getFractionTextStyle(titleCollapsedStyle, titleExpandedStyle, fraction)
            }
        }
    }

    @Composable
    override fun indexTextStyle(fraction: Float): State<TextStyle> {
        return remember(fraction) {
            derivedStateOf {
                getFractionTextStyle(indexCollapsedStyle, indexExpandedStyle, fraction)
            }
        }
    }

    private companion object {

        private fun getFractionTextStyle(cStyle: TextStyle, eStyle: TextStyle, fraction: Float): TextStyle {
            val style = eStyle.takeIf { fraction > 0.5 } ?: cStyle
            return style.copy(
                color = getFractionColor(cStyle.color, eStyle.color, fraction),
                fontSize = getFractionTextSize(cStyle.fontSize, eStyle.fontSize, fraction),
                fontWeight = getFractionFontWeight(cStyle.fontWeight, eStyle.fontWeight, fraction)
            )
        }

        private fun getFractionFontWeight(
            cFontWeight: FontWeight?,
            eFontWeight: FontWeight?,
            fraction: Float
        ): FontWeight? {
            cFontWeight ?: return null
            eFontWeight ?: return null
            return FontWeight(
                getFractionValue(
                    cFontWeight.weight.toFloat(),
                    eFontWeight.weight.toFloat(),
                    fraction).toInt()
            )
        }

        @OptIn(ExperimentalUnitApi::class)
        private fun getFractionTextSize(
            cTextUnit: TextUnit,
            eTextUnit: TextUnit,
            fraction: Float
        ): TextUnit {
            return TextUnit(
                getFractionValue(cTextUnit.value, eTextUnit.value, fraction), TextUnitType.Sp
            )
        }

        private fun getFractionColor(cColor: Color, eColor: Color, fraction: Float): Color {
            return Color(
                red = getFractionValue(cColor.red, eColor.red, fraction),
                green = getFractionValue(cColor.green, eColor.green, fraction),
                blue = getFractionValue(cColor.blue, eColor.blue, fraction),
            )
        }

        private fun getFractionValue(cValue: Float, eValue: Float, fraction: Float) =
            cValue + fraction * (eValue - cValue)
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
                textStyles = MainTopBarDefaults.mainTopBarTextStyles(
                    placeExpandedStyle = MaterialTheme.typography.labelLarge,
                    placeCollapsedStyle = MaterialTheme.typography.labelLarge,
                    titleExpandedStyle = MaterialTheme.typography.displaySmall,
                    titleCollapsedStyle = MaterialTheme.typography.titleMedium,
                    indexExpandedStyle = MaterialTheme.typography.displayLarge,
                    indexCollapsedStyle = MaterialTheme.typography.titleMedium,

                    ),
                minHeight = 64.dp,
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
                        style = MaterialTheme.typography.displayLarge,
                        color = Color.Black,
                    )
                }
            )
        }
    }
}
package uv.index.features.main.ui.composable.sections.dataview.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.Placeable
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

    @Composable
    fun peakHourTextStyle(fraction: Float): State<TextStyle>

    @Composable
    fun riseSetTextStyle(fraction: Float): State<TextStyle>
}

object MainTopBarDefaults {

    @Composable
    fun mainTopBarTextStyles(
        placeExpandedStyle: TextStyle,
        placeCollapsedStyle: TextStyle,
        titleExpandedStyle: TextStyle,
        titleCollapsedStyle: TextStyle,
        riseSetTextStyle: TextStyle,
        indexExpandedStyle: TextStyle,
        indexCollapsedStyle: TextStyle,
        peakHourStyle: TextStyle,
    ): MainTopBarTextStyles {
        return remember(
            placeExpandedStyle,
            placeCollapsedStyle,
            titleExpandedStyle,
            titleCollapsedStyle,
            riseSetTextStyle,
            indexExpandedStyle,
            indexCollapsedStyle,
            peakHourStyle
        ) {
            AnimatingMainTopBarTextStyles(
                placeExpandedStyle,
                placeCollapsedStyle,
                titleExpandedStyle,
                titleCollapsedStyle,
                riseSetTextStyle,
                indexExpandedStyle,
                indexCollapsedStyle,
                peakHourStyle
            )
        }
    }
}

@Composable
internal fun MainCurrentInfoTopBarInnerPart(
    modifier: Modifier = Modifier,
    collapsedFraction: Float,
    minHeight: Dp,
    textStyles: MainTopBarTextStyles,
    placeContent: @Composable (fraction: Float) -> Unit,
    titleContent: @Composable (fraction: Float) -> Unit,
    subTitleContent: @Composable (fraction: Float) -> Unit,
    indexContent: @Composable (fraction: Float) -> Unit,
    maxTimeContent: @Composable (fraction: Float) -> Unit,
) {


    val placeAlpha by remember(collapsedFraction) {
        derivedStateOf {
            (1f - collapsedFraction * 1.5f).coerceIn(0.01f, 1f)
        }
    }

    val hourAlpha by remember(collapsedFraction) {
        derivedStateOf {
            (1f - collapsedFraction * 3f).coerceIn(0.01f, 1f)
        }
    }

    val fraction = 1f - collapsedFraction

    val placeStyle by textStyles.placeTextStyle(fraction = fraction)
    val titleStyle by textStyles.titleTextStyle(fraction = fraction)
    val indexStyle by textStyles.indexTextStyle(fraction = fraction)
    val hourStyle by textStyles.peakHourTextStyle(fraction = fraction)
    val subTitleStyle by textStyles.riseSetTextStyle(fraction = fraction)

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
                Modifier.layoutId("titleContent")
            ) {
                ProvideTextStyle(value = titleStyle) { titleContent(fraction) }
            }

            Box(
                Modifier
                    .layoutId("subTitleContent")
                    .fillMaxWidth()
                    .graphicsLayer {
                        alpha = hourAlpha
                        scaleY = fraction
                    },
            ) {
                ProvideTextStyle(value = subTitleStyle) { subTitleContent(fraction) }
            }

            Box(
                Modifier.layoutId("indexContent")
            ) {
                ProvideTextStyle(value = indexStyle) { indexContent(fraction) }
            }

            Box(
                Modifier
                    .layoutId("peakHourContent")
                    .alpha(hourAlpha)
            ) {
                ProvideTextStyle(value = hourStyle) {
                    maxTimeContent(fraction)
                }
            }
        }
    )
    { measurables, constraints ->

        val placePlaceable = measurables.first { it.layoutId == "placeContent" }
            .measure(constraints.copy(minHeight = 0))

        val titlePlaceable = measurables.first { it.layoutId == "titleContent" }
            .measure(constraints)

        val subTitlePlaceable = measurables.first { it.layoutId == "subTitleContent" }
            .measure(constraints.copy(minWidth = constraints.maxWidth))

        val indexPlaceable: Placeable = measurables.first { it.layoutId == "indexContent" }
            .measure(constraints)

        val peakHourPlaceable = measurables.first { it.layoutId == "peakHourContent" }
            .measure(constraints)

//        val indexBaseline = indexPlaceable[FirstBaseline]
//        val lastBaseline = indexPlaceable[HorizontalAlignmentLine(merger = { old, new ->
//            kotlin.math.max(
//                old,
//                new
//            )
//        })]

        layout(constraints.maxWidth, constraints.maxHeight) {

            val yPlacePosition = (0 - collapsedFraction * placePlaceable.height).toInt()

            val yUVTopPosition = (yPlacePosition + placePlaceable.height)
                .coerceAtLeast(0)

            val yUVCurrentPosition = ((minHeight.toPx().toInt() - titlePlaceable.height) / 2)
                .coerceAtLeast(yUVTopPosition)

            val yIndexCollapsedPosition = (minHeight.toPx().toInt() - indexPlaceable.height) / 2
            val yIndexExpandedPosition = constraints.maxHeight - indexPlaceable.height

            val yIndexPosition = yIndexCollapsedPosition +
                    ((1f - collapsedFraction) * (yIndexExpandedPosition - yIndexCollapsedPosition)).toInt()

            val yPeakHourPosition =
                yIndexPosition + indexPlaceable.height * 0.83f - peakHourPlaceable.height
            val xPeakHourPosition = (constraints.maxWidth / 2)
                .coerceAtLeast(indexPlaceable.width)

            placePlaceable.placeRelative(
                x = 0,
                y = yPlacePosition
            )

            titlePlaceable.placeRelative(
                x = constraints.maxWidth - titlePlaceable.width,
                y = yUVCurrentPosition
            )

            if (collapsedFraction < 0.8) {
                subTitlePlaceable.placeRelative(
                    x = 0,
                    y = yUVCurrentPosition + titlePlaceable.height
                )
            }

            indexPlaceable.placeRelative(
                x = 0,
                y = yIndexPosition
            )

            if (collapsedFraction < 0.4) {
                peakHourPlaceable.placeRelative(
                    x = xPeakHourPosition,
                    y = yPeakHourPosition.toInt()
                )
            }
        }
    }

}

@Stable
private class AnimatingMainTopBarTextStyles(
    private val placeExpandedStyle: TextStyle,
    private val placeCollapsedStyle: TextStyle,
    private val titleExpandedStyle: TextStyle,
    private val titleCollapsedStyle: TextStyle,
    private val riseSetTextStyle: TextStyle,
    private val indexExpandedStyle: TextStyle,
    private val indexCollapsedStyle: TextStyle,
    private val peakHourStyle: TextStyle

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

    @Composable
    override fun peakHourTextStyle(fraction: Float): State<TextStyle> {
        return remember(Unit) { mutableStateOf(peakHourStyle) }
    }

    @Composable
    override fun riseSetTextStyle(fraction: Float): State<TextStyle> {
        return remember(Unit) { mutableStateOf(riseSetTextStyle) }
    }

    private companion object {

        private fun getFractionTextStyle(
            cStyle: TextStyle,
            eStyle: TextStyle,
            fraction: Float
        ): TextStyle {
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
                    fraction
                ).toInt()
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

    val scrollBehavior: TopAppBarScrollBehavior =
        TopAppBarDefaults.exitUntilCollapsedScrollBehavior(
            rememberTopAppBarState()
        )

    UVIndexAppTheme {
        Surface {
            MainCurrentInfoTopBarInnerPart(
                collapsedFraction = scrollBehavior.state.collapsedFraction,
                textStyles = MainTopBarDefaults.mainTopBarTextStyles(
                    placeExpandedStyle = MaterialTheme.typography.labelLarge,
                    placeCollapsedStyle = MaterialTheme.typography.labelLarge,
                    titleExpandedStyle = MaterialTheme.typography.displaySmall,
                    titleCollapsedStyle = MaterialTheme.typography.titleMedium,
                    indexExpandedStyle = MaterialTheme.typography.displayLarge,
                    indexCollapsedStyle = MaterialTheme.typography.titleMedium,
                    peakHourStyle = MaterialTheme.typography.labelLarge,
                    riseSetTextStyle = MaterialTheme.typography.labelLarge
                ),
                minHeight = 64.dp,
                placeContent = {
                    MainPlacePart(
                        modifier = Modifier.fillMaxWidth(),
                        onEditPlace = {},
                        place = null
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
                },
                subTitleContent = {},
                maxTimeContent = {}
            )
        }
    }
}
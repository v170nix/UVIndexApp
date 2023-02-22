package uv.index.features.main.ui.composable.sections.dataview.components

import androidx.annotation.DrawableRes
import androidx.compose.animation.*
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.max
import androidx.compose.ui.unit.sp
import uv.index.R
import uv.index.features.astronomy.data.SunPosition
import uv.index.features.main.common.getUVITitle
import uv.index.features.main.ui.MainContract
import uv.index.features.uvi.data.UVLevel
import uv.index.ui.theme.Dimens
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
@Suppress("LongMethod")
fun BoxWithConstraintsScope.MainCurrentInfoTopBarPart(
    modifier: Modifier = Modifier,
    scrollBehavior: TopAppBarScrollBehavior,
    placeContent: @Composable (fraction: Float) -> Unit,
    collapsedHeight: Dp = 64.dp,
    state: MainContract.State,
    onShowIndexInfo: () -> Unit
) {
    val statusHeight: Dp by animateDpAsState(
        targetValue = max(
            this.maxWidth * (1 - scrollBehavior.state.collapsedFraction),
            collapsedHeight
        )
    )

    val currentIndex by remember(state.uvCurrentData?.index) {
        derivedStateOf {
            state.uvCurrentData?.index?.roundToInt() ?: 0
        }
    }

    val maxIndex by remember(state.uvCurrentSummaryDayData) {
        derivedStateOf {
            state.uvCurrentSummaryDayData?.maxIndex?.getIntIndex() ?: 0
        }
    }

    val context = LocalContext.current

    @Suppress("NAME_SHADOWING")
    val titleString by remember(
        state.uvCurrentData?.index,
        state.currentSunData?.position,
        context
    ) {
        derivedStateOf {
            val currentIndex = state.uvCurrentData?.index?.roundToInt() ?: Int.MIN_VALUE
            val array = context.resources.getStringArray(R.array.uvindex_status_info)
            getUVITitle(currentIndex, state.currentSunData?.position ?: SunPosition.Above, array)
        }
    }

    TopAppBar(
        modifier = modifier
            .statusBarsPadding()
            .height(statusHeight),
        title = {},
        colors = TopAppBarDefaults.smallTopAppBarColors(
            containerColor = Color.Transparent,
            scrolledContainerColor = Color.Transparent,
            titleContentColor = Color.Transparent
        ),
        scrollBehavior = scrollBehavior
    )

    Column(
        Modifier
            .statusBarsPadding()
            .height(statusHeight)
    ) {

        val inverseSurface = contentColorFor(MaterialTheme.colorScheme.inverseSurface)
        val surface = contentColorFor(MaterialTheme.colorScheme.surface)

        MainCurrentInfoTopBarInnerPart(
            minHeight = collapsedHeight,
            collapsedFraction = scrollBehavior.state.collapsedFraction,
            textStyles = MainTopBarDefaults.mainTopBarTextStyles(
                placeExpandedStyle = MaterialTheme.typography.labelLarge.copy(color = Color.White),
                placeCollapsedStyle = MaterialTheme.typography.labelLarge.copy(color = inverseSurface),
                titleExpandedStyle = MaterialTheme.typography.displaySmall.copy(color = Color.White),
                titleCollapsedStyle = MaterialTheme.typography.titleLarge.copy(color = surface),
                riseSetTextStyle = MaterialTheme.typography.labelLarge.copy(color = inverseSurface),
                indexExpandedStyle = MaterialTheme.typography.displayLarge
                    .copy(fontWeight = FontWeight.SemiBold, fontSize = 72.sp)
                    .copy(color = surface),
                indexCollapsedStyle = MaterialTheme.typography.titleLarge.copy(color = surface),
                peakHourStyle = MaterialTheme.typography.labelLarge.copy(color = surface),
            ),
            placeContent = placeContent,
//            {
//                MainPlacePart(
//                    modifier = Modifier
//                        .padding(horizontal = Dimens.grid_1)
//                        .fillMaxWidth(),
//                    onEditPlace = onEditPlace,
//                    place = state.place
//                )
//            }
//            ,
            titleContent = {
                val style = LocalTextStyle.current
                TextButton(
                    colors = ButtonDefaults.textButtonColors(contentColor = LocalContentColor.current),
                    modifier = Modifier.padding(end = Dimens.grid_1),
                    contentPadding = PaddingValues(0.dp),
                    onClick = {
                        if (currentIndex > 2) onShowIndexInfo()
                    },
                    enabled = currentIndex > 2

                ) {
                    Text(
                        modifier = Modifier
                            .padding(horizontal = Dimens.grid_2),
                        text = titleString,
                        style = style
                    )
                }

            },
            indexContent = {
                IndexParts(
                    modifier = Modifier.padding(horizontal = Dimens.grid_2),
                    currentIndex = currentIndex, maxIndex = maxIndex
                )
            },
            subTitleContent = {
                IconsInfo(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            start = Dimens.grid_1,
                            end = Dimens.grid_1,
                            top = Dimens.grid_2
                        ),
                    currentIndexValue = state.uvCurrentData?.index,
                    onClick = onShowIndexInfo
                )
            },
            maxTimeContent = {
                PeakTime(
                    modifier = Modifier.padding(start = 0.dp, end = Dimens.grid_2),
                    maxTime = state.peakTime
                )
            }
        )
    }
}

@Composable
internal fun IconsInfo(
    modifier: Modifier = Modifier,
    currentIndexValue: Double?,
    onClick: () -> Unit
) {

    val currentIndexInt by remember(currentIndexValue) {
        derivedStateOf {
            UVLevel.valueOf(currentIndexValue?.roundToInt() ?: Int.MIN_VALUE) ?: UVLevel.Low
//            currentIndexValue?.roundToInt() ?: Int.MIN_VALUE
        }
    }

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.End,
        verticalAlignment = Alignment.CenterVertically
    ) {


        if (currentIndexInt > UVLevel.Low) {
            InfoIconButton(
                modifier = Modifier.size(48.dp),
                id = R.drawable.ic_glasses,
                onClick = onClick
            )
            Spacer(modifier = Modifier.width(8.dp))
        }

        if (currentIndexInt > UVLevel.Low) {
            InfoIconButton(
                modifier = Modifier.size(36.dp),
                id = R.drawable.ic_sunblock_alt,
                onClick = onClick
            )

            Spacer(modifier = Modifier.width(8.dp))

            InfoIconButton(
                modifier = Modifier.size(48.dp),
                id = R.drawable.ic_hat,
                onClick = onClick
            )

            Spacer(modifier = Modifier.width(8.dp))
        }

        if (currentIndexInt > UVLevel.Moderate) {

            InfoIconButton(
                modifier = Modifier.size(40.dp),
                id = R.drawable.ic_shirt,
                onClick = onClick
            )

            Spacer(modifier = Modifier.width(8.dp))

        }

        if (currentIndexInt > UVLevel.High) {
            InfoIconButton(
                modifier = Modifier.size(40.dp),
                id = R.drawable.beach_shadow,
                onClick = onClick
            )
        }
    }
}

@Composable
private fun InfoIconButton(
    modifier: Modifier = Modifier,
    @DrawableRes id: Int,
    onClick: () -> Unit
) {
    Icon(
        modifier = modifier
            .clickable(
                onClick = onClick,
                enabled = true,
                role = Role.Button,
                interactionSource = remember { MutableInteractionSource() },
                indication = rememberRipple(
                    bounded = false,
                    radius = 48.dp / 2,
                    color = MaterialTheme.colorScheme.inverseOnSurface
                )
            ),
        tint = Color.White,
        painter = painterResource(id = id),
        contentDescription = null
    )
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
private fun IndexParts(
    modifier: Modifier = Modifier,
    currentIndex: Int,
    maxIndex: Int
) {
    Row(
        modifier = modifier
    ) {
        AnimatedContent(
            targetState = currentIndex,
            transitionSpec = {
                if (targetState > initialState) {
                    slideInVertically { height -> height } + fadeIn() with
                            slideOutVertically { height -> -height } + fadeOut()
                } else {
                    slideInVertically { height -> -height } + fadeIn() with
                            slideOutVertically { height -> height } + fadeOut()
                }.using(
                    SizeTransform(clip = false)
                )
            }
        ) { state ->

            Text(
                text = state.toString()
            )

        }
        Text(
            text = "/"
        )
        Crossfade(targetState = maxIndex) {
            Text(
                text = it.toString()
            )
        }
    }
}

@Composable
private fun PeakTime(
    modifier: Modifier = Modifier,
    maxTime: LocalTime?
) {

    val stringHour by remember(maxTime) {
        derivedStateOf {
            maxTime?.format(DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT))
        }
    }

    Crossfade(
        modifier = modifier,
        targetState = stringHour
    ) { state ->
        when (state) {
            null -> {}
            else -> {
                Column {
                    Text(text = stringResource(id = R.string.uvindex_peak_time))
                    Text(text = state)
                }
            }
        }
    }
}
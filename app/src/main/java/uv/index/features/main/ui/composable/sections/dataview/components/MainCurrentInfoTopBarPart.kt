package uv.index.features.main.ui.composable.sections.dataview.components

import androidx.compose.animation.*
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.max
import androidx.compose.ui.unit.sp
import uv.index.R
import uv.index.features.main.common.getUVITitle
import uv.index.features.main.domain.SunPosition
import uv.index.features.main.ui.MainContract
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BoxWithConstraintsScope.MainCurrentInfoTopBarPart(
    modifier: Modifier = Modifier,
    scrollBehavior: TopAppBarScrollBehavior,
    collapsedHeight: Dp = 64.dp,
    state: MainContract.State,
    onEditPlace: () -> Unit
) {
    val statusHeight: Dp by animateDpAsState(
        targetValue = max(
            this.maxWidth * (1 - scrollBehavior.state.collapsedFraction),
            collapsedHeight
        )
    )

    val currentIndex by remember(state.currentIndexValue) {
        derivedStateOf {
            state.currentIndexValue?.roundToInt() ?: 0
        }
    }

    val maxIndex by remember(state.currentSummaryDayData) {
        derivedStateOf {
            state.currentSummaryDayData?.maxIndex?.getIntIndex() ?: 0
        }
    }

    val context = LocalContext.current

    @Suppress("NAME_SHADOWING")
    val titleString by remember(
        state.currentIndexValue,
        state.currentSunPosition,
        context
    ) {
        derivedStateOf {
            val currentIndex = state.currentIndexValue?.roundToInt() ?: Int.MIN_VALUE
            val array = context.resources.getStringArray(R.array.uvindex_status_info)
            getUVITitle(currentIndex, state.currentSunPosition ?: SunPosition.Above, array)
        }
    }

    SmallTopAppBar(
        modifier = modifier
            .statusBarsPadding()
            .height(statusHeight),
        title = {
            Column(Modifier.statusBarsPadding()) {

                val inverseSurface = contentColorFor(MaterialTheme.colorScheme.inverseSurface)
                val surface = contentColorFor(MaterialTheme.colorScheme.surface)

                MainCurrentInfoTopBarInnerPart(
                    minHeight = collapsedHeight,
                    collapsedFraction = scrollBehavior.state.collapsedFraction,
                    textStyles = MainTopBarDefaults.mainTopBarTextStyles(
                        placeExpandedStyle = MaterialTheme.typography.labelLarge.copy(color = inverseSurface),
                        placeCollapsedStyle = MaterialTheme.typography.labelLarge.copy(color = inverseSurface),
                        titleExpandedStyle = MaterialTheme.typography.displaySmall.copy(color = inverseSurface),
                        titleCollapsedStyle = MaterialTheme.typography.titleLarge.copy(color = surface),
                        riseSetTextStyle = MaterialTheme.typography.labelLarge.copy(color = inverseSurface),
                        indexExpandedStyle = MaterialTheme.typography.displayLarge
                            .copy(fontWeight = FontWeight.SemiBold, fontSize = 72.sp)
                            .copy(color = surface),
                        indexCollapsedStyle = MaterialTheme.typography.titleLarge.copy(color = surface),
                        peakHourStyle = MaterialTheme.typography.labelLarge.copy(color = surface),
                    ),
                    placeContent = {
                        MainPlacePart(
                            modifier = Modifier
                                .padding(end = 8.dp)
                                .fillMaxWidth(),
                            onEditPlace = onEditPlace
                        )
                    },
                    titleContent = {
                        Text(
                            modifier = Modifier.padding(end = 16.dp),
                            text = titleString,
                        )
                    },
                    indexContent = {
                        IndexParts(currentIndex = currentIndex, maxIndex = maxIndex)
                    },
                    subTitleContent = {
                        IconsInfo(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(end = 16.dp, top = 16.dp),
                            currentIndexValue = state.currentIndexValue
                        )
                    },
                    maxTimeContent = {
                        PeakTime(
                            modifier = Modifier.padding(start = 8.dp, end = 16.dp),
                            maxTime = state.currentPeakTime
                        )
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

@Composable
private fun IconsInfo(
    modifier: Modifier = Modifier,
    currentIndexValue: Double?
) {

    val currentIndexInt by remember(currentIndexValue) {
        derivedStateOf {
            currentIndexValue?.roundToInt() ?: Int.MIN_VALUE
        }
    }

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.End,
        verticalAlignment = Alignment.CenterVertically
    ) {

        if (currentIndexInt > 2) {
            Icon(
                modifier = Modifier.size(48.dp),
                tint = Color.White,
                painter = painterResource(id = R.drawable.ic_glasses),
                contentDescription = ""
            )
            Spacer(modifier = Modifier.width(8.dp))
        }

        if (currentIndexInt > 2) {
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
        }

        if (currentIndexInt > 4) {

            Icon(
                modifier = Modifier.size(40.dp),
                tint = Color.White,
                painter = painterResource(id = R.drawable.ic_shirt),
                contentDescription = ""
            )

            Spacer(modifier = Modifier.width(8.dp))

        }

        if (currentIndexInt > 6) {
            Icon(
                modifier = Modifier.size(40.dp),
                tint = Color.White,
                painter = painterResource(id = R.drawable.beach_shadow),
                contentDescription = ""
            )
        }
    }
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
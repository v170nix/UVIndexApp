package uv.index.parts.main.ui.composable

import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Card
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import uv.index.R
import uv.index.parts.main.ui.MainContract

@Composable
internal fun MainTimeToEventPart(
    modifier: Modifier = Modifier,
    timeToBurn: MainContract.TimeToBurn?
) {

    val context = LocalContext.current

    val timeToBurnString by remember(timeToBurn, context) {
        derivedStateOf {
            when (timeToBurn) {
                MainContract.TimeToBurn.Infinity -> "∞"
                is MainContract.TimeToBurn.Value -> {
                    buildString {
                        timeToString(
                            context,
                            (timeToBurn.minTimeInMins + (timeToBurn.maxTimeInMins
                                ?: (1.5 * timeToBurn.minTimeInMins)).toInt()) / 2
                        )
                    }
                }
                else -> ""
            }
        }
    }

    val timeToVitaminD by remember(timeToBurn, context) {
        derivedStateOf {
            when (timeToBurn) {
                MainContract.TimeToBurn.Infinity -> "∞"
                is MainContract.TimeToBurn.Value -> {
                    var time = (timeToBurn.minTimeInMins + (timeToBurn.maxTimeInMins
                        ?: (1.5 * timeToBurn.minTimeInMins)).toInt()) / 6
                    time = (time / 5) * 5
                    buildString {
                        timeToString(context, time)
                    }
                }
                else -> ""
            }
        }
    }

    Row(
        modifier = modifier.height(IntrinsicSize.Max),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        InnerCard(
            modifier = Modifier.weight(1f),
            info = timeToBurnString,
            description = stringResource(id = R.string.uvindex_sunburn_title)
        )

        InnerCard(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight(),
            info = timeToVitaminD,
            description = stringResource(id = R.string.uvindex_vitamin_D_title)
        )
    }
}

private fun StringBuilder.timeToString(context: Context, time: Int): StringBuilder {
    val (hourPart, minPart) = timeInMinsToHHMM(time)
    if (hourPart > 0) {
        append(hourPart)
        append(" ")
        append(context.getString(uv.index.R.string.uvindex_sunburn_hour_part))
        append(" ")
    }
    if (minPart > 0) {
        append(minPart)
        append(" ")
        append(context.getString(uv.index.R.string.uvindex_sunburn_min_part))
    }

    return this
}

private fun timeInMinsToHHMM(time: Int): Pair<Int, Int> {
    return getHourPart(time) to getMinPart(time)
}

private fun getMinPart(duration: Int) = duration % 60
private fun getHourPart(duration: Int) = duration / 60

@Composable
private fun InnerCard(
    modifier: Modifier = Modifier,
    info: String,
    description: String
) {
    Card(
        modifier = modifier,
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = info,
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.titleMedium
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = description,
                style = MaterialTheme.typography.labelLarge,
                color = LocalContentColor.current.copy(alpha = 0.6f),
                textAlign = TextAlign.Center
            )
        }
    }
}
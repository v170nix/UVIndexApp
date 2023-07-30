@file:Suppress("MagicNumber")
package uv.index.features.main.ui.composable.sections.dataview.components

import android.content.Context
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Card
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import uv.index.R
import uv.index.features.main.ui.MainContract
import uv.index.features.uvi.ui.rememberTimeToBurnString
import uv.index.features.uvi.ui.rememberTimeToVitaminDString

@Composable
internal fun MainTimeToEventPart(
    modifier: Modifier = Modifier,
    infoState: MainInfoState,
    timeToBurn: MainContract.TimeToEvent?,
    timeToVitaminD: MainContract.TimeToEvent?
) {
    val resource = LocalContext.current.resources

    Row(
        modifier = modifier.height(IntrinsicSize.Max),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        InnerCard(
            modifier = Modifier.weight(1f),
            info = rememberTimeToBurnString(timeToBurn),
            description = stringResource(id = R.string.uvindex_sunburn_title),
            onClick = {
                infoState.data = MainInfoData(
                    headline = resource.getString(R.string.uvindex_sunburn_title),
                    info = resource.getString(R.string.uvindex_sunburn_info),
                )
                infoState.openDialog = true
            }
        )

        InnerCard(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight(),
            info = rememberTimeToVitaminDString(timeToVitaminD),
            description = stringResource(id = R.string.uvindex_vitamin_D_title),
            onClick = {
                infoState.data = MainInfoData(
                    headline = resource.getString(R.string.uvindex_vitamin_D_title),
                    info = resource.getString(R.string.uvindex_vitamin_D_info)
                )
                infoState.openDialog = true
            }
        )
    }
}

private fun StringBuilder.timeToString(context: Context, time: Int): StringBuilder {
    val (hourPart, minPart) = timeInMinsToHHMM(time)
    if (hourPart > 0) {
        append(hourPart)
        append(" ")
        append(context.getString(R.string.uvindex_sunburn_hour_part))
        append(" ")
    }
    if (minPart > 0) {
        append(minPart)
        append(" ")
        append(context.getString(R.string.uvindex_sunburn_min_part))
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
    description: String,
    onClick: () -> Unit
) {
    Card(modifier = modifier)
    {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .clickable(onClick = onClick)
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
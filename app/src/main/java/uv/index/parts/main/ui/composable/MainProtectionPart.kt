package uv.index.parts.main.ui.composable

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import uv.index.common.AutoSizeText
import uv.index.lib.data.UVSummaryDayData
import uv.index.parts.main.common.rememberPeriod

@Composable
internal fun MainProtectionPart(
    modifier: Modifier = Modifier,
    uvSummaryDayData: UVSummaryDayData?
) {
    CompositionLocalProvider(
        LocalContentColor provides contentColorFor(
            MaterialTheme.colorScheme.surface
        )
    ) {
        Crossfade(
            modifier = modifier,
            targetState = rememberPeriod(data = uvSummaryDayData)
        ) {
            when (it) {
                null -> {
                    Text(
                        modifier = Modifier.fillMaxWidth(),
                        text = stringResource(id = uv.index.R.string.uvindex_current_protection_not_required).uppercase(),
                        style = MaterialTheme.typography.labelLarge,
                        textAlign = TextAlign.Center
                    )
                }
                else -> {
                    Column(
                        modifier = modifier,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            modifier = Modifier,
                            text = stringResource(id = uv.index.R.string.uvindex_current_protection_required_first_part),
                            style = MaterialTheme.typography.labelLarge
                        )

                        Box(modifier = Modifier.fillMaxWidth(0.7f)) {
                            AutoSizeText(
                                text = stringResource(
                                    id = uv.index.R.string.uvindex_current_protection_required_second_part,
                                    it.first,
                                    it.second
                                ),
                                textStyle = MaterialTheme.typography.headlineLarge
                            )
                        }
                    }
                }
            }

        }
    }

}
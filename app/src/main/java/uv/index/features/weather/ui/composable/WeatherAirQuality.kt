package uv.index.features.weather.ui.composable

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import uv.index.R
import uv.index.features.main.ui.composable.sections.ClickableCard
import uv.index.features.weather.data.Weather
import uv.index.features.weather.ui.getAQIColor
import uv.index.features.weather.ui.rememberAQIDescription
import uv.index.features.weather.ui.rememberAQIText
import uv.index.ui.theme.Dimens


// https://www.airnow.gov/sites/default/files/2023-03/air-quality-guide-for-particle-pollution_0.pdf
// https://www.airnow.gov/aqi/aqi-basics/
// https://en.wikipedia.org/wiki/Air_quality_index

@Composable
fun WeatherAirQuality(
    modifier: Modifier = Modifier,
    data: Weather.AirQuality
) {

    ClickableCard(
        modifier = modifier,
        onClick = {

        }
    ) {
        Column(
            modifier = Modifier
                .padding(Dimens.grid_2),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {

            Text(
                text = stringResource(id = R.string.uvindex_weather_aqi_title).uppercase(),
                style = MaterialTheme.typography.labelLarge,
                color = LocalContentColor.current.copy(alpha = 0.7f)
            )

            val epaText = rememberAQIText(data.usEPAIndex)

            val epaString by remember(data.usEPAIndex, epaText) {
                derivedStateOf {
                    buildString {
                        append(data.usEPAIndex)
                        append(" - ")
                        append(epaText)
                    }
                }
            }

            Text(
                text = epaString,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
//                color = LocalContentColor.current.copy(alpha = 0.7f)
            )

            Text(
                text = rememberAQIDescription(data.usEPAIndex),
                style = MaterialTheme.typography.bodyMedium,
                color = LocalContentColor.current.copy(alpha = 0.7f)
            )

            Row(modifier = Modifier.fillMaxWidth()) {
                for (i in 1..6) {
                    Box(
                        modifier = Modifier.weight(1f),
                    ) {
                        Spacer(
                            modifier = Modifier
                                .padding(vertical = 16.dp)
                                .fillMaxWidth()
                                .height(8.dp)
                                .background(
                                    color = getAQIColor(
                                        epaIndex = i,
                                        transparentColor = Color.Transparent
                                    )
                                )

                        )

                        if (i == data.usEPAIndex) {
                            Icon(
                                modifier = Modifier
                                    .align(Alignment.TopCenter),
                                imageVector = Icons.Default.ArrowDropDown,
                                contentDescription = null
                            )
                            Icon(
                                modifier = Modifier
                                    .align(Alignment.BottomCenter)
                                    .rotate(180f),
                                imageVector = Icons.Default.ArrowDropDown,
                                contentDescription = null
                            )
                            Spacer(
                                modifier = Modifier
                                    .height(14.dp)
                                    .width(1.dp)
                                    .align(Alignment.Center)
                                    .background(LocalContentColor.current)
                            )
                        }
                    }

                }
            }

        }
    }

}
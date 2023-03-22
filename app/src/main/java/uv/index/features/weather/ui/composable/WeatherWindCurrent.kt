package uv.index.features.weather.ui.composable

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import uv.index.R
import uv.index.features.preferences.ui.rememberWeatherDisplayMode
import uv.index.features.weather.data.Weather
import uv.index.features.weather.ui.rememberWindText
import uv.index.ui.theme.Dimens

@Composable
fun WeatherWindCurrent(
    modifier: Modifier,
    wind: Weather.Wind,
) {

    val displayMode = rememberWeatherDisplayMode()

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Icon(
                modifier = Modifier
                    .rotate(180f + wind.degree.toFloat())
                    .size(56.dp),
                imageVector = ImageVector.vectorResource(id = R.drawable.ic_compass_arrow),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onTertiary
            )
        }

        Column(
            horizontalAlignment = Alignment.Start,
        ) {

            Row(
                horizontalArrangement = Arrangement.Start
            ) {

                Text(
                    modifier = Modifier,
                    text = "Легкий ",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                )

                Text(
                    modifier = Modifier,
                    text = rememberWindText(displayMode, wind.speed),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                )

            }

            Row(
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    modifier = Modifier,
                    text = "Южный",
                    style = MaterialTheme.typography.bodyMedium,
//                fontWeight = FontWeight.Bold,
                )

                if (wind.gust > wind.speed) {

                    Icon(
                        modifier = Modifier
                            .padding(horizontal = Dimens.grid_0_5)
                            .size(8.dp),
                        imageVector = ImageVector.vectorResource(id = R.drawable.ic_circle),
                        contentDescription = null
                    )

                    Text(
                        modifier = Modifier,
                        text = "Порывы ",
                        style = MaterialTheme.typography.bodyMedium,
//                fontWeight = FontWeight.Bold,
                    )

                    Text(
                        modifier = Modifier,
                        text = rememberWindText(displayMode, wind.gust),
                        style = MaterialTheme.typography.bodyMedium,
//                fontWeight = FontWeight.Bold,
                    )
                }

            }
        }

    }



}
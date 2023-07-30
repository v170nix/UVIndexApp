package uv.index.features.weather.ui.composable

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import uv.index.R
import uv.index.features.preferences.ui.rememberWeatherMetricsMode
import uv.index.features.weather.data.Weather
import uv.index.features.weather.ui.rememberBeaufortText
import uv.index.features.weather.ui.rememberPointOfCompassText
import uv.index.features.weather.ui.rememberWindText
import uv.index.ui.theme.Dimens

@Composable
fun LazyItemScope.WeatherWindCurrent(
    modifier: Modifier,
    wind: Weather.Wind,
) {

    val displayMode = rememberWeatherMetricsMode()

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(Dimens.grid_1)
    ) {

        Row(
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.spacedBy(Dimens.grid_2)
        ) {

            Icon(
                modifier = Modifier
                    .rotate(180f + wind.degree.toFloat())
                    .size(56.dp),
                imageVector = ImageVector.vectorResource(id = R.drawable.ic_compass_arrow),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onTertiary
            )

            Column {
                Text(
                    modifier = Modifier,
                    text = rememberBeaufortText(wind.speed),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                )

                Text(
                    modifier = Modifier,
                    text = rememberPointOfCompassText(wind.degree),
                    style = MaterialTheme.typography.bodyMedium,
                )
            }

        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(Dimens.grid_0_5),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Text(
                text = stringResource(
                    id = R.string.weather_wind_speed_title,
                    rememberWindText(displayMode, wind.speed)
                ),
                style = MaterialTheme.typography.bodyMedium,
            )

            if (wind.gust > wind.speed) {

                Icon(
                    modifier = Modifier
                        .padding(horizontal = Dimens.grid_0_5)
                        .size(Dimens.grid_0_5),
                    imageVector = ImageVector.vectorResource(id = R.drawable.ic_circle),
                    contentDescription = null
                )

                Text(
                    modifier = Modifier,
                    text = stringResource(
                        id = R.string.weather_wind_gusts_title,
                        rememberWindText(displayMode, wind.gust)
                    ),
                    style = MaterialTheme.typography.bodyMedium,
                )
            }

        }
    }


}
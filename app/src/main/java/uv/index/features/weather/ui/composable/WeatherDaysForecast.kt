package uv.index.features.weather.ui.composable

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import uv.index.features.astronomy.data.SunPosition
import uv.index.features.weather.data.Weather
import uv.index.features.weather.ui.rememberConditionIcon
import uv.index.ui.theme.Dimens

@Composable
fun WeatherDaysForecast(
    modifier: Modifier,
    data: Weather.Data
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(Dimens.grid_0_5)
    ) {
        data.days.forEach {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {

                Text(
                    text = it.date.toString()
                )
                Text(
                    text = it.temperatureMin.value.toString()
                )
                Text(
                    text = it.temperatureMax.value.toString()
                )

                val weatherId = rememberConditionIcon(it.condition, SunPosition.Above)

                if (weatherId != null) {
                    Icon(
                        imageVector = ImageVector.vectorResource(id = weatherId),
                        contentDescription = "",
                        tint = MaterialTheme.colorScheme.onTertiary
                    )
                } else {
                    Spacer(modifier = Modifier.size(48.dp))
                }
            }
        }


    }
}
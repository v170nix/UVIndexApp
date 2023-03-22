package uv.index.features.weather.ui.composable

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Divider
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import uv.index.features.weather.data.Weather
import uv.index.ui.theme.Dimens

@Composable
fun WeatherCurrentInfo(
    modifier: Modifier = Modifier,
    data: Weather.Data
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            WeatherRow(
                title = "Влажность",
                value = "${data.realTime.humidity.percentage}%"
            )
            Divider(
                modifier = Modifier.padding(vertical = Dimens.grid_1)
            )
            WeatherRow(
                title = "Давление",
                value = data.realTime.pressure.millibars.toString() + "мб"
            )
        }
        Divider(
            modifier = Modifier
                .padding(horizontal = Dimens.grid_1)
                .fillMaxHeight()
                .width(1.dp)
                .heightIn(32.dp)
//                .padding(horizontal = Dimens.grid_1)
                .background(color = DividerDefaults.color)
        )
        Column(modifier = Modifier.weight(1f)) {
            WeatherRow(
                title = "Ветер",
                value = data.realTime.wind.speed.value.toString() + "км/ч"
            )
            Divider(
                modifier = Modifier.padding(vertical = Dimens.grid_1)
            )
            WeatherRow(
                title = "Порывы",
                value = data.realTime.wind.gust.value.toString() + "км/ч"
            )
        }
    }
}

@Composable
private fun WeatherRow(
    modifier: Modifier = Modifier,
    title: String,
    value: String
) {
    Row(
        modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.bodyMedium
        )
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium
        )
    }

}
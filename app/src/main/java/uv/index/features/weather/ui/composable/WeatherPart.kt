package uv.index.features.weather.ui.composable

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import uv.index.R
import uv.index.features.weather.data.Weather
import uv.index.ui.theme.Dimens
import java.time.ZoneId

// https://windy.app/forecast2/spot/36345/Russian+Federation+-+Sankt-Peterburg
// https://api.open-meteo.com/v1/forecast?latitude=60.06&longitude=30.33&daily=weathercode,temperature_2m_max,temperature_2m_min&timezone=auto

fun LazyListScope.weatherPart(
    zoneId: ZoneId,
    data: Weather.Data?,
) {

    item(key = 21) {
//        Text("weather part", modifier = Modifier.animateItemPlacement())
        if (data != null) {
            WeatherCurrentInfo(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(Dimens.grid_2),
                data = data
            )
        }
    }
    item(key = 22) {
        if (data?.realTime?.airQuality != null) {
            WeatherAirQuality(
                modifier = Modifier
                    .padding(horizontal = Dimens.grid_2)
                    .fillMaxWidth(),
                data = data.realTime.airQuality
            )
        }
    }
    item(key = 23) {
        if (data != null) {

            Spacer(modifier = Modifier.height(Dimens.grid_3))

            Text(
                modifier = Modifier
                    .padding(horizontal = Dimens.grid_2)
                    .fillMaxWidth(),
                text = stringResource(id = R.string.weather_hourly_forecast).uppercase(),
                style = MaterialTheme.typography.labelLarge,
                color = LocalContentColor.current.copy(alpha = 0.7f)
            )

            Spacer(modifier = Modifier.height(Dimens.grid_1))

            WeatherTemperatureHours(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(Dimens.grid_2),
                data = data
            )

            Spacer(modifier = Modifier.height(Dimens.grid_2))

            Text(
                modifier = Modifier
                    .padding(horizontal = Dimens.grid_2)
                    .fillMaxWidth(),
                text = stringResource(id = R.string.weather_wind_title).uppercase(),
                style = MaterialTheme.typography.labelLarge,
                color = LocalContentColor.current.copy(alpha = 0.7f)
            )

            Spacer(modifier = Modifier.height(Dimens.grid_1))

            WeatherWindCurrent(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = Dimens.grid_2),
                wind = data.realTime.wind
            )

            Spacer(modifier = Modifier.height(16.dp))

            WeatherWindHours(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(Dimens.grid_2),
                data = data
            )

            Spacer(modifier = Modifier.height(Dimens.grid_3))

            Text(
                modifier = Modifier
                    .padding(horizontal = Dimens.grid_2)
                    .fillMaxWidth(),
                text = "Осадки".uppercase(),
                style = MaterialTheme.typography.labelLarge,
                color = LocalContentColor.current.copy(alpha = 0.7f)
            )

            Text(
                modifier = Modifier
                    .padding(horizontal = Dimens.grid_2)
                    .fillMaxWidth(),
                text = "вероятность",
                style = MaterialTheme.typography.labelSmall,
                color = LocalContentColor.current.copy(alpha = 0.7f)
            )

//            Spacer(modifier = Modifier.height(16.dp))

            WeatherPrecipitationHours(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(Dimens.grid_2),
                data = data
            )



//            WeatherHours(
//                modifier = Modifier.fillMaxWidth().padding(Dimens.grid_2),
//                zoneId = zoneId,
//                data = data
//            )

        }
    }

    item(key = 400) {
        if (data != null) {
            WeatherDaysForecast(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(Dimens.grid_2),
                data = data
            )
        }
    }

    item(key = 300) {
        Spacer(modifier = Modifier.height(64.dp))
    }
}
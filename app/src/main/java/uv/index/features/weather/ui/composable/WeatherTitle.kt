package uv.index.features.weather.ui.composable

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import uv.index.features.astronomy.data.SunPosition
import uv.index.features.weather.data.Weather
import uv.index.features.weather.ui.getName
import uv.index.ui.theme.Dimens

@Composable
fun WeatherTitle(
    modifier: Modifier = Modifier,
    style: TextStyle = LocalTextStyle.current,
    condition: Weather.Condition?,
    sunPosition: SunPosition,
) {
    if (condition != null)
        TextButton(
            colors = ButtonDefaults.textButtonColors(contentColor = LocalContentColor.current),
            modifier = modifier,
            contentPadding = PaddingValues(0.dp),
            enabled = false,
            onClick = {}
        ) {
            Text(
                modifier = Modifier.padding(horizontal = Dimens.grid_2),
                text = condition.getName(Locale.current.language, sunPosition == SunPosition.Above),
                style = style,
                textAlign = TextAlign.End
            )
        }
}
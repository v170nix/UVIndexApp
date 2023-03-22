package uv.index.features.weather.ui

import androidx.annotation.DrawableRes
import androidx.collection.SparseArrayCompat
import androidx.collection.getOrDefault
import androidx.collection.getOrElse
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import uv.index.R
import uv.index.features.astronomy.data.SunPosition
import uv.index.features.weather.data.Weather

@DrawableRes
fun Weather.Condition.getIconId(isDay: Boolean): Int? {
    return when (this.code) {
        1000 -> if (isDay) R.drawable.w_sunny else R.drawable.w_clear
        1003 -> if (isDay) R.drawable.w_partly_cloudy_day else R.drawable.w_partly_cloudy_night // Partly cloudy
        1006 -> R.drawable.w_cloudy // Cloudy
        1009 -> R.drawable.w_cloudy_filled // Overcast
        1030 -> R.drawable.w_foggy // mist
        1063 -> if (isDay) R.drawable.w_partly_rainy_day else R.drawable.w_partly_rainy_night // Patchy rain possible
        1066 -> if (isDay) R.drawable.w_partly_snow_day else R.drawable.w_partly_snow_night // Patchy snow possible
        1069 -> if (isDay) R.drawable.w_partly_sleet_day else R.drawable.w_partly_sleet_night // Patchy sleet
        1072 -> null // Patchy freezing drizzle possible
        1087 -> R.drawable.w_thunderstorm // Thundery outbreaks possible
        1114 -> R.drawable.w_snowing // Blowing snow
        1117 -> R.drawable.w_snowing // Blizzard
        1135 -> R.drawable.w_foggy // Fog
        1147 -> null // Freezing fog
        1150 -> null // Patchy light drizzle
        1153 -> R.drawable.w_rainy // Light drizzle
        1168 -> null // Freezing drizzle
        1171 -> null // Heavy freezing drizzle
        1080 -> if (isDay) R.drawable.w_partly_rainy_day else R.drawable.w_partly_rainy_night // Patchy light rain
        1183 -> R.drawable.w_rainy // Light rain
        1186 -> if (isDay) R.drawable.w_partly_rainy_day else R.drawable.w_partly_rainy_night  // Moderate rain at times
        1189 -> R.drawable.w_rainy // Moderate rain
        1192 -> if (isDay) R.drawable.w_partly_rainy_day else R.drawable.w_partly_rainy_night // Heavy rain at times
        1195 -> R.drawable.w_rainy // Heavy rain
        1198 -> null // Light freezing rain
        1201 -> null // Moderate or heavy freezing rain
        1204 -> null // Light sleet
        1207 -> null // Moderate or heavy sleet
        1210 -> if (isDay) R.drawable.w_partly_snow_day else R.drawable.w_partly_snow_night // Patchy light snow
        1213 -> R.drawable.w_snowing // Light snow
        1216 -> if (isDay) R.drawable.w_partly_snow_day else R.drawable.w_partly_snow_night // Patchy moderate snow
        1219 -> R.drawable.w_snowing // Moderate snow
        1222 -> if (isDay) R.drawable.w_partly_snow_day else R.drawable.w_partly_snow_night // Patchy heavy snow
        1225 -> R.drawable.w_snowing // Heavy snow
        1237 -> null // Ice pellets
        1240 -> R.drawable.w_rainy // Light rain shower
        1243 -> R.drawable.w_rainy // Moderate or heavy rain shower
        1246 -> R.drawable.w_rainy // Torrential rain shower
        1249 -> R.drawable.w_snowing // Light sleet showers
        1252 -> R.drawable.w_snowing // Moderate or heavy sleet showers
        1255 -> R.drawable.w_snowing // Light snow showers
        1258 -> R.drawable.w_snowing // Moderate or heavy snow showers
        1261 -> null // Light showers of ice pellets
        1264 -> null // Moderate or heavy showers of ice pellets
        1273 -> R.drawable.w_thunderstorm // Patchy light rain with thunder
        1276 -> R.drawable.w_thunderstorm // Moderate or heavy rain with thunder
        1279 -> R.drawable.w_thunderstorm // Patchy light snow with thunder
        1282 -> R.drawable.w_thunderstorm // Moderate or heavy snow with thunder
        else -> null
    }
}

fun Weather.Condition.getName(language: String, isDay: Boolean): String {

    val dayArray = when (language) {
        "ru" -> conditionNamesDayRu
        else -> conditionNamesDayEn
    }

    val defaultArray = when (language) {
        "ru" -> conditionNamesRu
        else -> conditionNamesEn
    }

    return if (isDay) dayArray.getOrElse(code) {
        defaultArray.getOrDefault(code, "")
    } else {
        defaultArray.getOrDefault(code, "")
    }
}

private val conditionNamesEn by lazy {
    SparseArrayCompat<String>(45).apply {
        put(1000, "Clear")
        put(1003, "Partly cloudy")
        put(1006, "Cloudy")
        put(1009, "Overcast")
        put(1030, "Mist")
        put(1063, "Patchy rain possible")
        put(1066, "Patchy snow possible")
        put(1069, "Patchy sleet possible")
        put(1072, "Patchy freezing drizzle possible")
        put(1087, "Thundery outbreaks possible")
        put(1114, "Blowing snow")
        put(1117, "Blizzard")
        put(1135, "Fog")
        put(1147, "Freezing fog")
        put(1150, "Patchy light drizzle")
        put(1153, "Light drizzle")
        put(1168, "Freezing drizzle")
        put(1171, "Heavy freezing drizzle")
        put(1180, "Patchy light rain")
        put(1183, "Light rain")
        put(1186, "Moderate rain at times")
        put(1189, "Moderate rain")
        put(1192, "Heavy rain at times")
        put(1195, "Heavy rain")
        put(1198, "Light freezing rain")
        put(1201, "Moderate or heavy freezing rain")
        put(1204, "Light sleet")
        put(1207, "Moderate or heavy sleet")
        put(1210, "Patchy light snow")
        put(1213, "Light snow")
        put(1216, "Patchy moderate snow")
        put(1219, "Moderate snow")
        put(1222, "Patchy heavy snow")
        put(1225, "Heavy snow")
        put(1237, "Ice pellets")
        put(1240, "Light rain shower")
        put(1243, "Moderate or heavy rain shower")
        put(1246, "Torrential rain shower")
        put(1249, "Light sleet showers")
        put(1252, "Moderate or heavy sleet showers")
        put(1255, "Light snow showers")
        put(1258, "Moderate or heavy snow showers")
        put(1261, "Light showers of ice pellets")
        put(1264, "Moderate or heavy showers of ice pellets")
        put(1273, "Patchy light rain with thunder")
        put(1276, "Moderate or heavy rain with thunder")
        put(1279, "Patchy light snow with thunder")
        put(1282, "Moderate or heavy snow with thunder")
    }
}

private val conditionNamesDayEn by lazy {
    SparseArrayCompat<String>().apply {
        put(1000, "Sunny")
    }
}

private val conditionNamesRu by lazy {
    SparseArrayCompat<String>(45).apply {
        put(1000, "Ясно")
        put(1003, "Переменная облачность")
        put(1006, "Облачно")
        put(1009, "Пасмурно")
        put(1030, "Дымка")
        put(1063, "Местами дождь")
        put(1066, "Местами снег")
        put(1069, "Местами дождь со снегом")
        put(1072, "Местами замерзающая морось")
        put(1087, "Местами грозы")
        put(1114, "Позёмок")
        put(1117, "Метель")
        put(1135, "Туман")
        put(1147, "Переохлажденный туман")
        put(1150, "Местами слабая морось")
        put(1153, "Слабая морось")
        put(1168, "Замерзающая морось")
        put(1171, "Сильная замерзающая морось")
        put(1180, "Местами небольшой дождь")
        put(1183, "Небольшой дождь")
        put(1186, "Временами умеренный дождь")
        put(1189, "Умеренный дождь")
        put(1192, "Временами сильный дождь")
        put(1195, "Сильный дождь")
        put(1198, "Слабый переохлажденный дождь")
        put(1201, "Умеренный или сильный переохлажденный дождь")
        put(1204, "Небольшой дождь со снегом")
        put(1207, "Умеренный или сильный дождь со снегом")
        put(1210, "Местами небольшой снег")
        put(1213, "Небольшой снег")
        put(1216, "Местами умеренный снег")
        put(1219, "Умеренный снег")
        put(1222, "Местами сильный снег")
        put(1225, "Сильный снег")
        put(1237, "Ледяной дождь")
        put(1240, "Небольшой ливневый дождь")
        put(1243, "Умеренный или сильный ливневый дождь")
        put(1246, "Сильные ливни")
        put(1249, "Небольшой ливневый дождь со снегом")
        put(1252, "Умеренные или сильные ливневые дожди со снегом")
        put(1255, "Небольшой снег")
        put(1258, "Умеренный или сильный снег")
        put(1261, "Небольшой ледяной дождь")
        put(1264, "Умеренный или сильный ледяной дождь")
        put(1273, "В отдельных районах местами небольшой дождь с грозой")
        put(1276, "В отдельных районах умеренный или сильный дождь с грозой")
        put(1279, "В отдельных районах местами небольшой снег с грозой")
        put(1282, "В отдельных районах умеренный или сильный снег с грозой")
    }
}

private val conditionNamesDayRu by lazy {
    SparseArrayCompat<String>().apply {
        put(1000, "Солнечно")
    }
}

@Composable
@DrawableRes
fun rememberConditionIcon(
    condition: Weather.Condition, sunPosition: SunPosition
): Int? {

    val weatherId by remember(
        condition, sunPosition
    ) {
        derivedStateOf {
            condition.getIconId(sunPosition == SunPosition.Above)
        }
    }
    return weatherId
}
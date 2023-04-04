package uv.index.features.weather.data.repository

import com.google.android.gms.maps.model.LatLng
import uv.index.common.remote.Request
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract

sealed class WeatherRequest : Request {
    object Empty : WeatherRequest()
    data class Location(val latLng: LatLng) : WeatherRequest()
}

@OptIn(ExperimentalContracts::class)
fun WeatherRequest.isEmpty(): Boolean {
    contract {
        returns(false) implies (this@isEmpty is WeatherRequest.Location)
    }
    return this == WeatherRequest.Empty
}

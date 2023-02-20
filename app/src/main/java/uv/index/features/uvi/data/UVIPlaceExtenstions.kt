package uv.index.features.main.data

import uv.index.features.place.data.room.PlaceData
import uv.index.lib.data.UVIPlaceData

fun PlaceData.toUVIPlaceData() = UVIPlaceData(
    zone = zone,
    latitude = latLng.latitude,
    longitude = latLng.longitude
)
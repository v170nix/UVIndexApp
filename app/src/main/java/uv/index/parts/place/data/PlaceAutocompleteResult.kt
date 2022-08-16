package uv.index.parts.place.data

import com.google.android.gms.common.api.Status
import com.google.android.libraries.places.api.model.Place

sealed class PlaceAutocompleteResult {
    object Canceled : PlaceAutocompleteResult()
    data class Error(val status: Status) : PlaceAutocompleteResult()
    data class Ok(val place: Place) : PlaceAutocompleteResult()

    fun getPlaceInResult(): Place? {
        if (this is Ok) return this.place
        return null
    }
}

fun Place.getSubTitle(): String {
    val placeName = name
    return addressComponents?.asList()?.run {
        val shortCountry = find { it.types.indexOf("country") > -1 }?.shortName
        val level1 = find { it.types.indexOf("administrative_area_level_1") > -1 }?.name
        val locality = find { it.types.indexOf("locality") > -1 }?.name
        val list = ArrayList<String>()
        if (locality != null && locality != placeName) list += locality
        if (level1 != null && level1 != placeName) list += level1
        if (shortCountry != null && shortCountry != placeName) list += shortCountry
        list.joinToString()
    } ?: ""
}
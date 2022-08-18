package uv.index.features.place.data

import android.content.Context
import android.location.Address
import android.location.Geocoder
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.IOException
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GeocoderRepository @Inject constructor(@ApplicationContext context: Context) {

    private val geocoder: Geocoder? = if (Geocoder.isPresent()) {
        Geocoder(context, Locale.getDefault())
    } else null


    @Throws(IOException::class)
    fun getAddress(latitude: Double, longitude: Double): Address? {
        return geocoder?.getFromLocation(latitude, longitude, 1)?.getOrNull(0)
    }



    fun getAddressOrNull(latitude: Double, longitude: Double): Address? =
        runCatching { getAddress(latitude, longitude) }.getOrNull()

}
package uv.index.features.place.common

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.os.Looper
import androidx.core.content.ContextCompat
import com.google.android.gms.location.*
import kotlinx.coroutines.suspendCancellableCoroutine
import java.util.concurrent.CancellationException
import kotlin.coroutines.resume
import kotlin.math.abs

fun Context.locationCheckPermission() = ContextCompat.checkSelfPermission(
    this,
    Manifest.permission.ACCESS_FINE_LOCATION
) == PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(
    this,
    Manifest.permission.ACCESS_COARSE_LOCATION
) == PackageManager.PERMISSION_GRANTED


//fun Activity.locationIsRationale() = ActivityCompat.shouldShowRequestPermissionRationale(
//    this,
//    Manifest.permission.ACCESS_FINE_LOCATION
//)

@SuppressLint("MissingPermission")
suspend fun FusedLocationProviderClient.awaitLastLocation(): Location? {
    return runCatching { lastLocation.await() }.except<CancellationException, Location?>()
        .getOrNull()
}

@SuppressLint("MissingPermission")
suspend fun Context.awaitLastLocation(): Location? {
    val provider: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(this)
    return provider.awaitLastLocation()
}

@SuppressLint("MissingPermission")
suspend fun FusedLocationProviderClient.awaitUpdateLastLocation(
    interval: Long = 10000L,
    fastestInterval: Long = 5000L
): Location? {
    val request = LocationRequest.create().apply {
        numUpdates = 1
        this.interval = interval
        this.fastestInterval = fastestInterval
        priority = LocationRequest.PRIORITY_HIGH_ACCURACY
    }
    return suspendCancellableCoroutine { continuation ->
        val callback = object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                super.onLocationResult(result)
                val location = result.locations.firstOrNull { it != null }
                removeLocationUpdates(this)
                continuation.resume(location)
            }
        }
        continuation.invokeOnCancellation { removeLocationUpdates(callback) }
        requestLocationUpdates(request, callback, Looper.getMainLooper())
    }
}

@SuppressLint("MissingPermission")
suspend fun Context.updateOneAndGetLastLocation(): Location? {
    val provider = LocationServices.getFusedLocationProviderClient(this)
    return provider.awaitUpdateLastLocation()
}

suspend fun Context.getLocation(isForceUpdate: Boolean): Location? {
    val provider = LocationServices.getFusedLocationProviderClient(this)
    return provider.getLocation(isForceUpdate)
}

suspend fun FusedLocationProviderClient.getLocation(isForceUpdate: Boolean) =
    runCatching { if (isForceUpdate) awaitUpdateLastLocation() else awaitLastLocation() }
        .except<CancellationException, Location?>()
        .getOrNull()

private const val LOCATION_FORMAT_STRING = "%02d°%02d′%04.1f″"

@Suppress("MagicNumber")
private fun getDegree(double: Double): Triple<Int, Int, Double> {
    val degs = abs(double)
    val deg = degs.toInt()
    val minutes = (degs - deg) * 60.0
    val minute = minutes.toInt()
    val second = ((minutes - minute) * 60.0)
    return Triple(deg, minute, second)
}

@Suppress("SpreadOperator")
fun latToString(latitude: Double, n: String, s: String): String {
    val ns = if (latitude > 0) n else s
    val lat = getDegree(latitude).toList().toTypedArray()
    return buildString {
        append(String.format(LOCATION_FORMAT_STRING, *lat))
        append(ns)
    }
}

fun lngToString(longitude: Double, e: String, w: String): String {
    return latToString(longitude, e, w)
}

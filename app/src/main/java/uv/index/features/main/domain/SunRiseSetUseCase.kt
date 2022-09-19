package uv.index.features.main.domain

import net.arwix.urania.core.math.angle.deg
import net.arwix.urania.core.observer.Observer
import net.arwix.urania.core.toDeg
import net.arwix.urania.core.toRad
import uv.index.features.astronomy.data.SunRepository
import uv.index.features.astronomy.data.context.ASTRONOMY_EVENT_RISE
import uv.index.features.astronomy.data.context.ASTRONOMY_EVENT_SET
import uv.index.features.astronomy.data.context.SunPositionContext
import uv.index.features.main.data.SunPosition
import uv.index.features.place.data.room.PlaceData
import java.time.LocalTime
import java.time.ZonedDateTime
import javax.inject.Inject

class SunRiseSetUseCase @Inject constructor(private val sunRepository: SunRepository) {

    suspend operator fun invoke(
        place: PlaceData,
        zdt: ZonedDateTime
    ): Pair<LocalTime?, LocalTime?> {
        val rsEvents = sunRepository.getRiseSetEvents(
            observer = Observer(
                position = Observer.Position(
                    longitude = place.latLng.longitude.deg.toRad(),
                    latitude = place.latLng.latitude.deg.toRad(),
                    altitude = 0.0
                )
            ),
            localDate = zdt.toLocalDate(),
            zoneId = zdt.zone
        )
        val riseEvent = rsEvents.firstOrNull { it.typeId == ASTRONOMY_EVENT_RISE }
        val setEvent = rsEvents.firstOrNull { it.typeId == ASTRONOMY_EVENT_SET }
        return riseEvent?.instant?.atZone(zdt.zone)?.toLocalTime() to
                setEvent?.instant?.atZone(zdt.zone)?.toLocalTime()
    }


    suspend fun getPosition(place: PlaceData, zdt: ZonedDateTime): SunPosition {
        val azimuth = sunRepository.getSunAzimuth(
            observer = Observer(
                position = Observer.Position(
                    longitude = place.latLng.longitude.deg.toRad(),
                    latitude = place.latLng.latitude.deg.toRad(),
                    altitude = 0.0
                )
            ),
            zonedDateTime = zdt
        )
        val altitude = azimuth.theta.toDeg()
        return if (SunPositionContext.isAbove(altitude)) {
            SunPosition.Above
        } else {
            if (altitude >= (-TWILIGHT_ANGLE).deg) SunPosition.Twilight else SunPosition.Night
        }
    }

    private companion object {
        private const val TWILIGHT_ANGLE = 18
    }

}
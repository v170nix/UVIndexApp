package uv.index.parts.main.domain

import net.arwix.urania.core.math.angle.deg
import net.arwix.urania.core.observer.Observer
import net.arwix.urania.core.toRad
import uv.index.lib.data.UVIPlaceData
import uv.index.parts.astronomy.data.SunRepository
import uv.index.parts.astronomy.data.context.ASTRONOMY_EVENT_RISE
import uv.index.parts.astronomy.data.context.ASTRONOMY_EVENT_SET
import java.time.LocalTime
import java.time.ZonedDateTime
import javax.inject.Inject

class SunRiseSetUseCase @Inject constructor(private val sunRepository: SunRepository) {

    suspend operator fun invoke(place: UVIPlaceData, zdt: ZonedDateTime): Pair<LocalTime?, LocalTime?> {
        val rsEvents = sunRepository.getRiseSetEvents(
            observer = Observer(
                position = Observer.Position(
                    longitude = place.longitude.deg.toRad(),
                    latitude = place.latitude.deg.toRad(),
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

}
package uv.index.parts.astronomy.data

import net.arwix.urania.core.ephemeris.Ephemeris
import net.arwix.urania.core.math.vector.SphericalVector
import net.arwix.urania.core.observer.Observer
import uv.index.di.SunEphemeris
import uv.index.parts.astronomy.data.context.PositionContext
import uv.index.parts.astronomy.data.context.SunPositionContext
import uv.index.parts.astronomy.data.ephemeris.TopocentricTransformation
import java.time.LocalDate
import java.time.ZoneId
import java.time.ZonedDateTime
import javax.inject.Inject

class SunRepository @Inject constructor(
    @SunEphemeris private val sunEphemeris: Ephemeris
) {

    private val topocentricTransformation = TopocentricTransformation()

    suspend fun getRiseSetEvents(
        observer: Observer,
        localDate: LocalDate,
        zoneId: ZoneId,
    ): List<PositionContext.Event> {

        return SunPositionContext.provideDayEvents(
            localDate.atStartOfDay(zoneId),
            observer,
            PositionContext.MASK_RISE_SET,
            sunEphemeris
        )
    }

    suspend fun getSunAzimuth(
        observer: Observer,
        zonedDateTime: ZonedDateTime
    ): SphericalVector {
        return topocentricTransformation.transform(
            zonedDateTime,
            observer.position,
            sunEphemeris
        )
    }

}
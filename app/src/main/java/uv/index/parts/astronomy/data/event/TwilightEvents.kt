package uv.index.parts.astronomy.data.event

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import net.arwix.urania.core.ephemeris.Ephemeris
import net.arwix.urania.core.ephemeris.calculation.RiseSetTransitCalculation
import net.arwix.urania.core.observer.Observer
import uv.index.parts.astronomy.data.context.SunPositionContext
import java.time.Duration
import java.time.Instant
import java.time.ZonedDateTime


internal suspend inline fun getTwilightEvents(
    context: SunPositionContext,
    zdt: ZonedDateTime,
    duration: Duration,
    observer: Observer,
    request: RiseSetTransitCalculation.Request.RiseSet,
    ephemeris: Ephemeris
): List<SunPositionContext.SunEvent> = withContext(Dispatchers.Default) {
    mutableListOf<SunPositionContext.SunEvent>().apply {

        val result = RiseSetTransitCalculation.obtainNextResults(
            kotlinx.datetime.Instant.fromEpochSeconds(zdt.toEpochSecond()),
            duration.seconds,
            observer,
            ephemeris,
            setOf(request)
        )

        val rise: RiseSetTransitCalculation.Result.Rise.Value? =
            result.firstNotNullOfOrNull {
                if (it is RiseSetTransitCalculation.Result.Rise.Value) it else null
            }

        val set: RiseSetTransitCalculation.Result.Set.Value? =
            result.firstNotNullOfOrNull {
                if (it is RiseSetTransitCalculation.Result.Set.Value) it else null
            }

        if (rise != null) {
            add(transformResultToContextResult(context, rise.time, true, request))
        }

        if (set != null) {
            add(transformResultToContextResult(context, set.time, false, request))
        }
    }
}

private fun transformResultToContextResult(
    context: SunPositionContext,
    instant: kotlinx.datetime.Instant,
    isBegin: Boolean,
    request: RiseSetTransitCalculation.Request.RiseSet,
): SunPositionContext.SunEvent {
    return when (request) {
        is RiseSetTransitCalculation.Request.RiseSet.TwilightCivil -> if (isBegin) SunPositionContext.SunEvent.CivilBegin(
            context,
            Instant.ofEpochMilli(instant.toEpochMilliseconds())
        ) else SunPositionContext.SunEvent.CivilEnd(
            context,
            Instant.ofEpochMilli(instant.toEpochMilliseconds())
        )
        is RiseSetTransitCalculation.Request.RiseSet.TwilightNautical -> if (isBegin) SunPositionContext.SunEvent.NauticalBegin(
            context,
            Instant.ofEpochMilli(instant.toEpochMilliseconds())
        ) else SunPositionContext.SunEvent.NauticalEnd(
            context,
            Instant.ofEpochMilli(instant.toEpochMilliseconds())
        )
        is RiseSetTransitCalculation.Request.RiseSet.TwilightAstronomical -> if (isBegin) SunPositionContext.SunEvent.AstronomicalBegin(
            context,
            Instant.ofEpochMilli(instant.toEpochMilliseconds())
        ) else SunPositionContext.SunEvent.AstronomicalEnd(
            context,
            Instant.ofEpochMilli(instant.toEpochMilliseconds())
        )
        else -> throw IllegalArgumentException()
    }
}
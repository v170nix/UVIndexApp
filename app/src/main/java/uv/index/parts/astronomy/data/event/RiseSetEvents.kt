package uv.index.parts.astronomy.data.event

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.datetime.Instant
import net.arwix.urania.core.annotation.Equatorial
import net.arwix.urania.core.annotation.Geocentric
import net.arwix.urania.core.ephemeris.Ephemeris
import net.arwix.urania.core.ephemeris.calculation.RiseSetTransitCalculation
import net.arwix.urania.core.observer.Observer
import uv.index.parts.astronomy.data.context.PositionContext
import java.time.Duration
import java.time.ZonedDateTime

suspend inline fun getRiseSetEvents(
    context: PositionContext,
    zdt: ZonedDateTime,
    duration: Duration,
    observer: Observer,
    request: RiseSetTransitCalculation.Request.RiseSet,
    @Geocentric @Equatorial
    ephemeris: Ephemeris
): List<PositionContext.Event> = withContext(Dispatchers.Default) {
    val out = mutableListOf<PositionContext.Event>()

    val riseSetResult = RiseSetTransitCalculation.obtainNextResults(
        Instant.fromEpochSeconds(zdt.toEpochSecond()),
        duration.seconds,
        observer,
        ephemeris,
        request = setOf(request)
    )

    val rise: RiseSetTransitCalculation.Result.Rise.Value? =
        riseSetResult.firstNotNullOfOrNull {
            if (it is RiseSetTransitCalculation.Result.Rise.Value) it else null
        }

    val set: RiseSetTransitCalculation.Result.Set.Value? =
        riseSetResult.firstNotNullOfOrNull {
            if (it is RiseSetTransitCalculation.Result.Set.Value) it else null
        }

    if (rise != null) {
        out.add(
            PositionContext.DefaultEvent.Rise(
                context,
                java.time.Instant.ofEpochMilli(rise.time.toEpochMilliseconds())
            )
        )
    }

    if (set != null) {
        out.add(
            PositionContext.DefaultEvent.Set(
                context,
                java.time.Instant.ofEpochMilli(set.time.toEpochMilliseconds())
            )
        )
    }

    return@withContext out
}
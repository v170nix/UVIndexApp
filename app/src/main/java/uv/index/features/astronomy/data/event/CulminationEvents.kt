package uv.index.features.astronomy.data.event

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.datetime.toKotlinInstant
import net.arwix.urania.core.ephemeris.Ephemeris
import net.arwix.urania.core.ephemeris.calculation.RiseSetTransitCalculation
import net.arwix.urania.core.math.angle.Radian
import net.arwix.urania.core.observer.Observer
import net.arwix.urania.core.toDeg
import uv.index.features.astronomy.data.context.PositionContext
import java.time.Duration
import java.time.Instant
import java.time.ZonedDateTime

suspend inline fun getCulminationEvents(
    context: PositionContext,
    zdt: ZonedDateTime,
    duration: Duration,
    observer: Observer,
    objectElevation: Radian,
    ephemeris: Ephemeris
): List<PositionContext.Event> = withContext(Dispatchers.Default) {
    val out = mutableListOf<PositionContext.Event>()

    val result = RiseSetTransitCalculation.obtainNextResults(
        zdt.toInstant().toKotlinInstant(),
        duration.seconds,
        observer,
        ephemeris,
        request = setOf(
            RiseSetTransitCalculation.Request.UpperTransit(),
            RiseSetTransitCalculation.Request.DownTransit(),
        )
    )

    val upperTransit = result.firstNotNullOfOrNull {
        if (it is RiseSetTransitCalculation.Result.UpperTransit) it else null
    }

    val downTransit = result.firstNotNullOfOrNull {
        if (it is RiseSetTransitCalculation.Result.DownTransit) it else null
    }

    if (upperTransit != null) {
        if (upperTransit.altitude > objectElevation.toDeg()) {
            out.add(
                PositionContext.DefaultEvent.UpperCulminationAbove(
                    context,
                    Instant.ofEpochMilli(upperTransit.time.toEpochMilliseconds()),
                    altitude = upperTransit.altitude
                )
            )
        } else {
            out.add(
                PositionContext.DefaultEvent.UpperCulminationNotAbove(
                    context,
                    Instant.ofEpochMilli(upperTransit.time.toEpochMilliseconds()),
                    altitude = upperTransit.altitude
                )
            )
        }
    }

    if (downTransit != null) {
        if (downTransit.altitude > objectElevation.toDeg()) {
            out.add(
                PositionContext.DefaultEvent.LowerCulminationAbove(
                    context,
                    Instant.ofEpochMilli(downTransit.time.toEpochMilliseconds()),
                    altitude = downTransit.altitude
                )
            )
        } else {
            out.add(
                PositionContext.DefaultEvent.LowerCulminationNotAbove(
                    context,
                    Instant.ofEpochMilli(downTransit.time.toEpochMilliseconds()),
                    altitude = downTransit.altitude
                )
            )
        }
    }

    return@withContext out

}
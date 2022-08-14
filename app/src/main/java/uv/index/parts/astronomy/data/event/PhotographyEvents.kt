package uv.index.parts.astronomy.data.event

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import net.arwix.urania.core.ephemeris.Ephemeris
import net.arwix.urania.core.ephemeris.calculation.RiseSetTransitCalculation
import net.arwix.urania.core.observer.Observer
import net.arwix.urania.core.toRad
import uv.index.parts.astronomy.data.context.SunPositionContext
import java.time.Duration
import java.time.Instant
import java.time.ZonedDateTime

internal suspend fun getPhotographyEvents(
    context: SunPositionContext,
    type: SunPositionContext.PhotographyType,
    zdt: ZonedDateTime,
    duration: Duration,
    observer: Observer,
    isReverse: Boolean,
    ephemeris: Ephemeris
): List<SunPositionContext.SunEvent> = withContext(Dispatchers.Default) {

    val request = RiseSetTransitCalculation.Request.RiseSet.Custom(
        elevation = if (isReverse) type.angleRange.endInclusive.toRad()
        else type.angleRange.start.toRad()
    )

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
            add(transformResultToContextResult(context, rise.time, !isReverse, type))
        }

        if (set != null) {
            add(transformResultToContextResult(context, set.time, isReverse, type))
        }
    }
}

private fun transformResultToContextResult(
    context: SunPositionContext,
    instant: kotlinx.datetime.Instant,
    isBegin: Boolean,
    photographyType: SunPositionContext.PhotographyType,
): SunPositionContext.SunEvent {
    return when (photographyType) {
        SunPositionContext.PhotographyType.BlueHour -> if (isBegin) SunPositionContext.SunEvent.BlueHourBegin(
            context,
            Instant.ofEpochMilli(instant.toEpochMilliseconds())
        ) else SunPositionContext.SunEvent.BlueHourEnd(
            context,
            Instant.ofEpochMilli(instant.toEpochMilliseconds())
        )
        SunPositionContext.PhotographyType.GoldenHour -> if (isBegin) SunPositionContext.SunEvent.GoldenHourBegin(
            context,
            Instant.ofEpochMilli(instant.toEpochMilliseconds())
        ) else SunPositionContext.SunEvent.GoldenHourEnd(
            context,
            Instant.ofEpochMilli(instant.toEpochMilliseconds())
        )
    }
}
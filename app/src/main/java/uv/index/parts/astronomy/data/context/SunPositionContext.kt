package uv.index.parts.astronomy.data.context

import androidx.compose.runtime.Immutable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import net.arwix.extension.isBitSet
import net.arwix.urania.core.ephemeris.Ephemeris
import net.arwix.urania.core.ephemeris.calculation.RiseSetTransitCalculation
import net.arwix.urania.core.math.angle.Degree
import net.arwix.urania.core.math.angle.deg
import net.arwix.urania.core.math.angle.sin
import net.arwix.urania.core.observer.Observer
import net.arwix.urania.core.toDeg
import net.arwix.urania.core.toRad
import uv.index.parts.astronomy.data.date.Second
import uv.index.parts.astronomy.data.date.second
import uv.index.parts.astronomy.data.event.getPhotographyEvents
import uv.index.parts.astronomy.data.event.getTwilightEvents
import java.time.Duration
import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime

@Suppress("unused")
object SunPositionContext : PositionContext() {

    private val elevation = RiseSetTransitCalculation.Request.RiseSet.DefaultSun.elevation.toDeg()

    enum class PhotographyType(val angleRange: ClosedRange<Degree>) {
        BlueHour((-8.0).deg..(-3.0).deg),
        GoldenHour((-3.0).deg..10.0.deg);

        internal val sinRefractionAngleStart = sin(angleRange.start.toRad())
        internal val sinRefractionAngleEnd = sin(angleRange.endInclusive.toRad())

        fun isTop(altitude: Degree): Boolean {
            return when (this) {
                BlueHour -> altitude > (-5.5).deg
                GoldenHour -> altitude > 3.5.deg
            }
        }
    }

    @Immutable
    sealed class SunEvent(
        override val context: PositionContext,
        override val instant: Instant,
        override val typeId: Int,
    ) : Event {

        override fun toString() = "${this.javaClass.canonicalName} $instant"

        @Immutable
        open class CivilBegin(
            context: SunPositionContext, instant: Instant
        ) : SunEvent(context, instant, SUN_EVENT_CIVIL_BEGIN)

        @Immutable
        open class CivilEnd(
            context: SunPositionContext, instant: Instant
        ) : SunEvent(context, instant, SUN_EVENT_CIVIL_END)

        @Immutable
        open class NauticalBegin(
            context: SunPositionContext, instant: Instant
        ) : SunEvent(context, instant, SUN_EVENT_NAUTICAL_BEGIN)

        @Immutable
        open class NauticalEnd(
            context: SunPositionContext, instant: Instant
        ) : SunEvent(context, instant, SUN_EVENT_NAUTICAL_END)

        @Immutable
        open class AstronomicalBegin(
            context: SunPositionContext, instant: Instant
        ) : SunEvent(context, instant, SUN_EVENT_ASTRONOMICAL_BEGIN)

        @Immutable
        open class AstronomicalEnd(
            context: SunPositionContext, instant: Instant
        ) : SunEvent(context, instant, SUN_EVENT_ASTRONOMICAL_END)

        @Immutable
        open class BlueHourBegin(
            context: SunPositionContext, instant: Instant
        ) : SunEvent(context, instant, PHOTOGRAPHY_EVENT_BLUE_HOUR_BEGIN)

        @Immutable
        open class BlueHourEnd(
            context: SunPositionContext, instant: Instant
        ) : SunEvent(context, instant, PHOTOGRAPHY_EVENT_BLUE_HOUR_END)

        @Immutable
        open class GoldenHourBegin(
            context: SunPositionContext, instant: Instant
        ) : SunEvent(context, instant, PHOTOGRAPHY_EVENT_GOLDEN_HOUR_BEGIN)

        @Immutable
        open class GoldenHourEnd(
            context: SunPositionContext, instant: Instant
        ) : SunEvent(context, instant, PHOTOGRAPHY_EVENT_GOLDEN_HOUR_END)
    }

    fun getRiseSetRefractionAngle() = elevation

    fun isAbove(altitude: Degree): Boolean {
        return altitude >= elevation
    }

    fun isCivilAngle(altitude: Degree): Boolean {
        return altitude >= (-6.0).deg && altitude < elevation
    }

    fun getCivilAngle(): Degree = (-6.0).deg

    fun isNauticalAngle(altitude: Degree): Boolean {
        return altitude >= (-12.0).deg && altitude < (-6.0).deg
    }

    fun getNauticalAngle(): Degree = (-12.0).deg

    fun isAstronomicalAngle(altitude: Degree): Boolean {
        return altitude >= (-18.0).deg && altitude < (-12.0).deg
    }

    fun getAstronomicalAngle(): Degree = (-18.0).deg

    suspend fun provideDayEvents(
        zdt: ZonedDateTime,
        observer: Observer,
        filter: Int = MASK_ASTRONOMICAL or MASK_NAUTICAL or MASK_CIVIL or MASK_RISE_SET or MASK_CULMINATION,
        ephemeris: Ephemeris
    ): List<Event> = coroutineScope {

        val duration = Duration.between(zdt, zdt.plusDays(1L))

        val civilEvents =
            if (filter.isBitSet(MASK_CIVIL)) async(Dispatchers.Default) {
                getTwilightEvents(
                    this@SunPositionContext,
                    zdt,
                    duration,
                    observer,
                    RiseSetTransitCalculation.Request.RiseSet.TwilightCivil(),
                    ephemeris = ephemeris
                )
            }
            else
                null
        val nauticalEvents =
            if (filter.isBitSet(MASK_NAUTICAL)) async(Dispatchers.Default) {
                getTwilightEvents(
                    this@SunPositionContext,
                    zdt,
                    duration,
                    observer,
                    RiseSetTransitCalculation.Request.RiseSet.TwilightNautical(),
                    ephemeris = ephemeris
                )
        } else
            null
        val astronomicalEvents =
            if (filter.isBitSet(MASK_ASTRONOMICAL)) async(Dispatchers.Default) {
                getTwilightEvents(
                    this@SunPositionContext,
                    zdt,
                    duration,
                    observer,
                    RiseSetTransitCalculation.Request.RiseSet.TwilightAstronomical(),
                    ephemeris = ephemeris
                )
            } else
                null
        val photoHourOne =
            if (filter.isBitSet(MASK_BLUE_HOUR)) async(Dispatchers.Default) {
                getPhotographyEvents(
                    this@SunPositionContext,
                    PhotographyType.BlueHour,
                    zdt,
                    duration,
                    observer,
                    isReverse = false,
                    ephemeris = ephemeris
                )
            } else null

        val photoHourTwo = when {
            filter.isBitSet(MASK_GOLDEN_HOUR) -> async(Dispatchers.Default) {

                getPhotographyEvents(
                    this@SunPositionContext,
                    PhotographyType.GoldenHour,
                    zdt,
                    duration,
                    observer,
                    isReverse = false,
                    ephemeris = ephemeris
                )
            }
            filter.isBitSet(MASK_BLUE_HOUR) -> async(Dispatchers.Default) {

                getPhotographyEvents(
                    this@SunPositionContext,
                    PhotographyType.BlueHour,
                    zdt,
                    duration,
                    observer,
                    isReverse = true,
                    ephemeris = ephemeris
                )
            }
            else -> null
        }


        val photoHourThree =
            if (filter.isBitSet(MASK_GOLDEN_HOUR)) async(Dispatchers.Default) {
                getPhotographyEvents(
                    this@SunPositionContext,
                    PhotographyType.GoldenHour,
                    zdt,
                    duration,
                    observer,
                    isReverse = true,
                    ephemeris = ephemeris
                )
            } else
                null

        mutableListOf<Event>().apply {
            civilEvents?.await()?.let(this::addAll)
            nauticalEvents?.await()?.let(this::addAll)
            astronomicalEvents?.await()?.let(this::addAll)
            photoHourOne?.await()?.let(this::addAll)
            photoHourTwo?.await()?.let(this::addAll)
            photoHourThree?.await()?.let(this::addAll)
            addAll(
                super.provideDayEvents(
                    zdt,
                    observer,
                    request = RiseSetTransitCalculation.Request.RiseSet.DefaultSun,
                    filter = filter,
                    ephemeris =ephemeris
                )
            )
        }
    }

    fun getDayLengthInSeconds(
        zoneId: ZoneId,
        currentDayEvents: Pair<Instant?, Instant?>,
        previousDayEvents: Pair<Instant?, Instant?>? = null,
        nextDayEvents: Pair<Instant?, Instant?>? = null
    ): Second? {
        val (cRise, cSet) = currentDayEvents
        val pRise = previousDayEvents?.first
        val pSet = previousDayEvents?.second
        val nRise = nextDayEvents?.first
        val nSet = nextDayEvents?.second
        var delta: Long? = null
        if (cSet != null && cRise != null) {
            if (cSet.isAfter(cRise)) {
                delta = cSet.epochSecond - cRise.epochSecond
            } else {
                if (cSet.atZone(zoneId).hour < 12) {
                    if (nSet != null && (nRise == null || nRise.isAfter(nSet))) delta =
                        nSet.epochSecond - cRise.epochSecond
                } else {
                    if (pRise != null && (pSet == null || pSet.isBefore(pRise))) delta =
                        cSet.epochSecond - pRise.epochSecond
                }
            }
        }
        if (cSet == null && cRise != null) {
            if (nSet != null && (nRise == null || nRise.isAfter(nSet))) delta =
                nSet.epochSecond - cRise.epochSecond
        }
        if (cSet != null && cRise == null) {
            if (pRise != null && (pSet == null || pSet.isBefore(pRise))) delta =
                cSet.epochSecond - pRise.epochSecond
        }

        return delta?.second
    }

    const val MASK_CIVIL = 0b100
    const val MASK_NAUTICAL = 0b1000
    const val MASK_ASTRONOMICAL = 0b10000
    const val MASK_BLUE_HOUR = 0b100000
    const val MASK_GOLDEN_HOUR = 0b1000000

}
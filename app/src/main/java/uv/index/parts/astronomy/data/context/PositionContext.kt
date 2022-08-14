package uv.index.parts.astronomy.data.context

import androidx.compose.runtime.Immutable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.datetime.toKotlinInstant
import net.arwix.extension.isBitSet
import net.arwix.urania.core.calendar.toJT
import net.arwix.urania.core.ephemeris.Ephemeris
import net.arwix.urania.core.ephemeris.Epoch
import net.arwix.urania.core.ephemeris.Orbit
import net.arwix.urania.core.ephemeris.Plane
import net.arwix.urania.core.ephemeris.calculation.RiseSetTransitCalculation
import net.arwix.urania.core.math.angle.Degree
import net.arwix.urania.core.math.vector.SphericalVector
import net.arwix.urania.core.observer.Observer
import net.arwix.urania.core.spherical
import net.arwix.urania.core.transformation.rotateToTopocentric
import uv.index.BuildConfig
import uv.index.parts.astronomy.data.event.getCulminationEvents
import uv.index.parts.astronomy.data.event.getRiseSetEvents
import java.time.Duration
import java.time.Instant
import java.time.ZonedDateTime

open class PositionContext : AstronomyContext() {

    interface Event : AstronomyContext.Event<PositionContext>
    interface State : AstronomyContext.State<PositionContext>

    @Immutable
    sealed class DefaultEvent(
        override val context: PositionContext,
        override val instant: Instant,
        override val typeId: Int
    ) : Event {
        override fun toString() = "${this.javaClass.canonicalName} $instant"

        @Immutable
        open class LowerCulminationAbove(
            context: PositionContext,
            instant: Instant,
            val altitude: Degree,
        ) :
            DefaultEvent(
                context, instant,
                ASTRONOMY_EVENT_LOWER_CULMINATION_ABOVE
            )

        @Immutable
        open class LowerCulminationNotAbove(
            context: PositionContext,
            instant: Instant,
            val altitude: Degree
        ) :
            DefaultEvent(
                context, instant,
                ASTRONOMY_EVENT_LOWER_CULMINATION_NOT_ABOVE
            )

        @Immutable
        open class UpperCulminationAbove(
            context: PositionContext,
            instant: Instant,
            val altitude: Degree
        ) :
            DefaultEvent(
                context, instant,
                ASTRONOMY_EVENT_UPPER_CULMINATION_ABOVE
            )

        @Immutable
        open class UpperCulminationNotAbove(
            context: PositionContext,
            instant: Instant,
            val altitude: Degree
        ) :
            DefaultEvent(
                context, instant,
                ASTRONOMY_EVENT_UPPER_CULMINATION_NOT_ABOVE
            )

        @Immutable
        open class Rise(context: PositionContext, instant: Instant) :
            DefaultEvent(
                context, instant,
                ASTRONOMY_EVENT_RISE
            )

        @Immutable
        open class Set(context: PositionContext, instant: Instant) :
            DefaultEvent(
                context, instant,
                ASTRONOMY_EVENT_SET
            )

        fun getAltitudeOrNull(): Degree? {
            return when (this) {
                is LowerCulminationAbove -> altitude
                is LowerCulminationNotAbove -> altitude
                is Rise -> null
                is Set -> null
                is UpperCulminationAbove -> altitude
                is UpperCulminationNotAbove -> altitude
            }
        }
    }

    /**
     * создание событий
     * @param zdt
     * @param ephemeris
     */
    open suspend fun provideDayEvents(
        zdt: ZonedDateTime,
        observer: Observer,
        request: RiseSetTransitCalculation.Request.RiseSet,
        filter: Int = MASK_RISE_SET or MASK_CULMINATION,
        ephemeris: Ephemeris
    ): List<Event> = coroutineScope {

        if (BuildConfig.DEBUG) {
            if (with(ephemeris.metadata) {
                    orbit != Orbit.Geocentric ||
                            plane != Plane.Equatorial ||
                            epoch != Epoch.Apparent
                }) throw IllegalArgumentException()
        }

        val duration = Duration.between(zdt, zdt.plusDays(1L))

        val riseSetList = if (filter.isBitSet(MASK_RISE_SET)) async(Dispatchers.Default) {
            getRiseSetEvents(this@PositionContext, zdt, duration, observer, request, ephemeris = ephemeris)
        } else null

        val culminationList = if (filter.isBitSet(MASK_CULMINATION)) async(Dispatchers.Default) {
            getCulminationEvents(
                this@PositionContext,
                zdt,
                duration,
                observer,
                request.elevation,
                ephemeris =  ephemeris
            )
        } else null
        mutableListOf<Event>().apply {
            riseSetList?.await()?.let(this::addAll)
            culminationList?.await()?.let(this::addAll)
        }
    }

    companion object {
        const val MASK_RISE_SET = 0b1
        const val MASK_CULMINATION = 0b10

        suspend fun getTopocentric(
            instant: Instant,
            position: Observer.Position,
            ephemeris: Ephemeris
        ): SphericalVector {
            val vector = ephemeris(instant.toKotlinInstant().toJT())
            return vector.rotateToTopocentric(
                instant.toKotlinInstant(),
                position
            ).spherical
        }

    }
}
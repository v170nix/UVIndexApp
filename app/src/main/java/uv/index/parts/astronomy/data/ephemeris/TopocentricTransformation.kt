package uv.index.parts.astronomy.data.ephemeris

import kotlinx.datetime.toKotlinInstant
import net.arwix.urania.core.calendar.JT
import net.arwix.urania.core.calendar.SiderealTimeMethod
import net.arwix.urania.core.calendar.getEquationOfEquinoxes
import net.arwix.urania.core.calendar.toJT
import net.arwix.urania.core.ephemeris.Ephemeris
import net.arwix.urania.core.math.angle.Radian
import net.arwix.urania.core.math.vector.SphericalVector
import net.arwix.urania.core.math.vector.Vector
import net.arwix.urania.core.observer.Observer
import net.arwix.urania.core.spherical
import net.arwix.urania.core.transformation.rotateToTopocentric
import java.time.ZonedDateTime
import kotlin.math.round

class TopocentricTransformation {

    private val equMap = mutableMapOf<Int, Radian>()

    private fun getEquOfEqu(jt: JT): Radian {
        val key = round(jt * 100.0).toInt()
        return equMap.getOrPut(key) {
            getEquationOfEquinoxes(jt)
        }
    }

    fun transform(
        zdt: ZonedDateTime,
        position: Observer.Position,
        vector: Vector
    ): SphericalVector {
        val instant = zdt.toInstant().toKotlinInstant()
        val jt = instant.toJT()
        return vector.rotateToTopocentric(
            instant,
            position,
            SiderealTimeMethod.Williams1994
        ) {
            getEquOfEqu(jt)
        }.spherical
    }

    suspend fun transform(
        zdt: ZonedDateTime,
        position: Observer.Position,
        ephemeris: Ephemeris
    ): SphericalVector {
        val instant = zdt.toInstant().toKotlinInstant()
        val jt = instant.toJT()

        val vector = ephemeris(jt)
        return vector.rotateToTopocentric(
            instant,
            position,
            SiderealTimeMethod.Williams1994
        ) {
            getEquOfEqu(jt)
        }.spherical
    }

}
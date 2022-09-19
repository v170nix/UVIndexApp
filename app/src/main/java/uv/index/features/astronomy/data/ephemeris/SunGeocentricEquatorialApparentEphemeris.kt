@file:Suppress("unused", "MagicNumber")
package uv.index.features.astronomy.data.ephemeris

import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import net.arwix.urania.core.calendar.JT
import net.arwix.urania.core.calendar.jT
import net.arwix.urania.core.ephemeris.*
import net.arwix.urania.core.ephemeris.fast.FastSunEphemeris
import net.arwix.urania.core.math.vector.Vector
import net.arwix.urania.core.transformation.obliquity.Obliquity
import net.arwix.urania.core.transformation.obliquity.ObliquityElements
import net.arwix.urania.core.transformation.obliquity.createElements
import kotlin.math.round

class SunGeocentricEquatorialApparentEphemeris : Ephemeris {

    private val obliquityMap = mutableMapOf<Int, ObliquityElements>()
    private val mutex = Mutex()
    private val vectorMap = mutableMapOf<JT, Vector>()

    override val metadata: Metadata by lazy {
        Metadata(
            orbit = Orbit.Geocentric,
            plane = Plane.Equatorial,
            epoch = Epoch.Apparent
        )
    }

    override suspend fun invoke(jT: JT): Vector {
        val vector = vectorMap[jT]
        return if (vector == null) {
            val obliquity = getObliquityElements(jT)
            val newVector = obliquity.rotatePlane(FastSunEphemeris(jT), Plane.Equatorial)
            mutex.withLock {
                vectorMap.put(jT, newVector)
            }
            newVector
        } else {
            vector
        }
    }

    private suspend fun getObliquityElements(jT: JT): ObliquityElements {
        val key = round(jT * 100.0).toInt()
        var obliquity = obliquityMap[key]
        if (obliquity == null) {
            obliquity = mutex.withLock {
                obliquityMap.getOrPut(key) {
                    Obliquity.Simon1994.createElements((key / 100.0).jT)
                }
            }
        }
        return obliquity
    }

}
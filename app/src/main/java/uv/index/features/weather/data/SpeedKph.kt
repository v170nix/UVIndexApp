package uv.index.features.weather.data

import kotlin.math.roundToInt

@JvmInline
value class SpeedKph(val value: Double) {
    operator fun compareTo(speed: SpeedKph): Int = (this.value - speed.value).roundToInt()

    companion object {
        val Unspecified = SpeedKph(value = Double.NaN)
    }
}

inline val SpeedKph.isSpecified: Boolean
    get() = !value.isNaN()

inline val SpeedKph.isUnspecified: Boolean
    get() = value.isNaN()

inline val Int.kph: SpeedKph get() = SpeedKph(value = this.toDouble())
inline val Double.kph: SpeedKph get() = SpeedKph(value = this)
inline val Float.kph: SpeedKph get() = SpeedKph(value = this.toDouble())
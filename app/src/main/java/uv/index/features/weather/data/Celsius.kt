package uv.index.features.weather.data

import androidx.annotation.Keep
import kotlinx.serialization.Serializable

@JvmInline
@Keep
@Serializable
value class Celsius(val value: Double) {
    companion object {
        val Unspecified = Celsius(value = Double.NaN)
    }
}

inline val Celsius.isSpecified: Boolean
    get() = !value.isNaN()

inline val Celsius.isUnspecified: Boolean
    get() = value.isNaN()

inline val Int.celsius: Celsius get() = Celsius(value = this.toDouble())
inline val Double.celsius: Celsius get() = Celsius(value = this)
inline val Float.celsius: Celsius get() = Celsius(value = this.toDouble())
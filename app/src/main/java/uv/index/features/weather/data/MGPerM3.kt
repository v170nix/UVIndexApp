package uv.index.features.weather.data

@JvmInline
value class MGPerM3(val value: Double) {
    companion object {
        val Unspecified = MGPerM3(value = Double.NaN)
    }
}

inline val Int.mgPm3: MGPerM3 get() = MGPerM3(value = this.toDouble())
inline val Double.mgPm3: MGPerM3 get() = MGPerM3(value = this)
inline val Float.mgPm3: MGPerM3 get() = MGPerM3(value = this.toDouble())
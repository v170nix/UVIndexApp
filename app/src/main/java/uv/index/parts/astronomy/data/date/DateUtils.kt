package uv.index.parts.astronomy.data.date

import java.time.Instant


fun Instant.roundToMinute(): Instant {
    val s = epochSecond
    val m = s / 60L
    val ds = s - m * 60L
    val round = if (ds > 30L) s + 60L - ds else s - ds
    return Instant.ofEpochSecond(round)
}

inline val Long.hour get() = Hour(this)
inline val Long.minute get() = Minute(this)
inline val Long.second get() = Second(this)

@JvmInline value class Hour(val value: Long)
@JvmInline value class Minute(val value: Long)
@JvmInline value class Second(val value: Long) {

    fun toHHMMSS(): Triple<Hour, Minute, Second> {
        var different = value
        val minuteInSeconds = 60L
        val hourInSeconds = 60L * 60L
        val elapsedHours = different / hourInSeconds
        different %= hourInSeconds
        val elapsedMinutes = different / minuteInSeconds
        different %= minuteInSeconds
        val elapsedSeconds = different
        return Triple(
            Hour(elapsedHours),
            Minute(elapsedMinutes),
            Second(elapsedSeconds)
        )
    }
}
package uv.index.features.place.data.gtimezone

import java.time.Instant
import java.time.ZoneId
import java.time.ZoneOffset

data class TimeZoneDisplayEntry constructor(
    val id: ZoneId,
    val zoneOffset: ZoneOffset,
    val displayName: String,
    val displayLongName: String,
    val gmtOffsetString: String
) {

    constructor(zoneId: ZoneId, instant: Instant) : this(
        zoneId,
        zoneId.rules.getOffset(instant),
        TimeZoneRepository.getName(zoneId),
        TimeZoneRepository.getLongName(zoneId, instant),
        TimeZoneRepository.getGmtOffsetText(zoneId, instant)
    )

}
package uv.index.features.main.ui.transform

import uv.index.features.astronomy.domain.SunRiseSetUseCase
import uv.index.features.main.ui.MainContract
import uv.index.features.place.data.room.PlaceData
import java.time.ZonedDateTime
import javax.inject.Inject

class SunDataUseCase @Inject constructor(
    private val sunRiseSetUseCase: SunRiseSetUseCase,
) {

    suspend operator fun invoke(
        place: PlaceData,
        atStartDayDate: ZonedDateTime,
        currentDateTime: ZonedDateTime
    ): MainContract.SunData {
        val (riseTime, setTime) = sunRiseSetUseCase.getRiseSet(place, atStartDayDate)
        val sunPosition = sunRiseSetUseCase.getPosition(place, currentDateTime)
        return MainContract.SunData(sunPosition, riseTime, setTime)
    }
}
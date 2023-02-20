package uv.index.features.main.ui.transform

import uv.index.features.astronomy.data.SunPosition
import uv.index.features.main.ui.MainContract
import uv.index.features.place.data.room.PlaceData
import uv.index.lib.data.UVIndexData
import uv.index.lib.data.UVSkinType
import uv.index.lib.data.getCurrentIndex
import java.time.ZonedDateTime
import javax.inject.Inject
import kotlin.math.roundToInt

class UVICurrentDataUseCase @Inject constructor() {

    operator fun invoke(
        place: PlaceData,
        skin: UVSkinType,
        position: SunPosition,
        currentDayData: List<UVIndexData>
    ): MainContract.UVCurrentData {

        val currentZdt = ZonedDateTime.now(place.zone)
        val currentHour = currentZdt.hour + currentZdt.minute / MINUTES_IN_HOUR.toDouble()
        val list24 = currentDayData.getSubList(HOURS_IN_DAY)

        val timeToVitaminD = getTimeToVitaminD(skin, currentHour, position, list24)
        val timeToBurn =
            if (timeToVitaminD == MainContract.TimeToEvent.Infinity)
                MainContract.TimeToEvent.Infinity
            else
                getTimeToBurn(skin, currentHour, position, list24)

        return MainContract.UVCurrentData(
            index = list24.getCurrentIndex(currentHour),
            timeToBurn = timeToBurn,
            timeToVitaminD = timeToVitaminD
        )

    }

    private companion object {

        private const val MINUTES_IN_HOUR = 60
        private const val HOURS_IN_DAY = 24

        private fun List<UVIndexData>.getSubList(size: Int = HOURS_IN_DAY): List<UVIndexData> {
            if (this.size == size) return this
            return (0 until size).map {
                this.getOrNull(it) ?: UVIndexData(0L, 0, 0, 0.0)
            }
        }

        private fun getTimeToBurn(
            skin: UVSkinType,
            currentHour: Double,
            currentSunPosition: SunPosition,
            list24: List<UVIndexData>?
        ): MainContract.TimeToEvent {
            if (currentSunPosition != SunPosition.Above || list24 == null)
                return MainContract.TimeToEvent.Infinity

            val minTime = skin.getIntegralMinTimeToBurnInMins(
                list = list24,
                currentHour = currentHour
            )?.roundToInt()

            val maxTime = skin.getIntegralMaxTimeToBurnInMins(
                list = list24,
                currentHour = currentHour
            )?.roundToInt()

            return if (minTime != null) {
                MainContract.TimeToEvent.Value(
                    minTimeInMins = minTime,
                    maxTimeInMins = maxTime
                )
            } else {
                MainContract.TimeToEvent.Infinity
            }
        }

        private fun getTimeToVitaminD(
            skin: UVSkinType,
            currentHour: Double,
            currentSunPosition: SunPosition,
            list24: List<UVIndexData>?
        ): MainContract.TimeToEvent {
            if (currentSunPosition != SunPosition.Above || list24 == null)
                return MainContract.TimeToEvent.Infinity

            val minTime = skin.getIntegralMinTimeToVitaminDInMins(
                list = list24,
                currentHour = currentHour
            )?.roundToInt()

            val maxTime = skin.getIntegralMaxTimeToVitaminDInMins(
                list = list24,
                currentHour = currentHour
            )?.roundToInt()

            return if (minTime != null) {
                MainContract.TimeToEvent.Value(
                    minTimeInMins = minTime,
                    maxTimeInMins = maxTime
                )
            } else {
                MainContract.TimeToEvent.Infinity
            }
        }
    }
}
package uv.index.features.main.ui.transform

import kotlinx.coroutines.runBlocking
import uv.index.features.astronomy.data.SunPosition
import uv.index.features.astronomy.domain.SunRiseSetUseCase
import uv.index.features.main.ui.MainContract
import uv.index.features.place.data.room.PlaceData
import uv.index.lib.data.UVIndexData
import uv.index.lib.domain.UVForecastHoursUseCase
import java.time.Instant
import java.time.LocalTime
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import javax.inject.Inject
import kotlin.math.roundToInt

class UVIForecastUseCase @Inject constructor(
    private val sunRiseSetUseCase: SunRiseSetUseCase,
    private val forecastHoursUseCase: UVForecastHoursUseCase
) {

    @Volatile
    private var bufferList: Pair<Int, List<UVIndexData>> = 0 to listOf()

    @Volatile
    private var bufferHours: Pair<Int, List<MainContract.UVHourData>> = 0 to listOf()

    //TODO check buffer
    private fun getHashForList(
        place: PlaceData,
        currentDateAtStartDay: ZonedDateTime,
    ) = place.latLng.hashCode() + currentDateAtStartDay.hashCode()


    //TODO check buffer
    private fun checkHashForList(
        place: PlaceData,
        currentDateAtStartDay: ZonedDateTime
    ) = false // getHashForList(place, currentDateAtStartDay) == bufferList.first

    private fun getHashForHours(
        place: PlaceData,
        currentDateTime: ZonedDateTime
    ) = (place.latLng.hashCode() +
            currentDateTime.toLocalDate().hashCode() xor
            currentDateTime.toLocalTime()
                .withHour(0).withSecond(0).withNano(0)
                .hashCode() xor
            currentDateTime.offset.hashCode() xor
            Integer.rotateLeft(currentDateTime.zone.hashCode(), 3))

    //TODO check buffer
    private fun checkHashForHours(
        place: PlaceData,
        currentDateTime: ZonedDateTime
    )= false // = getHashForHours(place, currentDateTime) == bufferHours.first

    suspend fun getHours(
        place: PlaceData,
        currentDateAtStartDay: ZonedDateTime,
        currentDateTime: ZonedDateTime,
    ): Result {
        val forecastHours: List<UVIndexData> =
            if (!checkHashForList(place, currentDateAtStartDay)) {
                forecastHoursUseCase(
                    place.latLng.longitude,
                    place.latLng.latitude,
                    currentDateAtStartDay
                ).also {
                    bufferList = getHashForList(place, currentDateAtStartDay) to it
                }
            } else {
                bufferList.second
            }

        val maxHour = forecastHours.asSequence()
            .take(FORECAST_HOUR_COUNT)
            .maxByOrNull { it.value }
            ?.let {
                Instant.ofEpochSecond(it.time).atZone(currentDateTime.zone).toLocalTime()
            }

        val uvHours = if (!checkHashForHours(place, currentDateTime)) {
            transformListToUIHours(
                place, currentDateAtStartDay.toEpochSecond(), forecastHours, 48
            ).also {
                bufferHours = getHashForHours(place, currentDateTime) to it
            }
        } else {
            bufferHours.second
        }

        val currentTime = currentDateTime
            .toLocalDateTime()
            .minusHours(1L)

        var count = 0
        val forecastList = uvHours
            .filter {
                run {
                    if (it is MainContract.UVHourData.Item) {
                        it.localDateTime.isAfter(currentTime)
                            .also { if (it) count++ }
                    } else true
                }.let { it && count < 25 }
            }

        val dayList = uvHours
            .asSequence()
            .filterIsInstance<MainContract.UVHourData.Item>()
            .take(24)
            .toList()

        return Result(
            forecastList = forecastList,
            currentDayList = dayList,
            maxTime = maxHour
        )
    }

    private suspend fun transformListToUIHours(
        place: PlaceData,
        firstTime: Long,
        forecastHours: List<UVIndexData>,
        count: Int = FORECAST_HOUR_COUNT
    ) = forecastHours
        .asSequence()
        .filter { it.time >= firstTime }
        .take(count)
        .flatMapIndexed { i: Int, data: UVIndexData ->
            sequence {
                val zdt = Instant
                    .ofEpochSecond(data.time)
                    .atZone(place.zone)

                if (zdt.hour < 1 && i > 0) yield(MainContract.UVHourData.Divider)

                val index = (data.value * 10).roundToInt() / 10.0
                var iIndex = index.roundToInt()

                if (iIndex == 0) {
                    iIndex = runBlocking {
                        sunRiseSetUseCase.getPosition(place, zdt)
                    }.let {
                        when (it) {
                            SunPosition.Above -> 0
                            SunPosition.Twilight -> -1
                            SunPosition.Night -> -2
                        }
                    }
                }

                val localTime = zdt.toLocalTime()

                yield(
                    MainContract.UVHourData.Item(
                        localDateTime = zdt.toLocalDateTime(),
                        sIndex = index.toString(),
                        iIndex = iIndex,
                        timeText = localTime.format(formatter),
                    )
                )
            }
        }
        .toList()

    data class Result(
        val forecastList: List<MainContract.UVHourData>? = listOf(),
        val currentDayList: List<MainContract.UVHourData>? = listOf(),
        val maxTime: LocalTime? = null
    )

    private companion object {
        const val FORECAST_HOUR_COUNT = 24
        val formatter: DateTimeFormatter = DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT)
    }


}
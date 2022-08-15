package uv.index.parts.main.ui

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.runBlocking
import net.arwix.extension.ConflatedJob
import net.arwix.mvi.SimpleViewModel
import net.arwix.mvi.UISideEffect
import uv.index.lib.data.*
import uv.index.lib.domain.UVIndexRemoteUpdateUseCase
import uv.index.parts.main.common.ConflatedReducer
import uv.index.parts.main.domain.SunPosition
import uv.index.parts.main.domain.SunRiseSetUseCase
import uv.index.parts.main.domain.UVForecastHoursUseCase
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import javax.inject.Inject
import kotlin.math.roundToInt

@HiltViewModel
class MainViewModel @Inject constructor(
    private val dataRepository: UVIndexRepository,
    private val skinRepository: UVSkinRepository,
    sunRiseSetUseCase: SunRiseSetUseCase
) : SimpleViewModel<MainContract.Event, MainContract.State, UISideEffect>(
    MainContract.State(
        skinType = UVSkinType.Type3
    )
) {
    private val remoteUpdateUseCase = UVIndexRemoteUpdateUseCase(dataRepository, viewModelScope)
    private val hoursUseCase = UVForecastHoursUseCase(dataRepository)
    private val updateJob = ConflatedJob()

    private val zdtAtStartDayAsFlow = MutableStateFlow<ZonedDateTime?>(null)

    private val innerStateUpdater = InnerStateUpdater(sunRiseSetUseCase)

    private val placeAsFlow = flow {
//        emit(UVIPlaceData(ZoneId.ofOffset("UTC", ZoneOffset.ofHours(-5)), 29.7, -124.9))
//
//        delay(10000L)

        emit(UVIPlaceData(ZoneId.systemDefault(), 60.0, 30.0))

    }
        .onEach(innerStateUpdater::setFirstPlaceLoadingComplete)
        .distinctUntilChanged()
        .onEach(innerStateUpdater::newPlace)
        .filterNotNull()
        .onEach(::notifyNewStartDay)

    init {
        combine(
            placeAsFlow,
            zdtAtStartDayAsFlow.filterNotNull()
        ) { place: UVIPlaceData, zdtAtStartDay: ZonedDateTime ->
            attachDataObserver(place, zdtAtStartDay)
        }.launchIn(viewModelScope)

        remoteUpdateUseCase.asFlow
            .onEach(innerStateUpdater::remoteUpdateState)
            .launchIn(viewModelScope)
    }

    private fun attachDataObserver(place: UVIPlaceData, zdtAtStartDay: ZonedDateTime) {
        updateJob += dataRepository.getDataAsFlow(
            place.longitude,
            place.latitude,
            zdtAtStartDay
        )
            .onEach { list: List<UVIndexData> ->
                if (list.size < 23) return@onEach

                innerStateUpdater.setCurrentDayData(place, list)

                val forecastList: List<UVSummaryDayData> = dataRepository.getForecastData(
                    place.longitude,
                    place.latitude,
                    zdtAtStartDay.plusDays(1L)
                )


                val hoursList: List<UVIndexData> = hoursUseCase(
                    place.longitude,
                    place.latitude,
                    zdtAtStartDay
                )

                innerStateUpdater.setForecastData(forecastList, hoursList)
            }
            .onEach {
                remoteUpdateUseCase.checkAndUpdate(state.value.place, state.value.currentDayData)
                innerStateUpdater.updateStateWithCurrentTime()
            }
            .flowOn(Dispatchers.IO)
            .launchIn(viewModelScope)
    }

    override fun handleEvents(event: MainContract.Event) {
        when (event) {
            MainContract.Event.DoDataAutoUpdate -> {
                remoteUpdateUseCase.checkAndUpdate(state.value.place, state.value.currentDayData)
            }
            is MainContract.Event.DoChangeSkin -> TODO()
            MainContract.Event.DoDataManualUpdate -> TODO()
            MainContract.Event.DoUpdateWithCurrentTime ->
                innerStateUpdater.updateStateWithCurrentTime()
        }
    }

    private fun notifyNewStartDay(place: UVIPlaceData) {
        zdtAtStartDayAsFlow.update {
            LocalDate.now(place.zone).atStartOfDay(place.zone)
        }
    }

    private inner class InnerStateUpdater(sunRiseSetUseCase: SunRiseSetUseCase) {

        private val currentDateTimeReducer = CurrentDateTimeReducer(
            viewModelScope,
            sunRiseSetUseCase
        ) { prevValue, nextValue ->
            reduceState {
                if (this == prevValue) nextValue else this
            }
        }

        fun updateStateWithCurrentTime() {
            currentDateTimeReducer.launchReduce(state) {
                currentDateTimeReducer.reduce(it) { zdt ->
                    zdtAtStartDayAsFlow.update { zdt }
                }
            }
        }

        @Suppress("UNUSED_PARAMETER")
        fun setFirstPlaceLoadingComplete(place: UVIPlaceData?) {
            reduceState {
                copy(isLoadingPlace = false)
            }
        }

        fun newPlace(place: UVIPlaceData) {
            reduceState {
                copy(
                    place = place,
                    currentDayData = null,
                    currentSummaryDayData = null,
                    daysForecast = null,
                    isViewRetry = false,
                    isViewLoadingData = false,
                    hoursForecast = listOf(),
                    currentSunPosition = null,
                    currentIndexValue = null,
                    currentUiHoursData = listOf(),
                    riseTime = null,
                    setTime = null,
                    currentZdt = null
                )
            }
        }

        fun remoteUpdateState(state: UVIndexRepository.RemoteUpdateState) {
            reduceState {
                when (state) {
                    is UVIndexRepository.RemoteUpdateState.Failure -> {
                        copy(
                            isViewLoadingData = false,
                            isViewRetry = currentDayData?.let { it.size < 23 } ?: true
                        )
                    }
                    UVIndexRepository.RemoteUpdateState.Loading ->
                        copy(
                            isViewLoadingData = true,
                            isViewRetry = false
                        )
                    is UVIndexRepository.RemoteUpdateState.Success<*> -> {
                        copy(
                            isViewLoadingData = false,
                            isViewRetry = currentDayData?.let { it.size < 23 } ?: true
                        )
                    }
                    UVIndexRepository.RemoteUpdateState.None -> {
                        copy(
                            isViewLoadingData = false,
                            isViewRetry = false
                        )
                    }
                }
            }
        }

        fun setCurrentDayData(place: UVIPlaceData, list: List<UVIndexData>) {
            reduceState {
                copy(
                    currentDayData = list,
                    currentSummaryDayData = UVSummaryDayData.createFromDayList(
                        place.zone,
                        list
                    )
                )
            }
        }

        fun setForecastData(
            daysForecast: List<UVSummaryDayData>,
            hoursForecast: List<UVIndexData>
        ) {
            reduceState {
                copy(
                    daysForecast = daysForecast,
                    hoursForecast = hoursForecast
                )
            }
        }

    }

    private companion object {

        val formatter: DateTimeFormatter = DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT)

        private class CurrentDateTimeReducer(
            scope: CoroutineScope,
            private val sunRiseSetUseCase: SunRiseSetUseCase,
            compareAndSet: (prevValue: MainContract.State, nextValue: MainContract.State) -> Unit
        ) : ConflatedReducer<MainContract.State>(
            scope = scope,
            compareAndSet = compareAndSet
        ) {

            private val uiHourReducer = UIHourReducer()

            suspend fun reduce(
                state: MainContract.State,
                updateStartOfDay: (ZonedDateTime) -> Unit
            ): MainContract.State {

                val place = state.place ?: return state

                val currentDate = LocalDate.now(place.zone)
                val currentZdt = ZonedDateTime.now(place.zone)
                val atStartOfDayZdt = currentDate.atStartOfDay(place.zone)
                updateStartOfDay(atStartOfDayZdt)

                val (riseTime, setTime) = sunRiseSetUseCase(place, atStartOfDayZdt)

                val currentHour = currentZdt.hour + currentZdt.minute / 60.0

                val innerCurrentDayData = state.currentDayData?.let { list ->
                    if (list.size == 24) return@let list
                    (0..23).map {
                        list.getOrNull(it) ?: UVIndexData(0L, 0, 0, 0.0)
                    }
                }

                val currentIndex = innerCurrentDayData?.getCurrentIndex(currentHour)

                val sunPosition = sunRiseSetUseCase.getPosition(place, currentZdt)

                val timeToBurn =
                    if (innerCurrentDayData != null && sunPosition == SunPosition.Above) {
                        val minTime = state.skinType.getIntegralMinTimeToBurnInMins(
                            list = innerCurrentDayData,
                            currentHour = currentHour
                        )?.roundToInt()

                        val maxTime = state.skinType.getIntegralMaxTimeToBurnInMins(
                            list = innerCurrentDayData,
                            currentHour = currentHour
                        )?.roundToInt()
                        if (minTime != null) {
                            MainContract.TimeToBurn.Value(
                                minTimeInMins = minTime,
                                maxTimeInMins = maxTime
                            )
                        } else {
                            MainContract.TimeToBurn.Infinity
                        }
                    } else {
                        MainContract.TimeToBurn.Infinity
                    }


                val uiHourData = uiHourReducer.reduce(
                    sunRiseSetUseCase,
                    place,
                    state.hoursForecast,
                    currentZdt
                ) ?: state.currentUiHoursData


                return state.copy(
                    currentZdt = currentZdt,
                    riseTime = riseTime,
                    setTime = setTime,
                    currentIndexValue = currentIndex,
                    currentSunPosition = sunPosition,
                    currentUiHoursData = uiHourData,
                    currentTimeToBurn = timeToBurn
                )
            }
        }
    }


    private class UIHourReducer {

        @Volatile
        private var previousHash: Int? = null

        private fun checkHash(
            place: UVIPlaceData,
            zdt: ZonedDateTime,
            hoursList: List<UVIndexData>
        ): Boolean {
            val hash = place.hashCode() + zdt.dayOfYear * 10000 + zdt.hour * 100 + hoursList.size
            return (hash == previousHash).also {
                previousHash = hash
            }
        }

        suspend fun reduce(
            sunRiseSetUseCase: SunRiseSetUseCase,
            place: UVIPlaceData,
            hoursList: List<UVIndexData>,
            currentZdt: ZonedDateTime,
        ): List<MainContract.UIHourData>? {
            if (hoursList.isEmpty()) return emptyList()

            val firstTime = currentZdt.toEpochSecond() - 3600L
            val firstZdt = Instant.ofEpochSecond(firstTime).atZone(place.zone)

            if (checkHash(place, firstZdt, hoursList)) return null

            return hoursList
                .asSequence()
                .filter { it.time > firstTime }
                .take(24)
                .flatMapIndexed { i: Int, data: UVIndexData ->
                    sequence {
                        val zdt = Instant
                            .ofEpochSecond(data.time)
                            .atZone(currentZdt.zone)

                        if (zdt.hour < 1 && i > 0) yield(MainContract.UIHourData.Divider)

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
                            MainContract.UIHourData.Item(
                                sIndex = index.toString(),
                                iIndex = iIndex,
                                time = localTime.format(formatter),
                            )
                        )
                    }
                }
                .toList()
        }
    }


}





package uv.index.features.main.ui

import android.util.Log
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import net.arwix.coroutines.ConflatedJob
import net.arwix.mvi.SimpleViewModel
import net.arwix.mvi.UISideEffect
import uv.index.common.*
import uv.index.features.main.data.toUVIPlaceData
import uv.index.features.main.domain.WeatherUseCaseFactory
import uv.index.features.main.ui.transform.SunDataUseCase
import uv.index.features.main.ui.transform.UVICurrentDataUseCase
import uv.index.features.main.ui.transform.UVIForecastUseCase
import uv.index.features.place.data.room.PlaceDao
import uv.index.features.place.data.room.PlaceData
import uv.index.features.weather.data.repository.WeatherRequest
import uv.index.lib.data.*
import uv.index.lib.domain.UVIndexRemoteUpdateUseCase
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZonedDateTime
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class MainViewModel @Inject constructor(
    placeDao: PlaceDao,
    private val sunDataUseCase: SunDataUseCase,
    private val uvIndexRepository: UVIndexRepository,
    private val skinRepository: UVSkinRepository,
    private val uvRemoteUpdateUseCase: UVIndexRemoteUpdateUseCase,
    private val uvCurrentDataUseCase: UVICurrentDataUseCase,
    private val uvForecastUseCase: UVIForecastUseCase,
    weatherUseCaseFactory: WeatherUseCaseFactory,

    ) : SimpleViewModel<MainContract.Event, MainContract.State, UISideEffect>(
    MainContract.State()
) {
    private val weatherUseCase = weatherUseCaseFactory
        .createdDependency(viewModelScope, 15, 3)

    private val zdtAtStartDayAsFlow = MutableStateFlow<ZonedDateTime?>(null)
    private val triggerUpdateCurrentDateTime = MutableStateFlow(0)
    private val innerStateReducer = InnerStateReducer()

    private val placeAsFlow = placeDao.getSelectedItemAsFlow()
        .onEach { timeUpdateJob.cancel() }
        .onEach(innerStateReducer::changePlace)
        .distinctUntilChanged()
        .onEach(innerStateReducer::newPlace)
        .filterNotNull()
        .onEach { place ->
            val atStartDay = LocalDate.now(place.zone).atStartOfDay(place.zone)
            zdtAtStartDayAsFlow.update { atStartDay }
        }

    private val timeUpdateJob = ConflatedJob()

    init {

        weatherUseCase
            .state
            .onEach { weatherState ->
                reduceState {
                    copy(weatherData = weatherState.data)
                }
            }
            .launchIn(viewModelScope)

        combine(
            placeAsFlow,
            zdtAtStartDayAsFlow.filterNotNull(),
            transform = { place, atStartDay ->
                PartialData().apply {
                    this.place = place
                    this.atStartDayDate = atStartDay
                }
            }
        )
            .flatMapLatest { data ->
                sunDataUseCase(data.place, data.atStartDayDate, ZonedDateTime.now(data.place.zone))
                    .also(innerStateReducer::setSunData)
                uvIndexRepository
                    .getDataAsFlow(
                        longitude = data.place.latLng.longitude,
                        latitude = data.place.latLng.latitude,
                        data.atStartDayDate
                    )
//                    .onEach {
//                        Log.e("thread", Thread.currentThread().name)
//                        delay(10_000)
//                    }
                    .filter { list ->
                        (list.size > HOURS_IN_DAY - 1).also { if (!it) innerStateReducer.loadingDataError() }
                    }
                    .map { list ->
                        data.apply { this.list = list }
                            .also { innerStateReducer.setCurrentUVDay(it.place, it.list) }
                    }
            }
            .flatMapLatest { data ->
                uvIndexRepository
                    .getForecastDataAsFlow(
                        data.place.latLng.longitude,
                        data.place.latLng.latitude,
                        data.atStartDayDate
                    )
                    .onStart { emit(emptyList()) }
                    .onEach { forecastDays ->
                        innerStateReducer.setUVForecastDays(forecastDays)
                    }.map { data }
            }
            .distinctUntilChanged()
            .onEach { data ->
                viewModelScope
                    .launch {
                        uvRemoteUpdateUseCase
                            .checkAndUpdate(this, data.place.toUVIPlaceData(), data.list)
                    }
            }
            .onEach { dta ->
                launchConflatedPart(dta)
                viewModelScope.launch {
                    weatherUseCase.newPlace(
                        WeatherRequest.Location(dta.place.latLng)
                    )
                }
//                val r = weatherApi.get(dta.place.latLng.latitude, dta.place.latLng.longitude).also {
//                    Log.e("data", it.toString())
//                }
//
//                reduceState {
//                    copy(weatherData = r)
//                }
            }
            .flowOn(Dispatchers.Default)
            .launchIn(viewModelScope)

        uvRemoteUpdateUseCase
            .asFlow
            .onEach(innerStateReducer::remoteUpdateState)
            .launchIn(viewModelScope)

//        viewModelScope.launch {

//        }

    }


    override fun handleEvents(event: MainContract.Event) {
        when (event) {
            MainContract.Event.DoDataAutoUpdate -> {
                uvRemoteUpdateUseCase.checkAndUpdate(
                    viewModelScope,
                    state.value.place?.toUVIPlaceData(),
                    state.value.uvCurrentSummaryDayData?.hours
                )
            }
            is MainContract.Event.DoChangeSkin -> {
                if (state.value.skinType == event.skin) return
                viewModelScope.launch {
                    skinRepository.setSkin(event.skin)
                }
            }
            MainContract.Event.DoDataManualUpdate -> {
                viewModelScope.launch {
                    uvRemoteUpdateUseCase.update(
                        viewModelScope,
                        state.value.place?.toUVIPlaceData()
                    )
                }
            }
            MainContract.Event.DoUpdateWithCurrentTime -> updateStateWithCurrentTime()
            MainContract.Event.DoChangeViewMode -> {
                reduceState {
                    copy(
                        viewMode =
                        if (viewMode == MainContract.ViewMode.UV)
                            MainContract.ViewMode.Weather
                        else
                            MainContract.ViewMode.UV
                    )
                }
            }
        }
    }

    private fun updateStateWithCurrentTime() {
        viewModelScope.launch {
            triggerUpdateCurrentDateTime.emit(triggerUpdateCurrentDateTime.value + 1)
        }
    }

    private fun launchConflatedPart(partialData: PartialData) {
        timeUpdateJob += listOf(partialData).asFlow()
            .addTrigger(skinRepository.asFlow(UVSkinType.Type3)) { data, skinType ->
                data.skinType = skinType.also(innerStateReducer::setSkin)
                innerStateReducer.setSkin(skinType)
            }
            .addTrigger(triggerUpdateCurrentDateTime) { data, _ ->
                viewModelScope.launch {
                    weatherUseCase.autoCheckTimeToRemoteUpdate()
                }
                data.currentDateTime = ZonedDateTime.now(data.place.zone)
                    .also(innerStateReducer::setCurrentDateTime)
            }
            .applyData {
                sunData = sunDataUseCase(place, atStartDayDate, currentDateTime)
                    .also(innerStateReducer::setSunData)
            }
//            .onEach {
//                delay(5_000)
//            }
            .applyData {
                uvCurrentDataUseCase(place, skinType, sunData.position, list)
                    .also(innerStateReducer::setCurrentUVIData)
            }
            .applyData {
                uvForecastUseCase.getHours(
                    place,
                    atStartDayDate,
                    currentDateTime
                )
                    .also {
                        innerStateReducer.setUVForecastHours(
                            it.forecastList ?: listOf()
                        )
                        innerStateReducer.setUVPeakTime(it.maxTime)
                        innerStateReducer.setUVCurrentHours(it.currentDayList ?: listOf())
                    }
            }
            .launchIn(viewModelScope)
    }

    private inner class InnerStateReducer {

        private val checkErrorJob = ConflatedJob()

        @Suppress("UNUSED_PARAMETER")
        fun changePlace(place: PlaceData?) = reduceState {
            copy(isLoadingPlace = false)
        }

        fun loadingDataError() {
            reduceState {
                copy(
                    isViewRetry = true,
                    uvCurrentSummaryDayData = null,
                    uvForecastDays = listOf(),
                    isViewLoadingData = false,
                    currentSunData = null,
                    uvCurrentData = null,
                    currentDateTime = null
                )
            }
        }

        fun newPlace(place: PlaceData?) {
            reduceState {
                MainContract.State(place = place, skinType = this.skinType, isLoadingPlace = false)
            }
//            reduceState {
//                MainContract.State(place = place, skinType = this.skinType)
//            }
        }


        fun remoteUpdateState(state: UVIndexRepository.RemoteUpdateState) {
            reduceState {
                Log.e("update state", state.toString())
                when (state) {
                    is UVIndexRepository.RemoteUpdateState.Failure -> {
                        copy(
                            isViewLoadingData = false,
                            isViewRetry = true
                        )
                    }
                    UVIndexRepository.RemoteUpdateState.Loading ->
                        copy(
                            isViewLoadingData = true,
                            isViewRetry = false
                        )
                    is UVIndexRepository.RemoteUpdateState.Success<*> -> {
                        checkErrorJob += viewModelScope.launch {
                            delay(5000)
                            reduceState {
                                copy(isViewRetry = !isViewLoadingData && this.uvCurrentData == null)
                            }
                        }
                        copy(
                            isViewLoadingData = false,
//                            isViewRetry = false
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

        fun setCurrentUVDay(place: PlaceData, list: List<UVIndexData>) {
            reduceState {
                copy(
                    uvCurrentSummaryDayData = UVSummaryDayData.createFromDayList(
                        place.zone,
                        list
                    )
                )
            }
        }

        fun setCurrentDateTime(zdt: ZonedDateTime) {
            reduceState { copy(currentDateTime = zdt) }
        }

        fun setUVForecastDays(forecastDays: List<UVSummaryDayData>) {
            reduceState { copy(uvForecastDays = forecastDays) }
        }

        fun setUVForecastHours(forecastHours: List<MainContract.UVHourData>) {
            reduceState { copy(uvForecastHours = forecastHours) }
        }

        fun setUVCurrentHours(currentHours: List<MainContract.UVHourData>) {
            reduceState { copy(uvCurrentDayHours = currentHours) }
        }


        fun setSkin(skin: UVSkinType) {
            reduceState { copy(skinType = skin) }
        }

        fun setSunData(sunData: MainContract.SunData) {
            reduceState { copy(currentSunData = sunData) }
        }

        fun setCurrentUVIData(data: MainContract.UVCurrentData) {
            reduceState { copy(uvCurrentData = data) }
        }

        fun setUVPeakTime(time: LocalTime?) {
            reduceState { copy(peakTime = time) }
        }
    }

    private companion object {
        const val FORECAST_HOUR_COUNT = 24
        const val HOURS_IN_DAY = 24

        private class PartialData {
            lateinit var place: PlaceData
            lateinit var atStartDayDate: ZonedDateTime
            lateinit var currentDateTime: ZonedDateTime
            lateinit var skinType: UVSkinType
            lateinit var list: List<UVIndexData>
            lateinit var sunData: MainContract.SunData
        }
    }

}
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
import uv.index.features.main.domain.WeatherUseCase
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

    private val startOfDayZDTimeFlow = MutableStateFlow<ZonedDateTime?>(null)
    private val triggerUpdateCurrentDateTimeFlow = MutableStateFlow(0)
    private val innerStateReducer = InnerStateReducer()

    private val selectedPlaceFlow = placeDao.getSelectedItemAsFlow()
        .onEach { timeUpdateJob.cancel() }
        .onEach(innerStateReducer::changePlace)
        .distinctUntilChanged()
        .onEach(innerStateReducer::newPlace)
        .filterNotNull()
        .onEach { place ->
            val startOfDayZDTime = LocalDate.now(place.zone).atStartOfDay(place.zone)
            startOfDayZDTimeFlow.update { startOfDayZDTime }
        }

    private val timeUpdateJob = ConflatedJob()

    init {

        observeWeatherData(weatherUseCase)

        selectedPlaceFlow
            .combine(startOfDayZDTimeFlow.filterNotNull()) { place, startOfDayZDTime ->
                PartialData(place, startOfDayZDTime)
            }
            .flatMapLatest { data ->
                fetchSunData(sunDataUseCase, data)
                fetchUVIndexData(data)
            }
            .flatMapLatest { data ->
                fetchUVForecastData(data)
            }
            .distinctUntilChanged()
            .onEach { data ->
                viewModelScope.launch {
                    uvRemoteUpdateUseCase.checkAndUpdate(
                        this,
                        data.place.toUVIPlaceData(),
                        data.list
                    )
                }
            }
            .onEach { dta ->
                launchTimeTickPart(dta)
                viewModelScope.launch {
                    weatherUseCase.newPlace(WeatherRequest.Location(dta.place.latLng))
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

        observeRemoteUpdateState(uvRemoteUpdateUseCase)
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
                        viewMode = if (viewMode == MainContract.ViewMode.UV)
                            MainContract.ViewMode.Weather
                        else
                            MainContract.ViewMode.UV
                    )
                }
            }
        }
    }

    private fun observeWeatherData(weatherUseCase: WeatherUseCase) {
        weatherUseCase.state
            .onEach { weatherState ->
                reduceState {
                    copy(weatherData = weatherState.data)
                }
            }
            .launchIn(viewModelScope)
    }

    private fun observeRemoteUpdateState(remoteUpdateUseCase: UVIndexRemoteUpdateUseCase) {
        remoteUpdateUseCase.asFlow
            .onEach(innerStateReducer::remoteUpdateState)
            .launchIn(viewModelScope)
    }

    private suspend fun fetchSunData(
        sunDataUseCase: SunDataUseCase,
        data: PartialData
    ): Flow<MainContract.SunData> {
        return flow {
            emit(sunDataUseCase(
                data.place,
                data.atStartDayDate,
                ZonedDateTime.now(data.place.zone)
            ).also(innerStateReducer::setSunData))
        }
    }

    private fun fetchUVIndexData(data: PartialData): Flow<PartialData> {

        return uvIndexRepository.getDataAsFlow(
            longitude = data.place.latLng.longitude,
            latitude = data.place.latLng.latitude,
            data.atStartDayDate
        )
            .filter { list ->
                (list.size > HOURS_IN_DAY - 1).also {
                    if (!it) innerStateReducer.loadingDataError()
                }
            }
            .map { list ->
                data.apply { this.list = list }
                    .also { innerStateReducer.setCurrentUVDay(it.place, it.list) }
            }
    }

    private fun fetchUVForecastData(data: PartialData): Flow<PartialData> {
        return uvIndexRepository.getForecastDataAsFlow(
            data.place.latLng.longitude,
            data.place.latLng.latitude,
            data.atStartDayDate
        )
            .onStart { emit(emptyList()) }
            .onEach { forecastDays ->
                innerStateReducer.setUVForecastDays(forecastDays)
            }
            .map { data }
    }

    private fun updateStateWithCurrentTime() {
        viewModelScope.launch {
            triggerUpdateCurrentDateTimeFlow.emit(triggerUpdateCurrentDateTimeFlow.value + 1)
        }
    }

    private fun launchTimeTickPart(partialData: PartialData) {
        timeUpdateJob += partialData.asFlow()
            .addSkinTrigger(innerStateReducer, skinRepository.asFlow(UVSkinType.Type3))
            .addDateTimeTrigger(innerStateReducer, triggerUpdateCurrentDateTimeFlow)
            .onEach {
                viewModelScope.launch {
                    weatherUseCase.autoCheckTimeToRemoteUpdate()
                }
            }
            .applySunData(innerStateReducer, sunDataUseCase)
            .applyUVCurrentData(innerStateReducer, uvCurrentDataUseCase)
            .applyUVForecastData(innerStateReducer, uvForecastUseCase)
            .launchIn(viewModelScope)
    }

    private inner class InnerStateReducer {

        private val checkErrorJob = ConflatedJob()

        @Suppress("UNUSED_PARAMETER")
        fun changePlace(place: PlaceData?) = reduceState {
            copy(loadingPlace = false)
        }

        fun loadingDataError() {
            reduceState {
                copy(
                    viewRetry = true,
                    uvCurrentSummaryDayData = null,
                    uvForecastDays = emptyList(),
                    viewLoadingData = false,
                    currentSunData = null,
                    uvCurrentData = null,
                    currentDateTime = null
                )
            }
        }

        fun newPlace(place: PlaceData?) {
            reduceState {
                MainContract.State(place = place, skinType = this.skinType, loadingPlace = false)
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
                            viewLoadingData = false,
                            viewRetry = true
                        )
                    }

                    UVIndexRepository.RemoteUpdateState.Loading ->
                        copy(
                            viewLoadingData = true,
                            viewRetry = false
                        )

                    is UVIndexRepository.RemoteUpdateState.Success<*> -> {
                        checkErrorJob += viewModelScope.launch {
                            delay(5000)
                            reduceState {
                                copy(viewRetry = !viewLoadingData && this.uvCurrentData == null)
                            }
                        }
                        copy(
                            viewLoadingData = false,
//                            isViewRetry = false
                        )
                    }

                    UVIndexRepository.RemoteUpdateState.None -> {
                        copy(
                            viewLoadingData = false,
                            viewRetry = false
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

        private class PartialData(
            val place: PlaceData,
            val atStartDayDate: ZonedDateTime
        ) {
            lateinit var currentDateTime: ZonedDateTime
            lateinit var skinType: UVSkinType
            lateinit var list: List<UVIndexData>
            lateinit var sunData: MainContract.SunData

            fun asFlow() = flow { emit(this@PartialData) }
        }

        private fun Flow<PartialData>.addSkinTrigger(
            reducer: InnerStateReducer,
            skinTrigger: Flow<UVSkinType>,
        ): Flow<PartialData> {
            return addTrigger(skinTrigger) { data, skinType ->
                data.skinType = skinType.also(reducer::setSkin)
            }
        }

        private fun Flow<PartialData>.addDateTimeTrigger(
            reducer: InnerStateReducer,
            timeTrigger: StateFlow<*>,
        ): Flow<PartialData> {
            return addTrigger(timeTrigger) { data, _ ->
                data.currentDateTime =
                    ZonedDateTime.now(data.place.zone).also(reducer::setCurrentDateTime)
            }
        }

        private fun Flow<PartialData>.applySunData(
            reducer: InnerStateReducer,
            useCase: SunDataUseCase
        ): Flow<PartialData> {
            return applyData {
                sunData = useCase(place, atStartDayDate, currentDateTime)
                    .also(reducer::setSunData)
            }
        }

        private fun Flow<PartialData>.applyUVCurrentData(
            reducer: InnerStateReducer,
            useCase: UVICurrentDataUseCase
        ): Flow<PartialData> {
            return applyData {
                useCase(place, skinType, sunData.position, list)
                    .also(reducer::setCurrentUVIData)
            }
        }

        private fun Flow<PartialData>.applyUVForecastData(
            reducer: InnerStateReducer,
            useCase: UVIForecastUseCase
        ): Flow<PartialData> {
            return applyData {
                useCase
                    .getHours(
                        place,
                        atStartDayDate,
                        currentDateTime
                    )
                    .also {
                        reducer.setUVForecastHours(
                            it.forecastList ?: listOf()
                        )
                        reducer.setUVPeakTime(it.maxTime)
                        reducer.setUVCurrentHours(it.currentDayList ?: listOf())
                    }
            }
        }


    }


}
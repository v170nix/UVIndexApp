package uv.index.parts.main.ui

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import net.arwix.extension.ConflatedJob
import net.arwix.mvi.SimpleViewModel
import net.arwix.mvi.UISideEffect
import uv.index.lib.data.*
import uv.index.lib.domain.UVIndexRemoteUpdateUseCase
import uv.index.parts.main.domain.SunRiseSetUseCase
import uv.index.parts.main.domain.UVForecastHoursUseCase
import java.time.LocalDate
import java.time.ZoneId
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class MainViewModel @Inject constructor(
    private val dataRepository: UVIndexRepository,
    private val skinRepository: UVSkinRepository,
    private val sunRiseSetUseCase: SunRiseSetUseCase
) : SimpleViewModel<MainContract.Event, MainContract.State, UISideEffect>(
    MainContract.State(
        skinType = skinRepository.getSkinOrNull() ?: UVSkinType.Type3
    )
) {

    private val remoteUpdateUseCase = UVIndexRemoteUpdateUseCase(dataRepository, viewModelScope)
    private val hoursUseCase = UVForecastHoursUseCase(dataRepository)
    private val updateJob = ConflatedJob()

    init {
        flow { emit(UVIPlaceData(ZoneId.systemDefault(), 60.1, 30.2)) }
            .onEach { reduceState { copy(isLoadingPlace = false) } }
            .distinctUntilChanged()
            .onEach { place ->
                reduceState {
                    copy(
                        place = place,
                        currentDayData = null,
                        currentSummaryDayData = null,
                        forecastData = null,
                        isViewRetry = false,
                        isViewLoadingData = false
                    )
                }
                updateRiseSetData(place)
            }
            .filterNotNull()
            .flatMapLatest { place ->
                val dateAtStartDay = LocalDate.now(place.zone).atStartOfDay(place.zone)
                dataRepository.getDataAsFlow(
                    place.longitude,
                    place.latitude,
                    dateAtStartDay
                ).onEach { list ->
                    if (list.size < 23) return@onEach
                    reduceState {
                        copy(
                            currentDayData = list,
                            currentSummaryDayData = UVSummaryDayData.createFromDayList(
                                place.zone,
                                list
                            )
                        )
                    }
                    updateForecastData(place)
                }
            }
            .onEach {
                remoteUpdateUseCase.checkAndUpdate(state.value.place, state.value.currentDayData)
            }
            .flowOn(Dispatchers.IO)
            .launchIn(viewModelScope)

        remoteUpdateUseCase.asFlow
            .onEach { state ->
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
                            place?.run(::updateForecastData)
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
            .launchIn(viewModelScope)

    }

    private fun updateForecastData(place: UVIPlaceData) {
        updateJob += viewModelScope.launch(Dispatchers.IO) {
            val dateAtStartDay = LocalDate.now(place.zone).atStartOfDay(place.zone)
            val forecastList = dataRepository.getForecastData(
                place.longitude,
                place.latitude,
                dateAtStartDay.plusDays(1L)
            )
            val hoursList = hoursUseCase(
                place.longitude,
                place.latitude,
                dateAtStartDay
            )
            reduceState {
                copy(
                    forecastData = forecastList,
                    hoursData = hoursList
                )
            }
        }
    }

    private suspend fun updateRiseSetData(
        place: UVIPlaceData,
    ) = withContext(Dispatchers.Default) {
        val currentZdt = LocalDate.now(place.zone).atStartOfDay(place.zone)
        val (riseTime, setTime) = sunRiseSetUseCase(place, currentZdt)
        reduceState {
            copy(
                riseTime = riseTime,
                setTime = setTime
            )
        }
    }

    override fun handleEvents(event: MainContract.Event) {
        when (event) {
            MainContract.Event.DoAutoUpdate -> {
                remoteUpdateUseCase.checkAndUpdate(state.value.place, state.value.currentDayData)
                val place = state.value.place ?: return
                viewModelScope.launch { updateRiseSetData(place) }
            }
            is MainContract.Event.DoChangeSkin -> TODO()
            MainContract.Event.DoManualUpdate -> TODO()
        }
    }

}
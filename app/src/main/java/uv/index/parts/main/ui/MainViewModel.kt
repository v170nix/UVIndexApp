package uv.index.parts.main.ui

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import net.arwix.extension.ConflatedJob
import net.arwix.mvi.SimpleViewModel
import net.arwix.mvi.UISideEffect
import uv.index.lib.data.*
import uv.index.lib.domain.UVIndexRemoteUpdateUseCase
import uv.index.parts.main.domain.UVForecastHoursUseCase
import java.time.LocalDate
import java.time.ZoneId
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class MainViewModel @Inject constructor(
    private val repository: UVIndexRepository,
    private val skinRepository: UVSkinRepository
) : SimpleViewModel<MainContract.Event, MainContract.State, UISideEffect>(
    MainContract.State(
        skinType = skinRepository.getSkinOrNull() ?: UVSkinType.Type3
    )
) {

    private val remoteUpdateUseCase = UVIndexRemoteUpdateUseCase(repository, viewModelScope)
    private val hoursUseCase = UVForecastHoursUseCase(repository)
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
            }
            .filterNotNull()
            .flatMapLatest { place ->
                val dateAtStartDay = LocalDate.now(place.zone).atStartOfDay(place.zone)
                repository.getDataAsFlow(
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
            .onEach {  state ->
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
                        is UVIndexRepository.RemoteUpdateState.Success<*> ->  {
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
            val forecastList = repository.getForecastData(
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

    override fun handleEvents(event: MainContract.Event) {
        TODO("Not yet implemented")
    }

}
package uv.index.features.weather.data.repository

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.*
import net.arwix.repo.LoadState
import net.arwix.repo.LoadState.Companion.complete
import net.arwix.repo.LoadState.Companion.error
import net.arwix.repo.LoadState.Companion.incomplete
import net.arwix.repo.LoadState.Companion.start
import net.arwix.repo.RemoteRepository
import uv.index.features.weather.data.WeatherApi
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WeatherRemoteRepository @Inject constructor(
    private val weatherApi: WeatherApi,
) : RemoteRepository<WeatherRequest> {

    private val _state: MutableStateFlow<LoadState<out WeatherRequest>> =
        MutableStateFlow(WeatherRequest.Empty.incomplete())

    override val state: StateFlow<LoadState<out WeatherRequest>> = _state.asStateFlow()

    override fun requestLoad(request: WeatherRequest) = flow {
        when (request) {
            is WeatherRequest.Empty -> emit(request.incomplete())
            is WeatherRequest.Location -> {
                emit(request.start())
                runCatching {
                    weatherApi.get(request.latLng.latitude, request.latLng.longitude)
                }.onSuccess {
                    emit(request.complete(it))
                }.onFailure { error ->
                    if (error is CancellationException) throw error
                    emit(request.error(error))
                }
            }
        }
    }.onEach { state ->
        _state.update {
            state
        }
    }
}
package uv.index.features.weather.data.repository

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.*
import uv.index.common.remote.LoadState
import uv.index.common.remote.LoadState.Companion.complete
import uv.index.common.remote.LoadState.Companion.error
import uv.index.common.remote.LoadState.Companion.incomplete
import uv.index.common.remote.LoadState.Companion.start
import uv.index.common.remote.RemoteRepository
import uv.index.features.weather.data.WeatherApi
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WeatherRemoteRepository @Inject constructor(
    private val weatherApi: WeatherApi,
) : RemoteRepository<WeatherRequest> {

    private val _state: MutableStateFlow<LoadState<out WeatherRequest>> =
        MutableStateFlow(LoadState.Incomplete(WeatherRequest.Empty))

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
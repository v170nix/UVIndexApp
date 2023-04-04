package uv.index.features.weather.data.repository

import android.util.Log
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.*
import uv.index.common.remote.LoadState
import uv.index.common.remote.LoadState.Companion.complete
import uv.index.common.remote.LoadState.Companion.error
import uv.index.common.remote.LoadState.Companion.incomplete
import uv.index.common.remote.LoadState.Companion.start
import uv.index.common.remote.LocalRepository
import uv.index.features.weather.data.Weather
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WeatherLocalRepository @Inject constructor(
    private val dao: WeatherCacheDao
) :
    LocalRepository<WeatherRequest, Weather.Data> {

    private val _state: MutableStateFlow<LoadState<WeatherRequest>> =
        MutableStateFlow(LoadState.Incomplete(WeatherRequest.Empty))
    override val state: StateFlow<LoadState<WeatherRequest>> =
        _state.asStateFlow()

    override fun requestLoad(
        request: WeatherRequest
    ): Flow<LoadState<out WeatherRequest>> {
        return flow {
            if (request.isEmpty()) {
                emit(request.incomplete())
                return@flow
            }
            emit(request.start())
            runCatching {
                dao.getLonLatData(request)
            }.onSuccess {data ->
                if (data != null) {
                    Log.e("local data", data.realTime.toString())
                    emit(request.complete(data))
                } else {
                    Log.e("local error", "null")
                    emit(request.error(NullPointerException()))
                }
            }.onFailure { error ->
                Log.e("local error", error.toString())
                if (error is CancellationException) throw error
                emit(request.error(error))
            }
        }.onEach {
            _state.update { it }
        }
    }

    override suspend fun saveData(
        request: WeatherRequest,
        data: Weather.Data
    ): Boolean {
        if (request.isEmpty()) return false
        return runCatching {
            dao.insert(request, data)
        }.isSuccess
    }


}
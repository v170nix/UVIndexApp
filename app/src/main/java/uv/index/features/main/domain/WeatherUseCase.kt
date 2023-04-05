package uv.index.features.main.domain

import android.util.ArrayMap
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import net.arwix.coroutines.ConflatedJob
import net.arwix.repo.LoadState
import uv.index.features.weather.data.Weather
import uv.index.features.weather.data.repository.WeatherLocalRepository
import uv.index.features.weather.data.repository.WeatherRemoteRepository
import uv.index.features.weather.data.repository.WeatherRequest
import uv.index.features.weather.data.repository.isEmpty
import java.time.Instant

fun interface WeatherUseCaseFactory {
    fun createdDependency(
        scope: CoroutineScope,
        remoteCheckDeltaInMins: Int,
        remoteRetryCount: Int
    ): WeatherUseCase
}

class WeatherUseCase(
    private val scope: CoroutineScope,
    private val localRepository: WeatherLocalRepository,
    private val remoteRepository: WeatherRemoteRepository,
    private val remoteCheckDeltaInMins: Int = 15,
    remoteRetryCount: Int = 3
) {

    data class WeatherState(
        val request: WeatherRequest = WeatherRequest.Empty,
        val data: Weather.Data? = null,
        val isLoading: Boolean,
        val error: Throwable? = null,
    )

    private val timeLaunchHolder = RemoteTimeLaunchHolder()
    private val retryErrorHolder = RemoteErrorRetryHolder(
        remoteRetryCount,
        remoteCheckDeltaInMins
    )

    private val _state = MutableStateFlow(WeatherState(isLoading = false))
    val state: StateFlow<WeatherState> = _state.asStateFlow()
    private val mutex = Mutex()
    private val job = ConflatedJob()

    init {
        localRepository
            .state
            .filter { isValidRequest(it.request) }
            .onEach {
                mutex.withLock {
                    if (!isValidRequest(it.request)) return@withLock
                    localReduce(it)
                }
            }
            .launchIn(scope)

        remoteRepository
            .state
            .filter { isValidRequest(it.request) }
            .onEach {
                mutex.withLock {
                    if (!isValidRequest(it.request)) return@withLock
                    remoteReduce(it)
                }
            }
            .launchIn(scope)
    }

    suspend fun newPlace(request: WeatherRequest) {
//        if (request == state.value.request) return
        _state.update { state ->
            state.copy(request = request)
        }
        if (request.isEmpty()) return
        val loadState = launchLocalRequest(request)
        mutex.withLock {
            if (!isValidRequest(loadState.request)) return@withLock
            if (loadState is LoadState.Complete<*, *>) {
                _state.update { state ->
                    state.copy(
                        data = loadState.result as Weather.Data,
                    )
                }
                if (checkRemoteLaunch()) {
                    launchRemoteRequest(request)
                }
            } else {
                launchRemoteRequest(request)
            }
        }

    }

    suspend fun autoCheckTimeToRemoteUpdate() {
        mutex.withLock {
            val value = _state.value
            if (value.isLoading) return
            if (value.request.isEmpty()) return
            if (value.data == null) return
            if (timeLaunchHolder.isMore(
                    remoteCheckDeltaInMins,
                    value.request.latLng.latitude,
                    value.request.latLng.longitude
                )
            ) {
                Log.e("auto", "launch remote")
                launchRemoteRequest(value.request)
            }

        }
    }

    fun checkRemoteLaunch(): Boolean {
        //TODO
        return true
    }

    private fun isValidRequest(request: WeatherRequest) =
        request == _state.value.request

    private fun localReduce(loadState: LoadState<WeatherRequest>) {
        when (loadState) {
            is LoadState.Complete<*, *> -> {
                if (_state.value.data != loadState.result) {
                    _state.update { state ->
                        state.copy(data = loadState.result as Weather.Data)
                    }
                }
            }
            is LoadState.Error -> {
                _state.update { state ->
                    state.copy(error = state.error)
                }
            }
            else -> {}
        }
    }

    private suspend fun remoteReduce(loadState: LoadState<out WeatherRequest>) {
        when (loadState) {
            is LoadState.Complete<*, *> -> {
                scope.launch {
                    localRepository.saveData(
                        loadState.request,
                        loadState.result as Weather.Data
                    )
                }
                _state.update { state ->
                    state.copy(
                        data = loadState.result as Weather.Data,
                        isLoading = false,
                        error = null
                    )
                }
            }
            is LoadState.Error -> {
                _state.update { state ->
                    state.copy(
                        error = loadState.error,
                        isLoading = false
                    )
                }
                val request = _state.value.request
                if (request.isEmpty()) return
                if (!retryErrorHolder.isRetryMax(
                        request.latLng.latitude, request.latLng.longitude
                    )
                ) {
                    launchRemoteRequest(request)
                }
            }
            is LoadState.Incomplete -> {}
            is LoadState.ProgressLoading -> {}
            is LoadState.StartLoading -> {
                _state.update { state ->
                    state.copy(
                        error = null,
                        isLoading = true
                    )
                }
            }
        }
    }

    private suspend fun launchLocalRequest(
        request: WeatherRequest.Location
    ): LoadState<out WeatherRequest> {
        return localRepository
            .requestLoad(request)
            .last()
    }

    private fun launchRemoteRequest(
        request: WeatherRequest.Location
    ) {
        timeLaunchHolder.updateLastLaunchInMins(
            request.latLng.latitude,
            request.latLng.longitude
        )
        job += remoteRepository
            .requestLoad(request)
            .onEach { Log.e("remoteLoad", it.toString()) }
            .launchIn(scope)
    }

    private class RemoteErrorRetryHolder(
        private val maxCount: Int,
        private val deltaTimeInMins: Int
    ) {
        private val retryMap = ArrayMap<Pair<Double, Double>, MutableList<Long>>()

        fun isRetryMax(latitude: Double, longitude: Double): Boolean {
            val currentTime = Instant.now().epochSecond / 60
            val timeArray = retryMap
                .getOrPut(latitude to longitude) { mutableListOf() }
                .asSequence().filterNot {
                    currentTime - it > deltaTimeInMins
                }.toMutableList()
            timeArray.add(currentTime)
            retryMap[latitude to longitude] = timeArray
            Log.e("retryMapSize", timeArray.size.toString())
            return timeArray.size > maxCount
        }
    }

    private class RemoteTimeLaunchHolder {
        private val launchTimeMap = ArrayMap<Pair<Double, Double>, Long>()

        fun getLastLaunchInSecond(latitude: Double, longitude: Double): Long? {
            return launchTimeMap[latitude to longitude]
        }

        fun updateLastLaunchInMins(latitude: Double, longitude: Double) {
            launchTimeMap[latitude to longitude] = Instant.now().epochSecond
        }

        fun isMore(mins: Int, latitude: Double, longitude: Double): Boolean {
            val lastLaunch = getLastLaunchInSecond(latitude, longitude) ?: return true
            return Instant.now().epochSecond - lastLaunch > mins * 60
        }


    }
}
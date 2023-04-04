package uv.index.common

import androidx.annotation.RestrictTo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow


abstract class RemoteMediator(

) {

    enum class LoadType { REFRESH }
    enum class InitializeAction { LAUNCH_INITIAL_REFRESH, SKIP_INITIAL_REFRESH }


    sealed class Result {
        class Error(val error: Throwable) : Result()
        object Success : Result()
    }

    suspend fun initialize(): InitializeAction = InitializeAction.LAUNCH_INITIAL_REFRESH

    abstract suspend fun load(loadType: LoadType, state: Any): Result

}

interface RemoteRequest {
    companion object {
        val Empty: RemoteRequest = object : RemoteRequest {}
    }

    fun isEmpty() = this == Empty
}

abstract class RMU<R: RemoteRequest> {

    sealed class LoadState<F : RemoteRequest>(open val request: F) {

        data class Error<R : RemoteRequest> internal constructor(
            override val request: R,
            val error: Throwable,
        ) : LoadState<R>(request)

        data class StartLoading<R : RemoteRequest> internal constructor(
            override val request: R
        ) : LoadState<R>(request)

        data class ProgressLoading<R : RemoteRequest> internal constructor(
            override val request: R,
            val percent: Int
        ) : LoadState<R>(request)

        data class Incomplete<R : RemoteRequest> internal constructor(
            override val request: R
        ) : LoadState<R>(request)

        data class Complete<R : RemoteRequest, T>(
            override val request: R,
            val result: T
        ) : LoadState<R>(request)

        companion object {
            fun <R : RemoteRequest> R.error(error: Throwable) = Error(this, error)
            fun <R : RemoteRequest> R.start() = StartLoading(this)
            fun <R : RemoteRequest> R.progress(percent: Int) = ProgressLoading(this, percent)
            fun <R : RemoteRequest> R.incomplete() = Incomplete(this)
            fun <R : RemoteRequest, T> R.complete(result: T) = Complete(this, result)
        }

    }

    //    data class State<D>(val data: D, val loadState: LoadState)
    abstract val state: StateFlow<LoadState<R>>

//    fun asFlow(): Flow<State<T>> {
//
//    }

    abstract suspend fun load(request: R): Flow<LoadState<R>>


}

//class RemoteMediatorAccess<T>(
//    private val scope: CoroutineScope,
//    private val remoteMediator: RMU<RemoteRequest, T>
//) {
//    private val isolationRunner = SingleRunner()
//
//    fun requestLoad(request: RemoteRequest) {
//    }
//
//
//    private fun launchRefresh(request: RemoteRequest) {
//        scope.launch {
//            isolationRunner.runInIsolation {
//                val result = remoteMediator.load(request)
//            }
//        }
//    }
//
//}

//class DataHolder<R: RemoteRequest, T>(
//
//) {
//     private val lock = ReentrantLock()
//     private val _loadState = MutableStateFlow(LoadStates.IDLE)
//     val loadStates = _loadState.asStateFlow()
//     private val internalState: Pair<R, T>
//
//     fun use(block: (Pair<R, T>) -> R) {
//         lock.withLock {
//             block(internalState).also {
//                 _loadState.value = internalState.computeLoadStates()
//             }
//         }
//     }
//
//
//}

//private class DataHolder<T>(
//    private val scope: CoroutineScope,
//    private val rmu: RMU<RemoteRequest, T>
//) {
//
//    fun requestLoad(request: RemoteRequest) {
//
//    }
//
//    fun retryFailed() {
//
//    }
//}

private class AS<R> {
    private var blockState = AccessorState.BlockState.UNBLOCKED
    private var errors: LoadState.Error? = null
    private val pendingRequests = ArrayDeque<R>()

    private fun computeLoadTypeState(request: R): LoadState {
        val hasPending = pendingRequests.any {
            it == request
        }

        if (hasPending && blockState != AccessorState.BlockState.REQUIRES_REFRESH) {
            return LoadState.Loading
        }

        errors?.let {
            return it
        }

        return when (blockState) {
            AccessorState.BlockState.COMPLETED -> LoadState.NotLoading.Incomplete
            AccessorState.BlockState.REQUIRES_REFRESH -> LoadState.NotLoading.Incomplete
            AccessorState.BlockState.UNBLOCKED -> LoadState.NotLoading.Incomplete
        }
    }

    fun add(
        request: R
    ): Boolean {
        val existing = pendingRequests.firstOrNull {
            it == request
        }
        // De-dupe requests with the same LoadType, just update PagingState and return.
        if (existing != null) return false


        // If blocked on REFRESH, queue up the request, but don't trigger yet. In cases where
        // REFRESH returns endOfPaginationReached, we need to cancel the request. However, we
        // need to queue up this request because it's possible REFRESH may not trigger
        // invalidation even if it succeeds!
        if (blockState == AccessorState.BlockState.REQUIRES_REFRESH) {
            pendingRequests.add(request)
            return false
        }

        // Ignore block state for REFRESH as it is only sent in cases where we want to clear all
        // AccessorState, but we cannot simply generate a new one for an existing PageFetcher as
        // we need to cancel in-flight requests and prevent races between clearing state and
        // triggering remote REFRESH by clearing state as part of handling the load request.
        if (blockState != AccessorState.BlockState.UNBLOCKED) {
            return false
        }

        return true

//        if (loadType == LoadType.REFRESH) {
//            // for refresh, we ignore error states. see: b/173438474
//            setError(LoadType.REFRESH, null)
//        }
//        return if (errors[loadType.ordinal] == null) {
//            pendingRequests.add(PendingRequest(loadType, pagingState))
//        } else {
//            false
//        }
    }

}


sealed class LoadState {
    class Error(val error: Throwable) : LoadState()
    object Loading : LoadState()
    class NotLoading(private val isComplete: Boolean) : LoadState() {
        internal companion object {
            internal val Complete = NotLoading(isComplete = true)
            internal val Incomplete = NotLoading(isComplete = false)
        }
    }
}


private class AccessorState {
    private val blockStates = Array<BlockState>(RemoteMediator.LoadType.values().size) {
        BlockState.UNBLOCKED
    }
    private val errors = Array<LoadState.Error?>(RemoteMediator.LoadType.values().size) {
        null
    }


    private val pendingRequests = ArrayDeque<PendingRequest>()

    enum class BlockState { UNBLOCKED, COMPLETED, REQUIRES_REFRESH }

    class PendingRequest(
        val loadType: RemoteMediator.LoadType,
//        var pagingState: PagingState<Key, Value>
    )

    fun computeLoadStates(): LoadStates {
        return LoadStates(
            refresh = computeLoadTypeState(RemoteMediator.LoadType.REFRESH),
//            append = computeLoadTypeState(LoadType.APPEND),
//            prepend = computeLoadTypeState(LoadType.PREPEND)
        )
    }

//    fun computeLoadStates(): LoadStates {
//        return LoadStates(
//            refresh = computeLoadTypeState(LoadType.REFRESH),
//            append = computeLoadTypeState(LoadType.APPEND),
//            prepend = computeLoadTypeState(LoadType.PREPEND)
//        )
//    }

    private fun computeLoadTypeState(loadType: RemoteMediator.LoadType): LoadState {
        val blockState = blockStates[loadType.ordinal]
        val hasPending = pendingRequests.any {
            it.loadType == loadType
        }

        if (hasPending && blockState != BlockState.REQUIRES_REFRESH) {
            return LoadState.Loading
        }

        errors[loadType.ordinal]?.let {
            return it
        }

        return when (blockState) {
            BlockState.COMPLETED -> when (loadType) {
                RemoteMediator.LoadType.REFRESH -> LoadState.NotLoading.Incomplete
                else -> LoadState.NotLoading.Complete
            }
            BlockState.REQUIRES_REFRESH -> LoadState.NotLoading.Incomplete
            BlockState.UNBLOCKED -> LoadState.NotLoading.Incomplete
        }
    }

//    fun add(
//        loadType: RemoteMediator.LoadType
//    ): Boolean {
//        val existing = pendingRequests.firstOrNull { it.loadType == loadType }
//        if (existing != null) {
//            return false
//        }
//    }
}

/**
 * Collection of pagination [LoadState]s - refresh, prepend, and append.
 */
public data class LoadStates(
    /** [LoadState] corresponding to [LoadType.REFRESH] loads. */
    public val refresh: LoadState,
//    /** [LoadState] corresponding to [LoadType.PREPEND] loads. */
//    public val prepend: LoadState,
//    /** [LoadState] corresponding to [LoadType.APPEND] loads. */
//    public val append: LoadState
) {
    /** @suppress */
    @RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
    public inline fun forEach(op: (RemoteMediator.LoadType, LoadState) -> Unit) {
        op(RemoteMediator.LoadType.REFRESH, refresh)
//        op(RemoteMediator.LoadType.PREPEND, prepend)
//        op(RemoteMediator.LoadType.APPEND, append)
    }

    internal fun modifyState(loadType: RemoteMediator.LoadType, newState: LoadState): LoadStates {
        return when (loadType) {
//            RemoteMediator.LoadType.APPEND -> copy(
//                append = newState
//            )
//            LoadType.PREPEND -> copy(
//                prepend = newState
//            )
            RemoteMediator.LoadType.REFRESH -> copy(
                refresh = newState
            )
        }
    }

    internal fun get(loadType: RemoteMediator.LoadType) = when (loadType) {
        RemoteMediator.LoadType.REFRESH -> refresh
//        LoadType.APPEND -> append
//        LoadType.PREPEND -> prepend
    }

    internal companion object {
        val IDLE = LoadStates(
            refresh = LoadState.NotLoading.Incomplete,
//            prepend = NotLoading.Incomplete,
//            append = NotLoading.Incomplete
        )
    }
}
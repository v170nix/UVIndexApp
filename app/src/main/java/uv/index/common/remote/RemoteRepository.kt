package uv.index.common.remote

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

interface RemoteRepository<R : Request> {

    val state: StateFlow<LoadState<out R>>
    fun requestLoad(request: R): Flow<LoadState<out R>>
//    fun retryFailed()

}
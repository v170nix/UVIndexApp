package uv.index.features.main.common

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import net.arwix.extension.ConflatedJob

open class ConflatedReducer<T>(
    private val scope: CoroutineScope,
    private val compareAndSet: (prevValue: T, nextValue: T) -> Unit
) {
    private val job = ConflatedJob()

    fun launchReduce(state: StateFlow<T>, reduce: suspend (T) -> T) {
        job += scope.launch {
            val prevValue = state.value
            val nextValue = reduce(prevValue)
            compareAndSet(prevValue, nextValue)
        }
    }
}
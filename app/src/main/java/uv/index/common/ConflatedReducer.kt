package uv.index.common

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import net.arwix.coroutines.ConflatedJob

//open class ConflatedReducer<T>(
//    private val scope: CoroutineScope,
//    private val compareAndSet: (prevValue: T, nextValue: T) -> Unit
//) {
//    private val job = ConflatedJob()
//
//    fun launchReduce(state: StateFlow<T>, reduce: suspend (T) -> T) {
//        job += scope.launch {
//            val prevValue = state.value
//            val nextValue = reduce(prevValue)
//            compareAndSet(prevValue, nextValue)
//        }
//    }
//}

open class ConflatedReducer<T>(
    private val scope: CoroutineScope,
    private val block: suspend (T) -> Unit
) {
    private val job = ConflatedJob()

    fun launch(value: T) {
        job += scope.launch {
            block(value)
        }
    }

    fun cancel() {
        job.cancel()
    }
}
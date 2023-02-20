package uv.index.common

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.mapLatest

context(ViewModel)
fun <T1, T2> Flow<T1>.addTrigger(
    flow: Flow<T2>,
    transform: suspend (a: T1, b: T2) -> Unit
): Flow<T1> = combine(flow) { f1, f2 ->
    transform(f1, f2)
    f1
}

context(ViewModel)
fun <T1, T2> Flow<T1>.addTrigger(flow: Flow<T2>): Flow<T1> = combine(flow) { f1, _ -> f1 }

context(ViewModel)
        inline fun <T> Flow<T>.mapLatest(crossinline transform: suspend (value: T) -> T): Flow<T> =
    mapLatest { value ->
        val result = transform(value)
        result
    }

context(ViewModel)
        inline fun <T> Flow<T>.applyData(crossinline block: suspend T.() -> Unit): Flow<T> =
    mapLatest { value ->
        block(value)
        value
    }
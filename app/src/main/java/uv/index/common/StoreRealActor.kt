package uv.index.common

import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach
import java.util.concurrent.atomic.AtomicBoolean

abstract class StoreRealActor<T>(
    scope: CoroutineScope
) {
    private val inChannel = Channel<Any?>(capacity = Channel.RENDEZVOUS)
    private val closeCompleted = CompletableDeferred<Unit>()
    private val didClose = AtomicBoolean(false)

    init {
        inChannel.consumeAsFlow()
            .onEach { msg ->
                if (msg === CLOSE_TOKEN) {
                    doClose()
                } else {
                    handle(msg as T)
                }
            }.onCompletion {
                doClose()
            }
            .launchIn(scope)
    }

    private fun doClose() {
        if (didClose.compareAndSet(false, true)) {
            try {
                onClosed()
            } finally {
                inChannel.close()
                closeCompleted.complete(Unit)
            }
        }
    }

    open fun onClosed() = Unit

    abstract suspend fun handle(msg: T)

    suspend fun send(msg: T) {
        inChannel.send(msg)
    }

    suspend fun close() {
        inChannel.send(CLOSE_TOKEN)
        closeCompleted.await()
    }

    companion object {
        val CLOSE_TOKEN = Any()
    }
}
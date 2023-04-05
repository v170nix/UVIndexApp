package uv.index.common

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import net.arwix.coroutines.ConflatedJob

@Composable
fun LifecycleTimer(
    timeMillis: Long,
    isFirstDelay: Boolean = false,
    block: suspend () -> Unit
) {
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    val scope = rememberCoroutineScope()
    DisposableEffect(lifecycle) {
        val job = ConflatedJob()
        val lifecycleObserver = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_START -> {
                    job += scope.launch {
                        while (isActive) {
                            if (isFirstDelay) delay(timeMillis)
                            block()
                            if (!isFirstDelay) delay(timeMillis)
                        }
                    }
                }
                Lifecycle.Event.ON_STOP -> {
                    job.cancel()
                }
                else -> {
                }
            }
        }
        lifecycle.addObserver(lifecycleObserver)
        onDispose {
            job.cancel()
            lifecycle.removeObserver(lifecycleObserver)
        }
    }
}



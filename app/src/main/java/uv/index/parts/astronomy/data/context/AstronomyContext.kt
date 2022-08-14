package uv.index.parts.astronomy.data.context

import androidx.compose.runtime.Immutable
import java.time.Instant

abstract class AstronomyContext {

    @Immutable
    interface Event<out T : AstronomyContext> : Comparable<Event<AstronomyContext>> {
        val context: T
        val instant: Instant
        val typeId: Int

        override fun compareTo(other: Event<AstronomyContext>): Int = instant.compareTo(other.instant)
    }

    @Immutable
    interface State<out T : AstronomyContext> {
        val context: T
        val fromEvent: Event<T>?
        val toEvent: Event<T>?
    }
}
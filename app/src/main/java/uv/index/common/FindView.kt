package uv.index.common

import android.view.View
import android.view.ViewGroup

fun <T : View> ViewGroup.getViewsByType(tClass: Class<T>): Sequence<T> {
    return sequence<T?> {
        for (i in 0 until childCount) {
            val child = getChildAt(i)
            (child as? ViewGroup)?.let {
                yieldAll(child.getViewsByType(tClass))
            }
            if (tClass.isInstance(child))
                yield(tClass.cast(child))
        }
    }.filterNotNull()
}
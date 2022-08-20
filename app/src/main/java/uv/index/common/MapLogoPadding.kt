package uv.index.common

import android.content.Context
import android.view.View
import android.widget.RelativeLayout
import androidx.compose.ui.unit.DpOffset
import com.google.android.gms.maps.MapView

fun setLogoPadding(context: Context, offset: DpOffset, mapView: MapView) {
    try {
        val logo = mapView.findViewWithTag<View>("GoogleWatermark")
        val params = logo.layoutParams as RelativeLayout.LayoutParams
        val x = context.resources.displayMetrics.density * offset.x.value
        val y = context.resources.displayMetrics.density * offset.y.value
        params.marginStart = x.toInt()
        params.bottomMargin = y.toInt()
        logo.layoutParams = params
    } catch (i: Exception) {}
}
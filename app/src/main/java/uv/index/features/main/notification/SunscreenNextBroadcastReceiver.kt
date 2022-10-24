package uv.index.features.main.notification

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationManagerCompat
import uv.index.features.main.notification.Constants.SUNSCREEN_NOTIFICATION_ID
import uv.index.features.main.notification.sunscreen.SunscreenNotificationWorker

class SunscreenNextBroadcastReceiver : BroadcastReceiver() {
    @SuppressLint("MissingPermission")
    override fun onReceive(context: Context, intent: Intent) {
        SunscreenNotificationWorker.removeWork(context)
        NotificationManagerCompat.from(context).cancel(SUNSCREEN_NOTIFICATION_ID)
    }
}
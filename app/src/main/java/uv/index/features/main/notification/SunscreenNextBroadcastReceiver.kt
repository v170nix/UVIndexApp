package uv.index.features.main.notification

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import uv.index.R
import uv.index.features.main.notification.sunscreen.SunscreenNotificationWorker

class SunscreenNextBroadcastReceiver: BroadcastReceiver() {
    @SuppressLint("MissingPermission")
    override fun onReceive(context: Context, intent: Intent) {
        val r = goAsync()
        SunscreenNotificationWorker.createWork(context)

        val builder = NotificationCompat.Builder(context, Constants.CHANNEL_SUNSCREEN_ID)
            .setSmallIcon(R.drawable.ic_spf)
            .setContentTitle("My notification")
            .setContentText("Context text")
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        with(NotificationManagerCompat.from(context)) {
            notify(48938, builder.build())
        }

        CoroutineScope(SupervisorJob()).launch {
            delay(3000L)
            NotificationManagerCompat.from(context).cancel(48938)
            r.finish()
        }
    }
}
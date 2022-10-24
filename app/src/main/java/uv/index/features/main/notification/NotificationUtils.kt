package uv.index.features.main.notification

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import uv.index.MainActivity
import uv.index.R
import uv.index.features.main.notification.Constants.SUNSCREEN_NOTIFICATION_ID

fun Context.updateNotificationChannels() {
    createNotificationChannel(
        this,
        Constants.CHANNEL_SUNSCREEN_ID,
        resources.getString(R.string.uvindex_notification_sunscreen_reminder_channel_name)
    )
}

@SuppressLint("MissingPermission")
fun Context.showSunscreenNotification() {

    val intent = Intent(this, MainActivity::class.java).apply {
        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
    }

    val pendingIntent = PendingIntent.getActivity(
        this,
        0,
        intent,
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )

    val nextIntent = Intent(this, SunscreenNextBroadcastReceiver::class.java).apply {
        action = "SUNSCREEN_STOP"
    }

    val builder = NotificationCompat.Builder(this, Constants.CHANNEL_SUNSCREEN_ID)
        .setSmallIcon(R.drawable.ic_spf)
        .setContentTitle("My notification")
        .setContentText("Context text")
        .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
        .setContentIntent(pendingIntent)
        .setAutoCancel(true)
        .setStyle(NotificationCompat.BigTextStyle().bigText("big text"))
        .setPriority(NotificationCompat.PRIORITY_MAX)
        .setCategory(NotificationCompat.CATEGORY_ALARM)
        .addAction(
            R.drawable.ic_sunblock,
            "stop notify",
            PendingIntent.getBroadcast(this, 0, nextIntent, PendingIntent.FLAG_IMMUTABLE)
        )

    with(NotificationManagerCompat.from(this)) {
        notify(SUNSCREEN_NOTIFICATION_ID, builder.build())
    }
}

private fun createNotificationChannel(
    context: Context,
    channelId: String,
    name: String
) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val channel = NotificationChannel(channelId, name, importance)
        val notificationManager: NotificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }
}
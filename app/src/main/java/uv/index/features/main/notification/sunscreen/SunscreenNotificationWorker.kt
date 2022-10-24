package uv.index.features.main.notification.sunscreen

import android.content.Context
import androidx.work.*
import uv.index.features.main.notification.Constants
import uv.index.features.main.notification.showSunscreenNotification
import java.util.concurrent.TimeUnit

class SunscreenNotificationWorker(
    private val context: Context,
    params: WorkerParameters
) : Worker(context, params) {

    override fun doWork(): Result {
        context.showSunscreenNotification()
        createWork(context)
        return Result.success()
    }

    companion object {
        fun removeWork(context: Context) {
            WorkManager.getInstance(context)
                .cancelUniqueWork(Constants.SUNSCREEN_NOTIFICATION_WORK_NAME)
        }

        fun createWork(context: Context) {
            WorkManager.getInstance(context).beginUniqueWork(
                Constants.SUNSCREEN_NOTIFICATION_WORK_NAME,
                ExistingWorkPolicy.REPLACE,
                OneTimeWorkRequestBuilder<SunscreenNotificationWorker>()
                    .setInitialDelay(30L, TimeUnit.SECONDS)
                    .build()
            ).enqueue()
        }

    }
}

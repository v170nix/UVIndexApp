package uv.index

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import uv.index.features.main.notification.updateNotificationChannels

@HiltAndroidApp
class AppApplication: Application() {

    override fun onCreate() {
        super.onCreate()
        updateNotificationChannels()
    }
}
package lv.yumm

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber
import lv.yumm.BuildConfig

@HiltAndroidApp
class MainApplication: Application() {
    override fun onCreate() {
        super.onCreate()

        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }

    }
}
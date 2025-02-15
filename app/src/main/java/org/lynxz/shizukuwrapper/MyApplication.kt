package org.lynxz.shizukuwrapper

import android.app.Application
import org.lynxz.shizuku.ShizukuImpl
import org.lynxz.utils.log.LoggerUtil

class MyApplication : Application() {
    private val TAG = "MyApplication"
    override fun onCreate() {
        super.onCreate()
        LoggerUtil.d(TAG, "onCreate")
        ShizukuImpl.init(this.applicationInfo.packageName)
    }
}
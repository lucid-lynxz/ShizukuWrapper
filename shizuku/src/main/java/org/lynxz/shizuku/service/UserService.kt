package org.lynxz.shizuku.service

import android.content.Context
import android.os.RemoteException
import android.text.TextUtils
import androidx.annotation.Keep
import org.lynxz.shizuku.IUserService
import org.lynxz.utils.ShellUtil
import org.lynxz.utils.log.LoggerUtil


class UserService : IUserService.Stub {
    companion object {
        private const val TAG = "UserService"
    }

    /**
     * Constructor is required.
     */
    constructor() {
        LoggerUtil.i(TAG, "constructor")
    }

    /**
     * Constructor with Context. This is only available from Shizuku API v13.
     *
     *
     * This method need to be annotated with [Keep] to prevent ProGuard from removing it.
     *
     * @param context Context created with createPackageContextAsUser
     * @see [code used to create the instance of this class](https://github.com/RikkaApps/Shizuku-API/blob/672f5efd4b33c2441dbf609772627e63417587ac/server-shared/src/main/java/rikka/shizuku/server/UserService.java.L66)
     */
    @Keep
    constructor(context: Context) {
        LoggerUtil.i(TAG, "constructor with Context: context=$context")
    }

    /**
     * Reserved destroy method
     */
    override fun destroy() {
        LoggerUtil.i(TAG, "destroy")
        System.exit(0)
    }

    override fun exit() = destroy()

    @Throws(RemoteException::class)
    override fun exec(cmd: String?): String? {
        val result = ShellUtil.execCommand("$cmd\n", false)
        val msg = if (TextUtils.isEmpty(result.errorMsg)) result.successMsg else result.errorMsg
        LoggerUtil.d(TAG, "exec cmd=$cmd,resultCode=${result.result},errorMsg=$result.errorMsg,successMsg=${result.successMsg},finalMsg=$msg")
        return msg
    }
}

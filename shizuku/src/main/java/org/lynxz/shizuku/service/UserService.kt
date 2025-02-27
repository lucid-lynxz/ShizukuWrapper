package org.lynxz.shizuku.service

import android.content.Context
import android.os.RemoteException
import androidx.annotation.Keep
import org.lynxz.shizuku.IUserService
import org.lynxz.utils.ShellUtil
import org.lynxz.utils.log.LoggerUtil


class UserService : IUserService.Stub {
    companion object {
        private const val TAG = "UserService"
        const val EXEC_CMD_FLAG = "____" // 执行adb命令后,返回结果字符串, 将code, successMsg , errMsg 通过本符号进行拼接
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
    override fun exec(cmd: String?): String {
        val result = ShellUtil.execCommand("$cmd\n", false)
        val code = result.result
        val successMsg = result.successMsg ?: ""
        val errMsg = result.errorMsg ?: ""
        return "$code$EXEC_CMD_FLAG${successMsg.trim()}$EXEC_CMD_FLAG${errMsg.trim()}"
    }
}

package org.lynxz.shizuku

import android.content.ComponentName
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.os.IBinder
import android.util.SparseArray
import androidx.core.util.keyIterator
import org.lynxz.shizuku.service.UserService
import org.lynxz.utils.ShellUtil
import org.lynxz.utils.log.LoggerUtil
import org.lynxz.utils.otherwise
import org.lynxz.utils.yes
import rikka.shizuku.Shizuku
import rikka.shizuku.ShizukuSystemProperties
import rikka.sui.Sui
import java.util.function.Consumer
import kotlin.random.Random

/**
 * shizuku功能封装类
 * 使用:
 * 前置条件: [需要安装shizuku app 并激活启用](https://shizuku.rikka.app/zh-hans/guide/setup/)
 *
 * 1. 导入依赖(已在本模块中完成):
 *     api "dev.rikka.shizuku:api:13.1.5"
 *     api "dev.rikka.shizuku:provider:13.1.5"
 * 2. 在 androidManifest.xml中添加provider(已在本模块中完成)
 *    <provider
 *            android:name="rikka.shizuku.ShizukuProvider"
 *            android:authorities="${applicationId}.shizuku"
 *            android:enabled="true"
 *            android:exported="true"
 *            android:multiprocess="false"
 *            android:permission="android.permission.INTERACT_ACROSS_USERS_FULL" />
 *
 * 3. 在 Application 的 onCreate 中初始化
 *       ShizukuImpl.init(BuildConfig.APPLICATION_ID) // 传入app包名
 *
 * 4. 常用操作:
 * -    4.1 获取系统属性: val serailNo = ShizukuImpl.getSystemProperty("ro.serialno", "")
 * -    4.2 执行adb命令: val execResult =ShizukuImpl.exec("svc wifi enable")
 * -    4.3 执行指定的操作: ShizukuImpl.perform(consumer)
 * -    4.4 绑定自定义服务: val serviceCode = ShizukuImpl.bindUserService(args, conn)
 * -    4.5 解绑自定义服务: ShizukuImpl.unbindUserService(serviceCode)
 * -    4.6 不需要使用时反初始化: ShizukuImpl.uninit()
 * -    4.7 shizuku是否可用: ShizukuImpl.isEnabled()
 * -    4.8 获取imei: val imei = ShizukuImpl.getImei(index)  index可取值: 0,1
 * -    4.9 关闭logcat日志: ShizukuImpl.toggleLog(false)
 * -    4.10 是否已可通过shizuku执行adb命令: val isReady = ShizukuImpl.canExecCmd()
 */
object ShizukuImpl {
    const val TAG = "ShizukuImpl"
    private var enabled = false // 当前是否可用
    private var enableLog = true // 是否打印日志
    private var userService: IUserService? = null
    private var pkgName: String = ""

    // 权限申请结果回调
    private val mCallbacks = SparseArray<Consumer<Boolean>>()

    // 用于随机生成 requestCode
    private val mRandom = Random(System.currentTimeMillis())
    private var innerUserServiceCode = 0

    private data class BindServiceArgs(
        val args: Shizuku.UserServiceArgs,
        val conn: ServiceConnection
    )

    /**
     * 执行命令的结果
     * @param code 错误码, 0 表示正常,其他值表示异常
     * @param msg 执行结果, code=0时,有效
     * @param errMsg 错误信息
     */
    data class CmdResult(val code: Int, val msg: String, val errMsg: String) {
        fun isSuccess() = code == 0
    }

    private val mBindServiceArgs = SparseArray<BindServiceArgs>()
    private val obPermission = object : Shizuku.OnRequestPermissionResultListener {
        override fun onRequestPermissionResult(requestCode: Int, grantResult: Int) {
            val granted = grantResult == PackageManager.PERMISSION_GRANTED
            printLog("onRequestPermissionResult requestCode=$requestCode,granted=$granted,grantResult=$grantResult")
            val callback = mCallbacks.get(requestCode) ?: return
            mCallbacks.remove(requestCode)
            callback.accept(granted)
        }
    }
    private val obBinderReceive = Shizuku.OnBinderReceivedListener {
        enabled = true
        val clsPath = UserService::class.java.name
        printLog("onBinderReceived pkgName=$pkgName,clsPath=$clsPath")
        val userServiceArgs = Shizuku.UserServiceArgs(ComponentName(pkgName, clsPath))
            .daemon(false)
            .processNameSuffix("service")
            .debuggable(true)
            .version(1)
        innerUserServiceCode = bindUserService(userServiceArgs, userServiceConnection)
    }
    private val obBinderDead = Shizuku.OnBinderDeadListener {
        printLog("onBinderDead")
        enabled = false
        unbindUserService(innerUserServiceCode)
    }

    private val userServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, binder: IBinder?) {
            val pingResult = binder?.pingBinder()
            LoggerUtil.d(TAG, "onServiceConnected ComponentName=$name, binder=$binder, pingResult=$pingResult")
            if (pingResult == true) {
                userService = IUserService.Stub.asInterface(binder);
            }
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            LoggerUtil.d(TAG, "onServiceDisconnected ComponentName=$name")
            userService = null
        }
    }

    /**
     * 指定指定的adb命令
     * 执行多条时, 使用 && 符号连接
     * @param isRoot shizuku不可用时, 若要直接运行命令, 是否以root模式执行, 默认false, 请自行确保有root权限
     * @param onlyByShizuku 是否只使用shizuku执行, false-当shizuku不可用时, 直接执行adb命令
     */
    @JvmOverloads
    fun exec(cmd: String, isRoot: Boolean = false, onlyByShizuku: Boolean = true): CmdResult {
        var errMsg = when {
            !enabled -> "shizuku not enabled"
            userService == null -> "userService not connected"
            else -> ""
        }
        return if (errMsg.isBlank()) {
            val result: String = userService!!.exec(cmd)
            printLog("exec($cmd) result=$result,errMsg=$errMsg")
            val arr = result.split(UserService.EXEC_CMD_FLAG)
            val len = arr.size
            val code = if (arr.isNotEmpty()) arr[0].toInt() else 1
            val successMsg = if (len >= 2) arr[1] else ""
            errMsg = if (len >= 3) arr[2] else ""
            CmdResult(code, successMsg, errMsg)
        } else if (onlyByShizuku) {
            CmdResult(1, "", errMsg)
        } else {
            val info = ShellUtil.execCommand(cmd, isRoot) ?: ShellUtil.CommandResult(1, "", "直接执行结果为空,$errMsg")
            CmdResult(info.result, info.successMsg ?: "", info.errorMsg ?: "")
        }
    }

    fun init(pkgName: String): Boolean {
        this.pkgName = pkgName
        val suiResult = Sui.init(pkgName)
        Shizuku.addBinderReceivedListenerSticky(obBinderReceive)
        Shizuku.addBinderDeadListener(obBinderDead)
        Shizuku.addRequestPermissionResultListener(obPermission)
        printLog("init pkgName=$pkgName, suiResult=$suiResult")
        return true
    }

    /**
     * 是否允许输出日志
     */
    fun toggleLog(enable: Boolean) {
        this.enableLog = enable
    }

    fun uninit() {
        mBindServiceArgs.keyIterator().forEach { unbindUserService(it) }
        mBindServiceArgs.clear()
        innerUserServiceCode = 0

        Shizuku.removeBinderReceivedListener(obBinderReceive);
        Shizuku.removeBinderDeadListener(obBinderDead);
        Shizuku.removeRequestPermissionResultListener(obPermission)
    }

    /**
     * 自动发起shizuku权限申请, 申请成功后执行consumer操作
     */
    fun perform(consumer: Consumer<Boolean>) {
        val code = generateRequestCode()
        checkPermission(code).yes {
            consumer.accept(true)
        }.otherwise {
            mCallbacks.put(code, consumer)
        }
    }

    /**
     * 获取系统属性, 比如序列号:ro.serialno
     * imei: "persist.radio.imei"  或者 "ro.ril.oem.imei" (不一定有效, 建议使用 [getImei] 方法)
     * 首次运行会获取不到内容
     */
    fun getSystemProperty(key: String, def: String = ""): String {
        var result = def
        perform { granted ->
            if (granted) {
                result = getSystemPropertyImpl(key, def)
                printLog("getSystemProperty($key,$def) result=$result")
            } else {
                printLog("getSystemProperty($key,$def) fail: Shizuku not enabled")
            }
        }
        return result
    }

    /**
     * 绑定用户自定义的服务
     * @return 用于解绑服务的凭据
     */
    fun bindUserService(args: Shizuku.UserServiceArgs, conn: ServiceConnection): Int {
        val serviceCode = generateRequestCode()

        perform { granted ->
            granted.yes {
                Shizuku.bindUserService(args, conn)
                mBindServiceArgs.put(serviceCode, BindServiceArgs(args, conn))
            }
        }
        return serviceCode
    }

    fun unbindUserService(code: Int) {
        mBindServiceArgs.get(code)?.let {
            perform { granted ->
                granted.yes {
                    Shizuku.unbindUserService(it.args, it.conn, true)
                    mBindServiceArgs.remove(code)
                }
            }
        }
    }

    private fun getSystemPropertyImpl(key: String, def: String = ""): String {
        var result = def
        try {
            result = ShizukuSystemProperties.get(key, def)
        } catch (e: Exception) {
            printLog("getSystemProperty($key,$def) fail: ${e.message}")
        }
        return result
    }

    /**
     * 判断是否拥有shizuku adb shell权限,若无,则申请权限
     */
    private fun checkPermission(code: Int): Boolean {
        if (!enabled) {
            printLog("checkPermission fail: Shizuku not enabled")
            return false
        }

        if (Shizuku.isPreV11()) {
            // Pre-v11 is unsupported
            printLog("checkPermission fail: Pre-v11 is unsupported")
            return false;
        }

        if (Shizuku.checkSelfPermission() == PackageManager.PERMISSION_GRANTED) {
            // Granted
            return true;
        } else if (Shizuku.shouldShowRequestPermissionRationale()) {
            // Users choose "Deny and don't ask again"
            printLog("checkPermission fail: Users choose \"Deny and don't ask again\"")
            return false;
        } else {
            // Request the permission
            Shizuku.requestPermission(code);
            return false;
        }
    }

    val onRequestPermissionResultListener = object : Shizuku.OnRequestPermissionResultListener {
        override fun onRequestPermissionResult(requestCode: Int, grantResult: Int) {
            val granted = grantResult == PackageManager.PERMISSION_GRANTED;
        }
    }

    /**
     * 随机生成权限申请requestCode
     * */
    private fun generateRequestCode(): Int {
        var code = 0
        var tryTimes = 0
        do {
            // 选择 0xFFFF ,可以参考 FragmentActivity 类的 checkForValidRequestCode() 方法
            // 参考: https://blog.csdn.net/barryhappy/article/details/53229238
            code = mRandom.nextInt(0xFFFF)
            tryTimes++
        } while (mCallbacks.indexOfKey(code) >= 0 && tryTimes <= 20)
        return code
    }

    /**
     * shizuku是否可用
     */
    fun isEnabled(): Boolean = enabled

    /**
     * 当前是否可以通过shizuku执行adb命令, 返回true表示userService已连接
     */
    fun canExecCmd(): Boolean = enabled && userService != null

    private val imeiList = mutableListOf<String>()

    /**
     * 获取设备的imei值
     * @param index 第几个imei, 取值范围: 0-1, 0表示第一个imei, 1表示第二个imei
     *
     * 对于小米手机,实测: adb shell service call iphonesubinfo 1 返回一串点, 无意义
     * 使用: adb shell getprop | grep -i IMEI 可得到多个imei信息:
     * [persist.radio.imei]: [869071030058052]
     * [persist.radio.imei1]: [869071030058052]
     * [persist.radio.imei2]: [869071030058060]
     * [ro.ril.miui.imei0]: [869071030058052]
     * [ro.ril.miui.imei1]: [869071030058060]
     * [ro.ril.oem.imei]: [869071030058052]
     * [ro.ril.oem.imei1]: [869071030058052]
     * [ro.ril.oem.imei2]: [869071030058060]
     * 通过: adb shell getprop | grep -i IMEI | awk -F'[][]' '{print $4}' | sort | uniq -d 进行去重,得到:
     * 869071030058052
     * 869071030058060
     * P.S. 若此时 adb shell service call iphonesubinfo 1 | awk -F "'" '{print $2}' | sed '1 d' | tr -d '.' | awk '{print}' ORS= 可以获取到结果, 自测只会输出第一个iemi
     *
     */
    fun getImei(index: Int = 0): String {
        if (imeiList.isEmpty()) {
            var result = exec("getprop | grep -i IMEI | awk -F'[][]' '{print \$4}' | sort | uniq -d") // 一次性获取所有imei并去重
            add2ImeiList(result)

            if (imeiList.isEmpty()) {
                result = exec(" service call iphonesubinfo 1 | awk -F \"'\" '{print \$2}' | sed '1 d' | tr -d '.' | awk '{print}' ORS=") // SIM1
                add2ImeiList(result)

                result = exec(" service call iphonesubinfo 2 | awk -F \"'\" '{print \$2}' | sed '1 d' | tr -d '.' | awk '{print}' ORS=") // SIM2
                add2ImeiList(result)
            }
        }

        if (index >= 0 && index < imeiList.size) {
            return imeiList[index]
        }
        return ""
    }

    private fun add2ImeiList(result: CmdResult) {
        if (result.isSuccess()) {
            result.msg.split("\n").forEach {
                if (it.isNotBlank() && imeiList.indexOf(it) < 0) {
                    imeiList.add(it)
                }
            }
        }
    }

    private fun printLog(msg: String) {
        if (enableLog) {
            LoggerUtil.w(TAG, msg)
        }
    }
}
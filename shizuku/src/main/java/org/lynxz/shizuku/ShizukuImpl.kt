package org.lynxz.shizuku

import android.content.ComponentName
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.os.IBinder
import android.util.SparseArray
import androidx.core.util.keyIterator
import org.lynxz.shizuku.service.UserService
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
 *
 */
object ShizukuImpl {
    const val TAG = "ShizukuImpl"
    private var enable = false
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

    private val mBindServiceArgs = SparseArray<BindServiceArgs>()
    private val obPermission = object : Shizuku.OnRequestPermissionResultListener {
        override fun onRequestPermissionResult(requestCode: Int, grantResult: Int) {
            val granted = grantResult == PackageManager.PERMISSION_GRANTED
            LoggerUtil.w(TAG, "onRequestPermissionResult requestCode=$requestCode,granted=$granted,grantResult=$grantResult")
            val callback = mCallbacks.get(requestCode) ?: return
            mCallbacks.remove(requestCode)
            callback.accept(granted)
        }
    }
    private val obBinderReceive = Shizuku.OnBinderReceivedListener {
        LoggerUtil.w(TAG, "onBinderReceived")
        enable = true

        val clsPath = UserService::class.java.name
        LoggerUtil.w(TAG, "pkgName=$pkgName,clsPath=$clsPath")
        val userServiceArgs = Shizuku.UserServiceArgs(ComponentName(pkgName, clsPath))
            .daemon(false)
            .processNameSuffix("service")
            .debuggable(true)
            .version(1)
        innerUserServiceCode = bindUserService(userServiceArgs, userServiceConnection)
    }
    private val obBinderDead = Shizuku.OnBinderDeadListener {
        LoggerUtil.w(TAG, "onBinderDead")
        enable = false
        unbindUserService(innerUserServiceCode)
    }

    private val userServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, binder: IBinder?) {
            LoggerUtil.d(TAG, "onServiceConnected ComponentName=$name, binder=$binder")
            if (binder?.pingBinder() == true) {
                userService = IUserService.Stub.asInterface(binder);
            }
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            LoggerUtil.d(TAG, "onServiceConnected ComponentName=$name")
            userService = null
        }
    }

    /**
     * 指定指定的adb命令
     */
    fun exec(cmd: String): String {
        val result = userService?.exec(cmd) ?: "fail:userService not connected"
        LoggerUtil.w(TAG, "exec($cmd) result=$result")
        return result
    }

    fun init(pkgName: Any?): Boolean {
        if (pkgName is String) {
            this.pkgName = pkgName
        }
        Sui.init("$pkgName")
        Shizuku.addBinderReceivedListenerSticky(obBinderReceive)
        Shizuku.addBinderDeadListener(obBinderDead)
        Shizuku.addRequestPermissionResultListener(obPermission)
        LoggerUtil.w(TAG, "init pkgName=$pkgName")
        return true
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
     * 首次运行会获取不到内容
     */
    fun getSystemProperty(key: String, def: String = ""): String {
        var result = def
        perform { granted ->
            if (granted) {
                result = getSystemPropertyImpl(key, def)
                LoggerUtil.w(TAG, "getSystemProperty($key,$def) result=$result")
            } else {
                LoggerUtil.w(TAG, "getSystemProperty($key,$def) fail: Shizuku not enabled")
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
            LoggerUtil.w(TAG, "getSystemProperty($key,$def) fail: ${e.message}")
        }
        return result
    }

    /**
     * 判断是否拥有shizuku adb shell权限,若无,则申请权限
     */
    private fun checkPermission(code: Int): Boolean {
        if (!enable) {
            LoggerUtil.w(TAG, "checkPermission fail: Shizuku not enabled")
            return false
        }

        if (Shizuku.isPreV11()) {
            // Pre-v11 is unsupported
            LoggerUtil.w(TAG, "checkPermission fail: Pre-v11 is unsupported")
            return false;
        }

        if (Shizuku.checkSelfPermission() == PackageManager.PERMISSION_GRANTED) {
            // Granted
            return true;
        } else if (Shizuku.shouldShowRequestPermissionRationale()) {
            // Users choose "Deny and don't ask again"
            LoggerUtil.w(TAG, "checkPermission fail: Users choose \"Deny and don't ask again\"")
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
}
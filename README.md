## [Shizuku](https://github.com/RikkaApps/Shizuku) 封装, 简化使用操作
[![](https://jitpack.io/v/lucid-lynxz/shizukuwrapper.svg)](https://jitpack.io/#lucid-lynxz/shizukuwrapper)

1. 导入依赖库:
    * 添加jitpack仓库: `maven { url = uri("https://jitpack.io") }`
    * 添加依赖: `api("com.github.lucid-lynxz:shizukuwrapper:latest")`
2. 在Application中初始化ShizukuManager: `ShizukuImpl.init("app包名")`
3. 调用ShizukuManager的方法进行操作:
   * 获取系统属性: val serailNo = ShizukuImpl.getSystemProperty("ro.serialno", "")
   * 执行adb命令: val execResult =ShizukuImpl.exec("svc wifi enable")
   * 执行指定的操作: ShizukuImpl.perform(consumer)
   * 绑定自定义服务: val serviceCode = ShizukuImpl.bindUserService(args, conn)
   * 解绑自定义服务: ShizukuImpl.unbindUserService(serviceCode)
   * 不需要使用时反初始化: ShizukuImpl.uninit()
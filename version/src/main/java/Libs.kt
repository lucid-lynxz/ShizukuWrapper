object Libs {
    const val byteBuddy = "net.bytebuddy:byte-buddy:1.10.1"

    // 仿微信图片压缩库,生成jpeg图片
    const val luban = "top.zibin:Luban:1.1.8"
    const val gson = "com.google.code.gson:gson:2.8.9"
    const val leakcanary = "com.squareup.leakcanary:leakcanary-android:2.7"
    const val leakcanaryNoOp = "com.squareup.leakcanary:leakcanary-android-no-op:2.7"

    const val kotlinPlugin = "org.jetbrains.kotlin:kotlin-gradle-plugin:1.6.10"
    const val kotlinStdLib = "org.jetbrains.kotlin:kotlin-stdlib:1.6.10"
    const val kotlinStdLib7 = "org.jetbrains.kotlin:kotlin-stdlib-jdk7:1.6.10"
    const val kotlinStdLib8 = "org.jetbrains.kotlin:kotlin-stdlib-jdk8:1.6.10"
    const val kotlinCoreKtx = "androidx.core:core-ktx:1.3.1"
    const val kotlinCoroutinesCore = "org.jetbrains.kotlinx:kotlinx-coroutines-core:1.5.2"
    const val kotlinCoroutinesAndroid = "org.jetbrains.kotlinx:kotlinx-coroutines-android:1.5.2"

    const val retrofit2Version = "2.9.0"
    const val retrofit2 = "com.squareup.retrofit2:retrofit:${retrofit2Version}"
    const val retrofit2Gson = "com.squareup.retrofit2:converter-gson:${retrofit2Version}"
    const val retrofit2Jackson = "com.squareup.retrofit2:converter-jackson:${retrofit2Version}"
    const val stetho = "com.facebook.stetho:stetho:1.6.0"
    const val stethoOkhttp3 = "com.facebook.stetho:stetho-okhttp3:1.6.0"

    // retrofit kotlin coroutines 适配器
    const val retrofit2KtxAdapter =
        "com.jakewharton.retrofit:retrofit2-kotlin-coroutines-adapter:0.9.2"
    const val retrofit2Rxjava = "com.squareup.retrofit2:adapter-rxjava:${retrofit2Version}"
    const val retrofit2Rxjava2 = "com.squareup.retrofit2:adapter-rxjava2:${retrofit2Version}"
    const val retrofit2Logging = "com.squareup.okhttp3:logging-interceptor:4.11.0"

    /**
     * ARouter路由
     * implementation Libs.arouterAPI
     * annotationProcessor Libs.arouterCompiler
     */
    const val arouterAPI = "com.alibaba:arouter-api:1.5.2"
    const val arouterCompiler = "com.alibaba:arouter-compiler:1.5.2"

    const val material = "com.google.android.material:material:1.2.1"
    const val gmsAppindexing = "com.google.android.gms:play-services-appindexing:9.8.0"

    // jetPack相关
    const val lifecycleExt = "android.arch.lifecycle:extensions:1.1.1"
    const val lifecycleCompiler = "android.arch.lifecycle:compiler:1.1.1"
    const val navigationFragment = "android.arch.navigation:navigation-fragment:1.0.0"
    const val navigationUI = "android.arch.navigation:navigation-ui:1.0.0"

    // support库
    const val appcompatV7 = "com.android.support:appcompat-v7:28.0.0"
    const val recyclerviewV7 = "com.android.support:recyclerview-v7:28.0.0"
    const val constraintLayout = "com.android.support.constraint:constraint-layout:1.1.3"
    const val multidex = "com.android.support:multidex:1.0.0"
    const val design = "com.android.support:design:28.0.0"

    // androidX库
    const val constraintLayoutX = "androidx.constraintlayout:constraintlayout:2.1.4"
    const val appcompatX = "androidx.appcompat:appcompat:1.2.0"
    const val annotationX = "androidx.annotation:annotation:1.8.0"
    const val lifecycleViewModelExt = "androidx.lifecycle:lifecycle-viewmodel-ktx:2.8.4"

    // epic库
    const val epic = "com.github.tiann:epic:0.11.2"
    const val epic_aar = "com.github.tiann:epic:0.11.2@aar"

    // 其他三方库
    const val zxingLite = "com.github.jenly1314:zxing-lite:2.1.1"
    const val zxing = "com.google.zxing:core:3.3.0"
    const val blockcanary = "com.github.markzhai:blockcanary-android:1.5.0"
    const val jackson = "com.fasterxml.jackson.core:jackson-databind:2.10.3"

    const val javassist = "org.javassist:javassist:3.28.0-GA"

    const val baseRecyclerViewAdapterHelper =
        "com.github.CymChad:BaseRecyclerViewAdapterHelper:2.9.44"
    const val recyclerviewfastscroller = "xyz.danoz:recyclerviewfastscroller:0.1.3"

    // 广告banner控件,带多种样式(指示器/数字/数字+文字等)
    const val banner = "com.youth.banner:banner:1.4.10"

    // 自动轮播的Viewpager
    const val rollviewpager = "com.jude:rollviewpager:1.4.6"

    const val toasty = "com.github.GrenderG:Toasty:1.3.0"

    // 替代shape,直接在xml中设置相关属性
    // https://github.com/JavaNoober/BackgroundLibrary
    const val backgroundShape = "com.github.JavaNoober.BackgroundLibrary:library:1.7.6"
    const val backgroundShapeX = "com.github.JavaNoober.BackgroundLibrary:libraryx:1.7.6"

    // 滑动删除
    const val swipeDelMenuLayout = "com.github.mcxtzhang:SwipeDelMenuLayout:V1.2.5"

    // 滑动返回
    const val bgaSwipebacklayout = "cn.bingoogolapple:bga-swipebacklayout:1.0.8@aar"

    // 可指定imageView为圆形/圆角/五星等形状
    const val siyamedShapeIamgeview = "com.github.siyamed:android-shape-imageview:0.9.3@aar"

    // 支持图片缩放的 imageView
    const val photoView = "com.github.chrisbanes:PhotoView:2.1.4"

    // 富文本及新html解析器: https://github.com/zzhoujay/RichText
    const val richText = "com.zzhoujay.richtext:richtext:3.0.7"
    const val richTextHtmlParser = "com.zzhoujay:html:1.0.2"

    // viewpager指示器框架,支持多种样式
    const val magicIndicator = "com.github.hackware1993:MagicIndicator:1.5.0"

    // TabLayout 支持多种样式
    const val flycoTabLayout = "com.flyco.tablayout:FlycoTabLayout_Lib:2.1.2@aar"

    // 视频缓存
    const val videoCache = "com.danikula:videocache:2.7.1"

    // 骨架图
    const val shimmerRecyclerView = "com.github.sharish:ShimmerRecyclerView:v1.3"

    // http请求信息查看库 https://github.com/jgilfelt/chuck
    // debugImplementation(libs.readystatesoftwareChuckDebug)
    // releaseImplementation(libs.readystatesoftwareChuckRelease)
    const val readystatesoftwareChuckDebug = "com.readystatesoftware.chuck:library:1.1.0"
    const val readystatesoftwareChuckRelease = "com.readystatesoftware.chuck:library-no-op:1.1.0"


    const val lynxzUtils = "com.github.lucid-lynxz:utils:0.1.25"
    const val lynxzShizuku = "com.github.lucid-lynxz:shizukuwrapper:1.0.5"
    const val mlkitWrapperVersion = "1.0.4"
    const val mlkikBase = "com.github.lucid-lynxz.MLKitWrapper:base:$mlkitWrapperVersion"
    const val mlkikTextdetector =
        "com.github.lucid-lynxz.MLKitWrapper:textdetector:$mlkitWrapperVersion"
    const val mlkikObjectdetector =
        "com.github.lucid-lynxz.MLKitWrapper:objectdetector:$mlkitWrapperVersion"

    const val activityX = "androidx.activity:activity:1.9.0"
    const val activityXKtx = "androidx.activity:activity-ktx:1.9.0"
    const val activityXCompose = "androidx.activity:activity-compose:1.9.0"

    // shizuku
    const val shizukuApi = "dev.rikka.shizuku:api:13.1.5"
    const val shizukuProvider = "dev.rikka.shizuku:provider:13.1.5"
}
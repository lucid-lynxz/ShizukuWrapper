plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
//    `maven-publish`
}

android {
    namespace = "org.lynxz.shizuku"
    compileSdk = 30

    defaultConfig {
        minSdk = 24
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    buildFeatures {
        aidl = true
    }

    sourceSets {
        getByName("main") {
            aidl.srcDirs("src/main/aidl")
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)

    api(libs.shizuku)
    api(libs.shizukuProvider)
    api(libs.lynxzUtils)
}
group = "com.github.lucid-lynxz" // 指定group:com.github.<用户名>
version = "1.0.0" // 版本号，可按需修改

//publishing {
//    publications {
//        create<MavenPublication>("maven") {
//            groupId = project.group.toString()
//            artifactId = "shizuku"
//            version = project.version.toString()
//
//            afterEvaluate {
//                from(components["release"])
//            }
//        }
//    }
//}

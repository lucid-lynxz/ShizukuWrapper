pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }

//        maven { url = uri("https://maven.aliyun.com/repository/public/") }
//        maven { url = uri("https://maven.aliyun.com/repository/google/") }
//        maven { url = uri("https://maven.aliyun.com/repository/jcenter/") }
        maven { url = uri("https://maven.aliyun.com/repository/central/") }
        maven { url = uri("https://jitpack.io") }
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
//        maven { url = uri("https://maven.aliyun.com/repository/public/") }
//        maven { url = uri("https://maven.aliyun.com/repository/google/") }
//        maven { url = uri("https://maven.aliyun.com/repository/jcenter/") }
        maven { url = uri("https://maven.aliyun.com/repository/central/") }
        maven { url = uri("https://jitpack.io") }
        google()
        mavenCentral()
    }
}

rootProject.name = "ShizukuWrapper"
include(":app")
include(":shizuku")

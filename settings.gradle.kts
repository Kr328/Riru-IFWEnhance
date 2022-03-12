@file:Suppress("UnstableApiUsage")

rootProject.name = "IFW Enhance"

include(":module", ":hideapi")

pluginManagement {
    repositories {
        maven(url = "https://maven.kr328.app/releases")
        mavenCentral()
        google()
        mavenLocal()
    }
}

dependencyResolutionManagement {
    repositories {
        maven(url = "https://maven.kr328.app/releases")
        mavenCentral()
        google()
        mavenLocal()
    }
    versionCatalogs {
        create("deps") {
            val agp = "7.1.2"
            val zloader = "2.6"
            val magic = "1.4"

            plugin("android-application", "com.android.application").version(agp)
            plugin("android-library", "com.android.library").version(agp)
            plugin("zygote-loader", "com.github.kr328.gradle.zygote").version(zloader)
            library("magic-library", "com.github.kr328.magic", "library").version(magic)
        }
    }
}
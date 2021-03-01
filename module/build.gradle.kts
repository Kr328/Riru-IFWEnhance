plugins {
    id("com.android.application")
    id("hideapi-redefine")
    id("riru")
}

riru {
    id = "riru_ifw_enhance"
    name = "Riru - IFW Enhance"
    minApi = 9
    minApiName = "v22.0"
    description = "A module of Riru. Enhance intent firewall."
    author = "Kr328"
    dexName = "boot-ifw-enhance.dex"
}

android {
    compileSdkVersion(30)

    ndkVersion = "21.3.6528147"

    defaultConfig {
        applicationId = "com.github.kr328.ifw"

        minSdkVersion(26)
        targetSdkVersion(30)

        versionCode = 7
        versionName = "v7"

        multiDexEnabled = false

        externalNativeBuild {
            cmake {
                arguments(
                        "-DRIRU_NAME:STRING=${riru.name}",
                        "-DRIRU_MODULE_ID:STRING=${riru.riruId}",
                        "-DRIRU_MODULE_VERSION_CODE:INTEGER=$versionCode",
                        "-DRIRU_MODULE_VERSION_NAME:STRING=$versionName"
                )
            }
        }
    }

    buildFeatures {
        buildConfig = false
        prefab = true
    }

    buildTypes {
        named("release") {
            isMinifyEnabled = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    externalNativeBuild {
        cmake {
            path = file("src/main/cpp/CMakeLists.txt")
        }
    }
}

dependencies {
    compileOnly(project(":hideapi"))

    implementation("rikka.ndk:riru:10")
}
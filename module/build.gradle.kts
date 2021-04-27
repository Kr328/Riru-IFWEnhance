plugins {
    id("com.android.application")
    id("hideapi-redefine")
    id("riru")
}

riru {
    id = "riru_ifw_enhance"
    name = "Riru - IFW Enhance"
    minApi = 25
    minApiName = "25.0.0"
    description = "A module of Riru. Enhance intent firewall."
    author = "Kr328"
}

android {
    compileSdkVersion(30)

    ndkVersion = "23.0.7123448"

    defaultConfig {
        applicationId = "com.github.kr328.ifw"

        minSdk = 26
        targetSdk = 30

        versionCode = 8
        versionName = "v8"

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

    implementation("dev.rikka.ndk:riru:25.0.0")
}

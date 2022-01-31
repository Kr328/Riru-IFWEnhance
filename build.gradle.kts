import com.android.build.gradle.BaseExtension

buildscript {
    repositories {
        mavenCentral()
        google()
        maven(url = "https://maven.kr328.app/releases")
    }
    dependencies {
        classpath(deps.build.android)
        classpath(deps.build.zloader)
    }
}

subprojects {
    repositories {
        mavenCentral()
        google()
        maven(url = "https://maven.kr328.app/releases")
    }

    val isApp = name == "module"

    apply(plugin = if (isApp) "com.android.application" else "com.android.library")

    extensions.configure<BaseExtension> {
        val minSdkVersion = 26
        val targetSdkVersion = 31
        val buildVersionName = "v13"
        val buildVersionCode = 13

        compileSdkVersion(targetSdkVersion)

        defaultConfig {
            if (isApp) {
                applicationId = "com.github.kr328.ifw"
            }

            minSdk = minSdkVersion
            targetSdk = targetSdkVersion

            versionName = buildVersionName
            versionCode = buildVersionCode

            if (!isApp) {
                consumerProguardFiles("consumer-rules.pro")
            }
        }

        buildTypes {
            named("release") {
                isMinifyEnabled = isApp
                isShrinkResources = isApp
                proguardFiles(
                    getDefaultProguardFile("proguard-android-optimize.txt"),
                    "proguard-rules.pro"
                )
            }
        }
    }
}

task("clean", type = Delete::class) {
    delete(rootProject.buildDir)
}

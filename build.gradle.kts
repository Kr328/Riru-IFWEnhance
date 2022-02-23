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
        compileSdkVersion(31)

        defaultConfig {
            if (isApp) {
                applicationId = "com.github.kr328.ifw"
            }

            minSdk = 26
            targetSdk = 31

            versionName = "v17"
            versionCode = 17

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

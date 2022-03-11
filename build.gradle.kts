import com.android.build.gradle.BaseExtension

plugins {
    alias(deps.plugins.android.library) apply false
    alias(deps.plugins.android.application) apply false
}

subprojects {
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

            versionName = "v18"
            versionCode = 18

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

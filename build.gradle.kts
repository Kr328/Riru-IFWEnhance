import com.android.build.gradle.BaseExtension

plugins {
    alias(deps.plugins.android.library) apply false
    alias(deps.plugins.android.application) apply false
}

subprojects {
    val configureBaseExtension: Project.(Boolean) -> Unit = { isApp ->
        extensions.configure<BaseExtension> {
            compileSdkVersion(31)

            defaultConfig {
                if (isApp) {
                    applicationId = "com.github.kr328.ifw"
                }

                minSdk = 26
                targetSdk = 31

                versionName = "v19"
                versionCode = 19

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

    plugins.withId("com.android.application") {
        configureBaseExtension(true)
    }
    plugins.withId("com.android.library") {
        configureBaseExtension(false)
    }
}

task("clean", type = Delete::class) {
    delete(rootProject.buildDir)
}

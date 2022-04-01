import com.android.build.gradle.AppExtension
import com.android.build.gradle.BaseExtension

plugins {
    val agp = "7.1.2"
    val zygote = "2.6"

    id("com.android.library") version agp apply false
    id("com.android.application") version agp apply false
    id("com.github.kr328.gradle.zygote") version zygote apply false
}

subprojects {
    plugins.withId("com.android.base") {
        extensions.configure<BaseExtension> {
            val isApp = this is AppExtension

            println("Configure $name: isApp = $isApp")

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
}

task("clean", type = Delete::class) {
    delete(rootProject.buildDir)
}

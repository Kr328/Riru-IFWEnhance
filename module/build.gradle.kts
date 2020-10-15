import org.gradle.kotlin.dsl.support.zipTo
import java.security.MessageDigest

plugins {
    id("com.android.application")
}

val riruId = "ifw_enhance"
val riruApi = 9
val riruName = "v22.0"

val moduleId = "riru_ifw_enhance"
val moduleName = "Riru - IFW Enhance"
val moduleDescription = "A module of Riru. Enhance intent firewall."
val moduleAuthor = "Kr328"
val moduleFiles = listOf(
        "system/framework/$riruId.dex"
)

val binaryTypes = setOf("dex", "so")

android {
    compileSdkVersion(30)
    buildToolsVersion("30.0.2")

    ndkVersion = "21.3.6528147"

    defaultConfig {
        applicationId = "com.github.kr328.ifw"

        minSdkVersion(26)
        targetSdkVersion(30)

        versionCode = 1
        versionName = "v1"

        externalNativeBuild {
            cmake {
                arguments(
                        "-DRIRU_API:INTEGER=$riruApi",
                        "-DRIRU_NAME:STRING=$riruName",
                        "-DRIRU_MODULE_ID:STRING=$riruId",
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

    applicationVariants.all {
        val task = assembleProvider?.get() ?: error("assemble task not found")
        val prefix = moduleId.replace('_', '-')
        val zipFile = buildDir.resolve("outputs/$prefix-$name.zip")
        val zipContent = buildDir.resolve("intermediates/magisk/$name")
        val apkFile = this.outputs.first()?.outputFile ?: error("apk not found")

        task.doLast {
            zipContent.deleteRecursively()

            zipContent.mkdirs()

            val apkTree = zipTree(apkFile)

            copy {
                into(zipContent)

                from(file("src/main/raw")) {
                    exclude("riru.sh", "module.prop", "riru/module.prop.new", "dist-gitattributes")
                }

                from(file("src/main/raw/dist-gitattributes")) {
                    rename { ".gitattributes" }
                }

                from(file("src/main/raw/riru.sh")) {
                    filter { line ->
                        line.replace("%%%RIRU_MODULE_ID%%%", riruId)
                                .replace("%%%RIRU_MIN_API_VERSION%%%", "$riruApi")
                                .replace("%%%RIRU_MIN_VERSION_NAME%%%", riruName)
                    }
                }

                from(file("src/main/raw/module.prop")) {
                    filter { line ->
                        line.replace("%%%MAGISK_ID%%%", moduleId)
                                .replace("%%%MAIGKS_NAME%%%", moduleName)
                                .replace("%%%MAGISK_VERSION_NAME%%%", versionName!!)
                                .replace("%%%MAGISK_VERSION_CODE%%%", "$versionCode")
                                .replace("%%%MAGISK_AUTHOR%%%", moduleAuthor)
                                .replace("%%%MAGISK_DESCRIPTION%%%", moduleDescription)
                    }
                }

                from(file("src/main/raw/riru/module.prop.new")) {
                    into("riru/")

                    filter { line ->
                        line.replace("%%%RIRU_NAME%%%", moduleName.removePrefix("Riru - "))
                                .replace("%%%RIRU_VERSION_NAME%%%", versionName!!)
                                .replace("%%%RIRU_VERSION_CODE%%%", "$versionCode")
                                .replace("%%%RIRU_AUTHOR%%%", moduleAuthor)
                                .replace("%%%RIRU_DESCRIPTION%%%", moduleDescription)
                                .replace("%%%RIRU_API%%%", "$riruApi")
                                .replace("%%%RURU_MIN_SDK_VERSION%%%", packageApplicationProvider!!.get().minSdkVersion.get().toString())
                    }
                }

                from(apkTree) {
                    include("lib/**")
                    eachFile {
                        path = path
                                .replace("lib/x86_64", "system_x86/lib64")
                                .replace("lib/x86", "system_x86/lib")
                                .replace("lib/arm64-v8a", "system/lib64")
                                .replace("lib/armeabi-v7a", "system/lib")
                    }
                }

                from(apkTree) {
                    include("classes.dex")
                    eachFile {
                        path = "system/framework/$riruId.dex"
                    }
                }
            }

            zipContent.resolve("extras.files")
                    .writeText(moduleFiles.joinToString("\n") + "\n")

            fileTree(zipContent)
                    .filter { it.isFile }
                    .filterNot { it.extension in binaryTypes }
                    .forEach { it.writeText(it.readText().replace("\r\n", "\n")) }

            fileTree(zipContent)
                    .matching { exclude("customize.sh", "verify.sh", "META-INF", "README.md") }
                    .filter { it.isFile }
                    .forEach {
                        val sha256sum = MessageDigest.getInstance("SHA-256").digest(it.readBytes())
                        val sha256text = sha256sum.joinToString(separator = "") { b ->
                            String.format("%02x", b.toInt() and 0xFF)
                        }

                        File(it.absolutePath + ".sha256sum").writeText(sha256text)
                    }

            zipTo(zipFile, zipContent)
        }
    }
}

dependencies {
    compileOnly(project(":hideapi"))

    implementation("rikka.ndk:riru:9.1")
}
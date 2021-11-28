import com.github.kr328.zloader.gradle.ZygoteLoader.*
import com.github.kr328.zloader.gradle.tasks.PackageMagiskTask
import com.github.kr328.zloader.gradle.util.toCapitalized

plugins {
    id("com.android.application")
    id("zygote-loader")
}

dependencies {
    compileOnly(project(":hideapi"))

    implementation(deps.magic.library)
}

zygote {
    val moduleId = "ifw_enhance"
    val moduleName = "IFW Enhance"
    val moduleDescription = "Enhance Intent Firewall."
    val moduleAuthor = "Kr328"
    val moduleEntrypoint = "com.github.kr328.ifw.Injector"

    packages(PACKAGE_SYSTEM_SERVER)

    riru {
        id = "riru_$moduleId"
        name = "Riru - $moduleName"
        author = moduleAuthor
        description = moduleDescription
        entrypoint = moduleEntrypoint
    }
}

androidComponents {
    onVariants {
        val name = it.name
        val flavorName = it.flavorName!!

        afterEvaluate {
            (tasks[PackageMagiskTask.taskName(name)] as Zip).apply {
                archiveBaseName.set("$flavorName-ifw-enhance-${android.defaultConfig.versionName}")
            }
        }
    }
}
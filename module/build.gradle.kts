import com.github.kr328.zloader.gradle.ZygoteLoader.PACKAGE_SYSTEM_SERVER

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
        name = "$moduleName (Riru)"
        author = moduleAuthor
        description = moduleDescription
        entrypoint = moduleEntrypoint
        archiveName = "${id.replace('_', '-')}-${android.defaultConfig.versionName}"
        updateJson = "https://github.com/Kr328/Riru-IFWEnhance/releases/latest/download/${id.replace('_', '-')}.json"
    }

    zygisk {
        id = "zygisk_$moduleId"
        name = "$moduleName (Zygisk)"
        author = moduleAuthor
        description = moduleDescription
        entrypoint = moduleEntrypoint
        archiveName = "${id.replace('_', '-')}-${android.defaultConfig.versionName}"
        updateJson = "https://github.com/Kr328/Riru-IFWEnhance/releases/latest/download/${id.replace('_', '-')}.json"
    }
}

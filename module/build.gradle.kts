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
    val moduleId = "ifw-enhance"
    val moduleName = "IFW Enhance"
    val moduleDescription = "Enhance Intent Firewall."
    val moduleAuthor = "Kr328"
    val moduleEntrypoint = "com.github.kr328.ifw.Injector"
    val versionName = android.defaultConfig.versionName

    packages(PACKAGE_SYSTEM_SERVER)

    riru {
        id = "riru-$moduleId".replace('-', '_')
        name = "Riru - $moduleName"
        archiveName = "riru-$moduleId-$versionName"
        updateJson = "https://github.com/Kr328/Riru-IFWEnhance/releases/latest/download/riru-$moduleId.json"
    }

    zygisk {
        id = "zygisk-$moduleId".replace('-', '_')
        name = "Zygisk - $moduleName"
        archiveName = "zygisk-$moduleId-$versionName"
        updateJson = "https://github.com/Kr328/Riru-IFWEnhance/releases/latest/download/zygisk-$moduleId.json"
    }

    all {
        author = moduleAuthor
        description = moduleDescription
        entrypoint = moduleEntrypoint
    }
}

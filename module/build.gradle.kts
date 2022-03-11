import com.github.kr328.gradle.zygote.ZygoteLoader

plugins {
    alias(deps.plugins.android.application)
    alias(deps.plugins.zygote.loader)
}

dependencies {
    compileOnly(project(":hideapi"))

    implementation(deps.magic.library)
}

zygote {
    val moduleId = "ifw-enhance"
    val moduleName = "IFW Enhance"
    val moduleDescription = "Allows Intent Firewall to filter results of queryIntent(Activities/Services) APIs."
    val moduleAuthor = "Kr328"
    val moduleEntrypoint = "com.github.kr328.ifw.Main"
    val versionName = android.defaultConfig.versionName

    packages(ZygoteLoader.PACKAGE_SYSTEM_SERVER)

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

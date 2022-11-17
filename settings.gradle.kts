@file:Suppress("UnstableApiUsage")

enableFeaturePreview("VERSION_CATALOGS")

rootProject.name = "IFW Enhance"

include(":module", ":hideapi")

dependencyResolutionManagement {
    versionCatalogs {
        create("deps") {
            val agp = "7.3.1"
            val zloader = "2.6"
            val magic = "1.4"

            library("build-android", "com.android.tools.build", "gradle").version(agp)
            library("build-zloader", "com.github.kr328.gradle.zygote", "gradle-plugin").version(zloader)
            library("magic-library", "com.github.kr328.magic", "library").version(magic)
        }
    }
}

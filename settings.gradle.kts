@file:Suppress("UnstableApiUsage")

enableFeaturePreview("VERSION_CATALOGS")

rootProject.name = "IFW Enhance"

include(":module", ":hideapi")

dependencyResolutionManagement {
    versionCatalogs {
        create("deps") {
            val agp = "7.1.1"
            val zloader = "2.3"
            val magic = "1.4"

            alias("build-android").to("com.android.tools.build:gradle:$agp")
            alias("build-zloader").to("com.github.kr328.zloader:gradle-plugin:$zloader")
            alias("magic-library").to("com.github.kr328.magic:library:$magic")
        }
    }
}
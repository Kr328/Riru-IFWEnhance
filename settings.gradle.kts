rootProject.name = "IFW Enhance"

include(":module", ":hideapi")

pluginManagement {
    repositories {
        maven(url = "https://maven.kr328.app/releases")
        mavenCentral()
        google()
        mavenLocal()
    }
}

dependencyResolutionManagement {
    repositories {
        maven(url = "https://maven.kr328.app/releases")
        mavenCentral()
        google()
        mavenLocal()
    }
    versionCatalogs {
        create("libs") {
            val magic = "1.9"

            library("magic-library", "com.github.kr328.magic", "library").version(magic)
        }
    }
}

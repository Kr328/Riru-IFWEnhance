allprojects {
    repositories {
        google()
        jcenter()
    }
}

task("clean", type = Delete::class) {
    delete(rootProject.buildDir)
}

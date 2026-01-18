plugins {
    id("java")
    id("org.jetbrains.intellij.platform.module")
}

repositories {
    intellijPlatform {
        defaultRepositories()
    }
}

// Helper to access 'jetbrains' extra property in a type-safe way
val jetbrains: Map<String, String> by rootProject.extra

dependencies {
    intellijPlatform {
        pycharmCommunity(jetbrains["version"]!!)
        bundledPlugin("PythonCore")
        instrumentationTools()
    }

    implementation(project(":envfile-platform"))
    implementation("org.jetbrains:annotations:23.0.0")

    compileOnly("org.projectlombok:lombok:1.18.42")
    annotationProcessor("org.projectlombok:lombok:1.18.42")
}

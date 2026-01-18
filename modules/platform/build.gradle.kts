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
        intellijIdeaCommunity(jetbrains["version"]!!)
        instrumentationTools()
    }

    implementation(project(":envfile-core"))
    implementation("org.jetbrains:annotations:23.0.0")
    implementation("org.apache.commons:commons-text:1.15.0")

    compileOnly("org.projectlombok:lombok:1.18.42")
    annotationProcessor("org.projectlombok:lombok:1.18.42")
}

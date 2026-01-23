import java.io.ByteArrayOutputStream

plugins {
    id("java")
    id("idea")
    id("org.jetbrains.intellij.platform") version "2.2.1"
}

group = "com.cmmoran"

// Native versioning: Get version from Git tags or fallback to 'unspecified'
fun getProjectVersion(): String {
    return try {
        val stdout = ByteArrayOutputStream()
        exec {
            commandLine("git", "describe", "--tags", "--always", "--dirty")
            standardOutput = stdout
        }
        stdout.toString().trim().replaceFirst("^v".toRegex(), "")
    } catch (_: Exception) {
        "0.0.1-SNAPSHOT"
    }
}

version = getProjectVersion()

// Helper to access 'jetbrains' extra property in a type-safe way
val jetbrains: Map<String, String> by extra

allprojects {
    apply(plugin = "java")

    // Use toolchains to ensure the correct JDK is used for compilation
    extensions.configure<JavaPluginExtension> {
        toolchain {
            languageVersion.set(JavaLanguageVersion.of(17))
        }
    }

    repositories {
        mavenCentral()
    }

    project.version = rootProject.version

    extra["jetbrains"] = mapOf(
        "version" to "2025.1.1",
        "pycharm" to "PythonCore:242.21829.142",
        "rubymine" to "org.jetbrains.plugins.ruby:242.21829.142",
        "goland" to "org.jetbrains.plugins.go:242.21829.142",
        "scala" to "org.intellij.scala:2024.2.25"
    )
}

repositories {
    intellijPlatform {
        defaultRepositories()
    }
}

intellijPlatform {
    projectName.set("envfile")
    buildSearchableOptions.set(false)

    pluginConfiguration {
        name.set("envfile")

        ideaVersion {
            sinceBuild.set("253")
            untilBuild.set("999")
        }
    }

    pluginVerification {
        ides {
            ide("IC", jetbrains["version"]!!)
        }
    }
}

dependencies {
    intellijPlatform {
        intellijIdeaCommunity(jetbrains["version"]!!)
        bundledPlugin("com.intellij.java")
    }

    implementation(project(":envfile-products-idea"))
    implementation(project(":envfile-products-pycharm"))
    implementation(project(":envfile-products-rubymine"))
    implementation(project(":envfile-products-goland"))
}

tasks.wrapper {
    gradleVersion = "8.5"
}

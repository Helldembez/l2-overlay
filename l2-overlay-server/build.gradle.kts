import groovy.lang.Closure
import io.github.fvarrui.javapackager.gradle.PackageTask
import io.github.fvarrui.javapackager.model.WindowsConfig
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "2.1.20"
    id("com.github.johnrengelman.shadow") version "8.1.1"
    application
}

buildscript {
    repositories { mavenCentral() }
    dependencies { classpath("io.github.fvarrui:javapackager:1.7.6") }
}

apply(plugin = "io.github.fvarrui.javapackager.plugin")

dependencies {
    val ktor = "2.3.7"
    implementation("io.ktor:ktor-server-core:$ktor")
    implementation("io.ktor:ktor-server-websockets:$ktor")
    implementation("io.ktor:ktor-server-netty:$ktor")
    implementation("io.ktor:ktor-serialization-kotlinx-json:$ktor")
    implementation("io.ktor:ktor-server-content-negotiation:$ktor")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.2")
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "21"
}

application {
    mainClass.set("com.helldembez.l2overlay.MainKt")
}

tasks.jar {
    manifest {
        attributes["Main-Class"] = "com.helldembez.l2overlay.MainKt"
        attributes["Implementation-Version"] = project.version
    }
    from({
        configurations.runtimeClasspath.get().map { if (it.isDirectory) it else zipTree(it) }
    })
    duplicatesStrategy = DuplicatesStrategy.WARN
}

tasks.shadowJar {
    archiveBaseName.set("l2-overlay")
    archiveClassifier.set("")
    archiveVersion.set(project.version.toString())
    manifest {
        attributes["Main-Class"] = "com.helldembez.l2overlay.MainKt"
        attributes["Implementation-Version"] = project.version
    }
}

// 🔹 Define packaging task
tasks.register<PackageTask>("packageApp").configure {
    dependsOn("shadowJar")

    mainClass = "com.helldembez.l2overlay.MainKt"
    isBundleJre = true
    isGenerateInstaller = true
    isAdministratorRequired = false
    platform = io.github.fvarrui.javapackager.model.Platform.windows
    val file = file("icon.ico")

    val myClosure = closureOf<WindowsConfig> {
        icoFile = file
    } as Closure<WindowsConfig>
    winConfig(myClosure)
}
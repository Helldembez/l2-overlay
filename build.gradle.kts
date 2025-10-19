import groovy.lang.Closure
import io.github.fvarrui.javapackager.gradle.PackageTask
import io.github.fvarrui.javapackager.model.WindowsConfig
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "2.1.20"
    id("com.github.johnrengelman.shadow") version "8.1.1"
    application
}

apply(plugin = "io.github.fvarrui.javapackager.plugin")

buildscript {
    repositories {
        mavenCentral()
    }
    dependencies {
        classpath("io.github.fvarrui:javapackager:1.7.6")
    }
}

group = "com.helldembez.l2overlay"
version = "0.4"

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib"))
    val javaFxVersion = "21.0.2"
    implementation("org.openjfx:javafx-base:$javaFxVersion:win")
    implementation("org.openjfx:javafx-controls:$javaFxVersion:win")
    implementation("org.openjfx:javafx-graphics:$javaFxVersion:win")

    implementation("com.github.kwhat:jnativehook:2.2.2")
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
plugins {
    kotlin("jvm") version "2.1.20" apply false
    id("com.github.johnrengelman.shadow") version "8.1.1" apply false
}

allprojects {
    group = "com.helldembez.l2overlay"
    version = "0.2"

    repositories {
        mavenCentral()
    }
}

subprojects {
    // Java/Kotlin toolchain alignment
    plugins.withId("org.jetbrains.kotlin.jvm") {
        tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
            kotlinOptions.jvmTarget = "21"
        }
    }
}
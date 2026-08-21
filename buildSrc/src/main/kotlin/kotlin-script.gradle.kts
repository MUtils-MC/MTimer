import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    kotlin("jvm")
    kotlin("plugin.serialization")
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.jetbrains.kotlinx", "kotlinx-serialization-json", "1.11.+")
    implementation("org.jetbrains.kotlinx", "kotlinx-coroutines-core", "1.11.+")
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(25))
    }
}

tasks {
    compileJava {
        options.encoding = "UTF-8"
        options.release.set(25)
    }
    compileKotlin {
        compilerOptions.jvmTarget.set(JvmTarget.JVM_25)
    }
}
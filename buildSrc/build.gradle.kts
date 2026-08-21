plugins {
    `kotlin-dsl`
    kotlin("plugin.serialization") version "2.4.10"
}

repositories {
    mavenCentral()
    gradlePluginPortal()
    maven("https://repo.papermc.io/repository/maven-public/")
}

dependencies {
    fun pluginDep(id: String, version: String) = "${id}:${id}.gradle.plugin:${version}"
    val kotlinVersion = "2.4.10"

    compileOnly(kotlin("gradle-plugin", kotlinVersion))
    runtimeOnly(kotlin("gradle-plugin", kotlinVersion))
    compileOnly(pluginDep("org.jetbrains.kotlin.plugin.serialization", kotlinVersion))
    runtimeOnly(pluginDep("org.jetbrains.kotlin.plugin.serialization", kotlinVersion))
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.+")

    // Paper implementation
    implementation(pluginDep("io.papermc.paperweight.userdev", "2.0.0-beta.22"))
    implementation(pluginDep("xyz.jpenilla.run-paper", "3.1.0"))
    implementation(pluginDep("de.eldoria.plugin-yml.paper", "0.9.+"))

    // Project configuration
    implementation(pluginDep("com.modrinth.minotaur", "2.+"))
    implementation(pluginDep("io.github.dexman545.outlet", "1.6.+"))
}

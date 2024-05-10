import gradle.kotlin.dsl.accessors._285dcef16d8875fee0ec91e18e07daf9.implementation
import net.minecrell.pluginyml.bukkit.BukkitPluginDescription

plugins {
    kotlin("jvm")
    id("io.papermc.paperweight.userdev")
    id("xyz.jpenilla.run-paper")
    id("net.minecrell.plugin-yml.bukkit")
    id("com.modrinth.minotaur")
    id("com.github.johnrengelman.shadow")
}

description = properties["description"] as String

val gameVersion by properties
val foliaSupport = properties["foliaSupport"] as String == "true"
val projectName = properties["name"] as String

repositories {
    mavenCentral()
    mavenLocal()
}

dependencies {
    paperweight.paperDevBundle("1.20.6-R0.1-SNAPSHOT")

    // Kotlin libraries
    library(kotlin("stdlib"))
    library("org.jetbrains.kotlinx:kotlinx-serialization-json:1.+")
    library("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.+")

    // Utility libraries (optional)
    val useBrigadier = properties["useBrigadier"] as String == "true"
    if (useBrigadier) {
        implementation("dev.jorel:commandapi-bukkit-shade:9.4.0")
        library("dev.jorel:commandapi-bukkit-kotlin:9.4.0")
    }

    implementation("de.miraculixx:kpaper-light:1.2.1")
    implementation("de.miraculixx:mc-commons:1.0.1")
    implementation("de.miraculixx:timer-api:1.1.3")
    implementation("de.miraculixx:mbridge:1.0.0")
}

tasks {
    assemble {
        dependsOn(shadowJar)
        dependsOn(reobfJar)
    }
}

bukkit {
    main = "$group.${projectName.lowercase()}.${projectName}"
    apiVersion = "1.16"
    foliaSupported = foliaSupport

    // Optionals
    load = BukkitPluginDescription.PluginLoadOrder.STARTUP
    depend = listOf()
    softDepend = listOf()
}

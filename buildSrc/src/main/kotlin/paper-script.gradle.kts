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
val projectName = properties["projectName"] as String

repositories {
    mavenCentral()
    mavenLocal()
    maven("https://s01.oss.sonatype.org/content/repositories/snapshots/")
}

dependencies {
    paperweight.paperDevBundle("1.21.1-R0.1-SNAPSHOT")

    // Kotlin libraries
    library(kotlin("stdlib"))
    library("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.+")
    library("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.8.+")

    // Utility libraries (optional)
    val useBrigadier = properties["useBrigadier"] as String == "true"
    if (useBrigadier) {
        implementation("dev.jorel:commandapi-bukkit-shade:10.1.1")
        library("dev.jorel:commandapi-bukkit-kotlin:10.1.1")
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
    println(projectName)
    name = projectName
    main = "$group.${projectName.lowercase()}.${projectName}"
    apiVersion = "1.16"
    foliaSupported = foliaSupport

    // Optionals
    load = BukkitPluginDescription.PluginLoadOrder.STARTUP
    depend = listOf()
    softDepend = listOf()
    libraries = listOf(
        "io.ktor:ktor-client-core-jvm:2.3.7",
        "io.ktor:ktor-client-cio-jvm:2.3.7"
    )
}

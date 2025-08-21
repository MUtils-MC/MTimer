import net.minecrell.pluginyml.bukkit.BukkitPluginDescription
import net.minecrell.pluginyml.paper.PaperPluginDescription

plugins {
    kotlin("jvm")
    id("io.papermc.paperweight.userdev")
    id("xyz.jpenilla.run-paper")
    id("de.eldoria.plugin-yml.paper")
    id("com.modrinth.minotaur")
}

description = properties["description"] as String

val gameVersion by properties
val foliaSupport = properties["foliaSupport"] as String == "true"
val projectName = properties["projectName"] as String

paperweight.reobfArtifactConfiguration = io.papermc.paperweight.userdev.ReobfArtifactConfiguration.MOJANG_PRODUCTION

repositories {
    mavenCentral()
    maven("https://s01.oss.sonatype.org/content/repositories/snapshots/")
}

dependencies {
    paperweight.paperDevBundle("1.21.8-R0.1-SNAPSHOT")

    // Kotlin libraries
    library(kotlin("stdlib"))
    library("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.+")
    library("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.8.+")

    // Utility libraries (optional)
    val useBrigadier = properties["useBrigadier"] as String == "true"
    if (useBrigadier) {
        implementation(library("dev.jorel:commandapi-bukkit-shade-mojang-mapped:10.1.2")!!)
        implementation(library("dev.jorel:commandapi-bukkit-kotlin:10.1.2")!!)
    }

    paperLibrary("de.miraculixx:kpaper-light:1.2.2")
    paperLibrary("de.miraculixx:mc-commons:1.0.1")
    paperLibrary("de.miraculixx:timer-api:1.1.3")
    //implementation("de.miraculixx:mbridge:1.0.0")
}

paper {
    main = "$group.mtimer.MTimer"
    bootstrapper = "$group.mtimer.TimerBootstrapper"
    loader = "$group.mtimer.TimerLoader"
    generateLibrariesJson = true

    name = "Timer"
    website = "https://mutils.net"

    foliaSupported = false
    apiVersion = "1.20"
    load = BukkitPluginDescription.PluginLoadOrder.STARTUP

    serverDependencies {
        register("mchallenge") {
            required = false
            joinClasspath = true
            load = PaperPluginDescription.RelativeLoadOrder.BEFORE
        }
    }
}

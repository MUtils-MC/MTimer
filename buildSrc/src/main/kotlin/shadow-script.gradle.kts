
plugins {
    kotlin("jvm")
    id("com.github.johnrengelman.shadow")
}

tasks {
    shadowJar {
        dependencies {
            include {
                it.moduleGroup == group || it.moduleGroup == "dev.jorel"
            }
        }
        relocate("dev.jorel.commandapi", "de.miraculixx.timer.commandapi")
    }
}

plugins {
    kotlin("jvm")
    id("com.github.johnrengelman.shadow")
}

tasks {
    shadowJar {
        dependencies {
            include {
                (it.moduleGroup == "de.miraculixx" && it.moduleName != "challenge-api")
            }
        }
    }
    assemble {
        dependsOn(shadowJar)
    }
}
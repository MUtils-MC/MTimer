plugins {
    `kotlin-script`
    `fabric-script`
    `adventure-script`
}

dependencies {
    implementation(include(project(":global"))!!)
}

loom {
    runs {
        named("server") {
            ideConfigGenerated(true)
        }
        named("client") {
            ideConfigGenerated(true)
        }
    }
}

kotlin {
    sourceSets {
        all {
            languageSettings.optIn("net.silkmc.silk.core.annotations.ExperimentalSilkApi")
        }
    }
}

sourceSets {
    main {
        resources.srcDirs("$rootDir/timer/data/")
    }
}

group = "de.miraculixx.timer"

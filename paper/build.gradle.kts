plugins {
    `kotlin-script`
    `paper-script`
    `shadow-script`
}

repositories {
    mavenLocal()
}

dependencies {
    implementation(project(":global"))

    implementation("de.miraculixx:challenge-api:1.5.0")
}

group = "de.miraculixx.timer"

sourceSets {
    main {
        resources.srcDirs("$rootDir/timer/data/")
    }
}

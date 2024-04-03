plugins {
    kotlin("jvm")
}

repositories {
    mavenLocal()
}

dependencies {
    implementation("net.kyori:adventure-api:4.+")
    implementation("net.kyori:adventure-text-minimessage:4.+")
    implementation("net.kyori:adventure-text-serializer-plain:4.+")
    implementation("net.kyori:adventure-text-serializer-gson:4.+")

    implementation("de.miraculixx:mc-commons:1.0.1")
    implementation("de.miraculixx:timer-api:1.2.0")
}
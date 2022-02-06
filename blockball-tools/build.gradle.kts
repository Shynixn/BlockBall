plugins {
    id("java")
    id("application")
    id("org.jetbrains.kotlin.jvm")
    id("com.github.johnrengelman.shadow") version "7.0.0"
}

tasks.withType<com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar> {
    archiveName = "Deploy.jar"
}

application {
    mainClassName = "com.github.shynixn.blockballtools.MainKt"
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.jsoup:jsoup:1.11.3")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("com.github.shynixn:discordwebhook-ktl:1.2")
    implementation("org.apache.cxf:cxf-rt-rs-client:3.3.0")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.9.8")
}

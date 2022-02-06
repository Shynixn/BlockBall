import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.gradle.api.tasks.testing.logging.TestLogEvent

plugins {
    id("java")
    id("org.jetbrains.kotlin.jvm") version "1.6.10"
}

repositories {
    mavenCentral()
    mavenLocal()
}

tasks.register("printVersion") {
    println(subprojects.first().version)
}

subprojects {
    apply(plugin = "java")
    apply(plugin = "org.jetbrains.kotlin.jvm")

    group = "com.github.shynixn.blockball"
    version = "6.28.0"

    java {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
        kotlinOptions.jvmTarget = "1.8"
    }

    tasks.test {
        useJUnitPlatform()
        testLogging.showStandardStreams = true
        failFast = true

        testLogging {
            events(TestLogEvent.FAILED, TestLogEvent.PASSED, TestLogEvent.SKIPPED, TestLogEvent.STARTED)
            displayGranularity = 0
            showExceptions = true
            showCauses = true
            showStackTraces = true
            exceptionFormat = TestExceptionFormat.FULL
        }
    }

    repositories {
        mavenCentral()
        mavenLocal()
        maven("https://jitpack.io")
        maven("'https://repo.spongepowered.org/maven")
    }

    dependencies {
        implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:1.6.10")

        testImplementation("org.junit.jupiter:junit-jupiter-api:5.7.0")
        testImplementation("org.mockito:mockito-core:3.5.13")
        testImplementation("org.junit.jupiter:junit-jupiter-engine:5.6.2")
    }
}

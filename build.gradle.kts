import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import java.util.*
import java.io.*

plugins {
    id("org.jetbrains.kotlin.jvm") version ("1.9.25")
    id("com.github.johnrengelman.shadow") version ("7.0.0")
}

group = "com.github.shynixn"
version = "7.8.0"

repositories {
    mavenLocal()
    mavenCentral()
    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
    maven("https://repo.extendedclip.com/content/repositories/placeholderapi")
    maven("https://repo.opencollab.dev/main/")
    maven(System.getenv("SHYNIXN_MCUTILS_REPOSITORY")) // All MCUTILS libraries are private and not OpenSource.
}

tasks.register("printVersion") {
    println(version)
}

dependencies {
    // Compile Only
    compileOnly("me.clip:placeholderapi:2.11.6")
    compileOnly("org.spigotmc:spigot-api:1.18.2-R0.1-SNAPSHOT")

    // Library dependencies with legacy compatibility, we can use more up-to-date version in the plugin.yml
    implementation("com.github.shynixn.org.bstats:bstats-bukkit:1.7")
    implementation("com.github.shynixn.mccoroutine:mccoroutine-bukkit-api:2.20.0")
    implementation("com.github.shynixn.mccoroutine:mccoroutine-bukkit-core:2.20.0")
    implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-yaml:2.3.0")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.2.3")
    implementation("com.google.inject:guice:5.0.1")
    implementation("com.google.code.gson:gson:2.8.6")
    implementation("com.zaxxer:HikariCP:4.0.3")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.4.2")

    // Custom dependencies
    implementation("com.github.shynixn.mcutils:common:2024.39")
    implementation("com.github.shynixn.mcutils:packet:2024.51")
    implementation("com.github.shynixn.mcutils:database:2024.8")
    implementation("com.github.shynixn.mcutils:sign:2024.3")
    implementation("com.github.shynixn.mcutils:guice:2024.2")
}

tasks.test {
    useJUnitPlatform()
    testLogging.showStandardStreams = true
    failFast = true

    testLogging {
        events(
            org.gradle.api.tasks.testing.logging.TestLogEvent.FAILED,
            org.gradle.api.tasks.testing.logging.TestLogEvent.PASSED,
            org.gradle.api.tasks.testing.logging.TestLogEvent.SKIPPED,
            org.gradle.api.tasks.testing.logging.TestLogEvent.STARTED
        )
        displayGranularity = 0
        showExceptions = true
        showCauses = true
        showStackTraces = true
        exceptionFormat = org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
    }
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

/**
 * Include all but exclude debugging classes.
 */
tasks.withType<com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar> {
    dependsOn("jar")
    archiveName = "${baseName}-${version}-shadowjar.${extension}"
    exclude("DebugProbesKt.bin")
    exclude("module-info.class")
}

/**
 * Create all plugin jar files.
 */
tasks.register("pluginJars") {
    dependsOn("pluginJarLatest")
    dependsOn("pluginJarPremium")
    dependsOn("pluginJarLegacy")
}

/**
 * Relocate Plugin Jar.
 */
tasks.register("relocatePluginJar", ShadowJar::class.java) {
    dependsOn("shadowJar")
    from(zipTree(File("./build/libs/" + (tasks.getByName("shadowJar") as Jar).archiveName)))
    archiveName = "${baseName}-${version}-relocate.${extension}"
    relocate("org.bstats", "com.github.shynixn.blockball.lib.org.bstats")
    relocate("com.fasterxml", "com.github.shynixn.blockball.lib.com.fasterxml")
    relocate("com.github.shynixn.mcutils", "com.github.shynixn.blockball.lib.com.github.shynixn.mcutils")
}

/**
 * Create latest plugin jar file.
 */
tasks.register("pluginJarLatest", ShadowJar::class.java) {
    dependsOn("relocatePluginJar")
    from(zipTree(File("./build/libs/" + (tasks.getByName("relocatePluginJar") as Jar).archiveName)))
    archiveName = "${baseName}-${version}-latest.${extension}"
    destinationDir = File("C:\\temp\\plugins")

    exclude("com/github/shynixn/blockball/lib/com/github/shynixn/mcutils/packet/nms/v1_8_R3/**")
    exclude("com/github/shynixn/blockball/lib/com/github/shynixn/mcutils/packet/nms/v1_9_R2/**")
    exclude("com/github/shynixn/blockball/lib/com/github/shynixn/mcutils/packet/nms/v1_17_R1/**")
    exclude("com/github/shynixn/blockball/lib/com/github/shynixn/mcutils/packet/nms/v1_18_R1/**")
    exclude("com/github/shynixn/blockball/lib/com/github/shynixn/mcutils/packet/nms/v1_18_R2/**")
    exclude("com/github/shynixn/blockball/lib/com/github/shynixn/mcutils/packet/nms/v1_19_R1/**")
    exclude("com/github/shynixn/blockball/lib/com/github/shynixn/mcutils/packet/nms/v1_19_R2/**")
    exclude("com/github/shynixn/blockball/lib/com/github/shynixn/mcutils/packet/nms/v1_19_R3/**")
    exclude("com/github/shynixn/blockball/lib/com/github/shynixn/mcutils/packet/nms/v1_20_R1/**")
    exclude("com/github/shynixn/blockball/lib/com/github/shynixn/mcutils/packet/nms/v1_20_R2/**")
    exclude("com/github/shynixn/blockball/lib/com/github/shynixn/mcutils/packet/nms/v1_20_R3/**")
    exclude("com/github/shynixn/blockball/lib/com/github/shynixn/mcutils/packet/nms/v1_20_R4/**")
    exclude("com/github/shynixn/blockball/lib/com/github/shynixn/mcutils/packet/nms/v1_21_R1/**")
    exclude("com/github/shynixn/blockball/lib/com/github/shynixn/mcutils/packet/nms/v1_21_R2/**")
    exclude("com/github/shynixn/mcutils/**")
    exclude("com/github/shynixn/mccoroutine/**")
    exclude("org/**")
    exclude("kotlin/**")
    exclude("kotlinx/**")
    exclude("javax/**")
    exclude("com/google/**")
    exclude("com/fasterxml/**")
    exclude("com/zaxxer/**")
    exclude("plugin-legacy.yml")
}

/**
 * Create premium plugin jar file.
 */
tasks.register("pluginJarPremium", com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar::class.java) {
    dependsOn("relocatePluginJar")
    from(zipTree(File("./build/libs/" + (tasks.getByName("relocatePluginJar") as Jar).archiveFileName.get())))
    archiveFileName.set("${archiveBaseName.get()}-${archiveVersion.get()}-premium.${archiveExtension.get()}")
    // destinationDir = File("C:\\temp\\plugins")

    exclude("com/github/shynixn/mcutils/**")
    exclude("com/github/shynixn/mccoroutine/**")
    exclude("org/**")
    exclude("kotlin/**")
    exclude("kotlinx/**")
    exclude("javax/**")
    exclude("com/google/**")
    exclude("com/fasterxml/**")
    exclude("com/zaxxer/**")
    exclude("plugin-legacy.yml")
}

/**
 * Relocate legacy plugin jar file.
 */
tasks.register("relocateLegacyPluginJar", ShadowJar::class.java) {
    dependsOn("shadowJar")
    from(zipTree(File("./build/libs/" + (tasks.getByName("shadowJar") as Jar).archiveName)))
    archiveName = "${baseName}-${version}-legacy-relocate.${extension}"
    relocate("kotlin", "com.github.shynixn.blockball.lib.kotlin")
    relocate("kotlinx", "com.github.shynixn.blockball.lib.kotlinx")
    relocate("org.intellij", "com.github.shynixn.blockball.lib.org.intelli")
    relocate("org.jetbrains", "com.github.shynixn.blockball.lib.org.jetbrains")
    relocate("org.bstats", "com.github.shynixn.blockball.lib.org.bstats")
    relocate("javax.inject", "com.github.shynixn.blockball.lib.javax.inject")
    relocate("javax.annotation", "com.github.shynixn.blockball.lib.javax.annotation")
    relocate("org.checkerframework", "com.github.shynixn.blockball.lib.org.checkerframework")
    relocate("org.aopalliance", "com.github.shynixn.blockball.lib.org.aopalliance")
    relocate("org.slf4j", "com.github.shynixn.blockball.lib.org.slf4j")
    relocate("com.github.shynixn.mccoroutine", "com.github.shynixn.blockball.lib.com.github.shynixn.mccoroutine")
    relocate("com.google", "com.github.shynixn.blockball.lib.com.google")
    relocate("com.zaxxer", "com.github.shynixn.blockball.lib.com.zaxxer")
    relocate("org.apache", "com.github.shynixn.blockball.lib.org.apache")
    relocate("com.fasterxml", "com.github.shynixn.blockball.lib.com.fasterxml")
    relocate("com.github.shynixn.mcutils", "com.github.shynixn.blockball.lib.com.github.shynixn.mcutils")
    relocate("com.github.shynixn.mccoroutine", "com.github.shynixn.blockball.lib.com.github.shynixn.mccoroutine")

    exclude("plugin.yml")
    rename("plugin-legacy.yml", "plugin.yml")
}

/**
 * Create legacy plugin jar file.
 */
tasks.register("pluginJarLegacy", ShadowJar::class.java) {
    dependsOn("relocateLegacyPluginJar")
    from(zipTree(File("./build/libs/" + (tasks.getByName("relocateLegacyPluginJar") as Jar).archiveName)))
    archiveName = "${baseName}-${version}-legacy.${extension}"
    // destinationDir = File("C:\\temp\\plugins")
    exclude("com/github/shynixn/mcutils/**")
    exclude("com/github/shynixn/mccoroutine/**")
    exclude("org/**")
    exclude("kotlin/**")
    exclude("kotlinx/**")
    exclude("javax/**")
    exclude("com/google/**")
    exclude("com/fasterxml/**")
    exclude("com/zaxxer/**")
    exclude("plugin-legacy.yml")
}

tasks.register("languageFile") {
    val kotlinSrcFolder = project.sourceSets.toList()[0].allJava.srcDirs.first { e -> e.endsWith("java") }
    val contractFile = kotlinSrcFolder.resolve("com/github/shynixn/blockball/contract/Language.kt")
    val resourceFile = kotlinSrcFolder.parentFile.resolve("resources").resolve("lang").resolve("en_us.yml")
    val lines = resourceFile.readLines()

    val contractContents = ArrayList<String>()
    contractContents.add("package com.github.shynixn.blockball.contract")
    contractContents.add("")
    contractContents.add("import com.github.shynixn.mcutils.common.language.LanguageItem")
    contractContents.add("import com.github.shynixn.mcutils.common.language.LanguageProvider")
    contractContents.add("")
    contractContents.add("interface Language : LanguageProvider {")
    for (key in lines) {
        if (key.toCharArray()[0].isLetter()) {
            contractContents.add("  var ${key} LanguageItem")
            contractContents.add("")
        }
    }
    contractContents.removeLast()
    contractContents.add("}")

    contractFile.printWriter().use { out ->
        for (line in contractContents) {
            out.println(line)
        }
    }

    val implFile = kotlinSrcFolder.resolve("com/github/shynixn/blockball/BlockBallLanguageImpl.kt")
    val implContents = ArrayList<String>()
    implContents.add("package com.github.shynixn.blockball")
    implContents.add("")
    implContents.add("import com.github.shynixn.mcutils.common.language.LanguageItem")
    implContents.add("import com.github.shynixn.mcutils.common.language.LanguageProviderImpl")
    implContents.add("import com.github.shynixn.blockball.contract.Language")
    implContents.add("")
    implContents.add("class BlockBallLanguageImpl : Language, LanguageProviderImpl() {")
    implContents.add(" override val names: List<String>\n" +
            "  get() = listOf(\"en_us\", \"es_es\", \"zh_cn\")")

    for (i in 0 until lines.size) {
        val key = lines[i]

        if (key.toCharArray()[0].isLetter()) {
            var text = ""
            println(">" + lines[i])

            println("_")
            var j = i
            while (true){
                if(lines[j].contains("text:")){
                    text = lines[j]
                    break
                }
                j++
            }

            implContents.add(" override var ${key.replace(":","")} = LanguageItem(${text.replace("  text: ","")})")
            implContents.add("")
        }
    }
    implContents.removeLast()
    implContents.add("}")

    implFile.printWriter().use { out ->
        for (line in implContents) {
            out.println(line)
        }
    }
}

import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import java.util.*
import java.io.*

plugins {
    id("org.jetbrains.kotlin.jvm") version ("1.9.25")
    id("com.gradleup.shadow") version ("8.3.6")
}

group = "com.github.shynixn"
version = "7.39.0"

repositories {
    mavenCentral()
    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
    maven(System.getenv("SHYNIXN_MCUTILS_REPOSITORY_2026")) // All MCUTILS libraries are private and not OpenSource.
    maven("https://maven.shynixn.com/releases")
}

dependencies {
    // Compile Only
    compileOnly("org.spigotmc:spigot-api:1.18.2-R0.1-SNAPSHOT")

    // Library dependencies with legacy compatibility, we can use more up-to-date version in the plugin.yml
    implementation("com.github.shynixn.mccoroutine:mccoroutine-folia-api:2.22.0")
    implementation("com.github.shynixn.mccoroutine:mccoroutine-folia-core:2.22.0")
    implementation("com.github.shynixn:fasterxml:1.2.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.4.2")
    implementation("com.zaxxer:HikariCP:4.0.3")

    // Custom dependencies
    implementation("com.github.shynixn.shycommandsigns:shycommandsigns:1.5.0")
    implementation("com.github.shynixn.shybossbar:shybossbar:1.7.0")
    implementation("com.github.shynixn.shyscoreboard:shyscoreboard:1.13.0")
    implementation("com.github.shynixn.shyparticles:shyparticles:1.3.0")
    implementation("com.github.shynixn.shyguild:shyguild:1.1.0")
    implementation("com.github.shynixn.mcutils:common:2026.4")
    implementation("com.github.shynixn.mcutils:packet:2026.12")
    implementation("com.github.shynixn.mcutils:worldguard:2026.1")
    implementation("com.github.shynixn.mcutils:database:2026.3")
    implementation("com.github.shynixn.mcutils:http:2026.3")

    // Test
    testImplementation(kotlin("test"))
    testImplementation("org.spigotmc:spigot-api:1.18.2-R0.1-SNAPSHOT")
    testImplementation("org.mockito:mockito-core:2.23.0")
}

tasks.withType<com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar> {
    dependsOn("jar")
    archiveFileName.set("${archiveBaseName.get()}-${archiveVersion.get()}-shadowjar.${archiveExtension.get()}")
    exclude("DebugProbesKt.bin")
    exclude("module-info.class")
}

/**
 * Create all plugin jar files.
 */
tasks.register("pluginJars") {
    dependsOn("pluginJar-1.8.8-1.16.5-premium")
    dependsOn("pluginJar-1.17.0-1.21.11-premium")
    dependsOn("pluginJar-1.17.0-1.21.11-premium-folia")
    dependsOn("pluginJar-26.1.0-latest-premium")
    dependsOn("pluginJar-26.1.0-latest-premium-folia")
    dependsOn("pluginJar-26.1.0-latest-free")
}

registerPluginJar("1.8.8-1.16.5-premium", "plugin-1.8.8-1.16.5.yml", isLegacy = true)
registerPluginJar("1.17.0-1.21.11-premium", "plugin-1.17.0-1.21.11.yml")
registerPluginJar("1.17.0-1.21.11-premium-folia", "plugin-1.17.0-1.21.11-folia.yml", isFolia = true)
registerPluginJar("26.1.0-latest-premium", "plugin-26.1.0-latest.yml")
registerPluginJar("26.1.0-latest-premium-folia", "plugin-26.1.0-latest-folia.yml", isFolia = true)
registerPluginJar("26.1.0-latest-free", "plugin-26.1.0-latest.yml", excludeOldNms = true)

fun registerPluginJar(
    taskName: String,
    pluginYml: String,
    isFolia: Boolean = false,
    excludeOldNms: Boolean = false,
    isLegacy: Boolean = false,
    debug: Boolean = false
) {
    val relocateTaskName = "relocatePluginJar-$taskName"
    val jarTaskName = "pluginJar-$taskName"

    // ── Step 1: relocation ────────────────────────────────────────────────────
    tasks.register(relocateTaskName, com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar::class.java) {
        dependsOn("shadowJar")
        from(zipTree(File("./build/libs/" + (tasks.getByName("shadowJar") as Jar).archiveFileName.get())))
        archiveFileName.set("${archiveBaseName.get()}-${archiveVersion.get()}-${taskName}-relocate.${archiveExtension.get()}")
        relocate("com.github.shynixn.mcutils", "com.github.shynixn.blockball.lib.com.github.shynixn.mcutils")
        relocate("com.github.shynixn.shyscoreboard", "com.github.shynixn.blockball.lib.com.github.shynixn.shyscoreboard")
        relocate("com.github.shynixn.shyparticles", "com.github.shynixn.blockball.lib.com.github.shynixn.shyparticles")
        relocate("com.github.shynixn.shyguild", "com.github.shynixn.blockball.lib.com.github.shynixn.shyguild")
        relocate("com.github.shynixn.shycommandsigns", "com.github.shynixn.blockball.lib.com.github.shynixn.shycommandsigns")
        relocate("com.github.shynixn.shybossbar", "com.github.shynixn.blockball.lib.com.github.shynixn.shybossbar")
        if (isLegacy) {
            relocate(
                "com.github.shynixn.mccoroutine",
                "com.github.shynixn.blockball.lib.com.github.shynixn.mccoroutine"
            )
            relocate(
                "com.github.shynixn.fasterxml",
                "com.github.shynixn.blockball.lib.com.github.shynixn.fasterxml"
            )
            relocate("kotlin", "com.github.shynixn.blockball.lib.kotlin")
            relocate("kotlinx", "com.github.shynixn.blockball.lib.kotlinx")
            relocate("org.intellij", "com.github.shynixn.blockball.lib.org.intellij")
            relocate("org.jetbrains", "com.github.shynixn.blockball.lib.org.jetbrains")
            relocate("javax", "com.github.shynixn.blockball.lib.javax")
            relocate("com.zaxxer", "com.github.shynixn.blockball.lib.com.zaxxer")
        }
    }

    // ── Step 2: excludes + plugin.yml selection ───────────────────────────────
    tasks.register(jarTaskName, com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar::class.java) {
        dependsOn(relocateTaskName)
        from(zipTree(File("./build/libs/" + (tasks.getByName(relocateTaskName) as Jar).archiveFileName.get())))
        archiveFileName.set("${archiveBaseName.get()}-${archiveVersion.get()}-${taskName}.${archiveExtension.get()}")
        if (debug) {
            destinationDirectory.set(File(System.getenv("HOME"), "git/mc/plugins"))
        }
        // Keep only the correct plugin yml
        rename(pluginYml, "plugin.yml")
        val allPluginYmls = listOf(
            "plugin-1.8.8-1.16.5.yml",
            "plugin-1.17.0-1.21.11.yml",
            "plugin-1.17.0-1.21.11-folia.yml",
            "plugin-26.1.0-latest.yml",
            "plugin-26.1.0-latest-folia.yml"
        )
        for (yml in allPluginYmls) {
            if (yml != pluginYml) exclude(yml)
        }

        if (!isFolia) {
            exclude("com/github/shynixn/blockball/lib/com/github/shynixn/mcutils/common/FoliaMarker.class")
        }

        exclude("com/github/shynixn/mcutils/**")
        exclude("com/github/shynixn/mccoroutine/**")
        exclude("com/github/shynixn/fasterxml/**")
        exclude("kotlin/**")
        exclude("org/**")
        exclude("kotlinx/**")
        exclude("javax/**")
        exclude("com/zaxxer/**")
        exclude("com/github/shynixn/shyscoreboard/**")
        exclude("com/github/shynixn/shyparticles/**")
        exclude("com/github/shynixn/shyguild/**")
        exclude("com/github/shynixn/shycommandsigns/**")
        exclude("com/github/shynixn/shybossbar/**")

        if (excludeOldNms) {
            val oldNmsPaths = listOf(
                "com/github/shynixn/blockball/lib/com/github/shynixn/mcutils/packet/nms/v1_8_R3/**",
                "com/github/shynixn/blockball/lib/com/github/shynixn/mcutils/packet/nms/v1_9_R2/**",
                "com/github/shynixn/blockball/lib/com/github/shynixn/mcutils/packet/nms/v1_17_R1/**",
                "com/github/shynixn/blockball/lib/com/github/shynixn/mcutils/packet/nms/v1_18_R1/**",
                "com/github/shynixn/blockball/lib/com/github/shynixn/mcutils/packet/nms/v1_18_R2/**",
                "com/github/shynixn/blockball/lib/com/github/shynixn/mcutils/packet/nms/v1_19_R1/**",
                "com/github/shynixn/blockball/lib/com/github/shynixn/mcutils/packet/nms/v1_19_R2/**",
                "com/github/shynixn/blockball/lib/com/github/shynixn/mcutils/packet/nms/v1_19_R3/**",
                "com/github/shynixn/blockball/lib/com/github/shynixn/mcutils/packet/nms/v1_20_R1/**",
                "com/github/shynixn/blockball/lib/com/github/shynixn/mcutils/packet/nms/v1_20_R2/**",
                "com/github/shynixn/blockball/lib/com/github/shynixn/mcutils/packet/nms/v1_20_R3/**",
                "com/github/shynixn/blockball/lib/com/github/shynixn/mcutils/packet/nms/v1_20_R4/**",
                "com/github/shynixn/blockball/lib/com/github/shynixn/mcutils/packet/nms/v1_21_R1/**",
                "com/github/shynixn/blockball/lib/com/github/shynixn/mcutils/packet/nms/v1_21_R2/**",
                "com/github/shynixn/blockball/lib/com/github/shynixn/mcutils/packet/nms/v1_21_R3/**",
                "com/github/shynixn/blockball/lib/com/github/shynixn/mcutils/packet/nms/v1_21_R4/**",
                "com/github/shynixn/blockball/lib/com/github/shynixn/mcutils/packet/nms/v1_21_R5/**",
                "com/github/shynixn/blockball/lib/com/github/shynixn/mcutils/packet/nms/v1_21_R6/**",
                "com/github/shynixn/blockball/lib/com/github/shynixn/mcutils/packet/nms/v1_21_R7/**"
            )
            for (path in oldNmsPaths) exclude(path)
        }
    }
}

tasks.register("languageFile") {
    val kotlinSrcFolder = project.sourceSets.toList()[0].allJava.srcDirs.first { e -> e.endsWith("java") }
    val contractFile = kotlinSrcFolder.resolve("com/github/shynixn/blockball/contract/BlockBallLanguage.kt")
    val resourceFile = kotlinSrcFolder.parentFile.resolve("resources").resolve("lang").resolve("en_us.yml")
    val lines = resourceFile.readLines()

    val contractContents = ArrayList<String>()
    val ignoredKeys = listOf(
        "shyBossBar",
        "shyScoreboard",
        "shyCommandSigns",
        "shyParticles",
        "shyGuild"
    )
    contractContents.add("package com.github.shynixn.blockball.contract")
    contractContents.add("")
    contractContents.add("import com.github.shynixn.shyscoreboard.contract.ShyScoreboardLanguage")
    contractContents.add("import com.github.shynixn.mcutils.common.language.LanguageItem")
    contractContents.add("import com.github.shynixn.shybossbar.contract.ShyBossBarLanguage")
    contractContents.add("import com.github.shynixn.mcutils.common.language.LanguageProvider")
    contractContents.add("import com.github.shynixn.shycommandsigns.contract.ShyCommandSignsLanguage")
    contractContents.add("import com.github.shynixn.shyparticles.contract.ShyParticlesLanguage")
    contractContents.add("import com.github.shynixn.shyguild.contract.ShyGuildLanguage")
    contractContents.add("")
    contractContents.add("interface BlockBallLanguage : LanguageProvider, ShyScoreboardLanguage, ShyBossBarLanguage, ShyCommandSignsLanguage, ShyParticlesLanguage, ShyGuildLanguage {")
    for (key in lines) {
        if (key.toCharArray()[0].isLetter()) {
            if (ignoredKeys.firstOrNull { e -> key.startsWith(e) } != null) {
                continue
            }

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
    implContents.add("import com.github.shynixn.blockball.contract.BlockBallLanguage")
    implContents.add("")
    implContents.add("class BlockBallLanguageImpl : BlockBallLanguage {")
    implContents.add(
        " override val names: List<String>\n" +
                "  get() = listOf(\"en_us\")"
    )

    for (i in lines.indices) {
        val key = lines[i]

        if (key.toCharArray()[0].isLetter()) {
            var text: String

            var j = i
            while (true) {
                if (lines[j].contains("text:")) {
                    text = lines[j]
                    break
                }
                j++
            }

            implContents.add(" override var ${key.replace(":", "")} = LanguageItem(${text.replace("  text: ", "")})")
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

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}
tasks.register("printVersion") {
    println(version)
}

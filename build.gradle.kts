import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("org.jetbrains.kotlin.jvm") version ("1.9.25")
    id("com.gradleup.shadow") version ("8.3.6")
}

group = "com.github.shynixn"
version = "7.34.2"

repositories {
    mavenLocal()
    mavenCentral()
    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
    maven("https://maven.shynixn.com/releases")
    maven(System.getenv("SHYNIXN_MCUTILS_REPOSITORY_2025")) // All MCUTILS libraries are private and not OpenSource.
}

dependencies {
    // Compile Only
    compileOnly("org.spigotmc:spigot-api:1.18.2-R0.1-SNAPSHOT")

    // Library dependencies with legacy compatibility, we can use more up-to-date version in the plugin.yml
    implementation("com.github.shynixn.mccoroutine:mccoroutine-folia-api:2.22.0")
    implementation("com.github.shynixn.mccoroutine:mccoroutine-folia-core:2.22.0")
    implementation("com.github.shynixn:fasterxml:1.2.0")
    implementation("com.zaxxer:HikariCP:4.0.3")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.4.2")

    // Custom dependencies
    implementation("com.github.shynixn.shycommandsigns:shycommandsigns:1.4.0")
    implementation("com.github.shynixn.shybossbar:shybossbar:1.6.0")
    implementation("com.github.shynixn.shyscoreboard:shyscoreboard:1.12.0")
    implementation("com.github.shynixn.shyparticles:shyparticles:1.2.0")
    implementation("com.github.shynixn.mcutils:common:2025.51")
    implementation("com.github.shynixn.mcutils:packet:2025.60")
    implementation("com.github.shynixn.mcutils:database:2025.10")
    implementation("com.github.shynixn.mcutils:worldguard:2025.5")
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
tasks.withType<ShadowJar> {
    dependsOn("jar")
    archiveFileName.set("${archiveBaseName.get()}-${archiveVersion.get()}-shadowjar.${archiveExtension.get()}")
    exclude("DebugProbesKt.bin")
    exclude("module-info.class")
}

/**
 * Create all plugin jar files.
 */
tasks.register("pluginJars") {
    dependsOn("pluginJarLatest")
    dependsOn("pluginJarPremium")
    dependsOn("pluginJarPremiumFolia")
    dependsOn("pluginJarLegacy")
}

/**
 * Relocate Plugin Jar.
 */
tasks.register("relocatePluginJar", ShadowJar::class.java) {
    dependsOn("shadowJar")
    from(zipTree(File("./build/libs/" + (tasks.getByName("shadowJar") as Jar).archiveFileName.get())))
    archiveFileName.set("${archiveBaseName.get()}-${archiveVersion.get()}-relocate.${archiveExtension.get()}")
    relocate("com.github.shynixn.mcutils", "com.github.shynixn.blockball.lib.com.github.shynixn.mcutils")
    relocate("com.github.shynixn.shyscoreboard", "com.github.shynixn.blockball.lib.com.github.shynixn.shyscoreboard")
    relocate("com.github.shynixn.shybossbar", "com.github.shynixn.blockball.lib.com.github.shynixn.shybossbar")
    relocate("com.github.shynixn.shycommandsigns", "com.github.shynixn.blockball.lib.com.github.shynixn.shycommandsigns")
    relocate("com.github.shynixn.shyparticles", "com.github.shynixn.blockball.lib.com.github.shynixn.shyparticles")
}

/**
 * Create latest plugin jar file.
 */
tasks.register("pluginJarLatest", ShadowJar::class.java) {
    dependsOn("relocatePluginJar")
    from(zipTree(File("./build/libs/" + (tasks.getByName("relocatePluginJar") as Jar).archiveFileName.get())))
    archiveFileName.set("${archiveBaseName.get()}-${archiveVersion.get()}-latest.${archiveExtension.get()}")
    // destinationDirectory.set(File("/app/plugins"))

    exclude("com/github/shynixn/blockball/lib/com/github/shynixn/mcutils/common/FoliaMarker.class")
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
    exclude("com/github/shynixn/blockball/lib/com/github/shynixn/mcutils/packet/nms/v1_21_R3/**")
    exclude("com/github/shynixn/blockball/lib/com/github/shynixn/mcutils/packet/nms/v1_21_R4/**")
    exclude("com/github/shynixn/blockball/lib/com/github/shynixn/mcutils/packet/nms/v1_21_R5/**")
    exclude("com/github/shynixn/blockball/lib/com/github/shynixn/mcutils/packet/nms/v1_21_R6/**")
    exclude("com/github/shynixn/mcutils/**")
    exclude("com/github/shynixn/mccoroutine/**")
    exclude("com/github/shynixn/shyscoreboard/**")
    exclude("com/github/shynixn/shybossbar/**")
    exclude("com/github/shynixn/shycommandsigns/**")
    exclude("com/github/shynixn/shyparticles/**")
    exclude("com/github/shynixn/fasterxml/**")
    exclude("org/**")
    exclude("kotlin/**")
    exclude("kotlinx/**")
    exclude("javax/**")
    exclude("com/zaxxer/**")
    exclude("templates/**")
    exclude("plugin-legacy.yml")
    exclude("plugin-folia.yml")
}

/**
 * Create premium plugin jar file.
 */
tasks.register("pluginJarPremium", com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar::class.java) {
    dependsOn("relocatePluginJar")
    from(zipTree(File("./build/libs/" + (tasks.getByName("relocatePluginJar") as Jar).archiveFileName.get())))
    archiveFileName.set("${archiveBaseName.get()}-${archiveVersion.get()}-premium.${archiveExtension.get()}")
    // destinationDirectory.set(File("C:\\git\\mc\\plugins"))

    exclude("com/github/shynixn/blockball/lib/com/github/shynixn/mcutils/common/FoliaMarker.class")
    exclude("com/github/shynixn/mcutils/**")
    exclude("com/github/shynixn/mccoroutine/**")
    exclude("com/github/shynixn/shyscoreboard/**")
    exclude("com/github/shynixn/shybossbar/**")
    exclude("com/github/shynixn/shycommandsigns/**")
    exclude("com/github/shynixn/shyparticles/**")
    exclude("com/github/shynixn/fasterxml/**")
    exclude("org/**")
    exclude("kotlin/**")
    exclude("kotlinx/**")
    exclude("javax/**")
    exclude("com/zaxxer/**")
    exclude("templates/**")
    exclude("plugin-legacy.yml")
    exclude("plugin-folia.yml")
}

/**
 * Relocate Plugin Folia Jar.
 */
tasks.register("relocateFoliaPluginJar", com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar::class.java) {
    dependsOn("shadowJar")
    from(zipTree(File("./build/libs/" + (tasks.getByName("shadowJar") as Jar).archiveFileName.get())))
    archiveFileName.set("${archiveBaseName.get()}-${archiveVersion.get()}-relocate-folia.${archiveExtension.get()}")
    relocate("com.github.shynixn.mcutils", "com.github.shynixn.blockball.lib.com.github.shynixn.mcutils")
    relocate("com.github.shynixn.shyscoreboard", "com.github.shynixn.blockball.lib.com.github.shynixn.shyscoreboard")
    relocate("com.github.shynixn.shybossbar", "com.github.shynixn.blockball.lib.com.github.shynixn.shybossbar")
    relocate("com.github.shynixn.shycommandsigns", "com.github.shynixn.blockball.lib.com.github.shynixn.shycommandsigns")
    relocate("com.github.shynixn.shyparticles", "com.github.shynixn.blockball.lib.com.github.shynixn.shyparticles")
    exclude("plugin.yml")
    rename("plugin-folia.yml", "plugin.yml")
}

/**
 * Create premium folia plugin jar file.
 */
tasks.register("pluginJarPremiumFolia", com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar::class.java) {
    dependsOn("relocateFoliaPluginJar")
    from(zipTree(File("./build/libs/" + (tasks.getByName("relocateFoliaPluginJar") as Jar).archiveFileName.get())))
    archiveFileName.set("${archiveBaseName.get()}-${archiveVersion.get()}-premium-folia.${archiveExtension.get()}")
    // destinationDirectory.set(File("C:\\git\\mc\\Folia\\plugins"))

    exclude("com/github/shynixn/mcutils/**")
    exclude("com/github/shynixn/mccoroutine/**")
    exclude("com/github/shynixn/shyscoreboard/**")
    exclude("com/github/shynixn/shybossbar/**")
    exclude("com/github/shynixn/shycommandsigns/**")
    exclude("com/github/shynixn/shyparticles/**")
    exclude("com/github/shynixn/fasterxml/**")
    exclude("org/**")
    exclude("kotlin/**")
    exclude("kotlinx/**")
    exclude("javax/**")
    exclude("com/zaxxer/**")
    exclude("templates/**")
    exclude("plugin-legacy.yml")
}

/**
 * Relocate legacy plugin jar file.
 */
tasks.register("relocateLegacyPluginJar", ShadowJar::class.java) {
    dependsOn("shadowJar")
    from(zipTree(File("./build/libs/" + (tasks.getByName("shadowJar") as Jar).archiveFileName.get())))
    archiveFileName.set("${archiveBaseName.get()}-${archiveVersion.get()}-legacy-relocate.${archiveExtension.get()}")
    relocate("kotlin", "com.github.shynixn.blockball.lib.kotlin")
    relocate("kotlinx", "com.github.shynixn.blockball.lib.kotlinx")
    relocate("org.intellij", "com.github.shynixn.blockball.lib.org.intelli")
    relocate("org.jetbrains", "com.github.shynixn.blockball.lib.org.jetbrains")
    relocate("javax.inject", "com.github.shynixn.blockball.lib.javax.inject")
    relocate("javax.annotation", "com.github.shynixn.blockball.lib.javax.annotation")
    relocate("org.slf4j", "com.github.shynixn.blockball.lib.org.slf4j")
    relocate("com.github.shynixn.mccoroutine", "com.github.shynixn.blockball.lib.com.github.shynixn.mccoroutine")
    relocate("com.google", "com.github.shynixn.blockball.lib.com.google")
    relocate("com.zaxxer", "com.github.shynixn.blockball.lib.com.zaxxer")
    relocate("org.apache", "com.github.shynixn.blockball.lib.org.apache")
    relocate("com.github.shynixn.mcutils", "com.github.shynixn.blockball.lib.com.github.shynixn.mcutils")
    relocate("com.github.shynixn.mccoroutine", "com.github.shynixn.blockball.lib.com.github.shynixn.mccoroutine")
    relocate("com.github.shynixn.shyscoreboard", "com.github.shynixn.blockball.lib.com.github.shynixn.shyscoreboard")
    relocate("com.github.shynixn.shybossbar", "com.github.shynixn.blockball.lib.com.github.shynixn.shybossbar")
    relocate("com.github.shynixn.shycommandsigns", "com.github.shynixn.blockball.lib.com.github.shynixn.shycommandsigns")
    relocate("com.github.shynixn.shyparticles", "com.github.shynixn.blockball.lib.com.github.shynixn.shyparticles")
    relocate("com.github.shynixn.fasterxml", "com.github.shynixn.blockball.lib.com.github.shynixn.fasterxml")

    exclude("plugin.yml")
    rename("plugin-legacy.yml", "plugin.yml")
}

/**
 * Create legacy plugin jar file.
 */
tasks.register("pluginJarLegacy", ShadowJar::class.java) {
    dependsOn("relocateLegacyPluginJar")
    from(zipTree(File("./build/libs/" + (tasks.getByName("relocateLegacyPluginJar") as Jar).archiveFileName.get())))
    archiveFileName.set("${archiveBaseName.get()}-${archiveVersion.get()}-legacy.${archiveExtension.get()}")
    // destinationDirectory.set(File("C:\\git\\mc\\plugins"))

    exclude("com/github/shynixn/blockball/lib/com/github/shynixn/mcutils/common/FoliaMarker.class")
    exclude("com/github/shynixn/mcutils/**")
    exclude("com/github/shynixn/mccoroutine/**")
    exclude("com/github/shynixn/shyscoreboard/**")
    exclude("com/github/shynixn/shybossbar/**")
    exclude("com/github/shynixn/shycommandsigns/**")
    exclude("com/github/shynixn/shyparticles/**")
    exclude("com/github/shynixn/fasterxml/**")
    exclude("org/**")
    exclude("kotlin/**")
    exclude("kotlinx/**")
    exclude("javax/**")
    exclude("com/zaxxer/**")
    exclude("templates/**")
    exclude("plugin-legacy.yml")
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
        "shyParticles"
    )
    contractContents.add("package com.github.shynixn.blockball.contract")
    contractContents.add("")
    contractContents.add("import com.github.shynixn.shyscoreboard.contract.ShyScoreboardLanguage")
    contractContents.add("import com.github.shynixn.mcutils.common.language.LanguageItem")
    contractContents.add("import com.github.shynixn.shybossbar.contract.ShyBossBarLanguage")
    contractContents.add("import com.github.shynixn.mcutils.common.language.LanguageProvider")
    contractContents.add("import com.github.shynixn.shycommandsigns.contract.ShyCommandSignsLanguage")
    contractContents.add("import com.github.shynixn.shyparticles.contract.ShyParticlesLanguage")
    contractContents.add("")
    contractContents.add("interface BlockBallLanguage : LanguageProvider, ShyScoreboardLanguage, ShyBossBarLanguage, ShyCommandSignsLanguage, ShyParticlesLanguage {")
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

tasks.register("printVersion") {
    println(version)
}

import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import java.net.URL
import java.nio.file.Files
import java.util.*

plugins {
    id("com.github.johnrengelman.shadow") version ("7.0.0")
}

tasks.withType<ShadowJar> {
    dependsOn("jar")
    archiveName = "${baseName}-${version}-mojangmapping.${extension}"

    relocate("kotlin", "com.github.shynixn.blockball.lib.kotlin")

    relocate("org.intellij", "com.github.shynixn.blockball.lib.org.intelli")
    relocate("org.jetbrains", "com.github.shynixn.blockball.lib.org.jetbrains")
    relocate("org.bstats", "com.github.shynixn.blockball.externallib.org.bstats")
    relocate("javax.inject", "com.github.shynixn.blockball.lib.javax.inject")
    relocate("javax.annotation", "com.github.shynixn.blockball.lib.javax.annotation")
    relocate("org.checkerframework", "com.github.shynixn.blockball.lib.org.checkerframework")
    relocate("org.aopalliance", "com.github.shynixn.blockball.lib.org.aopalliance")
    relocate("org.slf4j", "com.github.shynixn.blockball.lib.org.slf4j")

    relocate("com.github.shynixn.mccoroutine", "com.github.shynixn.blockball.lib.com.github.shynixn.mccoroutine")
    relocate("com.google", "com.github.shynixn.blockball.lib.com.google")
    relocate("com.zaxxer", "com.github.shynixn.blockball.lib.com.zaxxer")
    relocate("org.apache", "com.github.shynixn.blockball.lib.org.apache")

    exclude("DebugProbesKt.bin")
    exclude("module-info.class")
}


tasks.register("pluginJar", Exec::class.java) {
    // Change the output folder of the plugin.
    // val destinationDir = File("C:/temp/plugins")
    val destinationDir = File(buildDir, "libs")

    dependsOn("shadowJar")
    workingDir = buildDir

    if (!workingDir.exists()) {
        workingDir.mkdir();
    }

    val folder = File(workingDir, "mapping")

    if (!folder.exists()) {
        folder.mkdir()
    }

    val file = File(folder, "SpecialSources.jar")

    if (!file.exists()) {
        URL("https://repo.maven.apache.org/maven2/net/md-5/SpecialSource/1.10.0/SpecialSource-1.10.0-shaded.jar").openStream()
            .use {
                Files.copy(it, file.toPath())
            }
    }

    val shadowJar = tasks.findByName("shadowJar")!! as ShadowJar
    val sourceJarFile = File(buildDir, "libs/" + shadowJar.archiveName)
    val archiveName = "${shadowJar.baseName}-${shadowJar.version}.${shadowJar.extension}"
    val targetJarFile = File(destinationDir, archiveName)

    var obsMapping = createCommand(
        "1.17.1-R0.1-SNAPSHOT",
        "com/github/shynixn/blockball/bukkit/logic/business/service/nms/v1_17_R1",
        file,
        shadowJar,
        sourceJarFile,
        targetJarFile
    )
    obsMapping = "$obsMapping && " + createCommand(
        "1.18-R0.1-SNAPSHOT",
        "com/github/shynixn/blockball/bukkit/logic/business/service/nms/v1_18_R1",
        file,
        shadowJar,
        targetJarFile,
        targetJarFile
    )
    obsMapping = "$obsMapping && " + createCommand(
        "1.18.2-R0.1-SNAPSHOT",
        "com/github/shynixn/blockball/bukkit/logic/business/service/nms/v1_18_R2",
        file,
        shadowJar,
        targetJarFile,
        targetJarFile
    )
    obsMapping = "$obsMapping && " + createCommand(
        "1.19-R0.1-SNAPSHOT",
        "com/github/shynixn/blockball/bukkit/logic/business/service/nms/v1_19_R1",
        file,
        shadowJar,
        targetJarFile,
        targetJarFile
    )
    obsMapping = "$obsMapping && " + createCommand(
        "1.19.3-R0.1-SNAPSHOT",
        "com/github/shynixn/blockball/bukkit/logic/business/service/nms/v1_19_R2",
        file,
        shadowJar,
        targetJarFile,
        targetJarFile
    )
    obsMapping = "$obsMapping && " + createCommand(
        "1.19.4-R0.1-SNAPSHOT",
        "com/github/shynixn/blockball/bukkit/logic/business/service/nms/v1_19_R3",
        file,
        shadowJar,
        targetJarFile,
        targetJarFile
    )
    obsMapping = "$obsMapping && " + createCommand(
        "1.20.1-R0.1-SNAPSHOT",
        "com/github/shynixn/blockball/bukkit/logic/business/service/nms/v1_20_R1",
        file,
        shadowJar,
        targetJarFile,
        targetJarFile
    )

    if (System.getProperty("os.name").toLowerCase(Locale.ROOT).contains("windows")) {
        commandLine = listOf("cmd", "/c", obsMapping.replace("\$HOME", "%userprofile%"))
    } else {
        commandLine = listOf("sh", "-c", obsMapping)
    }
}

fun createCommand(
    version: String,
    include: String,
    file: File,
    shadowJar: ShadowJar,
    sourceJarFile: File,
    targetJarFile: File
): String {
    val obfArchiveName = "${shadowJar.baseName}-${shadowJar.version}-obfuscated.${shadowJar.extension}"
    val obfJarFile = File(buildDir, "libs/$obfArchiveName")

    return "java -jar ${file.absolutePath} -i \"$sourceJarFile\" -o \"$obfJarFile\"  -only \"$include\" -m \"\$HOME/.m2/repository/org/spigotmc/minecraft-server/${version}/minecraft-server-${version}-maps-mojang.txt\" --reverse" +
            "&& java -jar ${file.absolutePath} -i \"$obfJarFile\" -o \"$targetJarFile\"  -only \"$include\" -m \"\$HOME/.m2/repository/org/spigotmc/minecraft-server/${version}/minecraft-server-${version}-maps-spigot.csrg\""
}

repositories {
    maven("https://repo.extendedclip.com/content/repositories/placeholderapi")
    maven("https://repo.codemc.org/repository/maven-public")
    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
}

dependencies {
    implementation(project(":blockball-api"))
    implementation(project(":blockball-bukkit-api"))
    implementation(project(":blockball-core"))
    implementation(project(":blockball-bukkit-plugin:bukkit-nms-117R1"))
    implementation(project(":blockball-bukkit-plugin:bukkit-nms-118R1"))
    implementation(project(":blockball-bukkit-plugin:bukkit-nms-118R2"))
    implementation(project(":blockball-bukkit-plugin:bukkit-nms-119R1"))
    implementation(project(":blockball-bukkit-plugin:bukkit-nms-119R2"))
    implementation(project(":blockball-bukkit-plugin:bukkit-nms-119R3"))
    implementation(project(":blockball-bukkit-plugin:bukkit-nms-120R1"))

    implementation("com.github.shynixn.org.bstats:bstats-bukkit:1.7")
    implementation("org.slf4j:slf4j-jdk14:1.7.25")
    implementation("com.google.inject:guice:5.0.1")
    implementation("commons-io:commons-io:2.6")
    implementation("com.google.code.gson:gson:2.8.6")

    compileOnly("me.clip:placeholderapi:2.9.2")
    compileOnly("net.milkbowlvault:VaultAPI:1.7")
    compileOnly("org.spigotmc:spigot:1.16.4-R0.1-SNAPSHOT")

    testImplementation("org.xerial:sqlite-jdbc:3.23.1")
    testImplementation("org.spigotmc:spigot:1.16.4-R0.1-SNAPSHOT")
}

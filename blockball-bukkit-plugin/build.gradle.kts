import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    id("com.github.johnrengelman.shadow") version ("2.0.4")
}

tasks.withType<ShadowJar> {
    archiveName = "$baseName-$version.$extension"

    // Change the output folder of the plugin.
    // destinationDir = File("D:\\Benutzer\\Temp\\plugins")

    relocate("kotlin", "com.github.shynixn.blockball.lib.kotlin")

    relocate("org.intellij", "com.github.shynixn.blockball.lib.org.intelli")
    relocate("org.jetbrains", "com.github.shynixn.blockball.lib.org.jetbrains")
    relocate("org.bstats", "com.github.shynixn.blockball.lib.org.bstats")
    relocate("javax.inject", "com.github.shynixn.blockball.lib.javax.inject")
    relocate("org.aopalliance", "com.github.shynixn.blockball.lib.org.aopalliance")
    relocate("org.slf4j", "com.github.shynixn.blockball.lib.org.slf4j")

    relocate("com.google", "com.github.shynixn.blockball.lib.com.google")
    relocate("com.zaxxer", "com.github.shynixn.blockball.lib.com.zaxxer")
    relocate("org.apache", "com.github.shynixn.blockball.lib.org.apache")
}

publishing {
    publications {
        (findByName("mavenJava") as MavenPublication).artifact(tasks.findByName("shadowJar")!!)
    }
}

tasks.register<Exec>("dockerJar") {
    dependsOn("shadowJar")

    commandLine = if (System.getProperty("os.name").toLowerCase().contains("windows")) {
        listOf("cmd", "/c", "docker cp build/libs/. blockball-1.15:/minecraft/plugins")
    } else {
        listOf("sh", "-c", "docker cp build/libs/. blockball-1.15:/minecraft/plugins")
    }
}

repositories {
    maven("https://jitpack.io")
    maven("http://repo.extendedclip.com/content/repositories/placeholderapi")
    maven("http://maven.sk89q.com/repo")
}

dependencies {
    implementation(project(":blockball-api"))
    implementation(project(":blockball-bukkit-api"))
    implementation(project(":blockball-core"))
    implementation(project(":blockball-bukkit-plugin:blockball-bukkit-nms-108R3"))
    implementation(project(":blockball-bukkit-plugin:blockball-bukkit-nms-109R2"))
    implementation(project(":blockball-bukkit-plugin:blockball-bukkit-nms-110R1"))
    implementation(project(":blockball-bukkit-plugin:blockball-bukkit-nms-111R1"))
    implementation(project(":blockball-bukkit-plugin:blockball-bukkit-nms-112R1"))
    implementation(project(":blockball-bukkit-plugin:blockball-bukkit-nms-113R2"))
    implementation(project(":blockball-bukkit-plugin:blockball-bukkit-nms-114R1"))
    implementation(project(":blockball-bukkit-plugin:blockball-bukkit-nms-115R1"))

    implementation("org.slf4j:slf4j-jdk14:1.7.25")
    implementation("com.zaxxer:HikariCP:3.2.0")
    implementation("com.google.inject:guice:4.1.0")
    implementation("org.bstats.bStats-Metrics:bstats-bukkit:1.3")
    implementation("commons-io:commons-io:2.6")

    compileOnly("me.clip:placeholderapi:2.9.2")
    compileOnly("net.milkbowlvault:VaultAPI:1.7")

    compileOnly("org.spigotmc:spigot114R1:1.14.4-R1.0")

    testCompile("org.xerial:sqlite-jdbc:3.23.1")
    testCompile("ch.vorburger.mariaDB4j:mariaDB4j:2.2.3")
    testCompile("org.spigotmc:spigot18R3:1.8.8-R3.0")
}
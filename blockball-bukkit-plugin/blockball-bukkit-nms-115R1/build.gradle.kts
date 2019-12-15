dependencies {
    implementation(project(":blockball-api"))
    implementation(project(":blockball-core"))
    implementation(project(":blockball-bukkit-api"))

    compileOnly("org.spigotmc:spigot115R1:1.15.0-R1.0")
    compileOnly("com.google.inject:guice:4.1.0")

    testCompile("org.spigotmc:spigot115R1:1.15.0-R1.0")
}
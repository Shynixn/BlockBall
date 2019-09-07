dependencies {
    implementation(project(":blockball-api"))
    implementation(project(":blockball-core"))
    implementation(project(":blockball-bukkit-api"))

    compileOnly("org.spigotmc:spigot114R1:1.14.4-R1.0")
    compileOnly("com.google.inject:guice:4.1.0")

    testCompile("org.spigotmc:spigot114R1:1.14.4-R1.0")
}
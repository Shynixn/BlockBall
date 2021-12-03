dependencies {
    implementation(project(":blockball-api"))
    implementation(project(":blockball-core"))
    compileOnly("com.google.inject:guice:5.0.1")
    compileOnly("org.spigotmc:spigot:1.17.1-R0.1-SNAPSHOT:remapped-mojang")
    testImplementation("org.spigotmc:spigot:1.17.1-R0.1-SNAPSHOT:remapped-mojang")
}

dependencies {
    implementation(project(":blockball-api"))
    implementation(project(":blockball-sponge-api"))
    implementation(project(":blockball-core"))

    implementation("org.bstats.bStats-Metrics:bstats-sponge:1.3")

    compileOnly("org.spongepowered:spongeapi:7.1.0")
}

repositories {
    maven("https://jitpack.io")
}
dependencies {
    implementation(project(":blockball-api"))

    compileOnly("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.3.9")
    compileOnly("com.google.inject:guice:5.0.1")
    compileOnly("com.zaxxer:HikariCP:3.2.0")
    compileOnly("io.netty:netty-all:4.1.52.Final")

    testImplementation("commons-io:commons-io:2.6")
    testImplementation("org.yaml:snakeyaml:1.24")
}

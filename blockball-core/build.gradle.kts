dependencies {
    implementation(project(":blockball-api"))

    compileOnly("com.google.inject:guice:4.1.0")
    compileOnly("com.zaxxer:HikariCP:3.2.0")

    testCompile("commons-io:commons-io:2.6")
    testCompile("org.yaml:snakeyaml:1.24")
}
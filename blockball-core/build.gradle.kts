dependencies {
    implementation(project(":blockball-api"))

    compileOnly("com.google.inject:guice:4.1.0")
    compileOnly("com.zaxxer:HikariCP:3.2.0")

    testCompile("org.jetbrains.kotlin:kotlin-test")
    testCompile("org.jetbrains.kotlin:kotlin-test-junit")
    testCompile("org.mockito:mockito-core:2.23.0")
    testCompile("org.junit.jupiter:junit-jupiter-api:5.3.1")
    testCompile("org.xerial:sqlite-jdbc:3.23.1")
    testCompile("org.yaml:snakeyaml:1.24")
    testCompile("ch.vorburger.mariaDB4j:mariaDB4j:2.2.3")

    testRuntime("org.junit.jupiter:junit-jupiter-engine:5.3.1")
}
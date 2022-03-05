repositories {
    maven(url = "https://libraries.minecraft.net")
}

dependencies {
    // Dependencies of spigot mojang want to restrict usage to only Java 17. However, we do not care
    // what they want because the general compatibility of this plugin is Java 8. The plugin
    // guarantees that everything works during runtime. This error is a false positive.
    components {
        all {
            allVariants {
                attributes {
                    attribute(TargetJvmVersion.TARGET_JVM_VERSION_ATTRIBUTE, 8)
                }
            }
        }
    }

    implementation(project(":blockball-api"))
    implementation(project(":blockball-core"))
    compileOnly("com.google.inject:guice:5.0.1")
    compileOnly("org.spigotmc:spigot:1.18.2-R0.1-SNAPSHOT:remapped-mojang")
    testImplementation("org.spigotmc:spigot:1.18.2-R0.1-SNAPSHOT:remapped-mojang")
}

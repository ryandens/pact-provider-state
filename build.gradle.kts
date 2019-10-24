allprojects {
    group = "com.github.ryandens"
    version = "1.0-SNAPSHOT"
    repositories {
        mavenCentral()
    }
    apply(plugin = "java")
    apply(plugin = "com.diffplug.gradle.spotless")

    configure<JavaPluginConvention> {
        sourceCompatibility = JavaVersion.VERSION_11
    }

    spotless {
        java {
            removeUnusedImports()
            googleJavaFormat()
        }
        kotlinGradle {
            ktlint()
        }
    }
}

plugins {
    java
    id("com.diffplug.gradle.spotless") version "3.19.0"
}
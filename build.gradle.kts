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

    tasks.withType<Test> {
        useJUnitPlatform()
    }

    dependencies {
        testImplementation("org.junit.jupiter", "junit-jupiter-api", "5.4.0")
        testRuntimeOnly("org.junit.jupiter", "junit-jupiter-engine", "5.4.0")
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
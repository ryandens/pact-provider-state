plugins {
    application
    id("com.google.cloud.tools.jib") version "1.1.2"
}

jib.to.image = "ryandens/pact-provider-state"

application {
    mainClassName = "com.github.ryandens.pact.provider.state.Application"
}

dependencies {
    implementation("com.typesafe", "config", "1.3.1")
    implementation("com.fasterxml.jackson.core", "jackson-databind", "2.9.8")
}
plugins {
    kotlin("jvm") version "2.2.20"
    id("me.champeau.jmh") version "0.7.3"
    application
}

group = "com.github.sebastianp265"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("com.github.ajalt.clikt:clikt:4.2.1")
    testImplementation("io.kotest:kotest-runner-junit5:6.1.3")
    testImplementation("io.kotest:kotest-assertions-core:6.1.3")
    jmh("org.openjdk.jmh:jmh-core:1.37")
    jmh("org.openjdk.jmh:jmh-generator-annprocess:1.37")
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(21)
}

application {
    mainClass.set("com.github.sebastianp265.MainKt")
}

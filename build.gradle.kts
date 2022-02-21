plugins {
    kotlin("jvm") version "1.6.10"
}

group = "com.soarex"
version = "1.0"

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib"))

    implementation("com.github.h0tk3y.betterParse:better-parse:0.4.3")
    implementation("org.slf4j", "slf4j-simple", "1.7.30")
    implementation("io.github.microutils:kotlin-logging:2.1.21")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.0")

    testImplementation(kotlin("test"))
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.2")
    testImplementation("org.junit.jupiter:junit-jupiter:5.8.2")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}

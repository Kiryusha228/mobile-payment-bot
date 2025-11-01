plugins {
    alias(libs.plugins.spring.boot)
    alias(libs.plugins.spring.dependency.management)
    java
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.spring)
}

group = "org.example"
version = "1.0-SNAPSHOT"

dependencies {
    implementation(project(":core"))
    implementation(libs.bundles.spring.webflux)
    implementation(libs.spring.kafka)
    implementation(libs.telegrambots)

    compileOnly(libs.lombok)
    annotationProcessor(libs.lombok)

    testImplementation(platform(libs.junit.bom))
    testImplementation(libs.junit.jupiter)
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(17)
}
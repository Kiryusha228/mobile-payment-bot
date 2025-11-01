plugins {
    alias(libs.plugins.kotlin.kapt)
    alias(libs.plugins.kotlin.spring)
    alias(libs.plugins.kotlin.jpa)
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.spring.boot)
    alias(libs.plugins.spring.dependency.management)
}

group = "org.example"
version = "1.0-SNAPSHOT"

kapt {
    correctErrorTypes = true
}

dependencies {
    implementation(project(":core"))

    implementation(libs.bundles.kotlin.common)
    implementation(libs.bundles.spring.web)
    implementation(libs.spring.boot.starter.data.jpa)
    implementation(libs.postgresql)
    implementation(libs.kotlin.logging)
    implementation(libs.springdoc.openapi)
    implementation(libs.spring.kafka)

    implementation(libs.mapstruct)
    kapt(libs.mapstruct.processor)

    testImplementation(libs.bundles.testing)
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(17)
}
plugins {
    kotlin("jvm")
    kotlin("kapt")
    kotlin("plugin.spring") version "1.9.25"
    kotlin("plugin.jpa") version "1.9.25"
    id("org.springframework.boot") version "3.3.3"
    id("io.spring.dependency-management") version "1.1.6"
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

kapt {
    correctErrorTypes = true
}

dependencies {
    testImplementation(kotlin("test"))
    implementation(kotlin("reflect"))
    //spring
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter")
    implementation("org.springframework.boot:spring-boot-starter-webflux")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    //lombok
    implementation("org.projectlombok:lombok:1.18.34")
    annotationProcessor("org.projectlombok:lombok:1.18.34")
    //postgres
    implementation("org.postgresql:postgresql")
    //tests
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    //swagger
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.6.0")
    //mapstruct
    implementation("org.mapstruct:mapstruct:1.5.5.Final")
    kapt("org.mapstruct:mapstruct-processor:1.5.5.Final")
    //kafka
    implementation("org.springframework.kafka:spring-kafka")

    implementation(project(":core"))
    // kotlin
    implementation("io.github.oshai:kotlin-logging-jvm:7.0.13")
}



tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(17)
}
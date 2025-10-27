plugins {
    kotlin("jvm")
    kotlin("plugin.spring") version "1.9.25"
    id("io.spring.dependency-management") version "1.1.6"
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencyManagement {
    imports {
        mavenBom("org.springframework.boot:spring-boot-dependencies:3.3.3")
    }
}

dependencies {
    testImplementation(kotlin("test"))
    //spring
    implementation("org.springframework.boot:spring-boot-starter")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    //kafka
    implementation("org.springframework.kafka:spring-kafka")
}



tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(17)
}
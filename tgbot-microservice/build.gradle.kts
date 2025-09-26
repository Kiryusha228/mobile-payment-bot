plugins {
    id("org.springframework.boot") version "3.3.3"
    id("io.spring.dependency-management") version "1.1.6"
    id("java")
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenLocal()
    mavenCentral()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    implementation("org.telegram:telegrambots:6.9.7.1")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.projectlombok:lombok:1.18.34")
    annotationProcessor("org.projectlombok:lombok:1.18.34")
    implementation("org.postgresql:postgresql")
    implementation("org.hibernate:hibernate-core:5.6.0.Final")
    implementation("jakarta.persistence:jakarta.persistence-api:2.2.3")
    implementation("org.springframework.boot:spring-boot-starter-webflux")

    implementation("io.github.cdimascio:dotenv-java:2.3.2")
}

tasks.test {
    useJUnitPlatform()
}
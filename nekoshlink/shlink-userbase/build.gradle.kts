import org.springframework.boot.gradle.tasks.bundling.BootJar

plugins {
    id("org.springframework.boot")
    id("io.spring.dependency-management")
    kotlin("jvm")
    kotlin("plugin.spring")
}

dependencies {
    implementation("org.springframework.data:spring-data-jpa")
    implementation("org.springframework.security:spring-security-core")
    implementation("org.springframework:spring-web")
    implementation("org.springframework.boot:spring-boot-autoconfigure")
    implementation("com.fasterxml.jackson.core:jackson-annotations")
    implementation("jakarta.annotation:jakarta.annotation-api")
    implementation("org.hibernate:hibernate-core")
    implementation("io.github.microutils:kotlin-logging-jvm:2.1.23")
    implementation("org.javers:javers-spring-boot-starter-sql:6.6.5")
    implementation("org.nekosoft.utils:utils-common:main-SNAPSHOT")
}

tasks.withType<BootJar>().configureEach() {
    enabled = false
}

tasks.getByName<Jar>("jar") {
    enabled = true
}

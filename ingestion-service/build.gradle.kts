plugins {
    java
    alias(libs.plugins.spring.boot)
    alias(libs.plugins.spring.dependency.management)
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":common"))

    implementation(libs.spring.boot.webmvc)
    implementation(libs.spring.boot.validation)
    implementation(libs.spring.boot.actuator)
    implementation(libs.spring.boot.data.jpa) // JPA, Hibernate and Spring Data
    implementation(libs.spring.boot.flyway) // Flyway
    
    implementation(platform(libs.spring.ai.bom)) // a BOM for Spring AI
    implementation(libs.spring.ai.pdf.reader)
    implementation(libs.spring.ai.model.transformers)
    implementation(libs.spring.ai.vector.store)

    runtimeOnly(libs.postgresql.postgresql)
    runtimeOnly(libs.flyway.postgresql)

    testImplementation(libs.spring.boot.webmvc.test)
    testImplementation(libs.spring.boot.testcontainers)
    testImplementation(libs.testcontainers.postgresql)
    testImplementation(libs.testcontainers.junit.jupiter)

    testRuntimeOnly(libs.junit.platform.launcher)
}

tasks.withType<Test> {
    useJUnitPlatform()
}

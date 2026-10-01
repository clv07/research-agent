// Shared library (DTOs, error model) used by the services. A plain jar, not a runnable app.
plugins {
    `java-library`
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

repositories {
    mavenCentral()
}

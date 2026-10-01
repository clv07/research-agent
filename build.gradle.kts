// Root build: declares the plugins once (with versions from the catalog) without applying them.
// Each module then applies only the plugins it needs.
plugins {
    alias(libs.plugins.spring.boot) apply false
    alias(libs.plugins.spring.dependency.management) apply false
}

allprojects {
    group = "scholar"
    version = "0.0.1-SNAPSHOT"
}

plugins {
    alias(libs.plugins.hitz.jvm.library)
}

// Test doubles shared by the tests of other modules (testImplementation only).
dependencies {
    api(project(":core:domain"))
}

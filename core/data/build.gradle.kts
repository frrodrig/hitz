plugins {
    alias(libs.plugins.hitz.android.library)
    alias(libs.plugins.hitz.hilt)
}

android {
    namespace = "eus.hitz.core.data"
}

dependencies {
    api(project(":core:domain"))
    implementation(libs.kotlinx.coroutines.android)
}

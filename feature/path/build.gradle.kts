plugins {
    alias(libs.plugins.hitz.android.feature)
}

android {
    namespace = "eus.hitz.feature.path"
}

dependencies {
    implementation(libs.coil.compose)
}

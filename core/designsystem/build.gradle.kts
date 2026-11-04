plugins {
    alias(libs.plugins.hitz.android.library.compose)
}

android {
    namespace = "eus.hitz.core.designsystem"
}

dependencies {
    testImplementation(libs.androidx.test.ext.junit)
}

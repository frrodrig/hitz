plugins {
    alias(libs.plugins.hitz.android.library.compose)
}

android {
    namespace = "eus.hitz.core.designsystem"
}

dependencies {
    implementation(libs.lottie.compose)
    testImplementation(libs.androidx.test.ext.junit)
}

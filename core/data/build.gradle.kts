plugins {
    alias(libs.plugins.hitz.android.library)
    alias(libs.plugins.hitz.android.room)
    alias(libs.plugins.hitz.hilt)
}

android {
    namespace = "eus.hitz.core.data"
}

dependencies {
    api(project(":core:domain"))
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.androidx.datastore.preferences)

    testImplementation(libs.robolectric)
    testImplementation(libs.androidx.test.core)
    testImplementation(libs.androidx.test.ext.junit)
}

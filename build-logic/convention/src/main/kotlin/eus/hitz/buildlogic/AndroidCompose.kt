package eus.hitz.buildlogic

import com.android.build.api.dsl.CommonExtension
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

/** Enables Compose and adds the BOM, Material 3, preview tooling and screenshot-test libraries. */
internal fun Project.configureAndroidCompose(commonExtension: CommonExtension) {
    commonExtension.apply {
        buildFeatures.apply {
            compose = true
        }
    }
    dependencies {
        val bom = libs.lib("androidx-compose-bom")
        "implementation"(platform(bom))
        "androidTestImplementation"(platform(bom))
        "testImplementation"(platform(bom))
        "implementation"(libs.lib("androidx-compose-ui"))
        "implementation"(libs.lib("androidx-compose-material3"))
        "implementation"(libs.lib("androidx-compose-ui-tooling-preview"))
        "debugImplementation"(libs.lib("androidx-compose-ui-tooling"))
        "debugImplementation"(libs.lib("androidx-compose-ui-test-manifest"))
        "testImplementation"(libs.lib("androidx-compose-ui-test-junit4"))
        "testImplementation"(libs.lib("androidx-test-ext-junit"))
        "testImplementation"(libs.lib("robolectric"))
        "testImplementation"(libs.lib("roborazzi"))
        "testImplementation"(libs.lib("roborazzi-compose"))
        "testImplementation"(libs.lib("roborazzi-junit-rule"))
        "androidTestImplementation"(libs.lib("androidx-compose-ui-test-junit4"))
        "androidTestImplementation"(libs.lib("androidx-test-ext-junit"))
        "androidTestImplementation"(libs.lib("androidx-test-runner"))
    }
}

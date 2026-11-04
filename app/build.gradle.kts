plugins {
    alias(libs.plugins.hitz.android.application.compose)
    alias(libs.plugins.hitz.hilt)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.roborazzi)
}

val keystorePath = providers.environmentVariable("HITZ_KEYSTORE_PATH")

android {
    namespace = "eus.hitz.app"

    defaultConfig {
        applicationId = "eus.hitz.app"
        versionCode = providers.gradleProperty("hitz.versionCode").getOrElse("1").toInt()
        versionName = providers.gradleProperty("hitz.versionName").getOrElse("0.1.0")
    }

    signingConfigs {
        create("release") {
            if (keystorePath.isPresent) {
                storeFile = file(keystorePath.get())
                storePassword = providers.environmentVariable("HITZ_KEYSTORE_PASSWORD").get()
                keyAlias = providers.environmentVariable("HITZ_KEY_ALIAS").get()
                keyPassword = providers.environmentVariable("HITZ_KEY_PASSWORD").get()
            }
        }
    }

    buildTypes {
        getByName("debug") {
            applicationIdSuffix = ".debug"
        }
        getByName("release") {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
            // Sin keystore (builds locales y CI de PR) el release queda sin firmar.
            if (keystorePath.isPresent) {
                signingConfig = signingConfigs.getByName("release")
            }
        }
    }

    packaging {
        resources.excludes += "/META-INF/{AL2.0,LGPL2.1}"
    }
}

dependencies {
    implementation(project(":core:data"))
    implementation(project(":core:designsystem"))
    implementation(project(":core:domain"))
    implementation(project(":feature:path"))
    implementation(project(":feature:lesson"))
    implementation(project(":feature:profile"))
    implementation(project(":feature:leaderboard"))
    implementation(project(":feature:shop"))

    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.core.splashscreen)
    implementation(libs.androidx.hilt.navigation.compose)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.kotlinx.serialization.json)

    testImplementation(libs.androidx.test.ext.junit)
    androidTestImplementation(libs.androidx.test.espresso.core)
}

import io.gitlab.arturbosch.detekt.Detekt

plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.kotlin.jvm) apply false
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.kotlin.serialization) apply false
    alias(libs.plugins.ksp) apply false
    alias(libs.plugins.hilt) apply false
    alias(libs.plugins.roborazzi) apply false
    alias(libs.plugins.detekt)
}

// detekt (con las reglas de ktlint vía detekt-formatting) se ejecuta una sola vez
// sobre todo el código desde el proyecto raíz. Así no depende de la API de variantes
// de Android, que AGP 9 ha retirado.
detekt {
    buildUponDefaultConfig = true
    parallel = true
    config.setFrom(files("config/detekt/detekt.yml"))
    source.setFrom(
        fileTree(rootDir) {
            include("**/src/*/kotlin/**/*.kt", "**/src/*/java/**/*.kt", "**/*.gradle.kts")
            exclude("**/build/**", "**/.gradle/**")
        },
    )
}

dependencies {
    detektPlugins(libs.detekt.formatting)
}

// detekt 1.23 está compilado con Kotlin 2.0.x; evitamos que Gradle lo alinee con el
// Kotlin del proyecto.
configurations.matching { it.name == "detekt" }.configureEach {
    resolutionStrategy.eachDependency {
        if (requested.group == "org.jetbrains.kotlin") {
            useVersion(libs.versions.detektKotlin.get())
        }
    }
}

tasks.withType<Detekt>().configureEach {
    jvmTarget = "17"
    reports {
        html.required.set(true)
        sarif.required.set(true)
        xml.required.set(false)
        txt.required.set(false)
        md.required.set(false)
    }
}

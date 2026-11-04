import eus.hitz.buildlogic.lib
import eus.hitz.buildlogic.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.dependencies

/** Módulo de feature: librería Compose + Hilt + navegación type-safe + dependencias de core. */
abstract class AndroidFeatureConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            apply(plugin = "hitz.android.library.compose")
            apply(plugin = "hitz.hilt")
            apply(plugin = "org.jetbrains.kotlin.plugin.serialization")
            dependencies {
                "implementation"(project(":core:designsystem"))
                "implementation"(project(":core:domain"))
                "implementation"(project(":core:model"))
                "implementation"(libs.lib("androidx-hilt-navigation-compose"))
                "implementation"(libs.lib("androidx-lifecycle-runtime-compose"))
                "implementation"(libs.lib("androidx-lifecycle-viewmodel-compose"))
                "implementation"(libs.lib("androidx-navigation-compose"))
                "implementation"(libs.lib("kotlinx-serialization-json"))
            }
        }
    }
}

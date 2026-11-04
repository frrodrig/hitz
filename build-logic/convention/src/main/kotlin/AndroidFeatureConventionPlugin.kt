import eus.hitz.buildlogic.lib
import eus.hitz.buildlogic.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.dependencies

/** Feature module: Compose library + Hilt + type-safe navigation + core dependencies. */
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

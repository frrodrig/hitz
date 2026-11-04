import eus.hitz.buildlogic.lib
import eus.hitz.buildlogic.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.dependencies

/**
 * JVM modules only get the annotations (hilt-core); Android modules also get the
 * Hilt Gradle plugin and the KSP processor.
 */
abstract class HiltConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            apply(plugin = "com.google.devtools.ksp")
            dependencies {
                "ksp"(libs.lib("hilt-compiler"))
            }
            pluginManager.withPlugin("org.jetbrains.kotlin.jvm") {
                dependencies { "implementation"(libs.lib("hilt-core")) }
            }
            pluginManager.withPlugin("com.android.base") {
                apply(plugin = "com.google.dagger.hilt.android")
                dependencies {
                    "implementation"(libs.lib("hilt-android"))
                    "testImplementation"(libs.lib("hilt-android-testing"))
                    "kspTest"(libs.lib("hilt-compiler"))
                    "androidTestImplementation"(libs.lib("hilt-android-testing"))
                    "kspAndroidTest"(libs.lib("hilt-compiler"))
                }
            }
        }
    }
}

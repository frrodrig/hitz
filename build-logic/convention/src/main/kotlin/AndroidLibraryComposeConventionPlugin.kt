import com.android.build.api.dsl.LibraryExtension
import eus.hitz.buildlogic.configureAndroidCompose
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.configure

abstract class AndroidLibraryComposeConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            apply(plugin = "hitz.android.library")
            apply(plugin = "org.jetbrains.kotlin.plugin.compose")
            apply(plugin = "io.github.takahirom.roborazzi")
            extensions.configure<LibraryExtension> {
                configureAndroidCompose(this)
            }
        }
    }
}

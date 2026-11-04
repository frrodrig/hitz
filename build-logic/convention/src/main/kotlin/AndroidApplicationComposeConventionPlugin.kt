import com.android.build.api.dsl.ApplicationExtension
import eus.hitz.buildlogic.configureAndroidCompose
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.configure

abstract class AndroidApplicationComposeConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            apply(plugin = "hitz.android.application")
            apply(plugin = "org.jetbrains.kotlin.plugin.compose")
            extensions.configure<ApplicationExtension> {
                configureAndroidCompose(this)
            }
        }
    }
}

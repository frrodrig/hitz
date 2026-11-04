import com.android.build.api.dsl.LibraryExtension
import eus.hitz.buildlogic.configureKotlinAndroid
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.configure

abstract class AndroidLibraryConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            apply(plugin = "com.android.library")
            extensions.configure<LibraryExtension> {
                configureKotlinAndroid(this)
                // Resource prefix derived from the module path: :feature:path -> "feature_path_"
                resourcePrefix = path.split(':', '-').filter(String::isNotBlank)
                    .joinToString(separator = "_", postfix = "_")
                    .lowercase()
            }
        }
    }
}

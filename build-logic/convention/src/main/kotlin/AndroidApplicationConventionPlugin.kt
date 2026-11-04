import com.android.build.api.dsl.ApplicationExtension
import eus.hitz.buildlogic.configureKotlinAndroid
import eus.hitz.buildlogic.libs
import eus.hitz.buildlogic.versionInt
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.configure

abstract class AndroidApplicationConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            apply(plugin = "com.android.application")
            extensions.configure<ApplicationExtension> {
                configureKotlinAndroid(this)
                defaultConfig.targetSdk = libs.versionInt("targetSdk")
                testOptions.animationsDisabled = true
                lint.apply {
                    abortOnError = true
                    checkDependencies = true
                    xmlReport = false
                    htmlReport = true
                    sarifReport = true
                }
            }
        }
    }
}

import com.google.devtools.ksp.gradle.KspExtension
import eus.hitz.buildlogic.lib
import eus.hitz.buildlogic.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies

abstract class AndroidRoomConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            apply(plugin = "com.google.devtools.ksp")
            extensions.configure<KspExtension> {
                arg("room.schemaLocation", layout.projectDirectory.dir("schemas").asFile.path)
                arg("room.generateKotlin", "true")
            }
            dependencies {
                "implementation"(libs.lib("room-runtime"))
                "implementation"(libs.lib("room-ktx"))
                "ksp"(libs.lib("room-compiler"))
            }
        }
    }
}

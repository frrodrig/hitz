package eus.hitz.core.data.content

import com.google.common.truth.Truth.assertThat
import eus.hitz.core.model.CourseId
import eus.hitz.core.model.content.Level
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Test
import java.io.File

/**
 * Decodes every bundled content file with the app's strict JSON settings, so any drift between
 * the JSON, the schema and the Kotlin model fails the build.
 */
class BundledContentTest {
    private val root = File("src/main/assets/courses")
    private val source = ContentSource { path -> File(root, path).readText() }

    @Test
    fun `every unit listed in the course decodes`() = runTest {
        val repository = AssetCourseRepository(source, StandardTestDispatcher(testScheduler))
        val courseId = CourseId.BASQUE_FROM_SPANISH
        val course = repository.getCourse(courseId)

        assertThat(course.id).isEqualTo(courseId.value)
        val unitIds = course.sections.flatMap { it.units }
        assertThat(unitIds).isNotEmpty()
        unitIds.forEach { unitId ->
            val unit = repository.getUnit(courseId, unitId)
            assertThat(unit.id).isEqualTo(unitId)
            assertThat(unit.levels.filterIsInstance<Level.Lessons>()).isNotEmpty()
        }
    }

    @Test
    fun `there are no unit files outside the course index`() = runTest {
        val repository = AssetCourseRepository(source, StandardTestDispatcher(testScheduler))
        val listed = repository.getCourse(CourseId.BASQUE_FROM_SPANISH).sections.flatMap { it.units }.toSet()
        val files = File(root, "eu-es/units").listFiles().orEmpty().map { it.nameWithoutExtension }.toSet()
        assertThat(files).isEqualTo(listed)
    }

    @Test
    fun `the glossary decodes and covers the first lesson`() = runTest {
        val repository = AssetCourseRepository(source, StandardTestDispatcher(testScheduler))
        val glossary = repository.getGlossary(CourseId.BASQUE_FROM_SPANISH)

        assertThat(glossary.entries["egun on"]?.es).isEqualTo("buenos días")
        assertThat(glossary.segment("Kaixo! Ni Ane naiz.").mapNotNull { it.gloss }).hasSize(3)
    }

    @Test
    fun `repository caches parsed units`() = runTest {
        var reads = 0
        val counting = ContentSource { path ->
            reads++
            source.read(path)
        }
        val repository = AssetCourseRepository(counting, StandardTestDispatcher(testScheduler))
        repeat(3) { repository.getCourse(CourseId.BASQUE_FROM_SPANISH) }
        assertThat(reads).isEqualTo(1)
    }
}

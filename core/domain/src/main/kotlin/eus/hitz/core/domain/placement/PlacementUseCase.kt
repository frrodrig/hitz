package eus.hitz.core.domain.placement

import eus.hitz.core.domain.content.CourseRepository
import eus.hitz.core.domain.progress.ProgressRepository
import eus.hitz.core.model.CourseId
import eus.hitz.core.model.content.CourseUnit
import eus.hitz.core.model.content.Exercise
import eus.hitz.core.model.content.Level
import javax.inject.Inject
import kotlin.random.Random

/** A placement test for one section of the course. */
data class PlacementTest(
    val sectionId: String,
    /** 1-based position of the section in the course. */
    val sectionNumber: Int,
    val exercises: List<Exercise>,
    /** The section after this one, if any: a learner who passes can try it too. */
    val nextSectionId: String?,
)

/**
 * Placement tests for learners who already know some Basque: one test per section, taken in
 * order. A test asks a few exercises of every unit of the section (from the later lessons of each
 * unit, so they cover what the unit builds up to); a learner who gets [PASS_ACCURACY] of them right
 * at the first try skips the section, which is marked as done on the path.
 */
class PlacementUseCase @Inject constructor(
    private val courses: CourseRepository,
    private val progress: ProgressRepository,
) {
    suspend fun test(
        sectionId: String,
        random: Random = Random.Default,
        courseId: CourseId = CourseId.BASQUE_FROM_SPANISH,
    ): PlacementTest {
        val sections = courses.getCourse(courseId).sections
        val index = sections.indexOfFirst { it.id == sectionId }
        require(index >= 0) { "unknown section $sectionId" }
        val exercises = sections[index].units.flatMap { unitId ->
            courses.getUnit(courseId, unitId).testableExercises().shuffled(random).take(PER_UNIT)
        }
        return PlacementTest(sectionId, index + 1, exercises, sections.getOrNull(index + 1)?.id)
    }

    /** Marks every required level of the section as completed (legendary levels stay optional). */
    suspend fun skipSection(
        sectionId: String,
        accuracy: Float,
        now: Long,
        courseId: CourseId = CourseId.BASQUE_FROM_SPANISH,
    ) {
        val section = courses.getCourse(courseId).sections.first { it.id == sectionId }
        for (unitId in section.units) {
            courses.getUnit(courseId, unitId).completionIds().forEach { completionId ->
                progress.recordCompletion(unitId, completionId, accuracy, now)
            }
        }
    }

    /** Exercises of the unit's later lesson levels that do not need speaking or a card. */
    private fun CourseUnit.testableExercises(): List<Exercise> {
        val lessonLevels = levels.filterIsInstance<Level.Lessons>()
        return lessonLevels.drop(1).ifEmpty { lessonLevels }
            .flatMap { level -> level.lessons.flatMap { it.exercises } }
            .filterNot { it is Exercise.WordIntro || it is Exercise.Speak }
    }

    private fun CourseUnit.completionIds(): List<String> = levels.flatMap { level ->
        when (level) {
            is Level.Lessons -> level.lessons.map { it.id }
            is Level.StoryLevel, is Level.Review -> listOf(level.id)
            is Level.Legendary -> emptyList()
        }
    }

    companion object {
        /** Exercises per unit: a section of six units makes a test of 12. */
        const val PER_UNIT = 2
        const val PASS_ACCURACY = 0.8f

        fun passed(accuracy: Float): Boolean = accuracy >= PASS_ACCURACY
    }
}

package eus.hitz.core.domain.placement

import eus.hitz.core.domain.content.CourseRepository
import eus.hitz.core.domain.progress.ProgressRepository
import eus.hitz.core.model.CourseId
import eus.hitz.core.model.content.CourseUnit
import eus.hitz.core.model.content.Exercise
import eus.hitz.core.model.content.Level
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import kotlin.random.Random

/**
 * A placement test. Passing it marks every section of [sectionIds] as done. A test from the first
 * run covers one section; a jump from the sections overview covers every section before the one
 * the learner jumps to ([isJump]).
 */
data class PlacementTest(
    val sectionId: String,
    /** 1-based position in the course of the tested section, or of the target of a jump. */
    val sectionNumber: Int,
    val exercises: List<Exercise>,
    /** The section after this one, if any: a learner who passes can try it too. None for jumps. */
    val nextSectionId: String?,
    val sectionIds: List<String> = listOf(sectionId),
    val isJump: Boolean = false,
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

    /**
     * A test to jump to [targetSectionId]: about [JUMP_SIZE] exercises spread over every unit of the
     * sections between the first one not finished and the target. Passing it marks them all as
     * done, which unlocks the target.
     */
    suspend fun jumpTest(
        targetSectionId: String,
        random: Random = Random.Default,
        courseId: CourseId = CourseId.BASQUE_FROM_SPANISH,
    ): PlacementTest {
        val sections = courses.getCourse(courseId).sections
        val target = sections.indexOfFirst { it.id == targetSectionId }
        require(target > 0) { "cannot jump to section $targetSectionId" }
        val completed = progress.observeCompleted().first()
        val skipped = sections.subList(0, target).filterNot { section ->
            section.units.all { unitId -> courses.getUnit(courseId, unitId).completionIds().all { it in completed } }
        }
        require(skipped.isNotEmpty()) { "section $targetSectionId is already unlocked" }
        val units = skipped.flatMap { it.units }
        val perUnit = (JUMP_SIZE + units.size - 1) / units.size
        val exercises = units.flatMap { unitId ->
            courses.getUnit(courseId, unitId).testableExercises().shuffled(random).take(perUnit)
        }.shuffled(random).take(JUMP_SIZE.coerceAtLeast(units.size))
        return PlacementTest(
            sectionId = targetSectionId,
            sectionNumber = target + 1,
            exercises = exercises,
            nextSectionId = null,
            sectionIds = skipped.map { it.id },
            isJump = true,
        )
    }

    /** Marks every required level of the section as completed (star challenges stay optional). */
    suspend fun skipSection(
        sectionId: String,
        accuracy: Float,
        now: Long,
        courseId: CourseId = CourseId.BASQUE_FROM_SPANISH,
    ) {
        skipSections(listOf(sectionId), accuracy, now, courseId)
    }

    /** [skipSection] for every section of [sectionIds]. */
    suspend fun skipSections(
        sectionIds: List<String>,
        accuracy: Float,
        now: Long,
        courseId: CourseId = CourseId.BASQUE_FROM_SPANISH,
    ) {
        val units = courses.getCourse(courseId).sections.filter { it.id in sectionIds }.flatMap { it.units }
        for (unitId in units) {
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
            is Level.Challenge -> emptyList()
        }
    }

    companion object {
        /** Exercises per unit: a section of six units makes a test of 12. */
        const val PER_UNIT = 2

        /** Exercises in a jump test, whatever the number of sections it covers. */
        const val JUMP_SIZE = 18
        const val PASS_ACCURACY = 0.8f

        fun passed(accuracy: Float): Boolean = accuracy >= PASS_ACCURACY
    }
}

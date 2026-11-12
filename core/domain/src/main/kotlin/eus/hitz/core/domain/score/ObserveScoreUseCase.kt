package eus.hitz.core.domain.score

import eus.hitz.core.domain.content.CourseRepository
import eus.hitz.core.domain.progress.ProgressRepository
import eus.hitz.core.domain.score.ScoreRules.steps
import eus.hitz.core.domain.time.EpochClock
import eus.hitz.core.domain.time.ZoneProvider
import eus.hitz.core.domain.time.epochDay
import eus.hitz.core.model.CourseId
import eus.hitz.core.model.content.Course
import eus.hitz.core.model.content.CourseUnit
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

private const val WEEK_DAYS = 7

/**
 * The learner's [Score], updated whenever a step is completed. The history is rebuilt from the
 * completion times, so it needs no storage of its own and includes placement tests.
 */
class ObserveScoreUseCase @Inject constructor(
    private val courses: CourseRepository,
    private val progress: ProgressRepository,
    private val clock: EpochClock,
    private val zones: ZoneProvider,
) {
    operator fun invoke(courseId: CourseId = CourseId.BASQUE_FROM_SPANISH): Flow<Score> {
        val content = flow {
            val course = courses.getCourse(courseId)
            emit(course to course.sections.flatMap { it.units }.associateWith { courses.getUnit(courseId, it) })
        }
        return combine(content, progress.observeCompletions()) { (course, units), completions ->
            score(course, units, completions)
        }
    }

    private fun score(course: Course, units: Map<String, CourseUnit>, completions: Map<String, Long>): Score {
        val zone = zones.zone()
        val today = epochDay(clock.now(), zone)
        val steps = steps(course, units)
        val total = steps.size
        // The day each required step was first completed.
        val days = steps.mapNotNull { step -> completions[step]?.let { epochDay(it, zone) } }.sorted()
        val points = ScoreRules.points(days.size, total)
        val history = days.distinct().map { day ->
            ScorePoint(day, ScoreRules.value(ScoreRules.points(days.count { it <= day }, total)))
        }
        val now = ScoreRules.value(points)
        val weekAgo = ScoreRules.value(ScoreRules.points(days.count { it <= today - WEEK_DAYS }, total))
        return Score(
            value = now,
            progressToNext = ScoreRules.progressToNext(points),
            gainedThisWeek = now - weekAgo,
            history = history.filter { it.epochDay < today } + ScorePoint(today, now),
            canDo = course.sections.flatMap { it.units }.mapNotNull { units[it] }
                .filter { unit -> unit.steps().let { it.isNotEmpty() && it.all { step -> step in completions } } }
                .map { CanDo(it.id, it.title, it.goals) },
        )
    }
}

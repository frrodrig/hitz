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
        val byLevel = ScoreRules.stepsByLevel(course, units)
        val max = ScoreRules.max(byLevel.keys)
        // The day each required step was first completed, per level.
        val days = byLevel.mapValues { (_, steps) ->
            steps.mapNotNull { step -> completions[step]?.let { epochDay(it, zone) } }
        }
        fun pointsUntil(day: Long?): Float = byLevel.entries.sumOf { (level, steps) ->
            val done = days.getValue(level).count { day == null || it <= day }
            ScoreRules.points(level, done, steps.size).toDouble()
        }.toFloat()
        val points = pointsUntil(null)
        val now = ScoreRules.value(points, max)
        val history = days.values.flatten().distinct().sorted().map { day ->
            ScorePoint(day, ScoreRules.value(pointsUntil(day), max))
        }
        val weekAgo = ScoreRules.value(pointsUntil(today - WEEK_DAYS), max)
        val level = byLevel.entries.firstOrNull { (level, steps) -> days.getValue(level).size < steps.size }?.key
            ?: byLevel.keys.last()
        return Score(
            value = now,
            max = max,
            level = level,
            progressToNext = ScoreRules.progressToNext(points, max),
            gainedThisWeek = now - weekAgo,
            history = history.filter { it.epochDay < today } + ScorePoint(today, now),
            canDo = course.sections.flatMap { it.units }.mapNotNull { units[it] }
                .filter { unit -> unit.steps().let { it.isNotEmpty() && it.all { step -> step in completions } } }
                .map { CanDo(it.id, it.title, it.goals) },
        )
    }
}

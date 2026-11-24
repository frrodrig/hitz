package eus.hitz.core.domain.path

import eus.hitz.core.domain.challenge.ChallengeRules
import eus.hitz.core.model.content.Course
import eus.hitz.core.model.content.CourseUnit
import eus.hitz.core.model.content.Level
import eus.hitz.core.model.content.ReviewScope

/**
 * Builds the learning path from the course and the learner's completions.
 *
 * Progression is linear over the levels of the path (lessons, stories and reviews): every level
 * before the current one is completed, the current one is available and the rest are locked. Star
 * challenges are not steps of the path: each unit's one becomes available when the unit's levels
 * are completed and never blocks progress. [bestAccuracy] holds the best stored accuracy of each
 * completion, from which a challenge's stars are read.
 */
object LearningPathBuilder {
    fun build(
        course: Course,
        units: Map<String, CourseUnit>,
        completed: Set<String>,
        bestAccuracy: Map<String, Float> = emptyMap(),
    ): LearningPath {
        var currentFound = false
        val sections = course.sections.map { section ->
            val pathUnits = section.units.mapNotNull { units[it] }.map { unit ->
                val nodes = unit.levels.filterNot { it is Level.Challenge }.map { level ->
                    val (done, total, next) = level.progress(completed)
                    val isCompleted = total > 0 && done == total
                    val isCurrent = !isCompleted && !currentFound
                    val state = when {
                        isCompleted -> NodeState.Completed
                        currentFound -> NodeState.Locked
                        else -> NodeState.Available
                    }
                    if (isCurrent) currentFound = true
                    PathNode(unit.id, level.id, level.nodeType, level.title, state, isCurrent, done, total, next)
                }
                PathUnit(unit.id, unit.title, unit.goals, nodes, unit.challenge(nodes, completed, bestAccuracy))
            }
            PathSection(section.id, section.title, section.description, pathUnits)
        }
        return LearningPath(sections)
    }

    private fun CourseUnit.challenge(
        nodes: List<PathNode>,
        completed: Set<String>,
        bestAccuracy: Map<String, Float>,
    ): PathChallenge? {
        val level = levels.filterIsInstance<Level.Challenge>().firstOrNull() ?: return null
        val sectionReviews = levels.filter { it is Level.Review && it.scope == ReviewScope.Section }.map { it.id }
        val unitDone = nodes.filterNot { it.levelId in sectionReviews }.all { it.state == NodeState.Completed }
        val stars = if (level.id in completed) ChallengeRules.starsFrom(bestAccuracy[level.id] ?: 1f) else 0
        return PathChallenge(id, level.id, level.title, level.host, isAvailable = unitDone, stars = stars)
    }

    private val Level.nodeType: NodeType
        get() = when (this) {
            is Level.Lessons -> NodeType.Lesson
            is Level.StoryLevel -> NodeType.Story
            is Level.Review, is Level.Challenge -> NodeType.Review
        }

    /** (completed lessons, total lessons, next lesson id) — generated levels count as one lesson. */
    private fun Level.progress(completed: Set<String>): Triple<Int, Int, String?> = when (this) {
        is Level.Lessons -> {
            val ids = lessons.map { it.id }
            val done = ids.count { it in completed }
            Triple(done, ids.size, ids.firstOrNull { it !in completed } ?: ids.firstOrNull())
        }
        else -> Triple(if (id in completed) 1 else 0, 1, null)
    }
}

package eus.hitz.core.domain.path

import eus.hitz.core.model.content.Course
import eus.hitz.core.model.content.CourseUnit
import eus.hitz.core.model.content.Level

/**
 * Builds the learning path from the course and the learner's completions.
 *
 * Progression is linear over the *required* levels (lessons, stories and unit reviews): every
 * required level before the current one is completed, the current one is available and the rest
 * are locked. Legendary levels are optional: they unlock when reached and never block progress.
 */
object LearningPathBuilder {
    fun build(course: Course, units: Map<String, CourseUnit>, completed: Set<String>): LearningPath {
        var currentFound = false
        val sections = course.sections.map { section ->
            val pathUnits = section.units.mapNotNull { units[it] }.map { unit ->
                val nodes = unit.levels.map { level ->
                    val (done, total, next) = level.progress(completed)
                    val isCompleted = total > 0 && done == total
                    val required = level.isRequired
                    val isCurrent = required && !isCompleted && !currentFound
                    val state = when {
                        isCompleted -> NodeState.Completed
                        currentFound -> NodeState.Locked
                        else -> NodeState.Available
                    }
                    if (isCurrent) currentFound = true
                    PathNode(unit.id, level.id, level.nodeType, level.title, state, isCurrent, done, total, next)
                }
                PathUnit(unit.id, unit.title, unit.goals, nodes)
            }
            PathSection(section.id, section.title, section.description, pathUnits)
        }
        return LearningPath(sections)
    }

    private val Level.isRequired: Boolean get() = this !is Level.Legendary

    private val Level.nodeType: NodeType
        get() = when (this) {
            is Level.Lessons -> NodeType.Lesson
            is Level.StoryLevel -> NodeType.Story
            is Level.Review -> NodeType.Review
            is Level.Legendary -> NodeType.Legendary
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

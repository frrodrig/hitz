package eus.hitz.core.domain.path

import eus.hitz.core.model.content.CefrLevel
import eus.hitz.core.model.content.LocalizedText

enum class NodeType { Lesson, Story, Review }

enum class NodeState { Locked, Available, Completed }

/** A node of the learning path. [nextLessonId] is the lesson to play for lesson levels. */
data class PathNode(
    val unitId: String,
    val levelId: String,
    val type: NodeType,
    val title: LocalizedText,
    val state: NodeState,
    val isCurrent: Boolean,
    val completedLessons: Int,
    val totalLessons: Int,
    val nextLessonId: String?,
)

/**
 * A unit's star challenge, shown beside the path halfway through the unit. It is available once
 * every level of the unit is completed (a section review that closes the unit aside), and keeps
 * the best [stars] earned, 0 until it is passed.
 */
data class PathChallenge(
    val unitId: String,
    val levelId: String,
    val title: LocalizedText,
    /** The cast member who presents it: `ane`, `mikel`, `maite` or `unai`. */
    val host: String,
    val isAvailable: Boolean,
    val stars: Int,
)

/**
 * The cumulative review after a textbook unit (see [eus.hitz.core.domain.recall.RecallRules]):
 * optional, available once the unit's levels are completed, and never blocking the path.
 */
data class PathRecall(
    val unitId: String,
    val levelId: String,
    val isAvailable: Boolean,
    val isDone: Boolean,
)

data class PathUnit(
    val id: String,
    val title: LocalizedText,
    val goals: List<LocalizedText>,
    val nodes: List<PathNode>,
    val challenge: PathChallenge? = null,
    val recall: PathRecall? = null,
) {
    val isUnlocked: Boolean get() = nodes.any { it.state != NodeState.Locked }
}

data class PathSection(
    val id: String,
    val title: LocalizedText,
    val description: LocalizedText,
    val units: List<PathUnit>,
    /** CEFR level of the section, as in the course (`A1`, `A2.1`). */
    val level: String = CefrLevel.A1,
) {
    val isCompleted: Boolean get() = units.all { unit -> unit.nodes.all { it.state == NodeState.Completed } }

    val isUnlocked: Boolean get() = units.any { it.isUnlocked }

    val isCurrent: Boolean get() = units.any { unit -> unit.nodes.any { it.isCurrent } }

    val completedUnits: Int get() = units.count { unit -> unit.nodes.all { it.state == NodeState.Completed } }
}

data class LearningPath(val sections: List<PathSection>) {
    /** The section of the current step; the last one when the course is finished. */
    val currentSection: PathSection? get() = sections.firstOrNull { it.isCurrent } ?: sections.lastOrNull()

    val currentNode: PathNode? get() = sections.flatMap { it.units }.flatMap { it.nodes }.firstOrNull { it.isCurrent }

    val challenges: List<PathChallenge> get() = sections.flatMap { it.units }.mapNotNull { it.challenge }
}

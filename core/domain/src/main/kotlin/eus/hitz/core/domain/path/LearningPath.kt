package eus.hitz.core.domain.path

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

data class PathUnit(
    val id: String,
    val title: LocalizedText,
    val goals: List<LocalizedText>,
    val nodes: List<PathNode>,
    val challenge: PathChallenge? = null,
) {
    val isUnlocked: Boolean get() = nodes.any { it.state != NodeState.Locked }
}

data class PathSection(
    val id: String,
    val title: LocalizedText,
    val description: LocalizedText,
    val units: List<PathUnit>,
)

data class LearningPath(val sections: List<PathSection>) {
    val currentNode: PathNode? get() = sections.flatMap { it.units }.flatMap { it.nodes }.firstOrNull { it.isCurrent }

    val challenges: List<PathChallenge> get() = sections.flatMap { it.units }.mapNotNull { it.challenge }
}

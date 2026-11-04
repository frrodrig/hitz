package eus.hitz.feature.path

import eus.hitz.core.domain.path.LearningPath
import eus.hitz.core.domain.path.NodeState
import eus.hitz.core.domain.path.NodeType
import eus.hitz.core.domain.path.PathNode
import eus.hitz.core.domain.path.PathSection
import eus.hitz.core.domain.path.PathUnit
import eus.hitz.core.model.content.LocalizedText

internal object PathTestData {
    private fun t(es: String, eu: String) = LocalizedText(es, eu)

    private fun node(
        unit: String,
        n: Int,
        type: NodeType,
        title: LocalizedText,
        state: NodeState,
        current: Boolean = false,
    ) = PathNode(
        unitId = unit,
        levelId = "$unit.l$n",
        type = type,
        title = title,
        state = state,
        isCurrent = current,
        completedLessons = if (state == NodeState.Completed) 1 else 0,
        totalLessons = 1,
        nextLessonId = "$unit.l$n.s1",
    )

    val path = LearningPath(
        listOf(
            PathSection(
                id = "s1",
                title = t("Primeros pasos", "Lehen urratsak"),
                description = t(
                    "Saludar, presentarse y hablar de la familia.",
                    "Agurtu, aurkeztu eta familiaz hitz egin.",
                ),
                units = listOf(
                    PathUnit(
                        id = "u01-1",
                        title = t("¡Hola!", "Kaixo!"),
                        goals = emptyList(),
                        nodes = listOf(
                            node("u01-1", 1, NodeType.Lesson, t("Saludos", "Agurrak"), NodeState.Completed),
                            node(
                                unit = "u01-1",
                                n = 2,
                                type = NodeType.Lesson,
                                title = t("¿Quién eres?", "Nor zara?"),
                                state = NodeState.Available,
                                current = true,
                            ),
                            node("u01-1", 3, NodeType.Story, t("Historia", "Istorioa"), NodeState.Locked),
                            node("u01-1", 4, NodeType.Review, t("Repaso", "Errepasoa"), NodeState.Locked),
                            node("u01-1", 5, NodeType.Legendary, t("Legendario", "Legendarioa"), NodeState.Locked),
                        ),
                    ),
                    PathUnit(
                        id = "u01-2",
                        title = t("¿Quién eres?", "Nor zara?"),
                        goals = emptyList(),
                        nodes = listOf(
                            node("u01-2", 1, NodeType.Lesson, t("Profesiones", "Lanbideak"), NodeState.Locked),
                        ),
                    ),
                ),
            ),
        ),
    )
}

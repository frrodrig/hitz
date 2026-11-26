package eus.hitz.feature.path

import eus.hitz.core.domain.path.LearningPath
import eus.hitz.core.domain.path.NodeState
import eus.hitz.core.domain.path.NodeType
import eus.hitz.core.domain.path.PathChallenge
import eus.hitz.core.domain.path.PathNode
import eus.hitz.core.domain.path.PathSection
import eus.hitz.core.domain.path.PathUnit
import eus.hitz.core.model.content.LocalizedText

/**
 * A short path for previews: a first section with one unit under way, with its challenge, and one
 * locked, then a locked second section.
 */
internal object PathPreviewData {
    private fun node(unit: String, n: Int, type: NodeType, title: LocalizedText, state: NodeState) = PathNode(
        unitId = unit,
        levelId = "$unit.l$n",
        type = type,
        title = title,
        state = state,
        isCurrent = state == NodeState.Available,
        completedLessons = if (state == NodeState.Completed) 1 else 0,
        totalLessons = 1,
        nextLessonId = null,
    )

    val nextSection = PathSection(
        id = "s2",
        title = LocalizedText("En casa y en la calle", "Etxean eta kalean"),
        description = LocalizedText(
            "Describir la casa, hablar de comida y hacer compras.",
            "Etxea deskribatu, janariaz hitz egin eta erosketak egin.",
        ),
        units = listOf(
            PathUnit(
                id = "u04-1",
                title = LocalizedText("Etxea", "Etxea"),
                goals = emptyList(),
                nodes = listOf(node("u04-1", 1, NodeType.Lesson, LocalizedText("La casa", "Etxea"), NodeState.Locked)),
            ),
        ),
    )

    val path = LearningPath(
        listOf(
            PathSection(
                id = "s1",
                title = LocalizedText("Primeros pasos", "Lehen urratsak"),
                description = LocalizedText("Saludar y presentarse.", "Agurtu eta aurkeztu."),
                units = listOf(
                    PathUnit(
                        id = "u01-1",
                        title = LocalizedText("¡Hola!", "Kaixo!"),
                        goals = listOf(LocalizedText("Saludar y decir cómo te llamas", "Agurtu eta izena esan")),
                        nodes = listOf(
                            node("u01-1", 1, NodeType.Lesson, LocalizedText("Saludos", "Agurrak"), NodeState.Completed),
                            node(
                                unit = "u01-1",
                                n = 2,
                                type = NodeType.Lesson,
                                title = LocalizedText("¿Quién eres?", "Nor zara?"),
                                state = NodeState.Available,
                            ),
                            node("u01-1", 3, NodeType.Story, LocalizedText("Historia", "Istorioa"), NodeState.Locked),
                            node("u01-1", 4, NodeType.Review, LocalizedText("Repaso", "Errepasoa"), NodeState.Locked),
                        ),
                        challenge = PathChallenge(
                            unitId = "u01-1",
                            levelId = "u01-1.l5",
                            title = LocalizedText("Reto de estrellas", "Izarren erronka"),
                            host = "ane",
                            isAvailable = false,
                            stars = 0,
                        ),
                    ),
                    PathUnit(
                        id = "u01-2",
                        title = LocalizedText("¿De dónde eres?", "Nongoa zara?"),
                        goals = emptyList(),
                        nodes = listOf(
                            node(
                                unit = "u01-2",
                                n = 1,
                                type = NodeType.Lesson,
                                title = LocalizedText("Países", "Herrialdeak"),
                                state = NodeState.Locked,
                            ),
                        ),
                        challenge = PathChallenge(
                            unitId = "u01-2",
                            levelId = "u01-2.l5",
                            title = LocalizedText("Reto de estrellas", "Izarren erronka"),
                            host = "mikel",
                            isAvailable = false,
                            stars = 0,
                        ),
                    ),
                ),
            ),
            nextSection,
        ),
    )
}

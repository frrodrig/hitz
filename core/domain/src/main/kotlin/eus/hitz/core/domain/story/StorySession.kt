package eus.hitz.core.domain.story

import eus.hitz.core.model.content.Story

/** A step of a story: a line to read, or a comprehension question. */
sealed interface StoryStep {
    data class Line(val index: Int) : StoryStep

    data class Question(val index: Int) : StoryStep
}

/**
 * Story flow, independent of the UI. Lines are revealed one by one; a question appears after the
 * line it refers to. A wrong option is marked and the learner tries again until the right one is
 * chosen: stories cost no hearts. Accuracy counts questions answered right at the first try.
 */
data class StorySession(
    val story: Story,
    val steps: List<StoryStep>,
    val position: Int = 0,
    /** Wrong options already tried in the current question. */
    val wrongOptions: Set<Int> = emptySet(),
    /** True once the current question has been answered correctly. */
    val answered: Boolean = false,
    val firstTryCorrect: Int = 0,
) {
    val current: StoryStep? get() = steps.getOrNull(position)

    val isOver: Boolean get() = position >= steps.size

    val questionCount: Int get() = story.questions.size

    val accuracy: Float get() = if (questionCount == 0) 1f else firstTryCorrect.toFloat() / questionCount

    /** From 0 to 1: share of steps done. */
    val progress: Float get() = if (steps.isEmpty()) 1f else position.toFloat() / steps.size

    /** Lines revealed so far (the current one included). */
    val visibleLines: Int
        get() = steps.take(position + 1).filterIsInstance<StoryStep.Line>().maxOfOrNull { it.index + 1 } ?: 0

    /** Moves past the current line, or past a question once it is answered. */
    fun next(): StorySession = when (current) {
        is StoryStep.Line -> copy(position = position + 1)
        is StoryStep.Question -> nextQuestion()
        null -> this
    }

    private fun nextQuestion(): StorySession =
        if (answered) copy(position = position + 1, wrongOptions = emptySet(), answered = false) else this

    fun answer(option: Int): StorySession {
        val step = current as? StoryStep.Question ?: return this
        if (answered || option in wrongOptions) return this
        val question = story.questions[step.index]
        return if (option == question.correct) {
            copy(answered = true, firstTryCorrect = firstTryCorrect + if (wrongOptions.isEmpty()) 1 else 0)
        } else {
            copy(wrongOptions = wrongOptions + option)
        }
    }

    companion object {
        fun start(story: Story): StorySession {
            val steps = buildList {
                story.lines.indices.forEach { line ->
                    add(StoryStep.Line(line))
                    story.questions.forEachIndexed { index, question ->
                        if (question.afterLine == line) add(StoryStep.Question(index))
                    }
                }
            }
            return StorySession(story, steps)
        }
    }
}

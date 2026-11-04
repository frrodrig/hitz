package eus.hitz.core.domain.story

import com.google.common.truth.Truth.assertThat
import eus.hitz.core.model.content.Story
import eus.hitz.core.model.content.StoryLine
import eus.hitz.core.model.content.StoryQuestion
import org.junit.Test

class StorySessionTest {
    private val story = Story(
        id = "u01-1.l3.story",
        title = "Lehen eguna",
        lines = listOf(
            StoryLine("Ane", "Kaixo!", "¡Hola!"),
            StoryLine("Iker", "Kaixo! Zer moduz?", "¡Hola! ¿Qué tal?"),
            StoryLine("Ane", "Ondo!", "¡Bien!"),
        ),
        questions = listOf(
            StoryQuestion(afterLine = 1, prompt = "¿Qué dice Iker?", listOf("Qué tal", "Quién es"), correct = 0),
            StoryQuestion(afterLine = 2, prompt = "¿Cómo está Ane?", options = listOf("Mal", "Bien"), correct = 1),
        ),
    )

    @Test
    fun `questions follow the line they refer to`() {
        val steps = StorySession.start(story).steps
        assertThat(steps).containsExactly(
            StoryStep.Line(0),
            StoryStep.Line(1),
            StoryStep.Question(0),
            StoryStep.Line(2),
            StoryStep.Question(1),
        ).inOrder()
    }

    @Test
    fun `lines are revealed one by one`() {
        val session = StorySession.start(story)
        assertThat(session.visibleLines).isEqualTo(1)
        val atQuestion = session.next().next()
        assertThat(atQuestion.current).isEqualTo(StoryStep.Question(0))
        assertThat(atQuestion.visibleLines).isEqualTo(2)
    }

    @Test
    fun `a question blocks until it is answered correctly`() {
        val atQuestion = StorySession.start(story).next().next()
        assertThat(atQuestion.next()).isEqualTo(atQuestion)
        val wrong = atQuestion.answer(1)
        assertThat(wrong.wrongOptions).containsExactly(1)
        assertThat(wrong.answered).isFalse()
        val right = wrong.answer(0)
        assertThat(right.answered).isTrue()
        assertThat(right.firstTryCorrect).isEqualTo(0)
        assertThat(right.next().current).isEqualTo(StoryStep.Line(2))
    }

    @Test
    fun `accuracy counts first-try answers`() {
        val done = StorySession.start(story)
            .next().next()
            .answer(0).next()
            .next()
            .answer(0).answer(1).next()
        assertThat(done.isOver).isTrue()
        assertThat(done.progress).isEqualTo(1f)
        assertThat(done.accuracy).isEqualTo(0.5f)
    }
}

package eus.hitz.core.domain.lesson

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class LessonSessionTest {
    private fun mistake(id: String) = Mistake(id, listOf("kaixo"), listOf("greetings"), "x", "y")

    @Test
    fun `correct answers advance and fill the progress`() {
        val session = LessonSession.start("l", listOf("a", "b"), mistakesAllowed = 5).answerCorrect()
        assertThat(session.current?.exerciseId).isEqualTo("b")
        assertThat(session.progress).isEqualTo(0.5f)
    }

    @Test
    fun `a wrong answer uses up an allowed mistake and is asked again at the end`() {
        val session = LessonSession.start("l", listOf("a", "b"), mistakesAllowed = 5).answerIncorrect(mistake("a"))
        assertThat(session.mistakesLeft).isEqualTo(4)
        assertThat(session.progress).isEqualTo(0f)
        assertThat(session.queue.map { it.exerciseId }).containsExactly("a", "b", "a").inOrder()
        val finished = session.answerCorrect().answerCorrect()
        assertThat(finished.isOver).isTrue()
        val result = finished.result()
        assertThat(result.completed).isTrue()
        assertThat(result.firstTryCorrect).isEqualTo(1)
        assertThat(result.accuracy).isEqualTo(0.5f)
        assertThat(result.mistakes.map { it.exerciseId }).containsExactly("a")
    }

    @Test
    fun `without retries a wrong answer is final`() {
        val session = LessonSession.start("l", listOf("a", "b"), mistakesAllowed = null, retries = false)
            .answerIncorrect(mistake("a"))
            .answerCorrect()
        assertThat(session.isOver).isTrue()
        assertThat(session.result().accuracy).isEqualTo(0.5f)
    }

    @Test
    fun `an exercise is asked again until it is answered correctly`() {
        val session = LessonSession.start("l", listOf("a"), mistakesAllowed = null)
            .answerIncorrect(mistake("a"))
            .answerIncorrect(mistake("a"))
        assertThat(session.isOver).isFalse()
        assertThat(session.queue).hasSize(3)
        val done = session.answerCorrect()
        assertThat(done.result().completed).isTrue()
        assertThat(done.result().firstTryCorrect).isEqualTo(0)
        assertThat(done.result().mistakes).hasSize(2)
    }

    @Test
    fun `going over the mistakes allowed ends the lesson as not completed`() {
        val atTheLimit = LessonSession.start("l", listOf("a", "b", "c"), mistakesAllowed = 1)
            .answerIncorrect(mistake("a"))
        assertThat(atTheLimit.mistakesLeft).isEqualTo(0)
        assertThat(atTheLimit.isOutOfMistakes).isFalse()
        val session = atTheLimit.answerIncorrect(mistake("b"))
        assertThat(session.isOutOfMistakes).isTrue()
        assertThat(session.current).isNull()
        assertThat(session.result().completed).isFalse()
    }

    @Test
    fun `without a limit mistakes never end the lesson`() {
        var session = LessonSession.start("l", listOf("a"), mistakesAllowed = null)
        session = session.answerIncorrect(mistake("a"))
        assertThat(session.isOutOfMistakes).isFalse()
        assertThat(session.current?.isRetry).isTrue()
    }

    @Test
    fun `recorded mistakes use up no allowed mistake`() {
        val session = LessonSession.start("l", listOf("a"), mistakesAllowed = 5).recordMistake(mistake("a"))
        assertThat(session.mistakesLeft).isEqualTo(5)
        assertThat(session.current?.exerciseId).isEqualTo("a")
        assertThat(session.mistakes).hasSize(1)
    }

    @Test
    fun `a skipped exercise leaves the lesson without counting`() {
        val session = LessonSession.start("l", listOf("a", "b"), mistakesAllowed = 5).skip()
        assertThat(session.current?.exerciseId).isEqualTo("b")
        assertThat(session.progress).isEqualTo(0f)
        val result = session.answerCorrect().result()
        assertThat(result.completed).isTrue()
        assertThat(result.exerciseCount).isEqualTo(1)
        assertThat(result.accuracy).isEqualTo(1f)
        assertThat(result.mistakesLeft).isEqualTo(5)
    }

    @Test
    fun `skipping a retried exercise removes it from the queue`() {
        val session = LessonSession.start("l", listOf("a", "b"), mistakesAllowed = 5)
            .answerIncorrect(mistake("a"))
            .answerCorrect()
            .skip()
        assertThat(session.isOver).isTrue()
        assertThat(session.result().exerciseCount).isEqualTo(1)
    }
}

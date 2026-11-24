package eus.hitz.core.domain.challenge

import com.google.common.truth.Truth.assertThat
import eus.hitz.core.domain.progress.MistakeStats
import eus.hitz.core.model.content.Exercise
import eus.hitz.core.model.content.Language
import eus.hitz.core.model.content.Phrase
import org.junit.Test
import kotlin.random.Random

class ChallengeRulesTest {
    private fun typing(id: String) = Exercise.TranslateTyping(id, Phrase(Language.Spanish, "Hola"), listOf("Kaixo"))

    private fun order(id: String) = Exercise.OrderSentence(id, "Kaixo, Ane", "Hola, Ane")

    private fun wordBank(id: String) = Exercise.TranslateWordBank(id, Phrase(Language.Spanish, "Hola"), listOf("Kaixo"))

    private fun meaning(id: String) = Exercise.ChooseMeaning(id, "Kaixo", listOf("Hola", "Adiós"), 0)

    /** Ten exercises of each of four types, from the hardest to the easiest, and two never played. */
    private val pool = (1..10).map { typing("t$it") } + (1..10).map { order("o$it") } +
        (1..10).map { wordBank("w$it") } + (1..10).map { meaning("m$it") } +
        Exercise.WordIntro("i1", "kaixo", "hola") + Exercise.Speak("s1", "Kaixo", "Hola")

    @Test
    fun `stars go down with every mistake`() {
        assertThat((0..3).map(ChallengeRules::stars)).containsExactly(3, 2, 1, 0).inOrder()
        assertThat(ChallengeRules.starsFrom(ChallengeRules.storedAccuracy(2))).isEqualTo(2)
    }

    @Test
    fun `a challenge has thirty exercises that can cost a star, of varied types`() {
        val challenge = ChallengeRules.select(pool, MistakeStats(), Random(1))
        assertThat(challenge).hasSize(ChallengeRules.SIZE)
        assertThat(challenge.none { it is Exercise.WordIntro || it is Exercise.Speak }).isTrue()
        val perType = challenge.groupingBy { it.id.first() }.eachCount()
        // The hardest kinds fill their places first; the easiest one takes what is left.
        assertThat(perType).containsExactly('t', 8, 'o', 8, 'w', 8, 'm', 6)
    }

    @Test
    fun `the learner's mistakes come in first, then the hardest exercises`() {
        val stats = MistakeStats(byExercise = mapOf("m7" to 2))
        val challenge = ChallengeRules.select(pool, stats, Random(1)).map { it.id }
        assertThat(challenge).contains("m7")
        assertThat(challenge.count { it.startsWith("t") }).isEqualTo(ChallengeRules.PER_TYPE)
    }

    @Test
    fun `every try is different`() {
        val first = ChallengeRules.select(pool, MistakeStats(), Random(1))
        val second = ChallengeRules.select(pool, MistakeStats(), Random(2))
        assertThat(first).isNotEqualTo(second)
    }
}

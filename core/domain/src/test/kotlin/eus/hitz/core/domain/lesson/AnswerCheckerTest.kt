package eus.hitz.core.domain.lesson

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class AnswerCheckerTest {
    private val checker = AnswerChecker()
    private val accepted = listOf("¡Hola! Soy Ane.", "¡Hola! Yo soy Ane.")

    @Test
    fun `normalization ignores case, accents and punctuation`() {
        assertThat(AnswerNormalizer.normalize("  ¿Qué TAL,   Ane?  ")).isEqualTo("que tal ane")
        assertThat(AnswerNormalizer.normalize("Iñaki")).isEqualTo("iñaki")
    }

    @Test
    fun `any accepted answer is correct`() {
        assertThat(checker.check("hola yo soy ane", accepted)).isEqualTo(AnswerResult.Correct("¡Hola! Yo soy Ane."))
        assertThat(checker.check("Hola, soy Ane", accepted)).isEqualTo(AnswerResult.Correct("¡Hola! Soy Ane."))
    }

    @Test
    fun `a small typo is almost correct and shows the right spelling`() {
        assertThat(
            checker.check("Eskerik asko", listOf("Eskerrik asko."))
        ).isEqualTo(AnswerResult.AlmostCorrect("Eskerrik asko."))
        assertThat(checker.check("hola sy ane", accepted)).isEqualTo(AnswerResult.AlmostCorrect("¡Hola! Soy Ane."))
    }

    @Test
    fun `swapping two letters counts as one typo`() {
        assertThat(
            checker.check("eskerrik aksó", listOf("Eskerrik asko."))
        ).isInstanceOf(AnswerResult.AlmostCorrect::class.java)
    }

    @Test
    fun `short words allow no typo`() {
        assertThat(checker.check("ez", listOf("bai"))).isEqualTo(AnswerResult.Incorrect("bai"))
        assertThat(checker.check("zu", listOf("ni"))).isInstanceOf(AnswerResult.Incorrect::class.java)
    }

    @Test
    fun `a different word is incorrect and reports the canonical answer`() {
        assertThat(checker.check("Adiós, soy Ane", accepted)).isEqualTo(AnswerResult.Incorrect("¡Hola! Soy Ane."))
    }

    @Test
    fun `blank input is incorrect`() {
        assertThat(checker.check("  ", accepted).isAccepted).isFalse()
    }

    @Test
    fun `edit distance`() {
        assertThat(editDistance("naiz", "naiz")).isEqualTo(0)
        assertThat(editDistance("naiz", "niaz")).isEqualTo(1)
        assertThat(editDistance("kaixo", "kaio")).isEqualTo(1)
        assertThat(editDistance("", "abc")).isEqualTo(3)
    }
}

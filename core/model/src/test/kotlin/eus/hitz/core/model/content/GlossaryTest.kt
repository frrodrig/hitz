package eus.hitz.core.model.content

import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class GlossaryTest {
    private val glossary = Glossary(
        entries = mapOf(
            "egun on" to Gloss("buenos días"),
            "egun" to Gloss("día"),
            "ogia" to Gloss("pan", lemma = "ogi"),
            "liburu-denda" to Gloss("librería"),
        ),
    )

    @Test
    fun multiWordExpressionsWinOverTheirWords() {
        val runs = glossary.segment("Egun on, Ane!")

        assertEquals(listOf("Egun on", ", Ane!"), runs.map { it.text })
        assertEquals(Gloss("buenos días"), runs[0].gloss)
        assertNull(runs[1].gloss)
    }

    @Test
    fun runsRebuildTheTextAndMatchAnyCase() {
        val text = "OGIA eta egun bat liburu-dendan? Ogia!"
        val runs = glossary.segment(text)

        assertEquals(text, runs.joinToString("") { it.text })
        assertEquals(listOf("OGIA", "egun", "Ogia"), runs.filter { it.gloss != null }.map { it.text })
    }

    @Test
    fun anExpressionSplitByPunctuationIsTwoWords() {
        val runs = glossary.segment("Egun, on")

        assertEquals(Gloss("día"), runs.first().gloss)
        assertEquals(listOf("Egun"), runs.filter { it.gloss != null }.map { it.text })
    }

    @Test
    fun hyphenatedWordsAreOneWord() {
        assertEquals(Gloss("librería"), glossary.segment("liburu-denda").single().gloss)
    }

    @Test
    fun anEmptyGlossaryHasNoHints() {
        assertEquals(listOf(HintedRun("Kaixo!", null)), Glossary.Empty.segment("Kaixo!"))
    }
}

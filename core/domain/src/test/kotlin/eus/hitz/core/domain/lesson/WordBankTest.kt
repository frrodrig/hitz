package eus.hitz.core.domain.lesson

import com.google.common.truth.Truth.assertThat
import org.junit.Test
import kotlin.random.Random

class WordBankTest {
    @Test
    fun `tiles are the answer words without punctuation plus distractors`() {
        val tiles = WordBank.tiles("Egun on, zer moduz?", listOf("gabon", "agur"), Random(1))
        assertThat(tiles.map { it.text }).containsExactly("Egun", "on", "zer", "moduz", "gabon", "agur")
        assertThat(tiles.map { it.id }).containsExactly(0, 1, 2, 3, 4, 5).inOrder()
    }

    @Test
    fun `selected tiles form the answer in order`() {
        val tiles = listOf(Tile(0, "moduz"), Tile(1, "Zer"), Tile(2, "agur"))
        assertThat(WordBank.answer(tiles, listOf(1, 0))).isEqualTo("Zer moduz")
    }

    @Test
    fun `the same seed gives the same order`() {
        val a = WordBank.tiles("Ni Ane naiz", listOf("zara"), Random(7))
        val b = WordBank.tiles("Ni Ane naiz", listOf("zara"), Random(7))
        assertThat(a).isEqualTo(b)
    }
}

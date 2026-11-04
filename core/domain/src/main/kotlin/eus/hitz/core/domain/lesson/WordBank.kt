package eus.hitz.core.domain.lesson

import kotlin.random.Random

/** A tile of a word bank. [id] is its position in the bank. */
data class Tile(val id: Int, val text: String)

/** Builds word-bank tiles: the words of the canonical answer plus the distractors, shuffled. */
object WordBank {
    private val edgePunctuation = Regex("^[¡¿\\p{Punct}«»]+|[\\p{Punct}«»]+$")

    fun words(sentence: String): List<String> =
        sentence.split(' ').map { it.replace(edgePunctuation, "") }.filter { it.isNotEmpty() }

    fun tiles(canonicalAnswer: String, distractors: List<String>, random: Random): List<Tile> =
        (words(canonicalAnswer) + distractors)
            .shuffled(random)
            .mapIndexed { index, text -> Tile(index, text) }

    /** The sentence formed by the selected tiles, in order. */
    fun answer(tiles: List<Tile>, selected: List<Int>): String =
        selected.joinToString(" ") { id -> tiles.first { it.id == id }.text }
}

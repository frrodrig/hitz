package eus.hitz.core.model.content

import kotlinx.serialization.Serializable

/**
 * Spanish hints for the Basque words of a course (`courses/<id>/glossary.json`), shown when the
 * learner taps a word. Keys are lower-case words or multi-word lemmas (`egun on`).
 */
@Serializable
data class Glossary(
    val schemaVersion: Int = 1,
    val entries: Map<String, Gloss> = emptyMap(),
) {
    /**
     * Splits [text] into runs: words (or multi-word expressions) with their hint, and everything
     * else (spaces, punctuation, words without a hint) with none. Joining the runs gives [text].
     */
    fun segment(text: String): List<HintedRun> {
        val words = WORD.findAll(text).toList()
        val runs = mutableListOf<HintedRun>()
        var cursor = 0
        var index = 0
        while (index < words.size) {
            val match = (MAX_PHRASE downTo 1).asSequence()
                .filter { index + it <= words.size }
                .map { size -> words.subList(index, index + size) }
                .filter { group ->
                    group.zipWithNext().all { (a, b) ->
                        text.substring(
                            a.range.last + 1,
                            b.range.first
                        ).isBlank()
                    }
                }
                .firstNotNullOfOrNull { group ->
                    entries[group.joinToString(" ") { it.value.lowercase() }]?.let { group to it }
                }
            if (match == null) {
                index++
                continue
            }
            val (group, gloss) = match
            val start = group.first().range.first
            val end = group.last().range.last + 1
            if (start > cursor) runs += HintedRun(text.substring(cursor, start), null)
            runs += HintedRun(text.substring(start, end), gloss)
            cursor = end
            index += group.size
        }
        if (cursor < text.length) runs += HintedRun(text.substring(cursor), null)
        return runs
    }

    companion object {
        val Empty = Glossary()
        private const val MAX_PHRASE = 4
        private val WORD = Regex("[\\p{L}\\p{N}]+(?:-[\\p{L}\\p{N}]+)*")
    }
}

/** A hint: the Spanish meaning and, for an inflected word, its dictionary form. */
@Serializable
data class Gloss(
    val es: String,
    val lemma: String? = null,
)

/** A piece of text and the hint shown when it is tapped, if any. */
data class HintedRun(val text: String, val gloss: Gloss?)

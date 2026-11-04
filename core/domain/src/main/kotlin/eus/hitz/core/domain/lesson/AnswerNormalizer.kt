package eus.hitz.core.domain.lesson

import java.util.Locale

/**
 * Normalizes free-text answers before comparing them: lower case, no accents on vowels
 * (á → a, ü → u), no punctuation and single spaces. `ñ` and `ç` are kept because they are
 * different letters.
 */
object AnswerNormalizer {
    private val accents = mapOf(
        'á' to 'a', 'à' to 'a', 'â' to 'a', 'ä' to 'a',
        'é' to 'e', 'è' to 'e', 'ê' to 'e', 'ë' to 'e',
        'í' to 'i', 'ì' to 'i', 'î' to 'i', 'ï' to 'i',
        'ó' to 'o', 'ò' to 'o', 'ô' to 'o', 'ö' to 'o',
        'ú' to 'u', 'ù' to 'u', 'û' to 'u', 'ü' to 'u',
    )
    private val whitespace = Regex("\\s+")

    fun normalize(text: String): String {
        val builder = StringBuilder(text.length)
        for (char in text.lowercase(Locale.ROOT)) {
            val mapped = accents[char] ?: char
            builder.append(if (mapped.isLetterOrDigit()) mapped else ' ')
        }
        return builder.toString().trim().replace(whitespace, " ")
    }
}

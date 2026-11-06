package eus.hitz.core.model.content

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/** Language of a piece of exercise text. */
@Serializable
enum class Language {
    @SerialName("eu")
    Basque,

    @SerialName("es")
    Spanish,
}

@Serializable
data class Phrase(
    val lang: Language,
    val text: String,
)

@Serializable
data class ImageOption(
    val text: String,
    /** File name in `assets/images/`. */
    val image: String,
)

@Serializable
data class WordPair(
    val eu: String,
    val es: String,
)

@Serializable
data class ChatMessage(
    val speaker: String,
    val text: String,
)

/**
 * One exercise. Every type shares [id], the syllabus lemmas it practises ([vocabulary]) and the
 * grammar points it practises ([structures]); both feed spaced repetition. Accepted answers are
 * lists: the first one is canonical and is shown as the correction.
 */
@Serializable
sealed interface Exercise {
    val id: String
    val vocabulary: List<String>
    val structures: List<String>

    /** Translate by tapping tiles: the answer's words plus [distractors]. */
    @Serializable
    @SerialName("translate_wordbank")
    data class TranslateWordBank(
        override val id: String,
        val prompt: Phrase,
        val answers: List<String>,
        val distractors: List<String> = emptyList(),
        override val vocabulary: List<String> = emptyList(),
        override val structures: List<String> = emptyList(),
    ) : Exercise

    /** Translate by typing. */
    @Serializable
    @SerialName("translate_typing")
    data class TranslateTyping(
        override val id: String,
        val prompt: Phrase,
        val answers: List<String>,
        override val vocabulary: List<String> = emptyList(),
        override val structures: List<String> = emptyList(),
    ) : Exercise

    /** Pick the image that matches a Spanish prompt; options are labelled in Basque. */
    @Serializable
    @SerialName("choose_image")
    data class ChooseImage(
        override val id: String,
        val prompt: String,
        val options: List<ImageOption>,
        val correct: Int,
        override val vocabulary: List<String> = emptyList(),
        override val structures: List<String> = emptyList(),
    ) : Exercise

    /** Match Basque and Spanish words. */
    @Serializable
    @SerialName("match_pairs")
    data class MatchPairs(
        override val id: String,
        val pairs: List<WordPair>,
        override val vocabulary: List<String> = emptyList(),
        override val structures: List<String> = emptyList(),
    ) : Exercise

    /** Listen and tap the words in order. */
    @Serializable
    @SerialName("listen_tap")
    data class ListenTap(
        override val id: String,
        val text: String,
        val distractors: List<String> = emptyList(),
        override val vocabulary: List<String> = emptyList(),
        override val structures: List<String> = emptyList(),
    ) : Exercise

    /** Listen and type what you hear. */
    @Serializable
    @SerialName("listen_type")
    data class ListenType(
        override val id: String,
        val text: String,
        val answers: List<String>,
        override val vocabulary: List<String> = emptyList(),
        override val structures: List<String> = emptyList(),
    ) : Exercise

    /** Complete the gap (`___`) in a Basque sentence. */
    @Serializable
    @SerialName("fill_blank")
    data class FillBlank(
        override val id: String,
        val sentence: String,
        val options: List<String>,
        val correct: Int,
        val translation: String,
        override val vocabulary: List<String> = emptyList(),
        override val structures: List<String> = emptyList(),
    ) : Exercise

    /** Choose the Spanish meaning of a Basque word or sentence. */
    @Serializable
    @SerialName("choose_meaning")
    data class ChooseMeaning(
        override val id: String,
        val text: String,
        val options: List<String>,
        val correct: Int,
        override val vocabulary: List<String> = emptyList(),
        override val structures: List<String> = emptyList(),
    ) : Exercise

    /** Pick the right reply to complete a chat. */
    @Serializable
    @SerialName("complete_chat")
    data class CompleteChat(
        override val id: String,
        val messages: List<ChatMessage>,
        val options: List<String>,
        val correct: Int,
        override val vocabulary: List<String> = emptyList(),
        override val structures: List<String> = emptyList(),
    ) : Exercise

    /** Read a Basque passage and say whether the Spanish statement is true. */
    @Serializable
    @SerialName("read_true_false")
    data class ReadTrueFalse(
        override val id: String,
        val passage: String,
        val statement: String,
        val answer: Boolean,
        override val vocabulary: List<String> = emptyList(),
        override val structures: List<String> = emptyList(),
    ) : Exercise

    /** Put the words of a Basque sentence in order. */
    @Serializable
    @SerialName("order_sentence")
    data class OrderSentence(
        override val id: String,
        val answer: String,
        val translation: String,
        override val vocabulary: List<String> = emptyList(),
        override val structures: List<String> = emptyList(),
    ) : Exercise

    /**
     * A new word, shown before its first use: the word with its audio, its Spanish translation and
     * an [image] when there is one. There is nothing to answer; the learner continues.
     */
    @Serializable
    @SerialName("word_intro")
    data class WordIntro(
        override val id: String,
        val word: String,
        val translation: String,
        /** File name in `assets/images/`. */
        val image: String? = null,
        override val vocabulary: List<String> = emptyList(),
        override val structures: List<String> = emptyList(),
    ) : Exercise

    /** Say a sentence aloud (optional; the learner can turn speaking exercises off). */
    @Serializable
    @SerialName("speak")
    data class Speak(
        override val id: String,
        val text: String,
        val translation: String,
        override val vocabulary: List<String> = emptyList(),
        override val structures: List<String> = emptyList(),
    ) : Exercise
}

package eus.hitz.core.model

/**
 * Course identifier in the form `<learning-language>-<base-language>`,
 * e.g. `eu-es` (Basque for Spanish speakers).
 */
@JvmInline
value class CourseId(val value: String) {
    init {
        require(PATTERN.matches(value)) { "Invalid CourseId: '$value'" }
    }

    val learningLanguage: String get() = value.substringBefore('-')
    val fromLanguage: String get() = value.substringAfter('-')

    override fun toString(): String = value

    companion object {
        private val PATTERN = Regex("^[a-z]{2}-[a-z]{2}$")
        val BASQUE_FROM_SPANISH = CourseId("eu-es")
    }
}

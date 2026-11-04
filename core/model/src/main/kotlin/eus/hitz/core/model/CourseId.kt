package eus.hitz.core.model

/**
 * Identificador de un curso con el formato `<idioma-aprendido>-<idioma-base>`,
 * por ejemplo `eu-es` (euskera desde castellano).
 */
@JvmInline
value class CourseId(val value: String) {
    init {
        require(PATTERN.matches(value)) { "CourseId no válido: '$value'" }
    }

    val learningLanguage: String get() = value.substringBefore('-')
    val fromLanguage: String get() = value.substringAfter('-')

    override fun toString(): String = value

    companion object {
        private val PATTERN = Regex("^[a-z]{2}-[a-z]{2}$")
        val BASQUE_FROM_SPANISH = CourseId("eu-es")
    }
}

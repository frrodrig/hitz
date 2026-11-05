package eus.hitz.feature.lesson

import eus.hitz.core.domain.audio.AudioProvider
import eus.hitz.core.domain.audio.SoundEffects
import eus.hitz.core.model.content.Exercise
import eus.hitz.core.model.content.Language
import javax.inject.Inject

/** The Basque voice and the sound effects of a session. */
class LessonAudio @Inject constructor(
    val voice: AudioProvider,
    val effects: SoundEffects,
)

/**
 * Basque audio that goes with an exercise. Ids follow docs/content-guide.md ("Audio") and
 * tools/validate_content.py, so recorded clips are found.
 */
internal object ExerciseClips {
    /** Played when the exercise appears (and with the speaker button): a Basque prompt. */
    fun prompt(exercise: Exercise): AudioClip? = when (exercise) {
        is Exercise.ListenTap -> AudioClip(exercise.id, exercise.text)
        is Exercise.ListenType -> AudioClip(exercise.id, exercise.text)
        is Exercise.ChooseMeaning -> AudioClip(exercise.id, exercise.text)
        is Exercise.Speak -> AudioClip(exercise.id, exercise.text)
        is Exercise.TranslateWordBank ->
            AudioClip(exercise.id, exercise.prompt.text).takeIf { exercise.prompt.lang == Language.Basque }
        is Exercise.TranslateTyping ->
            AudioClip(exercise.id, exercise.prompt.text).takeIf { exercise.prompt.lang == Language.Basque }
        else -> null
    }

    /** Played after checking, so the learner hears the Basque answer. */
    fun answer(exercise: Exercise): AudioClip? = when (exercise) {
        is Exercise.TranslateWordBank ->
            AudioClip(exercise.id, exercise.answers.first()).takeIf { exercise.prompt.lang == Language.Spanish }
        is Exercise.TranslateTyping ->
            AudioClip(exercise.id, exercise.answers.first()).takeIf { exercise.prompt.lang == Language.Spanish }
        is Exercise.OrderSentence -> AudioClip(exercise.id, exercise.answer)
        is Exercise.FillBlank ->
            AudioClip(exercise.id, exercise.sentence.replace(BLANK, exercise.options[exercise.correct]))
        is Exercise.CompleteChat -> AudioClip("${exercise.id}.answer", exercise.options[exercise.correct])
        else -> null
    }
}

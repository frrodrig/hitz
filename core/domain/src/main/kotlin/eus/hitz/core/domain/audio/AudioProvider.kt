package eus.hitz.core.domain.audio

/**
 * Plays Basque audio. Clips are pre-recorded and named by id (see docs/content-guide.md); when a
 * clip is not bundled yet, the implementation speaks [text] with text-to-speech instead.
 */
interface AudioProvider {
    /** Plays a clip; [slow] plays it at a slower pace for listening practice. */
    fun play(clipId: String, text: String, slow: Boolean = false)

    fun stop()
}

/** Short sounds for answers and achievements. */
enum class SoundEffect { Correct, Incorrect, Finished }

interface SoundEffects {
    fun play(effect: SoundEffect)
}

package eus.hitz.core.domain.audio

/**
 * Plays Basque audio. Clips are pre-recorded and named by id (see docs/content-guide.md); when a
 * clip is not bundled yet, the implementation speaks [text] with text-to-speech instead.
 */
interface AudioProvider {
    fun play(clipId: String, text: String)

    fun stop()
}

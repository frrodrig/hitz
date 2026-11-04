package eus.hitz.core.data.audio

import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.speech.tts.TextToSpeech
import android.util.Log
import dagger.hilt.android.qualifiers.ApplicationContext
import eus.hitz.core.domain.audio.AudioProvider
import java.io.IOException
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Plays `assets/audio/<clipId>.ogg` when it is bundled, and otherwise speaks the text with the
 * system text-to-speech engine: in Basque when the engine has it, else with Spanish voice, whose
 * pronunciation is close enough for a fallback. Only one clip plays at a time.
 */
@Singleton
internal class AndroidAudioProvider @Inject constructor(
    @ApplicationContext private val context: Context,
) : AudioProvider {
    private val clips: Set<String> by lazy {
        context.assets.list(AUDIO_DIR).orEmpty().map { it.removeSuffix(CLIP_EXTENSION) }.toSet()
    }
    private var player: MediaPlayer? = null
    private var tts: TextToSpeech? = null
    private var ttsReady = false
    private var pendingText: String? = null

    override fun play(clipId: String, text: String) {
        stop()
        if (clipId in clips && playClip(clipId)) return
        speak(text)
    }

    override fun stop() {
        player?.release()
        player = null
        tts?.stop()
        pendingText = null
    }

    private fun playClip(clipId: String): Boolean = try {
        context.assets.openFd("$AUDIO_DIR/$clipId$CLIP_EXTENSION").use { fd ->
            player = MediaPlayer().apply {
                setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                        .build(),
                )
                setDataSource(fd.fileDescriptor, fd.startOffset, fd.length)
                setOnCompletionListener { finished ->
                    finished.release()
                    if (player === finished) player = null
                }
                prepare()
                start()
            }
        }
        true
    } catch (e: IOException) {
        Log.w(TAG, "Cannot play clip $clipId", e)
        false
    }

    private fun speak(text: String) {
        val engine = tts
        if (engine == null) {
            pendingText = text
            tts = TextToSpeech(context) { status -> onTtsInit(status) }
        } else if (ttsReady) {
            engine.speak(text, TextToSpeech.QUEUE_FLUSH, null, UTTERANCE_ID)
        } else {
            pendingText = text
        }
    }

    private fun onTtsInit(status: Int) {
        val engine = tts ?: return
        if (status != TextToSpeech.SUCCESS) {
            Log.w(TAG, "Text-to-speech is not available")
            return
        }
        val basque = engine.setLanguage(BASQUE)
        if (basque == TextToSpeech.LANG_MISSING_DATA || basque == TextToSpeech.LANG_NOT_SUPPORTED) {
            engine.setLanguage(SPANISH)
        }
        ttsReady = true
        pendingText?.let { engine.speak(it, TextToSpeech.QUEUE_FLUSH, null, UTTERANCE_ID) }
        pendingText = null
    }

    private companion object {
        const val TAG = "AudioProvider"
        const val AUDIO_DIR = "audio"
        const val CLIP_EXTENSION = ".ogg"
        const val UTTERANCE_ID = "hitz"
        val BASQUE: Locale = Locale.forLanguageTag("eu-ES")
        val SPANISH: Locale = Locale.forLanguageTag("es-ES")
    }
}

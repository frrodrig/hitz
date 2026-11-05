package eus.hitz.core.data.audio

import android.content.Context
import android.content.res.AssetFileDescriptor
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.media.PlaybackParams
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
    private var pending: Pair<String, Boolean>? = null

    override fun play(clipId: String, text: String, slow: Boolean) {
        stop()
        if (clipId in clips && playClip(clipId, slow)) return
        speak(text, slow)
    }

    override fun stop() {
        player?.release()
        player = null
        tts?.stop()
        pending = null
    }

    private fun playClip(clipId: String, slow: Boolean): Boolean = try {
        player = context.assets.openFd("$AUDIO_DIR/$clipId$CLIP_EXTENSION").use { fd -> preparedPlayer(fd) }
        player?.let { if (slow) it.playbackParams = PlaybackParams().setSpeed(SLOW_SPEED) }
        player?.start()
        true
    } catch (e: IOException) {
        Log.w(TAG, "Cannot play clip $clipId", e)
        false
    }

    private fun preparedPlayer(fd: AssetFileDescriptor): MediaPlayer = MediaPlayer().apply {
        setAudioAttributes(SPEECH)
        setDataSource(fd.fileDescriptor, fd.startOffset, fd.length)
        setOnCompletionListener(::release)
        prepare()
    }

    private fun release(finished: MediaPlayer) {
        finished.release()
        if (player === finished) player = null
    }

    private fun speak(text: String, slow: Boolean) {
        val engine = tts
        if (engine == null) {
            pending = text to slow
            tts = TextToSpeech(context) { status -> onTtsInit(status) }
        } else if (ttsReady) {
            engine.say(text, slow)
        } else {
            pending = text to slow
        }
    }

    private fun TextToSpeech.say(text: String, slow: Boolean) {
        setSpeechRate(if (slow) SLOW_SPEED else 1f)
        speak(text, TextToSpeech.QUEUE_FLUSH, null, UTTERANCE_ID)
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
        pending?.let { (text, slow) -> engine.say(text, slow) }
        pending = null
    }

    private companion object {
        const val TAG = "AudioProvider"
        const val AUDIO_DIR = "audio"
        const val CLIP_EXTENSION = ".ogg"
        const val UTTERANCE_ID = "hitz"
        const val SLOW_SPEED = 0.7f
        val BASQUE: Locale = Locale.forLanguageTag("eu-ES")
        val SPANISH: Locale = Locale.forLanguageTag("es-ES")
        val SPEECH: AudioAttributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_MEDIA)
            .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
            .build()
    }
}

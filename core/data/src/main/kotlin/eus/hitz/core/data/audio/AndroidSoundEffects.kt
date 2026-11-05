package eus.hitz.core.data.audio

import android.content.Context
import android.media.AudioAttributes
import android.media.SoundPool
import dagger.hilt.android.qualifiers.ApplicationContext
import eus.hitz.core.data.R
import eus.hitz.core.domain.audio.SoundEffect
import eus.hitz.core.domain.audio.SoundEffects
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Short effects from `res/raw` (synthesised by tools/audio/make_sound_effects.py, so they are
 * original), played with [SoundPool], which keeps them in memory for low latency.
 */
@Singleton
internal class AndroidSoundEffects @Inject constructor(
    @ApplicationContext context: Context,
) : SoundEffects {
    private val pool: SoundPool = SoundPool.Builder()
        .setMaxStreams(2)
        .setAudioAttributes(
            AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_GAME)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build(),
        )
        .build()

    private val sounds: Map<SoundEffect, Int> = mapOf(
        SoundEffect.Correct to pool.load(context, R.raw.core_data_sfx_correct, 1),
        SoundEffect.Incorrect to pool.load(context, R.raw.core_data_sfx_incorrect, 1),
        SoundEffect.Finished to pool.load(context, R.raw.core_data_sfx_finished, 1),
    )

    override fun play(effect: SoundEffect) {
        val id = sounds[effect] ?: return
        pool.play(id, VOLUME, VOLUME, 1, 0, 1f)
    }

    private companion object {
        const val VOLUME = 0.6f
    }
}

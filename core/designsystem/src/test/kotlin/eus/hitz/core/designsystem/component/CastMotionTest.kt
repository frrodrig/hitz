package eus.hitz.core.designsystem.component

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class CastMotionTest {
    @Test
    fun everyTrackCoversTheWholeLoopInOrder() {
        Motion.entries.forEach { motion ->
            motion.tracks.forEach { track ->
                val times = track.keys.map { it.at }
                assertThat(times.first()).isEqualTo(0f)
                assertThat(times.last()).isEqualTo(1f)
                assertThat(times).isInOrder()
            }
        }
    }

    @Test
    fun loopsJoinWithoutAVisibleJump() {
        // The page and the dropped pelota start over on purpose; everything else loops smoothly
        // or is invisible where its loop starts again.
        val restarts = setOf(Motion.PageFlip, Motion.Roll)
        Motion.entries.filterNot { it in restarts }.forEach { motion ->
            val hidden = motion.tracks.any { it.prop == Prop.Alpha && it.valueAt(0f) == 0f && it.valueAt(1f) == 0f }
            if (!hidden) {
                motion.tracks.forEach { track ->
                    assertThat(track.valueAt(1f)).isWithin(TOLERANCE).of(track.valueAt(0f))
                }
            }
        }
    }

    @Test
    fun tracksInterpolateBetweenKeys() {
        val track = Motion.Bob.tracks.single()
        assertThat(track.valueAt(0f)).isEqualTo(0f)
        assertThat(track.valueAt(0.5f)).isEqualTo(3f)
        assertThat(track.valueAt(0.25f)).isWithin(EASING_TOLERANCE).of(1.5f)
    }

    @Test
    fun delayedMotionsAreLate() {
        assertThat(Motion.NoteRiseLate.fractionAt(1_600)).isEqualTo(0f)
        assertThat(Motion.NoteRise.fractionAt(1_600)).isEqualTo(0.5f)
        assertThat(Motion.Bubble3.fractionAt(0)).isWithin(TOLERANCE).of(0.875f)
    }

    @Test
    fun everyMemberHasEveryPose() {
        CastMember.entries.forEach { member ->
            MascotPose.entries.forEach { pose ->
                assertThat(CastArtwork[member]?.get(pose)?.nodes).isNotEmpty()
            }
        }
    }

    @Test
    fun eachPoseHasItsSpeaker() {
        assertThat(CastMember.speakerFor(MascotPose.Hola)).isEqualTo(CastMember.Ane)
        assertThat(CastMember.speakerFor(MascotPose.Bien)).isEqualTo(CastMember.Mikel)
        assertThat(CastMember.speakerFor(MascotPose.Ups)).isEqualTo(CastMember.Maite)
        assertThat(CastMember.speakerFor(MascotPose.Piensa)).isEqualTo(CastMember.Maite)
    }

    private companion object {
        const val TOLERANCE = 1e-4f
        const val EASING_TOLERANCE = 1e-2f
    }
}

package eus.hitz.feature.lesson.story

import androidx.lifecycle.SavedStateHandle
import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import eus.hitz.core.domain.audio.SoundEffect
import eus.hitz.core.domain.content.CourseRepository
import eus.hitz.core.model.CourseId
import eus.hitz.core.model.content.Course
import eus.hitz.core.model.content.CourseUnit
import eus.hitz.core.model.content.Guidebook
import eus.hitz.core.model.content.Level
import eus.hitz.core.model.content.LocalizedText
import eus.hitz.core.model.content.Story
import eus.hitz.core.model.content.StoryLine
import eus.hitz.core.model.content.StoryQuestion
import eus.hitz.core.testing.FakeAudioProvider
import eus.hitz.core.testing.FakeGamificationRepository
import eus.hitz.core.testing.FakeSettingsRepository
import eus.hitz.core.testing.FakeSoundEffects
import eus.hitz.feature.lesson.FakeProgressRepository
import eus.hitz.feature.lesson.LessonAudio
import eus.hitz.feature.lesson.MainDispatcherRule
import eus.hitz.feature.lesson.recordSession
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test

class StoryViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val progress = FakeProgressRepository()
    private val audio = FakeAudioProvider()
    private val effects = FakeSoundEffects()
    private val gamification = FakeGamificationRepository()

    private val story = Story(
        id = "u01-1.l3.story",
        title = "Lehen eguna",
        lines = listOf(
            StoryLine("Ane", "Egun on!", "¡Buenos días!"),
            StoryLine("Iker", "Kaixo! Zer moduz?", "¡Hola! ¿Qué tal?"),
        ),
        questions = listOf(StoryQuestion(afterLine = 1, prompt = "¿Qué dice Iker?", listOf("Adiós", "Qué tal"), 1)),
    )

    private val courses = object : CourseRepository {
        override suspend fun getCourse(courseId: CourseId): Course = error("unused")

        override suspend fun getUnit(courseId: CourseId, unitId: String): CourseUnit {
            val text = LocalizedText("x", "x")
            return CourseUnit(
                1,
                UNIT,
                "s1",
                1,
                text,
                listOf(text),
                Guidebook(emptyList()),
                listOf(Level.StoryLevel(LEVEL, text, story))
            )
        }
    }

    private fun viewModel(levelId: String = LEVEL) = StoryViewModel(
        savedStateHandle = SavedStateHandle(mapOf("unitId" to UNIT, "levelId" to levelId)),
        courses = courses,
        recordSession = gamification.recordSession(progress),
        audio = LessonAudio(audio, effects),
        settings = FakeSettingsRepository(),
    )

    private val StoryViewModel.active get() = state.value as StoryUiState.Active

    @Test
    fun `starts with the first line and reads it aloud`() {
        val vm = viewModel()
        assertThat(vm.active.title).isEqualTo("Lehen eguna")
        assertThat(vm.active.lines.map { it.text }).containsExactly("Egun on!")
        assertThat(vm.active.canContinue).isTrue()
        assertThat(audio.played).containsExactly("u01-1.l3.story.0")
    }

    @Test
    fun `a question waits for the right answer`() {
        val vm = viewModel()
        vm.onAction(StoryAction.Continue)
        vm.onAction(StoryAction.Continue)
        assertThat(vm.active.question?.prompt).isEqualTo("¿Qué dice Iker?")
        assertThat(vm.active.canContinue).isFalse()
        vm.onAction(StoryAction.Answer(0))
        assertThat(vm.active.question?.wrong).containsExactly(0)
        assertThat(effects.played).containsExactly(SoundEffect.Incorrect)
        vm.onAction(StoryAction.Continue)
        assertThat(vm.active.question).isNotNull()
        vm.onAction(StoryAction.Answer(1))
        assertThat(vm.active.question?.correct).isEqualTo(1)
        assertThat(vm.active.canContinue).isTrue()
    }

    @Test
    fun `finishing records the level with the first-try accuracy`() {
        val vm = viewModel()
        vm.onAction(StoryAction.Continue)
        vm.onAction(StoryAction.Continue)
        vm.onAction(StoryAction.Answer(1))
        vm.onAction(StoryAction.Continue)
        val finished = vm.state.value as StoryUiState.Finished
        assertThat(finished.accuracy).isEqualTo(1f)
        assertThat(finished.reward?.xp).isEqualTo(20)
        assertThat(effects.played).containsExactly(SoundEffect.Correct, SoundEffect.Finished).inOrder()
        assertThat(progress.completed).containsExactly(LEVEL)
    }

    @Test
    fun `translations toggle per line`() {
        val vm = viewModel()
        vm.onAction(StoryAction.ToggleTranslation(0))
        assertThat(vm.active.translated).containsExactly(0)
        vm.onAction(StoryAction.ToggleTranslation(0))
        assertThat(vm.active.translated).isEmpty()
    }

    @Test
    fun `an unknown level is an error that can be closed`() = runTest {
        val vm = viewModel(levelId = "u01-1.l9")
        assertThat(vm.state.value).isEqualTo(StoryUiState.Error)
        vm.events.test {
            vm.onAction(StoryAction.Close)
            assertThat(awaitItem()).isEqualTo(StoryEvent.Close)
        }
    }

    private companion object {
        const val UNIT = "u01-1"
        const val LEVEL = "u01-1.l3"
    }
}

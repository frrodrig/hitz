package eus.hitz.feature.lesson

import androidx.lifecycle.SavedStateHandle
import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import eus.hitz.core.domain.audio.SoundEffect
import eus.hitz.core.domain.gamification.HeartsUseCase
import eus.hitz.core.domain.gamification.PlayerData
import eus.hitz.core.domain.gamification.Wallet
import eus.hitz.core.domain.lesson.PairSide
import eus.hitz.core.domain.lesson.WordBank
import eus.hitz.core.domain.placement.PlacementUseCase
import eus.hitz.core.domain.session.PlanSessionUseCase
import eus.hitz.core.domain.settings.UserSettings
import eus.hitz.core.model.content.Exercise
import eus.hitz.core.testing.FakeAudioProvider
import eus.hitz.core.testing.FakeGamificationRepository
import eus.hitz.core.testing.FakeGamificationRepository.Companion.NOW
import eus.hitz.core.testing.FakeSettingsRepository
import eus.hitz.core.testing.FakeSoundEffects
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test

class LessonViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val progress = FakeProgressRepository()
    private val audio = FakeAudioProvider()
    private val effects = FakeSoundEffects()
    private val gamification = FakeGamificationRepository()

    private fun viewModel(
        vararg exercises: Exercise,
        speakingEnabled: Boolean = true,
        settings: UserSettings = UserSettings(speakingEnabled = speakingEnabled),
        placementSection: String? = null,
    ) = LessonViewModel(
        savedStateHandle = SavedStateHandle(
            if (placementSection != null) {
                mapOf("sectionId" to placementSection)
            } else {
                mapOf("unitId" to TestLesson.UNIT, "levelId" to TestLesson.LEVEL, "lessonId" to TestLesson.LESSON)
            },
        ),
        planSession = PlanSessionUseCase(TestLesson.repository(*exercises), progress),
        placement = PlacementUseCase(TestLesson.repository(*exercises), progress),
        recordSession = gamification.recordSession(progress),
        settings = FakeSettingsRepository(settings),
        audio = LessonAudio(audio, effects),
        hearts = HeartsUseCase(gamification, gamification.clock),
    )

    private val LessonViewModel.active get() = state.value as LessonUiState.Active

    /** Taps the tiles that spell [sentence]. */
    private fun LessonViewModel.tap(sentence: String) {
        WordBank.words(sentence).forEach { word ->
            val exercise = active.exercise as ExerciseUi.TileAnswer
            val tile = exercise.tiles.first { it.text == word && it.id !in exercise.selected }
            onAction(LessonAction.SelectTile(tile.id))
        }
    }

    /** Selects the shown option with [text]. */
    private fun LessonViewModel.choose(text: String) {
        val options = when (val exercise = active.exercise) {
            is ExerciseUi.FillBlank -> exercise.options
            is ExerciseUi.ChooseMeaning -> exercise.options
            is ExerciseUi.CompleteChat -> exercise.options
            else -> error("not a text choice")
        }
        onAction(LessonAction.SelectOption(options.indexOf(text)))
    }

    @Test
    fun `starts with the first exercise`() {
        val vm = viewModel(TestLesson.wordBank, TestLesson.typing)
        assertThat(vm.active.exercise.id).isEqualTo(TestLesson.wordBank.id)
        assertThat(vm.active.hearts).isEqualTo(5)
        assertThat(vm.active.canCheck).isFalse()
    }

    @Test
    fun `a new word card plays the word and continues without counting as an exercise`() {
        val vm = viewModel(TestLesson.wordIntro, TestLesson.wordBank)
        val card = vm.active.exercise as ExerciseUi.WordIntro
        assertThat(card.word).isEqualTo("kaixo")
        assertThat(card.translation).isEqualTo("hola")
        assertThat(card.imageUri).isEqualTo("file:///android_asset/images/greeting.webp")
        assertThat(vm.active.canCheck).isFalse()
        assertThat(audio.played).containsExactly(TestLesson.wordIntro.id)

        vm.onAction(LessonAction.Continue)

        assertThat(vm.active.exercise.id).isEqualTo(TestLesson.wordBank.id)
        assertThat(vm.active.progress).isEqualTo(0f)
        vm.tap("¡Hola! Soy Ane.")
        vm.onAction(LessonAction.Check)
        vm.onAction(LessonAction.Continue)
        val finished = vm.state.value as LessonUiState.Finished
        assertThat(finished.result.exerciseCount).isEqualTo(1)
    }

    @Test
    fun `passing a placement test skips the section without second chances or hints`() {
        val vm = viewModel(TestLesson.typing, TestLesson.listenType, placementSection = TestLesson.SECTION)
        assertThat(vm.active.hearts).isNull()
        assertThat(vm.active.glossary).isNull()
        repeat(2) {
            val input = if (vm.active.exercise.id == TestLesson.typing.id) "Eskerrik asko." else "Eskerrik asko"
            vm.onAction(LessonAction.ChangeInput(input))
            vm.onAction(LessonAction.Check)
            vm.onAction(LessonAction.Continue)
        }
        val finished = vm.state.value as LessonUiState.PlacementFinished
        assertThat(finished.passed).isTrue()
        assertThat(finished.sectionNumber).isEqualTo(1)
        assertThat(finished.nextSectionId).isNull()
        assertThat(progress.completed).contains(TestLesson.LESSON)
    }

    @Test
    fun `failing a placement test keeps the section`() {
        val vm = viewModel(TestLesson.typing, TestLesson.listenType, placementSection = TestLesson.SECTION)
        repeat(2) {
            vm.onAction(LessonAction.ChangeInput("nik ez dakit"))
            vm.onAction(LessonAction.Check)
            vm.onAction(LessonAction.Continue)
        }
        val finished = vm.state.value as LessonUiState.PlacementFinished
        assertThat(finished.passed).isFalse()
        assertThat(progress.completed).isEmpty()
    }

    @Test
    fun `lessons have word hints`() {
        val vm = viewModel(TestLesson.wordBank)
        assertThat(vm.active.glossary).isEqualTo(TestLesson.glossary)
    }

    @Test
    fun `listening exercises play their clip when shown and on demand`() {
        val vm = viewModel(TestLesson.listenTap)
        assertThat(audio.played).containsExactly(TestLesson.listenTap.id)
        vm.onAction(LessonAction.PlayAudio(slow = true))
        assertThat(audio.played).hasSize(2)
        assertThat(audio.playedSlow).containsExactly(TestLesson.listenTap.id)
    }

    @Test
    fun `listen and tap accepts the words in order`() {
        val vm = viewModel(TestLesson.listenTap)
        vm.tap("Gero arte Jon")
        vm.onAction(LessonAction.Check)
        assertThat(vm.active.feedback).isEqualTo(FeedbackUi(FeedbackType.Correct))
    }

    @Test
    fun `listen and type tolerates a typo`() {
        val vm = viewModel(TestLesson.listenType)
        vm.onAction(LessonAction.ChangeInput("eskerrik aska"))
        vm.onAction(LessonAction.Check)
        assertThat(vm.active.feedback).isEqualTo(FeedbackUi(FeedbackType.AlmostCorrect, "Eskerrik asko"))
    }

    @Test
    fun `ordering the words wrongly is incorrect`() {
        val vm = viewModel(TestLesson.order)
        vm.tap("Mikel Zu zara")
        vm.onAction(LessonAction.Check)
        assertThat(vm.active.feedback).isEqualTo(FeedbackUi(FeedbackType.Incorrect, "Zu Mikel zara?"))
        assertThat(vm.active.hearts).isEqualTo(4)
    }

    @Test
    fun `a wrong word in the gap shows the whole sentence`() {
        val vm = viewModel(TestLesson.fillBlank)
        val exercise = vm.active.exercise as ExerciseUi.FillBlank
        assertThat(exercise.before).isEqualTo("Ni Ane ")
        assertThat(exercise.after).isEqualTo(".")
        vm.choose("zara")
        vm.onAction(LessonAction.Check)
        assertThat(vm.active.feedback).isEqualTo(FeedbackUi(FeedbackType.Incorrect, "Ni Ane naiz."))
    }

    @Test
    fun `shuffled options are checked by their content`() {
        val vm = viewModel(TestLesson.meaning, TestLesson.chat)
        vm.choose("¡Perdón!")
        vm.onAction(LessonAction.Check)
        assertThat(vm.active.feedback?.type).isEqualTo(FeedbackType.Correct)
        vm.onAction(LessonAction.Continue)
        vm.choose("Ez horregatik.")
        vm.onAction(LessonAction.Check)
        assertThat(vm.active.feedback).isEqualTo(FeedbackUi(FeedbackType.Incorrect, "Ni Itziar naiz."))
    }

    @Test
    fun `true or false shows no correction`() {
        val vm = viewModel(TestLesson.trueFalse)
        vm.onAction(LessonAction.SelectOption(ExerciseUi.ReadTrueFalse.FALSE))
        vm.onAction(LessonAction.Check)
        assertThat(vm.active.feedback).isEqualTo(FeedbackUi(FeedbackType.Incorrect, null))
        vm.onAction(LessonAction.Continue)
        vm.onAction(LessonAction.SelectOption(ExerciseUi.ReadTrueFalse.TRUE))
        vm.onAction(LessonAction.Check)
        assertThat(vm.active.feedback?.type).isEqualTo(FeedbackType.Correct)
    }

    @Test
    fun `recognized speech is checked tolerantly`() {
        val vm = viewModel(TestLesson.speak)
        assertThat(vm.active.canCheck).isFalse()
        vm.onAction(LessonAction.SpeechResult(listOf("egun ona", "egun on")))
        assertThat(vm.active.feedback?.type).isEqualTo(FeedbackType.Correct)
        vm.onAction(LessonAction.Continue)
        assertThat((vm.state.value as LessonUiState.Finished).result.accuracy).isEqualTo(1f)
    }

    @Test
    fun `unrecognized speech shows the sentence and costs no heart`() {
        val vm = viewModel(TestLesson.speak, TestLesson.typing)
        vm.onAction(LessonAction.SpeechResult(listOf("kaixo zer moduz")))
        assertThat(vm.active.feedback).isEqualTo(FeedbackUi(FeedbackType.Incorrect, "Egun on!"))
        assertThat(vm.active.hearts).isEqualTo(5)
        vm.onAction(LessonAction.Continue)
        assertThat(vm.active.exercise.id).isEqualTo(TestLesson.typing.id)
    }

    @Test
    fun `skipping speaking skips the remaining speaking exercises`() {
        val second = TestLesson.speak.copy(id = "${TestLesson.LESSON}.e20")
        val vm = viewModel(TestLesson.speak, TestLesson.typing, second)
        vm.onAction(LessonAction.SkipSpeaking)
        assertThat(vm.active.exercise.id).isEqualTo(TestLesson.typing.id)
        assertThat(vm.active.progress).isEqualTo(0f)
        vm.onAction(LessonAction.ChangeInput("Eskerrik asko"))
        vm.onAction(LessonAction.Check)
        vm.onAction(LessonAction.Continue)
        val finished = vm.state.value as LessonUiState.Finished
        assertThat(finished.result.exerciseCount).isEqualTo(1)
        assertThat(progress.completed).containsExactly(TestLesson.LESSON)
    }

    @Test
    fun `speaking exercises are left out when turned off`() {
        val vm = viewModel(TestLesson.speak, TestLesson.typing, speakingEnabled = false)
        assertThat(vm.active.exercise.id).isEqualTo(TestLesson.typing.id)
        assertThat(vm.active.progress).isEqualTo(0f)
    }

    @Test
    fun `correct word bank answer advances the progress`() {
        val vm = viewModel(TestLesson.wordBank, TestLesson.typing)
        vm.tap("Hola Soy Ane")
        vm.onAction(LessonAction.Check)
        assertThat(vm.active.feedback).isEqualTo(FeedbackUi(FeedbackType.Correct))
        assertThat(vm.active.progress).isEqualTo(0.5f)
        vm.onAction(LessonAction.Continue)
        assertThat(vm.active.exercise).isInstanceOf(ExerciseUi.Typing::class.java)
    }

    @Test
    fun `removing a tile takes it out of the answer`() {
        val vm = viewModel(TestLesson.wordBank)
        vm.tap("Hola")
        val tile = (vm.active.exercise as ExerciseUi.WordBank).selected.single()
        vm.onAction(LessonAction.RemoveTile(tile))
        assertThat((vm.active.exercise as ExerciseUi.WordBank).selected).isEmpty()
    }

    @Test
    fun `wrong answer costs a heart, shows the answer and is asked again`() {
        val vm = viewModel(TestLesson.wordBank)
        vm.tap("adiós eres Ane")
        vm.onAction(LessonAction.Check)
        assertThat(vm.active.hearts).isEqualTo(4)
        assertThat(vm.active.feedback).isEqualTo(FeedbackUi(FeedbackType.Incorrect, "¡Hola! Soy Ane."))
        vm.onAction(LessonAction.Continue)
        assertThat(vm.active.exercise.id).isEqualTo(TestLesson.wordBank.id)
        assertThat((vm.active.exercise as ExerciseUi.WordBank).selected).isEmpty()
    }

    @Test
    fun `typing with a small typo is almost correct`() {
        val vm = viewModel(TestLesson.typing)
        vm.onAction(LessonAction.ChangeInput("eskerik asko"))
        vm.onAction(LessonAction.Check)
        assertThat(vm.active.feedback).isEqualTo(FeedbackUi(FeedbackType.AlmostCorrect, "Eskerrik asko."))
        assertThat(vm.active.hearts).isEqualTo(5)
    }

    @Test
    fun `choosing the right image is correct`() {
        val vm = viewModel(TestLesson.image)
        vm.onAction(LessonAction.SelectOption(1))
        vm.onAction(LessonAction.Check)
        assertThat(vm.active.feedback?.type).isEqualTo(FeedbackType.Correct)
    }

    @Test
    fun `matching every pair finishes the exercise without a check button`() {
        val vm = viewModel(TestLesson.pairs)
        vm.onAction(LessonAction.SelectPair(PairSide.Left, 0))
        vm.onAction(LessonAction.SelectPair(PairSide.Right, 1))
        assertThat(vm.active.feedback).isNull()
        (0..2).forEach {
            vm.onAction(LessonAction.SelectPair(PairSide.Left, it))
            vm.onAction(LessonAction.SelectPair(PairSide.Right, it))
        }
        assertThat(vm.active.feedback?.type).isEqualTo(FeedbackType.Correct)
        vm.onAction(LessonAction.Continue)
        val finished = vm.state.value as LessonUiState.Finished
        assertThat(finished.result.mistakes.single().given).isEqualTo("kaixo = adiós")
        assertThat(finished.result.heartsLeft).isEqualTo(5)
    }

    @Test
    fun `finishing every exercise shows the result`() {
        val vm = viewModel(TestLesson.image)
        vm.onAction(LessonAction.SelectOption(1))
        vm.onAction(LessonAction.Check)
        vm.onAction(LessonAction.Continue)
        val finished = vm.state.value as LessonUiState.Finished
        assertThat(finished.result.completed).isTrue()
        assertThat(finished.result.accuracy).isEqualTo(1f)
        assertThat(progress.completed).containsExactly(TestLesson.LESSON)
    }

    @Test
    fun `losing every heart ends the lesson`() {
        val vm = viewModel(TestLesson.image)
        repeat(5) {
            vm.onAction(LessonAction.SelectOption(0))
            vm.onAction(LessonAction.Check)
            vm.onAction(LessonAction.Continue)
        }
        assertThat(vm.state.value).isEqualTo(LessonUiState.OutOfHearts)
        assertThat(progress.completed).isEmpty()
        assertThat(progress.mistakes).hasSize(5)
    }

    @Test
    fun `confirming quit closes the lesson`() = runTest {
        val vm = viewModel(TestLesson.wordBank)
        vm.onAction(LessonAction.RequestQuit)
        assertThat(vm.active.showQuitDialog).isTrue()
        vm.events.test {
            vm.onAction(LessonAction.ConfirmQuit)
            assertThat(awaitItem()).isEqualTo(LessonEvent.Close)
        }
    }

    @Test
    fun `answers cannot change once checked`() {
        val vm = viewModel(TestLesson.image)
        vm.onAction(LessonAction.SelectOption(1))
        vm.onAction(LessonAction.Check)
        vm.onAction(LessonAction.SelectOption(0))
        assertThat((vm.active.exercise as ExerciseUi.ChooseImage).selected).isEqualTo(1)
    }

    @Test
    fun `lessons use and spend the shared hearts`() {
        gamification.data.value = PlayerData(wallet = Wallet(hearts = 2, heartsUpdatedAt = NOW))
        val vm = viewModel(TestLesson.image)
        assertThat(vm.active.hearts).isEqualTo(2)
        vm.onAction(LessonAction.SelectOption(0))
        vm.onAction(LessonAction.Check)
        assertThat(gamification.data.value.wallet.hearts).isEqualTo(1)
    }

    @Test
    fun `without hearts a lesson cannot start`() {
        gamification.data.value = PlayerData(wallet = Wallet(hearts = 0, heartsUpdatedAt = NOW))
        val vm = viewModel(TestLesson.image)
        assertThat(vm.state.value).isEqualTo(LessonUiState.OutOfHearts)
    }

    @Test
    fun `a finished lesson shows its XP and streak`() {
        val vm = viewModel(TestLesson.image)
        vm.onAction(LessonAction.SelectOption(1))
        vm.onAction(LessonAction.Check)
        vm.onAction(LessonAction.Continue)
        val reward = (vm.state.value as LessonUiState.Finished).reward
        assertThat(reward?.xp).isEqualTo(15)
        assertThat(reward?.streak).isEqualTo(1)
        assertThat(reward?.streakExtended).isTrue()
    }

    @Test
    fun `listening exercises are left out when turned off`() {
        val vm = viewModel(
            TestLesson.listenTap,
            TestLesson.listenType,
            TestLesson.typing,
            settings = UserSettings(listeningEnabled = false)
        )
        assertThat(vm.active.exercise.id).isEqualTo(TestLesson.typing.id)
        assertThat(audio.played).isEmpty()
    }

    @Test
    fun `checking plays a sound effect and the Basque answer`() = runTest {
        val vm = viewModel(TestLesson.order, TestLesson.image)
        vm.events.test {
            vm.tap("Zu Mikel zara")
            vm.onAction(LessonAction.Check)
            assertThat(awaitItem()).isEqualTo(LessonEvent.Feedback(correct = true))
            assertThat(effects.played).containsExactly(SoundEffect.Correct)
            assertThat(audio.played).containsExactly(TestLesson.order.id)
            vm.onAction(LessonAction.Continue)
            vm.onAction(LessonAction.SelectOption(0))
            vm.onAction(LessonAction.Check)
            assertThat(awaitItem()).isEqualTo(LessonEvent.Feedback(correct = false))
        }
        assertThat(effects.played).containsExactly(SoundEffect.Correct, SoundEffect.Incorrect).inOrder()
    }

    @Test
    fun `sound effects can be turned off`() = runTest {
        val vm = viewModel(TestLesson.image, settings = UserSettings(soundEffectsEnabled = false))
        vm.onAction(LessonAction.SelectOption(1))
        vm.onAction(LessonAction.Check)
        vm.onAction(LessonAction.Continue)
        assertThat(effects.played).isEmpty()
        assertThat(vm.state.value).isInstanceOf(LessonUiState.Finished::class.java)
    }

    @Test
    fun `finishing plays the finished sound`() {
        val vm = viewModel(TestLesson.image)
        vm.onAction(LessonAction.SelectOption(1))
        vm.onAction(LessonAction.Check)
        vm.onAction(LessonAction.Continue)
        assertThat(effects.played.last()).isEqualTo(SoundEffect.Finished)
    }
}

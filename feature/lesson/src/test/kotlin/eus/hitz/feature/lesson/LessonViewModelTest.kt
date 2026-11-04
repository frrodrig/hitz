package eus.hitz.feature.lesson

import androidx.lifecycle.SavedStateHandle
import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import eus.hitz.core.domain.lesson.AnswerChecker
import eus.hitz.core.domain.lesson.LoadLessonUseCase
import eus.hitz.core.domain.lesson.PairSide
import eus.hitz.core.domain.lesson.WordBank
import eus.hitz.core.model.content.Exercise
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test

class LessonViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private fun viewModel(vararg exercises: Exercise) = LessonViewModel(
        savedStateHandle = SavedStateHandle(mapOf("unitId" to TestLesson.UNIT, "lessonId" to TestLesson.LESSON)),
        loadLesson = LoadLessonUseCase(TestLesson.repository(*exercises)),
        checker = AnswerChecker(),
    )

    private val LessonViewModel.active get() = state.value as LessonUiState.Active

    /** Taps the tiles that spell [sentence]. */
    private fun LessonViewModel.tap(sentence: String) {
        val tiles = (active.exercise as ExerciseUi.WordBank).tiles
        WordBank.words(sentence).forEach { word ->
            val tile = tiles.first { it.text == word && it.id !in (active.exercise as ExerciseUi.WordBank).selected }
            onAction(LessonAction.SelectTile(tile.id))
        }
    }

    @Test
    fun `starts with the first supported exercise and skips the rest`() {
        val vm = viewModel(TestLesson.unsupported, TestLesson.wordBank)
        assertThat(vm.active.exercise.id).isEqualTo(TestLesson.wordBank.id)
        assertThat(vm.active.hearts).isEqualTo(5)
        assertThat(vm.active.canCheck).isFalse()
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
}

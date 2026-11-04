package eus.hitz.feature.lesson

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import eus.hitz.core.domain.lesson.AnswerChecker
import eus.hitz.core.domain.lesson.AnswerResult
import eus.hitz.core.domain.lesson.LessonSession
import eus.hitz.core.domain.lesson.LoadLessonUseCase
import eus.hitz.core.domain.lesson.MatchPairsState
import eus.hitz.core.domain.lesson.Mistake
import eus.hitz.core.domain.lesson.WordBank
import eus.hitz.core.model.CourseId
import eus.hitz.core.model.content.Exercise
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.random.Random

/** Exercise types the lesson engine can play (the rest arrive in M5). */
internal fun Exercise.isSupported(): Boolean = when (this) {
    is Exercise.TranslateWordBank, is Exercise.TranslateTyping, is Exercise.ChooseImage, is Exercise.MatchPairs -> true
    else -> false
}

@HiltViewModel
class LessonViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val loadLesson: LoadLessonUseCase,
    private val checker: AnswerChecker,
) : ViewModel() {
    private val unitId: String = checkNotNull(savedStateHandle["unitId"])
    private val lessonId: String = checkNotNull(savedStateHandle["lessonId"])

    private val _state = MutableStateFlow<LessonUiState>(LessonUiState.Loading)
    val state: StateFlow<LessonUiState> = _state.asStateFlow()

    private val _events = Channel<LessonEvent>(Channel.BUFFERED)
    val events: Flow<LessonEvent> = _events.receiveAsFlow()

    private var exercises: Map<String, Exercise> = emptyMap()
    private lateinit var session: LessonSession

    init {
        viewModelScope.launch {
            runCatching { loadLesson(CourseId.BASQUE_FROM_SPANISH, unitId, lessonId) }
                .onSuccess { content ->
                    val playable = content.lesson.exercises.filter { it.isSupported() }
                    exercises = playable.associateBy { it.id }
                    session = LessonSession.start(lessonId, playable.map { it.id }, hearts = STARTING_HEARTS)
                    showCurrent()
                }
                .onFailure { _state.value = LessonUiState.Error }
        }
    }

    fun onAction(action: LessonAction) {
        when (action) {
            is LessonAction.SelectTile -> updateExercise<ExerciseUi.WordBank> {
                if (action.tileId in it.selected) it else it.copy(selected = it.selected + action.tileId)
            }
            is LessonAction.RemoveTile -> updateExercise<ExerciseUi.WordBank> {
                it.copy(selected = it.selected - action.tileId)
            }
            is LessonAction.ChangeInput -> updateExercise<ExerciseUi.Typing> { it.copy(input = action.text) }
            is LessonAction.SelectOption -> updateExercise<ExerciseUi.ChooseImage> { it.copy(selected = action.index) }
            is LessonAction.SelectPair -> selectPair(action)
            LessonAction.Check -> check()
            LessonAction.Continue -> next()
            LessonAction.RequestQuit -> updateActive { it.copy(showQuitDialog = true) }
            LessonAction.DismissQuit -> updateActive { it.copy(showQuitDialog = false) }
            LessonAction.ConfirmQuit -> close()
        }
    }

    private fun selectPair(action: LessonAction.SelectPair) {
        val before = ((_state.value as? LessonUiState.Active)?.exercise as? ExerciseUi.MatchPairs)?.state ?: return
        updateExercise<ExerciseUi.MatchPairs> { it.copy(state = it.state.select(action.side, action.pairIndex)) }
        val active = _state.value as? LessonUiState.Active ?: return
        val pairs = active.exercise as? ExerciseUi.MatchPairs ?: return
        val mismatch = pairs.state.lastMismatch
        if (pairs.state.mismatches > before.mismatches && mismatch != null) {
            val exercise = exercises.getValue(pairs.id)
            val (left, right) = mismatch
            session = session.recordMistake(
                Mistake(
                    exerciseId = exercise.id,
                    vocabulary = exercise.vocabulary,
                    structures = exercise.structures,
                    given = "${pairs.state.left[left]} = ${pairs.state.right[right]}",
                    expected = "${pairs.state.left[left]} = ${pairs.state.right[left]}",
                ),
            )
        }
        // Mismatches are practice: they are counted but cost no heart.
        if (pairs.state.isComplete && active.feedback == null) {
            session = session.answerCorrect()
            _state.value = active.copy(progress = session.progress, feedback = FeedbackUi(FeedbackType.Correct))
        }
    }

    private fun check() {
        val active = _state.value as? LessonUiState.Active ?: return
        if (!active.canCheck) return
        val exercise = exercises.getValue(active.exercise.id)
        val (result, given) = evaluate(exercise, active.exercise)
        session = if (result.isAccepted) {
            session.answerCorrect()
        } else {
            val mistake = Mistake(exercise.id, exercise.vocabulary, exercise.structures, given, result.expected)
            session.answerIncorrect(mistake)
        }
        val feedback = when (result) {
            is AnswerResult.Correct -> FeedbackUi(FeedbackType.Correct)
            is AnswerResult.AlmostCorrect -> FeedbackUi(FeedbackType.AlmostCorrect, result.expected)
            is AnswerResult.Incorrect -> FeedbackUi(FeedbackType.Incorrect, result.expected)
        }
        _state.value = active.copy(progress = session.progress, hearts = session.hearts, feedback = feedback)
    }

    private fun evaluate(exercise: Exercise, ui: ExerciseUi): Pair<AnswerResult, String> = when {
        exercise is Exercise.TranslateWordBank && ui is ExerciseUi.WordBank -> {
            val given = WordBank.answer(ui.tiles, ui.selected)
            checker.check(given, exercise.answers).exactOnly() to given
        }
        exercise is Exercise.TranslateTyping && ui is ExerciseUi.Typing ->
            checker.check(ui.input, exercise.answers) to ui.input
        exercise is Exercise.ChooseImage && ui is ExerciseUi.ChooseImage -> {
            val chosen = exercise.options[checkNotNull(ui.selected)]
            val expected = exercise.options[exercise.correct].text
            val result = if (ui.selected == exercise.correct) {
                AnswerResult.Correct(
                    expected
                )
            } else {
                AnswerResult.Incorrect(expected)
            }
            result to chosen.text
        }
        else -> error("exercise ${exercise.id} does not match its UI state")
    }

    /** Tiles leave no room for typos: an "almost" answer built from tiles is a wrong order. */
    private fun AnswerResult.exactOnly(): AnswerResult =
        if (this is AnswerResult.AlmostCorrect) AnswerResult.Incorrect(expected) else this

    private fun next() {
        val active = _state.value as? LessonUiState.Active ?: return
        if (active.feedback == null) return
        showCurrent()
    }

    private fun showCurrent() {
        val current = session.current
        _state.value = when {
            session.isOutOfHearts -> LessonUiState.OutOfHearts
            current == null -> LessonUiState.Finished(session.result())
            else -> LessonUiState.Active(
                progress = session.progress,
                hearts = session.hearts,
                exercise = exercises.getValue(current.exerciseId).toUi(randomFor(current.exerciseId, current.isRetry)),
            )
        }
    }

    private fun close() {
        viewModelScope.launch { _events.send(LessonEvent.Close) }
    }

    private inline fun updateActive(transform: (LessonUiState.Active) -> LessonUiState.Active) {
        _state.update { if (it is LessonUiState.Active) transform(it) else it }
    }

    private inline fun <reified T : ExerciseUi> updateExercise(crossinline transform: (T) -> ExerciseUi) {
        updateActive { active ->
            val exercise = active.exercise
            if (active.feedback == null && exercise is T) active.copy(exercise = transform(exercise)) else active
        }
    }

    private fun randomFor(exerciseId: String, retry: Boolean) = Random(exerciseId.hashCode() + if (retry) 1 else 0)

    private companion object {
        const val STARTING_HEARTS = 5
    }
}

internal fun Exercise.toUi(random: Random): ExerciseUi = when (this) {
    is Exercise.TranslateWordBank -> ExerciseUi.WordBank(
        id,
        prompt.text,
        prompt.lang,
        WordBank.tiles(answers.first(), distractors, random)
    )
    is Exercise.TranslateTyping -> ExerciseUi.Typing(id, prompt.text, prompt.lang)
    is Exercise.ChooseImage -> ExerciseUi.ChooseImage(
        id = id,
        prompt = prompt,
        options = options.map { ImageOptionUi(it.text, "file:///android_asset/images/${it.image}") },
    )
    is Exercise.MatchPairs -> ExerciseUi.MatchPairs(
        id = id,
        state = MatchPairsState.start(pairs.map { it.eu }, pairs.map { it.es }, random),
    )
    else -> error("exercise type of $id is not supported yet")
}

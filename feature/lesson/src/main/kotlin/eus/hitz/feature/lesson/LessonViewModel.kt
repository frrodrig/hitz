package eus.hitz.feature.lesson

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import eus.hitz.core.domain.audio.AudioProvider
import eus.hitz.core.domain.gamification.ActivityKind
import eus.hitz.core.domain.gamification.HeartsUseCase
import eus.hitz.core.domain.lesson.AnswerChecker
import eus.hitz.core.domain.lesson.AnswerResult
import eus.hitz.core.domain.lesson.LessonResult
import eus.hitz.core.domain.lesson.LessonSession
import eus.hitz.core.domain.lesson.MatchPairsState
import eus.hitz.core.domain.lesson.Mistake
import eus.hitz.core.domain.lesson.WordBank
import eus.hitz.core.domain.session.HeartPolicy
import eus.hitz.core.domain.session.PlanSessionUseCase
import eus.hitz.core.domain.session.RecordSessionUseCase
import eus.hitz.core.domain.settings.SettingsRepository
import eus.hitz.core.model.content.Exercise
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.random.Random

@HiltViewModel
class LessonViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val planSession: PlanSessionUseCase,
    private val recordSession: RecordSessionUseCase,
    private val settings: SettingsRepository,
    private val audio: AudioProvider,
    private val hearts: HeartsUseCase,
) : ViewModel() {
    private val unitId: String = checkNotNull(savedStateHandle["unitId"])
    private val levelId: String = checkNotNull(savedStateHandle["levelId"])
    private val lessonId: String? = savedStateHandle["lessonId"]

    private val checker = AnswerChecker()

    private val _state = MutableStateFlow<LessonUiState>(LessonUiState.Loading)
    val state: StateFlow<LessonUiState> = _state.asStateFlow()

    private val _events = Channel<LessonEvent>(Channel.BUFFERED)
    val events: Flow<LessonEvent> = _events.receiveAsFlow()

    private var exercises: Map<String, Exercise> = emptyMap()
    private lateinit var session: LessonSession
    private var heartPolicy: HeartPolicy = HeartPolicy.Unlimited
    private var kind: ActivityKind = ActivityKind.Lesson

    /** Set by "I can't speak now": the remaining speaking exercises are skipped. */
    private var skipSpeaking = false

    init {
        viewModelScope.launch {
            runCatching {
                val speakingEnabled = settings.settings.first().speakingEnabled
                val plan = planSession(unitId, levelId, lessonId)
                plan to plan.exercises.filter { speakingEnabled || it !is Exercise.Speak }
            }.onSuccess { (plan, playable) ->
                exercises = playable.associateBy { it.id }
                heartPolicy = plan.hearts
                kind = plan.kind
                val startHearts = when (val policy = plan.hearts) {
                    HeartPolicy.Shared -> hearts.current()
                    is HeartPolicy.Limited -> policy.hearts
                    HeartPolicy.Unlimited -> null
                }
                session = LessonSession.start(plan.sessionId, playable.map { it.id }, hearts = startHearts)
                if (session.isOutOfHearts) _state.value = LessonUiState.OutOfHearts else showCurrent()
            }.onFailure { _state.value = LessonUiState.Error }
        }
    }

    fun onAction(action: LessonAction) {
        when (action) {
            is LessonAction.SelectTile -> updateExercise<ExerciseUi.TileAnswer> {
                if (action.tileId in it.selected) it else it.withSelected(it.selected + action.tileId)
            }
            is LessonAction.RemoveTile -> updateExercise<ExerciseUi.TileAnswer> {
                it.withSelected(it.selected - action.tileId)
            }
            is LessonAction.ChangeInput -> updateExercise<ExerciseUi.TextAnswer> { it.withInput(action.text) }
            is LessonAction.SelectOption -> updateExercise<ExerciseUi.SingleChoice> { it.withSelected(action.index) }
            is LessonAction.SelectPair -> selectPair(action)
            LessonAction.PlayAudio -> playCurrent()
            is LessonAction.SpeechResult -> speech(action.candidates)
            LessonAction.SkipSpeaking -> skipSpeakingExercises()
            LessonAction.Check -> check()
            LessonAction.Continue -> next()
            LessonAction.RequestQuit, LessonAction.DismissQuit, LessonAction.ConfirmQuit -> quit(action)
        }
    }

    private fun quit(action: LessonAction) {
        when (action) {
            LessonAction.RequestQuit -> updateActive { it.copy(showQuitDialog = true) }
            LessonAction.DismissQuit -> updateActive { it.copy(showQuitDialog = false) }
            else -> close()
        }
    }

    override fun onCleared() {
        audio.stop()
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
        val evaluation = evaluate(exercise, active.exercise)
        val result = evaluation.result
        session = if (result.isAccepted) {
            session.answerCorrect()
        } else {
            val mistake =
                Mistake(exercise.id, exercise.vocabulary, exercise.structures, evaluation.given, result.expected)
            if (heartPolicy == HeartPolicy.Shared) viewModelScope.launch { hearts.lose() }
            session.answerIncorrect(mistake)
        }
        val expected = result.expected.takeIf { evaluation.showExpected }
        val feedback = when (result) {
            is AnswerResult.Correct -> FeedbackUi(FeedbackType.Correct)
            is AnswerResult.AlmostCorrect -> FeedbackUi(FeedbackType.AlmostCorrect, expected)
            is AnswerResult.Incorrect -> FeedbackUi(FeedbackType.Incorrect, expected)
        }
        _state.value = active.copy(progress = session.progress, hearts = session.hearts, feedback = feedback)
    }

    private class Evaluation(val result: AnswerResult, val given: String, val showExpected: Boolean = true)

    private fun evaluate(exercise: Exercise, ui: ExerciseUi): Evaluation = when (exercise) {
        is Exercise.TranslateWordBank -> tiles(ui, exercise.answers)
        is Exercise.TranslateTyping -> typed(ui, exercise.answers)
        is Exercise.ChooseImage -> choice(ui, exercise.correct, exercise.options.map { it.text })
        is Exercise.ListenTap -> tiles(ui, listOf(exercise.text))
        is Exercise.ListenType -> typed(ui, exercise.answers)
        is Exercise.OrderSentence -> tiles(ui, listOf(exercise.answer))
        is Exercise.FillBlank -> choice(
            ui = ui,
            correct = exercise.correct,
            options = exercise.options,
            expected = exercise.sentence.replace(BLANK, exercise.options[exercise.correct]),
        )
        is Exercise.ChooseMeaning -> choice(ui, exercise.correct, exercise.options)
        is Exercise.CompleteChat -> choice(ui, exercise.correct, exercise.options)
        is Exercise.ReadTrueFalse -> trueFalse(ui, exercise.answer)
        is Exercise.MatchPairs, is Exercise.Speak -> error("exercise ${exercise.id} is not answered with Check")
    }

    /** Tiles leave no room for typos: an "almost" answer built from tiles is a wrong order. */
    private fun tiles(ui: ExerciseUi, accepted: List<String>): Evaluation {
        val tileAnswer = ui as ExerciseUi.TileAnswer
        val given = WordBank.answer(tileAnswer.tiles, tileAnswer.selected)
        val result = checker.check(given, accepted)
        val exact = if (result is AnswerResult.AlmostCorrect) AnswerResult.Incorrect(accepted.first()) else result
        return Evaluation(exact, given)
    }

    private fun typed(ui: ExerciseUi, accepted: List<String>): Evaluation {
        val input = (ui as ExerciseUi.TextAnswer).input
        return Evaluation(checker.check(input, accepted), input)
    }

    private fun choice(
        ui: ExerciseUi,
        correct: Int,
        options: List<String>,
        expected: String = options[correct],
    ): Evaluation {
        val shown = checkNotNull((ui as ExerciseUi.SingleChoice).selected)
        val chosen = ui.contentIndex(shown)
        val result = if (chosen == correct) AnswerResult.Correct(expected) else AnswerResult.Incorrect(expected)
        return Evaluation(result, options[chosen])
    }

    private fun trueFalse(ui: ExerciseUi, answer: Boolean): Evaluation {
        val chosen = (ui as ExerciseUi.ReadTrueFalse).selected == ExerciseUi.ReadTrueFalse.TRUE
        val expected = answer.toString()
        val result = if (chosen == answer) AnswerResult.Correct(expected) else AnswerResult.Incorrect(expected)
        return Evaluation(result, chosen.toString(), showExpected = false)
    }

    /**
     * Speech is checked like typing, against the best candidate. Recognition of Basque is often
     * imperfect, so a failed attempt costs no heart: the exercise is skipped and the sentence shown.
     */
    private fun speech(candidates: List<String>) {
        val active = _state.value as? LessonUiState.Active ?: return
        val ui = active.exercise as? ExerciseUi.Speak ?: return
        if (active.feedback != null || candidates.isEmpty()) return
        val accepted = candidates.any { checker.check(it, listOf(ui.text)).isAccepted }
        session = if (accepted) session.answerCorrect() else session.skip()
        val feedback = if (accepted) FeedbackUi(FeedbackType.Correct) else FeedbackUi(FeedbackType.Incorrect, ui.text)
        _state.value = active.copy(progress = session.progress, feedback = feedback)
    }

    private fun skipSpeakingExercises() {
        val active = _state.value as? LessonUiState.Active ?: return
        if (active.exercise !is ExerciseUi.Speak || active.feedback != null) return
        skipSpeaking = true
        showCurrent()
    }

    private fun playCurrent() {
        val exercise = (_state.value as? LessonUiState.Active)?.exercise as? ExerciseUi.Audible ?: return
        audio.play(exercise.clip.id, exercise.clip.text)
    }

    private fun next() {
        val active = _state.value as? LessonUiState.Active ?: return
        if (active.feedback == null) return
        showCurrent()
    }

    private fun showCurrent() {
        while (skipSpeaking && session.current?.let { exercises[it.exerciseId] } is Exercise.Speak) {
            session = session.skip()
        }
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
        if (session.isOver) record(session.result())
        val shown = (_state.value as? LessonUiState.Active)?.exercise
        if (shown is ExerciseUi.ListenTap || shown is ExerciseUi.ListenType) playCurrent()
    }

    private fun record(result: LessonResult) {
        viewModelScope.launch {
            val reward = recordSession(unitId, result.lessonId, result, kind) ?: return@launch
            _state.update { if (it is LessonUiState.Finished) it.copy(reward = RewardUi.of(reward)) else it }
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
}

/** The gap marker of fill-in-the-blank sentences. */
internal const val BLANK = "___"

/** Content index of the option shown at [shown]. */
private fun ExerciseUi.SingleChoice.contentIndex(shown: Int): Int = when (this) {
    is ExerciseUi.FillBlank -> order[shown]
    is ExerciseUi.ChooseMeaning -> order[shown]
    is ExerciseUi.CompleteChat -> order[shown]
    is ExerciseUi.ChooseImage, is ExerciseUi.ReadTrueFalse -> shown
}

internal fun Exercise.toUi(random: Random): ExerciseUi = when (this) {
    is Exercise.TranslateWordBank -> ExerciseUi.WordBank(
        id,
        prompt.text,
        prompt.lang,
        WordBank.tiles(answers.first(), distractors, random),
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
    is Exercise.ListenTap -> ExerciseUi.ListenTap(id, AudioClip(id, text), WordBank.tiles(text, distractors, random))
    is Exercise.ListenType -> ExerciseUi.ListenType(id, AudioClip(id, text))
    is Exercise.OrderSentence -> ExerciseUi.OrderSentence(id, translation, WordBank.tiles(answer, emptyList(), random))
    is Exercise.FillBlank -> {
        val order = options.indices.shuffled(random)
        ExerciseUi.FillBlank(
            id = id,
            before = sentence.substringBefore(BLANK),
            after = sentence.substringAfter(BLANK),
            translation = translation,
            options = order.map { options[it] },
            order = order,
        )
    }
    is Exercise.ChooseMeaning -> {
        val order = options.indices.shuffled(random)
        ExerciseUi.ChooseMeaning(id, AudioClip(id, text), order.map { options[it] }, order = order)
    }
    is Exercise.CompleteChat -> {
        val order = options.indices.shuffled(random)
        ExerciseUi.CompleteChat(
            id = id,
            messages = messages.map { ChatMessageUi(it.speaker, it.text) },
            options = order.map { options[it] },
            order = order,
        )
    }
    is Exercise.ReadTrueFalse -> ExerciseUi.ReadTrueFalse(id, passage, statement)
    is Exercise.Speak -> ExerciseUi.Speak(id, AudioClip(id, text), translation)
}

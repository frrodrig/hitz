@file:OptIn(ExperimentalMaterial3ExpressiveApi::class)

package eus.hitz.feature.lesson

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import eus.hitz.core.designsystem.component.FeedbackKind
import eus.hitz.core.designsystem.component.HitzButton
import eus.hitz.core.designsystem.component.HitzFeedbackPanel
import eus.hitz.core.designsystem.component.HitzLessonProgressBar
import eus.hitz.core.designsystem.component.HitzMascot
import eus.hitz.core.designsystem.component.HitzStatCounter
import eus.hitz.core.designsystem.component.StatKind
import eus.hitz.core.designsystem.icon.HitzIcons
import eus.hitz.core.designsystem.theme.HitzSpacing
import eus.hitz.core.model.content.Language
import eus.hitz.feature.lesson.exercise.ChooseImageExercise
import eus.hitz.feature.lesson.exercise.ChooseMeaningExercise
import eus.hitz.feature.lesson.exercise.CompleteChatExercise
import eus.hitz.feature.lesson.exercise.FillBlankExercise
import eus.hitz.feature.lesson.exercise.ListenTapExercise
import eus.hitz.feature.lesson.exercise.ListenTypeExercise
import eus.hitz.feature.lesson.exercise.MatchPairsExercise
import eus.hitz.feature.lesson.exercise.OrderSentenceExercise
import eus.hitz.feature.lesson.exercise.ReadTrueFalseExercise
import eus.hitz.feature.lesson.exercise.SpeakExercise
import eus.hitz.feature.lesson.exercise.TypingExercise
import eus.hitz.feature.lesson.exercise.WordBankExercise
import kotlin.math.roundToInt

private const val PERCENT = 100

@Composable
fun LessonScreen(
    onClose: () -> Unit,
    viewModel: LessonViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    LaunchedEffect(viewModel) {
        viewModel.events.collect { event ->
            when (event) {
                LessonEvent.Close -> onClose()
            }
        }
    }
    BackHandler(enabled = state is LessonUiState.Active) { viewModel.onAction(LessonAction.RequestQuit) }
    LessonContent(state = state, onAction = viewModel::onAction, onClose = onClose)
}

/** Stateless lesson UI, driven by [state]. */
@Composable
internal fun LessonContent(
    state: LessonUiState,
    onAction: (LessonAction) -> Unit,
    onClose: () -> Unit,
    modifier: Modifier = Modifier,
) {
    when (state) {
        LessonUiState.Loading -> Box(modifier.fillMaxSize(), contentAlignment = Alignment.Center) { LoadingIndicator() }
        LessonUiState.Error -> LessonMessage(
            title = stringResource(R.string.feature_lesson_error),
            body = null,
            action = stringResource(R.string.feature_lesson_finish),
            onAction = onClose,
            modifier = modifier,
        )
        LessonUiState.OutOfHearts -> LessonMessage(
            title = stringResource(R.string.feature_lesson_out_of_hearts_title),
            body = stringResource(R.string.feature_lesson_out_of_hearts_body),
            action = stringResource(R.string.feature_lesson_finish),
            onAction = onClose,
            modifier = modifier,
        )
        is LessonUiState.Finished -> LessonMessage(
            title = stringResource(R.string.feature_lesson_finished_title),
            body = stringResource(
                R.string.feature_lesson_finished_accuracy,
                (state.result.accuracy * PERCENT).roundToInt()
            ),
            action = stringResource(R.string.feature_lesson_continue),
            onAction = onClose,
            modifier = modifier,
        )
        is LessonUiState.Active -> ActiveLesson(state, onAction, modifier)
    }
}

@Composable
private fun ActiveLesson(
    state: LessonUiState.Active,
    onAction: (LessonAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier,
        topBar = { LessonTopBar(state.progress, state.hearts, onClose = { onAction(LessonAction.RequestQuit) }) },
        bottomBar = { LessonBottomBar(state, onAction) },
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(HitzSpacing.lg),
            verticalArrangement = Arrangement.spacedBy(HitzSpacing.xl),
        ) {
            Text(
                text = instruction(state.exercise),
                style = MaterialTheme.typography.headlineSmallEmphasized,
                modifier = Modifier.semantics { heading() },
            )
            ExerciseContent(state.exercise, state.feedback?.type, onAction)
        }
    }
    if (state.showQuitDialog) {
        QuitDialog(onAction)
    }
}

@Composable
private fun ExerciseContent(exercise: ExerciseUi, feedback: FeedbackType?, onAction: (LessonAction) -> Unit) {
    val enabled = feedback == null
    when (exercise) {
        is ExerciseUi.WordBank -> WordBankExercise(exercise, onAction, enabled = enabled)
        is ExerciseUi.Typing -> TypingExercise(exercise, onAction, enabled = enabled)
        is ExerciseUi.ChooseImage -> ChooseImageExercise(exercise, onAction, enabled = enabled)
        is ExerciseUi.MatchPairs -> MatchPairsExercise(exercise, onAction, enabled = enabled)
        is ExerciseUi.ListenTap -> ListenTapExercise(exercise, onAction, enabled = enabled)
        is ExerciseUi.ListenType -> ListenTypeExercise(exercise, onAction, enabled = enabled)
        is ExerciseUi.OrderSentence -> OrderSentenceExercise(exercise, onAction, enabled = enabled)
        is ExerciseUi.FillBlank -> FillBlankExercise(exercise, onAction, enabled = enabled)
        is ExerciseUi.ChooseMeaning -> ChooseMeaningExercise(exercise, feedback, onAction)
        is ExerciseUi.CompleteChat -> CompleteChatExercise(exercise, feedback, onAction)
        is ExerciseUi.ReadTrueFalse -> ReadTrueFalseExercise(exercise, feedback, onAction)
        is ExerciseUi.Speak -> SpeakExercise(exercise, onAction, enabled = enabled)
    }
}

@Composable
private fun instruction(exercise: ExerciseUi): String = when (exercise) {
    is ExerciseUi.WordBank -> stringResource(R.string.feature_lesson_instruction_translate)
    is ExerciseUi.Typing -> stringResource(
        if (exercise.answerLanguage == Language.Basque) {
            R.string.feature_lesson_instruction_type_eu
        } else {
            R.string.feature_lesson_instruction_type_es
        },
    )
    is ExerciseUi.ChooseImage -> stringResource(R.string.feature_lesson_instruction_image, exercise.prompt)
    is ExerciseUi.MatchPairs -> stringResource(R.string.feature_lesson_instruction_pairs)
    is ExerciseUi.ListenTap -> stringResource(R.string.feature_lesson_instruction_listen_tap)
    is ExerciseUi.ListenType -> stringResource(R.string.feature_lesson_instruction_listen_type)
    is ExerciseUi.OrderSentence -> stringResource(R.string.feature_lesson_instruction_order)
    is ExerciseUi.FillBlank -> stringResource(R.string.feature_lesson_instruction_fill_blank)
    is ExerciseUi.ChooseMeaning -> stringResource(R.string.feature_lesson_instruction_meaning)
    is ExerciseUi.CompleteChat -> stringResource(R.string.feature_lesson_instruction_chat)
    is ExerciseUi.ReadTrueFalse -> stringResource(R.string.feature_lesson_instruction_true_false)
    is ExerciseUi.Speak -> stringResource(R.string.feature_lesson_instruction_speak)
}

@Composable
internal fun LessonTopBar(
    progress: Float,
    hearts: Int?,
    onClose: () -> Unit,
    closeLabel: String = stringResource(R.string.feature_lesson_close),
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(horizontal = HitzSpacing.xs, vertical = HitzSpacing.sm),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(HitzSpacing.sm),
    ) {
        IconButton(onClick = onClose) {
            Icon(painterResource(HitzIcons.Close), contentDescription = closeLabel)
        }
        HitzLessonProgressBar(progress, Modifier.weight(1f))
        if (hearts != null) {
            HitzStatCounter(StatKind.Hearts, hearts)
        }
    }
}

@Composable
private fun LessonBottomBar(state: LessonUiState.Active, onAction: (LessonAction) -> Unit) {
    val feedback = state.feedback
    when {
        feedback != null -> HitzFeedbackPanel(
            kind = when (feedback.type) {
                FeedbackType.Correct -> FeedbackKind.Correct
                FeedbackType.AlmostCorrect -> FeedbackKind.AlmostCorrect
                FeedbackType.Incorrect -> FeedbackKind.Incorrect
            },
            title = stringResource(feedback.title),
            message = feedback.expected,
            continueLabel = stringResource(R.string.feature_lesson_continue),
            onContinue = { onAction(LessonAction.Continue) },
        )
        // Pairs finish on their own and speaking has its own buttons.
        state.exercise !is ExerciseUi.MatchPairs && state.exercise !is ExerciseUi.Speak -> HitzButton(
            text = stringResource(R.string.feature_lesson_check),
            onClick = { onAction(LessonAction.Check) },
            enabled = state.canCheck,
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(HitzSpacing.lg),
        )
    }
}

private val FeedbackUi.title: Int
    get() = when {
        type == FeedbackType.Correct -> R.string.feature_lesson_correct
        type == FeedbackType.AlmostCorrect -> R.string.feature_lesson_almost
        expected == null -> R.string.feature_lesson_incorrect_plain
        else -> R.string.feature_lesson_incorrect
    }

@Composable
private fun QuitDialog(onAction: (LessonAction) -> Unit) {
    AlertDialog(
        onDismissRequest = { onAction(LessonAction.DismissQuit) },
        title = { Text(stringResource(R.string.feature_lesson_quit_title)) },
        text = { Text(stringResource(R.string.feature_lesson_quit_body)) },
        confirmButton = {
            TextButton(onClick = { onAction(LessonAction.DismissQuit) }) {
                Text(stringResource(R.string.feature_lesson_quit_dismiss))
            }
        },
        dismissButton = {
            TextButton(onClick = { onAction(LessonAction.ConfirmQuit) }) {
                Text(stringResource(R.string.feature_lesson_quit_confirm))
            }
        },
    )
}

@Composable
internal fun LessonMessage(
    title: String,
    body: String?,
    action: String,
    onAction: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .navigationBarsPadding()
            .padding(HitzSpacing.xl),
        verticalArrangement = Arrangement.spacedBy(HitzSpacing.lg, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        HitzMascot(contentDescription = null)
        Text(
            text = title,
            style = MaterialTheme.typography.headlineMediumEmphasized,
            textAlign = TextAlign.Center,
            modifier = Modifier.semantics { heading() },
        )
        if (body != null) {
            Text(body, style = MaterialTheme.typography.bodyLarge, textAlign = TextAlign.Center)
        }
        HitzButton(text = action, onClick = onAction, modifier = Modifier.fillMaxWidth())
    }
}

package eus.hitz.feature.lesson.story

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import eus.hitz.core.domain.audio.AudioProvider
import eus.hitz.core.domain.content.CourseRepository
import eus.hitz.core.domain.lesson.LessonResult
import eus.hitz.core.domain.session.RecordSessionUseCase
import eus.hitz.core.domain.story.StorySession
import eus.hitz.core.domain.story.StoryStep
import eus.hitz.core.model.CourseId
import eus.hitz.core.model.content.Level
import eus.hitz.feature.lesson.AudioClip
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Plays the story of a story level. Each new line is read aloud; finishing records the level as
 * completed, with the share of questions answered right at the first try as accuracy.
 */
@HiltViewModel
class StoryViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val courses: CourseRepository,
    private val recordSession: RecordSessionUseCase,
    private val audio: AudioProvider,
) : ViewModel() {
    private val unitId: String = checkNotNull(savedStateHandle["unitId"])
    private val levelId: String = checkNotNull(savedStateHandle["levelId"])

    private val _state = MutableStateFlow<StoryUiState>(StoryUiState.Loading)
    val state: StateFlow<StoryUiState> = _state.asStateFlow()

    private val _events = Channel<StoryEvent>(Channel.BUFFERED)
    val events: Flow<StoryEvent> = _events.receiveAsFlow()

    private lateinit var session: StorySession
    private var translated: Set<Int> = emptySet()

    init {
        viewModelScope.launch {
            runCatching {
                val unit = courses.getUnit(CourseId.BASQUE_FROM_SPANISH, unitId)
                unit.levels.filterIsInstance<Level.StoryLevel>().first { it.id == levelId }.story
            }.onSuccess { story ->
                session = StorySession.start(story)
                show(readLine = true)
            }.onFailure { _state.value = StoryUiState.Error }
        }
    }

    fun onAction(action: StoryAction) {
        if (!::session.isInitialized) {
            if (action == StoryAction.Close) close()
            return
        }
        when (action) {
            StoryAction.Continue -> advance()
            is StoryAction.Answer -> {
                session = session.answer(action.option)
                show(readLine = false)
            }
            is StoryAction.ToggleTranslation -> {
                translated = if (action.line in translated) translated - action.line else translated + action.line
                show(readLine = false)
            }
            is StoryAction.PlayLine -> play(action.line)
            StoryAction.Close -> close()
        }
    }

    override fun onCleared() {
        audio.stop()
    }

    private fun advance() {
        val before = session
        session = session.next()
        if (session == before) return
        if (session.isOver) {
            audio.stop()
            val result = LessonResult(
                lessonId = levelId,
                completed = true,
                exerciseCount = session.questionCount,
                firstTryCorrect = session.firstTryCorrect,
                heartsLeft = null,
                mistakes = emptyList(),
            )
            viewModelScope.launch { recordSession(unitId, levelId, result) }
            _state.value = StoryUiState.Finished(session.accuracy)
        } else {
            show(readLine = session.current is StoryStep.Line)
        }
    }

    private fun show(readLine: Boolean) {
        val story = session.story
        val question = (session.current as? StoryStep.Question)?.let { step ->
            val content = story.questions[step.index]
            StoryQuestionUi(
                prompt = content.prompt,
                options = content.options,
                wrong = session.wrongOptions,
                correct = content.correct.takeIf { session.answered },
            )
        }
        val lines = story.lines.take(session.visibleLines).mapIndexed { index, line ->
            StoryLineUi(index, line.speaker, line.eu, line.es, AudioClip(clipId(index), line.eu))
        }
        _state.value = StoryUiState.Active(story.title, session.progress, lines, question, translated)
        if (readLine) (session.current as? StoryStep.Line)?.let { play(it.index) }
    }

    private fun play(line: Int) {
        val text = session.story.lines.getOrNull(line)?.eu ?: return
        audio.play(clipId(line), text)
    }

    /** Clip ids of story lines: `<story id>.<line index>` (see docs/content-guide.md). */
    private fun clipId(line: Int) = "${session.story.id}.$line"

    private fun close() {
        viewModelScope.launch { _events.send(StoryEvent.Close) }
    }
}

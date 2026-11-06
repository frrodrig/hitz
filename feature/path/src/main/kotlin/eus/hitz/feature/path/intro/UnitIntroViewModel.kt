package eus.hitz.feature.path.intro

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import eus.hitz.core.domain.audio.AudioProvider
import eus.hitz.core.domain.content.CourseRepository
import eus.hitz.core.domain.settings.SettingsRepository
import eus.hitz.core.model.CourseId
import eus.hitz.core.model.content.KeyWord
import eus.hitz.core.model.content.LocalizedText
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface UnitIntroUiState {
    data object Loading : UnitIntroUiState

    data object Error : UnitIntroUiState

    /** What the unit teaches: goals, key words and the titles of its grammar notes. */
    data class Ready(
        val unitId: String,
        val title: LocalizedText,
        val goals: List<LocalizedText>,
        val keyWords: List<KeyWordUi>,
        val grammar: List<LocalizedText>,
    ) : UnitIntroUiState
}

/** A key word; [clipId] is its recorded audio (`<unit>.kw<index>`). */
data class KeyWordUi(val eu: String, val es: String, val imageUri: String?, val clipId: String)

/**
 * The introduction shown before a unit's first lesson. Opening it marks it as seen, so the path
 * shows it only once per unit (it stays reachable from the guidebook).
 */
@HiltViewModel
class UnitIntroViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    courses: CourseRepository,
    settings: SettingsRepository,
    private val audio: AudioProvider,
) : ViewModel() {
    private val unitId: String = checkNotNull(savedStateHandle["unitId"])

    private val _state = MutableStateFlow<UnitIntroUiState>(UnitIntroUiState.Loading)
    val state: StateFlow<UnitIntroUiState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            _state.value = runCatching { courses.getUnit(CourseId.BASQUE_FROM_SPANISH, unitId) }.fold(
                onSuccess = { unit ->
                    UnitIntroUiState.Ready(
                        unitId = unit.id,
                        title = unit.title,
                        goals = unit.goals,
                        keyWords = unit.keyWords.mapIndexed { index, word -> word.toUi("${unit.id}.kw$index") },
                        grammar = unit.guidebook.sections.map { it.title },
                    )
                },
                onFailure = { UnitIntroUiState.Error },
            )
            settings.markUnitIntroSeen(unitId)
        }
    }

    fun play(word: KeyWordUi) = audio.play(word.clipId, word.eu)

    override fun onCleared() {
        audio.stop()
    }
}

private fun KeyWord.toUi(clipId: String) =
    KeyWordUi(eu, es, image?.let { "file:///android_asset/images/$it" }, clipId)

package eus.hitz.feature.practice

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import eus.hitz.core.domain.audio.AudioProvider
import eus.hitz.core.domain.practice.GetUnitReviewUseCase
import eus.hitz.core.domain.practice.ObservePracticeHubUseCase
import eus.hitz.core.domain.practice.PracticeHub
import eus.hitz.core.domain.practice.PracticeMode
import eus.hitz.core.domain.practice.ReviewWord
import eus.hitz.core.domain.practice.UnitReview
import eus.hitz.core.domain.settings.SettingsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val STOP_TIMEOUT_MILLIS = 5_000L

sealed interface PracticeUiState {
    data object Loading : PracticeUiState

    /** [modes] are the quick practices to offer: those with exercises and turned on in the settings. */
    data class Ready(val hub: PracticeHub, val modes: List<PracticeMode>) : PracticeUiState
}

/** The practice hub: personalised review, quick practice and everything learned so far. */
@HiltViewModel
class PracticeViewModel @Inject constructor(
    observeHub: ObservePracticeHubUseCase,
    settings: SettingsRepository,
) : ViewModel() {
    val state: StateFlow<PracticeUiState> = combine(observeHub(), settings.settings) { hub, preferences ->
        val modes = listOfNotNull(
            PracticeMode.Listening.takeIf { preferences.listeningEnabled },
            PracticeMode.Speaking.takeIf { preferences.speakingEnabled },
        ).filter { (hub.available[it] ?: 0) > 0 }
        PracticeUiState.Ready(hub, modes) as PracticeUiState
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS), PracticeUiState.Loading)
}

sealed interface UnitReviewUiState {
    data object Loading : UnitReviewUiState

    data object Error : UnitReviewUiState

    data class Ready(val review: UnitReview) : UnitReviewUiState
}

/** One unit to review: its words with audio, guide, phrases and story. */
@HiltViewModel
class UnitReviewViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    getUnitReview: GetUnitReviewUseCase,
    private val audio: AudioProvider,
) : ViewModel() {
    private val unitId: String = checkNotNull(savedStateHandle["unitId"])

    private val _state = MutableStateFlow<UnitReviewUiState>(UnitReviewUiState.Loading)
    val state: StateFlow<UnitReviewUiState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            _state.value = runCatching { getUnitReview(unitId) }
                .fold(onSuccess = { UnitReviewUiState.Ready(it) }, onFailure = { UnitReviewUiState.Error })
        }
    }

    fun play(word: ReviewWord) = audio.play(word.clipId, word.eu)

    override fun onCleared() {
        audio.stop()
    }
}

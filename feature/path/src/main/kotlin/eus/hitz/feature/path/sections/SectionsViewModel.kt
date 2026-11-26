package eus.hitz.feature.path.sections

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import eus.hitz.core.domain.path.LearningPath
import eus.hitz.core.domain.path.ObserveLearningPathUseCase
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

sealed interface SectionsUiState {
    data object Loading : SectionsUiState

    data object Error : SectionsUiState

    data class Ready(val path: LearningPath) : SectionsUiState
}

@HiltViewModel
class SectionsViewModel @Inject constructor(observeLearningPath: ObserveLearningPathUseCase) : ViewModel() {
    val state: StateFlow<SectionsUiState> = observeLearningPath()
        .map { SectionsUiState.Ready(it) as SectionsUiState }
        .catch { emit(SectionsUiState.Error) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS), SectionsUiState.Loading)

    private companion object {
        const val STOP_TIMEOUT_MILLIS = 5_000L
    }
}
